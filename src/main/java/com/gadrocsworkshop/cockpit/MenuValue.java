package com.gadrocsworkshop.cockpit;

import java.util.ArrayList;
import java.util.List;

/**
 * Object containing the settable value in a menu item.
 *
 * Created by Craig Courtney on 8/1/2015.
 */
public class MenuValue {

    private final List<MenuValueListener> listeners = new ArrayList<>();
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        notify(value);
    }

    public void addListener(MenuValueListener toAdd) {
        listeners.add(toAdd);
    }

    private void notify(String newValue) {
        for (MenuValueListener listener : listeners) {
            listener.onMenuValueChanged(newValue);
        }
    }
}
