package com.example.prabh.bluetoothmodule;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.UUID;

public class DeviceControl extends AppCompatActivity {

    CardView close, open, disconnect;
    String macAddress = "";
    ProgressDialog progress;
    BluetoothSocket bluetoothSocket = null;
    BluetoothAdapter bluetoothAdapter;
    boolean isConnected = false;
    RelativeLayout relativeLayout;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);
        close = (CardView) findViewById(R.id.open);
        open = (CardView) findViewById(R.id.close);
        disconnect = (CardView) findViewById(R.id.disconnect);
        Intent i = getIntent();
        relativeLayout=(RelativeLayout)findViewById(R.id.device_control);
        macAddress ="00:21:13:00:1C:B0";

        ConnectBT establishConnection=new ConnectBT();
        establishConnection.execute();
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDoor();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDoor();
            }
        });
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

    }
    void openDoor()
    {
        if(bluetoothSocket!=null)
        {
            try {
                bluetoothSocket.getOutputStream().write("ON".toString().getBytes());
            }
            catch (IOException e)
            {
                snackbarMessage("Error Occurred");
            }
        }
    }
    void closeDoor()
    {
        if(bluetoothSocket!=null)
        {
            try {
                bluetoothSocket.getOutputStream().write("OFF".toString().getBytes());
            }
            catch (IOException e)
            {
                snackbarMessage("Error Occurred");
            }
        }
    }
    void disconnect()
    {
        if(bluetoothSocket!=null)
        {
            try {
                bluetoothSocket.close();
            }
            catch (IOException e)
            {
                snackbarMessage("Error Occurred");
            }
            finish();
        }

    }
    void snackbarMessage(String s)
    {
        Snackbar snackbar=Snackbar.make(relativeLayout,s,Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean connectionStatus = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(DeviceControl.this, "Connecting...","Please Wait");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!connectionStatus) {
                snackbarMessage("Unable to Connect. Please be near WisOpt Office");
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },2500);
            }
            else
            {
                snackbarMessage("Connected");
            isConnected=true;
            }
            progress.dismiss();

        }


        @Override
        protected Void doInBackground(Void... voids) {
            try{
                if(bluetoothSocket==null||!isConnected)
                {
                    bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice myDevice=bluetoothAdapter.getRemoteDevice(macAddress);
                    bluetoothSocket=myDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                snackbarMessage("Error Occurred");
                connectionStatus=false;
            }

            return null;
        }

    }
}