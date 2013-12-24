package com.game.bluetoothgame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bluetooth.IBluetoothGameListener;
import com.config.BundleKeys;
import com.bluetooth.BluetoothService;
import com.bluetooth.BluetoothServiceViaProtobuf;
import com.entity.OneMove;
import com.entity.TypeOfMove;
import com.game.Controller;
import com.game.GameType;
import com.game.activity.R;
import com.game.gamefield.GameFieldActivity;

import net.protocol.Protocol;

/**
 * Created by Maksym on 6/20/13.
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class BluetoothGameActivity extends Activity implements View.OnClickListener {
    private static final int DISCOVERY_DEVICE = 150;
    private static final int ENABLE_DISCOVERABLE = 4;
    private static final int ENABLE_BT_FOR_CONNECT = 320;
    public static final int DISCOVERABLE_TIME = 300;
    private static final int START_GAME_ACTIVITY = 100;
    private static final String TAG = BluetoothGameActivity.class.getCanonicalName();

    private TextView createGame;
    private TextView connect;
    private BluetoothService bluetoothService;
    private BluetoothAdapter bluetoothAdapter;
    private ProgressDialog progressDialogStartServer;
    private ProgressDialog progressDialogConnectToserver;
    private boolean isNeedToConnect = false;
    private String playerName;
    private boolean isPlayerMoveFirst = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_game_activity_layout);
        createGame = (TextView) findViewById(R.id.tv_create_bt_game);
        connect = (TextView) findViewById(R.id.tv_connect_bt_game);
        createGame.setOnClickListener(this);
        connect.setOnClickListener(this);
        playerName = getIntent().getStringExtra(BundleKeys.PLAYER_NAME);
        bluetoothService = new BluetoothServiceViaProtobuf(this, playerName);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_TIME);
            startActivityForResult(discoverableIntent, ENABLE_DISCOVERABLE);
        }
        bluetoothService.registerListener(new IBluetoothGameListener() {
            @Override
            public void receivedNewChatMessage(String message) {
            }

            @Override
            public void receivedNewOneMove(OneMove oneMove) {
            }

            @Override
            public void startGame(String opponentName) {
                startBTGame(opponentName);
            }


            @Override
            public void playerExitFromGame() {
            }

            @Override
            public void continueGame() {


            }
        });
    }


    private void startBTGame(String oponenName) {
        Intent intent = new Intent(this, GameFieldActivity.class);
        intent.putExtra(BundleKeys.TYPE_OF_GAME, GameType.BLUETOOTH);
        intent.putExtra(BundleKeys.PLAYER_NAME, playerName);
        intent.putExtra(BundleKeys.OPPONENT_NAME, oponenName);
        intent.putExtra(BundleKeys.IS_PLAYER_MOVE_FIRST, isPlayerMoveFirst);
        Controller.getInstance().setBluetoothService(bluetoothService);
//        progressDialogStartServer.dismiss();
        startActivityForResult(intent, START_GAME_ACTIVITY);
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
                } else startServer();
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
                isNeedToConnect = true;
                break;
        }
    }

    private void startServer() {
        isPlayerMoveFirst = true;
        bluetoothService.start();
        progressDialogStartServer = new ProgressDialog(this);
        progressDialogStartServer.setTitle(R.string.wait_opponent_connection);
        progressDialogStartServer.setMessage(getString(R.string.server_started_wait_connection));
        progressDialogStartServer.show();
        progressDialogStartServer.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode + " " + resultCode + " isNeedToConnect  " +
                " " + isNeedToConnect);
        if (resultCode == RESULT_OK && requestCode == DISCOVERY_DEVICE) {
            if (isNeedToConnect) {
                connectDevice(data, false);
                isNeedToConnect = false;
            }
        } else if (requestCode == ENABLE_DISCOVERABLE && resultCode == DISCOVERABLE_TIME) {
            // bluetoothService.start();
        } else if (requestCode == ENABLE_DISCOVERABLE && resultCode == RESULT_CANCELED) {
            finish();
        } else if (requestCode == START_GAME_ACTIVITY) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectDevice(Intent data, boolean secure) {
        progressDialogConnectToserver = new ProgressDialog(this);
        progressDialogConnectToserver.setTitle(R.string.wait_connection);
        progressDialogConnectToserver.setMessage(getString(R.string.try_connect_to_game));
        progressDialogConnectToserver.show();
        progressDialogConnectToserver.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        Log.d(TAG, "attempting to connect with " + address + " name " + device.getName());
        bluetoothService.connect(device, false);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothService.unRegisterListener();
        bluetoothService.stop();
    }
}