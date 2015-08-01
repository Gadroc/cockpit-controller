package com.gadrocsworkshop.cockpit.displays;

import com.gadrocsworkshop.cockpit.MenuDisplay;

import java.util.logging.Logger;

/**
 * Main menu for the cockpit controls.
 *
 * Created by Craig Courtney on 8/1/2015.
 */
public class MainMenuDisplay extends MenuDisplay {

    private static final Logger LOGGER = Logger.getLogger(MainMenuDisplay.class.getName());

    public MainMenuDisplay() {
        super("Main Menu");
    }

    @Override
    public void onDisplay() {
        LOGGER.fine("Showing Main Menu");
        super.onDisplay();
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        addMenuItem("Exit Menu", controller::removeActiveDisplay);
        addMenuItem("Configure CP", this::displayConfigCpMenu);
        addMenuItem("Shutdown Cockpit", controller::shutdown);
    }

    private void displayConfigCpMenu() {
        ConfigureCpDisplay configCp = new ConfigureCpDisplay();
        controller.initDisplay(configCp);
        controller.showDisplay(configCp);
    }
}
