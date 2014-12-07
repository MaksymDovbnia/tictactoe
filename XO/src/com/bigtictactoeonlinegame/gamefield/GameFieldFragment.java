package com.bigtictactoeonlinegame.gamefield;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;

import android.widget.ImageView;
import android.widget.TextView;
import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.activity.*;
import com.bigtictactoeonlinegame.gamefield.handler.*;

/**
 * Created by Maksym on 9/1/13.
 */
public class GameFieldFragment extends Fragment implements IGameFieldFragmentAction {

    private final static String IS_PLAYER_MOVE_FIRST_KEY = "IS_PLAYER_MOVE_FIRST_KEY";
    private IGameModel gameModel;
    private GameFieldActivityAction gameFieldActivityAction;
    private GameFieldController gameFieldController;
    private ImageView firtMarker;
    private ImageView secondMarker;
    private boolean mIsPlayerMoveFirst;


    public static GameFieldFragment newInstance(boolean isPlayerMoveFirst) {
        GameFieldFragment fragment = new GameFieldFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_PLAYER_MOVE_FIRST_KEY, isPlayerMoveFirst);
        fragment.setArguments(bundle);
        return fragment;
    }



    private IMoveMarker moveMarker = new IMoveMarker() {
        @Override
        public void selectFirst() {
            firtMarker.setSelected(true);
            secondMarker.setSelected(false);
        }

        @Override
        public void selectSecond() {
            secondMarker.setSelected(true);
            firtMarker.setSelected(false);
        }

        @Override
        public void initMaker(boolean isPlayerMoveFirst) {
            if (isPlayerMoveFirst) {
                firtMarker.setBackgroundResource(R.drawable.x_marker_selector);
                secondMarker.setBackgroundResource(R.drawable.o_marker_selector);
            } else {
                firtMarker.setBackgroundResource(R.drawable.o_marker_selector);
                secondMarker.setBackgroundResource(R.drawable.x_marker_selector);
            }
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        gameFieldActivityAction = (GameFieldActivityAction) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mIsPlayerMoveFirst = args.getBoolean(IS_PLAYER_MOVE_FIRST_KEY);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_field_fragment_layout, null);
        Intent intent = getActivity().getIntent();
        firtMarker = (ImageView) view.findViewById(R.id.left_value);
        secondMarker = (ImageView) view.findViewById(R.id.right_value);
        gameModel = Controller.getInstance().getGameModel();


        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        gameFieldController = new GameFieldController((GameFieldView) view.findViewById(R.id.game_field_view), gameModel,
                mIsPlayerMoveFirst, (TextView) view.findViewById(R.id.time), gameFieldActivityAction, moveMarker);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void beginNewGame() {
        gameFieldController.startNewGame();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gameModel.unregisterHandler();
        gameFieldController.onDestroy();
    }


    interface IMoveMarker {
        void selectFirst();

        void selectSecond();

        void initMaker(boolean isPlayerMoveFirst);

    }
}
