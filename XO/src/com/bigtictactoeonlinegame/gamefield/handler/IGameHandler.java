package com.bigtictactoeonlinegame.gamefield.handler;

import java.util.List;

import android.widget.TextView;

import com.entity.OneMove;
import com.bigtictactoeonlinegame.GameType;
import com.bigtictactoeonlinegame.gamefield.GameFieldActivityAction;
import com.bigtictactoeonlinegame.gamefield.GameFieldAdapter;
import com.bigtictactoeonlinegame.gamefield.GameFieldItem;

public interface IGameHandler {
    // public List<Integer> performedOneMove(OneMove performedOneMove);
    public void sendMessage(String message);

    public GameType getGameType();



    public List<OneMove> performedOneMove(OneMove oneMove);

    public GameFieldItem.FieldType occurredMove(int i, int j);

    public void setAdapter(GameFieldAdapter adapter);

    public void setPlayer1TexView(TextView player1TexView);

    public void setPlayer2TexView(TextView player2TexView);

    public void setPlayer1ScoreTextView(TextView score1TexView);

    public void setPlayer2ScoreTextView(TextView score2TexView);

    public void setTimerTextView(TextView timerTexView);

    public void initIndicator();

    public void startNewGame();

    public void exitFromGame();

    public void setActivityAction(GameFieldActivityAction activityAction);


    public void unregisterHandler();




}
