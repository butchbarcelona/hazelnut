package com.proj.hazelnut;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.proj.hazelnut.ble.BlunoLibrary;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRestart1, btnRestart2, btnShutdown1, btnShutdown2;
        btnRestart1 = (Button) findViewById(R.id.btn_restart1);
        btnRestart2 = (Button) findViewById(R.id.btn_restart2);
        btnShutdown1 = (Button) findViewById(R.id.btn_shutdown1);
        btnShutdown2 = (Button) findViewById(R.id.btn_shutdown2);
        btnRestart1.setOnClickListener(this);
        btnRestart2.setOnClickListener(this);
        btnShutdown1.setOnClickListener(this);
        btnShutdown2.setOnClickListener(this);

    }

    protected void onResume() {
        super.onResume();                                                       //onResume Process by BlunoLibrary
    }

    @Override
    protected void onPause() {
        super.onPause();                                                    //onPause Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();                                                       //onDestroy Process by BlunoLibrary
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_restart1:
                sendData("1");
                break;
            case R.id.btn_restart2:
                sendData("3");
                break;
            case R.id.btn_shutdown1:
                sendData("2");
                break;
            case R.id.btn_shutdown2:
                sendData("4");
                break;
        }

    }


    private String TAG = "error";

    private Switch btSwitch;
    private static Switch btBagSwitch;
    private BluetoothAdapter btAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView lv;

    private static TextView tvConnectionStatus;

    public static boolean isRunning = false;

    //bluetooth variables
    private OutputStream outStream = null;
    private BluetoothSocket btSocket = null;
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        on();
        isRunning = true;
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        off();
        isRunning = false;
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
            throws IOException {

        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod(
                        "createInsecureRfcommSocketToServiceRecord",
                        new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    public void connectToBTDevice(BluetoothDevice btDevice) {

        BluetoothDevice device = btDevice;

        try {
            btSocket = createBluetoothSocket(device);

            btAdapter.cancelDiscovery();
            Log.d(TAG, "connecting");
            try {
                btSocket.connect();
                Log.d(TAG, "socket created");

                try {
                    outStream = btSocket.getOutputStream();
                    onBTConnect();
                    Toast.makeText(getBaseContext(), "Bluetooth Connected!",Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.d(TAG, "Socket failed to connect");
                }

            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    Log.d(TAG, "Socket failed to connect");
                }

            }

        } catch (IOException e1) {
            Log.d(TAG, "Socket failed to connect");
        }


    }

    private void closeSocket(){
        try {
            if(btSocket != null && btSocket.isConnected()){
                btSocket.close();
                onBTDisconnect();
            }
        } catch (IOException e2) {
            Log.d(TAG, "Socket failed to close");
        }
    }

    public static void onBTConnect() {
        tvConnectionStatus.setText("Connected!");
        btBagSwitch.setChecked(true);
    }

    public static void onBTDisconnect() {

        tvConnectionStatus.setText("Disconnected!");
        btBagSwitch.setChecked(false);
    }

    public void on() {

        if (!btAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void list(View view) {
        pairedDevices = btAdapter.getBondedDevices();

        ArrayList<String> list = new ArrayList<String>();
        for (BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());

        Toast.makeText(getApplicationContext(), "Showing Paired Devices",
                Toast.LENGTH_SHORT).show();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);
/*        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos,long id) {
                BluetoothDevice btFound = null;
                int counter = 0;
                for (Iterator<BluetoothDevice> it = pairedDevices.iterator(); it.hasNext();) {
                    BluetoothDevice f = it.next();
                    if (counter == pos) {
                        btFound = f;
                        break;
                    }
                    counter++;
                }

                if (btFound != null)
                    connectToBTDevice(btFound);
            }
        });*/

        new AlertDialog.Builder(this)
                .setTitle("BLE Device Scan...").setAdapter(adapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothDevice btFound = null;
                        int counter = 0;
                        for (Iterator<BluetoothDevice> it = pairedDevices.iterator(); it.hasNext();) {
                            BluetoothDevice f = it.next();
                            if (counter == which) {
                                btFound = f;
                                break;
                            }
                            counter++;
                        }

                        if (btFound != null)
                            connectToBTDevice(btFound);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface arg0) {
                    System.out.println("mBluetoothAdapter.stopLeScan");
                    off();
                    }
                }).create();

    }

    public void off() {
        btAdapter.disable();
        Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_LONG)
                .show();
    }

    public void visible(View view) {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);

        btSwitch.setChecked(true);

    }
    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
        Log.d(TAG, "...Send data: " + message + "...");
        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
        }
    }


}
