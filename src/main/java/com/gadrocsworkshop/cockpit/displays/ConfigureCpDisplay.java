package com.gadrocsworkshop.cockpit.displays;

import com.gadrocsworkshop.cockpit.MenuDisplay;

/**
 * Configuration items for the center panel.
 *
 * Created by Craig Courtney on 8/1/2015.
 */
public class ConfigureCpDisplay extends MenuDisplay {

    public ConfigureCpDisplay() {
        super("Configure CP");
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        addMenuItem("Back", controller::removeActiveDisplay);
    }
}
