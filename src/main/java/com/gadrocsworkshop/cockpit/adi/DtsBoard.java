package com.gadrocsworkshop.cockpit.adi;

import org.hid4java.*;
import org.hid4java.event.HidServicesEvent;

/**
 * Object to control brydling's DTS Converter (http://forums.eagle.ru/showthread.php?t=112902).
 *
 * Created by Craig Courtney on 6/21/2015.
 */
class DtsBoard implements HidServicesListener {

    /** The vendor ID of the DTS Board. */
    private static final short VENDOR_ID = (short)0x04d8;

    /** The product ID of the DTS Board. */
    private static final short PRODUCT_ID = (short)0xf64e;

    private String serialNumber;
    private HidDevice device;

    private int S1;
    private int S2;

    /**
     * Sends a list of connected DTS boards along with their serial numbers to the log file.
     */
    public static void logAvailableDtsBoards() {
        try {
            HidServices hidServices = HidManager.getHidServices();
            hidServices.getAttachedHidDevices().stream()
                    .filter(hidDevice -> hidDevice.isVidPidSerial(VENDOR_ID, PRODUCT_ID, null))
                    .forEach(
                            hidDevice -> System.out.println("DTS Board Detected - Serial Number '" + hidDevice.getSerialNumber() + "'")
                    );
        }
        catch (HidException e) {
            System.out.println("Error getting HidServices.");
            e.printStackTrace();
        }
    }

    public DtsBoard(String serialNumber) throws HidException {

        HidServices hidServices = HidManager.getHidServices();
        hidServices.addHidServicesListener(this);
        this.serialNumber = serialNumber;
        this.device = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, serialNumber);
        sendValues();
    }

    public void setAngle(double angle) {
        double statorAngle = angle+60;
        int newS1 = (int)(Math.sin(statorAngle * Math.PI / 180 -2 * Math.PI / 3) * 2048 + 2048);
        int newS2 = (int)(-Math.sin(statorAngle * Math.PI / 180 + 2 * Math.PI / 3) * 2048 + 2048);

        if (newS1 != S1 || newS2 != S2) {
            S1 = newS1;
            S2 = newS2;

            System.out.println("Sending Values to DTS("+serialNumber+") Angle:"+angle + " Offset Angel:" + statorAngle +" S1:" + S1 + " S2:" + S2);

            sendValues();
        }
    }

    /**
     * Must be called once you are done controlling the DTS Board.  If this is not called
     * some dangling listeners and references will not be properly cleaned up.
     */
    public void shutdown() {
        try {
            HidServices hidServices = HidManager.getHidServices();
            hidServices.removeUsbServicesListener(this);
            if (this.device != null) {
                if (this.device.isOpen()) {
                    this.device.close();
                }
                this.device = null;

            }
        }
        catch (HidException e) {
            System.out.println("Error stopping DtsBoard");
            e.printStackTrace();
        }
    }

    private void sendValues() {
        if (this.device != null) {
            byte[] outBuffer = {  0, 0, 0, 0 };

            outBuffer[0] = (byte)((S1 >> 8) & 0x0F);
            outBuffer[1] = (byte)(S1 & 0xFF);
            outBuffer[2] = (byte)((S2 >> 8) & 0x0F);
            outBuffer[3] = (byte)(S2 & 0xFF);

            this.device.write(outBuffer, 4, (byte)0);
        }
    }

    @Override
    public void hidDeviceAttached(HidServicesEvent event) {
        HidDevice newDevice = event.getHidDevice();
        if (newDevice.isVidPidSerial(VENDOR_ID, PRODUCT_ID, serialNumber)) {
            System.out.println("DTSBoard(" + serialNumber + ") Attached.");
            this.device = newDevice;
            sendValues();
        }
    }

    @Override
    public void hidDeviceDetached(HidServicesEvent event) {
        System.out.println("Detach event - " + event.getHidDevice().getSerialNumber());
        if (event.getHidDevice().isVidPidSerial(VENDOR_ID, PRODUCT_ID, serialNumber)) {
            System.out.println("DTSBoard(" + serialNumber + ") Detached.");
            if (device.isOpen()) {
                device.close();
            }
            device = null;
        }
    }

    @Override
    public void hidFailure(HidServicesEvent event) {
        // Nothing to do?
    }
}
