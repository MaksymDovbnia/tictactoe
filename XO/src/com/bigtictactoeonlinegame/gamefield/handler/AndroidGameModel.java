package com.bigtictactoeonlinegame.gamefield.handler;

import android.app.Activity;

import android.util.Log;

import com.bigtictactoeonlinegame.gamefield.GameFieldActivityAction;

import com.entity.OneMove;
import com.entity.Player;
import com.bigtictactoeonlinegame.GameType;
import com.entity.TypeOfMove;
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
public class AndroidGameModel extends GeneralGameModel {
    private StartNewGame androidLogic;
    private static final TypeMove DEFAULT_ANDROID_TYPE_MOVE = TypeMove.O;

    private Activity activity;

    private ResultMoveListener resultMoveListener = new ResultMoveListener() {
        @Override
        public void resultMove(final OneMoveHolder oneMove) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TypeOfMove typeOfMove = oneMove.typeMove == TypeMove.O ? TypeOfMove.O : TypeOfMove.X;
                    OneMove oneMove1 = new OneMove(oneMove.getI(), oneMove.getJ(), typeOfMove);
                    Log.d(Logger.TAG, "resultMove" + oneMove);

                    List<OneMove> list = gameFieldModel.oneMove(oneMove1);

                    opponentActionListener.onOpponentPerformMove(oneMove1, list != null);
                    if (list != null) {
                        wonGameListener.onGameWin(list);
                    }

                }
            });

        }
    };

    public AndroidGameModel(Player player, Player opponent, GameFieldActivityAction activityAction,
                            Activity activity) {
        super(player, opponent, activityAction);
        this.activity = activity;

        androidLogic = new StartNewGame(LogicLevel.EASY, resultMoveListener, DEFAULT_ANDROID_TYPE_MOVE);
        androidLogic.setLogger(new ILoger() {
            @Override
            public void logMessage(String s, String s2) {
                Log.d(s, s2);
            }
        });

    }


    @Override
    public GameType getGameType() {
        return GameType.ANDROID;
    }


    @Override
    public void userMadeMove(OneMove oneMove) {
        List<OneMove> list = gameFieldModel.oneMove(oneMove);
        if (list != null) {
            wonGameListener.onGameWin(list);
        } else {
            androidLogic.nextMove(getOneMoveHolder(oneMove));
        }
    }

    private OneMoveHolder getOneMoveHolder(OneMove oneMove) {
        TypeMove typeMove = (oneMove.type == TypeOfMove.O) ? TypeMove.O : TypeMove.X;
        return new OneMoveHolder(oneMove.i, oneMove.j, typeMove);
    }


    @Override
    public void startNewGame() {
        gameFieldModel.newGame();

    }

    @Override
    public void exitFromGame() {

    }


    @Override
    public void unregisterHandler() {

    }

    @Override
    public void userTimeForMoveEnd() {
        androidLogic.timeInOpponentForMoveFinished();
    }
}
