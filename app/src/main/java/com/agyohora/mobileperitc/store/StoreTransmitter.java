package com.agyohora.mobileperitc.store;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.agyohora.mobileperitc.userInterface.MyActivity;

/**
 * Created by namputo on 09/06/17.
 */

public class StoreTransmitter extends IntentService {
    public static final String CHANGETRIGG_UI = "com.agyohora.mobileperitc.store.StoreTransmitter.trigger.toUI";
    public static final String CHANGETRIGG_COMM = "com.agyohora.mobileperitc.store.StoreTransmitter.trigger.toComm";

    //communication module function names
    public static final String COMM_FUNCTION_INITIALIZE = "com.agyohora.mobileperitc.store.StoreTransmitter.Commfunction.Initialize";
    public static final String COMM_FUNCTION_CLICKONTC = "com.agyohora.mobileperitc.store.StoreTransmitter.Commfunction.clickonTC";
    public StoreTransmitter() {
        super("StoreTransmitter");
    }
//todo : make storetransmitter leaner like Actions.
    //todo : do not have dummy placeholder extras like you are having.
    public static void updatedUIState(){
        Context context = MyActivity.applicationContext;
        Intent intent = new Intent(context,StoreTransmitter.class);
        Bundle dataToSend = new Bundle();
        intent.putExtra("DESTINATION",CHANGETRIGG_UI);
        intent.putExtra("FUNCTION","NA");
        intent.putExtra("FUNCTION_DATA",dataToSend);
        context.startService(intent);
    }

    public static void doCommFunction(String functionName, Bundle dataToSend){
        Log.d("StoreTransmitter","This got called");
        Context context = MyActivity.applicationContext;
        Intent intent = new Intent(context,StoreTransmitter.class);
        intent.putExtra("DESTINATION",CHANGETRIGG_COMM);
        intent.putExtra("FUNCTION",functionName);
        intent.putExtra("FUNCTION_DATA",dataToSend);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Intent intent2send = new Intent();
        intent2send.setAction(intent.getStringExtra("DESTINATION"));
        intent2send.putExtra("FUNCTION",intent.getStringExtra("FUNCTION"));
        intent2send.putExtra("FUNCTION_DATA",intent.getBundleExtra("FUNCTION_DATA"));
        sendBroadcast(intent2send);
    }
}
