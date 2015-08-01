package com.gadrocsworkshop.cockpit;

/**
 * Interface for objects which listen for rotary encoder events.
 *
 * Created by Craig Courtney on 6/28/2015.
 */
@FunctionalInterface
interface RotaryEncoderListener {
    void EncoderRotated(RotaryEncoder source, RotaryEncoderDirection direction);
}
