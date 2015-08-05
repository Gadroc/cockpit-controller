package com.gadrocsworkshop.cockpit.buses;

import com.gadrocsworkshop.cockpit.BitMapper;
import com.gadrocsworkshop.cockpit.CockpitBus;
import com.gadrocsworkshop.cockpit.CockpitBusBank;
import com.gadrocsworkshop.dcsbios.DcsBiosParser;

public class CenterConsoleBus extends CockpitBus {

    public CenterConsoleBus(DcsBiosParser parser) {
        super(parser);
        setupBank3();
    }

    private void setupBank3() {
        CockpitBusBank bank = getBank(3);

        // Master Caution Indicator
        registerMapper(new BitMapper(0, 0, bank, 28, 4));
    }
}
