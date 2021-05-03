package com.agyohora.mobileperitc.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.agyohora.mobileperitc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Invent on 16-1-18.
 */

public class CustomWirelessManagementFragment extends Fragment {
    private ListView listView;
    private static WifiManager wifiManager;
    private static WifiReceiver wifiReceiver;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi_management_layout, container, false);
        listView = view.findViewById(R.id.wifiDeviceList);
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        if (!wifiManager.isWifiEnabled()) {
            if (wifiManager.setWifiEnabled(true)) {
                Toast.makeText(getActivity(), "WIFI enabled.", Toast.LENGTH_SHORT).show();
                getActivity().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifiManager.startScan();
            } else {
                Toast.makeText(getActivity(), "Error in WiFi Initialization!!", Toast.LENGTH_SHORT).show();
            }
        } else if (wifiManager.isWifiEnabled()) {
            getActivity().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
        }
        this.adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(this.adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getActivity(), "Selected: " + listView.getItemAtPosition(position), Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(wifiReceiver);
        } catch (Exception e) {
            Log.e("Exception","onPreExecute of custom wirelesss managemen "+e.getMessage());
        }
    }

    class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Toast.makeText(getActivity(), "SCAN_RESULTS_AVAILABLE", Toast.LENGTH_SHORT).show();
                List<ScanResult> wifiList = wifiManager.getScanResults();
                Toast.makeText(getActivity(), "WiFi List: " + wifiList, Toast.LENGTH_SHORT).show();
                if (wifiList.size() > 0) {
                    int TotalWifiDevices = wifiList.size();
                    Toast.makeText(getActivity(), "Number of Wifi connections : " + TotalWifiDevices, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "No Wifi found.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
