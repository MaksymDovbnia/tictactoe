package com.game.gamefield.handler;

import java.util.List;

import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.TextView;

import com.entity.OneMove;
import com.game.GameType;
import com.game.gamefield.GameFieldActivityAction;
import com.game.gamefield.GameFieldAdapter;
import com.game.gamefield.GameFieldItem;

public interface IGameHandler {
    // public List<Integer> performedOneMove(OneMove performedOneMove);
    public void sendMessage(String message);

    public GameType getGameType();

    public Handler getHandler();

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
