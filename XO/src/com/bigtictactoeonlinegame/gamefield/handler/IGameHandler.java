package com.bigtictactoeonlinegame.gamefield.handler;

import android.widget.*;

import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.gamefield.*;
import com.entity.*;

import java.util.*;

public interface IGameHandler {

    public void sendMessage(String message);

    public GameType getGameType();



    public List<OneMove> performedOneMove(OneMove oneMove);

    public GameFieldItem.FieldType occurredMove(int i, int j);

    public void setAdapter(GameFieldAdapter adapter);

    public void setPlayer1TextView(TextView player1TexView);

    public void setPlayer2TextView(TextView player2TexView);

    public void setPlayer1ScoreTextView(TextView score1TexView);

    public void setPlayer2ScoreTextView(TextView score2TexView);

    public void setTimerTextView(TextView timerTexView);

    public void initIndicator();

    public void startNewGame();

    public void exitFromGame();

    public void setActivityAction(GameFieldActivityAction activityAction);


    public void unregisterHandler();




}
