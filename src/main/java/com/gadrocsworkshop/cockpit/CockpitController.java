package com.gadrocsworkshop.cockpit;

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
public class CockpitController extends Application {

    private DcsBiosUdpReceiver receiver;
    private AnimationTimer timer;
    private Display activeDisplay;
    private Display rootDisplay;
    private Stack<Display> displayStack;

    private GpioController gpio;
    private GpioPinDigitalOutput powerOutput;

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
    }

    public void shutdown() {
        removeAllDisplays();
    }

    public void powerOff() {
        System.out.println("Cockpit turned off");
        powerOutput.high();
    }

    public void powerOn() {
        System.out.println("Cockpit turned on");
        powerOutput.low();
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

    private void startGpio() {
        gpio = GpioFactory.getInstance();
        powerOutput = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.HIGH);
        startControlButtonListener();
    }

    private void startControlButtonListener() {
        final GpioPinDigitalInput controlButtonPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_UP);
        controlButtonPin.setDebounce(100);
        controlButtonPin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                Display displayTarget = activeDisplay == null ? rootDisplay : activeDisplay;
                if (event.getState() == PinState.LOW) {
                    Platform.runLater(() -> displayTarget.controlButtonPressed());
                }
                else {
                    Platform.runLater(() -> displayTarget.controlButtonReleased());
                }
            }
        });
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
        receiver.stop();
        timer.stop();
    }
}
