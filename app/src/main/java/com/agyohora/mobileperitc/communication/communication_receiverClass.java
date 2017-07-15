package com.agyohora.mobileperitc.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by namputo on 19/06/17.
 */
import com.agyohora.mobileperitc.communication.Communication_MainModule;
import com.agyohora.mobileperitc.store.StoreTransmitter;

public class communication_receiverClass {
    class dataToSend {
        public String tc2HMD_piatientName;
        public String tc2HMD_patientAge;
        public String tc2HMD_patientSex;
        public String tc2HMD_testEye;
        public String tc2HMD_testType;
    }

    public static BroadcastReceiver commReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             String function = intent.getStringExtra("FUNCTION");
             Log.d("commReceiver","comm receiver is working");
             switch (function){
                 case StoreTransmitter.COMM_FUNCTION_INITIALIZE:
                     Communication_MainModule.Comm_Initialize();
                     break;
                 case StoreTransmitter.COMM_FUNCTION_CLICKONTC:
                     Bundle datatoSend = intent.getBundleExtra("FUNCTION_DATA");
                     String data = datatoSend.getString("data");
                     int hashcode = datatoSend.getInt("hashcode");
                     String onPass = datatoSend.getString("onPass");
                     String onFail = datatoSend.getString("onFail");
                     Communication_MainModule.Write_Tx_Data_Promise("TC_BUTTON_CLICK",hashcode,data,onPass,onFail);
             }
        }
    };


}
