package com.game.gamefield.handler;

import android.media.MediaPlayer;
import android.widget.TextView;

import com.bluetooth.BluetoothService;
import com.bluetooth.IBluetoothGameListener;
import com.entity.OneMove;
import com.entity.Player;
import com.entity.TypeOfMove;
import com.game.GameType;
import com.game.activity.R;
import com.game.chat.ChatMessage;
import com.game.gamefield.GameFieldActivityAction;
import com.game.gamefield.GameFieldAdapter;
import com.game.gamefield.GameFieldItem;
import com.bluetooth.protocol.BluetoothProtocol;

import java.util.List;

/**
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class BluetoothGameHandler extends GlobalHandler implements IGameHandler {
    private BluetoothService bluetoothService;
    private boolean isPlayerMoveFirst;
    private boolean isPlayerWantToContinue = false;
    private boolean isOpponentWantToContinue = false;


    public BluetoothGameHandler(Player player, Player opponent, GameFieldActivityAction activityAction, MediaPlayer mediaPlayer,
                                BluetoothService bluetoothService1, final boolean isPlayerMoveFirst) {
        super(player, opponent, activityAction, mediaPlayer);
        bluetoothService = bluetoothService1;
        bluetoothService.registerListener(iBluetoothGameListener);
        this.isPlayerMoveFirst = isPlayerMoveFirst;
    }


    private IBluetoothGameListener iBluetoothGameListener = new IBluetoothGameListener() {
        @Override
        public void receivedNewChatMessage(String message) {
            activityAction.receivedChatMessage(new ChatMessage(message, opponent.getName()));
        }

        @Override
        public void receivedNewOneMove(OneMove oneMove) {
            gameFieldAdapter.setEnableAllUnusedGameField(true);
            gameFieldAdapter.showOneMove(oneMove);
            List<OneMove> list = gameLogicHandler.oneMove(oneMove);
            if (list != null) {
                wonGame(list);
            }
            changeIndicator();
        }

        @Override
        public void startGame(String opponentName) {
        }

        @Override
        public void playerExitFromGame() {
            activityAction.opponentExitFromGame();
        }

        @Override
        public void continueGame() {
            isOpponentWantToContinue = true;
            startCheckingForNewGame();
        }
    };

    private void startCheckingForNewGame() {
        if (isOpponentWantToContinue && isPlayerWantToContinue) {
            isOpponentWantToContinue = false;
            isPlayerWantToContinue = false;
            gameFieldAdapter.startNewGame();
            gameLogicHandler.newGame();
            if (player.getMoveType() == TypeOfMove.X) {
                player.setMoveType(TypeOfMove.O);
                opponent.setMoveType(TypeOfMove.X);
                indicator = SECOND_PLAYER;
                tvPlayer2Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
                tvPlayer1Name.setBackgroundResource(R.drawable.button_white);
            } else {
                gameFieldAdapter.setEnableAllUnusedGameField(true);
                player.setMoveType(TypeOfMove.X);
                opponent.setMoveType(TypeOfMove.O);
                indicator = FIRST_PLAYER;
                tvPlayer1Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
                tvPlayer2Name.setBackgroundResource(R.drawable.button_white);
            }
        }
    }

    @Override
    public void sendMessage(String message) {
        bluetoothService.sentPacket(BluetoothProtocol.ChatMessage.newBuilder().setMessage(message).build());
    }

    @Override
    public GameType getGameType() {
        return GameType.BLUETOOTH;
    }


    @Override
    public List<OneMove> performedOneMove(OneMove oneMove) {
        bluetoothService.sentPacket(BluetoothProtocol.DidMove.newBuilder()
                .setI(oneMove.i)
                .setJ(oneMove.j)
                .setType((oneMove.type.equals(TypeOfMove.X)) ?
                        BluetoothProtocol.TypeMove.X : BluetoothProtocol.TypeMove.O).build());
        List<OneMove> list = gameLogicHandler.oneMove(oneMove);
        if (list != null) {
            wonGame(list);
        }
        return list;
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
        tvPlayer1Score.setText(String.valueOf(0));
    }

    @Override
    public void setPlayer2ScoreTextView(TextView score2TexView) {
        tvPlayer2Score = score2TexView;
        tvPlayer2Score.setText(String.valueOf(0));
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
        bluetoothService.sentPacket(BluetoothProtocol.ContinueGame
                .newBuilder().setContinueGame(true).build());
        isPlayerWantToContinue = true;
        startCheckingForNewGame();
    }

    @Override
    public void exitFromGame() {
    }

    @Override
    public void setActivityAction(GameFieldActivityAction activityAction) {
    }

    @Override
    public void unregisterHandler() {
        bluetoothService.unRegisterListener();
    }
}
