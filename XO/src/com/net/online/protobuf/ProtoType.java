package com.net.online.protobuf;

import net.protocol.*;

import java.util.*;

public enum ProtoType {

    // from Main Server
    CLOGINTOGAME((byte) 0x01, 1),
    CUPDATEAOBOUTACTIVITYPLAYER((byte) 0x02, 2),
    CWANTTOPLAY((byte) 0x03, 3),
    CSTARTGAME((byte) 0x04, 4),
    CDIDMOVE((byte) 0x05, 5),
    CEXITFROMGAME((byte) 0x0D, 13),
    CCONTINUEGAME((byte) 0x07, 7),
    CGETGROUPLIST((byte) 0x09, 9),
    CCANCELDESIREPLAY((byte) 0x08, 8),

    CCHATMESSAGE((byte) 0x0C, 10),
    CGROUPCHATMESSAGE((byte) 0x1C, 11),
    CTOP100((byte) 0x0E, 12),
    TIME_FOR_MOVE_FULL_UP((byte) 0x9B, Protocol.TimeForMoveFullUp.class),
//    TIME_FOR_MOVE_FULL_UP((byte) 0x9B, 19),


    //OTHER
    CONNECTION_TO_SERVER_LOST((byte) 0x40, 400),
    APP_NEED_UPDATE_TO_LAST_VERSION((byte) 0x9E, 90),

    // TO SERVER
    SGETUPDATE((byte) 0x02, Protocol.SGetUpdate.class), SLOGINTOGAME(
            (byte) 0x01, Protocol.SLoginToGame.class), SWANTTOPlAY((byte) 0x03,
            Protocol.SWantToPlay.class), SDIDMOVE((byte) 0x05,
            Protocol.SDidMove.class), SEXITFROMGAME((byte) 0x0D,
            Protocol.SExitFromGame.class), SCONTINUEGAME((byte) 0x07,
            Protocol.SContinueGame.class), SWONGAME((byte) 0x06,
            Protocol.SWonGame.class), SGETGROUPLIST((byte) 0x09, Protocol.SGetGroupList.class),
    SENTERTOGROUP((byte) 0x8A, Protocol.SEnterToGroup.class),
    SСANCELDESIREPLAY((byte) 0x08, Protocol.SCancelDesirePlay.class),
    SCHATMESSAGE((byte) 0x0C, Protocol.SChatMessage.class),
    SGROUPCHATMESSAGE((byte) 0x1C, Protocol.SGroupChatMessage.class),

    SEXITFROMGROUP((byte) 0x1D, Protocol.SExitFromGroup.class),
    SEXITFROMGLOBALGAME((byte) 0x2D,
            Protocol.SExitFromGlobalGame.class),
    STOP100((byte) 0x0E, Protocol.STop100Player.class),


    UNKNOWN((byte) 0x00, 0);


    private final byte b;
    private Class protoClass;
    public int index;
    private final static HashMap<Class, ProtoType> classMap;
    private final static HashMap<Byte, ProtoType> byteMap;
    private final static HashMap<Integer, ProtoType> intMap;

    static {
        classMap = new HashMap<Class, ProtoType>(64);
        byteMap = new HashMap<Byte, ProtoType>(64);
        intMap = new HashMap<Integer, ProtoType>(64);
        for (ProtoType type : values()) {
            classMap.put(type.protoClass, type);
            if (type.protoClass == null) {
                byteMap.put(type.b, type);
                intMap.put(type.index, type);
            }

        }
        // add additional
        byteMap.put(TIME_FOR_MOVE_FULL_UP.b, TIME_FOR_MOVE_FULL_UP);
        intMap.put(TIME_FOR_MOVE_FULL_UP.index, TIME_FOR_MOVE_FULL_UP);
    }

    private ProtoType(byte b, Class protoClass) {
        this.b = b;
        this.protoClass = protoClass;
    }

    private ProtoType(byte b, int index) {
        this.b = b;
        this.index = index;
    }

    public static int getInt(byte b) {
        ProtoType type = byteMap.get(b);
        if (type != null)
            return type.index;
        else
            return UNKNOWN.index;
    }

    public static ProtoType fromByte(byte b) {
        ProtoType type = byteMap.get(b);
        if (type != null)
            return type;
        else
            return UNKNOWN;
    }

    public static ProtoType fromInt(int index) {
        ProtoType type = intMap.get(index);
        if (type != null)
            return type;
        else
            return UNKNOWN;
    }

    public static ProtoType fromClass(Class c) {
        ProtoType type = classMap.get(c);
        if (type != null)
            return type;
        else
            return UNKNOWN;

    }

    public byte getByteValue() {
        return b;
    }

}