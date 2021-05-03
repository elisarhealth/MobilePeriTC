package com.agyohora.mobileperitc.communication;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;

import com.agyohora.mobileperitc.actions.Actions;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.store.StoreTransmitter;
import com.agyohora.mobileperitc.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.agyohora.mobileperitc.store.Store.isCommInitializationOver;
import static com.agyohora.mobileperitc.store.StoreTransmitter.COMM_FUNCTION_SEND_UPDATE;
import static com.agyohora.mobileperitc.store.StoreTransmitter.COMM_FUNCTION_STOP;

@SuppressWarnings("AccessStaticViaInstance")
public class CommunicationService extends Service {

    public static boolean isHMDConnected = false;
    boolean isDataRecievedJSONValid;
    public static Bitmap videoFrame = null;
    public static Bitmap tempFrame = null;
    public static long duplicateFrameThreshold;
    public static boolean isFeedbackSent = false;
    //promise checking hashcode variable
    private static String rxDataHashcode;
    final String TAG = "CommunicationService";

    @Override
    public void onCreate() {
        CommInitialize();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String function = intent.getStringExtra("FUNCTION");
            Log.d("CommunicationService", "comm receiver is working " + function);
            if (function != null && !function.equals(""))
                switch (function) {
                    case StoreTransmitter.COMM_FUNCTION_INITIALIZE:
                        Log.d(TAG, "Comm initialize has already executed " + isCommInitializationOver);
                        break;
                    case StoreTransmitter.COMM_FUNCTION_CLICKONTC:
                        Bundle datatoSend = intent.getBundleExtra("FUNCTION_DATA");
                        String data = datatoSend.getString("data");
                        int hashcode = datatoSend.getInt("hashcode");
                        String onPass = datatoSend.getString("onPass");
                        String onFail = datatoSend.getString("onFail");
                        Write_Tx_Data_Promise("TC_BUTTON_CLICK", hashcode, data, onPass, onFail);
                        break;
                    case StoreTransmitter.COMM_FUNCTION_CONTINUE:
                        CommInitialize();
                        break;
                    case COMM_FUNCTION_SEND_UPDATE:
                        Log.d("ComService", "COMM_FUNCTION_SEND_UPDATE called");
                        Thread sendUpdateThread = new Thread(sendUpdateRunnable);
                        sendUpdateThread.start();
                        break;
                    case COMM_FUNCTION_STOP:
                        //isHMDConnected = false;
                        CommonUtils.switchOffHotSpot(this);
                        stopSelf();
                        break;
                }
        }

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void StopCommunication() {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d("ComService", "Destoryed");
        super.onDestroy();
    }

    WifiCommunicationManager.MessageChannel txMessageChannelArray[] = new WifiCommunicationManager.MessageChannel[]
            {
                    new WifiCommunicationManager.MessageChannel("HTBT"),
                    new WifiCommunicationManager.MessageChannel("DATA"),
                    new WifiCommunicationManager.MessageChannel("VIDEO1"),
                    new WifiCommunicationManager.MessageChannel("VIDEO2"),
                    new WifiCommunicationManager.MessageChannel("FILE")
            };

    WifiCommunicationManager.MessageChannel rxMessageChannelArray[] = new WifiCommunicationManager.MessageChannel[]
            {
                    new WifiCommunicationManager.MessageChannel("HTBT"),
                    new WifiCommunicationManager.MessageChannel("DATA"),
                    new WifiCommunicationManager.MessageChannel("VIDEO1"),
                    new WifiCommunicationManager.MessageChannel("VIDEO2"),
                    new WifiCommunicationManager.MessageChannel("FILE")
            };

    WifiCommunicationManager.MessageReceiptCallback messageReceiptCallback = new WifiCommunicationManager.MessageReceiptCallback() {
        @Override
        public void onMessageReceived(String messageChannelId, String message_StringBase64) {

            if (messageChannelId.equals("DATA")) {
                Log.d(TAG, "Some DATA is coming in");
                String dataMessageString = new String(Base64.decode(message_StringBase64, Base64.DEFAULT));
                isDataRecievedJSONValid = false;
                //Log.d(TAG, "RX_MESSAGE = " + dataMessageString);
                JSONObject rxDataMessage = null;
                try {
                    rxDataMessage = new JSONObject(dataMessageString);
                    isDataRecievedJSONValid = true;
                    Log.d(TAG, "A new message was received and casted");
                } catch (JSONException e) {
                    isDataRecievedJSONValid = false;
                    Log.e(TAG, "JSONCasting did not happen");
                }

                //TODO:Check if this is too blocking
                if (isDataRecievedJSONValid) {
                    try {
                        String packetType = (String) rxDataMessage.get("PACKET_TYPE");
                        switch (packetType) {
                            case "DATA": {
                                int hashcode = rxDataMessage.getInt("PACKET_NO");
                                String packet_data = (String) rxDataMessage.get("PACKET_DATA");
                                Log.d("NampuCommFROMHMD", packet_data);
                                On_Data_Packet_Received(packet_data, hashcode);
                                break;
                            }
                            case "COMM_STATUS":
                                rxDataHashcode = (String) rxDataMessage.get("PACKET_DATA");
                                Log.d("NampuComm", rxDataHashcode);
                                break;
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error occurred in extracting specific key out of the JSONObject");
                    }
                }

            }

            if (messageChannelId.equals("VIDEO1")) {
                Log.d(TAG, "RX MESSAGE : VIDEO1 : " + message_StringBase64);
                Log.d(TAG, "RX MESSAGE : Size : " + message_StringBase64.getBytes().length);
                byte[] videoMessageByteArray = Base64.decode(message_StringBase64, Base64.DEFAULT);
                videoFrame = BitmapFactory.decodeByteArray(videoMessageByteArray, 0, videoMessageByteArray.length);
                if (tempFrame != null) {
                    if (tempFrame.sameAs(videoFrame)) {
                        duplicateFrameThreshold = duplicateFrameThreshold + 1;
                        Log.d("TempFrame", "equals " + duplicateFrameThreshold);
                    } else {
                        tempFrame = videoFrame;
                        duplicateFrameThreshold = 0;
                        Log.d("TempFrame", "not equals " + duplicateFrameThreshold);
                    }
                } else {
                    tempFrame = videoFrame;
                }
                if (!isFeedbackSent)
                    if (duplicateFrameThreshold > 90) {
                        Log.d("Counter", "reached threshold");
                        isFeedbackSent = true;
                        Actions.cameraFreeze();
                    }
            }

        }
    };

    //todo : call back whenever HMD is connected or disconnected is required.

    //Instantiate the WifiCommunicationManager
    // public WifiCommunicationManager wifiCommunicationManager = new WifiCommunicationManager(MainActivity.applicationContext, txMessageChannelArray, rxMessageChannelArray, messageReceiptCallback);
    public WifiCommunicationManager wifiCommunicationManager = new WifiCommunicationManager(MyApplication.getInstance(), txMessageChannelArray, rxMessageChannelArray, messageReceiptCallback);


    void CommInitialize() {
        //Define the wifiCommunicationManager_thread
        Thread wifiCommunicationManager_thread = new Thread(wifiCommunicationManager_runnable);
        Thread checkConnectionStatusThread = new Thread(checkConnectedState);
        wifiCommunicationManager_thread.start();

        Log.d(TAG, "wifiCommunicationManager Thread started");
        isHMDConnected = false;
        //Start the state machine
        boolean commInitialized = wifiCommunicationManager.initializeCommunication();
        checkConnectionStatusThread.start();


    }

    Runnable wifiCommunicationManager_runnable = new Runnable() {
        @Override
        public void run() {

            wifiCommunicationManager.WifiCommunicationStateMachine();

        }
    };


    Handler CommStatusHandler = new Handler();

    Runnable checkConnectedState = new Runnable() {
        @Override
        public void run() {
            try {
                //check for HMD Status
                boolean HMDConnectionState = wifiCommunicationManager.isConnected();
                if (HMDConnectionState) {
                    if (!isHMDConnected) {
                        MyApplication.getInstance().set_HMD_CONNECTED(true);
                        //Actions.HMDConnected();
                        isHMDConnected = HMDConnectionState;
                    }
                } else {
                    if (isHMDConnected) {
                        Actions.HMDNotConnected();
                        isHMDConnected = HMDConnectionState;
                    }
                }
                CommStatusHandler.removeCallbacks(checkConnectedState);
                CommStatusHandler.postDelayed(checkConnectedState, 5000);
            } catch (Exception e) {
                Log.e("HMDStatusCheck", "This is not working");
            }
        }
    };

    public void Write_Tx_Data_Promise(String packetType, final int packetNo, String txDataPacket, final String OnPass, final String OnFail) {
        String wrappedDATAString = buildDataPacketToTransmit(packetType, packetNo, txDataPacket);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Log.e("InterruptedException", e.getMessage());
        }
        wifiCommunicationManager.transmitMessage("DATA", wrappedDATAString.getBytes());
        Log.d(TAG, "the data string has been sent");
        //part of code that performs the promise
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String castedHashcode = String.valueOf(packetNo);
                Log.d("NampuComm", rxDataHashcode + " Cast hashcode " + castedHashcode);
                if (castedHashcode.equals(rxDataHashcode)) {
                    Log.d("NampuComm", "CommStatus Passed");
                    Actions.commStatus(OnPass);
                } else {
                    Log.d("NampuComm", "CommStatus Failed");
                }
            }
        }, 500);
    }

    public void Write_Tx_Data(String packetType, int packetNo, String txDataPacket) {
        txDataPacket = buildDataPacketToTransmit(packetType, packetNo, txDataPacket);
        wifiCommunicationManager.transmitMessage("DATA", txDataPacket.getBytes());
    }

    public void On_Data_Packet_Received(String data, int hashcode) {
        Actions.dataFromHMD(data);
        Log.d("NampuCommFROMHMD", String.valueOf(hashcode));
        Log.d("NampuCommFROMHMD", "dataFromHMD " + String.valueOf(hashcode));
        Write_Tx_Data("COMM_STATUS", 0, String.valueOf(hashcode));
    }

    public String buildDataPacketToTransmit(String packetType, int packetNo, String packetData) {

        JSONObject builtPacketJsonObject = new JSONObject();
        try {
            builtPacketJsonObject.put("##PACKET_START##", "");
            builtPacketJsonObject.put("PACKET_TYPE", packetType);
            builtPacketJsonObject.put("PACKET_NO", packetNo);
            builtPacketJsonObject.put("PACKET_LENGTH", packetData.length());
            builtPacketJsonObject.put("PACKET_DATA", packetData);
            builtPacketJsonObject.put("##PACKET_END##", "");
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSONObject : " + e.toString());
        }

        return builtPacketJsonObject.toString();
    }

    Runnable sendUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] apk = CommonUtils.convertUpdateApkToBytes(MyApplication.getInstance());
            Log.d("ComService O", "apk size " + apk.length);
            wifiCommunicationManager.transmitMessage("FILE", apk);

        }
    };

}
