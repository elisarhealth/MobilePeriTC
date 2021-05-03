package com.agyohora.mobileperitc.filetransfer;

import android.content.Context;
import android.util.Log;

import com.agyohora.mobileperitc.utils.CommonUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Invent on 3-1-18.
 *
 * @see java.lang.Runnable
 * Class to transfer the HMD app updates
 */

public class FileServer implements Runnable {

    private static final int SERVER_PORT = 50001;
    Context mContext;

    public FileServer(Context context) {
        this.mContext = context;
    }

    @Override
    public void run() {

        String SERVER_IP = CommonUtils.getHmdStaticIpFromSerialId();
        if (SERVER_IP != null) {
            try {
                Log.d("FileSever", " IP assigned --->" + SERVER_IP);
                Log.d("FileClient", "Connecting...");
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                File myFile = CommonUtils.checkForUpdateFile(mContext);
                if (socket != null) {
                    if (socket.isConnected()) {
                        byte[] mybytearray = new byte[8192];
                        FileInputStream fis = new FileInputStream(myFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        DataInputStream dis = new DataInputStream(bis);
                        OutputStream os;

                        os = socket.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(os);
                        dos.writeUTF(myFile.getName());
                        //dos.writeLong(mybytearray.length);
                        int read;
                        while ((read = dis.read(mybytearray)) != -1) {
                            dos.write(mybytearray, 0, read);
                        }

                        dis.close();
                        os.close();
                        dos.close();
                    }
                }
                Log.d("FileClient", "Closed");
            } catch (Exception e) {
                Log.e("FileClient", "Error" + e.getMessage());
            }
        } else {
            Log.d("FileClient", "Server IP Null");
        }
    }

}
