package com.example.bleapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bleapplication.Util.BleUtil;
import com.example.bleapplication.Util.BleUuid;

import java.util.UUID;

public class DeviceActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "BLEDevice";

    public static final String EXTRA_BLUETOOTH_DEVICE = "BT_DEVICE";
    private BluetoothAdapter mBTAdapter;
    private BluetoothDevice mDevice;
    private BluetoothGatt mConnGatt;
    private int mStatus;
    private Button onButton;
    private Button OffButton;
    private Button OnBeepButton;
    private Button OffBeepButton;
    private ImageView bulbImage;
    TextView TemperatureNotify;

    int onValue =1;
    int offValue =0;

    private Button mReadSerialNumberButton;
    private Button mWriteAlertLevelButton;

    private final BluetoothGattCallback mGattcallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mStatus = newState;
                mConnGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mStatus = newState;
                runOnUiThread(new Runnable() {
                    public void run() {
                        onButton.setEnabled(false);
                        OffButton.setEnabled(false);
                        OnBeepButton.setEnabled(false);
                        OffBeepButton.setEnabled(false);
                       /* mReadSerialNumberButton.setEnabled(false);
                        mWriteAlertLevelButton.setEnabled(false);*/
                    };
                });
            }
        };

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            for (BluetoothGattService service : gatt.getServices()) {
                if ((service == null) || (service.getUuid() == null)) {
                    continue;
                }
                if (BleUuid.SERVICE_DEVICE_INFORMATION.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    Log.d("debug","charachteristics:"+service.getCharacteristics().toString());
                    Log.d("debug","charachteristics:"+service.getCharacteristic(UUID
                            .fromString(BleUuid.Temperature)).getValue());
                    Log.d("debug","charachteristics bulb:"+service.getCharacteristic(UUID
                            .fromString(BleUuid.Bulb)).getValue());

                    onButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.Bulb)));
                    OffButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.Bulb)));
                    OnBeepButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.Beep)));
                    OffBeepButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.Beep)));
                    TemperatureNotify.setTag(service.getCharacteristic(UUID
                            .fromString(BleUuid.Temperature)));

                    BluetoothGattCharacteristic characteristic =service.getCharacteristic(UUID
                            .fromString(BleUuid.Temperature));

                    gatt.setCharacteristicNotification(characteristic,true);
                    gatt.readCharacteristic(characteristic);

                    /*mReadSerialNumberButton
                            .setTag(service.getCharacteristic(UUID
                                    .fromString(BleUuid.Bulb)));*/
                    runOnUiThread(new Runnable() {
                        public void run() {
                            onButton.setEnabled(true);
                            OffButton.setEnabled(true);
                            OnBeepButton.setEnabled(true);
                            OffBeepButton.setEnabled(true);
                            //TemperatureNotify.setText();
//                            mReadSerialNumberButton.setEnabled(true);
                        };
                    });
                }
              /*  if (BleUuid.SERVICE_IMMEDIATE_ALERT.equalsIgnoreCase(service
                        .getUuid().toString())) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mWriteAlertLevelButton.setEnabled(true);
                        };
                    });
                    mWriteAlertLevelButton.setTag(service
                            .getCharacteristic(UUID
                                    .fromString(BleUuid.CHAR_ALERT_LEVEL)));
                }*/
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    setProgressBarIndeterminateVisibility(false);
                };
            });
        };

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt,
                                         final BluetoothGattCharacteristic characteristic, int status) {
            Log.d("debug","characteristic read");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (BleUuid.Bulb
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    //Log.d("debug","temperature value char"+characteristic.getValue());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Log.d("debug","temperature value"+name);
                            //setProgressBarIndeterminateVisibility(false);
                        };
                    });
                } else if (BleUuid.Temperature
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        public void run() {

                            Log.d("debug","temperature value"+name);
                            TemperatureNotify.setText(name + " F");
                            //mReadSerialNumberButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        };
                    });
                }

            }
        }

        @Override
// Characteristic notification
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.d("debug","notify temp change");
            if (BleUuid.Temperature
                    .equalsIgnoreCase(characteristic.getUuid().toString())) {
                final String name = characteristic.getStringValue(0);

                //Log.d("debug","temperature value char"+characteristic.getValue());
                runOnUiThread(new Runnable() {
                    public void run() {
                        TemperatureNotify.setText(name + " F");
                        Log.d("debug","temperature value"+name);
                       // onButton.setText(name);
                        setProgressBarIndeterminateVisibility(false);
                    };
                });
            }

        }
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            Log.d("debug","write temp change");
            runOnUiThread(new Runnable() {
                public void run() {

                    setProgressBarIndeterminateVisibility(false);
                };
            });
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device);

        setTitle("Smart BLE Bulb");

        mStatus = BluetoothProfile.STATE_DISCONNECTED;
        onButton = (Button) findViewById(R.id.OnButton);
        onButton.setOnClickListener(this);
        OffButton = (Button) findViewById(R.id.OffButton);
        OffButton.setOnClickListener(this);
        OnBeepButton = (Button) findViewById(R.id.onBeepButton);
        OnBeepButton.setOnClickListener(this);
        OffBeepButton = (Button) findViewById(R.id.offBeepButton);
        OffBeepButton.setOnClickListener(this);
        TemperatureNotify=(TextView)findViewById(R.id.temperatureText);
        TemperatureNotify.setOnClickListener(this);
        bulbImage = (ImageView)findViewById(R.id.bulbImageView);

        /*mReadSerialNumberButton = (Button) findViewById(R.id.read_serial_number_button);
        mReadSerialNumberButton.setOnClickListener(this);
        mWriteAlertLevelButton = (Button) findViewById(R.id.write_alert_level_button);
        mWriteAlertLevelButton.setOnClickListener(this);*/
    }


    @Override
    protected void onResume() {
        super.onResume();

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnGatt != null) {
            if ((mStatus != BluetoothProfile.STATE_DISCONNECTING)
                    && (mStatus != BluetoothProfile.STATE_DISCONNECTED)) {
                mConnGatt.disconnect();
            }
            mConnGatt.close();
            mConnGatt = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.OnButton) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();

                int unixTime = onValue;
                String unixTimeString = Integer.toHexString(unixTime);
                Log.d("test", "value in string is" +unixTimeString);
                byte[] b = unixTimeString.getBytes();
                Log.d("test","value in bytes" +b);
//                byte[] byteArray = hexStringToByteArray(unixTimeString);
                Log.d("test","value is"+new byte[] { (byte) 0x03 });
                //Log.d("test","value in converted byte"+byteArray);
                Log.d("debug","Value of ch in onButton"+ch.getStringValue(0));
                ch.setValue(b);
                if (mConnGatt.writeCharacteristic(ch)) {
                    bulbImage.setImageResource(R.drawable.onbulb);
                    setProgressBarIndeterminateVisibility(true);
                }
            }
        } else if (v.getId() == R.id.OffButton) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                int offval = offValue;
                String offvalstr = Integer.toHexString(offval);
                Log.d("test", "value in string is"+offvalstr);
                byte[] f = offvalstr.getBytes();
                Log.d("test","value in bytes for offButton" +f);
                ch.setValue(new byte[] { (byte) 0x00 });
                if (mConnGatt.writeCharacteristic(ch)) {
                    Log.d("test","success");
                    bulbImage.setImageResource(R.drawable.bulb);
                    setProgressBarIndeterminateVisibility(true);
                }
            }

        } else if (v.getId() == R.id.onBeepButton) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                ch.setValue(new byte[] { (byte) 0x03 });
                if (mConnGatt.writeCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }
        }else if (v.getId() == R.id.offBeepButton) {
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                ch.setValue(new byte[] { (byte) 0x00 });
                if (mConnGatt.writeCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }
        }else if (v.getId() == R.id.temperatureText) {
            Log.d("debug","On temp click");
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                mConnGatt.readCharacteristic(ch);
                Log.d("debug","On click of temperature text"+ch.getStringValue(0));
                if (mConnGatt.readCharacteristic(ch)) {
                    TemperatureNotify.setText(String.valueOf(ch.getValue()));
                    setProgressBarIndeterminateVisibility(true);
                }
            }
        }
    }

   /* public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }*/

    private void init() {
        // BLE check
        if (!BleUtil.isBLESupported(this)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        // BT check
        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();
        }
        if (mBTAdapter == null) {
            Toast.makeText(this, R.string.bt_unavailable, Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        // check BluetoothDevice
        if (mDevice == null) {
            mDevice = getBTDeviceExtra();
            if (mDevice == null) {
                finish();
                return;
            }
        }

        // button disable
        onButton.setEnabled(false);
        OffButton.setEnabled(false);
        OnBeepButton.setEnabled(false);
        OffBeepButton.setEnabled(false);
        /*mReadSerialNumberButton.setEnabled(false);
        mWriteAlertLevelButton.setEnabled(false);*/

        // connect to Gatt
        if ((mConnGatt == null)
                && (mStatus == BluetoothProfile.STATE_DISCONNECTED)) {
            // try to connect
            mConnGatt = mDevice.connectGatt(this, false, mGattcallback);
            mStatus = BluetoothProfile.STATE_CONNECTING;
        } else {
            if (mConnGatt != null) {
                // re-connect and re-discover Services
                mConnGatt.connect();
                mConnGatt.discoverServices();
            } else {
                Log.e(TAG, "state error");
                finish();
                return;
            }
        }
        setProgressBarIndeterminateVisibility(true);
    }

    private BluetoothDevice getBTDeviceExtra() {
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }

        Bundle extras = intent.getExtras();
        if (extras == null) {
            return null;
        }

        return extras.getParcelable(EXTRA_BLUETOOTH_DEVICE);
    }

}
