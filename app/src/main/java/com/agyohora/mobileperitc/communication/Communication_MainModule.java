package com.agyohora.mobileperitc.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;


import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.actions.Actions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import static com.agyohora.mobileperitc.userInterface.MyActivity.applicationContext;


public class Communication_MainModule {

    private static final String TAG = "MP TC - Comm Main";

    //TODO: How do we transfer these from one HMD to other???
    private static String serverIP = "192.168.43.1";
    //private static String serverIP = "172.16.1.33";
    private static final String NETWORK_SSID = "HMD 000001";
    private static final String NETWORK_PASSWORD = "agyohora";

    private static int dataServerPort = 50000;
    private static ServerSocket dataServerSocket = null;
    private static Socket dataSocket = null;

    private static int videoServerPort = 50001;
    private static ServerSocket videoServerSocket = null;
    private static Socket videoSocket = null;

    private static int heartBeatPacketNo = 0;

    private static String rxDataPacketFinal = "dummy";
    private static String rxHeartBeatDataPacketFinal = "dummy";
    private static String rxDataHashcode = "dummy";

    private static OutputStream dataTxStream = null;
    private static InputStream videoRxStream = null;
    private static InputStream dataRxStream = null;

    public static boolean commChannelFunctional = false;

    //private static boolean wifimanagerInitialize = false;

    private static ArrayBlockingQueue<String> dataPacketTxQueue = new ArrayBlockingQueue<String>(1);

    //Turn this flag on
    private static boolean videoStreamingEnabledFlag = true;

    public static Bitmap imageBitmapRxFinal = null;

    //The TC_Comm_Channel_Init is run in a separate thread so that the BroadcastReceiver (Communication_Receiver) is non-blocking
    //The TC_Comm_Channel_Init spawns off the Tx, Rx and HeartBeat threads
    public static Runnable Comm_Channel_Init_Thread = new Runnable() {
        @Override
        public void run() {
            //NAMPU: As there is a while loop which waits for connection with HMD to occur, we need a flag to send to the store tha status only once!

            //nampu added a flag wifimanaerInitialize
            /*WifiManager wifiManager = (WifiManager) applicationContext.getSystemService(applicationContext.WIFI_SERVICE);

                Log.d(TAG,"WiFi State"+wifiManager.getWifiState());

                wifiManager.setWifiEnabled(true);
                WifiConfiguration wifiConfiguration = new WifiConfiguration();
                wifiConfiguration.SSID = "\"" + NETWORK_SSID + "\"";
                wifiConfiguration.preSharedKey = "\""+ NETWORK_PASSWORD +"\"";
                wifiManager.addNetwork(wifiConfiguration);

                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                for( WifiConfiguration i : list ) {
                    if(i.SSID != null && i.SSID.equals("\"" + NETWORK_SSID + "\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);
                        Boolean wifiConnectionStatus = wifiManager.reconnect();
                        Log.d(TAG,"wifiManager.reconnect() status... : "+wifiConnectionStatus);
                        Log.d(TAG,"wifi BSSID"+wifiManager.getConnectionInfo().getBSSID());
                        break;
                    }
                }

                Log.d(TAG,"WiFi State"+wifiManager.getWifiState());*/


            //Initializing all of them to null
            dataTxStream = null;
            dataRxStream = null;
            videoRxStream = null;
            dataSocket = null;
            videoSocket = null;


            while (true){
                try{
                    dataSocket = new Socket(serverIP,dataServerPort);
                    Log.d(TAG,"Trying to connect to dataSocket...");
                }catch (IOException e){
                    Log.d(TAG,"Error occured in connecting dataSocket, maybe because server is not ready to accept connections : "+e.toString());
                }

                try{
                    Thread.sleep(100);
                }
                catch (InterruptedException e){
                    Log.d(TAG,"Error occured in Thread.sleep : "+e.toString());
                }

                //Basically, loop until connection is successfully made...
                if(dataSocket!=null){
                    if(dataSocket.isConnected()){
                        Log.d(TAG,"dataSocket is connected!!!");
                        break;
                    }
                }
            }

            while (true){
                try{
                    videoSocket = new Socket(serverIP,videoServerPort);
                    Log.d(TAG,"Trying to connect to videoSocket...");
                }catch (IOException e){
                    Log.d(TAG,"Error occured in connecting to videoSocket, maybe because server is not ready to accept connections : "+e.toString());
                }

                try{
                    Thread.sleep(100);
                }
                catch (InterruptedException e){
                    Log.d(TAG,"Error occured in Thread.sleep : "+e.toString());
                }

                //Basically, loop until connection is successfully made...
                if(videoSocket!=null){
                    if(videoSocket.isConnected()){
                        Log.d(TAG,"videoSocket is connected!!!");
                        break;
                    }
                }
            }



            //Start off the Tx thread, Rx thread and the Heart Beat Manager Threads
            Thread Data_Tx_Parallel_Thread = new Thread(Data_Tx_Thread);
            Data_Tx_Parallel_Thread.start();

            Thread Data_Rx_Parallel_Thread = new Thread(Data_Rx_Thread);
            Data_Rx_Parallel_Thread.start();

            Thread Heart_Beat_Manager_Thread = new Thread(Heart_Beat_Watchdog);
            Heart_Beat_Manager_Thread.start();

            Thread Video_Streaming_Thread = new Thread(Video_Rx_Thread);
            Video_Streaming_Thread.start();

            commChannelFunctional = true;
            //Send Comm sockets connected Active (NAMPU ADDED THIS)
            Actions.HMDConnected();

        }
    };

    //This method takes in the bare message (String) to transmit, appends the attributes, converts it into a JSONObject  and returns the stringified JSONObject to transmit
    public static String buildDataPacketToTransmit(String packetType, int packetNo,String packetData){

        JSONObject builtPacketJsonObject = new JSONObject();
        try{
            builtPacketJsonObject.put("##PACKET_START##","");
            builtPacketJsonObject.put("PACKET_TYPE",packetType);
            builtPacketJsonObject.put("PACKET_NO",packetNo);
            builtPacketJsonObject.put("PACKET_LENGTH",packetData.length());
            builtPacketJsonObject.put("PACKET_DATA",packetData);
            builtPacketJsonObject.put("##PACKET_END##","");
        }catch (JSONException e){
            Log.d(TAG,"Error creating JSONObject : "+e.toString());
        }

        return builtPacketJsonObject.toString();
    }

    public static Runnable Data_Tx_Thread = new Runnable() {
        @Override
        public void run() {
            String txData = "dummy1";

            while (!commChannelFunctional) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {

                }
            }

            try{
                dataTxStream = dataSocket.getOutputStream();
                Log.d(TAG,"OutputStream for dataSocket successfully acquired!!");
                dataTxStream.flush();
            }catch (IOException e){
                Log.d(TAG,"Error occurred in dataSocket.getOutputStream" + e.toString());
            }

            while (commChannelFunctional){
                if (dataPacketTxQueue.size() > 0){
                    txData = dataPacketTxQueue.poll();

                    if((txData != null)&&(txData != "")){
                        try{
                            Log.d(TAG,"txData = "+txData);
                            dataTxStream.write(txData.getBytes());
                        }catch (IOException e){
                            //Log.d(TAG,"Exception occurred in dataTxStream.write");
                            try{
                                Thread.sleep(100); // This wait is put so as to avoid ultra-fast while loop in case of exception
                            }catch (InterruptedException i){

                            }
                        }
                    }
                }else{
                    try{
                        Thread.sleep(10);
                    }catch (InterruptedException e){

                    }
                }
            }
        }
    };


    public static Runnable Data_Rx_Thread = new Runnable() {
        @Override
        public void run() {

            while (!commChannelFunctional) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {

                }
            }

            byte[] rxPacketRaw = new byte[1000];
            int noOfBytes = -1;
            String rxPacketTrimmed = "";
            JSONObject rxDataJsonObject = null;
            boolean JsonObjectValid = false;

            try{
                dataRxStream = dataSocket.getInputStream();
                Log.d(TAG,"InputStream for dataSocket successfully acquired!!");
            }catch (IOException e){
                Log.d(TAG,"Error occurred in dataSocket.getInputStream" + e.toString());
            }

            while (commChannelFunctional) {

                try {
                    noOfBytes = dataRxStream.read(rxPacketRaw);
                    String rxPacketRawString = new String(rxPacketRaw);
                    Log.d(TAG,"Raw data received = "+rxPacketRawString);
                } catch (IOException e) {
                    Log.d(TAG,"Exception occurred in dataRxStream.read");
                }

                //If non-zero number of bytes are received...
                if(noOfBytes != -1) {
                    byte[] rxPacketBytesTrimmed = new byte[noOfBytes];
                    System.arraycopy(rxPacketRaw, 0, rxPacketBytesTrimmed, 0, noOfBytes);
                    rxPacketTrimmed = new String(rxPacketBytesTrimmed);
                    //Log.d(TAG,"Raw Data Received : "+rxPacketTrimmed);

                    //Try to fit the received data to a JSONObject
                    try{
                        rxDataJsonObject = new JSONObject(rxPacketTrimmed);
                        JsonObjectValid = true;
                    }catch (JSONException e){
                        Log.d(TAG,"JSONException - Unable to cast the received data packet into JSONObject");
                        JsonObjectValid = false;
                    }

                    //If JSONObject is valid...
                    if (JsonObjectValid){
                        try{
                            String packetType = (String) rxDataJsonObject.get("PACKET_TYPE");
                            switch (packetType){
                                case "DATA":{

                                    int hashcode  = rxDataJsonObject.getInt("PACKET_NO");
                                    rxDataPacketFinal = (String) rxDataJsonObject.get("PACKET_DATA");
                                    Log.d("NampuCommFROMHMD",rxDataPacketFinal);
                                    On_Data_Packet_Received(rxDataPacketFinal, hashcode);
                                     // TODO: Insert your non-blocking code in this method
                                    break;
                                }
                                case "HEARTBEAT":{
                                    rxHeartBeatDataPacketFinal = (String) rxDataJsonObject.get("PACKET_DATA");
                                    //Log.d(TAG,"Rx Extracted Data Packet : "+rxHeartBeatDataPacketFinal);
                                    On_HeartBeat_Received(); // TODO: Insert your non-blocking code in this method
                                    break;

                                }
                                case "COMM_STATUS":
                                    rxDataHashcode = (String) rxDataJsonObject.get("PACKET_DATA");
                                    Log.d("NampuComm",rxDataHashcode);
                                    break;
                            }
                        }catch (JSONException e){
                            Log.d(TAG,"Error occurred in extracting specific key out of the JSONObject");
                        }

                    }
                }else{
                    try{
                        Thread.sleep(10);
                        Log.d(TAG,"No data received in this iteration");
                    }catch (InterruptedException e){

                    }
                }

            }
        }
    };

    public static Runnable Video_Rx_Thread = new Runnable() {
        @Override
        public void run() {

            while (!commChannelFunctional) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {

                }
            }

            byte[] rxVideoPacketByteArray = new byte[1000];
            String rxVideoPacketString = "";
            String rxVideoFrameString = "";

            try{
                videoRxStream = videoSocket.getInputStream();
                Log.d(TAG,"InputStream for videoSocket successfully acquired!!");
            }catch (IOException e){
                Log.d(TAG,"Error occurred in dataSocket.getInputStream" + e.toString());
            }

            int iterationCount = 0;

            while (commChannelFunctional){
                if(videoStreamingEnabledFlag){

                    boolean rxVideoBytesAvailable = false;
                    try{
                        rxVideoBytesAvailable= (videoRxStream.available() > 10);
                    }catch (IOException e){

                    }

                    if(rxVideoBytesAvailable){
                        try{
                            int noOfRxBytes = videoRxStream.read(rxVideoPacketByteArray);
                            byte[] rxVideoPacketByteArrayTrimmed = new byte[noOfRxBytes];
                            System.arraycopy(rxVideoPacketByteArray,0,rxVideoPacketByteArrayTrimmed,0,noOfRxBytes);
                            //Log.d(TAG,"Raw rxVideoPacketString : "+new String(rxVideoPacketByteArray));
                            rxVideoPacketString = new String(rxVideoPacketByteArrayTrimmed);
                        }catch (IOException e){
                            Log.d(TAG,"Exception occurred in videoRxStream.read"+e.toString());
                        }



                        //Log.d("jpeg_info","rxVideoPacketString with header and footer: "+iterationCount+ " : "+rxVideoPacketString);

                        int frameStart = rxVideoPacketString.indexOf("$$$##");
                        int frameEnd = rxVideoPacketString.indexOf("###$$");

                        if(frameStart != -1){
                            //Frame start detected
                            rxVideoFrameString = rxVideoPacketString.substring(frameStart+5); // '5' is hardcoded for now //Log.d("jpeg_info","Frame_Start Detected at = " + frameStart + "  ; iterationCount = " + iterationCount);
                        }else if ((frameStart == -1) && (frameEnd == -1)){
                            rxVideoFrameString = rxVideoFrameString+rxVideoPacketString;
                        }else if (frameEnd != -1){

                            //Log.d("jpeg_info","Frame_End Detected at = " + frameEnd + "  ; iterationCount = " + iterationCount);
                            rxVideoFrameString = rxVideoFrameString + rxVideoPacketString.substring(0,frameEnd);
                            //Log.d("jpeg_length","Frame_String Length : " + rxVideoFrameString.length() + "  ; iterationCount = " + iterationCount);

                            try{
                                byte[] rxImageFrameByteArray = Base64.decode(rxVideoFrameString,Base64.DEFAULT);
                                imageBitmapRxFinal = BitmapFactory.decodeByteArray(rxImageFrameByteArray,0,rxImageFrameByteArray.length);
                                Log.d(TAG,"Image Received & Decoded!!");


                            }catch(IllegalArgumentException e){
                                Log.d(TAG,"Corrupted Image Frame Received - Could not decode JEPG");
                            }

                            rxVideoFrameString = "";

                        }

                        iterationCount++;
                    }
                }
            }
        }
    };

    public static Runnable Heart_Beat_Watchdog = new Runnable() {
        @Override
        public void run() {
            while (!commChannelFunctional){
                try{
                    Thread.sleep(10);
                }catch (InterruptedException e){

                }
            }

            String registeredHeartBeatData = "";
            int missedHeartBeatNo = 0;

            while (true){
                Long systemCurrentTime = System.currentTimeMillis();
                Write_Tx_Data("HEARTBEAT",heartBeatPacketNo,systemCurrentTime.toString());
                heartBeatPacketNo++;
                try{
                    Thread.sleep(500);
                }catch (InterruptedException e){

                }
                if (registeredHeartBeatData == rxHeartBeatDataPacketFinal){
                    missedHeartBeatNo++;
                    Log.d(TAG,"missedHeartBeatNo. = "+missedHeartBeatNo);
                }else {
                    missedHeartBeatNo = 0;
                }
                registeredHeartBeatData = rxHeartBeatDataPacketFinal;
                if(missedHeartBeatNo > 4){
                    //Catastrophe...!!!
                    Log.d(TAG,"HeartBeat failed to receive for more than 4 iterations");
                    break;
                }
            }

            Log.d(TAG,"Need to reinitialize all the threads if you give me the permission, My Lord!!");
            Actions.HMDNotConnected();
            Stop_All_Threads_And_Reinitialize();

        }
    };

    public static void Stop_All_Threads_And_Reinitialize(){

       //Making this flag false, stops all other threads' while loops
        commChannelFunctional = false;

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){

        }

        try{
            Log.d(TAG,"is dataTxStream null???"+" -- "+(dataTxStream==null));
            Log.d(TAG,"is dataRxStream null???"+" -- "+(dataRxStream==null));
            Log.d(TAG,"is videoRxStream null???"+" -- "+(videoRxStream==null));
            Log.d(TAG,"is dataSocket null???"+" -- "+(dataSocket==null));
            Log.d(TAG,"is videoSocket null???"+" -- "+(videoSocket==null));
            dataTxStream.close();
            dataRxStream.close();
            videoRxStream.close();
            dataSocket.close();
            videoSocket.close();

        }catch (IOException e){

        }



        //Initialize the communication channel again
        Comm_Initialize();
    }

    public static void Comm_Initialize(){

        Thread Comm_Channel_Init_Parallel_Thread = new Thread(Comm_Channel_Init_Thread);
        Comm_Channel_Init_Parallel_Thread.start();

    }

    public static void Enable_Video_Streaming(Boolean shouldVideoBeStreamed){
        videoStreamingEnabledFlag = shouldVideoBeStreamed;
    }

    public static void Write_Tx_Data(String packetType, int packetNo, String txDataPacket){
        txDataPacket = buildDataPacketToTransmit(packetType,packetNo,txDataPacket);
        dataPacketTxQueue.offer(txDataPacket);
    }

    public static void Write_Tx_Data_Promise(String packetType, final int packetNo, String txDataPacket, final String OnPass, final String OnFail){
        txDataPacket = buildDataPacketToTransmit(packetType,packetNo,txDataPacket);
        dataPacketTxQueue.offer(txDataPacket);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String castedHashcode = String.valueOf(packetNo);
                Log.d("NampuComm",rxDataHashcode + " Cast hashcode "+ castedHashcode);
                if(castedHashcode.equals(rxDataHashcode)){
                    Log.d("NampuComm","CommStatus Passed");
                    Actions.commStatus(OnPass);
                }else{
                    Log.d("NampuComm","CommStatus Failed");
                }
            }
        }, 500);
    }

    public static void On_Data_Packet_Received(String data,int hashcode){
        Actions.dataFromHMD(data);
        Log.d("NampuCommFROMHMD",String.valueOf(hashcode));
        Write_Tx_Data("COMM_STATUS",0,String.valueOf(hashcode));
    }

    public static void On_HeartBeat_Received(){


    }

}