package com.agyohora.mobileperitc.store;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;

/**
 * Created by namputo on 09/06/17.
 */
import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.actions.Actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Store {
    //Initialize variables
    /*
     */
    //TODO : Implement comm disconnect too
    private static int activeView_number = R.layout.activity_initscreen;
    public static Bundle statebundle = new Bundle();
    private static Bundle sendDatabundle = new Bundle();
    private static int getStatecounter = 0;
    private static String pi_patientNameVal = null;
    private static String pi_patientAgeVal = null;
    private static String pi_patientSexVal = "Male";
    private static String pi_testEyeVal = "Left";
    private static String pi_testTypeVal = "Demo";
    //state variables that define a test set up information
    private static String set_patientName = "Jhon";
    private static String set_patientAge = null;
    private static String set_patientSex = null;
    private static String set_testEye = "Right";
    private static String set_testType = "Suprathreshold";
    //counter to tell HMD start test was clicked
    private static int set_startTestCounter = 0;
    //state variables related to calibration screen
    private static String cl_calibStatus = null;
    private static int cl_saveProccedVisbility = View.GONE;
    private static String cl_calibRecalib = "Calibrate";
    //the data that is sent to HMD
    //In calib test mode
    private static int cl_calibrateCounter = 0;
    private static int cl_skipCounter = 0;
    private static int cl_backCounter = 0;
    private static int cl_sendAndProceedCounter = 0;
    //In during test mode
    private static int dt_pauseCounter = 0;
    private static int dt_unpauseCounter = 0;
    private static int dt_abortCounter = 0;
    //during test specific
    private static boolean dt_chronometerOn = true;
    private static long dt_timebase = SystemClock.elapsedRealtime();
    private static boolean dt_testOver = false;
    //TODO:Vamsi needs to read about computer data structures
    private static ArrayList<String> dt_result = new ArrayList(54);
    private static int FP_ans = 0;
    private static int FP_total = 0;
    private static int FL_ans = 0;
    private static int FL_total = 0;
    private static int FN_ans = 0;
    private static int FN_total = 0;
    //generic toast flag
    private static boolean showToast = false;
    private static String toastMessage = null;
    //data to be sent to sample report views
    public static Bundle reportvals1 = new Bundle();
    private static ArrayList<Integer> repValAbs1 = new ArrayList(54);
    private static ArrayList<Integer> repValAgeRelNormal1 = new ArrayList(54);
    //data to be sent to sample report views
    public static Bundle reportvals2 = new Bundle();
    private static ArrayList<Integer> repValAbs2 = new ArrayList(54);
    private static ArrayList<Integer> repValAgeRelNormal2 = new ArrayList(54);
    public static Bundle reportvals3 = new Bundle();
    private static ArrayList<Integer> repValAbs3 = new ArrayList(54);
    private static ArrayList<Integer> repValAgeRelNormal3 = new ArrayList(54);

    private static boolean isCommInitializationOver = false;
    //

    private static boolean communicationActive = false;
    //test information object which is set on entering patient information
    public static class testInformation{
        String PatName;
        String PatAge;
        String PatSex;
        String TestType;
        String TestEye;

        testInformation(){
            PatName = null;
            PatAge = null;
            PatSex = null;
            TestType = null;
            TestEye = null;
        }
    }
    //object that sends buttong strokes
    public static class TCButtonData{
        String buttonName;
        String buttonData;
        TCButtonData(){
            buttonName = null;
            buttonData = null;
        }
    }


    private static testInformation testinfo = new testInformation();
    private static TCButtonData buttonClickData = new TCButtonData();
    private static int clickDataHashCode;
    private static Bundle clickData  = new Bundle();
    public static Gson gson = new Gson();

    public static void dataFromHMD(String incomingData){
        JSONObject dataFromHMDBundle = null; //this is the fully wrapped data bundle
        JSONObject dataReceived = null; //this is the wrapped data
        String dataTypeName = null;
        String data = null;
        try{
            dataFromHMDBundle = new JSONObject(incomingData);
            dataTypeName = dataFromHMDBundle.getString("dataName");
            data = dataFromHMDBundle.getString("data");
            switch (dataTypeName){
                case "TEST_INFORMATION":
                    dataReceived = new JSONObject(data);
                    set_patientName = dataReceived.getString("PatName");
                    set_patientAge = dataReceived.getString("PatAge");
                    set_patientSex = dataReceived.getString("PatSex");
                    set_testEye = dataReceived.getString("TestEye");
                    set_testType =dataReceived.getString("TestType");
                    activeView_number = R.layout.activity_setinfo;
                    showToast = false;
                    StoreTransmitter.updatedUIState();
                    break;
                case "RESULT":
                    dataReceived = new JSONObject(data);
                    Log.d("NampuResult",data);
                    JSONArray resList = dataReceived.getJSONArray("resList");
                    Log.d("NampuResult"," "+ resList.length());
                    for(int i=0;i<54;i++){
                        int resVal = resList.getInt(i);
                        switch (resVal){
                            case -1:
                                dt_result.set(i,".");
                                break;
                            case 0:
                                dt_result.set(i,"-");
                                break;
                            case 1:
                                dt_result.set(i,"+");
                                break;

                        }
                    }
                    //False Positive
                    JSONArray FPList = dataReceived.getJSONArray("FPList");
                    FP_total = FPList.length();
                    int FPsum = 0;
                    for(int i=0;i<FP_total;i++){
                        FPsum = FPsum+FPList.getInt(i);
                    }
                    FP_ans = FPsum;
                    JSONArray FNList = dataReceived.getJSONArray("FNList");
                    FN_total = FNList.length();
                    int FNsum = 0;
                    for(int i=0;i<FN_total;i++){
                        FNsum = FNsum+FNList.getInt(i);
                    }
                    FN_ans = FN_total-FNsum;
                    JSONArray FLList = dataReceived.getJSONArray("FLList");
                    FL_total = FLList.length();
                    int FLsum = 0;
                    for(int i=0;i<FL_total;i++){
                        FLsum = FLsum+FLList.getInt(i);
                    }
                    FL_ans = FLsum;
                    StoreTransmitter.updatedUIState();
                    break;
                case "FINAL_RESULT":
                    showToast = true;
                    toastMessage = "Test is complete. Results will load shortly";
                    dt_testOver = true;
                    StoreTransmitter.updatedUIState();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activeView_number = R.layout.activity_posttest;
                            StoreTransmitter.updatedUIState();
                        }
                    }, 5000);
                    break;
            }
        }catch (Throwable t){
            Log.d("NampuCommFromHMD","JSON Unwrap Failed");
        }
    }

    public static Bundle getstate(){
        Log.d("StoreGotCalled","get Store");
        statebundle.putInt("viewID",activeView_number);
        statebundle.putBoolean("showToast",showToast);
        if(showToast){
            statebundle.putString("toastMessage",toastMessage);
        }
        if(activeView_number==R.layout.activity_patinfo){
            statebundle.putString("pi_patientName",pi_patientNameVal);
            statebundle.putString("pi_patientAge",pi_patientAgeVal);
            statebundle.putString("pi_patientSex",pi_patientSexVal);
            statebundle.putString("pi_testEye",pi_testEyeVal);
            statebundle.putString("pi_testType",pi_testTypeVal);
        }
        if(activeView_number==R.layout.activity_setinfo){
            statebundle.putString("set_patientName",set_patientName);
            statebundle.putString("set_patientAge",set_patientAge);
            statebundle.putString("set_patientSex",set_patientSex);
            statebundle.putString("set_testEye",set_testEye);
            statebundle.putString("set_testType",set_testType);
        }
        if(activeView_number==R.layout.activity_calibration){
            statebundle.putString("cl_calibStatus",cl_calibStatus);
            statebundle.putString("cl_calibRecalib",cl_calibRecalib);
            statebundle.putInt("cl_saveProceedVisibility",cl_saveProccedVisbility);
        }
        //setting calibration screen specific informtion
        if(activeView_number==R.layout.activity_duringtest){
            statebundle.putString("set_patientName",set_patientName);
            statebundle.putString("set_patientAge",set_patientAge);
            statebundle.putString("set_patientSex",set_patientSex);
            statebundle.putString("set_testEye",set_testEye);
            statebundle.putString("set_testType",set_testType);
            //during test specific
            statebundle.putBoolean("dt_chronometerOn",dt_chronometerOn);
            statebundle.putLong("dt_timebase",dt_timebase);
            if(dt_result.isEmpty()) {
                for (int ii = 0; ii < 54; ii++) {
                    dt_result.add(ii, ".");
                }
            }
            statebundle.putBoolean("dt_testOver",dt_testOver);
            statebundle.putStringArrayList("dt_result",dt_result);
            statebundle.putString("FP",FP_ans+"/"+FP_total);
            statebundle.putString("FL",FL_ans+"/"+FL_total);
            statebundle.putString("FN",FN_ans+"/"+FN_total);
        }
        if(activeView_number==R.layout.activity_reportselector){
            getReportVals1();
            getReportVals2();
            getReportVals3();
        }
        return statebundle;
    }

    public static void initStatebundle(){

    }

    public static Bundle getSendData(){
        if(activeView_number==R.layout.activity_patinfo){
            sendDatabundle.putString("tc2HMD_patientName",set_patientName);
            sendDatabundle.putString("tc2HMD_patientAge",set_patientAge);
            sendDatabundle.putString("tc2HMD_patientSex",set_patientSex);
            sendDatabundle.putString("tc2HMD_testEye",set_testEye);
            sendDatabundle.putString("tc2HMD_testType",set_testType);
            sendDatabundle.putInt("tc2HMD_startTest",set_startTestCounter);
        }
        if(activeView_number == R.layout.activity_setinfo){
            sendDatabundle.putString("tc2HMD_patientName",set_patientName);
            sendDatabundle.putString("tc2HMD_patientAge",set_patientAge);
            sendDatabundle.putString("tc2HMD_patientSex",set_patientSex);
            sendDatabundle.putString("tc2HMD_testEye",set_testEye);
            sendDatabundle.putString("tc2HMD_testType",set_testType);
            sendDatabundle.putInt("tc2HMD_startTest",set_startTestCounter);
        }
        if (activeView_number == R.layout.activity_calibration) {
            sendDatabundle.putInt("tc2HMD_calibcounter",cl_calibrateCounter);
            sendDatabundle.putInt("tc2HMD_skipCounter",cl_skipCounter);
            sendDatabundle.putInt("tc2HMD_backCounter",cl_backCounter);
            sendDatabundle.putInt("tc2HMD_saveProceedCounter",cl_sendAndProceedCounter);
        }
        //todo : the senddatabundle for the during test mode
        return sendDatabundle;
    }


    public static BroadcastReceiver storeReceiever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle receivedPayload = intent.getBundleExtra(Actions.DISPATCH_PAYLOAD);
            String actionName = receivedPayload.getString("name");
            Object actionData = receivedPayload.get("data");
            Log.d("StoreSays","Hey Store Got Stuff here"+actionName);
            //TODO : Change to switch cases
            if(actionName.equals(Actions.ACTION_BACKTOINITSCREEN)){
                activeView_number = R.layout.activity_initscreen;
                StoreTransmitter.updatedUIState();
            }
            if (actionName.equals(Actions.ACTION_BACKTOREGISTER)){
                activeView_number = R.layout.activity_patinfo;
                StoreTransmitter.updatedUIState();
            }
            if(actionName.equals(Actions.ACTION_BACKTOSETTEST)){
                setClickData("BackToStartTest","NA");
                activeView_number= R.layout.activity_setinfo;
                if(communicationActive){
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC,clickData);
                    showToast = true;
                    toastMessage = "Okay!Going back to Start Test";
                }else{
                    showToast = true;
                    toastMessage = "Lost connection to HMD! Please try again";
                }
                StoreTransmitter.updatedUIState();
            }
            if(actionName.equals(Actions.ACTION_BEGINTEST)){
                Boolean fixationMonitoring = (boolean)actionData;
                setClickData("BeginTest",String.valueOf(fixationMonitoring));
                if(communicationActive){
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC,clickData);
                    Log.d("NampuComm","Begin test actiongot called");
                    showToast = false;
                    activeView_number = R.layout.activity_waitscreen_cl;
                }else{
                    showToast = true;
                    toastMessage = "Lost connection to HMD! Please try again";
                }


                StoreTransmitter.updatedUIState();
            }
            if(actionName.equals(Actions.ACTION_CALIBRATIONDONE)){
                cl_calibRecalib = "Recalibrate";
                cl_calibStatus = "Calibration is done";
                cl_saveProccedVisbility = View.VISIBLE;
                StoreTransmitter.updatedUIState();
            }
            if(actionName.equals(Actions.ACTION_COMMUNICATIONSTATUS)){
                String commState = (String)actionData;
                switch (commState){
                    case "RegisterTestPass":
                        if(activeView_number==R.layout.activity_waitscreen_pi){
                            Log.d( "NampuComm Store","We reached here");
                            showToast = false;
                            StoreTransmitter.updatedUIState();
                        }
                        break;
                    case "RegisterTestFail":
                        if(activeView_number==R.layout.activity_waitscreen_pi){
                            Log.d("NampuComm Store","Reached Here");
                            Log.d("NampuComm Store","We reached here to resend ifnormation");
                            //TODO : Check if this is the right way to do this : Alternative explained below
                            /*Todo: Do not change activeViewID when HMDNotConnected is called. Instead , one can show
                            [todo] a toast that HMD connection is lost and wait for the data to be sent Use a handler to check
                            [todo] on the communicationActive flag. if false, recall handler. if true; send the data and exit the
                            [todo] handler
                             */
                            if(communicationActive){
                                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC,clickData);
                            }else{
                                Log.d("NampuComm Store","Do nothing. The lost connection handler will go back to the setup page");
                            }
                        }
                        break;
                    case "StartTestPass":
                        if(activeView_number==R.layout.activity_waitscreen_st) {
                            //Log.d("NampuComStore", "Proceeding to Calibration");
                            activeView_number = R.layout.activity_calibration;
                            StoreTransmitter.updatedUIState();
                        }
                        break;
                    case "StartTestFail":
                        if(activeView_number==R.layout.activity_waitscreen_st){
                            if(communicationActive){
                                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC,clickData);
                            }else{
                                Log.d("NampuComm Store","Do nothing. The lost connection handler will go back to the start screen");
                                //activeView_number = R.layout.activity_setinfo;
                                //showToast = true;
                                //toastMessage = "Lost Connection with HMD. Please try again after 2 seconds";
                                //StoreTransmitter.updatedUIState();
                            }
                        }
                        break;
                    case "BackToStartTestPass":
                        activeView_number = R.layout.activity_setinfo;
                        StoreTransmitter.updatedUIState();
                        break;
                    case "BackToStartTestFail":
                        if(communicationActive) {
                            StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                        }else{
                            Log.d("NampuComm Store","Do nothing. the lost connection handler will take care");
                        }
                        break;
                    case "BeginTestPass":
                        if(activeView_number==R.layout.activity_waitscreen_cl) {
                            activeView_number = R.layout.activity_duringtest;
                            dt_chronometerOn = true;
                            dt_timebase = SystemClock.elapsedRealtime();
                            StoreTransmitter.updatedUIState();
                        }
                        break;
                    case "BeginTestFail":
                        Log.d("NampuCommBeginTest","Begin test is failing");
                        if (activeView_number==R.layout.activity_waitscreen_cl) {
                            if(communicationActive){
                                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC,clickData);
                            }else{
                                Log.d("NampuComm Store","No Connection. Go back to set test and wait");
                                activeView_number = R.layout.activity_calibration;
                                showToast = true;
                                toastMessage = "Lost Connection with HMD. Please try again after 2 seconds";
                                StoreTransmitter.updatedUIState();
                            }
                        }
                }
            }
            if(actionName.equals(Actions.ACTION_DATAFROMHMD)){
                String datafromHMD = (String)actionData;
                dataFromHMD(datafromHMD);
            }
            if(actionName.equals(Actions.ACTION_HMDCONNECTED)){
                //react to the HMD connected action as required
                //set connection status
                communicationActive = true;
                if(activeView_number==R.layout.activity_waitscreen_is){
                    isCommInitializationOver = true;
                    activeView_number = R.layout.activity_patinfo;
                    StoreTransmitter.updatedUIState();
                }else if(activeView_number==R.layout.activity_patinfo){
                    if (pi_patientNameVal != null && pi_patientAgeVal != null) {
                        showToast = true;
                        toastMessage = "Connected to HMD!Proceed";
                        StoreTransmitter.updatedUIState();
                    }
                }else{
                    showToast = true;
                    toastMessage = "Connected to HMD!Proceed";
                    StoreTransmitter.updatedUIState();
                }
            }
            if(actionName.equals(Actions.ACTION_HMDNOTCONNECTED)){
                //activeView_number = R.layout.activity_initscreen;
                //set communication status
                communicationActive = false;
                showToast = true;
                toastMessage = "Connection with HMD Lost.Please Wait";
                if(activeView_number==R.layout.activity_waitscreen_pi){
                    showToast = true;
                    activeView_number = R.layout.activity_patinfo;
                    StoreTransmitter.updatedUIState();
                }else if(activeView_number==R.layout.activity_waitscreen_st){
                    activeView_number = R.layout.activity_setinfo;
                    StoreTransmitter.updatedUIState();
                }else if(activeView_number==R.layout.activity_waitscreen_cl){
                    activeView_number = R.layout.activity_calibration;
                    StoreTransmitter.updatedUIState();
                }else if(activeView_number==R.layout.activity_patinfo) {
                    //do not want to update state when TC info is being input or when PI is not saved
                    //even once
                    if(pi_patientAgeVal!=null&&pi_patientNameVal!=null) {
                        StoreTransmitter.updatedUIState();
                    }
                }else {
                    StoreTransmitter.updatedUIState();
                }
            }
            if(actionName.equals(Actions.ACTION_REPORTVIEW)){
                activeView_number = R.layout.activity_reportselector;
                StoreTransmitter.updatedUIState();
            }
            if (actionName.equals(Actions.ACTION_REGISTERTEST)){
                Log.d("StoreSays"," "+pi_patientNameVal);
                if(!TextUtils.isEmpty(pi_patientAgeVal)&&!TextUtils.isEmpty(pi_patientNameVal)){
                    Log.d("StoreSays","register test does  reach here");
               ;
                    testinfo.PatName = pi_patientNameVal;
                    testinfo.PatAge = pi_patientAgeVal;
                    testinfo.PatSex = pi_patientSexVal;
                    testinfo.TestEye = pi_testEyeVal;
                    testinfo.TestType = pi_testTypeVal;
                    String testInfo = gson.toJson(testinfo);
                    buttonClickData.buttonName = "RegisterTest";
                    buttonClickData.buttonData = testInfo;
                    String sendData = gson.toJson(buttonClickData);
                    clickData.putInt("hashcode",sendData.hashCode());
                    clickData.putString("data",sendData);
                    clickData.putString("onPass","RegisterTestPass");
                    clickData.putString("onFail","RegisterTestFail");
                    if(communicationActive){
                        StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC,clickData);
                        showToast = false;
                        activeView_number = R.layout.activity_waitscreen_pi;
                    }else{
                        showToast = true;
                        toastMessage = "No connection to HMD! Please try again";
                    }


                }else if(TextUtils.isEmpty(pi_patientNameVal)){
                    showToast = true;
                    toastMessage = "Please eneter patient name";
                }else if(TextUtils.isEmpty(pi_patientAgeVal)){
                    showToast = true;
                    toastMessage = "Please enter patient age";
                }
                StoreTransmitter.updatedUIState();
            }
            if(actionName.equals(Actions.ACTION_RESETTOAST)){
                showToast = false;
            }
            if(actionName.equals(Actions.ACTION_SAVESTATE)){
                Bundle recvdState = (Bundle)actionData;
                activeView_number = recvdState.getInt("viewID");
                pi_patientNameVal = recvdState.getString("pi_patientName");
                pi_patientAgeVal = recvdState.getString("pi_patientAge");
                pi_patientSexVal = recvdState.getString("pi_patientSex");
                pi_testEyeVal = recvdState.getString("pi_testEye");
                pi_testTypeVal = recvdState.getString("pi_testType");
            }
            if (actionName.equals(Actions.ACTION_STARTNEWTEST)){
                if(communicationActive){
                    pi_patientNameVal = null;
                    pi_patientAgeVal = null;
                    pi_patientSexVal = "Male";
                    pi_testEyeVal = "Left";
                    pi_testTypeVal = "Suprathreshold";
                    dt_result = new ArrayList(54);
                    FP_ans = 0;
                    FP_total = 0;
                    FL_ans = 0;
                    FL_total = 0;
                    FN_ans = 0;
                     FN_total = 0;
                    activeView_number = R.layout.activity_patinfo;

                }else{
                    activeView_number = R.layout.activity_waitscreen_is;
                    if(isCommInitializationOver){
                      //Do nothing
                    }else{
                        StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_INITIALIZE,clickData);
                    }
                }
                //Log.d("storetoComm","initialize BT went");
                StoreTransmitter.updatedUIState();
            }
            if (actionName.equals(Actions.ACTION_STARTTEST)){
                buttonClickData.buttonName = "StartTest";
                buttonClickData.buttonData = "NA";
                String sendData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode",sendData.hashCode());
                clickData.putString("data",sendData);
                clickData.putString("onPass","StartTestPass");
                clickData.putString("onFail","StartTestFail");
                if(communicationActive){
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC,clickData);
                    showToast = false;
                    activeView_number = R.layout.activity_waitscreen_st;
                }else{
                    showToast = true;
                    toastMessage = "Connection with HMD Lost.Please Wait";
                }
                StoreTransmitter.updatedUIState();
            }
        }
    };

    public static void setClickData(String ButtonName,String ButtonData ){
        buttonClickData.buttonName = ButtonName;
        buttonClickData.buttonData = ButtonData;
        String sendData = gson.toJson(buttonClickData);
        clickData.putInt("hashcode",sendData.hashCode());
        clickData.putString("data",sendData);
        clickData.putString("onPass",ButtonName+"Pass");
        clickData.putString("onFail",ButtonName+"Fail");
    }

    //dummy functions for report values
    public static void getReportVals1(){
        reportvals1.putString("PatName","Zacheus");
        reportvals1.putString("PatAge","57");
        reportvals1.putString("testEye","Right");
        reportvals1.putString("testType","Zest");
        List<Integer> repVals1 = Arrays.asList(25,24,20,12,24,22,20,8,25,25,23,27,25,26,26,22,20,27,24,21,13,26,24,22,26,25,25,23,27,19,23,20,23,27,30,24,27,27,25,29,29,29,28,29,27,27,26,0,28,28,29,27,23,24);
        repValAbs1 = new ArrayList<Integer>(repVals1);
        List<Integer> repvalsAge1 = Arrays.asList(32,32,30,27,32,31,30,27,31,32,31,30,29,33,31,30,30,33,32,32,30,31,33,29,32,29,33,32,32,30,33,33,31,29,32,31,30,30,30,33,32,31,30,32,32,31,30,0,31,31,30,31,29,29);
        repValAgeRelNormal1= new ArrayList<Integer>(repvalsAge1);
        List<Integer> patDevVals1 = Arrays.asList(-2,-3,-6,-13,-3,-5,-6,-17,-2,-1,-2,0,-1,-1,-1,-4,-5,0,-3,-5,-12,-1,-2,-3,-1,-1,-2,-4,1,-6,-4,-7,-3,2,3,-2,2,0,-1,2,2,3,3,2,0,1,1,-27,2,3,2,1,-2,-1);
        ArrayList<Integer> patternDeviation1 = new ArrayList<Integer>(patDevVals1);
        reportvals1.putIntegerArrayList("AbsValues",repValAbs1);
        reportvals1.putIntegerArrayList("AgeRelNorm",repValAgeRelNormal1);
        reportvals1.putIntegerArrayList("PatternDeviation",patternDeviation1);
        reportvals1.putString("FP","0/11");
        reportvals1.putString("FL","0/11");
        reportvals1.putString("FN","0/10");
    }
    public static void getReportVals2(){
        reportvals2.putString("PatName","Lakshmi");
        reportvals2.putString("PatAge","20");
        reportvals2.putString("testEye","Right");
        reportvals2.putString("testType","Zest");
        List<Integer> repVals2 = Arrays.asList (36,32,25,27,35,34,31,28,23,33,27,28,29,39,32,31,28,33,29,28,20,29,32,29,31,30,36,33,30,29,34,34,30,30,35,30,30,33,30,34,33,31,32,33,33,32,32,0,33,32,32,32,28,29);
        repValAbs2 = new ArrayList<Integer>(repVals2);
        List<Integer> repvalsAge2 = Arrays.asList(34,34,31,30,34,32,31,29,31,32,31,32,31,34,34,32,30,34,33,32,30,33,33,31,31,31,35,35,33,31,34,34,32,30,34,33,32,32,31,34,34,33,31,34,33,32,31,0,33,32,32,32,29,30);
        repValAgeRelNormal2= new ArrayList<Integer>(repvalsAge2);
        List<Integer> patDevVals1 = Arrays.asList(3,-2,-8,-5,1,0,-1,-3,-11,0,-4,-5,-3,5,-1,-1,-3,-1,-4,-5,-11,-5,0,-3,-2,-2,3,-1,-2,-3,0,0,-2,-2,2,-2,-1,-1,-3,1,-1,-2,1,-1,-1,-1,1,-34,0,0,-1,-1,-4,-3);
        ArrayList<Integer> patternDeviation2 = new ArrayList<Integer>(patDevVals1);
        reportvals2.putIntegerArrayList("AbsValues",repValAbs2);
        reportvals2.putIntegerArrayList("AgeRelNorm",repValAgeRelNormal2);
        reportvals2.putIntegerArrayList("PatternDeviation",patternDeviation2);
        reportvals2.putString("FP","0/11");
        reportvals2.putString("FL","0/11");
        reportvals2.putString("FN","0/11");
    }
    public static void getReportVals3(){
        reportvals3.putString("PatName","Venugopal");
        reportvals3.putString("PatAge","62");
        reportvals3.putString("testEye","Left");
        reportvals3.putString("testType","Zest");
        List<Integer> repVals3 = Arrays.asList (0,0,5,10,2,0,12,11,0,2,2,0,0,3,0,2,7,0,0,5,12,0,4,18,9,10,28,20,19,18,29,27,20,20,0,19,15,13,13,28,25,23,21,25,20,23,21,5,2,18,4,9,12,12);
        repValAbs3 = new ArrayList<Integer>(repVals3);
        List<Integer> repvalsAge3 = Arrays.asList(32,31,29,26,31,31,28,26,31,30,28,29,28,32,31,29,26,31,30,28,36,33,29,28,29,28,32,31,31,30,32,31,30,29,0,30,30,30,30,32,32,31,29,32,31,30,29,31,30,29,29,29,26,26);
        repValAgeRelNormal3= new ArrayList<Integer>(repvalsAge3);
        List<Integer> patDevVals3 = Arrays.asList(-23,-23,-17,-11,-21,-23,-11,-10,-23,-20,-19,-23,-22,-20,-23,-20,-14,-23,-23,-17,-9,-23,-18,-4,-14,-12,5,-4,-3,-4,6,3,-2,-1,-23,-4,-6,-11,-9,4,1,1,-1,2,-3,1,-1,-19,-20,-4,-20,-14,-9,-9);
        ArrayList<Integer> patternDeviation3 = new ArrayList<Integer>(patDevVals3);
        reportvals3.putIntegerArrayList("AbsValues",repValAbs3);
        reportvals3.putIntegerArrayList("AgeRelNorm",repValAgeRelNormal3);
        reportvals3.putIntegerArrayList("PatternDeviation",patternDeviation3);
        reportvals3.putString("FP","1/11");
        reportvals3.putString("FL","0/11");
        reportvals3.putString("FN","2/11");
    }

}
