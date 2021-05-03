package com.agyohora.mobileperitc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.myapplication.MyApplication;

/**
 * Created by Invent on 25-1-18.
 */

public class UsbReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(context,"mobile_peri_tc_pref");
        appPreferencesHelper.setUSB(action);
        //Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
        Log.d("UsbReceiver", "Device Connected -> " + action);
        if(action.equalsIgnoreCase("android.hardware.usb.action.USB_STATE")) {
            UsbDevice device = intent
                    .getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device != null) {
                int vendorID = device.getVendorId();
                int productID = device.getProductId();
                String deviceName = device.getDeviceName();
                String manufacturerName = device.getManufacturerName();
                String productName = device.getProductName();
                String serialNumber = device.getSerialNumber();
                String data = vendorID + " " + productID + " " + deviceName +" "+ manufacturerName + " "+productName + " "+serialNumber;
                Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "No Devices", Toast.LENGTH_SHORT).show();
            }
        }
        if (action.equalsIgnoreCase("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            MyApplication.getInstance().setIs_OTG(true);
            UsbDevice device = intent
                    .getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device != null) {
                int vendorID = device.getVendorId();
                int productID = device.getProductId();
                String deviceName = device.getDeviceName();
                String manufacturerName = device.getManufacturerName();
                String productName = device.getProductName();
                String serialNumber = device.getSerialNumber();
                String data = vendorID + " " + productID + " " + deviceName +" "+ manufacturerName + " "+productName + " "+serialNumber;
                Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
            }
        } else if (action.equalsIgnoreCase("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
            MyApplication.getInstance().setIs_OTG(false);
            UsbDevice device = intent
                    .getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device != null) {
                int vendorID = device.getVendorId();
                int productID = device.getProductId();
                device.getDeviceClass();
                String deviceName = device.getDeviceName();
                String manufacturerName = device.getManufacturerName();
                String productName = device.getProductName();
                String serialNumber = device.getSerialNumber();
                String data = vendorID + " " + productID + " " + deviceName +" "+ manufacturerName + " "+productName + " "+serialNumber;
                Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
