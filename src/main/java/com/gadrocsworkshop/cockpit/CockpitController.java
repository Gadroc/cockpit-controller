package com.gadrocsworkshop.cockpit;

import com.gadrocsworkshop.cockpit.buses.CenterConsoleBus;
import com.gadrocsworkshop.cockpit.dts.DtsAdiListener;
import com.gadrocsworkshop.cockpit.displays.HsiDisplay;
import com.gadrocsworkshop.dcsbios.DcsBiosParser;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.gadrocsworkshop.dcsbios.DcsBiosUdpReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Primary object running the cockpit.
 *
 * Created by Craig Courtney on 2/8/15.
 */
public class CockpitController extends Application implements RotaryEncoderListener, ControlResponder {

    private static final Logger LOGGER;

    private DcsBiosUdpReceiver receiver;
    private AnimationTimer timer;
    private Display activeDisplay;
    private Group displayRoot;
    private HsiDisplay hsiDisplay;
    private final Stack<Display> displayStack;

    private CenterConsoleBus centerConsoleBus;

    private GpioController gpio;
    private GpioPinDigitalOutput powerOutput;

    private RotaryEncoder rightEncoder;
    private RotaryEncoder leftEncoder;

    private DtsAdiListener adiListener;

    static {
        try {
            final File loggingConfigFile = new File("/etc/cockpit/logging.properties");
            if (loggingConfigFile.exists()) {
                final InputStream loggingConfigStream = new FileInputStream(loggingConfigFile);
                LogManager.getLogManager().readConfiguration(loggingConfigStream);
                loggingConfigStream.close();
            }
        } catch (Exception e) {
            System.out.println("Error reading logging configuration file.");
        }
        LOGGER = Logger.getLogger(CockpitController.class.getName());
    }

    public CockpitController() {
        displayStack = new Stack<>();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        LOGGER.info("Initializing Cockpit");
        startDcsBiosReceiver();
        setupDisplay(primaryStage);
        startAnimationTimer();
        startGpio();
        startAdi();
        startConsoles();
        LOGGER.info("Initialization Complete");
    }

    public void sendCommand(String command) {
        try {
            receiver.sendCommand(command);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error sending command to DCS", ex);
        }
    }

    public void shutdown() {
        LOGGER.fine("Shutting down cockpit.");
        removeAllDisplays();
    }

    public void powerOff() {
        LOGGER.fine("Cockpit turned off");
        powerOutput.low();
    }

    public void powerOn() {
        LOGGER.fine("Cockpit turned on");
        powerOutput.high();
    }

    public void showDisplay(Display display) {
        try {
            if (activeDisplay != null) {
                displayStack.push(activeDisplay);
                activeDisplay.onHide();
            }
            activeDisplay = display;
            if (!displayRoot.getChildren().contains(display.getParentNode())) {
                displayRoot.getChildren().add(display.getParentNode());
            }
            display.onDisplay();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error activating display.", ex);
        }
    }

    public void removeAllDisplays() {
        while (activeDisplay != null) {
            removeActiveDisplay();
        }
    }

    public void removeActiveDisplay() {
        if (activeDisplay != null) {
            activeDisplay.getParentNode().toBack();
            displayRoot.getChildren().remove(activeDisplay.getParentNode());
            if (displayStack.isEmpty()) {
                activeDisplay = null;
                powerOff();
            } else {
                activeDisplay = displayStack.pop();
                activeDisplay.onDisplay();
            }
        }
    }

    private void startAdi() {
        LOGGER.fine("Starting ADI listener");
        adiListener = new DtsAdiListener();
        receiver.getParser().addDataListener(adiListener);
        receiver.getParser().addSyncListener(adiListener);
    }

    private void startGpio() {
        LOGGER.fine("Starting GPIO Control");
        gpio = GpioFactory.getInstance();
        powerOutput = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW);
        startControlButtonListener();
        rightEncoder = setupEncoder(RaspiPin.GPIO_04, RaspiPin.GPIO_05);
        leftEncoder = setupEncoder(RaspiPin.GPIO_02, RaspiPin.GPIO_03);
    }

    private RotaryEncoder setupEncoder(Pin pin1, Pin pin2) {
        RotaryEncoder encoder = new RotaryEncoder(gpio, pin1, pin2);
        encoder.addListener(this);
        return encoder;
    }

    public void EncoderRotated(RotaryEncoder source, RotaryEncoderDirection direction) {
        ControlResponder responder = getActiveResponder();
        if (rightEncoder == source) {
            Platform.runLater(() -> responder.rightRotaryRotated(direction));
        } else {
            Platform.runLater(() -> responder.leftRotaryRotated(direction));
        }
    }

    private void startControlButtonListener() {
        final GpioPinDigitalInput controlButtonPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_UP);
        controlButtonPin.setDebounce(100);
        controlButtonPin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                ControlResponder responder = getActiveResponder();
                if (event.getState() == PinState.LOW) {
                    Platform.runLater(responder::controlButtonPressed);
                } else {
                    Platform.runLater(responder::controlButtonReleased);
                }
            }
        });
    }

    protected ControlResponder getActiveResponder() {
        return activeDisplay == null ? this : activeDisplay;
    }

    public DcsBiosParser getDcsBiosParser() {
        return receiver.getParser();
    }

    private void startDcsBiosReceiver() throws IOException {
        LOGGER.fine("Starting DCS Bios Receiver");
        receiver = new DcsBiosUdpReceiver();
        receiver.start();
    }

    private void setupDisplay(Stage primaryStage) {
        LOGGER.fine("Initializing Display");

        displayRoot = new Group();
        Scene scene = new Scene(displayRoot, 640, 480, Color.BLACK);

        scene.setOnKeyPressed(event -> {
            ControlResponder responder = getActiveResponder();
            KeyCode keyCode = event.getCode();
            if (keyCode == KeyCode.SPACE) {
                Platform.runLater(responder::controlButtonPressed);
            } else if (keyCode == KeyCode.UP) {
                Platform.runLater(() -> responder.leftRotaryRotated(RotaryEncoderDirection.CCW));
            } else if (keyCode == KeyCode.DOWN) {
                Platform.runLater(() -> responder.leftRotaryRotated(RotaryEncoderDirection.CW));
            } else if (keyCode == KeyCode.LEFT) {
                Platform.runLater(() -> responder.rightRotaryRotated(RotaryEncoderDirection.CCW));
            } else if (keyCode == KeyCode.RIGHT) {
                Platform.runLater(() -> responder.rightRotaryRotated(RotaryEncoderDirection.CW));
            }
        });

        scene.setOnKeyReleased(event -> {
            ControlResponder responder = getActiveResponder();
            KeyCode keyCode = event.getCode();
            if (keyCode == KeyCode.SPACE) {
                Platform.runLater(responder::controlButtonReleased);
            } else if (keyCode == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        hsiDisplay = new HsiDisplay();
        initDisplay(hsiDisplay);
    }

    public void initDisplay(Display display) {
        try {
            display.setController(this);
            display.onInitialize();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, String.format("Error initializing display - %s", display.getClass().getName()), ex);
        }
    }

    private void startAnimationTimer() {
        LOGGER.fine("Starting Display Animation Timer");
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (activeDisplay != null) {
                    activeDisplay.onUpdateDisplay();
                }
            }
        };
        timer.start();
    }

    private void startConsoles() {
        LOGGER.fine("Starting Console Buses");
        centerConsoleBus = new CenterConsoleBus(getDcsBiosParser());
    }

    @Override
    public void stop() throws Exception {
        LOGGER.fine("Stopping controller");
        adiListener.shutdown();
        receiver.stop();
        timer.stop();
    }

    @Override
    public void controlButtonPressed() {
        LOGGER.finer("Control button pressed in off state.");
    }

    @Override
    public void controlButtonReleased() {
        LOGGER.fine("Control button released in off state.");
        powerOn();
        showDisplay(hsiDisplay);
    }

    @Override
    public void rightRotaryRotated(RotaryEncoderDirection direction) {
        LOGGER.fine("Right rotary rotated in off state.");
    }

    @Override
    public void leftRotaryRotated(RotaryEncoderDirection direction) {
        LOGGER.fine("Left rotary rotated in off state.");
    }
}
