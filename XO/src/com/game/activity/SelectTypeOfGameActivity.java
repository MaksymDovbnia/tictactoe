package com.game.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.entity.Font;
import com.entity.Player;
import com.game.Controler;
import com.game.gamefield.GameFieldActivity;
import com.game.gamefield.handler.FriendGameHandler;
import com.game.popup.XOAlertDialog;
import com.net.online.WorkerOnlineConnection;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;

import net.protocol.Protocol;

public class SelectTypeOfGameActivity extends FragmentActivity implements OnClickListener {

    private static final String TAG = SelectTypeOfGameActivity.class.getCanonicalName();
    private static final String KEY = SelectTypeOfGameActivity.class.getCanonicalName();
    private static final String FIRST_PLAYER_NAME = "first_player_name";
    private static final String SECOND_PLAYER_NAME = "second_player_name";
    private static final String PLAYER_NAME_FOR_LOGIN_TO_ONLINE_GAME = "player_name_for_login_to_online_game";

    private Button friend;
    private Button online;
    private Button android;
    private Button bluetooth;
    private static final int OFFLINE_GAME_POPUP = 1;
    private SharedPreferences sharedPreferences;
    private String player1NameFromSharedPrefences;
    private String player2NameFromSharedPrefences;
    private String playerNameFromSharedPrefencesForLoginToGame;

    private Handler handler;
    private ProgressDialog pd;
    private Player player;
    private EditText loginAnon;
    private WorkerOnlineConnection onlineGameWorker;
    private XOAlertDialog anonymousLoginPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_of_game);
        sharedPreferences = getSharedPreferences(KEY, MODE_PRIVATE);

        player1NameFromSharedPrefences = sharedPreferences.getString(FIRST_PLAYER_NAME, null);
        player2NameFromSharedPrefences = sharedPreferences.getString(SECOND_PLAYER_NAME, null);
        playerNameFromSharedPrefencesForLoginToGame = sharedPreferences.getString(PLAYER_NAME_FOR_LOGIN_TO_ONLINE_GAME, null);

        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/acquestscript.ttf");
        LinearLayout l = (LinearLayout) findViewById(R.id.LL_typeofgamenu);
        Font.setAppFont(l, mFont);

        friend = (Button) findViewById(R.id.button_gametypes_friend);
        friend.setOnClickListener(this);

        online = (Button) findViewById(R.id.button_gametypes_online);
        online.setOnClickListener(this);

        android = (Button) findViewById(R.id.button_gametypes_android);
        android.setOnClickListener(this);
        bluetooth = (Button) findViewById(R.id.button_gametypes_bluetooth);
        bluetooth.setOnClickListener(this);

        createHandler();

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
                        Controler.setPlayer(player);
                        Loger.printLog("Conected to server with id " + id);
                        pd.cancel();
                        if (anonymousLoginPopup != null) anonymousLoginPopup.dismiss();
                        loginToGame();
                        break;
                }
            }
        };


    }

    private void loginToGame() {
        Intent intent = new Intent(this, OnlineGroupsActivity.class);
        startActivity(intent);
    }

    private void showToastWithText(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
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
                if (player1NameFromSharedPrefences != null) {
                    playerName1.setText(player1NameFromSharedPrefences);
                    playerName2.setText(player2NameFromSharedPrefences);
                    view.findViewById(R.id.btn_start_offline_game).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FriendGameHandler f = new FriendGameHandler();
                            Controler.setGameHandler(f);
                            Intent intent = new Intent(SelectTypeOfGameActivity.this, GameFieldActivity.class);
                            String name1 = ((EditText) view.findViewById(R.id.edt_first_player_name)).getText().toString();
                            String name2 = ((EditText) view.findViewById(R.id.edt_second_player_name)).getText().toString();

                            intent.putExtra(GameFieldActivity.FIRST_PLAYER_NAME, name1);
                            intent.putExtra(GameFieldActivity.SECOND_PLAYER_NAME, name2);
                            sharedPreferences.edit().putString(FIRST_PLAYER_NAME, name1).commit();
                            sharedPreferences.edit().putString(SECOND_PLAYER_NAME, name2).commit();
                            startActivity(intent);
                            xoAlertDialog.dismiss();
                        }
                    });
                }

            }
        });

        xoAlertDialog.show(getSupportFragmentManager(), "");
    }

    private void showAnonymousPopup() {
        final XOAlertDialog xoAlertDialog = new XOAlertDialog();
        anonymousLoginPopup = xoAlertDialog;
        xoAlertDialog.setContent(R.layout.anonymous_popup_layout);
        xoAlertDialog.setContentInitialization(new XOAlertDialog.IContentInitialization() {
            @Override
            public void onContentItialization(View view) {
                final EditText playerNameEditText = (EditText) view.findViewById(R.id.edt_anonymous_player_name);
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
                            player = new Player();
                            player.setName(playerName);
                            player.setRegistrationType(Protocol.RegistrationType.annonymous);
                            onlineGameWorker = new WorkerOnlineConnection(handler,
                                    player, pd);
                            Controler.setOnl(onlineGameWorker);
                            onlineGameWorker.start();
                            pd.setTitle(getString(R.string.connection));
                            pd.setMessage(getString(R.string.connecting));
                            pd.show();
                            // pd.cancel();
                        } else
                            showToastWithText("No connection, please will make connection and reaped");
                    }
                });
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_gametypes_android:
                break;
            case R.id.button_gametypes_friend:
                showPopupForInputNameOfPlayers();
                break;
            case R.id.button_gametypes_online:
                showTypeOfLoginToGamePopup();
                break;
            case R.id.button_gametypes_bluetooth:
                intent = new Intent(this, BluetoothGameActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) startActivity(intent);

    }


}
