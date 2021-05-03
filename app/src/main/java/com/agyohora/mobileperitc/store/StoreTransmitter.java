package com.agyohora.mobileperitc.store;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.ui.MainActivity;

/**
 * Created by namputo on 09/06/17.
 */

public class StoreTransmitter extends IntentService {
    public static final String CHANGETRIGG_UI = "com.agyohora.mobileperitc.store.StoreTransmitter.trigger.toUI";
    public static final String CHANGETRIGG_DIALOG = "com.agyohora.mobileperitc.store.StoreTransmitter.trigger.toDialog";
    public static final String CHANGETRIGG_COMM = "com.agyohora.mobileperitc.store.StoreTransmitter.trigger.toComm";

    //communication module function names
    public static final String COMM_FUNCTION_INITIALIZE = "com.agyohora.mobileperitc.store.StoreTransmitter.Commfunction.Initialize";
    public static final String COMM_FUNCTION_CLICKONTC = "com.agyohora.mobileperitc.store.StoreTransmitter.Commfunction.clickonTC";
    public static final String COMM_FUNCTION_CONTINUE = "com.agyohora.mobileperitc.store.StoreTransmitter.Commfunction.continue";
    public static final String COMM_FUNCTION_SEND_UPDATE = "com.agyohora.mobileperitc.store.StoreTransmitter.Commfunction.sendUpdate";
    public static final String COMM_FUNCTION_STOP = "com.agyohora.mobileperitc.store.StoreTransmitter.Commfunction.stop";

    public StoreTransmitter() {
        super("StoreTransmitter");
    }

    //todo : make storetransmitter leaner like Actions.
    //todo : do not have dummy placeholder extras like you are having.
    public static void updatedUIState(String caller) {
        Log.e("updatedUIState","Caller Name "+caller);
        Context context = MainActivity.applicationContext;
        Intent intent = new Intent(context, StoreTransmitter.class);
        Bundle dataToSend = new Bundle();
        intent.putExtra("DESTINATION", CHANGETRIGG_UI);
        intent.putExtra("FUNCTION", "NA");
        intent.putExtra("FUNCTION_DATA", dataToSend);
        context.startService(intent);
    }

    public static void showDialog(String functionName, Bundle data) {
        Log.e("showDialog","Caller Name "+functionName);
        Context context = MainActivity.applicationContext;
        Intent intent = new Intent(context, StoreTransmitter.class);
        intent.putExtra("DESTINATION", CHANGETRIGG_DIALOG);
        intent.putExtra("FUNCTION", functionName);
        intent.putExtra("FUNCTION_DATA", data);
        context.startService(intent);
    }

    public static void doCommFunction(String functionName, Bundle dataToSend) {
        Log.d("StoreTransmitter", "This got called");
        Context context = MyApplication.getInstance(); //MainActivity.applicationContext;
        Intent intent = new Intent(context, StoreTransmitter.class);
        intent.putExtra("DESTINATION", CHANGETRIGG_COMM);
        intent.putExtra("FUNCTION", functionName);
        intent.putExtra("FUNCTION_DATA", dataToSend);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent intent2send = new Intent();
        intent2send.setAction(intent.getStringExtra("DESTINATION"));
        intent2send.putExtra("FUNCTION", intent.getStringExtra("FUNCTION"));
        intent2send.putExtra("FUNCTION_DATA", intent.getBundleExtra("FUNCTION_DATA"));
        sendBroadcast(intent2send);
    }
}
