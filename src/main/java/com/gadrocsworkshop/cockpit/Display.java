package com.gadrocsworkshop.cockpit;

import com.gadrocsworkshop.dcsbios.DcsBiosParser;
import javafx.scene.Group;

/**
 * Created by Craig Courtney on 3/1/2015.
 */
public abstract class Display extends Group {

    protected CockpitController controller;
    protected DcsBiosParser dcsBiosParser;

    public CockpitController getController() {
        return controller;
    }

    public void setController(CockpitController controller) {
        this.controller = controller;
    }

    public DcsBiosParser getDcsBiosParser() {
        return dcsBiosParser;
    }

    public void setDcsBiosParser(DcsBiosParser dcsBiosParser) {
        this.dcsBiosParser = dcsBiosParser;
    }

    public void showDisplay(Display display) {
        controller.showDisplay(display);
    }

    /**
     * Called when this display is active and the control button has
     * been pushed.
     */
    public void controlButtonPressed() {
    }

    /**
     * Called when this display is active and the control button has
     * been released.
     */
    public void controlButtonReleased() {

    }

    /**
     * Called when this display is active and the right rotary encoder
     * has been rotated.
     *
     * @param direction Direction encoder was rotated
     */
    public void rightRotaryRotated(RotaryEncoderDirection direction) {

    }

    /**
     * Called when this display is active and the left rotary encoder
     * has been rotated.
     *
     * @param direction Direction encoder was rotated
     */
    public void leftRotaryRotated(RotaryEncoderDirection direction) {
    }

    /**
     * Called during initialization of the display before it's actually displayed.
     */
    public void onInitialize() {
    }

    /**
     * Called when the display is displayed on the screen.
     */
    public void onDisplay() {

    }

    /**
     * Called when it is time to update the display.
     */
    public void onUpdateDisplay() {
    }

    /**
     * Called when this display is removed from the screen.
     */
    public void onHide() {
    }
}
