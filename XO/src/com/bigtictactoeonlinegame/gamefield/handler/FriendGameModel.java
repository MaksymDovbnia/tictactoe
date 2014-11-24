package com.bigtictactoeonlinegame.gamefield.handler;

import java.util.List;

import com.bigtictactoeonlinegame.GameType;
import com.bigtictactoeonlinegame.gamefield.GameFieldActivityAction;

import com.entity.OneMove;
import com.entity.Player;

public class FriendGameModel extends GeneralGameModel {

    public FriendGameModel(Player player, Player opponent, GameFieldActivityAction activityAction) {
        super(player, opponent, activityAction);
    }

    @Override
    public GameType getGameType() {
        return GameType.FRIEND;
    }


    @Override
    public void userMadeMove(OneMove oneMove) {
        List<OneMove> list = gameFieldModel.oneMove(oneMove);
        if (list != null) {
            wonGameListener.onGameWin(list);
            return;
        }
        opponentActionListener.onOpponentPerformMove(null, false);


    }


    @Override
    public void startNewGame(boolean isOpponentMoveFirst) {
        gameFieldModel.newGame();

    }

    @Override
    public void exitFromGame() {

    }


    @Override
    public void userTimeForMoveEnd() {
        opponentActionListener.onOpponentPerformMove(null, false);
    }

    @Override
    public void unregisterHandler() {

    }


}
