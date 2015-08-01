package com.gadrocsworkshop.cockpit;

/**
 * Interface for action implementation when a menu item value encoder action happens.
 *
 * Created by Craig Courtney on 8/1/2015.
 */
@FunctionalInterface
public interface MenuEncoderAction {
    void onEncoderRotated(RotaryEncoderDirection direction);
}
