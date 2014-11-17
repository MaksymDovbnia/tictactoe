package com.bigtictactoeonlinegame.gamefield;

import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.activity.*;
import com.bigtictactoeonlinegame.chat.*;
import com.bigtictactoeonlinegame.gamefield.handler.*;
import com.bigtictactoeonlinegame.mainactivity.*;
import com.bigtictactoeonlinegame.popup.*;
import com.bluetooth.protocol.*;
import com.config.*;
import com.entity.*;
import com.google.android.gms.games.Games;
import com.utils.Mathematics;

import net.protocol.*;

public class GameFieldActivity extends GoogleAnalyticsWithPlayServiceActivity implements OnClickListener,
        GameFieldActivityAction, IChatActionNotification {
    public static final String FIRST_PLAYER_NAME = "first_player_name";
    public static final String SECOND_PLAYER_NAME = "second_player_name";
    private static final String OPPONENT_EXIT_FROM_GAME_POPUP_TAG = "opponent_exit_from_game";
    private FragmentTransaction fragmentTransaction;
    private Fragment gameFieldFragment, chatFragment;
    private ChatAction chatAction;
    private Player opponent;
    private Player player;
    private String opponentName;

    private TextView firstNameTextView;
    private TextView secondNameTextView;
    private TextView firstPlayeScoreTextView;
    private TextView secondPlayserScoreTextView;
    private String playerName;


    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
    }

    private enum TAB {GAME, CHAT}


    private BlickingButton openChatButton;
    private Button mButtonnewGame;
    private TAB cureentTab;
    private GameType gameType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        gameType = (GameType) intent.getSerializableExtra(BundleKeys.TYPE_OF_GAME);
        setContentView(R.layout.game_fileld_activity_layout);
        mButtonnewGame = (Button) findViewById(R.id.btn_game_field_new_game);
        firstNameTextView = (TextView) findViewById(R.id.first_user_name);
        secondNameTextView = (TextView) findViewById(R.id.second_user_name);
        firstPlayeScoreTextView = (TextView) findViewById(R.id.first_player_score);
        secondPlayserScoreTextView = (TextView) findViewById(R.id.second_player_score);
        mButtonnewGame.setOnClickListener(this);
        findViewById(R.id.btn_game_field_back).setOnClickListener(this);

        openChatButton = (BlickingButton) findViewById(R.id.btn_chat);
        openChatButton.setOnClickListener(this);

        playerName = getString(R.string.player);
        Player player = new Player();
        Player opponent1 = new Player();
        boolean isFirst = true;
        if (gameType != null) {
            switch (gameType) {
                case ONLINE:
                    Player opponent = (Player) intent.getSerializableExtra(BundleKeys.OPPONENT);
                    this.opponent = opponent;
                    TypeOfMove typeOfMove = (TypeOfMove) intent.getSerializableExtra(BundleKeys.TYPE_OF_MOVE);
                    isFirst = typeOfMove == TypeOfMove.X;
                    OnlineGameModel onlineGameHandler = new OnlineGameModel(
                            Controller.getInstance().getOnlineWorker(),
                            Controller.getInstance().getPlayer(), opponent, this, (typeOfMove == TypeOfMove.X));
                    Controller.getInstance().setGameModel(onlineGameHandler);
                    mButtonnewGame.setEnabled(false);
                    mButtonnewGame.setBackgroundResource(R.drawable.button_empty);
                    break;
                case FRIEND:
                    if (intent.getStringExtra(FIRST_PLAYER_NAME) != null) {
                        playerName = intent.getStringExtra(FIRST_PLAYER_NAME);
                    }
                    if (intent.getStringExtra(SECOND_PLAYER_NAME) != null) {
                        opponentName = intent.getStringExtra(SECOND_PLAYER_NAME);
                    }
                    firstNameTextView.setText(playerName);
                    secondNameTextView.setText(opponentName);
                    player.setName(playerName);
                    opponent1.setName(opponentName);
                    FriendGameModel friendGameHandler = new FriendGameModel(player, opponent1, this);
                    Controller.getInstance().setGameModel(friendGameHandler);
                    Controller.getInstance().setPlayer(player);
                    openChatButton.setEnabled(false);
                    openChatButton.setBackgroundResource(R.drawable.button_empty);
                    break;
                case ANDROID:
                    if (intent.getStringExtra(FIRST_PLAYER_NAME) != null) {
                        playerName = intent.getStringExtra(FIRST_PLAYER_NAME);
                    }
                    opponentName = getString(R.string.android);
                    if (intent.getStringExtra(FIRST_PLAYER_NAME) != null) {
                        playerName = intent.getStringExtra(FIRST_PLAYER_NAME);
                    }
                    if (intent.getStringExtra(SECOND_PLAYER_NAME) != null) {
                        opponentName = intent.getStringExtra(SECOND_PLAYER_NAME);
                    }
                    player.setName(playerName);
                    opponent1.setName(opponentName);
                    AndroidGameModel androidGameHandler = new AndroidGameModel(player, opponent1, this,
                            this);
                    Controller.getInstance().setGameModel(androidGameHandler);
                    Controller.getInstance().setPlayer(player);
                    secondNameTextView.setText(opponentName);
                    firstNameTextView.setText("User");
                    openChatButton.setEnabled(false);
                    openChatButton.setBackgroundResource(R.drawable.button_empty);
                    break;
                case BLUETOOTH:
                    isFirst = getIntent().getBooleanExtra(BundleKeys.IS_PLAYER_MOVE_FIRST, false);
                    BluetoothGameModel bluetoothGameHandler = new BluetoothGameModel(player, opponent1,
                            this, Controller.getInstance().getBluetoothService(), isFirst);
                    if (intent.getStringExtra(BundleKeys.PLAYER_NAME) != null) {
                        playerName = intent.getStringExtra(BundleKeys.PLAYER_NAME);
                    }
                    if (intent.getStringExtra(BundleKeys.OPPONENT_NAME) != null) {
                        opponentName = intent.getStringExtra(BundleKeys.OPPONENT_NAME);
                    }
                    player.setName(playerName);
                    opponent1.setName(opponentName);
                    this.opponent = opponent1;
                    Controller.getInstance().setPlayer(player);
                    Controller.getInstance().setGameModel(bluetoothGameHandler);
                    mButtonnewGame.setEnabled(false);
                    mButtonnewGame.setBackgroundResource(R.drawable.button_empty);
                    break;
            }
        }
        this.player = Controller.getInstance().getPlayer();

        cureentTab = TAB.GAME;
        gameFieldFragment = GameFieldFragment.newInstance(isFirst);
        chatFragment = new ChatFragment();
        chatAction = (ChatAction) chatFragment;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.center_for_fragment, chatFragment);
        fragmentTransaction.add(R.id.center_for_fragment, gameFieldFragment);
        fragmentTransaction.hide(chatFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void showWonPopup(boolean isPlayerWin) {
        String title = isPlayerWin ? playerName : opponentName + " " + getString(R.string.won);
        GeneralDialog generalDialog = new GeneralDialog.Builder(this)
                .setTitleText(title)
                .setContentText(getString(R.string.are_you_want_continue_game))
                .setPositiveButtonListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newGame();
                    }
                })
                .setNegativeButtonListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Controller.getInstance().getGameModel().exitFromGame();
                        finish();
                    }
                })
                .build();
        generalDialog.show();

    }

    private void newGame() {
        if (gameFieldFragment != null) {
            IGameFieldFragmentAction iGameFieldFragmentAction = (IGameFieldFragmentAction) gameFieldFragment;
            iGameFieldFragmentAction.beginNewGame();
        }
    }

    @Override
    public void opponentExitFromGame() {
        Fragment popup = getSupportFragmentManager().findFragmentByTag(OPPONENT_EXIT_FROM_GAME_POPUP_TAG);
        if (popup == null || !popup.isAdded()) {
            XOAlertDialog xoAlertDialog = new XOAlertDialog();
            xoAlertDialog.setAlert_type(XOAlertDialog.ALERT_TYPE.ONE_BUTTON);
            xoAlertDialog.setTile(getResources().getString(R.string.opponent_exit_from_this_game));
            String mainText = opponent.getName() + " " + getString(R.string.left_the_game);
            xoAlertDialog.setMainText(mainText);
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
            xoAlertDialog.show(getSupportFragmentManager(), OPPONENT_EXIT_FROM_GAME_POPUP_TAG);
        }
    }

    @Override
    public void connectionToServerLost() {
        XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setAlert_type(XOAlertDialog.ALERT_TYPE.ONE_BUTTON);
        xoAlertDialog.setTile(getResources().getString(R.string.connection_to_server_lost));
        String mainText = getString(R.string.please_try_to_connect_once_more);
        xoAlertDialog.setMainText(mainText);
        xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.ok));
        xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void receivedChatMessage(ChatMessage chatMessage) {
        if (cureentTab == TAB.GAME) {
            openChatButton.setText(R.string.message);
            openChatButton.setTextColor(getResources().getColor(R.color.blue));
            openChatButton.setNeedingToBlick(true);
        }
        if (chatAction != null) {
            chatAction.receivedMessage(chatMessage);
        }
    }

    @Override
    public IGooglePlayServiceProvider getPlayServiceProvider() {
        return new IGooglePlayServiceProvider() {
            @Override
            public void wonOneGameVsAndroid() {
                if (isSignedIn()) {
                    Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_winner_android_easy));
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_young_player), 1);
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_good_player), 1);
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_very_good_player), 1);
                }
            }

            @Override
            public void winOneGameViaBluetooth() {
                if (isSignedIn()) {
                    Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_winner_bluetooth_game));
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_young_bluetooth_player), 1);
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_good_online_player), 1);
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_very_good_bluetooth_player), 1);
                }
            }

            @Override
            public void winOneGameViaOnline(long playerScoreInPS, long opponentScorePS) {
                if (isSignedIn()) {
                    long newRating = Mathematics.calculateWinPoints(playerScoreInPS, opponentScorePS) + playerScoreInPS;
                    Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_winner_online_game));
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_young_online_player), 1);
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_good_online_player), 1);
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_very_good_online_player), 1);
                    Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_the_best_online_players), newRating);
                }
            }

            @Override
            public void lostOneGameViaOnline(long playerScoreInPS, long opponentScorePS) {
                if (isSignedIn()) {
                    long newRating = Mathematics.calculateLostPoints(opponentScorePS, playerScoreInPS) + playerScoreInPS;
                    if (newRating > 0) {
                        Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_the_best_online_players), newRating);
                    } else {
                        Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_the_best_online_players), 0);
                    }
                }
            }
        };
    }

    @Override
    public GameScoreSettable getGameScoreSettable() {
        return new GameScoreSettable() {
            @Override
            public void setFirstPlayerScore(int score) {
                firstPlayeScoreTextView.setText(String.valueOf(score));
            }

            @Override
            public void setSecondPlayerScore(int score) {
                secondPlayserScoreTextView.setText(String.valueOf(score));
            }
        };
    }

    private void switchToTab(TAB tab) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        cureentTab = tab;
        switch (tab) {
            case GAME:

                openChatButton.setSelected(false);
                fragmentTransaction.show(gameFieldFragment);
                fragmentTransaction.hide(chatFragment);
                break;
            case CHAT:

                openChatButton.setSelected(true);
                fragmentTransaction.show(chatFragment);
                fragmentTransaction.hide(gameFieldFragment);
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_opened_online_group:
                switchToTab(TAB.GAME);
                break;
            case R.id.btn_chat:
                openChatButton.setText(R.string.chat);
                openChatButton.setTextColor(getResources().getColor(R.color.black));
                openChatButton.setNeedingToBlick(false);
                switchToTab(TAB.CHAT);
                break;
            case R.id.btn_game_field_back:
                showExitFromThisGamePopup();
                break;
            case R.id.btn_game_field_new_game:
                newGame();
                break;
        }
    }

    private void showExitFromThisGamePopup() {
        GeneralDialog generalDialog = new GeneralDialog.Builder(this)
                .setTitleTextId(R.string.exit_from_this_game)
                .setContentText(getResources().getString(R.string.exit_from_this_game_question))
                .setPositiveButtonListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Controller.getInstance().getGameModel().exitFromGame();
                        finish();
                    }
                })
                .build();
        generalDialog.show();

    }

    @Override
    public void actionSendChatMessage(ChatMessage chatMessage) {
        IGameModel gameModel = Controller.getInstance().getGameModel();
        if (gameModel.getGameType() == GameType.BLUETOOTH) {
            Controller.getInstance().getBluetoothService().sentPacket(BluetoothProtocol.ChatMessage
                    .newBuilder()
                    .setMessage(chatMessage.getMessage())
                    .build());
        } else if (gameModel.getGameType() == GameType.ONLINE) {
            Controller.getInstance().getOnlineWorker().sendPacket(Protocol.SChatMessage
                    .newBuilder().setMessage(chatMessage.getMessage())
                    .setOpponentId(opponent.getId())
                    .setPlayerId(Controller.getInstance().getPlayer().getId())
                    .build());
        }
    }

    @Override
    public String getPlayerName() {
        return Controller.getInstance().getPlayer().getName();
    }

    @Override
    public void onBackPressed() {
        if (cureentTab == TAB.CHAT) {
            switchToTab(TAB.GAME);
        } else {
            showExitFromThisGamePopup();
        }
    }

    @Override
    protected void onDestroy() {
        openChatButton.stopTaskForBleak();
//        GameFieldItem.destroyAllBitmaps();
        Controller.getInstance().setGameModel(null);
        if (gameType != GameType.ONLINE) {
            Controller.getInstance().setPlayer(null);
        }
        super.onDestroy();

    }
}
