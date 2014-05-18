package com.bluetooth.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.bluetooth.protocol.BluetoothProtocol;
import com.utils.Logger;


public class BluetoothProtoFactory {
    public static AbstractMessageLite createProtoObject(byte data[],
                                                        BluetoothProtoType type) throws InvalidProtocolBufferException {
        switch (type) {
            case DID_MOVE:
                BluetoothProtocol.DidMove didMove = BluetoothProtocol.DidMove.parseFrom(data);
                return didMove;
            case CONTINUE_GAME:
                BluetoothProtocol.ContinueGame continueGame = BluetoothProtocol.ContinueGame.parseFrom(data);
                return continueGame;
            case CHAT_MESSAGE:
                BluetoothProtocol.ChatMessage chatMessage = BluetoothProtocol.ChatMessage.parseFrom(data);
                return chatMessage;
            default:
                Logger.printLog(" Wrong packet in bluetooth");
                return null;

        }

    }

}
