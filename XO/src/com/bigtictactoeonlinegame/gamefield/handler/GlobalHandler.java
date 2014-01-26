package com.bigtictactoeonlinegame.gamefield.handler;

import android.widget.TextView;

import com.bigtictactoeonlinegame.GameFieldWinLineHandler;
import com.bigtictactoeonlinegame.gamefield.GameFieldActivityAction;
import com.bigtictactoeonlinegame.gamefield.GameFieldAdapter;
import com.entity.OneMove;
import com.entity.Player;
import com.bigtictactoeonlinegame.activity.R;

import java.util.List;

/**
 * Created by Maksym on 10/13/13.
 */
public class GlobalHandler {
    protected static final int FIRST_PLAYER = 1;
    protected static final int SECOND_PLAYER = 2;
    protected static final int SELECT_PLAYER_BACKGROUND = R.drawable.ovalbound_blue;
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
    protected GameFieldWinLineHandler gameFieldWinLineHandler;


    public GlobalHandler(Player player, Player opponent, GameFieldActivityAction activityAction) {
        gameFieldWinLineHandler = new GameFieldWinLineHandler();
        this.player = player;
        this.opponent = opponent;
        this.activityAction = activityAction;

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
        gameFieldWinLineHandler.newGame();
        gameFieldAdapter.drawWinLine(list);
        activityAction.showWonPopup((indicator == FIRST_PLAYER) ? player.getName()
                : opponent.getName());
    }

    protected void changeIndicator() {
        if (indicator == FIRST_PLAYER) {
            indicator = SECOND_PLAYER;
            tvPlayer2Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
            tvPlayer1Name.setBackgroundResource(R.drawable.button_white);

        } else {
            indicator = FIRST_PLAYER;
            tvPlayer1Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
            tvPlayer2Name.setBackgroundResource(R.drawable.button_white);

        }
    }
}
