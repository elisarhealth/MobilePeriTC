package com.agyohora.mobileperitc.actions;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.agyohora.mobileperitc.userInterface.MyActivity;
import com.agyohora.mobileperitc.userInterface.ReportActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class Actions extends IntentService {
    //IntentService always dispatches to the following location only
    public static final String DISPATCH_TOSTORE = "com.agyohora.mobileperitc.actions.actions.dispatchtostore";
    //IntentService always dispatches a payload
    public static final String DISPATCH_PAYLOAD = "com.agyohora.mobileperitc.actions.actions.dispatchpayload";
    // IntentService can perform the following actions.
    public static final String ACTION_BACKTOREGISTER = "com.agyohora.mobileperitc.actions.actions.name.backToRegister";
    public static final String ACTION_BACKTOINITSCREEN = "com.agyohora.mobileperitc.actions.actions.name.backtoInitScreen";
    public static final String ACTION_BACKTOSETTEST = "com.agyohora.mobileperitc.actions.actions.name.backToSetTest";
    public static final String ACTION_BEGINTEST = "com.agyohora.mobileperitc.actions.actions.name.beginTest";
    public static final String ACTION_CALIBRATIONDONE = "com.agyohora.mobileperitc.actions.actions.name.CalibDone";
    public static final String ACTION_COMMUNICATIONSTATUS = "com.agyohora.mobileperitc.actions.actions.name.CommuunicationStatus";
    public static final String ACTION_DATAFROMHMD = "com.agyohora.mobileperitc.actions.actions.name.DataFromHMD";
    public static final String ACTION_DOCALIBRATION = "com.agyohora.mobileperitc.actions.actions.name.doCalibration";
    public static final String ACTION_HMDCONNECTED = "com.agyohora.mobileperitc.actions.actions.name.HMDConnected";
    public static final String ACTION_HMDNOTCONNECTED = "com.agyohora.mobileperitc.actions.actions.name.HMDNOTConeccted";
    public static final String ACTION_REGISTERTEST = "com.agyohora.mobileperitc.actions.actions.name.registerTest";
    public static final String ACTION_RESETTOAST = "com.agyohora.mobileperitc.actions.actions.name.resetToast";
    public static final String ACTION_REPORTVIEW = "com.agyohora.mobileperitc.actions.actions.name.reportView";
    public static final String ACTION_STARTNEWTEST = "com.agyohora.mobileperitc.actions.actions.name.startnewTest";
    public static final String ACTION_STARTTEST = "com.agyohora.mobileperitc.actions.actions.name.startTest";
    public static final String ACTION_SAVESTATE = "com.agyohora.mobileperitc.actions.actions.name.savestate";
    public static final String ACTION_WAITINGTOCONNECTTOHMD = "com.agyohora.mobileperitc.actions.actions.name.savestate";
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.agyohora.mobileperitc.actions.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.agyohora.mobileperitc.actions.extra.PARAM2";

    public Actions() {
        super("Actions");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void backToInitScreen(){
        Bundle payload = actionCreator(ACTION_BACKTOINITSCREEN);
        payload.putString("data","NA");
        firetheIntent(payload);
    }
    public static void backToRegister(){
        Bundle payload = actionCreator(ACTION_BACKTOREGISTER);
        payload.putString("data","NA");
        firetheIntent(payload);
    }

    public static void backToSetTest(){
        Bundle payload = actionCreator(ACTION_BACKTOSETTEST);
        payload.putString("data","NA");
        firetheIntent(payload);
    }

    public static void beginTest(boolean calibDone){
        Bundle payload = actionCreator(ACTION_BEGINTEST);
        payload.putBoolean("data",calibDone);
        firetheIntent(payload);
    }

    public static void calibDone(){
        Bundle payload = actionCreator(ACTION_CALIBRATIONDONE);
        payload.putString("data",null);
        //todo : we will need to update this based on what we actually send from communication
        firetheIntent(payload);
    }

    public static void commStatus(String status){
        Bundle payload = actionCreator(ACTION_COMMUNICATIONSTATUS);
        Log.d("NampuComm Actions","sending comm status to store");
        payload.putString("data",status);
        firetheIntent(payload);
    }

    public static void dataFromHMD(String data){
        Bundle payload = actionCreator(ACTION_DATAFROMHMD);
        Log.d("NampuCommTC_ACTIONS",data);
        payload.putString("data",data);
        firetheIntent(payload);
    }

    public static void doCalibration(){
        Bundle payload = actionCreator(ACTION_DOCALIBRATION);
        payload.putString("data","NA");

        //handler to simulate calibration
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                calibDone();
            }
        }, 500);
        firetheIntent(payload);
    }
    public static void HMDConnected(){
        Bundle payload = actionCreator(ACTION_HMDCONNECTED);
        payload.putString("data","NA");
        firetheIntent(payload);
    }
    public static void HMDNotConnected(){
        Bundle payload = actionCreator(ACTION_HMDNOTCONNECTED);
        payload.putString("data","NA");
        firetheIntent(payload);
    }

    public static void registerTest(){
        Bundle payload = actionCreator(ACTION_REGISTERTEST);
        payload.putString("data","NA");
        firetheIntent(payload);
    }

    public static void reportView(){
        Bundle payload = actionCreator(ACTION_REPORTVIEW);
        payload.putString("data","NA");
        firetheIntent(payload);
    }

    public static void resetToast(){
        Bundle payload = actionCreator(ACTION_RESETTOAST);
        payload.putString("data","NA");
        firetheIntent(payload);
    }

    public static void saveState(Bundle state){
        Log.d("ActionTracker","This got called");
        Bundle payload = actionCreator(ACTION_SAVESTATE);
        payload.putBundle("data",state);
        firetheIntent(payload);
    }

    public static void startnewTest(){
        Bundle payload = actionCreator(ACTION_STARTNEWTEST);
        payload.putString("data","NA");
        firetheIntent(payload);
    }

    public static void startTest(){
        Bundle payload = actionCreator(ACTION_STARTTEST);
        payload.putString("data",null);
        firetheIntent(payload);
    }

    private static void firetheIntent(Bundle payload){
        Context context = MyActivity.applicationContext;
        if(context==null){
            context = ReportActivity.applicationContext;
        }
        Intent intent = new Intent(context,Actions.class);
        intent.putExtra(DISPATCH_PAYLOAD,payload);
        context.startService(intent);
    }

    public static Bundle actionCreator(String name){
        Bundle payload = new Bundle();
        payload.putString("name",name);
        return payload;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent intent2send = new Intent();
        intent2send.setAction(DISPATCH_TOSTORE);
        intent2send.putExtra(DISPATCH_PAYLOAD,intent.getBundleExtra(DISPATCH_PAYLOAD));
        Log.d("ActionGen",intent2send.getBundleExtra(DISPATCH_PAYLOAD).getString("name"));
        Log.d("ActionGen","Sent some action ma");
        sendBroadcast(intent2send);
    }
}
