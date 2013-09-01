package com.net.bluetooth.protobuf;


import com.net.bluetooth.protocol.BluetoothProtocol;

import java.util.HashMap;

public enum BluetoothProtoType {
    DIDMOVE((byte) 0x05,
            BluetoothProtocol.DidMove.class), EXITFROMGAME((byte) 0x09,
            BluetoothProtocol.ExitFromGame.class),
    CONTINUEGAME((byte) 0x8A, BluetoothProtocol.ContinueGame.class),
    CHATMESSAGE((byte) 0x8A, BluetoothProtocol.ChatMessage.class),

    UNKNOWN((byte) 0x00, 0);

    private final byte b;
    private Class protoClass;
    public int index;
    private final static HashMap<Class, BluetoothProtoType> classMap;
    private final static HashMap<Byte, BluetoothProtoType> byteMap;
    private final static HashMap<Integer, BluetoothProtoType> intMap;

    static {
        classMap = new HashMap<Class, BluetoothProtoType>(64);
        byteMap = new HashMap<Byte, BluetoothProtoType>(64);
        intMap = new HashMap<Integer, BluetoothProtoType>(64);
        for (BluetoothProtoType type : values()) {
            classMap.put(type.protoClass, type);
            if (type.protoClass == null) {
                byteMap.put(type.b, type);
                intMap.put(type.index, type);
            }

        }
    }

    private BluetoothProtoType(byte b, Class protoClass) {
        this.b = b;
        this.protoClass = protoClass;
    }

    private BluetoothProtoType(byte b, int index) {
        this.b = b;
        this.index = index;
    }

    public static int getInt(byte b) {
        BluetoothProtoType type = byteMap.get(b);
        if (type != null)
            return type.index;
        else
            return UNKNOWN.index;
    }

    public static BluetoothProtoType fromByte(byte b) {
        BluetoothProtoType type = byteMap.get(b);
        if (type != null)
            return type;
        else
            return UNKNOWN;
    }

    public static BluetoothProtoType fromInt(int index) {
        BluetoothProtoType type = intMap.get(index);
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