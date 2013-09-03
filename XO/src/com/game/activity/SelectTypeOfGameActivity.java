package com.game.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.entity.Font;
import com.game.Controler;
import com.game.handler.FriendGameHandler;

public class SelectTypeOfGameActivity extends Activity implements OnClickListener,
        OnTouchListener {
    private static final String KEY = SelectTypeOfGameActivity.class.getCanonicalName();
    private static final String FIRST_PLAYER_NAME = "first_player_name";
    private static final String SECOND_PLAYER_NAME = "second_player_name";
    private Button friend;
    private Button online;
    private Button android;
    private Button bluetooth;
    private static final int OFFLINE_GAME_POPUP = 1;
    private SharedPreferences sharedPreferences;
    private String player1NameFromSharedPrefences;
    private String player2NameFromSharedPrefences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_of_game);
        sharedPreferences = getSharedPreferences(KEY, MODE_PRIVATE);

        player1NameFromSharedPrefences = sharedPreferences.getString(FIRST_PLAYER_NAME, null);
        player2NameFromSharedPrefences = sharedPreferences.getString(SECOND_PLAYER_NAME, null);


        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/acquestscript.ttf");
        LinearLayout l = (LinearLayout) findViewById(R.id.LL_typeofgamenu);
        Font.setAppFont(l, mFont);

        friend = (Button) findViewById(R.id.button_gametypes_friend);
        friend.setOnClickListener(this);
        friend.setOnTouchListener(this);

        online = (Button) findViewById(R.id.button_gametypes_online);
        online.setOnClickListener(this);
        online.setOnTouchListener(this);

        android = (Button) findViewById(R.id.button_gametypes_android);
        android.setOnClickListener(this);
        bluetooth = (Button) findViewById(R.id.button_gametypes_bluetooth);
        bluetooth.setOnClickListener(this);
        //	android.setOnTouchListener(this);

    }

    AlertDialog alertDialog = null;

    @Override
    protected Dialog onCreateDialog(final int id) {
        Rect displayRectangle = new Rect();
        Window window = this.getWindow();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        switch (id) {
            case OFFLINE_GAME_POPUP:
                final View view = inflater.inflate(R.layout.popup_offline_players_name, null);
                EditText playerName1;
                EditText playerName2;
                playerName1 = ((EditText) view.findViewById(R.id.edt_first_player_name));
                playerName2 = ((EditText) view.findViewById(R.id.edt_second_player_name));
                if (player1NameFromSharedPrefences != null) {
                    playerName1.setText(player1NameFromSharedPrefences);
                    playerName2.setText(player2NameFromSharedPrefences);
                }
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                view.setMinimumWidth((int) (displayRectangle.width() * 0.5f));
                view.setMinimumHeight((int) (displayRectangle.height() * 0.5f));
                builder.setView(view);
                view.findViewById(R.id.btn_start_offline_game).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendGameHandler f = new FriendGameHandler();
                        Controler.setGameHandler(f);
                        Intent intent = new Intent(SelectTypeOfGameActivity.this, GameFieldActivity.class);
                        String name1 = ((EditText) view.findViewById(R.id.edt_first_player_name)).getText().toString();
                        String name2= ((EditText) view.findViewById(R.id.edt_second_player_name)).getText().toString();

                        intent.putExtra(GameFieldActivity.FIRST_PLAYER_NAME, name1);
                        intent.putExtra(GameFieldActivity.SECOND_PLAYER_NAME, name2);
                        sharedPreferences.edit().putString(FIRST_PLAYER_NAME,name1).commit();
                        sharedPreferences.edit().putString(SECOND_PLAYER_NAME,name2).commit();
                        startActivity(intent);
                        //    alertDialog.cancel();
                    }
                });


        }
        alertDialog = builder.create();
        return alertDialog;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_gametypes_android:
                intent = new Intent(this, OnlineGroupsActivity.class);

                break;
            case R.id.button_gametypes_friend:
                showDialog(OFFLINE_GAME_POPUP);
//                FriendGameHandler f = new FriendGameHandler();
//                Controler.setGameHandler(f);
//                intent = new Intent(this, GameFieldActivity.class);

                break;
            case R.id.button_gametypes_online:

                intent = new Intent(this, OnlineTypeRegistrationActivity.class);
                break;
            case R.id.button_gametypes_bluetooth:

                intent = new Intent(this, BluetoothGameActivity.class);
                break;

            default:
                break;
        }
        if (intent != null) startActivity(intent);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundResource(R.drawable.button_white_withblueline);
                break;
            case MotionEvent.ACTION_UP:
                v.setBackgroundResource(R.drawable.button_white);
                break;

            default:
                break;
        }

        return false;
    }

}
