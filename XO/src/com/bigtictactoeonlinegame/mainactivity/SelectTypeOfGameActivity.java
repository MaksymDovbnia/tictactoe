package com.bigtictactoeonlinegame.mainactivity;

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.activity.*;
import com.bigtictactoeonlinegame.bluetoothgame.*;
import com.bigtictactoeonlinegame.gamefield.*;
import com.bigtictactoeonlinegame.onlinerooms.*;
import com.bigtictactoeonlinegame.popup.*;
import com.config.*;
import com.entity.Player;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.*;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.net.online.*;
import com.net.online.protobuf.*;
import com.utils.*;

import net.protocol.*;

import java.util.*;

public class SelectTypeOfGameActivity extends XOGameActivityWithAds implements OnClickListener {

    private static final String LOG_TAG = SelectTypeOfGameActivity.class.getName();
    private static final String KEY_FOR_SHARED_PREFERENCES = SelectTypeOfGameActivity.class.getCanonicalName();
    private static final String FIRST_PLAYER_NAME = "first_player_name";
    private static final String SECOND_PLAYER_NAME = "second_player_name";
    private static final String PLAYER_NAME_FOR_LOGIN_TO_ONLINE_GAME = "player_name_for_login_to_online_game";
    private static final String PLAYER_NAME_FOR_BLUETOOTH_GAME = "player_name_for_bluetooth_game";
    private static final String PLAYER_UUID_FOR_ONLINE_GAME = "player_uuid_for_online_game";
    private static final int REQUEST_ACHIEVEMENTS = 1000;
    private static final int REQUEST_LEADERBOARD = 1001;

    private SharedPreferences sharedPreferences;
    private String player1NameFromSharedPrefences;
    private String player2NameFromSharedPrefences;
    private String playerNameFromSharedPrefencesForLoginToGame;
    private String playerNameFromSharedPrefencesForBluetoohToGame;
    private String playerUUID;
    private String mUrlForUpdate;

    private Handler handler;
    private ProgressDialog pd;
    private Player mPlayer;

    private OnlineConnectionManager mOnlineGameWorker;
    private GeneralDialog mLoginPopup;
    private boolean mWasPrevClickLeaderBoardButton = false;
    private boolean mWasPrevClickAchievemntsButton = false;
    private long mGooglePlayScore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_of_game_activity_layout);
        sharedPreferences = getSharedPreferences(KEY_FOR_SHARED_PREFERENCES, MODE_PRIVATE);
        initViews();

        mPlayer = new Player();
        createHandler();
    }

    private void initViews() {
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        // findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.btn_leaderboards).setOnClickListener(this);
        findViewById(R.id.btn_achievments).setOnClickListener(this);
        View friend = findViewById(R.id.btn_two_players);
        friend.setOnClickListener(this);
        View online = findViewById(R.id.btn_online);
        online.setOnClickListener(this);
        View android = findViewById(R.id.btn_android);
        android.setOnClickListener(this);
        View bluetooth = findViewById(R.id.btn_bluetooth);
        bluetooth.setOnClickListener(this);
        findViewById(R.id.btn_back_from_select_type_game).setOnClickListener(this);
    }

    @Override
    public AdView getAdView() {
        return null;
    }


    private void createHandler() {
        handler = new Handler() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void handleMessage(Message msg) {
                ProtoType protoType = ProtoType.fromInt(msg.what);
                if (msg.what == 100) {
                    pd.cancel();
                    showToastWithText(getString(R.string.server_not_available));
                    return;
                }
                Log.d(LOG_TAG, "handler received message" + protoType);
                switch (protoType) {
                    case CLOGINTOGAME:
                        Protocol.CLoginToGame cLoginToGame = (Protocol.CLoginToGame) msg.obj;
                        int id = cLoginToGame.getId();
                        mPlayer.setId(id);
                        Controller.getInstance().setPlayer(mPlayer);
                        Logger.printLog("Conected to server with id " + id);
                        pd.cancel();
                        if (mLoginPopup != null) {
                            mLoginPopup.dismiss();
                        }
                        loginToGame();
                        break;
                    case APP_NEED_UPDATE_TO_LAST_VERSION:
                        Protocol.AppNeedUpdateToLastVersion appNeedUpdate = (Protocol.AppNeedUpdateToLastVersion) msg.obj;
                        mUrlForUpdate = appNeedUpdate.getUrlForUpdate();
                        pd.cancel();
                        showNeedAppUpdatePopup();
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
        if (playerUUID != null) {
            mPlayer.setUuid(playerUUID);
        }
    }

    private void loginToGame() {
        Intent intent = new Intent(this, OnlineRoomsActivity.class);
        startActivity(intent);
    }


    private long getGoogleLeaderBoardScore() {

        return mGooglePlayScore;
    }

    private void loadPlayServiceScore() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult = Games.Leaderboards.loadCurrentPlayerLeaderboardScore(getApiClient(),
                        getString(R.string.leaderboard_the_best_online_players), 1, 1).await();
                if (loadPlayerScoreResult != null && loadPlayerScoreResult.getScore() != null) {
                    mGooglePlayScore = loadPlayerScoreResult.getScore().getRawScore();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void showNeedAppUpdatePopup() {
        final XOAlertDialog xoAlertDialog = new XOAlertDialog();
        // mLoginPopup = xoAlertDialog;
        xoAlertDialog.setContent(R.layout.app_need_update_popup_layout);
        xoAlertDialog.setContentInitialization(new XOAlertDialog.IContentInitialization() {
            @Override
            public void onContentItialization(View view) {
                Button updateButton = (Button) view.findViewById(R.id.btn_update_game);
                updateButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrlForUpdate));
                        startActivity(browserIntent);
                    }
                });
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");
    }

    private void showToastWithText(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private String getAppVersion() {
        PackageInfo pInfo = null;
        try {

            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
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

    private View dialogView;
    private GeneralDialog generalDialog;

    public void showPopupForInputNameOfPlayers() {
        generalDialog = new GeneralDialog.Builder(this)
                .setContentId(R.layout.popup_two_players_game)
                .setTitleTextId(R.string.input_players_name)
                .setAutoDissmisDialog(false)
                .setContentInitialization(new GeneralDialog.IContentInitialization() {
                    @Override
                    public void onContentInitialization(View view) {
                        dialogView = view;
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

                    }
                })
                .setPositiveButtonListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SelectTypeOfGameActivity.this, GameFieldActivity.class);
                        String name1 = ((EditText) dialogView.findViewById(R.id.edt_first_player_name)).getText().toString();
                        String name2 = ((EditText) dialogView.findViewById(R.id.edt_second_player_name)).getText().toString();
                        intent.putExtra(GameFieldActivity.FIRST_PLAYER_NAME, name1);
                        intent.putExtra(GameFieldActivity.SECOND_PLAYER_NAME, name2);
                        intent.putExtra(BundleKeys.TYPE_OF_GAME, GameType.FRIEND);
                        sharedPreferences.edit().putString(FIRST_PLAYER_NAME, name1).commit();
                        sharedPreferences.edit().putString(SECOND_PLAYER_NAME, name2).commit();
                        startActivity(intent);
                        generalDialog.dismiss();
                    }
                })

                .build();

        generalDialog.show();


    }

    private EditText playerNameEditText;

    private void showAnonymousPopup() {
        mLoginPopup = new GeneralDialog.Builder(this)
                .setContentId(R.layout.input_player_name_popup_layout)
                .setAutoDissmisDialog(false)
                .setTitleTextId(R.string.online_game)
                .setContentInitialization(new GeneralDialog.IContentInitialization() {
                    @Override
                    public void onContentInitialization(View view) {
                        playerNameEditText = (EditText) view.findViewById(R.id.edt_player_name);
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
                            mPlayer.setUuid(playerUUID);
                        }
                        if (playerNameFromSharedPrefencesForLoginToGame != null)
                            playerNameEditText.setText(playerNameFromSharedPrefencesForLoginToGame);
                        else playerNameEditText.setText(getString(R.string.anonym_player));
                    }
                })
                .setPositiveButtonListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                            mPlayer.setName(playerName);
                            mPlayer.setPlayServiceScore(getGoogleLeaderBoardScore());
                            mPlayer.setRegistrationType(Protocol.RegistrationType.xo);
                            mOnlineGameWorker = new OnlineConnectionManager(handler,
                                    mPlayer, pd);
                            Controller.getInstance().setOnlineWorker(mOnlineGameWorker);
                            mOnlineGameWorker.start();
                            pd.setTitle(getString(R.string.connection));
                            pd.setMessage(getString(R.string.connecting_server));
                            pd.show();
                            // pd.cancel();
                        } else {
                            showToastWithText(getString(R.string.no_internet_connection));
                        }
                    }
                })
                .build();

        mLoginPopup.show();
    }


    private void showPopupForBluetoothGame() {
        mLoginPopup = new GeneralDialog.Builder(this)
                .setContentId(R.layout.input_player_name_popup_layout)
                .setTitleTextId(R.string.bluetooth_game)
                .setAutoDissmisDialog(false)
                .setContentInitialization(new GeneralDialog.IContentInitialization() {
                    @Override
                    public void onContentInitialization(View view) {
                        playerNameEditText = (EditText) view.findViewById(R.id.edt_player_name);
                        playerNameEditText.addTextChangedListener(new MemoTextWatcher() {
                            @Override
                            protected void showMaxAlert() {
                                String text = getString(R.string.maximum_chars_in_nick_is) + MemoTextWatcher.MAX_CHARACTER_COUNT;
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
                    }
                })
                .setPositiveButtonListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String playerName = playerNameEditText.getText().toString();
                        if (playerName.length() == 0) {
                            showToastWithText(getString(R.string.please_enter_your_nick_name));
                            return;
                        }
                        sharedPreferences.edit().putString(PLAYER_NAME_FOR_BLUETOOTH_GAME, playerNameFromSharedPrefencesForBluetoohToGame = playerName).commit();
                        Intent intent = new Intent(SelectTypeOfGameActivity.this, BluetoothGameActivity.class);
                        intent.putExtra(BundleKeys.PLAYER_NAME, playerName);
                        startActivity(intent);
                        mLoginPopup.dismiss();
                    }
                })
                .build();

        mLoginPopup.show();
    }

    private void showPopupSelectBotLevel() {
        final XOAlertDialog xoAlertDialog = new XOAlertDialog();
        // mLoginPopup = xoAlertDialog;
        xoAlertDialog.setContent(R.layout.select_bot_level_popup_layout);
        xoAlertDialog.setContentInitialization(new XOAlertDialog.IContentInitialization() {
            @Override
            public void onContentItialization(View view) {
                TextView textViewEasyLevel = (TextView) view.findViewById(R.id.tv_easy_level);
                textViewEasyLevel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startGameWithAndroid();
                        xoAlertDialog.dismiss();
                    }
                });
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");
    }

    private void startGameWithAndroid() {
        Intent intent = new Intent(SelectTypeOfGameActivity.this, GameFieldActivity.class);
        intent.putExtra(BundleKeys.TYPE_OF_GAME, GameType.ANDROID);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_android:
                startGameWithAndroid();
                // showPopupSelectBotLevel();
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
            case R.id.sign_in_button:
                beginUserInitiatedSignIn();
                break;
//            case R.id.sign_out_button:
//                signOut();
//                findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//                findViewById(R.id.sign_out_button).setVisibility(View.GONE);
//                break;
            case R.id.btn_leaderboards:
                if (!isSignedIn()) {
                    mWasPrevClickLeaderBoardButton = true;
                    beginUserInitiatedSignIn();
                } else {
                    startLeaderBoardActivity();
                }
                break;
            case R.id.btn_achievments:
                if (!isSignedIn()) {
                    beginUserInitiatedSignIn();
                    mWasPrevClickAchievemntsButton = true;
                } else {
                    startAchievementsActivity();
                }
                break;
            case R.id.btn_back_from_select_type_game:
                finish();
                break;
            default:
                break;
        }
        if (intent != null) startActivity(intent);
    }

    private void startLeaderBoardActivity() {
        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(),
                getString(R.string.leaderboard_the_best_online_players)), REQUEST_LEADERBOARD);
    }

    private void startAchievementsActivity() {
        startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), REQUEST_ACHIEVEMENTS);
    }


    private String generateUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid + System.currentTimeMillis();
    }

    @Override
    public void onSignInFailed() {
        super.onSignInFailed();
        mWasPrevClickLeaderBoardButton = false;
        mWasPrevClickAchievemntsButton = false;
    }

    @Override
    public void onSignInSucceeded() {
        super.onSignInSucceeded();
        loadPlayServiceScore();

        if (mWasPrevClickAchievemntsButton) {
            startAchievementsActivity();
            mWasPrevClickAchievemntsButton = false;
        }
        if (mWasPrevClickLeaderBoardButton) {
            startLeaderBoardActivity();
            mWasPrevClickLeaderBoardButton = false;
        }
    }

}
