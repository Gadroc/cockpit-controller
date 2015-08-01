package com.gadrocsworkshop.cockpit;

/**
 * Interface for listening for menu value changes.
 *
 * Created by Craig Courtney on 8/1/2015.
 */
@FunctionalInterface
public interface MenuValueListener {
    void onMenuValueChanged(String newValue);
}
