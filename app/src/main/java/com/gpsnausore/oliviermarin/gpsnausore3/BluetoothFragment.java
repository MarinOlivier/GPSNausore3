package com.gpsnausore.oliviermarin.gpsnausore3;

//www.  java 2 s. co m
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import java.util.Set;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//package info.androidhive.tabsswipe;

//import info.androidhive.tabsswipe.R;
//import android.os.Bundle;
import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;

public class BluetoothFragment extends Fragment {

    private Activity activity;
    private View view;

    private static final int REQUEST_ENABLE_BT = 1;
    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView pairedDevListView;
    private ArrayAdapter<String> pairedBTArrayAdapter;
    private ListView avDevListView;
    private ArrayAdapter<String> avBTArrayAdapter;

    private Button enableBT;

    public static BluetoothFragment newInstance() {
        BluetoothFragment fragment = new BluetoothFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        initScanBtn();
        getDevicesList();
        BluetoothIsOn(view);
        listPaired(view);
        find(view);

        return view;
    }

    private void getDevicesList(){
        // take an instance of BluetoothAdapter - Bluetooth radio
        myBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter==null) {
            text.setText("Status: not supported");

            Toast.makeText(activity, "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        }
        else {
            pairedDevListView = (ListView) view.findViewById(R.id.list_paired_devices);
            avDevListView = (ListView) view.findViewById(R.id.list_av_devices);

            // create the arrayAdapter that contains the BTDevices, and set it to the ListView
            pairedBTArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1);
            pairedDevListView.setAdapter(pairedBTArrayAdapter);

            avBTArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1);
            avDevListView.setAdapter(avBTArrayAdapter);
        }
    }

    private void BluetoothIsOn(View view){
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(activity,"Bluetooth turned on" ,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(activity,"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_ENABLE_BT){
            if(myBluetoothAdapter.isEnabled()) {
                Toast.makeText(activity,"Status: Enabled",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity,"Status: Disabled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void listPaired(View view){
        // get paired devices
        pairedDevices = myBluetoothAdapter.getBondedDevices();

        // put it's one to the adapter
        for(BluetoothDevice device : pairedDevices)
            pairedBTArrayAdapter.add(device.getName()+ "\n" + device.getAddress());

        Toast.makeText(activity,"Show Paired Devices",
                Toast.LENGTH_SHORT).show();

    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                avBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                avBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void initScanBtn() {
        Button button = view.findViewById(R.id.btn_scan_blt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                find(view);
            }
        });
    }

    public void find(View view) {
        if (myBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        }
        else {
            avBTArrayAdapter.clear();
            myBluetoothAdapter.startDiscovery();

            activity.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    public void off(View view){
        myBluetoothAdapter.disable();
        text.setText("Status: Disconnected");

        Toast.makeText(activity,"Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(bReceiver);
    }
}