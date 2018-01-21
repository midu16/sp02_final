package com.example.m16142.pulseoximeter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;


public class MeasureActivity extends Activity {

    private final static String TAG = MeasureActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private static final String MLDP_PRIVATE_SERVICE = "00035b03-58e6-07dd-021a-08123a000300";
    private static final String MLDP_DATA_PRIVATE_CHAR = "00035b03-58e6-07dd-021a-08123a000301";
    private static final String MLDP_CONTROL_PRIVATE_CHAR = "00035b03-58e6-07dd-021a-08123a0003ff";
    private static final String CHARACTERISTIC_NOTIFICATION_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";


    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mData, mControl;

    private TextView userNameTxt;
    private static TextView spO2Txt;
    private static TextView heartRateTxt;

    private String mDeviceName, mDeviceAddress;
    private boolean mConnected;
    private Handler mHandler;
    private TextView mConnectionState;


    private boolean writeComplete = false;


    public  String saturationVal;
    public  String pulseVal;

    //last value
    public static String lastSaturationVal;
    public static String lastPulseVal;

    public static User userM;


    //colors
    private static int RED = Color.rgb(242, 86, 86);
    private static int GREEN = Color.rgb(102, 169, 24);
    private static int BLUE = Color.rgb(148, 148, 247);
    private static int ORANGE = Color.rgb(239, 168, 61);
    private static int PURPLE = Color.rgb(107, 48, 158);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);


        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mHandler = new Handler();


        ((TextView) findViewById(R.id.deviceAddress)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connectionState);
        userNameTxt = (TextView) findViewById(R.id.textView_user);
        spO2Txt = (TextView) findViewById(R.id.textView_spO2);
        spO2Txt.setMovementMethod(new ScrollingMovementMethod());
        heartRateTxt = (TextView) findViewById(R.id.textView_heartRate);
        heartRateTxt.setMovementMethod(new ScrollingMovementMethod());



        if(NewUserFragment.registerNew == true){
            userM = NewUserFragment.patient;
            userNameTxt.setText(NewUserFragment.patient.getName());
        } else if(HistoryActivity.userSelected == true){
            userM = HistoryActivity.userS;
            userNameTxt.setText(HistoryActivity.userS.getName());
        } else {
            userNameTxt.setText("No user");
        }


        this.getActionBar().setTitle(mDeviceName);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mBluetoothAdapter == null || mDeviceAddress == null){
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            finish();
        }


        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
        if(device == null){
            Log.w(TAG, "Device not found. Unable to connect.");
            finish();
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_measure, menu);
        if(mConnected){
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_connect:
                if(mBluetoothGatt != null){
                    mBluetoothGatt.connect();
                }
                return true;
            case R.id.menu_disconnect:
                if(mBluetoothGatt != null){
                    mBluetoothGatt.disconnect();
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void findGattService(List<BluetoothGattService> gattServices){
        if(gattServices == null){
            Log.d(TAG, "findGattService found no Services");
            return;
        }

        String uuid;
        mData = null;

        for(BluetoothGattService gattService:gattServices){
            uuid = gattService.getUuid().toString();
            if(uuid.equals(MLDP_PRIVATE_SERVICE)){
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for(BluetoothGattCharacteristic gattCharacteristic:gattCharacteristics){
                    uuid = gattCharacteristic.getUuid().toString();
                    if(uuid.equals(MLDP_DATA_PRIVATE_CHAR)){
                        mData = gattCharacteristic;
                        Log.d(TAG, "Found data characteristics");
                    } else if(uuid.equals(MLDP_CONTROL_PRIVATE_CHAR)){
                        mControl = gattCharacteristic;
                        Log.d(TAG, "Found control characteristics");
                    }

                    final int characteristicProperties = gattCharacteristic.getProperties();
                    if((characteristicProperties & (BluetoothGattCharacteristic.PROPERTY_NOTIFY)) > 0){
                        mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                        BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(UUID.fromString(CHARACTERISTIC_NOTIFICATION_CONFIG));
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(descriptor);
                    }

                    if((characteristicProperties & (BluetoothGattCharacteristic.PROPERTY_INDICATE)) > 0){
                        mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                        BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(UUID.fromString(CHARACTERISTIC_NOTIFICATION_CONFIG));
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(descriptor);
                    }

                    if((characteristicProperties & (BluetoothGattCharacteristic.PROPERTY_WRITE)) > 0){
                        gattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    }
                    if((characteristicProperties & (BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0){
                        gattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    }
                }
                break;
            }
        }

        if(mData == null){
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "findGattService found no service");
            finish();
        }
    }


    private final BluetoothGattCallback aGattCallback = new BluetoothGattCallback() {
    };

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback(){

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState == BluetoothProfile.STATE_CONNECTED){
                Log.i(TAG, "Connected to GATT server");
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
                clearAllText();
                mBluetoothGatt.discoverServices();
            }
            else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                Log.i(TAG, "Disconnected from GATT server.");
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearAllText();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS && mBluetoothGatt != null){
                findGattService(mBluetoothGatt.getServices());
            }
            else{
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                String dataValue = characteristic.getStringValue(0);
                appendText(dataValue);

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                writeComplete = true;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            final String dataValue = characteristic.getStringValue(0);
            appendText(dataValue);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        }
    };


    private void readCharacteristic(BluetoothGattCharacteristic characteristic){
        if(mBluetoothAdapter == null || mBluetoothGatt == null){
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    private void writeCharacteristic(BluetoothGattCharacteristic characteristic){
        if(mBluetoothAdapter == null || mBluetoothGatt == null){
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        int test = characteristic.getProperties();
        if((test & BluetoothGattCharacteristic.PROPERTY_WRITE) == 0 && (test & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0){
            return;
        }

        if(mBluetoothGatt.writeCharacteristic(characteristic)){
            Log.d(TAG, "writeCharacteristic successful");
        }
        else{
            Log.d(TAG, "writeCharacteristic failed");
        }
    }

    private void updateConnectionState(final int state){
        runOnUiThread(new Runnable() {                                                  //Must run changes to user interface in the UI thread
            @Override
            public void run() {
                mConnectionState.setText(state);
            }
        });
    }

    private void clearAllText(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spO2Txt.setText(null);
                heartRateTxt.setText(null);
            }
        });
    }


    private void appendText(final String data){
        if(data != null){
            if(data.length() > 1){
                final String[] dataBuffer = data.split(";");
                if(NewUserFragment.registerNew == true || HistoryActivity.userSelected == true){
                    saturationVal = dataBuffer[1];
                    pulseVal = dataBuffer[2];
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        StringBuilder spO2Builder = new StringBuilder();
                        StringBuilder pulseBuilder = new StringBuilder();


                        //split every last 2 characters of string in one character
                        char c1 = dataBuffer[1].charAt(3);
                        char c2 = dataBuffer[1].charAt(4);

                        //append these characters to a StringBuilder
                        spO2Builder = spO2Builder.append(c1).append(c2);
                        //put StringBuilder into a String
                        String spO2String = new String(spO2Builder);
                        //convert string into integer
                        int spO2Val = Integer.parseInt(spO2String);
                        if(spO2Val >= 95){
                            spO2Txt.setTextColor(RED);
                        } else if(spO2Val <95 && spO2Val >= 90){
                            spO2Txt.setTextColor(GREEN);
                        } else if(spO2Val < 90 && spO2Val >= 80){
                            spO2Txt.setTextColor(ORANGE);
                        } else if(spO2Val < 80){
                            spO2Txt.setTextColor(PURPLE);
                        }

                        spO2Txt.setText(spO2String + "%");


                        //check if lastPulseVal has 3 digits
                        if(dataBuffer[2].charAt(2) != '0'){
                            //split every last 3 characters of string in one character
                            char c1P = dataBuffer[2].charAt(2);
                            char c2P = dataBuffer[2].charAt(3);
                            char c3P = dataBuffer[2].charAt(4);

                            //append these characters to a StringBuilder
                            pulseBuilder = pulseBuilder.append(c1P).append(c2P).append(c3P);
                            //put StringBuilder into a String
                            String pulseString = new String(pulseBuilder);
                            //convert String into Integer
                            int pulseVal = Integer.parseInt(pulseString);
                            if(pulseVal < 60){
                                heartRateTxt.setTextColor(ORANGE);
                            } else if(pulseVal > 90){
                                heartRateTxt.setTextColor(GREEN);
                            } else if(pulseVal <= 90 && pulseVal >= 60){
                                heartRateTxt.setTextColor(BLUE);
                            }

                            heartRateTxt.setText(pulseString + "bpm");
                        } else {
                            //split every last 3 characters of string in one character
                            char c1P = dataBuffer[2].charAt(3);
                            char c2P = dataBuffer[2].charAt(4);

                            //append these characters to a StringBuilder
                            pulseBuilder = pulseBuilder.append(c1P).append(c2P);
                            //put StringBuilder into a String
                            String pulseString = new String(pulseBuilder);
                            //convert String into Integer
                            int pulseVal = Integer.parseInt(pulseString);
                            if(pulseVal < 60){
                                heartRateTxt.setTextColor(ORANGE);
                            } else if(pulseVal > 90){
                                heartRateTxt.setTextColor(GREEN);
                            } else if(pulseVal <= 90 && pulseVal >= 60){
                                heartRateTxt.setTextColor(BLUE);
                            }

                            heartRateTxt.setText(pulseString + "bpm");
                        }

                    }
                });

            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spO2Txt.append("");

                    }
                });
            }

        } else {
            spO2Txt.setText("");
            heartRateTxt.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        lastSaturationVal = saturationVal;
        lastPulseVal = pulseVal;



       // Intent intent = new Intent(this, DeviceScanActivity.class);
       // startActivity(intent);
    }


}
