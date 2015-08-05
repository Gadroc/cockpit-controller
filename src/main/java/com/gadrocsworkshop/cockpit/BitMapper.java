package com.gadrocsworkshop.cockpit;

import com.gadrocsworkshop.dcsbios.DcsBiosDataListener;

public class BitMapper implements DcsBiosDataListener {

    private final int dcsBiosAddress;
    private final int dcsBiosMask;
    private final CockpitBusBank bank;
    private final int busIndex;
    private final byte busMask;

    public BitMapper(int dcsBiosAddress, int dcsBiosMask, CockpitBusBank bank, int busIndex, int busBit) {
        this.dcsBiosAddress = dcsBiosAddress;
        this.dcsBiosMask = dcsBiosMask;
        this.bank = bank;
        this.busIndex = busIndex;
        this.busMask = (byte)(1 << busBit);
    }

    @Override
    public void dcsBiosDataWrite(int address, int data) {
        if (address == dcsBiosAddress) {
            bank.setBit(busIndex, busMask, ((data & dcsBiosMask) == dcsBiosMask));
        }
    }
}
