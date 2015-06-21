package com.gadrocsworkshop.cockpit;

/**
 * Initial display state which turns off the power supply until the control button
 * is pushed.
 *
 * Created by Craig Courtney on 3/1/2015.
 */
public class OffDisplay extends Display {

    private HsiDisplay Hsi;

    @Override
    public void onInitialize() {
        Hsi = new HsiDisplay();
        controller.initDisplay(Hsi);
    }

    @Override
    public void onDisplay() {
        controller.powerOff();
    }

    @Override
    public void controlButtonPressed() {
        controller.powerOn();
        showDisplay(Hsi);
    }
}
