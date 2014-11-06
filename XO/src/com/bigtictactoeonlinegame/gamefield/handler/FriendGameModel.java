package com.bigtictactoeonlinegame.gamefield.handler;

import java.util.List;

import com.bigtictactoeonlinegame.GameType;
import com.bigtictactoeonlinegame.gamefield.GameFieldActivityAction;

import com.bigtictactoeonlinegame.gamefield.GameFieldItem;
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
        GameFieldItem.FieldType type = null;
//        OneMove oneMove = null;
//        if (indicator == FIRST_PLAYER) {
//            type = (player.getMoveType() == TypeOfMove.X) ? GameFieldItem.FieldType.X : GameFieldItem.FieldType.O;
//            oneMove = new OneMove(i, j, player.getMoveType());
//        } else if (indicator == SECOND_PLAYER) {
//            type = (opponent.getMoveType() == TypeOfMove.X) ? GameFieldItem.FieldType.X : GameFieldItem.FieldType.O;
//            oneMove = new OneMove(i, j, opponent.getMoveType());
//        }
//        gameFieldAdapter.showOneMove(oneMove);
//        performedOneMove(oneMove);
//        changeIndicator();

    }


    @Override
    public void startNewGame() {
        gameFieldModel.newGame();
//        gameFieldAdapter.startNewGame();
//        if (player.getMoveType() == TypeOfMove.X) {
//            indicator = SECOND_PLAYER;
//            tvPlayer2Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
//            tvPlayer1Name.setBackgroundResource(R.drawable.button_white);
//            player.setMoveType(TypeOfMove.O);
//            opponent.setMoveType(TypeOfMove.X);
//        } else {
//            indicator = FIRST_PLAYER;
//            player.setMoveType(TypeOfMove.X);
//            opponent.setMoveType(TypeOfMove.O);
//            tvPlayer1Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
//            tvPlayer2Name.setBackgroundResource(R.drawable.button_white);
//        }
    }

    @Override
    public void exitFromGame() {
    }


    @Override
    public void unregisterHandler() {

    }


}
