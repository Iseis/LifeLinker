package com.apps.andrew.lifelinker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class BlueToothDevicesDialog extends DialogFragment {
    private BroadcastReceiver broadcastReceiver;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> devices;
    private final int REQUEST_ENABLE_BT = 1;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<String> pairedDevices;
    private ArrayList<BluetoothDevice> connectedDevices;
    private BluetoothDevice sendDevice;


    public static BlueToothDevicesDialog newInstance() {

        BlueToothDevicesDialog fragment = new BlueToothDevicesDialog();
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        pairedDevices = new ArrayList<>();
        connectedDevices = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if(bluetoothAdapter == null) {
            System.out.println("Hello////////////////////////////");
            Context context = getActivity().getApplicationContext();
            CharSequence sequence = "Bluetooth not supported by this Device.";
            int length = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, sequence, length);
            toast.show();
            getActivity().finish();
            return builder.create();
        }

        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {

            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.startDiscovery();
        }

        devices = bluetoothAdapter.getBondedDevices();
        if(devices.size() > 0){
            for(BluetoothDevice device : devices){
                pairedDevices.add(device.getName());
            }
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    String s = "";
                    int devicesCount = pairedDevices.size();
                    if(devicesCount > 0){
                        for(int i = 0; i < devicesCount; i++){
                            if(device.getName().equals(pairedDevices.get(i))){
                                s = "(Paired)";
                                break;
                            }
                        }
                    }

                    connectedDevices.add(device);

                    mArrayAdapter.add(device.getName() + " " + s);
                } else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    if(bluetoothAdapter.getState() == bluetoothAdapter.STATE_OFF){
                        //turn on bluetooth
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                    else if(bluetoothAdapter.getState() == bluetoothAdapter.STATE_TURNING_OFF){
                        getActivity().unregisterReceiver(broadcastReceiver);
                        Toast.makeText(getActivity(), "Bluetooth Required closing.", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(broadcastReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(broadcastReceiver, filter);
        builder.setTitle("Pick a Device");
        builder.setAdapter(mArrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(bluetoothAdapter.isDiscovering())
                    bluetoothAdapter.cancelDiscovery();

                sendDevice = connectedDevices.get(which);

                if(sendDevice.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    sendDevice.createBond();
                }

                sendResult(Activity.RESULT_OK);
            }
        });

        return builder.create();
    }

    private void sendResult(int resultCode){
        if(getTargetFragment()== null)
            return;

        Intent i = new Intent();
        i.putExtra("Device", sendDevice);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result for returning from turning on bluetooth
        if (requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == getActivity().RESULT_OK){

                bluetoothAdapter.cancelDiscovery();
                bluetoothAdapter.startDiscovery();
            } else {
                Toast.makeText(getActivity(), "Can't Continue without Bluetooth.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isDiscovering())
                bluetoothAdapter.cancelDiscovery();

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isDiscovering())
                bluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setTargetFragment(null, -1);
    }
}
