package com.bluetooth.protobuf;
/**
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */

import com.bluetooth.protocol.BluetoothProtocol;

import java.util.HashMap;

public enum BluetoothProtoType {
    DID_MOVE((byte) 0x05, BluetoothProtocol.DidMove.class),
    EXIT_FROM_GAME((byte) 0x09, BluetoothProtocol.ExitFromGame.class),
    CONTINUE_GAME((byte) 0x8A, BluetoothProtocol.ContinueGame.class),
    CHAT_MESSAGE((byte) 0x4A, BluetoothProtocol.ChatMessage.class),
    STAR_GAME((byte) 0x9A, BluetoothProtocol.StartGame.class),

    UNKNOWN((byte) 0x00);

    private final byte b;
    private Class protoClass;

    private final static HashMap<Class, BluetoothProtoType> classMap;
    private final static HashMap<Byte, BluetoothProtoType> byteMap;

    static {
        classMap = new HashMap<Class, BluetoothProtoType>(64);
        byteMap = new HashMap<Byte, BluetoothProtoType>(64);
        for (BluetoothProtoType type : values()) {
            classMap.put(type.protoClass, type);
            byteMap.put(type.b, type);
        }
    }

    BluetoothProtoType(byte b) {
        this.b = b;
    }

    private BluetoothProtoType(byte b, Class protoClass) {
        this.b = b;
        this.protoClass = protoClass;
    }


    public static BluetoothProtoType fromByte(byte b) {
        BluetoothProtoType type = byteMap.get(b);
        if (type != null)
            return type;
        else
            return UNKNOWN;
    }


    public static BluetoothProtoType fromClass(Class c) {
        BluetoothProtoType type = classMap.get(c);
        if (type != null)
            return type;
        else
            return UNKNOWN;
    }

    public byte getByteValue() {
        return b;
    }
}