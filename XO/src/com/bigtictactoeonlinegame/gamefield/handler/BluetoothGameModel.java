package com.bigtictactoeonlinegame.gamefield.handler;

import com.bigtictactoeonlinegame.GameType;
import com.bigtictactoeonlinegame.chat.ChatMessage;
import com.bigtictactoeonlinegame.gamefield.GameFieldActivityAction;

import com.bluetooth.BluetoothService;
import com.bluetooth.IBluetoothGameListener;
import com.entity.OneMove;
import com.entity.Player;
import com.bluetooth.protocol.BluetoothProtocol;
import com.entity.TypeOfMove;

import java.util.List;

/**
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class BluetoothGameModel extends GeneralGameModel {

    private BluetoothService mBluetoothService;

    private boolean mIsPlayerWantToContinue = false;
    private boolean mIsOpponentWantToContinue = false;

    public BluetoothGameModel(Player player, Player opponent, GameFieldActivityAction activityAction,
                              BluetoothService bluetoothService, final boolean isPlayerMoveFirst) {
        super(player, opponent, activityAction);
        mBluetoothService = bluetoothService;
        mBluetoothService.registerListener(bluetoothGameListener);
    }


    private IBluetoothGameListener bluetoothGameListener = new IBluetoothGameListener() {
        @Override
        public void receivedNewChatMessage(String message) {
            mActivityAction.receivedChatMessage(new ChatMessage(message, opponent.getName()));
        }

        @Override
        public void receivedNewOneMove(OneMove oneMove) {

            List<OneMove> list = gameFieldModel.oneMove(oneMove);
            if (list != null) {
                opponentActionListener.onOpponentPerformMove(oneMove, true);
                wonGameListener.onGameWin(list);
            } else {
                opponentActionListener.onOpponentPerformMove(oneMove, false);
            }

        }

        @Override
        public void startGame(String opponentName) {


        }

        @Override
        public void playerExitFromGame() {
            mActivityAction.opponentExitFromGame();
        }

        @Override
        public void continueGame() {
            mIsOpponentWantToContinue = true;
            startCheckingForNewGame();

        }

        @Override
        public void connectionFailed() {
            mActivityAction.connectionToServerLost();
        }

        @Override
        public void opponentsTimeFinished() {
            opponentActionListener.opponentMoveTimeEnd();

        }
    };

    private void startCheckingForNewGame() {
        if (mIsOpponentWantToContinue && mIsPlayerWantToContinue) {
            mIsOpponentWantToContinue = false;
            mIsPlayerWantToContinue = false;
//            gameFieldAdapter.startNewGame();
//            gameFieldModel.newGame();
//            if (player.getMoveType() == TypeOfMove.X) {
//                gameFieldAdapter.setEnableAllUnusedGameField(false);
//                mMoveTimer.startNewTimer(false);
//                player.setMoveType(TypeOfMove.O);
//                opponent.setMoveType(TypeOfMove.X);
//                indicator = SECOND_PLAYER;
//                tvPlayer2Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
//                tvPlayer1Name.setBackgroundResource(R.drawable.button_white);
//            } else {
//                gameFieldAdapter.setEnableAllUnusedGameField(true);
//                mMoveTimer.startNewTimer(true);
//                player.setMoveType(TypeOfMove.X);
//                opponent.setMoveType(TypeOfMove.O);
//                indicator = FIRST_PLAYER;
//                tvPlayer1Name.setBackgroundResource(SELECT_PLAYER_BACKGROUND);
//                tvPlayer2Name.setBackgroundResource(R.drawable.button_white);
//            }
        }
    }


    public void sendMessage(String message) {
        mBluetoothService.sentPacket(BluetoothProtocol.ChatMessage.newBuilder().setMessage(message).build());
    }

    @Override
    public GameType getGameType() {
        return GameType.BLUETOOTH;
    }


    @SuppressWarnings("unchecked")


    @Override
    public void userMadeMove(OneMove oneMove) {
        BluetoothProtocol.DidMove didMove = BluetoothProtocol.DidMove.newBuilder()
                .setI(oneMove.i)
                .setJ(oneMove.j)
                .setType(oneMove.type == TypeOfMove.X ? BluetoothProtocol.TypeMove.X : BluetoothProtocol.TypeMove.O)
                .build();
        mBluetoothService.sentPacket(didMove);
        List<OneMove> list = gameFieldModel.oneMove(oneMove);
        if (list != null) {
            wonGameListener.onGameWin(list);
        }


    }


    @SuppressWarnings("unchecked")
    @Override
    public void startNewGame(boolean isOpponentMoveFirst) {
        mBluetoothService.sentPacket(BluetoothProtocol.ContinueGame
                .newBuilder().setContinueGame(true).build());
        mIsPlayerWantToContinue = true;
        startCheckingForNewGame();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void exitFromGame() {
        mBluetoothService.sentPacket(
                BluetoothProtocol.ExitFromGame.newBuilder().setOpponentId(1).build());
    }


    @Override
    public void unregisterHandler() {
        mBluetoothService.unRegisterListener();
    }
}
