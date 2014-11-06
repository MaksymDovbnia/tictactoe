package com.bigtictactoeonlinegame.gamefield.handler;

import com.bigtictactoeonlinegame.GameFieldModel;
import com.bigtictactoeonlinegame.gamefield.GameFieldActivityAction;

import com.entity.Player;

/**
 * @author Maksim Dovbnya(m.dovbnya@samsung.com). 10/13/13.
 */
public abstract class GeneralGameModel implements IGameModel {


    protected GameFieldActivityAction mActivityAction;
    protected IWonGameListener wonGameListener;

    protected IOpponentActionListener opponentActionListener;

    protected Player player;
    protected Player opponent;
    protected GameFieldModel gameFieldModel;


    public GeneralGameModel(Player player, Player opponent, GameFieldActivityAction activityAction) {
        gameFieldModel = new GameFieldModel();
        this.player = player;
        this.opponent = opponent;
        this.mActivityAction = activityAction;

    }


    @Override
    public void setWonGameListener(IWonGameListener listener) {
        wonGameListener = listener;
    }


    @Override
    public void setOpponentActionListener(IOpponentActionListener listener) {
        opponentActionListener = listener;
    }

    @Override
    public void userTimeForMoveEnd() {

    }
}
