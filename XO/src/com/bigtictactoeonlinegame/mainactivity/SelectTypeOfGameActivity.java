package com.bigtictactoeonlinegame.mainactivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigtictactoeonlinegame.GameType;
import com.bigtictactoeonlinegame.bluetoothgame.BluetoothGameActivity;
import com.bigtictactoeonlinegame.gamefield.GameFieldActivity;
import com.bigtictactoeonlinegame.onlinerooms.OnlineRoomsActivity;
import com.bigtictactoeonlinegame.popup.XOAlertDialog;
import com.config.BundleKeys;
import com.entity.Player;
import com.bigtictactoeonlinegame.Controller;
import com.bigtictactoeonlinegame.activity.R;
import com.google.android.gms.ads.AdView;
import com.net.online.WorkerOnlineConnection;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;
import com.utils.MemoTextWatcher;

import net.protocol.Protocol;

public class SelectTypeOfGameActivity extends GeneralAdActivity implements OnClickListener {

    private static final String TAG = SelectTypeOfGameActivity.class.getCanonicalName();
    private static final String KEY_FOR_SHARED_PREFERENCES = SelectTypeOfGameActivity.class.getCanonicalName();
    private static final String FIRST_PLAYER_NAME = "first_player_name";
    private static final String SECOND_PLAYER_NAME = "second_player_name";
    private static final String PLAYER_NAME_FOR_LOGIN_TO_ONLINE_GAME = "player_name_for_login_to_online_game";
    private static final String PLAYER_NAME_FOR_BLUETOOTH_GAME = "player_name_for_bluetooth_game";
    private static final String PLAYER_UUID_FOR_ONLINE_GAME = "player_uuid_for_online_game";


    private View friend;
    private View online;
    private View android;
    private View bluetooth;
    private static final int OFFLINE_GAME_POPUP = 1;
    private SharedPreferences sharedPreferences;
    private String player1NameFromSharedPrefences;
    private String player2NameFromSharedPrefences;
    private String playerNameFromSharedPrefencesForLoginToGame;
    private String playerNameFromSharedPrefencesForBluetoohToGame;
    private String playerUUID;


    private Handler handler;
    private ProgressDialog pd;
    private Player player;

    private EditText loginAnon;
    private WorkerOnlineConnection onlineGameWorker;
    private XOAlertDialog anonymousLoginPopup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_of_game_activity_layout);
        sharedPreferences = getSharedPreferences(KEY_FOR_SHARED_PREFERENCES, MODE_PRIVATE);
        friend = findViewById(R.id.btn_two_players);
        friend.setOnClickListener(this);
        online = findViewById(R.id.btn_online);
        online.setOnClickListener(this);
        android = findViewById(R.id.btn_android);
        android.setOnClickListener(this);
        bluetooth = findViewById(R.id.btn_bluetooth);
        bluetooth.setOnClickListener(this);
        player = new Player();
        createHandler();
    }

    @Override
    public AdView getAdView() {
        return (AdView) findViewById(R.id.ad_view);
    }


    private void createHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ProtoType protoType = ProtoType.fromInt(msg.what);
                if (msg.what == 100) {
                    pd.cancel();
                    showToastWithText(getString(R.string.server_not_available));
                    return;
                }
                Log.d(TAG, "handler received message" + protoType);
                switch (protoType) {
                    case CLOGINTOGAME:
                        Protocol.CLoginToGame cLoginToGame = (Protocol.CLoginToGame) msg.obj;
                        int id = cLoginToGame.getId();
                        player.setId(id);
                        Controller.getInstance().setPlayer(player);
                        Loger.printLog("Conected to server with id " + id);
                        pd.cancel();
                        if (anonymousLoginPopup != null) anonymousLoginPopup.dismiss();
                        loginToGame();
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        player1NameFromSharedPrefences = sharedPreferences.getString(FIRST_PLAYER_NAME, null);
        player2NameFromSharedPrefences = sharedPreferences.getString(SECOND_PLAYER_NAME, null);
        playerNameFromSharedPrefencesForLoginToGame = sharedPreferences.getString(PLAYER_NAME_FOR_LOGIN_TO_ONLINE_GAME, null);
        playerNameFromSharedPrefencesForBluetoohToGame = sharedPreferences.getString(PLAYER_NAME_FOR_BLUETOOTH_GAME, null);
        playerUUID = sharedPreferences.getString(PLAYER_UUID_FOR_ONLINE_GAME, null);
        if (playerUUID != null) player.setUuid(playerUUID);
    }

    private void loginToGame() {
        Intent intent = new Intent(this, OnlineRoomsActivity.class);
        startActivity(intent);
    }

    private void showToastWithText(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }


    public void showTypeOfLoginToGamePopup() {
        final XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setContent(R.layout.login_to_game_popup);
        xoAlertDialog.setContentInitialization(new XOAlertDialog.IContentInitialization() {
            @Override
            public void onContentItialization(View view) {
                view.findViewById(R.id.tv_anonymous).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAnonymousPopup();
                        xoAlertDialog.dismiss();
                    }
                });
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");
    }

    public void showPopupForInputNameOfPlayers() {
        final XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setContent(R.layout.popup_offline_players_name);
        xoAlertDialog.setContentInitialization(new XOAlertDialog.IContentInitialization() {
            @Override
            public void onContentItialization(final View view) {
                EditText playerName1;
                EditText playerName2;
                playerName1 = ((EditText) view.findViewById(R.id.edt_first_player_name));
                playerName2 = ((EditText) view.findViewById(R.id.edt_second_player_name));
                playerName1.addTextChangedListener(new MemoTextWatcher() {
                    @Override
                    protected void showMaxAlert() {
                        String text = getString(R.string.maximum_chars_in_nick_is) + MemoTextWatcher.MAX_CHARACTER_COUNT;
                        Toast.makeText(SelectTypeOfGameActivity.this, text, Toast.LENGTH_SHORT).show();
                    }
                });
                playerName2.addTextChangedListener(new MemoTextWatcher() {
                    @Override
                    protected void showMaxAlert() {
                        String text = getString(R.string.maximum_chars_in_nick_is) + " " + MemoTextWatcher.MAX_CHARACTER_COUNT;
                        Toast.makeText(SelectTypeOfGameActivity.this, text, Toast.LENGTH_SHORT).show();
                    }
                });
                if (player1NameFromSharedPrefences != null) {
                    playerName1.setText(player1NameFromSharedPrefences);
                    playerName2.setText(player2NameFromSharedPrefences);
                }
                view.findViewById(R.id.btn_start_offline_game).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SelectTypeOfGameActivity.this, GameFieldActivity.class);
                        String name1 = ((EditText) view.findViewById(R.id.edt_first_player_name)).getText().toString();
                        String name2 = ((EditText) view.findViewById(R.id.edt_second_player_name)).getText().toString();
                        intent.putExtra(GameFieldActivity.FIRST_PLAYER_NAME, name1);
                        intent.putExtra(GameFieldActivity.SECOND_PLAYER_NAME, name2);
                        intent.putExtra(BundleKeys.TYPE_OF_GAME, GameType.FRIEND);
                        sharedPreferences.edit().putString(FIRST_PLAYER_NAME, name1).commit();
                        sharedPreferences.edit().putString(SECOND_PLAYER_NAME, name2).commit();
                        startActivity(intent);
                        xoAlertDialog.dismiss();
                    }
                });
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");
    }

    private void showAnonymousPopup() {
        final XOAlertDialog xoAlertDialog = new XOAlertDialog();
        anonymousLoginPopup = xoAlertDialog;
        xoAlertDialog.setContent(R.layout.input_player_name_popup_layout);
        xoAlertDialog.setContentInitialization(new XOAlertDialog.IContentInitialization() {
            @Override
            public void onContentItialization(View view) {
                final EditText playerNameEditText = (EditText) view.findViewById(R.id.edt_player_name);
                playerNameEditText.addTextChangedListener(new MemoTextWatcher() {
                    @Override
                    protected void showMaxAlert() {
                        String text = getString(R.string.maximum_chars_in_nick_is) + MemoTextWatcher.MAX_CHARACTER_COUNT;
                        Toast.makeText(SelectTypeOfGameActivity.this, text, Toast.LENGTH_SHORT).show();
                    }
                });
                if (playerUUID == null) {
                    playerUUID = generateUUID();
                    sharedPreferences.edit().putString(PLAYER_UUID_FOR_ONLINE_GAME, playerUUID).commit();
                    player.setUuid(playerUUID);
                }
                if (playerNameFromSharedPrefencesForLoginToGame != null)
                    playerNameEditText.setText(playerNameFromSharedPrefencesForLoginToGame);
                else playerNameEditText.setText(getString(R.string.anonym_player));
                view.findViewById(R.id.btn_anonymous_login).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String playerName = playerNameEditText.getText().toString();
                        if (playerName.length() == 0) {
                            showToastWithText(getString(R.string.please_enter_your_nick_name));
                            return;
                        }
                        sharedPreferences.edit().putString(PLAYER_NAME_FOR_LOGIN_TO_ONLINE_GAME, playerNameFromSharedPrefencesForLoginToGame = playerName).commit();
                        pd = new ProgressDialog(SelectTypeOfGameActivity.this);
                        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
                        if (activeNetwork != null && activeNetwork.isConnected()) {
                            player.setName(playerName);
                            player.setRegistrationType(Protocol.RegistrationType.xo);
                            onlineGameWorker = new WorkerOnlineConnection(handler,
                                    player, pd);
                            Controller.getInstance().setOnlineWorker(onlineGameWorker);
                            onlineGameWorker.start();
                            pd.setTitle(getString(R.string.connection));
                            pd.setMessage(getString(R.string.connecting_server));
                            pd.show();
                            // pd.cancel();
                        } else
                            showToastWithText(getString(R.string.no_internet_connection));
                    }
                });
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");
    }


    private void showPopupForBluetoothGame() {
        final XOAlertDialog xoAlertDialog = new XOAlertDialog();
        anonymousLoginPopup = xoAlertDialog;
        xoAlertDialog.setContent(R.layout.input_player_name_popup_layout);
        xoAlertDialog.setContentInitialization(new XOAlertDialog.IContentInitialization() {
            @Override
            public void onContentItialization(View view) {
                final EditText playerNameEditText = (EditText) view.findViewById(R.id.edt_player_name);
                playerNameEditText.addTextChangedListener(new MemoTextWatcher() {
                    @Override
                    protected void showMaxAlert() {
                        String text = getString(R.string.maximum_chars_in_nick_is) + " " + MemoTextWatcher.MAX_CHARACTER_COUNT;
                        Toast.makeText(SelectTypeOfGameActivity.this, text, Toast.LENGTH_SHORT).show();
                    }
                });
                if (playerNameFromSharedPrefencesForBluetoohToGame != null) {
                    playerNameEditText.setText(playerNameFromSharedPrefencesForBluetoohToGame);
                } else {
                    if (BluetoothAdapter.getDefaultAdapter().getName() == null) {
                        playerNameEditText.setText(R.string.player);
                    } else {
                        playerNameEditText.setText(BluetoothAdapter.getDefaultAdapter().getName());

                    }
                }
                Button startBTGame = (Button) view.findViewById(R.id.btn_anonymous_login);
                startBTGame.setText(R.string.start_game);
                startBTGame.findViewById(R.id.btn_anonymous_login).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String playerName = playerNameEditText.getText().toString();
                        if (playerName.length() == 0) {
                            showToastWithText(getString(R.string.please_enter_your_nick_name));
                            return;
                        }
                        sharedPreferences.edit().putString(PLAYER_NAME_FOR_BLUETOOTH_GAME, playerNameFromSharedPrefencesForBluetoohToGame = playerName).commit();
                        Intent intent = new Intent(SelectTypeOfGameActivity.this, BluetoothGameActivity.class);
                        intent.putExtra(BundleKeys.PLAYER_NAME, playerName);
                        startActivity(intent);
                        xoAlertDialog.dismiss();
                    }
                });
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");
    }

    private void showPopupSelectBotLevel() {
        final XOAlertDialog xoAlertDialog = new XOAlertDialog();
        anonymousLoginPopup = xoAlertDialog;
        xoAlertDialog.setContent(R.layout.select_bot_level_popup_layout);
        xoAlertDialog.setContentInitialization(new XOAlertDialog.IContentInitialization() {
            @Override
            public void onContentItialization(View view) {
                TextView textViewEasyLevel = (TextView) view.findViewById(R.id.tv_easy_level);
                textViewEasyLevel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startGameWithBot();
                        xoAlertDialog.dismiss();
                    }
                });
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");
    }

    private void startGameWithBot() {
        Intent intent = new Intent(SelectTypeOfGameActivity.this, GameFieldActivity.class);
        intent.putExtra(BundleKeys.TYPE_OF_GAME, GameType.ANDROID);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_android:
                showPopupSelectBotLevel();
                break;
            case R.id.btn_two_players:
                showPopupForInputNameOfPlayers();
                break;
            case R.id.btn_online:
                showAnonymousPopup();
                break;
            case R.id.btn_bluetooth:
                if (BluetoothAdapter.getDefaultAdapter() == null) {
                    Toast.makeText(SelectTypeOfGameActivity.this,
                            R.string.your_device_doesnt_support_bluetooth, Toast.LENGTH_SHORT).show();
                } else {
                    showPopupForBluetoothGame();
                }
                break;
            default:
                break;
        }
        if (intent != null) startActivity(intent);
    }


    private String generateUUID() {
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String uuid = tManager.getDeviceId();
        return uuid + System.currentTimeMillis();
    }
}
