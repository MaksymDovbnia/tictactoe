package com.bigtictactoeonlinegame.gamefield.handler;

import android.widget.TextView;

import com.bigtictactoeonlinegame.GameType;
import com.bigtictactoeonlinegame.chat.ChatMessage;
import com.bigtictactoeonlinegame.gamefield.GameFieldActivityAction;
import com.bigtictactoeonlinegame.gamefield.GameFieldAdapter;
import com.bigtictactoeonlinegame.gamefield.GameFieldItem;
import com.bigtictactoeonlinegame.gamefield.OneMoveTimer;
import com.bluetooth.BluetoothService;
import com.bluetooth.IBluetoothGameListener;
import com.entity.OneMove;
import com.entity.Player;
import com.entity.TypeOfMove;
import com.bigtictactoeonlinegame.activity.R;
import com.bluetooth.protocol.BluetoothProtocol;

import java.util.List;

/**
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class BluetoothGameHandler extends GlobalHandler implements IGameHandler {
    private static final int TIME_FOR_MOVE_IN_SEC = 60;
    private BluetoothService bluetoothService;
    private boolean isPlayerMoveFirst;
    private boolean isPlayerWantToContinue = false;
    private boolean isOpponentWantToContinue = false;
    private OneMoveTimer moveTimer;


    public BluetoothGameHandler(Player player, Player opponent, GameFieldActivityAction activityAction,
                                BluetoothService bluetoothService1, final boolean isPlayerMoveFirst) {
        super(player, opponent, activityAction);
        bluetoothService = bluetoothService1;
        bluetoothService.registerListener(iBluetoothGameListener);
        this.isPlayerMoveFirst = isPlayerMoveFirst;
        moveTimer = new OneMoveTimer(TIME_FOR_MOVE_IN_SEC, timerListener);


    }


    private OneMoveTimer.TimerListener timerListener = new OneMoveTimer.TimerListener() {
        @Override
        public void timeChanged(int time) {
            tvTimeInsicator.setText(String.valueOf(time));
        }

        @Override
        public void timeFinished() {
            if (bluetoothService != null) {
                bluetoothService.sentPacket(BluetoothProtocol.TimeForMoveFullUp.
                        newBuilder().setTimeFullUp(true).build());
            }
            gameFieldAdapter.setEnableAllUnusedGameField(false);
            changeIndicator();

            moveTimer.startNewTimer(false);
        }
    };

    private IBluetoothGameListener iBluetoothGameListener = new IBluetoothGameListener() {
        @Override
        public void receivedNewChatMessage(String message) {
            activityAction.receivedChatMessage(new ChatMessage(message, opponent.getName()));
        }

        @Override
        public void receivedNewOneMove(OneMove oneMove) {
            gameFieldAdapter.setEnableAllUnusedGameField(true);
            gameFieldAdapter.showOneMove(oneMove, true);
            List<OneMove> list = gameFieldWinLineHandler.oneMove(oneMove);
            if (list != null) {
                wonGame(list);
            } else {
                moveTimer.startNewTimer(true);
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

        @Override
        public void connectionFailed() {

        }

        @Override
        public void opponentsTimeFinished() {
            gameFieldAdapter.setEnableAllUnusedGameField(true);
            changeIndicator();
            moveTimer.startNewTimer(true);
        }
    };

    private void startCheckingForNewGame() {
        if (isOpponentWantToContinue && isPlayerWantToContinue) {
            isOpponentWantToContinue = false;
            isPlayerWantToContinue = false;
            gameFieldAdapter.startNewGame();
            gameFieldWinLineHandler.newGame();
            if (player.getMoveType() == TypeOfMove.X) {
                moveTimer.startNewTimer(false);
                player.setMoveType(TypeOfMove.O);
                opponent.setMoveType(TypeOfMove.X);
                indicator = SECOND_PLAYER;
                tvPlayer2Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
                tvPlayer1Name.setBackgroundResource(R.drawable.button_white);
            } else {
                gameFieldAdapter.setEnableAllUnusedGameField(true);
                moveTimer.startNewTimer(true);
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


    @SuppressWarnings("unchecked")
    @Override
    public List<OneMove> performedOneMove(OneMove oneMove) {
        bluetoothService.sentPacket(BluetoothProtocol.DidMove.newBuilder()
                .setI(oneMove.i)
                .setJ(oneMove.j)
                .setType((oneMove.type.equals(TypeOfMove.X)) ?
                        BluetoothProtocol.TypeMove.X : BluetoothProtocol.TypeMove.O).build());
        List<OneMove> list = gameFieldWinLineHandler.oneMove(oneMove);
        if (list != null) {
            wonGame(list);
        } else {
            moveTimer.startNewTimer(false);
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
    public void setPlayer1TextView(TextView player1TexView) {
        this.tvPlayer1Name = player1TexView;
        this.tvPlayer1Name.setText(player.getName());
    }

    @Override
    public void setPlayer2TextView(TextView player2TexView) {
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
        moveTimer.startNewTimer(isPlayerMoveFirst);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void startNewGame() {
        bluetoothService.sentPacket(BluetoothProtocol.ContinueGame
                .newBuilder().setContinueGame(true).build());
        isPlayerWantToContinue = true;
        startCheckingForNewGame();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void exitFromGame() {
        bluetoothService.sentPacket(
                BluetoothProtocol.ExitFromGame.newBuilder().setOpponentId(1).build());
    }

    @Override
    public void setActivityAction(GameFieldActivityAction activityAction) {
    }

    @Override
    public void unregisterHandler() {
        bluetoothService.unRegisterListener();
        moveTimer.unRegisterListenerAndFinish();
    }
}
