package com.agyohora.mobileperitc.userInterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.actions.Actions;
import com.agyohora.mobileperitc.communication.*;
import com.agyohora.mobileperitc.store.Store;
import com.agyohora.mobileperitc.store.StoreTransmitter;

import java.util.ArrayList;


public class MyActivity extends AppCompatActivity {
    private int numb = 0;
    public static Context applicationContext;
    private static int activeViewID;
    private static int prevViewID= 0;
    //variables that ensure threads to update calibration occur only once per test sequence
    private static boolean CalibImageViewLinked = false;
    private static boolean TestImageViewLinked = false;
    private static Chronometer chrono;
    //TODO : Can we have the same view element in two different layouts ? it is not suggested.

    //chronometer for test time
    //TODO : Need to fix chronometer. Right now only a rudimentary chronometer is implemented
    public static boolean chronometerStarted = false;
    //TODO : ELapsed time of chronometer should be saved in store
    public static long elapsedtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationContext = getApplicationContext();
        //TODO : Document why application context is being set here.
        //Main reason this is being done is so that intent service can be begun from the same context always.
        //initializeFormSpinners();
        //register storereciever
        IntentFilter storefilter = new IntentFilter();
        storefilter.addAction(Actions.DISPATCH_TOSTORE);
        applicationContext.registerReceiver(Store.storeReceiever, storefilter);
        //register activity reciever
        IntentFilter activityreceiver = new IntentFilter();
        activityreceiver.addAction(StoreTransmitter.CHANGETRIGG_UI);
        this.registerReceiver(UIReceiever, activityreceiver);
        //register communication receviver
        IntentFilter commfilter = new IntentFilter();
        commfilter.addAction(StoreTransmitter.CHANGETRIGG_COMM);
        //UI elements setting

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Activity", "This got called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter activityreceiver = new IntentFilter();
        activityreceiver.addAction(StoreTransmitter.CHANGETRIGG_UI);
        this.registerReceiver(UIReceiever, activityreceiver);
        IntentFilter commfilter = new IntentFilter();
        commfilter.addAction(StoreTransmitter.CHANGETRIGG_COMM);
        this.registerReceiver(communication_receiverClass.commReceiver, commfilter);
        updateUI();
        //setContentView(R.layout.activity_initscreen);
    }


    @Override
    protected void onStop() {
        super.onStop();
        saveUI();
        unregisterReceiver(UIReceiever);
        unregisterReceiver(communication_receiverClass.commReceiver);
        Log.d("Activity", "Stop got called too");
    }

    public void clickCallback(View v) {

        switch (v.getId()) {
            case R.id.is_startTest:
                Log.d("ClickCallback", "Start Test Got clicked");
                Actions.startnewTest();
                break;
            case R.id.is_startReportView:
                Log.d("ClickCallback", "Report View Got Clicked");
                Actions.reportView();
                break;
            case R.id.pi_registerTest:
                Log.d("RegisterTest", "Register Test Got clicked");
                saveUI();
                Actions.registerTest();
                break;
            case R.id.si_startTest:
                Log.d("StartTest", "Test will begin");
                Actions.startTest();
                break;
            case R.id.si_backToRegister:
                Log.d("backToRegister", "Test will begin");
                Actions.backToRegister();
                break;
            case R.id.cl_calibRecalib:
                Log.d("Calibration", "Calibration got pressed");
                Actions.doCalibration();
                break;
            case R.id.cl_skipCalib:
                Log.d("Calibration", "Skip Calibration");
                Actions.beginTest(false);
                break;
            case R.id.cl_back:
                Log.d("Calibration", "BackToSetTest");
                Actions.backToSetTest();
                break;
            case R.id.cl_saveProceed:
                Log.d("Calibration", "Save and proceed");
                Actions.beginTest(true);
                break;
            case R.id.dt_abort:
                Log.d("During Test", "Abort got prssed");
                Actions.backToSetTest();
                break;
            case R.id.pt_newtest:
                Log.d("PostTest", "Start a new Test");
                Actions.startnewTest();
                break;
            case R.id.rs_report1:
                Intent intent = new Intent (applicationContext,ReportActivity.class);
                //Bundle b = Store.reportvals1;
                intent.putExtra("Payload",Store.reportvals1);
                startActivity(intent);
                break;
            case R.id.rs_report2:
                Intent intent1 = new Intent(applicationContext,ReportActivity.class);
                intent1.putExtra("Payload",Store.reportvals2);
                startActivity(intent1);
                break;
            case R.id.rs_report3:
                Intent intent2 = new Intent(applicationContext,ReportActivity.class);
                intent2.putExtra("Payload",Store.reportvals3);
                startActivity(intent2);
        }
    }

    public void updateUI() {
        Bundle state = Store.getstate();
        activeViewID = state.getInt("viewID");

        if(activeViewID!=prevViewID) {
            setContentView(activeViewID);
            prevViewID = activeViewID;
        }

        switch (activeViewID) {
            case R.layout.activity_patinfo:
                EditText pi_patientName = (EditText) findViewById(R.id.pi_patientName);
                EditText pi_patientAge = (EditText) findViewById(R.id.pi_patientAge);
                Spinner pi_patientSex = (Spinner) findViewById(R.id.pi_patientSex);
                ArrayAdapter pi_patientSexAdapter = (ArrayAdapter) pi_patientSex.getAdapter();
                Spinner pi_testEye = (Spinner) findViewById(R.id.pi_testEye);
                ArrayAdapter pi_testEyeAdapter = (ArrayAdapter) pi_testEye.getAdapter();
                Spinner pi_testType = (Spinner) findViewById(R.id.pi_testType);
                ArrayAdapter pi_testTypeAdapter = (ArrayAdapter) pi_testType.getAdapter();

                pi_patientName.setText(state.getString("pi_patientName"), TextView.BufferType.EDITABLE);
                pi_patientAge.setText(state.getString("pi_patientAge"), TextView.BufferType.EDITABLE);
                pi_patientSex.setSelection(pi_patientSexAdapter.getPosition(state.getString("pi_patientSex")));
                pi_testEye.setSelection(pi_testEyeAdapter.getPosition(state.getString("pi_testEye")));
                pi_testType.setSelection(pi_testTypeAdapter.getPosition(state.getString("pi_testType")));
                break;
            case R.layout.activity_setinfo:
                ((TextView) findViewById(R.id.si_patientName)).setText(state.getString("set_patientName"));
                ((TextView) findViewById(R.id.si_patientAge)).setText(state.getString("set_patientAge"));
                ((TextView) findViewById(R.id.si_patientSex)).setText(state.getString("set_patientSex"));
                ((TextView) findViewById(R.id.si_testEye)).setText(state.getString("set_testEye"));
                ((TextView) findViewById(R.id.si_testType)).setText(state.getString("set_testType"));
                //resetting chronometer start to false
                chronometerStarted = false;
                CalibImageViewLinked = false;
                break;
            case R.layout.activity_calibration:
                //set patient information
                ((TextView) findViewById(R.id.cl_patientName)).setText(state.getString("set_patientName"));
                ((TextView) findViewById(R.id.cl_testEye)).setText(state.getString("set_testEye"));
                ((TextView) findViewById(R.id.cl_testType)).setText(state.getString("set_testType"));
                //set calibraion status
                ((TextView) findViewById(R.id.cl_calibStatus)).setText(state.getString("cl_calibStatus"));
                //Now Buttons
                ((Button) findViewById(R.id.cl_calibRecalib)).setText(state.getString("cl_calibRecalib"));
                int saveProceedVisibility = state.getInt("cl_saveProceedVisibility");
                if (saveProceedVisibility == 8) {
                    ((Button) findViewById(R.id.cl_saveProceed)).setVisibility(View.GONE);
                } else if (saveProceedVisibility == 0) {
                    ((Button) findViewById(R.id.cl_saveProceed)).setVisibility(View.VISIBLE);
                }
                Thread Cl_Image_Update_Thread = new Thread(Calib_ImageView);
                if(!CalibImageViewLinked) {
                    Cl_Image_Update_Thread.start();
                    CalibImageViewLinked=true;
                }
                TestImageViewLinked = false;
                break;
            case R.layout.activity_duringtest:
                //Set patient information
                //reseting the calibImageview Initialized variable
                CalibImageViewLinked = false;
                ((TextView) findViewById(R.id.dt_patientName)).setText(state.getString("set_patientName"));
                ((TextView) findViewById(R.id.dt_testEye)).setText(state.getString("set_testEye"));
                ((TextView) findViewById(R.id.dt_testType)).setText(state.getString("set_testType"));
                //chronometer
                if (state.getBoolean("dt_chronometerOn")) {
                    chronometerStarted = true;
                    ((Chronometer) findViewById(R.id.dt_chrono)).setBase(state.getLong("dt_timebase"));
                    ((Chronometer) findViewById(R.id.dt_chrono)).start();
                } else {
                    ((Chronometer) findViewById(R.id.dt_chrono)).stop();
                }
                //color of the background of the resLayout
                ArrayList<String> resultArray = state.getStringArrayList("dt_result");
                String tesType = state.getString("set_testType");
                if (state.getString("set_testEye").equals("Right")) {
                    //remove the left elements
                    (findViewById(R.id.r53l)).setVisibility(View.INVISIBLE);
                    (findViewById(R.id.r54l)).setVisibility(View.INVISIBLE);
                    ((findViewById(R.id.r53l))).setTag(null);
                    ((findViewById(R.id.r54l))).setTag(null);
                    (findViewById(R.id.r53r)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.r54r)).setVisibility(View.VISIBLE);
                    ((findViewById(R.id.r53r))).setTag("r53");
                    ((findViewById(R.id.r54r))).setTag("r54");
                    //((TextView) findViewById(R.id.resLayout).findViewWithTag("r53r")).setText(resultArray.get(52));
                    //((TextView) findViewById(R.id.resLayout).findViewWithTag("r54r")).setText(resultArray.get(53));
                } else if (state.getString("set_testEye").equals("Left")) {
                    //remove the right element
                    (findViewById(R.id.r53r)).setVisibility(View.INVISIBLE);
                    (findViewById(R.id.r54r)).setVisibility(View.INVISIBLE);
                    ((findViewById(R.id.r53r))).setTag(null);
                    ((findViewById(R.id.r54r))).setTag(null);
                    (findViewById(R.id.r53l)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.r54l)).setVisibility(View.VISIBLE);
                    ((findViewById(R.id.r53l))).setTag("r53");
                    ((findViewById(R.id.r54l))).setTag("r54");
                    //((TextView) findViewById(R.id.resLayout).findViewWithTag("r53l")).setText(resultArray.get(52));
                    //((TextView) findViewById(R.id.resLayout).findViewWithTag("r54l")).setText(resultArray.get(53));
                }
                //((TextView)findViewById(R.id.resLayout).findViewWithTag("r"+String.valueOf(5))).setText(resultArray.get(2));
                for (int i = 1; i < 55; i++) {
                    ((TextView) findViewById(R.id.resLayout).findViewWithTag("r" + i)).setText(resultArray.get(i - 1));
                    //setting color
                    if(tesType.equals("Suprathreshold")||tesType.equals("Demo")){
                        switch (resultArray.get(i-1)){
                            case ".":
                                ( findViewById(R.id.resLayout).findViewWithTag("r" + i)).setBackgroundColor(Color.GRAY);
                                break;
                            case "+":
                                ( findViewById(R.id.resLayout).findViewWithTag("r" + i)).setBackgroundColor(Color.LTGRAY);
                                break;
                            case "-":
                                ( findViewById(R.id.resLayout).findViewWithTag("r" + i)).setBackgroundColor(Color.DKGRAY);
                        }
                    }
                }
                //set up FP,FN,FL
                ((TextView) findViewById(R.id.dt_falsePostitiveState)).setText(state.getString("FP"));
                ((TextView) findViewById(R.id.dt_falseNegetiveState)).setText(state.getString("FN"));
                ((TextView) findViewById(R.id.dt_fixationState)).setText(state.getString("FL"));
                //findViewById(R.layout.activity_duringtest).findViewWithTag()
                if(!TestImageViewLinked) {
                    Thread DT_Image_Update_Thread = new Thread(Test_ImageView);
                    Log.d("VideoLoop","this is starting again");
                    DT_Image_Update_Thread.start();
                    TestImageViewLinked=true;
                }
                if(state.getBoolean("dt_testOver")){
                    elapsedtime = SystemClock.elapsedRealtime()-((Chronometer) findViewById(R.id.dt_chrono)).getBase();
                }
                break;
            case R.layout.activity_posttest:
                //resetting test image view
                TestImageViewLinked = false;
                //set patient names and stuff
                ((TextView)findViewById(R.id.pt_patientName)).setText(state.getString("set_patientName"));
                ((TextView) findViewById(R.id.pt_testEye)).setText(state.getString("set_testEye"));
                ((TextView) findViewById(R.id.pt_testType)).setText(state.getString("set_testType"));
                //elapsed time
               // long timeelapsed = SystemClock.elapsedRealtime()- ((Chronometer)findViewById(R.id.dt_chrono)).getBase();
                int hours = (int) (elapsedtime / 3600000);
                int minutes = (int) (elapsedtime - hours * 3600000) / 60000;
                int seconds = (int) (elapsedtime - hours * 3600000 - minutes * 60000) / 1000;
                ((TextView)findViewById(R.id.pt_elapasedtime)).setText(String.valueOf(minutes)+":"+String.valueOf(seconds));
                //put the results in the right place
                //color of the background of the resLayout
                ArrayList<String> pt_resultArray = state.getStringArrayList("dt_result");
                String pt_tesType = state.getString("set_testType");
                if (state.getString("set_testEye").equals("Right")) {
                    //remove the left elements
                    (findViewById(R.id.r53l)).setVisibility(View.INVISIBLE);
                    (findViewById(R.id.r54l)).setVisibility(View.INVISIBLE);
                    ((findViewById(R.id.r53l))).setTag(null);
                    ((findViewById(R.id.r54l))).setTag(null);
                    (findViewById(R.id.r53r)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.r54r)).setVisibility(View.VISIBLE);
                    ((findViewById(R.id.r53r))).setTag("r53");
                    ((findViewById(R.id.r54r))).setTag("r54");
                    //((TextView) findViewById(R.id.resLayout).findViewWithTag("r53r")).setText(resultArray.get(52));
                    //((TextView) findViewById(R.id.resLayout).findViewWithTag("r54r")).setText(resultArray.get(53));
                } else if (state.getString("set_testEye").equals("Left")) {
                    //remove the right element
                    (findViewById(R.id.r53r)).setVisibility(View.INVISIBLE);
                    (findViewById(R.id.r54r)).setVisibility(View.INVISIBLE);
                    ((findViewById(R.id.r53r))).setTag(null);
                    ((findViewById(R.id.r54r))).setTag(null);
                    (findViewById(R.id.r53l)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.r54l)).setVisibility(View.VISIBLE);
                    ((findViewById(R.id.r53l))).setTag("r53");
                    ((findViewById(R.id.r54l))).setTag("r54");
                    //((TextView) findViewById(R.id.resLayout).findViewWithTag("r53l")).setText(resultArray.get(52));
                    //((TextView) findViewById(R.id.resLayout).findViewWithTag("r54l")).setText(resultArray.get(53));
                }
                //((TextView)findViewById(R.id.resLayout).findViewWithTag("r"+String.valueOf(5))).setText(resultArray.get(2));
                for (int i = 1; i < 55; i++) {
                    ((TextView) findViewById(R.id.pattern_resLayout).findViewWithTag("r" + i)).setText(pt_resultArray.get(i - 1));
                    //setting color
                    if(pt_tesType.equals("Suprathreshold")||pt_tesType.equals("Demo")){
                        switch (pt_resultArray.get(i-1)){
                            case ".":
                                ( findViewById(R.id.pattern_resLayout).findViewWithTag("r" + i)).setBackgroundColor(Color.GRAY);
                                break;
                            case "+":
                                ( findViewById(R.id.pattern_resLayout).findViewWithTag("r" + i)).setBackgroundColor(Color.LTGRAY);
                                break;
                            case "-":
                                ( findViewById(R.id.pattern_resLayout).findViewWithTag("r" + i)).setBackgroundColor(Color.DKGRAY);
                        }
                    }
                }
                //set up FP,FN,FL
                ((TextView) findViewById(R.id.pt_falsePostitiveState)).setText(state.getString("FP"));
                ((TextView) findViewById(R.id.pt_falseNegetiveState)).setText(state.getString("FN"));
                ((TextView) findViewById(R.id.pt_fixationState)).setText(state.getString("FL"));
        }
        if (state.getBoolean("showToast")) {
            Log.d("Toast", "toast is going to be shown");
            Toast.makeText(this, state.getString("toastMessage"),
                    Toast.LENGTH_LONG).show();
            Actions.resetToast();
        }
    }

    public void saveUI() {
        Bundle state = Store.getstate();
        Bundle activeState = new Bundle();
        int activeView = state.getInt("viewID");
        activeState.putInt("viewID", activeView);
        switch (activeView) {
            case R.layout.activity_patinfo:
                EditText pi_patientName = (EditText) findViewById(R.id.pi_patientName);
                EditText pi_patientAge = (EditText) findViewById(R.id.pi_patientAge);
                Spinner pi_patientSex = (Spinner) findViewById(R.id.pi_patientSex);
                Spinner pi_testEye = (Spinner) findViewById(R.id.pi_testEye);
                Spinner pi_testType = (Spinner) findViewById(R.id.pi_testType);

                activeState.putString("pi_patientName", pi_patientName.getText().toString());
                activeState.putString("pi_patientAge", pi_patientAge.getText().toString());
                activeState.putString("pi_patientSex", pi_patientSex.getSelectedItem().toString());
                activeState.putString("pi_testEye", pi_testEye.getSelectedItem().toString());
                activeState.putString("pi_testType", pi_testType.getSelectedItem().toString());
                break;
            case R.layout.activity_setinfo:
                //no need for saving state as there is nothing editable. this will just come back
        }
        Actions.saveState(activeState);
    }

    public BroadcastReceiver UIReceiever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    @Override
    public void onBackPressed(){
      switch (activeViewID) {
          case R.layout.activity_initscreen:
              android.os.Process.killProcess(android.os.Process.myPid());
              System.exit(1);
              break;
          case R.layout.activity_patinfo:
              Actions.backToInitScreen();
              break;
          case R.layout.activity_waitscreen_is:
              Actions.backToInitScreen();
              break;
          case R.layout.activity_setinfo:
              Actions.backToRegister();
              break;
          case R.layout.activity_calibration:
              Actions.backToSetTest();
              break;
          case R.layout.activity_duringtest:
              //ignore
              break;
          case R.layout.activity_waitscreen_cl:
              Actions.backToSetTest();
              break;
          case R.layout.activity_reportselector:
              Actions.backToInitScreen();
              break;
          //TODO : Report view backs

      }
    }

    Runnable Test_ImageView = new Runnable() {
        @Override
        public void run() {

            while (true && activeViewID ==R.layout.activity_duringtest) {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (activeViewID == R.layout.activity_duringtest) {
                            ((ImageView) findViewById(R.id.dt_ImView)).setImageBitmap(Communication_MainModule.imageBitmapRxFinal);
                        }
                    }
                });
            }

        }
    };


    Runnable Calib_ImageView = new Runnable() {
        @Override
        public void run() {

            while (true && activeViewID ==R.layout.activity_calibration) {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (activeViewID == R.layout.activity_calibration) {
                            ((ImageView) findViewById(R.id.cl_ImView2)).setImageBitmap(Communication_MainModule.imageBitmapRxFinal);
                        }
                    }
                });
            }

        }
    };


}
 //TODO : Simpler way to do this
   /* EditText pi_patientName = (EditText) findViewById(R.id.pi_patientName);
    EditText pi_patientAge = (EditText) findViewById(R.id.pi_patientAge);
    Spinner pi_patientSex = (Spinner) findViewById(R.id.pi_patientSex);
    ArrayAdapter pi_patientSexAdapter = (ArrayAdapter) pi_patientSex.getAdapter();
    Spinner pi_testEye = (Spinner) findViewById(R.id.pi_testEye);
    ArrayAdapter pi_testEyeAdapter = (ArrayAdapter) pi_testEye.getAdapter();
    Spinner pi_testType = (Spinner) findViewById(R.id.pi_testType);
    ArrayAdapter pi_testTypeAdapter = (ArrayAdapter) pi_testType.getAdapter();

    //compact way to do it
    ((EditText)findViewById(R.id.pi_patientName)).setText(state.getString("pi_patientName"), TextView.BufferType.EDITABLE);
                ((EditText)findViewById(R.id.pi_patientAge)).setText(String.valueOf(state.getInt("pi_patientAge")), TextView.BufferType.EDITABLE);
                ((Spinner)findViewById(R.id.pi_patientSex)).setSelection(((ArrayAdapter)(((Spinner)findViewById(R.id.pi_patientSex)).getAdapter())).getPosition(state.getString("pi_patientSex")));
                 .setSelection(((ArrayAdapter)pi_testEye.getAdapter()).getPosition(state.getString("pi_testEye")));
              //  pi_testType.setSelection(((ArrayAdapter)pi_testType.getAdapter()).getPosition(state.getString("pi_testType")));
    */
