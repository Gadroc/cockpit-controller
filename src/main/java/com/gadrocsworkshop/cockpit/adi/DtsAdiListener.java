package com.gadrocsworkshop.cockpit.adi;

import com.gadrocsworkshop.dcsbios.DcsBiosDataListener;
import com.gadrocsworkshop.dcsbios.DcsBiosSyncListener;
import org.hid4java.HidException;

/**
 * DCSBios Listener to control brydling's DTS Converter (http://forums.eagle.ru/showthread.php?t=112902).
 *
 * Created by Craig Courtney on 6/21/2015.
 */
public class DtsAdiListener implements DcsBiosDataListener, DcsBiosSyncListener {

    /** Serial number for the Roll DTS Board */
    private static final String ROLL_SERIAL_NUMBER = "A0034";

    /** Serial number for the Pitch DTS Board */
    private static final String PITCH_SERIAL_NUMBER = "A0035";

    private DtsBoard rollBoard;
    private DtsBoard pitchBoard;

    private int rollValue = 0;
    private int pitchValue = 0;

    public DtsAdiListener() {
        try {
            rollBoard = new DtsBoard(ROLL_SERIAL_NUMBER);
            pitchBoard = new DtsBoard(PITCH_SERIAL_NUMBER);
        }
        catch (HidException e) {
            System.out.println("Error initializing DTSBoard objects.");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        rollBoard.shutdown();
        pitchBoard.shutdown();
    }

    @Override
    public void dcsBiosDataWrite(int address, int newValue) {
        if (address == 0x1032) {
            if (pitchValue != newValue) {
                pitchValue = newValue;
                System.out.println("New Pitch Value: " + pitchValue);
            }
        }
        else if (address == 0x1034) {
            if (rollValue != newValue) {
                rollValue = newValue;
                System.out.println("New Roll Value: " + rollValue);
            }
        }
    }

    @Override
    public void handleDcsBiosFrameSync() {
        setBoardAngle(pitchBoard, pitchValue, -180.0);
        setBoardAngle(rollBoard, rollValue, 360.0);
    }

    private void setBoardAngle(DtsBoard board, int value, double factor) {
        board.setAngle(((value/65535.0) * factor) - (factor / 2.0));
    }

}