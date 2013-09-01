package com.net.bluetooth.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.net.bluetooth.protocol.BluetoothProtocol;
import com.utils.Loger;


public class BluetoothProtoFactory {
    public static AbstractMessageLite createProtoObject(byte data[],
                                                        BluetoothProtoType type) throws InvalidProtocolBufferException {
        switch (type) {
            case DIDMOVE:
                BluetoothProtocol.DidMove didMove = BluetoothProtocol.DidMove.parseFrom(data);
                return didMove;
            case CONTINUEGAME:
                BluetoothProtocol.ContinueGame continueGame = BluetoothProtocol.ContinueGame.parseFrom(data);
                return continueGame;
            case CHATMESSAGE:
                BluetoothProtocol.ChatMessage chatMessage = BluetoothProtocol.ChatMessage.parseFrom(data);
                return chatMessage;
            default:
                Loger.printLog(" Wrong packet in bluetooth");
                return null;

        }

    }

}
