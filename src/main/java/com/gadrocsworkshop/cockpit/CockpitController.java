package com.gadrocsworkshop.cockpit;

import com.gadrocsworkshop.cockpit.adi.DtsAdiListener;
import com.gadrocsworkshop.cockpit.displays.OffDisplay;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.gadrocsworkshop.dcsbios.DcsBiosUdpReceiver;

import java.io.IOException;
import java.util.Stack;

/**
 * Created by ccourtne on 2/8/15.
 */
public class CockpitController extends Application implements RotaryEncoderListener {

    private DcsBiosUdpReceiver receiver;
    private AnimationTimer timer;
    private Display activeDisplay;
    private Display rootDisplay;
    private Stack<Display> displayStack;

    private GpioController gpio;
    private GpioPinDigitalOutput powerOutput;

    private RotaryEncoder rightEncoder;
    private RotaryEncoder leftEncoder;

    private DtsAdiListener adiListener;

    public CockpitController() {
        displayStack = new Stack<>();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Starting DCS Bios Receiver");
        startDcsBiosReceiver();
        System.out.println("Initializing Display");
        setupDisplay(primaryStage);
        System.out.println("Starting Display Animation Timer");
        startAnimationTimer();
        System.out.println("Starting GPIO Control");
        startGpio();
        System.out.println("Starting ADI listener");
        startAdi();
    }

    public void sendCommand(String command) {
        try {
            receiver.sendCommand(command);
        }
        catch (IOException ex) {
            System.out.println("Error sending command to DCS");
            ex.printStackTrace();
        }

    }

    public void shutdown() {
        removeAllDisplays();
    }

    public void powerOff() {
        System.out.println("Cockpit turned off");
        powerOutput.low();
    }

    public void powerOn() {
        System.out.println("Cockpit turned on");
        powerOutput.high();
    }

    public void showDisplay(Display display) {
        if (activeDisplay != null) {
            displayStack.push(activeDisplay);
            activeDisplay.onHide();
        }
        rootDisplay.getChildren().add(display);
        activeDisplay = display;
        display.onDisplay();
    }

    public void removeAllDisplays() {
        if (activeDisplay != null) {
            rootDisplay.getChildren().remove(activeDisplay);
            activeDisplay = null;
        }
        displayStack.removeAllElements();
        rootDisplay.onDisplay();
    }

    public void removeActiveDisplay() {
        if (activeDisplay != null) {
            rootDisplay.getChildren().remove(activeDisplay);
            activeDisplay = null;
            if (displayStack.isEmpty()) {
                rootDisplay.onDisplay();
            }
            else {
                showDisplay(displayStack.pop());
            }
        }
    }

    private void startAdi() {
        adiListener = new DtsAdiListener();
        receiver.getParser().addDataListener(adiListener);
        receiver.getParser().addSyncListener(adiListener);
    }

    private void startGpio() {
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
        Display displayTarget = getDisplayTarget();
        if (rightEncoder == source) {
            displayTarget.rightRotaryRotated(direction);
        }
        else {
            displayTarget.leftRotaryRotated(direction);
        }
    }

    private void startControlButtonListener() {
        final GpioPinDigitalInput controlButtonPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_UP);
        controlButtonPin.setDebounce(100);
        controlButtonPin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                Display displayTarget = getDisplayTarget();
                if (event.getState() == PinState.LOW) {
                    Platform.runLater(() -> displayTarget.controlButtonPressed());
                }
                else {
                    Platform.runLater(() -> displayTarget.controlButtonReleased());
                }
            }
        });
    }

    public Display getDisplayTarget() {
        return activeDisplay == null ? rootDisplay : activeDisplay;
    }

    private void startDcsBiosReceiver() throws IOException {
        receiver = new DcsBiosUdpReceiver();
        receiver.start();
    }

    private void setupDisplay(Stage primaryStage) {
        rootDisplay = new OffDisplay();
        initDisplay(rootDisplay);
        primaryStage.setScene(new Scene(rootDisplay, 640, 480, Color.BLACK));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void initDisplay(Display display) {
        display.setController(this);
        display.setDcsBiosParser(receiver.getParser());
        display.onInitialize();
    }

    private void startAnimationTimer() {
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

    @Override
    public void stop() throws Exception {
        System.out.println("Stopping controller");
        adiListener.shutdown();
        receiver.stop();
        timer.stop();
    }
}
