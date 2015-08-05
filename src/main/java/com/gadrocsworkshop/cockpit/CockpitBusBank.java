package com.gadrocsworkshop.cockpit;

public class CockpitBusBank {

    private byte[] data = new byte[31];

    public void setBit(int index, byte mask, boolean value) {
        if (value) {
            data[index] = (byte)(data[index] | mask);
        } else {
            data[index] = (byte)(data[index] & ~mask);
        }
    }
}
