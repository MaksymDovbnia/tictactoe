package com.game.gamefield.handler;

import java.util.List;

import net.protocol.Protocol;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.entity.OneMove;
import com.entity.Player;
import com.entity.TypeOfMove;
import com.game.GameType;
import com.game.activity.R;
import com.game.chat.ChatMessage;
import com.game.gamefield.GameFieldActivityAction;
import com.game.gamefield.GameFieldAdapter;
import com.game.gamefield.GameFieldItem;
import com.net.online.WorkerOnlineConnection;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;

public class OnlineGameHandler extends GlobalHandler implements IGameHandler {

    private Handler handler;
    private WorkerOnlineConnection onlineGameWorker;
    private boolean isPlayerMoveFirst;

    public OnlineGameHandler(final WorkerOnlineConnection onlineGameWorker,
                             Player player, final Player opponent, GameFieldActivityAction fieldActivityAction, final boolean isPlayerMoveFirst, MediaPlayer mediaPlayer) {
        super(player, opponent, fieldActivityAction, mediaPlayer);
        this.onlineGameWorker = onlineGameWorker;
        this.activityAction = fieldActivityAction;
        this.isPlayerMoveFirst = isPlayerMoveFirst;

        this.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ProtoType protoType = ProtoType.fromInt(msg.what);
                switch (protoType) {
                    case CDIDMOVE:
                        Protocol.CDidMove cDidMove = (Protocol.CDidMove) msg.obj;
                        Loger.printLog(cDidMove.toString());
                        TypeOfMove typeFieldElement = (cDidMove.getType()
                                .equals(Protocol.TypeMove.X) ? TypeOfMove.X
                                : TypeOfMove.O);
                        OneMove oneMove = new OneMove(cDidMove.getI(),
                                cDidMove.getJ(), typeFieldElement);


                        gameFieldAdapter.setEnableAllUnusedGameField(true);
                        gameFieldAdapter.showOneMove(oneMove);
                        List<OneMove> list = gameActionHandler.oneMove(oneMove);
                        if (list != null) {
                            wonGame(list);
                        }
                        changeIndicator();
                        break;
                    case CEXITFROMGAME:
                        OnlineGameHandler.this.activityAction.opponentExitFromGame();
                        break;
                    case CCONTINUEGAME:
                        Protocol.CContinueGame cContinueGame = (Protocol.CContinueGame) msg.obj;
                        Protocol.TypeMove typeOfMove = cContinueGame.getType();
                        if (typeOfMove == Protocol.TypeMove.X) {
                            indicator = FIRST_PLAYER;
                            OnlineGameHandler.super.player.setMoveType(TypeOfMove.X);
                            OnlineGameHandler.super.opponent.setMoveType(TypeOfMove.O);
                            tvPlayer1Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
                            tvPlayer2Name.setBackgroundResource(R.drawable.button_white);
                            gameFieldAdapter.setEnableAllUnusedGameField(true);
                        } else {
                            indicator = SECOND_PLAYER;
                            OnlineGameHandler.super.player.setMoveType(TypeOfMove.O);
                            OnlineGameHandler.super.opponent.setMoveType(TypeOfMove.X);
                            tvPlayer2Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
                            tvPlayer1Name.setBackgroundResource(R.drawable.button_white);
                        }

                        break;
                    case CCHATMESSAGE:
                        Protocol.CChatMessage cChatMessage = (Protocol.CChatMessage) msg.obj;
                        activityAction.receivedChatMessage(new ChatMessage (cChatMessage.getMessage(), opponent.getName()));
                        break;
                    case CONNECTION_TO_SERVER_LOST:
                        activityAction.connectionToServerLost();
                        break;
                }

                super.handleMessage(msg);
            }

        };

        onlineGameWorker.registerHandler(handler);
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
                                : Protocol.TypeMove.O).build();

        onlineGameWorker.sendPacket(sDidMove);
        List<OneMove> list = gameActionHandler.oneMove(oneMove);
        if (list != null) {
            wonGame(list);
            onlineGameWorker.sendPacket(Protocol.SWonGame.newBuilder().setIdWonPlayer(player.getId()).
                    setIdLostPlayer(opponent.getId()).build());
        }

        return list;
    }

    @Override
    protected void wonGame(List<OneMove> list) {
        if (indicator == FIRST_PLAYER) {
            player1ScoreNum++;
            player.setNumOfAllWonGame(player.getNumOfAllWonGame() + 1);
            tvPlayer1Score.setText(player1ScoreNum + " (" + player.getNumOfAllWonGame() + ")");
        } else {
            player2ScoreNum++;
            opponent.setNumOfAllWonGame(opponent.getNumOfAllWonGame() + 1);
            tvPlayer2Score.setText(player2ScoreNum + " (" + opponent.getNumOfAllWonGame() + ")");
        }
        gameActionHandler.newGame();
        gameFieldAdapter.drawWinLine(list);
        activityAction.showWonPopup((indicator == FIRST_PLAYER) ? player.getName() : opponent.getName());
    }


    @Override
    public GameFieldItem.FieldType occurredMove(int i, int j) {
        GameFieldItem.FieldType type = null;
        OneMove oneMove = null;
        if (indicator == FIRST_PLAYER) {
            type = (player.getMoveType() == TypeOfMove.X) ? GameFieldItem.FieldType.X : GameFieldItem.FieldType.O;
            oneMove = new OneMove(i, j, player.getMoveType());
        } else if (indicator == SECOND_PLAYER) {
            type = (opponent.getMoveType() == TypeOfMove.X) ? GameFieldItem.FieldType.X : GameFieldItem.FieldType.O;
            oneMove = new OneMove(i, j, opponent.getMoveType());
        }
        gameFieldAdapter.showOneMove(oneMove);
        performedOneMove(oneMove);
        changeIndicator();
        gameFieldAdapter.setEnableAllUnusedGameField(false);
        return type;
    }


    public void sendMessage(String message) {
        Protocol.SChatMessage chatMessage = Protocol.SChatMessage.newBuilder()
                .setMessage(message).setPlayerId(player.getId())
                .setOpponentId(opponent.getId()).build();
        onlineGameWorker.sendPacket(chatMessage);
    }

    public GameType getGameType() {

        return GameType.ONLINE;
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    public void setAdapter(GameFieldAdapter adapter) {
        this.gameFieldAdapter = adapter;
    }

    @Override
    public void setPlayer1TexView(TextView player1TexView) {
        this.tvPlayer1Name = player1TexView;
        this.tvPlayer1Name.setText(player.getName());

    }

    @Override
    public void setPlayer2TexView(TextView player2TexView) {
        this.tvPlayer2Name = player2TexView;
        this.tvPlayer2Name.setText(opponent.getName());
    }


    @Override
    public void setPlayer1ScoreTextView(TextView score1TexView) {
        tvPlayer1Score = score1TexView;
        tvPlayer1Score.setText(0 + " (" + player.getNumOfAllWonGame() + ")");


    }

    @Override
    public void setPlayer2ScoreTextView(TextView score2TexView) {
        tvPlayer2Score = score2TexView;
        tvPlayer2Score.setText(0 + " (" + opponent.getNumOfAllWonGame() + ")");

    }

    @Override
    public void setTimerTextView(TextView timerTexView) {
        tvTimeInsicator = timerTexView;
    }

    @Override
    public void initIndicator() {
        if (!isPlayerMoveFirst) {
            gameFieldAdapter.setEnableAllUnusedGameField(isPlayerMoveFirst);
            player.setMoveType(TypeOfMove.O);
            opponent.setMoveType(TypeOfMove.X);
            indicator = SECOND_PLAYER;
            tvPlayer2Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
            tvPlayer1Name.setBackgroundResource(R.drawable.button_white);

        } else {
            indicator = FIRST_PLAYER;
            tvPlayer1Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
            tvPlayer2Name.setBackgroundResource(R.drawable.button_white);
            player.setMoveType(TypeOfMove.X);
            opponent.setMoveType(TypeOfMove.O);
        }
    }

    @Override
    public void startNewGame() {
        gameActionHandler.newGame();
        gameFieldAdapter.startNewGame();
        gameFieldAdapter.setEnableAllUnusedGameField(false);
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
    public void setActivityAction(GameFieldActivityAction activityAction) {
        this.activityAction = activityAction;
    }

    @Override
    public void unregisterHandler() {
        if (handler != null) onlineGameWorker.unRegisterHandler(handler);
    }

}
