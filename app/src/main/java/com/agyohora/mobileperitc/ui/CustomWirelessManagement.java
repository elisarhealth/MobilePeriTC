package com.agyohora.mobileperitc.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.agyohora.mobileperitc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Invent on 16-1-18.
 */

public class CustomWirelessManagement extends AppCompatActivity {
    private ListView listView;
    private static WifiManager wifiManager;
    private static WifiReceiver wifiReceiver;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_management_layout);
        listView = findViewById(R.id.wifiDeviceList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        Log.e("Wifi", " Manager " + wifiManager.isWifiEnabled());
        if (!wifiManager.isWifiEnabled()) {
            if (wifiManager.setWifiEnabled(true)) {
                Toast.makeText(this, "WIFI enabled.", Toast.LENGTH_SHORT).show();
                this.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifiManager.startScan();
            } else {
                Toast.makeText(this, "Error in WiFi Initialization!!", Toast.LENGTH_SHORT).show();
            }
        } else if (wifiManager.isWifiEnabled()) {
            this.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            Toast.makeText(this, "WIFI scan started.", Toast.LENGTH_SHORT).show();
            wifiManager.startScan();
        }
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(this.adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                connectToNetwork(listView.getItemAtPosition(position).toString());
                Toast.makeText(CustomWirelessManagement.this, "Selected: " + listView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiReceiver);
    }

    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (!arrayList.isEmpty()) arrayList.clear();
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Toast.makeText(CustomWirelessManagement.this, "SCAN_RESULTS_AVAILABLE", Toast.LENGTH_SHORT).show();
                List<ScanResult> wifiList = wifiManager.getScanResults();
                if (wifiList.size() > 0) {
                    int TotalWifiDevices = wifiList.size();
                    try {
                        while (TotalWifiDevices >= 0) {
                            TotalWifiDevices--;
                            arrayList.add(wifiList.get(TotalWifiDevices).SSID);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Log.w("WiFi Exception: ", e);
                    }
                } else {
                    Toast.makeText(CustomWirelessManagement.this, "No Wifi found.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void connectToNetwork(final String ssid) {
        final TextInputLayout passwordLayout = new TextInputLayout(this);
        passwordLayout.setPasswordVisibilityToggleEnabled(true);
        final TextInputEditText password = new TextInputEditText(this);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final EditText edittext = new EditText(this);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        try {
                            String key = edittext.getText().toString();
                            WifiConfiguration wifiConfig = new WifiConfiguration();
                            wifiConfig.SSID = String.format("\"%s\"", ssid);
                            wifiConfig.preSharedKey = String.format("\"%s\"", key);
                            Log.e("In ", "Positive Button");
                            Log.e("SSID", " " + ssid);
                            Log.e("Pass", " " + key);
                            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                            //remember id
                            int netId = wifiManager.addNetwork(wifiConfig);
                            Log.e("Network Id", " " + netId);
                            wifiManager.disconnect();
                            boolean stat = wifiManager.enableNetwork(netId, true);
                            Log.e("Connection", " status " + stat);
                            wifiManager.reconnect();
                        } catch (NullPointerException e) {
                            Log.e("Exception", " " + e.toString());
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(CustomWirelessManagement.this, R.style.myDialog));
        builder.setTitle(ssid).setPositiveButton("Connect", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .setView(edittext).show();
        Log.e("from CTN", " " + ssid);
    }
}
