package com.gadrocsworkshop.cockpit.adi;

import com.gadrocsworkshop.dcsbios.DcsBiosDataListener;
import com.gadrocsworkshop.dcsbios.DcsBiosSyncListener;

import javax.usb.*;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Craig Courtney on 6/21/2015.
 */
public class DtsAdiListener implements DcsBiosDataListener, DcsBiosSyncListener {

    /** Serial number for the Roll DTS Board */
    private static final String ROLL_SERIAL_NUMBER = "A035";

    /** Serial number for the Pitch DTS Board */
    private static final String PITCH_SERIAL_NUMBER = "A034";

    private DtsBoard rollBoard;
    private DtsBoard pitchBoard;

    public DtsAdiListener() {
        rollBoard = DtsBoard.findDtsBoard(ROLL_SERIAL_NUMBER);
        pitchBoard = DtsBoard.findDtsBoard(PITCH_SERIAL_NUMBER);
    }

    @Override
    public void dcsBiosDataWrite(int address, int newValue) {
    }

    @Override
    public void handleDcsBiosFrameSync() {
        //
    }
}