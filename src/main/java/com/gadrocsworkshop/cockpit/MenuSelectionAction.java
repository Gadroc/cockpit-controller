package com.gadrocsworkshop.cockpit;

/**
 * Interface for action implementation when a menu item is selected.
 *
 * Created by Craig Courtney on 8/1/2015.
 */
@FunctionalInterface
public interface MenuSelectionAction {
    void onMenuItemSelected();
}
