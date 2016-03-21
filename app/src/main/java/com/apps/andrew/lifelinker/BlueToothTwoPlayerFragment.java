package com.apps.andrew.lifelinker;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BlueToothTwoPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlueToothTwoPlayerFragment extends Fragment {
    private final String TAG = ".BlueToothTwoPlayerFragment";
    private Player p1;
    private Player p2;
    private BluetoothDevice device;
    private Button p1Plus;
    private Button p1Minus;
    private TextView p1Life;
    private TextView p2Life;
    private TextView p1Name;
    private TextView p2Name;
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final String NAME = "lifelinker";
    private int mState;
    private BluetoothAdapter adapter;
   // private ConnectThread mConnectThread = null;
   // private AcceptThread acceptThread = null;
   // private ConnectedThread mConnectedThread = null;
    private BluetoothSocket finalSocket;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_RECEIVED:
                    Log.d(TAG, "Message Recieved");
                    byte[] writeBuf = (byte[]) msg.obj;
                    String change = new String(writeBuf);
                    String integer = change.substring(0, 1);
                    int code = Integer.parseInt(integer);
                    setPlayerInfo(code);


                    break;
            }
        }
    };
    BlueToothServices services = new BlueToothServices(handler);

    public static final  int MESSAGE_RECEIVED = 0;
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device


    public BlueToothTwoPlayerFragment(){
        adapter = BluetoothAdapter.getDefaultAdapter();
        p1 = new Player();
        p2 = new Player();
    }

    //Used to set the players life when we receive a request to
    private void setPlayerLife(Player p, Boolean plus) {
        if(plus)
            p.setLife(p.getLife() +1);
        else
            p.setLife(p.getLife() - 1);
    }

    public static BlueToothTwoPlayerFragment newInstance() {
        BlueToothTwoPlayerFragment fragment = new BlueToothTwoPlayerFragment();

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        //if(services == null)
          // services = new BlueToothServices(handler);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //services = new BlueToothServices(handler);
    }

    @Override
    public void onResume() {
        super.onResume();
        //if(services == null)
            //services = new BlueToothServices(handler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_blue_tooth_two_player, container, false);

        p1Life = (TextView) v.findViewById(R.id.p1_life);
        p2Life = (TextView) v.findViewById(R.id.p2_life);
        p1Name = (TextView) v.findViewById(R.id.p1_name);
        p2Name = (TextView) v.findViewById(R.id.p2_name);

        p1Minus = (Button) v.findViewById(R.id.p1minus_button);
        p1Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayerLife(p1, false);
                p1Life.setText(Integer.toString(p1.getLife()));
            }
        });

        p1Plus = (Button) v.findViewById(R.id.p1plus_button);
        p1Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayerLife(p1, true);
                p1Life.setText(Integer.toString(p1.getLife()));
                byte[] buff = new byte[64];
                buff = "0 ".getBytes();
                //Log.d(TAG, services == null ? "services is null" : "services not null");
                services.write(buff);
            }
        });


       // services.start();

        //Start the accept thread
        services.start();
        return v;
    }
    

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode == BluToothTwoPlayerActivity.GET_DEVICE){
            device =  data.getParcelableExtra("Device");
            System.out.println(device.getName());
            //if(services != null)
                //Log.d(TAG, "MAC:" + device.getAddress());
            services.connect(device);
        }
    }

    private void setPlayerInfo(int code){
        switch (code){
            //if code is 0 then set the player life up 1
            case 0:
                setPlayerLife(p2, true);
                break;
        }
    }

   /* private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
    }

    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
       /* if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }*/

        //setState(STATE_LISTEN);


       /* if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
        Log.d(TAG, "Starting accept thread.");
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);


        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }


    private class AcceptThread extends Thread {
        // The local server socket
        private BluetoothServerSocket mmServerSocket = null;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = adapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                Log.d(TAG, "mmServerSocket intialized");

            } catch (IOException e) {
                Log.e(TAG, "Socket Type: listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "BEGIN mAcceptThread" + this);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    Log.d(TAG, "Looking for device");
                    socket = mmServerSocket.accept();
                    Log.d(TAG, "found device");
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BlueToothTwoPlayerFragment.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                Log.d(TAG, "CONNECTING//////////");
                                // Situation normal. Start the connected thread.
                               // connected(socket);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread");

        }

        public void cancel() {
            Log.d(TAG, "Socket Type cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type Sclose() of server failed", e);
            }
        }
    }

    //Used to connect to device
    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket =null;
        private final BluetoothDevice mmDevice;
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                Log.d(TAG, "Hello from connect thread atempt to connect");
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {Log.d(TAG, "Connection failed"); }
            finalSocket = tmp;
        }

        public void run() {

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                finalSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    finalSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            Log.d(TAG, finalSocket.isConnected() ? "finalSocket is Connected" : "finalSocket is'nt connected");
            // Do work to manage the connection (in a separate thread)
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            Log.d(TAG, "ConnectedThread constructor");
            // Get the input and output streams, using temp objects because

            // member streams are final
            try {
                tmpIn = finalSocket.getInputStream();
                tmpOut = finalSocket.getOutputStream();
            } catch (IOException e) { }

            Log.d(TAG, finalSocket.isConnected() ? "mmSocket is Connected" : "mmSocket not Connected");
            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            byte[] buffer = new byte[1024];  // buffer store for the stream
            Log.d(TAG, "Hello from the connected");
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    handler.obtainMessage(MESSAGE_RECEIVED, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "From Connected!!!!" + e);
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /** Will cancel an in-progress connection, and close the socket
        public void cancel() {
            try {
                finalSocket.close();
            } catch (IOException e) { }
        }
    }

    public synchronized void connected(BluetoothSocket socket) {
        Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        //Cancel the accept thread we only want to connect to one device
        if(acceptThread != null){
            acceptThread.cancel();
            acceptThread = null;
        }

        Log.d(TAG, socket.isConnected() ? "connected() socket connected" : "connected() socket not");
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        setState(STATE_CONNECTED);
    }

    //Used to communicate connected device
    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            Log.d(TAG, "ConnectedThread constructor");
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) { }

            Log.d(TAG, mmSocket.isConnected() ? "mmSocket is Connected" : "mmSocket not Connected");
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            Log.d(TAG, "Hello from the connected");
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    handler.obtainMessage(MESSAGE_RECEIVED, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "From Connected!!!!" + e);
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

    }


    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see #write(byte[])

    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        Log.d(TAG, mConnectedThread == null ? "mConnected null" : "mConnected not null");
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        if(r != null)
            r.write(out);
    }*/
}
