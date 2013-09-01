package com.game.handler;

import android.os.Handler;

import com.entity.OneMove;
import com.entity.TypeFieldElement;
import com.game.GameLogicHandler;
import com.game.GameType;
import com.game.adapters.GameFieldAdapter;
import com.net.bluetooth.BluetoothServiceViaProtobuf;
import com.net.bluetooth.protobuf.BluetoothProtoType;
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
    public List<OneMove> oneMove(OneMove oneMove) {
        bluetoothService.sentPacket(BluetoothProtocol.DidMove.newBuilder().setI(oneMove.i).setJ(oneMove.j).setType(
                (oneMove.type.equals(TypeFieldElement.X)) ? BluetoothProtocol.TypeMove.X : BluetoothProtocol.TypeMove.O).build());
        List<OneMove> list = gameActionHandler.oneMove(oneMove);
        return list;
    }
    @Override
    public void setAdapter(GameFieldAdapter adapter) {
        this.gameFieldAdapter = adapter;
    }

    @Override
    public void startNewGame() {
        gameActionHandler.newGame();
    }
}
