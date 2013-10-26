package com.game.gamefield.handler;

import android.os.Handler;
import android.widget.TextView;

import com.entity.OneMove;
import com.entity.TypeOfMove;
import com.game.GameLogicHandler;
import com.game.GameType;
import com.game.gamefield.GameFieldActivityAction;
import com.game.gamefield.GameFieldAdapter;
import com.game.gamefield.GameFieldItem;
import com.net.bluetooth.BluetoothServiceViaProtobuf;
import com.net.bluetooth.protocol.BluetoothProtocol;

import java.util.List;

/**
 * Created by Maksym on 6/20/13.
 */
public class BluetoothGameHandler implements GameHandler {

    private GameFieldAdapter gameFieldAdapter;
    private Handler handler;
    private BluetoothServiceViaProtobuf bluetoothService;
    private GameLogicHandler gameActionHandler;

    public BluetoothGameHandler(BluetoothServiceViaProtobuf bluetoothServiceViaProtobuf) {
        bluetoothService = bluetoothServiceViaProtobuf;
        bluetoothService.registerHandler(handler);
        gameActionHandler = new GameLogicHandler();

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
    public Handler getHandler() {
        return handler;
    }

    @Override
    public List<OneMove> performedOneMove(OneMove oneMove) {
        bluetoothService.sentPacket(BluetoothProtocol.DidMove.newBuilder().setI(oneMove.i).setJ(oneMove.j).setType(
                (oneMove.type.equals(TypeOfMove.X)) ? BluetoothProtocol.TypeMove.X : BluetoothProtocol.TypeMove.O).build());
        List<OneMove> list = gameActionHandler.oneMove(oneMove);
        return list;
    }

    @Override
    public GameFieldItem.FieldType occurredMove(int i, int j) {
        return null;
    }

    @Override
    public void setAdapter(GameFieldAdapter adapter) {
        this.gameFieldAdapter = adapter;
    }

    @Override
    public void setPlayer1TexView(TextView player1TexView) {

    }

    @Override
    public void setPlayer2TexView(TextView player2TexView) {

    }

    @Override
    public void setPlayer1ScoreTextView(TextView score1TexView) {

    }

    @Override
    public void setPlayer2ScoreTextView(TextView score2TexView) {

    }

    @Override
    public void setTimerTextView(TextView timerTexView) {

    }

    @Override
    public void initIndicator() {

    }

    @Override
    public void startNewGame() {
        gameActionHandler.newGame();
    }

    @Override
    public void exitFromGame() {

    }

    @Override
    public void setActivityAction(GameFieldActivityAction activityAction) {

    }

    @Override
    public void unregisterHandler() {

    }
}
