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
    private boolean opponentWantContinue;
    private boolean playerWantContinue;


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
                        List<OneMove> list = gameFieldModel.oneMove(oneMove);

                        opponentActionListener.onOpponentPerformMove(oneMove, false);
                        if (list != null) {
                            wonGameListener.onGameWin(list);
                        }

                        break;
                    case CEXITFROMGAME:
                        OnlineGameModel.this.mActivityAction.opponentExitFromGame();
                        break;
                    case CCONTINUEGAME:
                        Protocol.CContinueGame cContinueGame = (Protocol.CContinueGame) msg.obj;
                        Protocol.TypeMove typeOfMove = cContinueGame.getType();
                        opponentWantContinue = true;
                        checkForBothWantContinue();

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
                        opponentActionListener.opponentMoveTimeEnd();
                        break;
                }

                super.handleMessage(msg);
            }

        };

        onlineGameWorker.registerHandler(handler);
    }

    private void checkForBothWantContinue() {
        if (opponentWantContinue && playerWantContinue) {
            wonGameListener.onBothPlayerWantContinue();
        }
    }

    @Override
    public GameType getGameType() {
        return GameType.ONLINE;
    }


    @Override
    public void userMadeMove(OneMove oneMove) {
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

        List<OneMove> list = gameFieldModel.oneMove(oneMove);
        if (list != null) {
            wonGameListener.onGameWin(list);
        }
        onlineGameWorker.sendPacket(sDidMove);
        if (list != null) {
            onlineGameWorker.sendPacket(Protocol.SWonGame.newBuilder().setIdWonPlayer(player.getId()).
                    setIdLostPlayer(opponent.getId()).build());
        }


    }


    public Handler getHandler() {
        return handler;
    }


    @Override
    public void startNewGame(boolean isOpponentMoveFirst) {
        playerWantContinue = true;
        checkForBothWantContinue();
        gameFieldModel.newGame();
        onlineGameWorker.sendPacket(Protocol.SContinueGame
                .newBuilder()
                .setPlayerId(player.getId())
                .setOpponentId(opponent.getId())
                .build());
    }

    @Override
    public void userTimeForMoveEnd() {
        onlineGameWorker.sendPacket(Protocol.TimeForMoveFullUp.newBuilder().setTimeFullUp(true).build());
    }

    @Override
    public void exitFromGame() {
        onlineGameWorker.sendPacket(Protocol.SExitFromGame.newBuilder().setPlayerId(player.getId()).setOpponentId(1).build());
        onlineGameWorker.unRegisterHandler(handler);
    }


    @Override
    public void unregisterHandler() {
        if (handler != null) onlineGameWorker.unRegisterHandler(handler);
        onlineGameWorker = null;
    }

}
