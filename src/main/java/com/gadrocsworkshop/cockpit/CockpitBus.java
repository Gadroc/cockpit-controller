package com.gadrocsworkshop.cockpit;

import com.gadrocsworkshop.dcsbios.DcsBiosDataListener;
import com.gadrocsworkshop.dcsbios.DcsBiosParser;
import com.gadrocsworkshop.dcsbios.DcsBiosSyncListener;

import java.util.ArrayList;
import java.util.List;

public class CockpitBus implements DcsBiosSyncListener {

    private final DcsBiosParser parser;
    private final List<DcsBiosDataListener> mappers = new ArrayList<>();
    private CockpitBusBank[] busBanks = new CockpitBusBank[4];

    public CockpitBus(DcsBiosParser parser) {
        this.parser = parser;
    }

    public CockpitBusBank getBank(int bankNumber) {
        if (bankNumber < 0 || bankNumber > 3) {
            throw new IndexOutOfBoundsException("Bank Number out of range");
        }
        return busBanks[bankNumber];
    }

    protected  void registerMapper(DcsBiosDataListener listener) {
        mappers.add(listener);
        parser.addDataListener(listener);
    }

    @Override
    public void handleDcsBiosFrameSync() {
        // Send data to the bus serial port
    }
}
