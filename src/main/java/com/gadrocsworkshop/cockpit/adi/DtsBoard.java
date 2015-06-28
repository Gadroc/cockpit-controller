package com.gadrocsworkshop.cockpit.adi;

import javax.usb.*;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Craig Courtney on 6/21/2015.
 */
public class DtsBoard {
    /** The vendor ID of the DTS Board. */
    private static final short VENDOR_ID = (short)0x04d8;

    /** The product ID of the DTS Board. */
    private static final short PRODUCT_ID = (short)0xf64e;

    private DtsBoard(UsbDevice device) {
        try {
            UsbInterface usbi = device.getUsbConfiguration((byte)0).getUsbInterface((byte)0);
            usbi.claim();
            usbi.getUsbEndpoint((byte)0).getUsbPipe();
        }
        catch (UsbException e) {
            System.out.println("Error setting up DTS board");
            e.printStackTrace();
        }
    }

    public static DtsBoard findDtsBoard(String serialNumber) {

        UsbDevice device = null;

        try {
            UsbServices services = UsbHostManager.getUsbServices();
            device = findDtsBoard(services.getRootUsbHub(), serialNumber);
        }
        catch (UnsupportedEncodingException|UsbException e) {
            System.out.println("** Error finding DTS driver board.");
            e.printStackTrace();
        }

        if (device == null) {
            return null;
        }

        return new DtsBoard(device);
    }

    private static UsbDevice findDtsBoard(UsbHub hub, String serialNumber) throws UsbException, UnsupportedEncodingException {

        UsbDevice dtsBoard = null;

        @SuppressWarnings("unchecked")
        List<UsbDevice> devices = hub.getAttachedUsbDevices();

        for (UsbDevice device: devices)
        {
            if (device.isUsbHub())
            {
                dtsBoard = findDtsBoard((UsbHub) device, serialNumber);
                if (dtsBoard != null) return dtsBoard;
            }
            else
            {
                UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
                if (desc.idVendor() == VENDOR_ID &&
                        desc.idProduct() == PRODUCT_ID &&
                        device.getSerialNumberString().equalsIgnoreCase(serialNumber)) {
                    return device;
                }
            }
        }

        return null;
    }
}
