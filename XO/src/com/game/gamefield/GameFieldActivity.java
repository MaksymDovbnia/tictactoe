package com.game.gamefield;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.config.BundleKeys;
import com.entity.Player;
import com.entity.TypeOfMove;
import com.game.Controller;
import com.game.GameType;
import com.game.activity.R;
import com.game.fragments.GroupChatFragment;
import com.game.gamefield.handler.FriendGameHandler;
import com.game.gamefield.handler.OnlineGameHandler;
import com.game.popup.XOAlertDialog;

public class GameFieldActivity extends FragmentActivity implements OnClickListener, GameFieldActivityAction {
    public static final String FIRST_PLAYER_NAME = "first_player_name";
    public static final String SECOND_PLAYER_NAME = "second_player_name";
    private FragmentTransaction fragmentTransaction;
    private Fragment gameFieldFragment, chatFragment;
    private Player opponent;

    private enum TAB {GAME, CHAT}

    ;
    private Button openGroup;
    private Button openChat;
    private Button newGame;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        GameType gameType = (GameType) intent.getSerializableExtra(BundleKeys.TYPE_OF_GAME);
        setContentView(R.layout.game_fileld_activity_layout);
        newGame = (Button)    findViewById(R.id.btn_game_field_new_game);
        newGame.setOnClickListener(this);
        findViewById(R.id.btn_game_field_back).setOnClickListener(this);
        openGroup = (Button) findViewById(R.id.btn_group_chat);
        openChat = (Button) findViewById(R.id.btn_opened_online_group);
        openChat.setOnClickListener(this);
        openGroup.setOnClickListener(this);

        if (gameType != null && gameType == GameType.ONLINE) {
            Player opponent = (Player) intent.getSerializableExtra(BundleKeys.OPPONENT);
            this.opponent = opponent;
            TypeOfMove typeOfMove =    (TypeOfMove) intent.getSerializableExtra(BundleKeys.TYPE_OF_MOVE);
            OnlineGameHandler onlineGameHandler = new OnlineGameHandler(
                    Controller.getInstance().getOnlineWorker(), Controller.getInstance().getPlayer(), opponent, this, (typeOfMove == TypeOfMove.X), null);
            Controller.getInstance().setGameHandler(onlineGameHandler);
            onlineGameHandler.setActivityAction(this);
            newGame.setEnabled(false);
            newGame.setText("");
        }
        else if (gameType != null && gameType == GameType.FRIEND){
            String playerName = "";
            String opponentName = "";
            if(intent.getStringExtra(FIRST_PLAYER_NAME) != null){
              playerName =  intent.getStringExtra(FIRST_PLAYER_NAME);
            }

            if(intent.getStringExtra(SECOND_PLAYER_NAME) != null){
               opponentName =  intent.getStringExtra(SECOND_PLAYER_NAME);
            }


            Player player = new Player();
            player.setName(playerName);
            Player opponent = new Player();
            opponent.setName(opponentName);
         mediaPlayer = MediaPlayer.create(this, R.raw.draw_sound);;
            FriendGameHandler friendGameHandler = new FriendGameHandler(player, opponent, this, mediaPlayer);
            Controller.getInstance().setGameHandler(friendGameHandler);
        }




        gameFieldFragment = new GameFieldFragment();
        chatFragment = new GroupChatFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.center_for_fragment, chatFragment);
        fragmentTransaction.add(R.id.center_for_fragment, gameFieldFragment);
        fragmentTransaction.hide(chatFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void showWonPopup(String wonPlayerName) {
        final XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setTile(wonPlayerName + " " + getResources().getString(R.string.is_won));
        xoAlertDialog.setMainText(getResources().getString(R.string.are_you_want_continue_game));
        xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.yes));
        xoAlertDialog.setNegativeButtonText(getResources().getString(R.string.no));
        xoAlertDialog.setSleepTimeBeforeShowPopup(500);
        xoAlertDialog.setNegativeListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newGame();


            }
        });

        xoAlertDialog.show(getSupportFragmentManager(), "");


    }

    private void newGame() {
        if (gameFieldFragment != null) {
            IGameFieldFragmentAction iGameFieldFragmentAction = (IGameFieldFragmentAction) gameFieldFragment;
            iGameFieldFragmentAction.beginNewGame();
        }
    }

    @Override
    public void opponentExitFromGame() {
        XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setAlert_type(XOAlertDialog.ALERT_TYPE.ONE_BUTTON);
        xoAlertDialog.setTile(getResources().getString(R.string.exit_from_this_game));
        String mainText = opponent.getName() + " " + getString(R.string.left_the_game);
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


    private void switchToTab(TAB tab) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (tab) {
            case GAME:
                fragmentTransaction.show(gameFieldFragment);
                fragmentTransaction.hide(chatFragment);
                break;
            case CHAT:
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
            case R.id.btn_group_chat:
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
        XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setTile(getResources().getString(R.string.exit_from_this_game));
        xoAlertDialog.setMainText(getResources().getString(R.string.exit_from_this_game_question));
        xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.exit_from_this_game));
        xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Controller.getInstance().getGameHandler().exitFromGame();
                finish();
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");


    }

    @Override
    public void onBackPressed() {
        showExitFromThisGamePopup();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) mediaPlayer.release();
        super.onDestroy();
    }
}