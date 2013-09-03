package com.game.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.game.fragments.GameFieldFragment;
import com.game.fragments.GroupChatFragment;
import com.game.popup.XOAlertDialog;

public class GameFieldActivity extends FragmentActivity implements OnClickListener, GameFieldActivityAction {
    public static final String FIRST_PLAYER_NAME = "first_player_name";
    public static final String SECOND_PLAYER_NAME = "second_player_name";
    private FragmentTransaction fragmentTransaction;
    private Fragment gameFieldFragment, chatFragment;


    @Override
    public void showWonPopup(String wonPlayerName) {
        final XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setTile( wonPlayerName + " " + getResources().getString(R.string.is_won));
        xoAlertDialog.setMainText(getResources().getString(R.string.are_you_want_continue_game));
        xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.yes));
        xoAlertDialog.setNegativeButtonText(getResources().getString(R.string.no));

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


    private enum TAB {GAME, CHAT}

    ;
    private Button openGroup;
    private Button openChat;


    private void newGame() {
        if (gameFieldFragment != null) {
            IGameFieldFragmentAction iGameFieldFragmentAction = (IGameFieldFragmentAction) gameFieldFragment;
            iGameFieldFragmentAction.beginNewGame();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_fileld_activity_layout);
        findViewById(R.id.btn_game_field_new_game).setOnClickListener(this);
        findViewById(R.id.btn_game_field_back).setOnClickListener(this);
        openGroup = (Button) findViewById(R.id.btn_group_chat);
        openChat = (Button) findViewById(R.id.btn_opened_online_group);
        openChat.setOnClickListener(this);
        openGroup.setOnClickListener(this);
        gameFieldFragment = new GameFieldFragment();
        chatFragment = new GroupChatFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.center_for_fragment, chatFragment);
        fragmentTransaction.add(R.id.center_for_fragment, gameFieldFragment);
        fragmentTransaction.hide(chatFragment);
        fragmentTransaction.commit();

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

    private void showExitFromThisGamePopup(){
        XOAlertDialog xoAlertDialog = new XOAlertDialog();
        xoAlertDialog.setTile(getResources().getString(R.string.exit_from_this_game));
        xoAlertDialog.setMainText(getResources().getString(R.string.exit_from_this_game_question));
        xoAlertDialog.setPositiveButtonText(getResources().getString(R.string.exit_from_this_game));
        xoAlertDialog.setPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        xoAlertDialog.show(getSupportFragmentManager(), "");


    }

    @Override
    public void onBackPressed() {
     showExitFromThisGamePopup();
    }
}
