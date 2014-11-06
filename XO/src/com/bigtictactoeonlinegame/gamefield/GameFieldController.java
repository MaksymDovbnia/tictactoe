/*
 * Copyright (c) 2000~2013  Samsung Electronics, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Samsung Electronics, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Samsung Electronics.
 */

package com.bigtictactoeonlinegame.gamefield;

import android.widget.TextView;
import com.bigtictactoeonlinegame.GameType;
import com.bigtictactoeonlinegame.gamefield.handler.IGameModel;
import com.bigtictactoeonlinegame.gamefield.handler.IOpponentActionListener;
import com.bigtictactoeonlinegame.gamefield.handler.IWonGameListener;
import com.entity.OneMove;
import com.entity.TypeOfMove;

import java.util.List;

/**
 * @author Maksim Dovbnya(m.dovbnya@samsung.com).
 */
public class GameFieldController implements IWonGameListener, IOpponentActionListener {
    private static final int TIME_FOR_MOVE_IN_SEC = 60;
    private GameFieldView gameFieldView;
    private GameFieldView.FieldType currentFieldType = GameFieldView.FieldType.X;
    private IGameModel gameModel;
    private OneMoveTimer moveTimer;
    private boolean isPlayerMoveFirst;
    private TextView timeTextView;
    private GameType gameType;
    private IGooglePlayServiceProvider playServiceProvider;
    private GameFieldActivityAction gameFieldActivityAction;

    public GameFieldController(final GameFieldView gameFieldView, final IGameModel gameModel, boolean isPlayerMoveFirst,
                               TextView timeTextView, GameFieldActivityAction gameFieldActivityAction) {
        this.gameFieldView = gameFieldView;
        this.gameModel = gameModel;
        this.isPlayerMoveFirst = isPlayerMoveFirst;
        this.timeTextView = timeTextView;
        this.gameFieldActivityAction = gameFieldActivityAction;
        this.playServiceProvider = gameFieldActivityAction.getPlayServiceProvider();
        init();


    }

    private void init() {
        gameModel.setWonGameListener(this);
        gameModel.setOpponentActionListener(this);
        moveTimer = new OneMoveTimer(TIME_FOR_MOVE_IN_SEC, timerListener);
        gameType = gameModel.getGameType();

        gameFieldView.setFieldItemClickListener(new GameFieldView.FieldItemClickListener() {
            @Override
            public void onFieldItemClick(int i, int j) {
                gameFieldView.setEnableAllGameField(false);
                gameFieldView.showItem(i, j, currentFieldType);
                gameFieldView.setItemEnable(i, j, false);
                TypeOfMove typeOfMove = (currentFieldType == GameFieldView.FieldType.O) ? TypeOfMove.O : TypeOfMove.X;
                gameModel.userMadeMove(new OneMove(i, j, typeOfMove));
                changeFieldType();
                moveTimer.startNewTimer(false);
            }
        });

        initGameFieldAndTimer();
    }

    private void initGameFieldAndTimer() {
        if (!isPlayerMoveFirst) {
            gameFieldView.setEnableAllGameField(false);
        } else {
            moveTimer.startNewTimer(true);
        }
    }


    public GameFieldController(final GameFieldView gameFieldView, final IGameModel gameModel, TextView timeTextView,
                               GameFieldActivityAction gameFieldActivityAction) {
        this(gameFieldView, gameModel, true, timeTextView, gameFieldActivityAction);
    }

    public void startNewGame() {
        isPlayerMoveFirst = !isPlayerMoveFirst;
        gameModel.startNewGame();
        initGameFieldAndTimer();
    }

    private void changeFieldType() {
        if (currentFieldType == GameFieldView.FieldType.X) {
            currentFieldType = GameFieldView.FieldType.O;
        } else {
            currentFieldType = GameFieldView.FieldType.X;
        }
    }

    public boolean isPlayerWin() {
        return isPlayerMoveFirst && currentFieldType == GameFieldView.FieldType.X;
    }

    @Override
    public void onGameWin(List<OneMove> line) {
        OneMove first = line.get(0);
        OneMove last = line.get(line.size() - 1);
        gameFieldView.showWinLine(first.i, first.j, last.i, last.j);
        gameFieldView.setEnableAllGameField(false);
        moveTimer.unRegisterListenerAndFinish();
        sendDataToPlayService();
        showWinPopup();
    }

    private void showWinPopup() {
        String name = isPlayerWin() ? "You" : "Your opponent";
        gameFieldActivityAction.showWonPopup(name);
    }

    private void sendDataToPlayService() {
        if (isPlayerWin()) {
            switch (gameType) {
                case ANDROID:
                    playServiceProvider.wonOneGameVsAndroid();
                    break;

                case ONLINE:

                    break;

                case BLUETOOTH:
                    playServiceProvider.winOneGameViaBluetooth();
                    break;
            }
        }
    }

    @Override
    public void opponentMoveTimeEnd() {
        gameFieldView.setEnableAllGameField(true);
        changeFieldType();
        moveTimer.startNewTimer(true);

    }

    @Override
    public void onOpponentPerformMove(OneMove oneMove, boolean isLast) {
        gameFieldView.showItem(oneMove.i, oneMove.j, currentFieldType);
        gameFieldView.setItemEnable(oneMove.i, oneMove.j, false);
        if (!isLast) {
            gameFieldView.setEnableAllGameField(true);
            moveTimer.startNewTimer(true);
            changeFieldType();
        }
    }

    private OneMoveTimer.TimerListener timerListener = new OneMoveTimer.TimerListener() {
        @Override
        public void timeChanged(int time) {
            String timeS = "0:";
            if (time < 10) {
                timeS += "0" + time;
            } else {
                timeS += time;
            }

            timeTextView.setText(timeS);
        }

        @Override
        public void timeFinished() {
            changeFieldType();
            gameModel.userTimeForMoveEnd();
            moveTimer.startNewTimer(false);
        }
    };


}
