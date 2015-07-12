package com.gadrocsworkshop.cockpit;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.ArrayList;
import java.util.List;

/**
 * Object which listens for GPIO pin changes and decodes the output from a standard rotary encoder.
 *
 * Created by Craig Courtney on 6/28/2015.
 */
class RotaryEncoder implements GpioPinListenerDigital {

    private final List<RotaryEncoderListener> listeners = new ArrayList<>();

    private final GpioPinDigitalInput rotaryPin1;
    @SuppressWarnings("FieldCanBeLocal")
    private final GpioPinDigitalInput rotaryPin2;

    private boolean pinState1;
    private boolean pinState2;

    public RotaryEncoder(GpioController gpio, Pin pin1, Pin pin2) {
        rotaryPin1 = gpio.provisionDigitalInputPin(pin1, PinPullResistance.PULL_UP);
        pinState1 = rotaryPin1.isHigh();

        rotaryPin2 = gpio.provisionDigitalInputPin(pin2, PinPullResistance.PULL_UP);
        pinState2 = rotaryPin2.isHigh();

        rotaryPin1.addListener(this);
        rotaryPin2.addListener(this);
    }

    public void addListener(RotaryEncoderListener toAdd) {
        listeners.add(toAdd);
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        synchronized (this) {
            boolean check = false;
            if (event.getPin().getPin().equals(rotaryPin1.getPin())) {
                pinState1 = event.getState().isHigh();
                check = pinState1;
            } else {
                pinState2 = event.getState().isHigh();
            }

            if (check) {
                //noinspection ConstantConditions
                if (pinState1 == pinState2) {
                    notify(RotaryEncoderDirection.CCW);
                } else {
                    notify(RotaryEncoderDirection.CW);
                }
            }
        }
    }

    private void notify(RotaryEncoderDirection direction) {
        for (RotaryEncoderListener listener : listeners) {
            listener.EncoderRotated(this, direction);
        }
    }

}
