package com.gadrocsworkshop.cockpit;

/**
 * Interface for objects which can respond to the multi-function controls on the cockpit.
 *
 * Created by Craig Courtney on 8/1/2015.
 */
public interface ControlResponder {
    /**
     * Called when this responder is active and the control button has
     * been pushed.
     */
    void controlButtonPressed();

    /**
     * Called when this responder is active and the control button has
     * been released.
     */
    void controlButtonReleased();

    /**
     * Called when this responder is active and the right rotary encoder
     * has been rotated.
     *
     * @param direction Direction encoder was rotated
     */
    void rightRotaryRotated(RotaryEncoderDirection direction);

    /**
     * Called when this responder is active and the left rotary encoder
     * has been rotated.
     *
     * @param direction Direction encoder was rotated
     */
    void leftRotaryRotated(RotaryEncoderDirection direction);
}
