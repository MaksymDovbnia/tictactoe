package com.bigtictactoeonlinegame.bluetoothgame;

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.activity.*;
import com.bigtictactoeonlinegame.gamefield.*;
import com.bigtictactoeonlinegame.mainactivity.*;
import com.bigtictactoeonlinegame.popup.*;
import com.bluetooth.*;
import com.config.*;
import com.entity.*;

/**
 * Created by Maksym on 6/20/13.
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class BluetoothGameActivity extends GoogleAnalyticsActivity implements View.OnClickListener {
    private static final int DISCOVERY_DEVICE = 150;
    private static final int ENABLE_DISCOVERABLE = 4;
    private static final int ENABLE_BT_FOR_CONNECT = 320;
    public static final int DISCOVERABLE_TIME = 300;
    private static final int START_GAME_ACTIVITY = 100;
    private static final String TAG = BluetoothGameActivity.class.getCanonicalName();

    private View createGame;
    private View connect;
    private BluetoothService bluetoothService;
    private BluetoothAdapter bluetoothAdapter;
    private ProgressDialog progressDialogStartServer;
    private ProgressDialog progressDialogConnectToserver;
    private boolean isNeedToConnect = false;
    private String playerName;
    private boolean isPlayerMoveFirst = false;
    private BluetoothDevice device;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_game_activity_layout);
        findViewById(R.id.btn_back_from_select_type_game).setOnClickListener(this);
        createGame =  findViewById(R.id.tv_create_bt_game);
        connect =  findViewById(R.id.tv_connect_bt_game);
        createGame.setOnClickListener(this);
        connect.setOnClickListener(this);
        playerName = getIntent().getStringExtra(BundleKeys.PLAYER_NAME);
        bluetoothService = new BluetoothServiceViaProtobuf(playerName);
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

            @Override
            public void connectionFailed() {
                progressDialogConnectToserver.dismiss();
                XOAlertDialog xoAlertDialog = new XOAlertDialog();
                xoAlertDialog.setAlert_type(XOAlertDialog.ALERT_TYPE.ONE_BUTTON);
                xoAlertDialog.setTile(getResources().getString(R.string.try_to_connect_failed));
                xoAlertDialog.setMainText(getString(R.string.to) + " \"" + device.getName() + "\"");
                xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.ok));
                xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                xoAlertDialog.setNegativeListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                xoAlertDialog.show(getSupportFragmentManager(), "");
            }

            @Override
            public void opponentsTimeFinished() {

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
                Intent intent = new Intent(this, BTDeviceListActivity.class);
                startActivityForResult(intent, DISCOVERY_DEVICE);
                isNeedToConnect = true;
                break;
            case  R.id.btn_back_from_select_type_game:
                finish();
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
                connectToDevice(data, false);
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

    private void connectToDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(BTDeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        device = bluetoothAdapter.getRemoteDevice(address);
        progressDialogConnectToserver = new ProgressDialog(this);
        progressDialogConnectToserver.setTitle(R.string.connecting_to_bluetooth_server);
        progressDialogConnectToserver.setMessage(getString(R.string.connecting_to) + " \"" + device.getName() + "\"");
        progressDialogConnectToserver.show();
        progressDialogConnectToserver.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

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