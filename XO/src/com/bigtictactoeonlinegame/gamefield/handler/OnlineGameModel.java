package com.bigtictactoeonlinegame.gamefield.handler;

import android.os.*;

import com.bigtictactoeonlinegame.*;
import com.bigtictactoeonlinegame.chat.*;
import com.bigtictactoeonlinegame.gamefield.*;
import com.entity.*;
import com.net.online.*;
import com.net.online.protobuf.*;
import com.utils.*;

import net.protocol.*;

import java.util.*;

public class OnlineGameModel extends GeneralGameModel {

    private Handler handler;
    private OnlineConnectionManager onlineGameWorker;



    public OnlineGameModel(final OnlineConnectionManager onlineGameWorker,
                           Player player, final Player opponent,
                           GameFieldActivityAction fieldActivityAction, final boolean isPlayerMoveFirst) {
        super(player, opponent, fieldActivityAction);
        this.onlineGameWorker = onlineGameWorker;



        this.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ProtoType protoType = ProtoType.fromInt(msg.what);
                switch (protoType) {
                    case CDIDMOVE:
                        Protocol.CDidMove cDidMove = (Protocol.CDidMove) msg.obj;
                        Logger.printLog(cDidMove.toString());
                        TypeOfMove typeFieldElement = (cDidMove.getType()
                                .equals(Protocol.TypeMove.X) ? TypeOfMove.X
                                : TypeOfMove.O);
                        OneMove oneMove = new OneMove(cDidMove.getI(),
                                cDidMove.getJ(), typeFieldElement);


//                        gameFieldAdapter.setEnableAllUnusedGameField(true);
//                        gameFieldAdapter.showOneMove(oneMove, true);
                        List<OneMove> list = gameFieldModel.oneMove(oneMove);
                        if (list != null) {
                            //wonGame(list);
                        } else {

                        }
                    //    changeIndicator();
                        break;
                    case CEXITFROMGAME:
                        OnlineGameModel.this.mActivityAction.opponentExitFromGame();
                        break;
                    case CCONTINUEGAME:
                        Protocol.CContinueGame cContinueGame = (Protocol.CContinueGame) msg.obj;
                        Protocol.TypeMove typeOfMove = cContinueGame.getType();
                        if (typeOfMove == Protocol.TypeMove.X) {
                            //moveTimer.startNewTimer(false);
                          //  indicator = FIRST_PLAYER;
                            OnlineGameModel.super.player.setMoveType(TypeOfMove.X);
                            OnlineGameModel.super.opponent.setMoveType(TypeOfMove.O);
//                            tvPlayer1Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
//                            tvPlayer2Name.setBackgroundResource(R.drawable.button_white);
//                            gameFieldAdapter.setEnableAllUnusedGameField(true);
                        } else {
                           // indicator = SECOND_PLAYER;
                        //    moveTimer.startNewTimer(true);
                            OnlineGameModel.super.player.setMoveType(TypeOfMove.O);
                            OnlineGameModel.super.opponent.setMoveType(TypeOfMove.X);

                        }

                        break;
                    case CCHATMESSAGE:
                        Protocol.CChatMessage cChatMessage = (Protocol.CChatMessage) msg.obj;
                        mActivityAction.receivedChatMessage(new ChatMessage(cChatMessage.getMessage(), opponent.getName()));
                        break;
                    case CONNECTION_TO_SERVER_LOST:
                        mActivityAction.connectionToServerLost();
                        break;
                    case TIME_FOR_MOVE_FULL_UP:
                        Logger.printError("received TIME_FOR_MOVE_FULL_UP");

                     //   moveTimer.startNewTimer(true);
                        break;
                }

                super.handleMessage(msg);
            }

        };

        onlineGameWorker.registerHandler(handler);
    }


    @Override
    public GameType getGameType() {
        return GameType.ONLINE;
    }

    public List<OneMove> performedOneMove(OneMove oneMove) {
        Protocol.SDidMove sDidMove = Protocol.SDidMove
                .newBuilder()
                .setOpponentId(opponent.getId())
                .setPlayerId(player.getId())
                .setJ(oneMove.j)
                .setI(oneMove.i)
                .setType(
                        (oneMove.type.equals(TypeOfMove.X)) ? Protocol.TypeMove.X
                                : Protocol.TypeMove.O
                ).build();

        onlineGameWorker.sendPacket(sDidMove);
        List<OneMove> list = gameFieldModel.oneMove(oneMove);
        if (list != null) {

            onlineGameWorker.sendPacket(Protocol.SWonGame.newBuilder().setIdWonPlayer(player.getId()).
                    setIdLostPlayer(opponent.getId()).build());
        } else {
            //moveTimer.startNewTimer(false);
        }

        return list;
    }

    @Override
    public void userMadeMove(OneMove oneMove) {
        GameFieldItem.FieldType type = null;

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
//        gameFieldAdapter.setEnableAllUnusedGameField(false);

    }





    public Handler getHandler() {
        return handler;
    }



    @Override
    public void startNewGame() {
        gameFieldModel.newGame();
            onlineGameWorker.sendPacket(Protocol.SContinueGame.newBuilder().setPlayerId(player.getId()).setOpponentId(opponent.getId()).build());
        showWaitingPopup();
    }

    public void showWaitingPopup() {

    }

    @Override
    public void exitFromGame() {
        onlineGameWorker.sendPacket(Protocol.SExitFromGame.newBuilder().setPlayerId(player.getId()).setOpponentId(1).build());
        onlineGameWorker.unRegisterHandler(handler);
    }


    @Override
    public void unregisterHandler() {
      //  moveTimer.startNewTimer(false);
      //  //moveTimer.unRegisterListenerAndFinish();

        if (handler != null) onlineGameWorker.unRegisterHandler(handler);
        onlineGameWorker = null;
    }

}
