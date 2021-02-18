package com.example.microcontrollerfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Controll extends AppCompatActivity {
    Button set,refresh;//set degree of sg90 from 1 to 6
    RadioGroup radioGroup;
    RadioButton handMode,btMode,lightMode;
    String address = null;
    NumberPicker mNumberPicker ;//choose value from  1 to 6
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    InputStream inStream = null;
    private boolean isBtConnected = false;
    private final int MESSAGE_RECEIVE = 1;
    ConnectedThread dataThread;
    int from_Pic = 0;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//bt UUID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controll);

        Intent intent = getIntent();
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);



        refresh = findViewById(R.id.refresh);
        handMode = findViewById(R.id.handMode);
        btMode = findViewById(R.id.btMode);
        lightMode = findViewById(R.id.lightMode);
        radioGroup = findViewById(R.id.radioGroup);
        set = findViewById(R.id.set);
        mNumberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(6);
        radioGroup.check(R.id.handMode);
        new Controll.ConnectBT().execute();
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal(Integer.toString(mNumberPicker.getValue()-1));
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignal("s");
            }
        });
        /*btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Disconnect();
            }
        });*/

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(from_Pic == 1){
                    return;
                }
                if (checkedId == R.id.handMode) {
                    //msg("handmode");
                    mNumberPicker.setEnabled(false);
                    set.setVisibility(View.INVISIBLE);
                    sendSignal("h");
                }
                if (checkedId == R.id.btMode) {
                    //msg("btmode");
                    mNumberPicker.setEnabled(true);
                    set.setVisibility(View.VISIBLE);
                    sendSignal("b");
                }
                if (checkedId == R.id.lightMode) {
                    //msg("lightmode");
                    mNumberPicker.setEnabled(false);
                    set.setVisibility(View.INVISIBLE);
                    sendSignal("l");
                }
            }
        });
    }
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch(msg.what) {
                case MESSAGE_RECEIVE:
                    //mNumberPicker.setMinValue(1);
                    //mNumberPicker.setMaxValue(6);
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff,0,msg.arg1);
                    if(tempMsg.charAt(0) == 'H'){
                        mNumberPicker.setEnabled(false);
                        set.setVisibility(View.INVISIBLE);
                        from_Pic = 1;
                        ((RadioButton)findViewById(R.id.handMode)).setChecked(true);
                        from_Pic = 0;
                        sendSignal("v");

                    }
                    else if(tempMsg.charAt(0) == 'B'){
                        mNumberPicker.setEnabled(true);
                        set.setVisibility(View.VISIBLE);
                        from_Pic = 1;
                        ((RadioButton)findViewById(R.id.btMode)).setChecked(true);
                        from_Pic = 0;
                        sendSignal("v");

                    }
                    else if(tempMsg.charAt(0) == 'L'){
                        mNumberPicker.setEnabled(false);
                        set.setVisibility(View.INVISIBLE);
                        from_Pic = 1;
                        ((RadioButton)findViewById(R.id.lightMode)).setChecked(true);
                        from_Pic = 0;
                        sendSignal("v");
                    }
                    else{
                        int val= tempMsg.charAt(0)+1;
                        if(val>=49 && val < 55) {
                            mNumberPicker.setValue(val - 48);
                        }
                    }
                    break;
            }
            return true;
        }
    });
    private void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
               // String mode="mode";
                //mode = mode.concat(number.toString());
                String mode = number;
                btSocket.getOutputStream().write(mode.getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }






    private class ConnectedThread extends Thread {

        public ConnectedThread(BluetoothSocket btSocket) {
            InputStream tmpIn = null;
            if (btSocket != null) {
                try {
                    tmpIn = btSocket.getInputStream();
                } catch (IOException e) {
                    msg("Error");
                }
            }
            inStream = tmpIn;
        }

        public void run() {

            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                byte[] mmBuffer = new byte[1024];
                try {
                    // Read from the InputStream.
                    numBytes = inStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    mHandler.obtainMessage(
                            MESSAGE_RECEIVE, numBytes, -1,
                            mmBuffer).sendToTarget();

                } catch (IOException e) {
                    msg("Input stream was disconnected");
                    break;
                }
            }
        }
    }

    private void Disconnect () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(Controll.this, "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                    sendSignal("s");
                }
            } catch (IOException e) {
                ConnectSuccess = false;
                System.out.println(e);
                System.out.println(address);
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
                if(btSocket != null) {
                    dataThread=new ConnectedThread(btSocket);
                    dataThread.start();
                }
            }

            progress.dismiss();
        }

    }
}