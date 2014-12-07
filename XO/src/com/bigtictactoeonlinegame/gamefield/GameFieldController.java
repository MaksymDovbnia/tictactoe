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

import android.os.Handler;
import android.widget.TextView;
import com.bigtictactoeonlinegame.GameType;
import com.bigtictactoeonlinegame.gamefield.handler.IGameModel;
import com.bigtictactoeonlinegame.gamefield.handler.IOpponentActionListener;
import com.bigtictactoeonlinegame.gamefield.handler.IWonGameListener;
import com.entity.OneMove;
import com.entity.TypeOfMove;

import java.util.Collections;
import java.util.List;


/**
 * @author Maksim Dovbnya(m.dovbnya@samsung.com).
 */
public class GameFieldController implements IWonGameListener, IOpponentActionListener {
    private static final int TIME_FOR_MOVE_IN_SEC = 10;
    private GameFieldView gameFieldView;
    private GameFieldView.FieldType currentFieldType = GameFieldView.FieldType.X;
    private IGameModel gameModel;
    private OneMoveTimer moveTimer;
    private boolean isPlayerMoveFirst;
    private TextView timeTextView;
    private GameType gameType;
    private IGooglePlayServiceProvider playServiceProvider;
    private GameFieldActivityAction gameFieldActivityAction;
    private int firstPlayerScore;
    private int secondPlayerScore;
    private GameFieldFragment.IMoveMarker moveMarker;
    private Handler handler;


    public GameFieldController(final GameFieldView gameFieldView, final IGameModel gameModel, TextView timeTextView,
                               GameFieldActivityAction gameFieldActivityAction, GameFieldFragment.IMoveMarker marker) {
        this(gameFieldView, gameModel, true, timeTextView, gameFieldActivityAction, marker);
    }

    public GameFieldController(final GameFieldView gameFieldView, final IGameModel gameModel, boolean isPlayerMoveFirst,
                               TextView timeTextView, GameFieldActivityAction gameFieldActivityAction, GameFieldFragment.IMoveMarker moveMarker) {
        this.gameFieldView = gameFieldView;
        this.gameModel = gameModel;
        this.isPlayerMoveFirst = isPlayerMoveFirst;
        this.timeTextView = timeTextView;
        this.gameFieldActivityAction = gameFieldActivityAction;
        this.playServiceProvider = gameFieldActivityAction.getPlayServiceProvider();
        this.moveMarker = moveMarker;
        handler = new Handler();
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
                changeFieldType();
                gameModel.userMadeMove(new OneMove(i, j, typeOfMove));
                moveTimer.startNewTimer(false);
            }
        });

        initGameFieldAndTimer();
        initMoveMarker();

    }

    private void initMoveMarker() {
        moveMarker.initMaker(isPlayerMoveFirst);
        if (isPlayerMoveFirst) {
            moveMarker.selectFirst();
        } else {
            moveMarker.selectSecond();
        }
    }


    public void startNewGame() {
        isPlayerWin = false;
        if (gameModel.getGameType() == GameType.ANDROID) {
            gameFieldView.startNewGame();
        }
        isPlayerMoveFirst = !isPlayerMoveFirst;
        gameModel.startNewGame(!isPlayerMoveFirst);
        if (gameModel.getGameType() != GameType.ONLINE && gameModel.getGameType() != GameType.BLUETOOTH) {
            initGameFieldAndTimer();
            initMoveMarker();
            currentFieldType = GameFieldView.FieldType.X;
        }
    }


    @Override
    public void onBothPlayerWantContinue() {
        gameFieldView.startNewGame();
        initGameFieldAndTimer();
        initMoveMarker();
        currentFieldType = GameFieldView.FieldType.X;
    }

    private void initGameFieldAndTimer() {
        if (gameModel.getGameType() == GameType.FRIEND) {
            gameFieldView.setEnableAllGameField(true);
            moveTimer.startNewTimer(true);
            gameFieldView.startNewGame();
            return;
        }

        if (!isPlayerMoveFirst) {
            gameFieldView.setEnableAllGameField(false);
            moveTimer.startNewTimer(false);
        } else {
            gameFieldView.setEnableAllGameField(true);
            moveTimer.startNewTimer(true);
        }
    }

    private void changeFieldType() {
        if (currentFieldType == GameFieldView.FieldType.X) {
            if (!isPlayerMoveFirst) {
                moveMarker.selectFirst();
            } else {
                moveMarker.selectSecond();
            }
            currentFieldType = GameFieldView.FieldType.O;
        } else {
            if (isPlayerMoveFirst) {
                moveMarker.selectFirst();
            } else {
                moveMarker.selectSecond();
            }
            currentFieldType = GameFieldView.FieldType.X;
        }

    }

    public boolean isPlayerWin() {
//        if (gameModel.getGameType() != GameType.FRIEND){
//           return !isPlayerMoveFirst && currentFieldType == GameFieldView.FieldType.O || isPlayerMoveFirst && currentFieldType == GameFieldView.FieldType.X;
//        }
        return isPlayerMoveFirst && currentFieldType == GameFieldView.FieldType.O || !isPlayerMoveFirst && currentFieldType == GameFieldView.FieldType.X;
    }

    private boolean isPlayerWin;

    @Override
    public void onGameWin(List<OneMove> line) {
        Collections.sort(line, OneMove.firstOneMoveComparator);
        if (Math.abs(line.get(0).j - line.get(line.size() - 1).j) != 5) {
            Collections.sort(line, OneMove.secondOneMoveComparator);
        }
        isPlayerWin = isPlayerWin();

        OneMove first = line.get(0);
        OneMove last = line.get(line.size() - 1);


        gameFieldView.showWinLine(first.i, first.j, last.i, last.j, new GameFieldView.DrawWinLineCompletedListener() {
            @Override
            public void onLineDraw() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gameFieldActivityAction.showWonPopup(isPlayerWin);
                        moveTimer.unRegisterListenerAndFinish();
                        increaseScoreAndNotify();
                        sendDataToPlayService();
                    }
                }, 3000);

            }
        });
        gameFieldView.setEnableAllGameField(false);

    }


    private void increaseScoreAndNotify() {
        if (isPlayerWin) {
            firstPlayerScore++;
        } else {
            secondPlayerScore++;
        }
        gameFieldActivityAction.getGameScoreSettable().setFirstPlayerScore(firstPlayerScore);
        gameFieldActivityAction.getGameScoreSettable().setSecondPlayerScore(secondPlayerScore);
    }


    private void sendDataToPlayService() {
        if (isPlayerWin) {
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
        if (isPlayerWin) {
            return;
        }
        if (oneMove != null) {
            gameFieldView.showItem(oneMove.i, oneMove.j, currentFieldType);
            gameFieldView.setItemEnable(oneMove.i, oneMove.j, false);
        }
        if (!isLast) {
            gameFieldView.setEnableAllGameField(true);
            moveTimer.startNewTimer(true);
            if (oneMove != null) {
                changeFieldType();
            }
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
            gameFieldView.setEnableAllGameField(false);
            gameModel.userTimeForMoveEnd();
            moveTimer.startNewTimer(false);
        }
    };

    public void onDestroy() {
        moveTimer.unRegisterListenerAndFinish();
    }


}
