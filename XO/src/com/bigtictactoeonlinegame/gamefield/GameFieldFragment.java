package com.bigtictactoeonlinegame.gamefield;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;

import android.widget.TextView;
import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.activity.*;
import com.bigtictactoeonlinegame.gamefield.handler.*;

/**
 * Created by Maksym on 9/1/13.
 */
public class GameFieldFragment extends Fragment implements IGameFieldFragmentAction {

    private static final String FIRST_PLAYER_NAME = "first_player_name";
    private final String SECOND_PLAYER_NAME = "second_player_name";
    private IGameModel gameHandler;
    private GameFieldActivityAction gameFieldActivityAction;
    private GameFieldController gameFieldController;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        gameFieldActivityAction = (GameFieldActivityAction) activity;

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_field_fragment_layout, null);
        Intent intent = getActivity().getIntent();
        gameHandler = Controller.getInstance().getGameHandler();
        gameFieldController = new GameFieldController((GameFieldView) view.findViewById(R.id.btn_game_field_view), gameHandler
                , (TextView) view.findViewById(R.id.time), gameFieldActivityAction);

        String firstPlayerName = intent.getStringExtra(FIRST_PLAYER_NAME);
        String secondPlayerName = intent.getStringExtra(SECOND_PLAYER_NAME);

//        GridView gridView = (GridView) view.findViewById(R.id.grid_view_game_field);
//        TextView textViewPlayer1 = ((TextView) view.findViewById(R.id.tv_field_item));
//        textViewPlayer1.setText(firstPlayerName);
//        TextView textViewSecond = ((TextView) view.findViewById(R.id.tv_second_player_name));
//        textViewSecond.setText(secondPlayerName);
//        final LinearLayout containerGameFiled = (LinearLayout) view.findViewById(R.id.game_field_container);
//        final FrameLayout frame = (FrameLayout) view.findViewById(R.id.horizontal_scroll_game_field);

//        textViewSecond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });

/*        ViewTreeObserver vto = containerGameFiled.getViewTreeObserver();


        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //    containerGameFiled.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                width = containerGameFiled.getMeasuredWidth();
                height = containerGameFiled.getMeasuredHeight();


            }
        });*/


//        GameFieldAdapter gameFieldAdapter = new GameFieldAdapter(getActivity(), gameHandler,
//                (HorizontalScrollView) view
//                .findViewById(R.id.horizontal_scroll_game_field),
//                (ScrollView) view.findViewById(R.id.vertical_scroll_game_field));
//        gameHandler
//                .setPlayer1TextView((TextView) view.findViewById(R.id.tv_field_item));
//        gameHandler
//                .setPlayer2TextView((TextView) view.findViewById(R.id.tv_second_player_name));
//        gameHandler
//                .setPlayer1ScoreTextView((TextView) view.findViewById(R.id.tv_score_player_1));
//        gameHandler
//                .setPlayer2ScoreTextView((TextView) view.findViewById(R.id.tv_score_player_2));
//        gameHandler.setTimerTextView((TextView) view.findViewById(R.id.tv_timer));
//
//        if (gameHandler.getGameType() == GameType.ANDROID || gameHandler.getGameType() == GameType.FRIEND) {
//            view.findViewById(R.id.tv_timer).setVisibility(View.GONE);
//            view.findViewById(R.id.tv_sec).setVisibility(View.GONE);
//        }
        // gameHandler.setAdapter(gameFieldAdapter);
        //  gridView.setAdapter(gameFieldAdapter);

//        gridView.post(new Runnable() {
//            @Override
//            public void run() {
//                gameHandler.initIndicator();
//            }
//        });
        return view;

    }

    @Override
    public void beginNewGame() {
        gameFieldController.startNewGame();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gameHandler.unregisterHandler();
    }
}
