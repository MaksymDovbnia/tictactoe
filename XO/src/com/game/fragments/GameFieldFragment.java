package com.game.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.game.Controler;
import com.game.activity.IGameFieldFragmentAction;
import com.game.activity.R;
import com.game.adapters.GameFieldAdapter;
import com.game.handler.GameHandler;

/**
 * Created by Maksym on 9/1/13.
 */
public class GameFieldFragment extends Fragment implements View.OnClickListener,View.OnTouchListener, IGameFieldFragmentAction {

    private GridView gridView;
    private GameHandler gameHandler;
    private Handler handler;
    private GameFieldAdapter gameFieldAdapter;
    private Button newGame;
    public static final String FIRST_PLAYER_NAME = "first_player_name";
    public static final String SECOND_PLAYER_NAME = "second_player_name";
    private Activity activity;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
            this.activity=  activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_field_fragment_layout, null);
        gridView = (GridView) view.findViewById(R.id.gridView1);
        Intent intent = activity.getIntent();
        String firstPlayerName = intent.getStringExtra(FIRST_PLAYER_NAME);
        String secondPlayerName = intent.getStringExtra(SECOND_PLAYER_NAME);
        ((TextView) view.findViewById(R.id.tv_first_player_name)).setText(firstPlayerName);
        ((TextView) view.findViewById(R.id.tv_second_player_name)).setText(secondPlayerName);



        gameHandler = Controler.getGameHandler();
        handler = gameHandler.getHandler();
        gameFieldAdapter = new GameFieldAdapter(activity, gameHandler);
        gameFieldAdapter
                .setPlayer1((TextView) view.findViewById(R.id.tv_first_player_name));
        gameFieldAdapter
                .setPlayer2((TextView) view.findViewById(R.id.tv_second_player_name));

        gameHandler.setAdapter(gameFieldAdapter);
        gridView.setAdapter(gameFieldAdapter);
       getFragmentManager().openTransaction();



        return view;


    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setBackgroundResource(R.drawable.button_crinkle);

                break;
            case MotionEvent.ACTION_UP:
                // v.setBackgroundResource(R.drawable.knopka_prosta);
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    public void beginNewGame() {
        gameFieldAdapter.startNewGame();
        gameHandler.startNewGame();
    }
}
