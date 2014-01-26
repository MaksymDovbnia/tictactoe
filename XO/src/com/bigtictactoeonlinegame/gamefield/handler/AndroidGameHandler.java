package com.bigtictactoeonlinegame.gamefield.handler;

import android.app.Activity;

import android.util.Log;
import android.widget.TextView;

import com.bigtictactoeonlinegame.gamefield.GameFieldActivityAction;
import com.bigtictactoeonlinegame.gamefield.GameFieldAdapter;
import com.entity.OneMove;
import com.entity.Player;
import com.entity.TypeOfMove;
import com.bigtictactoeonlinegame.GameType;
import com.bigtictactoeonlinegame.activity.R;
import com.bigtictactoeonlinegame.gamefield.GameFieldItem;
import com.sec.xologic.impl.ILoger;
import com.sec.xologic.impl.LogicLevel;
import com.sec.xologic.impl.OneMoveHolder;
import com.sec.xologic.impl.ResultMoveListener;
import com.sec.xologic.impl.StartNewGame;
import com.sec.xologic.impl.TypeMove;
import com.sec.xologic.impl.log.Logger;

import java.util.List;

/**
 * Created by Maksym on 02.12.13.
 */
public class AndroidGameHandler extends GlobalHandler implements IGameHandler {
    private StartNewGame startNewGame;
    private static final TypeMove DEFAULT_TYPE_MOVE = TypeMove.O;

    private TypeMove logicMoveType = DEFAULT_TYPE_MOVE;
    private TypeMove moveType;
    private Activity activity;

    private ResultMoveListener resultMoveListener = new ResultMoveListener() {
        @Override
        public void resultMove(final OneMoveHolder oneMove) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OneMove oneMove1 = new OneMove(oneMove.getI(), oneMove.getJ(), opponent.getMoveType());
                    Log.d(Logger.TAG, "resultMove" + oneMove);
                    gameFieldAdapter.showOneMove(oneMove1);
                    gameFieldAdapter.setEnableAllUnusedGameField(true);
                    List<OneMove> list = gameFieldWinLineHandler.oneMove(oneMove1);
                    if (list != null) {
                        wonGame(list);
                    }
                    changeIndicator();
                }
            });

        }
    };

    public AndroidGameHandler(Player player, Player opponent, GameFieldActivityAction activityAction,
                             Activity activity) {
        super(player, opponent, activityAction);
        this.activity = activity;
        logicMoveType = TypeMove.O;
        moveType = TypeMove.X;
        startNewGame = new StartNewGame(LogicLevel.EASY, resultMoveListener, logicMoveType);
        startNewGame.setLogger(new ILoger() {
            @Override
            public void logMessage(String s, String s2) {
                Log.d(s, s2);
            }
        });

    }


    @Override
    public void sendMessage(String message) {

    }

    @Override
    protected void wonGame(List<OneMove> list) {

        gameFieldAdapter.drawWinLine(list);
        activityAction.showWonPopup((indicator == FIRST_PLAYER) ? player.getName()
                : opponent.getName());


        if (indicator == FIRST_PLAYER) {
            player1ScoreNum++;
            tvPlayer1Score.setText(player1ScoreNum + "");


        } else {
            player2ScoreNum++;
            tvPlayer2Score.setText(player2ScoreNum + "");


        }


    }

    @Override
    public GameType getGameType() {
        return GameType.ANDROID;
    }


    @Override
    public List<OneMove> performedOneMove(OneMove oneMove) {
        List<OneMove> list = gameFieldWinLineHandler.oneMove(oneMove);


        if (list != null) {
            wonGame(list);
        }
        startNewGame.nextMove(
                new OneMoveHolder(oneMove.i, oneMove.j,
                        moveType));
        return list;
    }

    @Override
    public GameFieldItem.FieldType occurredMove(int i, int j) {
        GameFieldItem.FieldType type = null;
        OneMove oneMove = null;
        if (indicator == FIRST_PLAYER) {
            type = (player.getMoveType() == TypeOfMove.X) ? GameFieldItem.FieldType.X
                    : GameFieldItem.FieldType.O;
            oneMove = new OneMove(i, j, player.getMoveType());
        } else if (indicator == SECOND_PLAYER) {
            type = (opponent.getMoveType() == TypeOfMove.X) ? GameFieldItem.FieldType.X
                    : GameFieldItem.FieldType.O;
            oneMove = new OneMove(i, j, opponent.getMoveType());
        }
        gameFieldAdapter.setEnableAllUnusedGameField(false);
        gameFieldAdapter.showOneMove(oneMove);


        performedOneMove(oneMove);
        changeIndicator();

        return type;
    }

    @Override
    public void setAdapter(GameFieldAdapter adapter) {
        this.gameFieldAdapter = adapter;
    }

    @Override
    public void setPlayer1TexView(TextView player1TexView) {
        this.tvPlayer1Name = player1TexView;
        this.tvPlayer1Name.setText(player.getName());
    }

    @Override
    public void setPlayer2TexView(TextView player2TexView) {
        this.tvPlayer2Name = player2TexView;
        this.tvPlayer2Name.setText(opponent.getName());
    }

    @Override
    public void setPlayer1ScoreTextView(TextView score1TexView) {
        tvPlayer1Score = score1TexView;
    }

    @Override
    public void setPlayer2ScoreTextView(TextView score2TexView) {
        tvPlayer2Score = score2TexView;
    }

    @Override
    public void setTimerTextView(TextView timerTexView) {
        tvTimeInsicator = timerTexView;
    }

    @Override
    public void initIndicator() {
        indicator = FIRST_PLAYER;
        tvPlayer1Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
        tvPlayer2Name.setBackgroundResource(R.drawable.button_white);
        player.setMoveType(TypeOfMove.X);
        opponent.setMoveType(TypeOfMove.O);
    }

    @Override
    public void startNewGame() {
        gameFieldWinLineHandler.newGame();
        gameFieldAdapter.startNewGame();
        if (player.getMoveType() == TypeOfMove.X) {
            indicator = SECOND_PLAYER;
            tvPlayer2Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
            tvPlayer1Name.setBackgroundResource(R.drawable.button_white);
            player.setMoveType(TypeOfMove.O);
            opponent.setMoveType(TypeOfMove.X);
            logicMoveType = TypeMove.X;
            moveType = TypeMove.O;
            gameFieldAdapter.setEnableAllUnusedGameField(false);
            startNewGame.setNextMoveListener(null);
            startNewGame = new StartNewGame(LogicLevel.EASY, resultMoveListener, logicMoveType);
            startNewGame.makeFirstMove();

        } else {
            indicator = FIRST_PLAYER;
            player.setMoveType(TypeOfMove.X);
            opponent.setMoveType(TypeOfMove.O);
            logicMoveType = TypeMove.O;
            moveType = TypeMove.X;
            startNewGame.setNextMoveListener(null);
            startNewGame = new StartNewGame(LogicLevel.EASY, resultMoveListener, logicMoveType);
            tvPlayer1Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
            tvPlayer2Name.setBackgroundResource(R.drawable.button_white);
        }
    }

    @Override
    public void exitFromGame() {

    }

    @Override
    public void setActivityAction(GameFieldActivityAction activityAction) {

    }

    @Override
    public void unregisterHandler() {

    }
}
