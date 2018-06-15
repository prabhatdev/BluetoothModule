package com.example.prabh.bluetoothmodule;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceControl extends AppCompatActivity {

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public Set<BluetoothDevice> pairedDevices;
    CardView close, open, disconnect;
    String macAddress = "00:21:13:00:1C:B0";
    ProgressDialog progress;
    BluetoothSocket bluetoothSocket = null;
    BluetoothAdapter bluetoothAdapter;
    boolean isConnected = false;
    String userId,registernumber;
    RelativeLayout relativeLayout;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);
        close = (CardView) findViewById(R.id.open);
        open = (CardView) findViewById(R.id.close);
        disconnect = (CardView) findViewById(R.id.disconnect);
        Intent i = getIntent();
        relativeLayout = (RelativeLayout) findViewById(R.id.device_control);
        sharedPreferences=getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        userId=sharedPreferences.getString("userId","not found");
        registernumber=sharedPreferences.getString("registernumber","not found");
        ConnectBT establishConnection = new ConnectBT();
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

    void openDoor() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.getOutputStream().write("qwertyuiop".getBytes());
                submitLog("Door Closed");
            } catch (IOException e) {
                snackbarMessage("Error Occurred");
            }
        }
    }

    void closeDoor() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.getOutputStream().write("poiuytrewq".getBytes());
                submitLog("Door Opened");
            } catch (IOException e) {
                snackbarMessage("Error Occurred");
            }
        }
    }

    void disconnect() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
                submitLog("Disconnected");
            } catch (IOException e) {
                snackbarMessage("Error Occurred");
            }
            Intent i = new Intent(DeviceControl.this, ConnectPage.class);
            startActivity(i);
        }

    }

    void submitLog(String operation) {
        String dateTime = getDateTime();
        Call<String> call = APIservice.getService().insertlog(userId, registernumber, operation, dateTime);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    void snackbarMessage(String s) {
        Snackbar snackbar = Snackbar.make(relativeLayout, s, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean connectionStatus1 = true;
        private boolean connectionStatus2 = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(DeviceControl.this, "Connecting...", "Please Wait");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!connectionStatus1) {
                snackbarMessage("Unable to Connect. Please be near WisOpt Office");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(DeviceControl.this, ConnectPage.class);
                        startActivity(i);
                    }
                }, 2500);
            } else if (!connectionStatus2) {
                snackbarMessage("Device not paired. Please pair it first");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(DeviceControl.this, ConnectPage.class);
                        startActivity(i);
                    }
                }, 2000);

            } else {
                snackbarMessage("Connected");
                isConnected = true;
            }
            progress.dismiss();

        }


        @Override
        protected Void doInBackground(Void... voids) {
            boolean deviceFound = false;
            try {
                if (bluetoothSocket == null || !isConnected) {

                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    pairedDevices = bluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice bt : pairedDevices) {
                            if (bt.getAddress().equals(macAddress)) {
                                deviceFound = true;
                            }
                        }
                    }
                    if (deviceFound) {
                        BluetoothDevice myDevice = bluetoothAdapter.getRemoteDevice(macAddress);
                        bluetoothSocket = myDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        bluetoothSocket.connect();
                    } else {
                        connectionStatus2 = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                snackbarMessage("Error Occurred");
                connectionStatus1 = false;
            }

            return null;
        }

    }
}