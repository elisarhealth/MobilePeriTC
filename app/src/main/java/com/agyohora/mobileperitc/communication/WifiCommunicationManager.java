package com.agyohora.mobileperitc.communication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.store.Store;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.android.dx.stock.ProxyBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Raviteja Chivukula on 11-02-2018.
 * Comments here:
 * This class defines the WifiCommunicationManager that implements WifiCommunicationStateMachine
 * along with other related methods.
 * There is no need to edit this class for any communication functionality
 * <p>
 * State Diagram of the code : https://drive.google.com/open?id=1nTG58Cw1ErYhtd0kww7aNN5sVPW06Hft
 * <p>
 * Overview:
 * <p>
 * - On the Tx side, the state machine accepts messages from the top level code and takes care of
 * packetizing the message into multiple packets
 * <p>
 * - On the Rx side, the state machine received the packets, checks for basic sanity, and stitches
 * the packets into the message, identifies which MessageChannel the Message belings to, pushes
 * it to the appropriate MessageChannel, and implements the appropriate MessageReceiptCallBack method
 * <p>
 * - The user can define multiple message channels. The default message channels are:
 * - "DATA", "VID1", "VID2", "FILE, depending on the type of the messages
 * - Note that the MessageChannelIds have to be 4 letter words only
 * <p>
 * - The class also implements inbuilt heartbeat to check the status of the connection
 * <p>
 * - The class implements auto-reconnect in case the connection fails
 * <p>
 * <p>
 * Step-by-step guide to use the class:
 * - Define the MessageChannelArray with the desired channels (Use only 4 letter names for channels)
 * - Define the MessageReceiptCallback method (Single method, but switch-case for implementing
 * multiple cases, one corresponding to each MessageChannel
 * - Instantiate the WifiCommunicationManager class with the appropriate arguments (including
 * MessagechannelArray and MessageReceiptCallback)
 * - Run the WifiCommunicationManager on a dedicated thread
 * - Run the WifiCommunicationManager.initializeCommunication() method
 * - This is a blocking method that releases control only after successful initialization (or) error
 * (whichever occurs first)
 * - Pump in the data in the form of byte[] using the transmitMessage() method. You need to give
 * the following inputs:
 * - MessageChannelId (the MessageChannel over which you want to transmit the Message)
 * - messageData (the byte array that you want to transmit)
 * ** FYI, in this iplementation, it is OK to invoke the transmitMessage method concurrently, i.e.,
 * you can invoke different transmitMessage methods in, say two different AsyncTasks simultaneously
 * <p>
 * You should receive the byte array that you transmitted, on the corresponding messageChannel on
 * the received side!!
 */


public class WifiCommunicationManager {

    private static String TAG = "WifiCommTC";

    //PKT_START and PKT_END are the markers for the packet. They delimit each packet from the neighbouring ones
    private static String PKT_START = "##PKT_ST##"; //This string is fixed as this is...
    private static String PKT_END = "##PKT_EN#\n"; //This string is fixed as this is...

    // All the entries other than PKT_START and PKT_END are encapsulated as a JSON Object as shown below:
    // Names of the elements present in the JSON Objects.
    private static String PKT_DATA_LENGTH = "PKT_DATA_LENGTH";
    private static String MSG_CHANNEL_ID = "MSG_CHANNEL_ID";
    private static String MSG_SRC = "MSG_SRC";
    private static String MSG_DST = "MSG_DST";
    private static String MSG_NO = "MSG_NO";
    private static String NO_OF_PKTS_IN_MSG = "NO_OF_PKTS_IN_MSG";
    private static String PKT_NO = "PKT_NO";
    private static String PKT_DATA = "PKT_DATA";

    // Example values of the elements in the JSON Objects
    //private static String PKT_DATA_LENGTH_EXAMPLE_VALUE = "1234"; //The PKT_DATA_LENGTH represented as a 4 digit decimal string
    //private static String MSG_CHANNEL_ID_EXAMPLE_VALUE = "DATA"; //Other possible entries are "HTBT", "FILE", "VIDEO1", "VIDEO2"
    //private static String MSG_SRC_EXAMPLE_VALUE = "ZXCWSDE232"; //HMD's Serial ID here
    //private static String MSG_DST_EXAMPLE_VALUE = "TC01"; //Static for now
    //private static String MSG_NO_EXAMPLE_VALUE = "0001"; //The MSG_NO represented as a 4 digit decimal string
    //private static String NO_OF_PKTS_IN_MSG_EXAMPLE_VALUES = "0001"; //The NO_OF_PKTS_IN_MSG represented as a 4 digit decimal string
    //private static String PKT_NO_EXAMPLE_VALUE = "0001"; //The PKT_NO represented as a 4 digit decimal string
    //private static String PKT_DATA_EXAMPLE_VALUE = "Base64 encoded string here"; //Data encoded as a Base64 string here


    private static int SERVERSOCKET_PORT = 50000; //Hardcoded for now. I don't see any need to change this though.

    private static int WIFI_ENABLE_DISABLE_MAX_WAIT_TIME_MILLIS = 5000; //Max wait time to let Wifi Radio complete ENABLING / DISABLING
    private static int IP_ASSIGNMENT_WAIT_TIMEOUT_MILLIS = 8000; //Time to wait for IP Address to be assigned
    private static int SERVER_SOCKET_TIMEOUT_MILLIS = 1000; //Wait time in milli-second for ServerSocket Timeout
    private static int SOCKET_TIMEOUT_MILLIS = 5; //Socket Timeout / in some cases the Loop time
    private static int TRANSMIT_BYTES_STATE_TIMEOUT_MILLIS = 5; //The maximum time to stay in TRANSMIT_BYTES state
    private static int TX_RX_LOOP_TIME_MILLIS = 100; //Loop time to wait in the MANAGE_HEARTBEAT_AND_WAIT State
    private static int HOTSPOT_ENABLE_WAIT_MILLIS = 5000; //Time to wait for hotspot to enable
    private static int COMM_CHANNEL_INIT_MAX_WAIT_TIME_MILLIS = 7000; // Max time to wait for Comm Channel to be initialized

    private static int MAX_ALLOWED_HEARTBEAT_MISSES = 2000; //If more heartbeats are missed, communication will be reset
    private static int MAX_ALLOWED_IO_ERRORS = 20; //It might usually be useless to keep MAX_ALLOWED_IO_ERRORS to be very different from MAX_ALLOWED_HEARTBEAT_MISSES

    private static int PKT_DATA_MAX_SIZE = 4000; //This is used to chunkify large messages into packets of this size.


    // Common variables shared between states
    private static String deviceSerialId = "DUMMY";
    private ServerSocket serverSocket_incomingConn = null;
    private Socket hmdTcSocket = null;
    private InputStream rxByteStream = null;
    private OutputStream txByteStream = null;
    private String rxByteStringRemaining = ""; //The Byte String that is yet to be processed
    private String nextPacket_fromRxBytes = ""; //Extracted Packet that needs to be processed further
    private JSONObject rxPacketChecked_json = null;
    private String rxHtbtMessage_prev;
    private int missedHeartBeatNo = 0;
    private int noOfIoErrors = 0;
    private boolean startComm = false;
    private boolean stopComm = false;
    public static boolean commInitialized = false;
    private static ConnectivityManager mConnectivityManager;
    private WifiManager wifiManager;

    //The list below has to be static, as it will be shared between multiple threads.
    private static List<String> txPacketBuffer = Collections.synchronizedList(new ArrayList<String>(100));
    //TODO: How to make txPacketBuffer resistant to race conditions?

    static class MessageChannel {
        String messageChannelId = "";
        int messageNo = 0;
        String message_stringBase64 = "";

        MessageChannel(String messageChannelName) {
            messageChannelId = messageChannelName;
        }
    }

    private MessageChannel txMessageChannelArray[];
    private MessageChannel rxMessageChannelArray[];
    private Context moduleMainContext;
    private MessageReceiptCallback messageReceiptCallback;

    //Constructor for the WifiCommunicationManager
    WifiCommunicationManager(Context moduleMainContext_arg, MessageChannel[] txMessageChannelArray_arg, MessageChannel[] rxMessageChannelArray_arg, MessageReceiptCallback messageReceiptCallback_arg) {
        txMessageChannelArray = txMessageChannelArray_arg;
        rxMessageChannelArray = rxMessageChannelArray_arg;
        moduleMainContext = moduleMainContext_arg;
        messageReceiptCallback = messageReceiptCallback_arg;
    }

    //StateResult vector definition
    private enum StateResult {
        START_CMD_RECEIVED,
        START_CMD_NOT_RECEIVED,
        WIFI_DISABLE_SUCCESSFUL,
        WIFI_DISABLE_FAILED,
        HOTSPOT_WIFI_HOSTING_SUCCESSFUL,
        HOTSPOT_WIFI_HOSTING_FAILED,
        SOCKET_CONNECT_SUCCESSFUL,
        SOCKET_CONNECT_TIMED_OUT,
        SOCKET_CONNECT_ERROR,
        SOCKET_CONFIGURATION_ERROR,
        RECEIVED_BYTES_NON_ZERO,
        NEXT_PACKET_COMPLETE,
        PACKET_VALID,
        PROCESS_PACKET_SUCCESSFUL,
        PROCESS_PACKET_UNKNOWN_ERROR,
        NEXT_PACKET_INCOMPLETE,
        PACKET_INVALID,
        BYTES_PROCESSING_FINISHED,
        RECEIVED_BYTES_ZERO,
        RECEIVE_BYTES_FAILED,
        TRANSMIT_BYTES_COMPLETE,
        TRANSMIT_BYTES_INCOMPLETE,
        TRANSMIT_BYTES_FAILED,
        HEARTBEAT_OK,
        HEARTBEAT_PAUSED,
        HEARTBEAT_FAILED,
        ERROR_TRANSIENT,
        ERROR_CRITICAL,
        STOP_CMD_RECEIVED,
        STOP_CMD_NOT_RECEIVED,
        CLOSE_CONNECTION_SUCCESSFUL,


    }

    //State vector definition
    private enum State {
        CHECK_FOR_START_CMD,
        DISABLE_WIFI,
        HOST_HOTSPOT_WIFI,
        ATTEMPT_SOCKET_CONNECT,
        RECEIVE_BYTES,
        EXTRACT_NEXT_PACKET_FROM_RX_BYTES,
        CHECK_PACKET_VALIDITY,
        PROCESS_PACKET,
        TRANSMIT_BYTES,
        CHECK_AND_SEND_HEARTBEAT,
        MANAGE_ERROR,
        CHECK_FOR_STOP_CMD,
        CLOSE_CONNECTION,
    }

    boolean initializeCommunication() {

        startComm = true;


        long startTime = SystemClock.elapsedRealtime();
        while (!commInitialized) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.d(TAG, "startStateMachine: Insomnia!!!");
            }

            if (SystemClock.elapsedRealtime() > (startTime + COMM_CHANNEL_INIT_MAX_WAIT_TIME_MILLIS)) {
                return false;
            }

        }

        return true;

    }

    //State methods
    private StateResult checkForStartCommand() {

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Log.d(TAG, "checkForStartCommand: Insomnia!!!");
        }

        if (startComm) {
            startComm = false; //Unset this flag for next use
            return StateResult.START_CMD_RECEIVED;
        } else {
            return StateResult.START_CMD_NOT_RECEIVED;
        }

    }

    @SuppressLint("WrongConstant")
    private StateResult disableWifiRadio(WifiManager wifiManager) {

        boolean wifiDisableSuccessful = false;

        wifiManager.setWifiEnabled(false);
        Log.d(TAG, "WiFi Radio State : " + wifiManager.getWifiState());

        // The Wifi Radio takes some time to get disabled. Wait for this time.
        // Usually this operation takes less than 10 milli-second
        long startTime = SystemClock.elapsedRealtime();
        long elapsedTime = SystemClock.elapsedRealtime() - startTime;
        while (elapsedTime < WIFI_ENABLE_DISABLE_MAX_WAIT_TIME_MILLIS) {

            if ((wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED)) {
                //WiFi is disabled
                Log.d(TAG, "WiFi Radio State : " + wifiManager.getWifiState());
                wifiDisableSuccessful = true;
                break;
            }

            //Wait for 10 milli-second
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.d(TAG, "Insomnia...!!");
            }

            elapsedTime = SystemClock.elapsedRealtime() - startTime;

        }

        if (wifiDisableSuccessful) {
            return StateResult.WIFI_DISABLE_SUCCESSFUL;
        } else {
            return StateResult.WIFI_DISABLE_FAILED;
        }

    }

    private StateResult hostHotspotWifi(Context context, WifiManager wifiManager, String hotspotWifiSsid, String hotspotWifiPassword) {

        //Build Wifi hotspot configuration
        hotspotWifiSsid =  deviceSerialId; // Double quotation marks are not needed for the SSID while setting the hotspot's SSID
        //hotspotWifiPassword = "\"" + "agyohora" + "\"";
        hotspotWifiPassword = "agyohora";//TODO: Don't know whether double quotation marks are needed here
        WifiConfiguration hotspotConfiguration = new WifiConfiguration();
        hotspotConfiguration.SSID = hotspotWifiSsid;
        hotspotConfiguration.preSharedKey = hotspotWifiPassword;
        hotspotConfiguration.allowedKeyManagement.set(4);


        int currentVersion = Build.VERSION.SDK_INT;
        Log.d(TAG, "hostHotspotWifi: Android Version = " + currentVersion);

        if (currentVersion <= 23) {
            //Android API version corresponds to lesser than Nougat
            //So programmatic hotspot turning on will work


            if (!isHotspotOn(context)) {
                try {

                    Log.d(TAG, "hostHotspotWifi: Trying to switch on hotspot as it is not on yet");

                    Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                    method.invoke(wifiManager, hotspotConfiguration, true);

                    long startTime = SystemClock.elapsedRealtime();
                    while (SystemClock.elapsedRealtime() < startTime + HOTSPOT_ENABLE_WAIT_MILLIS) {
                        if (isHotspotOn(context)) {
                            return StateResult.HOTSPOT_WIFI_HOSTING_SUCCESSFUL;
                        } else {
                            Thread.sleep(100);
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "hostHotspotWifi: Error occurred in invoking method : " + e.getMessage());
                }
            } else {
                //Hotspot is already on
                return StateResult.HOTSPOT_WIFI_HOSTING_SUCCESSFUL;
            }

        } else {
            //Android API version corresponds to Nougat and above
            //So programmatic hotspot turning will not work.

            //TODO: Intent to open the settings window here and wait till the hotspot is switched on

            return StateResult.HOTSPOT_WIFI_HOSTING_SUCCESSFUL;

        }

        return StateResult.HOTSPOT_WIFI_HOSTING_FAILED;

    }

    public static boolean isHotspotOn(Context context) {
        // This is a helper method that checks whether wifi hotspot is on or off

        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        } catch (Exception ex) {
            Log.d("WifiCommTC", "Error happened in isHotspotOn: " + ex.getMessage());
            return false;
        }
    }

    private String ipAddressIntToString(int ipAddress_int) {
        //Helper method for broadcastHmdIp()
        //byte[] ipAddress_byteArray = ((ByteBuffer.allocate(4)).putInt(HMD_IP)).array();
        byte[] ipAddress_byteArray = new byte[]{(byte) (ipAddress_int), (byte) (ipAddress_int >>> 8), (byte) (ipAddress_int >>> 16), (byte) (ipAddress_int >>> 24)};

        StringBuilder ipAddress_string = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            byte subnet_byte = ipAddress_byteArray[i];
            int subnet_int = (int) subnet_byte;
            if (subnet_int < 0) {
                subnet_int = subnet_int + 256;
            }
            ipAddress_string.append(String.valueOf(subnet_int));

            if (i != 3) {
                ipAddress_string.append(".");//Skip dot for the last subnet_string
            }
        }

        return ipAddress_string.toString();
    }

    private StateResult attemptSocketConnectAndConfigure(String hmdIp, int serversocketPort) {

        txPacketBuffer.clear();
        //txPacketBuffer.removeAll(txPacketBuffer);

        //If hmdTcSocket is already available, close it first and then proceed
        if (hmdTcSocket != null) {
            if (!hmdTcSocket.isClosed()) {
                try {
                    Log.d(TAG, "Valid hmdTcSocket is already present. Closing it anyways");
                    hmdTcSocket.close();
                } catch (IOException e) {
                    Log.d(TAG, "Error occurred in attempting to close hmdTcSocket : " + e.getMessage());
                }
            }
        }

        //If streams already exist, close them
        if (txByteStream != null) {
            try {
                txByteStream.flush();
                txByteStream.close();
            } catch (IOException e) {
                Log.d(TAG, "Error closing an already existing txByteStream");
            }
        }
        if (rxByteStream != null) {
            try {
                rxByteStream.close();
            } catch (IOException e) {
                Log.d(TAG, "Error closing an already existing rxByteStream");
            }
        }


        //Now that the socket is closed, try to connect to the serversocket on the HMD
        //InetSocketAddress hmdSocketAddress = new InetSocketAddress(HMD_IP,SERVERSOCKET_PORT);

        try {
            hmdTcSocket = new Socket(hmdIp, serversocketPort);
            //hmdTcSocket.connect(hmdSocketAddress,SOCKET_CONNECT_TIMEOUT_MILLIS);
        } catch (SocketTimeoutException e) {
            return StateResult.SOCKET_CONNECT_TIMED_OUT;
        } catch (IOException e) {
            Log.d(TAG, "attemptSocketConnect: IO Error occurred : " + e.getMessage()); //Problem
            return StateResult.SOCKET_CONNECT_ERROR;
        }

        //If HMD and TC are connected, create IOStreams, make hmdTcConnected = true
        if (hmdTcSocket != null) {
            if (hmdTcSocket.isConnected()) {
                try {

                    //Set socket timeout
                    hmdTcSocket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);

                    //Get the IO Streams
                    try {
                        rxByteStream = hmdTcSocket.getInputStream();
                        txByteStream = hmdTcSocket.getOutputStream();
                    } catch (IOException | NullPointerException e) {
                        Log.d(TAG, " Error in - Creating IO Streams (or) Setting Socket Timeout");
                        return StateResult.SOCKET_CONFIGURATION_ERROR;
                    }

                } catch (IOException e) {
                    Log.d(TAG, "Error in closing serverSocket after use : " + e.getMessage());
                    return StateResult.SOCKET_CONFIGURATION_ERROR;
                }
            }
        }

        commInitialized = true;
        return StateResult.SOCKET_CONNECT_SUCCESSFUL;

    }

    public static String getHmdStaticIpFromSerialId(String hmdSerialId) {
       /* Log.d("WifiCommStateMachine", " HMDSerialId " + hmdSerialId);
        String lastThreeDigits = hmdSerialId.substring(hmdSerialId.length() - 3);
        int lastSubnet_int = ((Integer.parseInt(lastThreeDigits) % 100) + 99);
        String lastSubnet_string = Integer.toString(lastSubnet_int);
         // Check this post for details: https://android.stackexchange.com/questions/46499/how-configure-the-dhcp-settings-of-wifi-tetheringhotspot-in-android
        return "192.168.43." + lastSubnet_string;*/

        return CommonUtils.getDetailsFromSerialNumber(hmdSerialId,"StaticIP");
    }

    private StateResult receiveBytes() {

        if (hmdTcSocket.isConnected() && (rxByteStream != null)) {

            try {
                byte[] rxBytesBuffer = new byte[100000];
                int noOfBytesRead = rxByteStream.read(rxBytesBuffer);
                //Log.d(TAG, "receiveBytes: noOfBytesRead = " + noOfBytesRead);
                if (noOfBytesRead > 0) {
                    byte[] rxBytes = new byte[noOfBytesRead];
                    System.arraycopy(rxBytesBuffer, 0, rxBytes, 0, rxBytes.length);
                    String rxByteString = new String(rxBytes);
                    // Log.d("rxByteString"," "+rxByteString);
                    rxByteStringRemaining = rxByteStringRemaining + rxByteString;

                    return StateResult.RECEIVED_BYTES_NON_ZERO;

                } else {
                    return StateResult.RECEIVED_BYTES_ZERO;
                }

            } catch (SocketTimeoutException t) {
                Log.d(TAG, "Socket timed out while trying to read bytes");
                return StateResult.RECEIVED_BYTES_ZERO; //If socket times out, it returns an exception. So handle it by appropriate StateResult
            } catch (IOException e) {
                Log.d(TAG, "IO Error occurred in receiveDataBytes()");
                return StateResult.RECEIVE_BYTES_FAILED;
            }


        } else {
            return StateResult.RECEIVE_BYTES_FAILED;
        }
    }

    private StateResult extractNextPacketFromReceivedBytes() {

        int firstPKT_START = rxByteStringRemaining.indexOf(PKT_START);
        int firstPKT_END = rxByteStringRemaining.indexOf(PKT_END);

        if ((firstPKT_START != -1) && (firstPKT_END != -1)) {

            if ((firstPKT_END > firstPKT_START)) {
                // A complete packet has been found. Extract it into rxDataNextPacket. Remove the PKT_START & PKT_END markers
                nextPacket_fromRxBytes = rxByteStringRemaining.substring(firstPKT_START + PKT_START.length(), firstPKT_END);
                // Trim the received packet from the previous rxByteStringRemaining
                rxByteStringRemaining = rxByteStringRemaining.substring(firstPKT_END + PKT_END.length());
                // Log.d(TAG,"STATE_RESULT: NEXT_PACKET_COMPLETE");
                return StateResult.NEXT_PACKET_COMPLETE;
            } else {
                /*Basically rxByteStringRemaining could have been something like below:
                ����������!=9Q�Q%5�2:00:00.019}C8##PKT_EN#
                ##PKT_ST##00560001PHONDATA{TIME12:00:00.019}C8##PKT_EN#
                ##PKT_ST##00560001PHONDATA{TIME12:00:00.019}C8##PKT_EN#  */
                // Basically, there is a valid PKT_START and valid PKT_END, but PKT_START is not less than PKT_END
                // In this case, we have to delete the portion prior to the firstPKT_START
                rxByteStringRemaining = rxByteStringRemaining.substring(firstPKT_START);
                return StateResult.NEXT_PACKET_INCOMPLETE;
            }


        } else if (firstPKT_START != -1) {
            // A PKT_START is found but PKT_END is not found. The data would look something like this...
            // ##PKT_ST##00560001PHONDA
            rxByteStringRemaining = rxByteStringRemaining.substring(firstPKT_START); //Retain the part after PKT_START. Discard the part before.
            return StateResult.BYTES_PROCESSING_FINISHED;
        } else {
            // Neither PKT_START nor PKT_END have been found. Just move on
            return StateResult.BYTES_PROCESSING_FINISHED;
        }

    }

    private StateResult checkPacketValidity() {

        String packetToCheck_string = nextPacket_fromRxBytes;
        //At this stage, the packet should ideally be a single packet. The checks below are various sanity checks for the packet

        //Defining the flags:
        boolean jsonChecked, PKT_DATA_LENGTH_Checked = false;

        //Checking for JSON Object Typecasting
        JSONObject packetToCheck_json = null;
        try {
            packetToCheck_json = new JSONObject(packetToCheck_string);
            jsonChecked = true;
        } catch (JSONException e) {
            jsonChecked = false;
            Log.d(TAG, "checkPacketValidity: JSON Error: " + e.getMessage());
        }

        //Checking for PKT_DATA_LENGTH
        if (packetToCheck_json != null) {
            try {
                int packetLength = Integer.parseInt(packetToCheck_json.getString(PKT_DATA_LENGTH));
                PKT_DATA_LENGTH_Checked = (packetToCheck_json.get(PKT_DATA).toString().length() == packetLength);
            } catch (JSONException e) {
                Log.d(TAG, "checkPacketValidity: JSON Error: " + e.getMessage());
                jsonChecked = false;
            } catch (NumberFormatException e) {
                Log.d(TAG, "Number format error");
            }
        }


        if (jsonChecked && PKT_DATA_LENGTH_Checked) {
            //Packet valid
            rxPacketChecked_json = packetToCheck_json;
            //Log.d(TAG, "RX PACKET: " + rxPacketChecked_json);
            return StateResult.PACKET_VALID;
        } else {
            rxPacketChecked_json = null;
            return StateResult.PACKET_INVALID;
        }

    }

    private StateResult processPacket(MessageReceiptCallback messageReceiptCallback) {

        try {
            String messageChannelId = (String) rxPacketChecked_json.get(MSG_CHANNEL_ID);
            int noOfPktsInMsg = Integer.parseInt(rxPacketChecked_json.get(NO_OF_PKTS_IN_MSG).toString());
            int pktNo = Integer.parseInt(rxPacketChecked_json.get(PKT_NO).toString());
            int messageChannelNo = getChannelNoFromChannelId(messageChannelId, rxMessageChannelArray);

            if (pktNo == 0) {
                // Reset the message text for further use
                rxMessageChannelArray[messageChannelNo].message_stringBase64 = "";
            }

            if (pktNo < (noOfPktsInMsg - 1)) {

                //Message is not complete yet, so append PKT_DATA (unlikely for HTBT Message)
                rxMessageChannelArray[messageChannelNo].message_stringBase64 += rxPacketChecked_json.get(PKT_DATA);
                return StateResult.PROCESS_PACKET_SUCCESSFUL;

            } else if (pktNo == (noOfPktsInMsg - 1)) {

                rxMessageChannelArray[messageChannelNo].message_stringBase64 += rxPacketChecked_json.get(PKT_DATA); //Append last packet

                /*try {
                    Log.d("Packet received ",""+new String(Base64.decode(rxMessageChannelArray[messageChannelNo].message_stringBase64, Base64.DEFAULT),"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.e("UnsupportedEncoding"," "+e.getMessage());
                }*/

                boolean base64StringCorrupted = true;
                try {
                    Base64.decode(rxMessageChannelArray[messageChannelNo].message_stringBase64, Base64.DEFAULT);
                    base64StringCorrupted = false;
                } catch (IllegalArgumentException e) {
                    base64StringCorrupted = true;
                    Log.d(TAG, "processPacket: Base64 String Corrupted");
                    //Base64 corruption is an occasional error. Ignore it.
                }

                if (!base64StringCorrupted) {
                    messageReceiptCallback.onMessageReceived(messageChannelId, rxMessageChannelArray[messageChannelNo].message_stringBase64);
                    return StateResult.PROCESS_PACKET_SUCCESSFUL;
                } else {
                    return StateResult.PROCESS_PACKET_UNKNOWN_ERROR;
                }

            } else {

                // (pktNo > noOfPktsInMsg-1), i.e. Some error happened
                return StateResult.PROCESS_PACKET_UNKNOWN_ERROR;

            }
        } catch (JSONException e) {

            Log.d(TAG, "processHtbtPacket: JSON Error: " + e.getMessage());
            return StateResult.PROCESS_PACKET_UNKNOWN_ERROR;

        }

    }

    interface MessageReceiptCallback {
        //Interface that outlines the callback method to be executed when a complete message is received
        void onMessageReceived(String messageChannelId, String message_StringBase64);
    }

    //Query this function for HMD status
    public static boolean isConnected() {
        if (commInitialized) {
            MyApplication.getInstance().set_HMD_CONNECTED(true);
            return true;
        } else
            MyApplication.getInstance().set_HMD_CONNECTED(false);
        return false;
    }


    boolean transmitMessage(String messageChannelId, byte[] messageBytes) {

        int messageChannelNo = getChannelNoFromChannelId(messageChannelId, txMessageChannelArray);

        txMessageChannelArray[messageChannelNo].message_stringBase64 = Base64.encodeToString(messageBytes, Base64.DEFAULT);

        //Getting the arguments for constructAndQueuePackets() for greater readability
        int messageNo = txMessageChannelArray[messageChannelNo].messageNo;
        String messageStringToTransmit = txMessageChannelArray[messageChannelNo].message_stringBase64;
        //Log.e(TAG,"txMessage is running and passed on to Construct and Queue");
        boolean txMessageQueued = constructAndQueuePackets(messageChannelId, messageNo, messageStringToTransmit);

        txMessageChannelArray[messageChannelNo].messageNo++; //Increment the respective messageNo.

        return txMessageQueued;


    }

    private int getChannelNoFromChannelId(String messageChannelId, MessageChannel[] messageChannelArray) {

        for (int i = 0; i < messageChannelArray.length; i++) {
            if (messageChannelId.equals(messageChannelArray[i].messageChannelId)) {
                return i;
            }
        }

        return -1;
    }

    private boolean constructAndQueuePackets(String messageChannelId, int messageNo, String messageStringToTransmit) {

        JSONObject txPacket_json = new JSONObject();
        boolean txMessageQueued = true;

        String messageChunk; //The part of the full string that will be transmitted in one packet

        //Compute NO_OF_PKTS_IN_MSG
        int noOfPacketsInMessage = messageStringToTransmit.length() / PKT_DATA_MAX_SIZE; //Rounds towards zero inherently
        //Log.e("noOfPacketsInMessage"," "+noOfPacketsInMessage);
        if (messageStringToTransmit.length() % PKT_DATA_MAX_SIZE != 0) {
            noOfPacketsInMessage++; //Add 1 corresponding to the last remaining chunk
        }

        for (int packetNo = 0; packetNo < noOfPacketsInMessage; packetNo++) {
            //Log.e("PocketNo"," "+packetNo);
            if ((packetNo + 1) * PKT_DATA_MAX_SIZE > messageStringToTransmit.length()) {
                //This is the last packet that is smaller than PKT_DATA_MAX_SIZE
                messageChunk = messageStringToTransmit.substring(packetNo * PKT_DATA_MAX_SIZE);
            } else {
                messageChunk = messageStringToTransmit.substring(packetNo * PKT_DATA_MAX_SIZE, (packetNo + 1) * PKT_DATA_MAX_SIZE);
            }
            try {
                txPacket_json.put(PKT_DATA_LENGTH, Integer.toString(messageChunk.length()));
                txPacket_json.put(MSG_CHANNEL_ID, messageChannelId);
                txPacket_json.put(MSG_SRC, "HMD-" + deviceSerialId);
                txPacket_json.put(MSG_DST, "TC01"); //Generic TC-ID for now
                txPacket_json.put(MSG_NO, Integer.toString(messageNo));
                txPacket_json.put(NO_OF_PKTS_IN_MSG, Integer.toString(noOfPacketsInMessage));
                txPacket_json.put(PKT_NO, Integer.toString(packetNo));
                txPacket_json.put(PKT_DATA, messageChunk);
            } catch (JSONException e) {
                Log.e(TAG, "JSON Error occurred while building packets to transmit");
                txMessageQueued = false;
                break;
            }

            //Append the PKT_START and PKT_END markers
            String txPacket_string = PKT_START + txPacket_json.toString() + PKT_END;


            synchronized (txPacketBuffer) {
                txPacketBuffer.add(txPacket_string);
                //Log.e(TAG, "TxPacketBuffer Size = " + txPacketBuffer.size());
            }

        }

        return txMessageQueued;

    }

    private StateResult transmitBytes() {

        long stateStartTimeMillis = SystemClock.elapsedRealtime();

        String packetStringToTransmit;

        synchronized (txPacketBuffer) {

            //Log.e(TAG, "Tx Packet Buffer Size at transmitBytes() = " + txPacketBuffer.size());


            while (txPacketBuffer.size() > 0) {
                packetStringToTransmit = txPacketBuffer.remove(0); //Get the first element

                try {
                    txByteStream.write(packetStringToTransmit.getBytes());
                    //Log.e(TAG, "TRANSMITTED TX_PACKET : " + packetStringToTransmit);
                } catch (IOException e) {
                    //Log.e(TAG, "IO Error occurred in writing bytes to socket");
                    return StateResult.TRANSMIT_BYTES_FAILED;
                }

                if ((SystemClock.elapsedRealtime() - stateStartTimeMillis) > TRANSMIT_BYTES_STATE_TIMEOUT_MILLIS) {
                    return StateResult.TRANSMIT_BYTES_INCOMPLETE;
                }

            }

        }

        return StateResult.TRANSMIT_BYTES_COMPLETE;

    }

    private StateResult checkAndSendHeartbeat() {

        try {
            Thread.sleep(TX_RX_LOOP_TIME_MILLIS);
        } catch (InterruptedException e) {
            Log.d(TAG, "manageHeartbeatAndWait: Insomnia!!!");
        }

        //By definition, "HTBT" messages should always be on channel '0'. Otherwise this code will fail
        transmitMessage(txMessageChannelArray[0].messageChannelId, Integer.toString(txMessageChannelArray[0].messageNo).getBytes());

        String rxHtbtMessage_current = rxMessageChannelArray[0].message_stringBase64;
        //Log.e("rxHtbtMessage_current"," "+rxHtbtMessage_current);
        ///Log.e("rxHtbtMessage_prev"," "+rxHtbtMessage_prev);
        try {
            byte[] currentBytes = Base64.decode(rxHtbtMessage_current, Base64.DEFAULT);
            String currentMode = new String(currentBytes,"UTF-8");
            Store.hmdModeWithBattery = currentMode;
            Log.d("currentMode "," "+currentMode);
        } catch (Exception e) {
            Log.e(TAG, "processPacket: Base64 String Corrupted");
        }
        if (rxHtbtMessage_current.equals(rxHtbtMessage_prev)) {
            missedHeartBeatNo++;
            Log.d(TAG, "checkAndSendHeartbeat: Missed " + missedHeartBeatNo + " Heartbeats!!");
        } else {
            missedHeartBeatNo = 0; //Reset the count if a packet is received
        }

        rxHtbtMessage_prev = rxHtbtMessage_current;

        if (missedHeartBeatNo > MAX_ALLOWED_HEARTBEAT_MISSES) {
            return StateResult.HEARTBEAT_FAILED;
        } else if (missedHeartBeatNo > 0) {
            return StateResult.HEARTBEAT_PAUSED;
        } else {
            return StateResult.HEARTBEAT_OK;
        }

    }

    private StateResult manageError(StateResult previousStateResult) {

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Log.d(TAG, "manageError: Insomnia!!!");
        }

        if ((previousStateResult == StateResult.RECEIVE_BYTES_FAILED) || (previousStateResult == StateResult.TRANSMIT_BYTES_FAILED)) {
            noOfIoErrors++;
        } else {
            noOfIoErrors = 0;
        }

        if (hmdTcSocket != null) {
            if (hmdTcSocket.isConnected() && (noOfIoErrors <= MAX_ALLOWED_IO_ERRORS)) {
                return StateResult.ERROR_TRANSIENT;
            } else {
                return StateResult.ERROR_CRITICAL;
            }
        } else {
            return StateResult.ERROR_TRANSIENT;
        }

    }

    void stopCommunication() {
        stopComm = true;
    }

    private StateResult checkForStopCommand() {

        if (stopComm) {
            stopComm = false; //Make this false for the next use
            return StateResult.STOP_CMD_RECEIVED;
        } else {
            return StateResult.STOP_CMD_NOT_RECEIVED;
        }


    }

    private StateResult closeConnection() {
        //Log.e("closeConnection", "called");
        commInitialized = false;

        missedHeartBeatNo = 0;

        try {
            if (txByteStream != null) {
                txByteStream.flush();
                txByteStream.close();
            }

            if (hmdTcSocket != null) {
                hmdTcSocket.close();//TODO: Is this required?
            }

        } catch (IOException e) {
            Log.d(TAG, "closeConnection: Error happened in closing connection");
        }

        return StateResult.CLOSE_CONNECTION_SUCCESSFUL;

    }

    //StateMachine Engine
    void WifiCommunicationStateMachine() {
        Context context =  moduleMainContext.getApplicationContext();
        String serial = CommonUtils.isUserSetUpFinished(context) ? CommonUtils.getHotSpotId() : CommonUtils.getSavedDeviceId(context);
        deviceSerialId = CommonUtils.getDetailsFromSerialNumber(serial,"DeviceId");
        Log.e(TAG,"deviceSerialId "+deviceSerialId);

        String hotspotWifiSsid = "\"" + deviceSerialId + "\"";
        String hotspotWifiPassword = "\"" + "agyohora" + "\""; //TODO: This has to be pre-shared between the HMD and TC via config file?

        //Autogenerate HMD_IP from the serial no. TODO: Rethink the exact mode of doing this
        String HMD_IP = getHmdStaticIpFromSerialId(serial);
        Log.e(TAG, "WifiCommunicationStateMachine: HMD_IP Computed from Serial ID = " + HMD_IP);
        //Get a reference to the wifiManager. This is a common reference used by multiple methods
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        StateResult currentStateResult = StateResult.START_CMD_NOT_RECEIVED;
        StateResult previousStateResult = StateResult.ERROR_TRANSIENT;
        State nextState = State.CHECK_FOR_START_CMD, currentState, previousState = State.CHECK_FOR_START_CMD;

        Log.e("WifiCommStateMachine", "Called");
        Log.e("WifiCommStateMachine", "Need " + MyApplication.getInstance().is_HMD_CONNECTION_NEEDED());

        while (MyApplication.getInstance().is_HMD_CONNECTION_NEEDED()) {

            //Log.e(TAG, "NEXT_STATE : " + nextState);

            currentState = nextState;

            switch (currentState) {

                case CHECK_FOR_START_CMD: {

                    currentStateResult = checkForStartCommand();

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "WifiCommunicationStateMachine: Insomnia!!");
                    }

                    if (currentStateResult == StateResult.START_CMD_NOT_RECEIVED) {
                        nextState = State.CHECK_FOR_START_CMD;
                    } else if (currentStateResult == StateResult.START_CMD_RECEIVED) {
                        nextState = State.DISABLE_WIFI; //Wifi has to be disabled to host hotspotWifi
                    } else {
                        nextState = State.MANAGE_ERROR;
                    }
                    break;
                }

                case DISABLE_WIFI: {

                    currentStateResult = disableWifiRadio(wifiManager);

                    if (currentStateResult == StateResult.WIFI_DISABLE_SUCCESSFUL) {
                        nextState = State.HOST_HOTSPOT_WIFI;
                    } else {
                        nextState = State.MANAGE_ERROR;
                    }
                    break;
                }

                case HOST_HOTSPOT_WIFI: {

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        currentStateResult = hostHotspotWifiBelowOreo(moduleMainContext, wifiManager, hotspotWifiSsid, hotspotWifiPassword);
                    } else {
                        currentStateResult = hostHotspotWifiAboveOreo(moduleMainContext, wifiManager);
                    }
                    Log.e("currentStateResult", " " + currentStateResult);
                    if (currentStateResult == StateResult.HOTSPOT_WIFI_HOSTING_SUCCESSFUL) {
                        nextState = State.ATTEMPT_SOCKET_CONNECT;
                    } else if (currentStateResult == StateResult.HOTSPOT_WIFI_HOSTING_FAILED) {
                        nextState = State.HOST_HOTSPOT_WIFI;
                    } else {
                        nextState = State.MANAGE_ERROR;
                    }
                    break;
                }

                case ATTEMPT_SOCKET_CONNECT: {

                    currentStateResult = attemptSocketConnectAndConfigure(HMD_IP, SERVERSOCKET_PORT);

                    if (currentStateResult == StateResult.SOCKET_CONNECT_SUCCESSFUL) {
                        nextState = State.RECEIVE_BYTES;
                    } else if (currentStateResult == StateResult.SOCKET_CONNECT_TIMED_OUT) {
                        nextState = State.ATTEMPT_SOCKET_CONNECT;
                    } else {
                        nextState = State.MANAGE_ERROR;
                    }
                    break;
                }

                case RECEIVE_BYTES: {

                    currentStateResult = receiveBytes();

                    if (currentStateResult == StateResult.RECEIVED_BYTES_NON_ZERO) {
                        nextState = State.EXTRACT_NEXT_PACKET_FROM_RX_BYTES;
                    } else if (currentStateResult == StateResult.RECEIVED_BYTES_ZERO) {
                        nextState = State.TRANSMIT_BYTES;
                    } else {
                        nextState = State.MANAGE_ERROR;
                    }
                    break;
                }

                case EXTRACT_NEXT_PACKET_FROM_RX_BYTES: {

                    currentStateResult = extractNextPacketFromReceivedBytes();

                    if (currentStateResult == StateResult.NEXT_PACKET_COMPLETE) {
                        nextState = State.CHECK_PACKET_VALIDITY;
                    } else if (currentStateResult == StateResult.NEXT_PACKET_INCOMPLETE) {
                        nextState = State.EXTRACT_NEXT_PACKET_FROM_RX_BYTES;
                    } else if (currentStateResult == StateResult.BYTES_PROCESSING_FINISHED) {
                        nextState = State.TRANSMIT_BYTES;
                    }
                    break;
                }

                case CHECK_PACKET_VALIDITY: {

                    currentStateResult = checkPacketValidity();

                    if (currentStateResult == StateResult.PACKET_VALID) {
                        nextState = State.PROCESS_PACKET;
                    } else if (currentStateResult == StateResult.PACKET_INVALID) {
                        nextState = State.EXTRACT_NEXT_PACKET_FROM_RX_BYTES;
                    } else {
                        nextState = State.MANAGE_ERROR;
                    }
                    break;
                }

                case PROCESS_PACKET: {

                    currentStateResult = processPacket(messageReceiptCallback);

                    if (currentStateResult == StateResult.PROCESS_PACKET_SUCCESSFUL) {
                        nextState = State.EXTRACT_NEXT_PACKET_FROM_RX_BYTES;
                    } else if (currentStateResult == StateResult.PROCESS_PACKET_UNKNOWN_ERROR) {
                        nextState = State.EXTRACT_NEXT_PACKET_FROM_RX_BYTES;
                    } else {
                        nextState = State.MANAGE_ERROR;
                    }
                    break;
                }

                case TRANSMIT_BYTES: {

                    //Log.e(TAG, "Tx Packet Buffer Size On State Machine's TRANSMIT BYTES STATE = " + txPacketBuffer.size());

                    currentStateResult = transmitBytes();

                    if (currentStateResult == StateResult.TRANSMIT_BYTES_FAILED) {
                        nextState = State.MANAGE_ERROR;
                    } else if (currentStateResult == StateResult.TRANSMIT_BYTES_INCOMPLETE) {
                        nextState = State.CHECK_AND_SEND_HEARTBEAT;
                    } else if (currentStateResult == StateResult.TRANSMIT_BYTES_COMPLETE) {
                        nextState = State.CHECK_AND_SEND_HEARTBEAT;
                    } else {
                        nextState = State.MANAGE_ERROR; //Default
                    }
                    break;
                }

                case CHECK_AND_SEND_HEARTBEAT: {

                    currentStateResult = checkAndSendHeartbeat();

                    if (currentStateResult == StateResult.HEARTBEAT_OK || currentStateResult == StateResult.HEARTBEAT_PAUSED) {
                        nextState = State.CHECK_FOR_STOP_CMD;
                    } else if (currentStateResult == StateResult.HEARTBEAT_FAILED) {
                        nextState = State.CLOSE_CONNECTION;
                    } else {
                        nextState = State.MANAGE_ERROR;
                    }
                    break;
                }

                case CHECK_FOR_STOP_CMD: {

                    currentStateResult = checkForStopCommand();

                    if (currentStateResult == StateResult.STOP_CMD_RECEIVED) {
                        nextState = State.CLOSE_CONNECTION;
                    } else if (currentStateResult == StateResult.STOP_CMD_NOT_RECEIVED) {
                        nextState = State.RECEIVE_BYTES;
                    } else {
                        nextState = State.MANAGE_ERROR;
                    }
                    break;
                }

                case MANAGE_ERROR: {

                    currentStateResult = manageError(previousStateResult);

                    if (currentStateResult == StateResult.ERROR_TRANSIENT) {
                        nextState = previousState;
                    } else {
                        nextState = State.CLOSE_CONNECTION;
                    }

                    break;

                }

                case CLOSE_CONNECTION: {

                    currentStateResult = closeConnection();

                    if (currentStateResult == StateResult.CLOSE_CONNECTION_SUCCESSFUL) {
                        if (previousStateResult == StateResult.STOP_CMD_RECEIVED) {
                            //An explicit stop command has been given. So check for a start command again
                            nextState = State.CHECK_FOR_START_CMD;
                        } else if (previousStateResult == StateResult.HEARTBEAT_FAILED) {
                            //Heart beat has failed. So reinitialize the connection
                            nextState = State.DISABLE_WIFI;
                        } else {
                            nextState = State.DISABLE_WIFI;
                        }
                    } else {
                        //Restart the connection anyways...
                        nextState = State.CHECK_FOR_START_CMD;
                    }
                    break;
                }

            }

            //Log.e(TAG, "STATE_RESULT: " + currentStateResult);

            previousState = currentState;
            previousStateResult = currentStateResult;

        }

    }

    private StateResult hostHotspotWifiBelowOreo(Context context, WifiManager wifiManager, String hotspotWifiSsid, String hotspotWifiPassword) {

        //Build Wifi hotspot configuration
        hotspotWifiSsid = deviceSerialId; // Double quotation marks are not needed for the SSID while setting the hotspot's SSID
        //hotspotWifiPassword = "\"" + "agyohora" + "\"";
        hotspotWifiPassword = "agyohora";//TODO: Don't know whether double quotation marks are needed here
        WifiConfiguration hotspotConfiguration = new WifiConfiguration();
        hotspotConfiguration.SSID = hotspotWifiSsid;
        hotspotConfiguration.preSharedKey = hotspotWifiPassword;
        hotspotConfiguration.allowedKeyManagement.set(4);


        int currentVersion = Build.VERSION.SDK_INT;
        Log.d(TAG, "hostHotspotWifi: Android Version = " + currentVersion);

        if (currentVersion <= 23) {
            //Android API version corresponds to lesser than Nougat
            //So programmatic hotspot turning on will work


            if (!isHotspotOn(context)) {
                try {

                    Log.d(TAG, "hostHotspotWifi: Trying to switch on hotspot as it is not on yet");

                    Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                    method.invoke(wifiManager, hotspotConfiguration, true);

                    long startTime = SystemClock.elapsedRealtime();
                    while (SystemClock.elapsedRealtime() < startTime + HOTSPOT_ENABLE_WAIT_MILLIS) {
                        if (isHotspotOn(context)) {
                            return StateResult.HOTSPOT_WIFI_HOSTING_SUCCESSFUL;
                        } else {
                            sleep(100);
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "hostHotspotWifi: Error occurred in invoking method : " + e.getMessage());
                }
            } else {
                //Hotspot is already on
                return StateResult.HOTSPOT_WIFI_HOSTING_SUCCESSFUL;
            }

        } else {
            //Android API version corresponds to Nougat and above
            //So programmatic hotspot turning will not work.

            //TODO: Intent to open the settings window here and wait till the hotspot is switched on

            return StateResult.HOTSPOT_WIFI_HOSTING_SUCCESSFUL;

        }

        return StateResult.HOTSPOT_WIFI_HOSTING_FAILED;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private StateResult hostHotspotWifiAboveOreo(Context context, WifiManager mWifiManager) {
        /*WifiConfiguration configuration = getCustomConfigs(mWifiManager, deviceSerialId, "agyohora");
        try {
            Method setConfigMethod =
                    wifiManager.getClass()
                            .getMethod("setWifiApConfiguration", WifiConfiguration.class);
            boolean status = (boolean) setConfigMethod.invoke(wifiManager, configuration);
            Log.e("TrackThis", "setWifiApConfiguration status " + status);
        } catch (Exception e) {
            Log.e("TrackThis", "setWifiApConfiguration " + e.getMessage());
        }*/

        MyOnStartTetheringCallback callback = new MyOnStartTetheringCallback() {
            @Override
            public void onTetheringStarted() {
                Log.e("hostHotspotWifi", " onTetheringStarted");
            }

            @Override
            public void onTetheringFailed() {
                Log.e("hostHotspotWifi", " onTetheringFailed");
            }
        };
        boolean flag = startTethering(callback, context);
        Log.e("TrackThis", "Flag " + flag);
        return flag ? StateResult.HOTSPOT_WIFI_HOSTING_SUCCESSFUL : StateResult.HOTSPOT_WIFI_HOSTING_FAILED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean startTethering(final MyOnStartTetheringCallback callback, Context mContext) {
        mConnectivityManager = (ConnectivityManager) mContext.getApplicationContext().getSystemService(ConnectivityManager.class);
        if (mConnectivityManager == null) {
            Log.e("startTethering", "mConnectivityManager is null ");
        }
        // On Pie if we try to start tethering while it is already on, it will
        // be disabled. This is needed when startTethering() is called programmatically.
        if (isTetherActive()) {
            Log.e("startTethering", "Tether already active, returning");
            return true;
        }

        File outputDir = mContext.getCodeCacheDir();
        Object proxy;
        try {
            proxy = ProxyBuilder.forClass(OnStartTetheringCallbackClass())
                    .dexCache(outputDir).handler(new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            switch (method.getName()) {
                                case "onTetheringStarted":
                                    callback.onTetheringStarted();
                                    break;
                                case "onTetheringFailed":
                                    callback.onTetheringFailed();
                                    break;
                                default:
                                    ProxyBuilder.callSuper(proxy, method, args);
                            }
                            return null;
                        }

                    }).build();
        } catch (Exception e) {
            Log.e("startTethering", "Error in enableTethering ProxyBuilder " + e.getMessage());
            return false;
        }

        Method method = null;
        try {
            method = mConnectivityManager.getClass().getDeclaredMethod("startTethering", int.class, boolean.class, OnStartTetheringCallbackClass(), Handler.class);
            if (method == null) {
                Log.e("startTethering", "startTetheringMethod is null");
            } else {
                method.invoke(mConnectivityManager, ConnectivityManager.TYPE_MOBILE, false, proxy, null);
                Log.e("startTethering", "startTethering invoked");
            }
            return true;
        } catch (Exception e) {
            Log.e("startTethering", "Error in enableTethering " + e.getMessage());
        }
        return false;
    }

    public boolean isTetherActive() {
        try {
            Method method = mConnectivityManager.getClass().getDeclaredMethod("getTetheredIfaces");
            if (method == null) {
                Log.e("startTethering", "getTetheredIfaces is null");
            } else {
                String[] res = (String[]) method.invoke(mConnectivityManager, (Object) null);
                Log.e("startTethering", "getTetheredIfaces invoked");
                Log.e("startTethering", Arrays.toString(res));
                if (res.length > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e("startTethering", "Error in getTetheredIfaces " + e.getMessage());
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void stopTethering(Context mContext) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getApplicationContext().getSystemService(ConnectivityManager.class);
            Method method = mConnectivityManager.getClass().getDeclaredMethod("stopTethering", int.class);
            if (method == null) {
                Log.e(TAG, "stopTetheringMethod is null");
            } else {
                method.invoke(mConnectivityManager, ConnectivityManager.TYPE_MOBILE);
                Log.d(TAG, "stopTethering invoked");
            }
        } catch (Exception e) {
            Log.e(TAG, "stopTethering error: " + e.toString());
            e.printStackTrace();
        }
    }

    interface MyOnStartTetheringCallback {
        /**
         * Called when tethering has been successfully started.
         */
        public abstract void onTetheringStarted();

        /**
         * Called when starting tethering failed.
         */
        public abstract void onTetheringFailed();

    }

    private Class OnStartTetheringCallbackClass() {
        try {
            return Class.forName("android.net.ConnectivityManager$OnStartTetheringCallback");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "OnStartTetheringCallbackClass error: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    private static WifiConfiguration getCustomConfigs(WifiManager manager, String ssid, String pass) {
        WifiConfiguration wifiConfig = null;
        try {
            Method getConfigMethod = manager.getClass().getMethod("getWifiApConfiguration");
            wifiConfig = (WifiConfiguration) getConfigMethod.invoke(manager);
            if (!TextUtils.isEmpty(ssid))
                wifiConfig.SSID = ssid;
            if (!TextUtils.isEmpty(pass))
                wifiConfig.preSharedKey = pass;
            return wifiConfig;
        } catch (Exception e) {
            Log.e("TrackThis", "getCustomConfigs " + e.getMessage());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(out));
            String str = new String(out.toByteArray());
            Log.e("TrackThis", "getCustomConfigs " + str);
        }
        if (wifiConfig == null) {
            Log.e("TrackThis", "getCustomConfigs wifiConfig is null ");
            wifiConfig = new WifiConfiguration();
            if (!TextUtils.isEmpty(ssid))
                wifiConfig.SSID = ssid;
            if (!TextUtils.isEmpty(pass))
                wifiConfig.preSharedKey = pass;
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        }
        return wifiConfig;
    }




}
