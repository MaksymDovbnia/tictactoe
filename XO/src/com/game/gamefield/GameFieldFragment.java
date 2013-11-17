package com.game.gamefield;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.game.Controller;
import com.game.activity.R;
import com.game.gamefield.handler.GameHandler;

/**
 * Created by Maksym on 9/1/13.
 */
public class GameFieldFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, IGameFieldFragmentAction {

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
        this.activity = activity;
    }

    int width;
    int height;
    double K = 1.5;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.game_field_fragment_layout, null);
        gridView = (GridView) view.findViewById(R.id.grid_view_game_field);
        Intent intent = activity.getIntent();
        String firstPlayerName = intent.getStringExtra(FIRST_PLAYER_NAME);
        String secondPlayerName = intent.getStringExtra(SECOND_PLAYER_NAME);
        ((TextView) view.findViewById(R.id.tv_field_item)).setText(firstPlayerName);
        TextView textView = ((TextView) view.findViewById(R.id.tv_second_player_name));
        textView.setText(secondPlayerName);
        final LinearLayout containerGameFiled = (LinearLayout) view.findViewById(R.id.game_field_container);
        final FrameLayout frame  = (FrameLayout) view.findViewById(R.id.frame);
        final FrameLayout frame2  = (FrameLayout) view.findViewById(R.id.frame2);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                gridView.setScaleX(2);
                gridView.setScaleY(2);
                frame.getLayoutParams().width = frame.getLayoutParams().width+1000;
                frame2.getLayoutParams().height = frame.getLayoutParams().height+1000;
                frame.invalidate();
                frame2.invalidate();

            }
        });

        ViewTreeObserver vto = containerGameFiled.getViewTreeObserver();



        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                containerGameFiled.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                width = containerGameFiled.getMeasuredWidth();
                height = containerGameFiled.getMeasuredHeight();


            }
        });


        gameHandler = Controller.getInstance().getGameHandler();

        gameFieldAdapter = new GameFieldAdapter(activity, gameHandler);
        gameHandler
                .setPlayer1TexView((TextView) view.findViewById(R.id.tv_field_item));
        gameHandler
                .setPlayer2TexView((TextView) view.findViewById(R.id.tv_second_player_name));
        gameHandler
                .setPlayer1ScoreTextView((TextView) view.findViewById(R.id.tv_score_player_1));
        gameHandler
                .setPlayer2ScoreTextView((TextView) view.findViewById(R.id.tv_score_player_2));
        gameHandler.setTimerTextView((TextView) view.findViewById(R.id.tv_timer));

        gameHandler.setAdapter(gameFieldAdapter);
        gridView.setAdapter(gameFieldAdapter);

        gridView.post(new Runnable() {
            @Override
            public void run() {
                gameHandler.initIndicator();
            }
        });
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
        gameHandler.startNewGame();
    }


    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onDestroy() {
        gameHandler.unregisterHandler();
        super.onDestroy();
    }
}
