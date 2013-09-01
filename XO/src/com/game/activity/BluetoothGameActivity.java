package com.game.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.entity.Font;
import com.net.bluetooth.BluetoothService;
import com.net.bluetooth.BluetoothServiceViaProtobuf;

/**
 * Created by Maksym on 6/20/13.
 */
public class BluetoothGameActivity extends Activity implements View.OnClickListener {

    private TextView createGame;
    private TextView connect;
    private BluetoothService bluetoothService;
    BluetoothAdapter bluetoothAdapter;
    private static final int DISCOVERY_DEVICE = 1;
    private static final int ENABLE_DISCOVERABLE = 4;
    private static final int ENABLE_BT_FOR_DISCOVERING = 2;
    private static final int ENABLE_BT_FOR_CONNECT = 3;
    public static  final  int DISCOVERABLE_TIME = 120;
    private static final String TAG = BluetoothGameActivity.class.getCanonicalName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_game_layout);
        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/acquestscript.ttf");
        RelativeLayout l = (RelativeLayout) findViewById(R.id.bt_center_layout);
        Font.setAppFont(l, mFont);
        createGame = (TextView) findViewById(R.id.tv_create_bt_game);
        connect = (TextView) findViewById(R.id.tv_connect_bt_game);
        createGame.setOnClickListener(this);
        connect.setOnClickListener(this);

      //  bluetoothService.start();
    }

    @Override
    protected void onResume() {
        bluetoothService = new BluetoothServiceViaProtobuf();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.tv_create_bt_game:
                if (bluetoothAdapter.getScanMode() !=
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_TIME);
                    startActivityForResult(discoverableIntent, ENABLE_DISCOVERABLE);
                }
                else bluetoothService.start();
                break;
            case R.id.tv_connect_bt_game:
                BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBtAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, ENABLE_BT_FOR_CONNECT);
                    break;
                }
                Intent intent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(intent, DISCOVERY_DEVICE);
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, requestCode + " " + resultCode);
        if (requestCode == ENABLE_BT_FOR_CONNECT && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(intent, DISCOVERY_DEVICE);
        } else if (requestCode == ENABLE_BT_FOR_DISCOVERING && resultCode == RESULT_OK) {
                    bluetoothService.start();
        }

        else if (requestCode == DISCOVERY_DEVICE && resultCode == RESULT_OK){
            connectDevice(data, false);
        }

        else if (requestCode == ENABLE_DISCOVERABLE && resultCode == DISCOVERABLE_TIME){
            bluetoothService.start();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        Log.d(TAG, "attempting to connect with " + address  + " name " + device.getName());
        bluetoothService.connect(device, true);

    }
}