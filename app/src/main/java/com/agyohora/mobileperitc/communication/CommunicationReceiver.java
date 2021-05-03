package com.agyohora.mobileperitc.communication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CommunicationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("CommunicationReceiver", "Called");
        Intent intent2Service = new Intent(context, CommunicationService.class);
        intent2Service.putExtra("FUNCTION", intent.getStringExtra("FUNCTION"));
        intent2Service.putExtra("FUNCTION_DATA", intent.getBundleExtra("FUNCTION_DATA"));
        context.startService(intent2Service);
    }
}
