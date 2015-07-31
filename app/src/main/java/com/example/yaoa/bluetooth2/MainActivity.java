package com.example.yaoa.bluetooth2;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.media.AudioManager;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import android.os.Handler;

import android.content.Intent;
import android.content.IntentFilter;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import android.os.Environment;


public class MainActivity extends Activity{

    public class Speaker implements TextToSpeech.OnInitListener {

        private TextToSpeech tts;

        private boolean ready = false;

        private boolean allowed = true;

        public Speaker(Context context){
            tts = new TextToSpeech(context, this);
        }

        public boolean isAllowed(){
            return allowed;
        }

        public void allow(boolean allowed){
            this.allowed = allowed;
        }

        @Override
        public void onInit(int status) {
            if(status == TextToSpeech.SUCCESS){
                // Change this to match your
                // locale
                tts.setLanguage(Locale.US);
                ready = true;
            }else{
                ready = false;
            }
        }

        public void speak(String text){

            // Speak only if the TTS is ready
            // and the user has allowed speech

            if(ready && allowed) {
//                HashMap<String, String> hash = new HashMap<String,String>();
//                hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
//                        String.valueOf(AudioManager.STREAM_NOTIFICATION));
                Bundle bundle = new Bundle();
                bundle.putString(TextToSpeech.Engine.KEY_PARAM_STREAM,
                        String.valueOf(AudioManager.STREAM_NOTIFICATION));
                speaker.allow(true);
                tts.speak(text, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }


    private String TAG = "MAINACTIVITY";

    private final int CHECK_CODE = 0x01;
    private static final int REQUEST_ENABLE_BT = 1;
    public Speaker speaker;
    private Button onBtn;
    private Button offBtn;
    private Button listBtn;
    private Button findBtn;
    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private BluetoothLeScanner scanner;
    private Set<BluetoothDevice> pairedDevices;
    private ScanSettings setting;
    private ArrayList<ScanFilter> filters;
    private ListView myListView;
    //private ArrayAdapter<String> BTArrayAdapter;
    private ArrayAdapter<BTItem> BTArrayAdapter;
    private ArrayList<BTItem> data; //this thing holds the bluetooth devices data
    private int rssi;
    private Handler mHandler;
    private ArrayAdapter<BTItem> BLE;
    private long timer;
    private File btFile;
    private String fOutputName = "btdata.txt";
    private FileWriter writer;
//    private String texttosay = "Hello";

    public void setRssi(int rssi){
        this.rssi = rssi;
    }
    public int getRssi(){
        return this.rssi;
    }
    public void setTimer(long timer) {
        this.timer = timer;
    }

    public long getTimer(){
        return timer;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiating it
        data = new ArrayList<BTItem>();
        speaker = new Speaker(this);

        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "Bluetooth_Data");
            if (!root.exists()) {
                root.mkdirs();
            }
            btFile = new File(root, fOutputName);
            writer = new FileWriter(btFile);
//            writer.write("Helloworld");
//            writer.write("Dis is a testing noob");
//            writer.flush();
//            Toast.makeText(this, "writer ready", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.i(TAG, "IOException...");
        }

        // take an instance of BluetoothAdapter - Bluetooth radio
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        scanner = myBluetoothAdapter.getBluetoothLeScanner();
//        setting = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        if(myBluetoothAdapter == null) {
            onBtn.setEnabled(false);
            offBtn.setEnabled(false);
            listBtn.setEnabled(false);
            findBtn.setEnabled(false);
            text.setText("Status: not supported");

            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {



            text = (TextView) findViewById(R.id.text);
            onBtn = (Button)findViewById(R.id.turnOn);
            onBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    on(v);
                }
            });

            offBtn = (Button)findViewById(R.id.turnOff);
            offBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    off(v);
                }
            });

            listBtn = (Button)findViewById(R.id.paired);
            listBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    list(v);
                }
            });

            findBtn = (Button)findViewById(R.id.search);
            findBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    findBtn.setEnabled(false);
//                    speaker.speak("hello");
                    setTimer(System.currentTimeMillis());
                    // TODO Auto-generated method stub
                    registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                    //find(v);
                    //the following mess aka handler and thread enables the list to refresh itself every 10 second.
                    mHandler = new Handler();
                    new Thread(new Runnable(){
                        @Override
                        public void run(){
                            while (true){
                                try{
                                    //Thread.sleep();
                                    Thread.sleep(400);
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            find(null);

                                        }
                                    });

                                } catch(Exception e){
                                    Log.e("Thread Error", e.toString());
                                }
                            }
                        }
                    }).start();
                }
            });


            myListView = (ListView)findViewById(R.id.listView1);

            // create the arrayAdapter that contains the BTDevices, and set it to the ListView
            //BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);

            BTArrayAdapter = new CustomArrayAdapter(getBaseContext());


            //instead of creating an ArrayAdapter with no ArrayList on it, we created our own ArrayList and pass it to the Adapter so that
            //we can modify the ArrayList
//            BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.bt_device_list_item);
            myListView.setAdapter(BTArrayAdapter);




        }
    }

    public void on(View view){
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                    Toast.LENGTH_LONG).show();

            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
                Log.i(TAG, "no LE support for Bluetooth");
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            if(myBluetoothAdapter.isEnabled()) {
                text.setText("Status: Enabled");
            } else {
                text.setText("Status: Disabled");
            }
        }
        else if(requestCode == CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                speaker = new Speaker(this);
            }else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }

    public void list(View view){
        // get paired devices
        pairedDevices = myBluetoothAdapter.getBondedDevices();

        // put it's one to the adapter

        //for(BluetoothDevice device : pairedDevices) {
        //TODO
        // need to find a way to check if the device.getName() is on the data ArrayList<String>
        // if it is, then don't add it
        //data.add(device.getName() + "\n" + device.getAddress());
        //data.add(new BTItem(device.getName(), device.getAddress(), ""));


        //}

        Toast.makeText(getApplicationContext(),"Show Paired Devices",
                Toast.LENGTH_SHORT).show();

    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i("MainActivity", "BroadcastReceiver");
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.i("MainActivity", "Action_found");
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //if(device.getAddress().equals("EA:14:44:80:AA:16")){

                if(device.getAddress().equals("EA:14:44:80:AA:16") || device.getAddress().equals("CE:02:43:71:74:57")){
                    int rssi2 = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
//                    if(rssi2 >= -59 && rssi2 <= -58) {
//                        speaker.speak("You are 1 meter away");
//                    }
                    try {
                        writer.write(rssi2 + "\t");
                        writer.flush();
                    }catch(IOException ioe){
                        Log.i(TAG, "failed to write to file...");
                    }

//                    Text2SpeechTest Text2SpeechTest = new Text2SpeechTest();
//                    Text2SpeechTest.say(texttosay);
                    BTArrayAdapter.add(
                            new BTItem(
                                    device.getName(),
                                    device.getAddress(),
                                    intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE))
                    );
                }

                /*

                // add the name and the MAC address of the object to the arrayAdapter
                //BTArrayAdapter.add(device.getName() + "\n" + device.getAddress() + "\n" + intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE) + "dBm");
                data.add(device.getName() + "\n" + device.getAddress() + "\n" + intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE) + "dBm");

                */

                BTArrayAdapter.notifyDataSetChanged();

                //Toast.makeText(getApplicationContext(),"  RSSI: " + getRssi() + "dBm", Toast.LENGTH_SHORT).show();
                //Log.i("RSSI", String.valueOf(getRssi()));
            }

            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                myBluetoothAdapter.startDiscovery();
            }

        }


    };

    public void find(View view) {
        if (myBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        }
//        else {
//            //BTArrayAdapter.clear();
//            //remove below to prevent list self deleting...
//            //data.clear();
//            myBluetoothAdapter.startDiscovery();
//
//            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
//        }
        myBluetoothAdapter.startDiscovery();
        //registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    public void off(View view){
        myBluetoothAdapter.disable();
        text.setText("Status: Disconnected");

        Toast.makeText(getApplicationContext(),"Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            writer.close();
        }
        catch(IOException ioe){
            Log.i(TAG, "failed to close the file");
        }
        unregisterReceiver(bReceiver);
    }



//    private BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
//
//        @Override
//        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//
//        }
//    };

}