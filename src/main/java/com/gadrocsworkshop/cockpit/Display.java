package com.gadrocsworkshop.cockpit;

import com.gadrocsworkshop.dcsbios.DcsBiosParser;
import javafx.scene.Group;

/**
 * Base class for displaying information on the cockpit display.
 *
 * Created by Craig Courtney on 3/1/2015.
 */
public abstract class Display extends Group {

    protected CockpitController controller;

    /**
     * Returns the cockpit controller for this display.
     *
     * @return CockpitController object for this display.
     */
    @SuppressWarnings("WeakerAccess")
    protected CockpitController getController() {
        return controller;
    }

    /**
     * Sets the controller for this display.
     *
     * @param controller CockpitController this display should use to interact with the cockpit.
     */
    public void setController(CockpitController controller) {
        this.controller = controller;
    }

    /**
     * Returns the parser this display is connected to.
     *
     * @return DcsBiosParser object which this display should listen to.
     */
    protected DcsBiosParser getDcsBiosParser() {
        return getController().getDcsBiosParser();
    }

    protected void showDisplay(Display display) {
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
    @SuppressWarnings("EmptyMethod")
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
    @SuppressWarnings("EmptyMethod")
    public void onHide() {
    }
}
