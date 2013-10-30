package com.game.gamefield.handler;

import android.media.MediaPlayer;
import android.widget.TextView;

import com.entity.OneMove;
import com.entity.Player;
import com.game.GameLogicHandler;
import com.game.activity.R;
import com.game.gamefield.GameFieldActivityAction;
import com.game.gamefield.GameFieldAdapter;

import java.util.List;

/**
 * Created by Maksym on 10/13/13.
 */
public class GlobalHandler {
    protected static final int FIRST_PLAYER = 1;
    protected static final int SECOND_PLAYER = 2;
    protected int player1ScoreNum = 0;
    protected int player2ScoreNum = 0;
    protected TextView tvPlayer1Name;
    protected TextView tvPlayer2Name;
    protected TextView tvPlayer1Score;
    protected TextView tvPlayer2Score;
    protected TextView tvTimeInsicator;
    protected int indicator;
    protected GameFieldAdapter gameFieldAdapter;
    protected GameFieldActivityAction activityAction;

    protected Player player;
    protected Player opponent;
    protected GameLogicHandler gameActionHandler;
    protected MediaPlayer mediaPlayer;

    public GlobalHandler(Player player, Player opponent, GameFieldActivityAction activityAction, MediaPlayer mediaPlayer) {
        gameActionHandler = new GameLogicHandler();
        this.player = player;
        this.opponent = opponent;
        this.activityAction = activityAction;
        this.mediaPlayer = mediaPlayer;
    }


    protected void wonGame(List<OneMove> list){
        if (indicator == FIRST_PLAYER){
            player1ScoreNum++;
            tvPlayer1Score.setText(player1ScoreNum +"");
        }
        else {
            player2ScoreNum++;
            tvPlayer2Score.setText(player2ScoreNum +"");
        }
        gameActionHandler.newGame();
        gameFieldAdapter.drawWinLine(list);
        activityAction.showWonPopup((indicator == FIRST_PLAYER) ? player.getName() : opponent.getName());
    }

    protected void changeIndicator() {
        if (indicator == FIRST_PLAYER) {
            indicator = SECOND_PLAYER;
            tvPlayer2Name.setBackgroundResource(R.drawable.ovalbound_red);
            tvPlayer1Name.setBackgroundResource(R.drawable.button_white);

        } else {
            indicator = FIRST_PLAYER;
            tvPlayer1Name.setBackgroundResource(R.drawable.ovalbound_red);
            tvPlayer2Name.setBackgroundResource(R.drawable.button_white);

        }
    }
}
