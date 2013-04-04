package com.net.online.protobuf;

import java.util.HashMap;

import net.protocol.Protocol;

public enum ProtoType {

	// from Main Server
	CLOGINTOGAME((byte) 0x01, 1), CUPDATEAOBOUTACTIVITYPLAYER((byte) 0x02, 2), CWANTTOPLAY(
			(byte) 0x03, 3), CSTARTGAME((byte) 0x04, 4), CDIDMOVE((byte) 0x05,
			5), CCHATMESSAGE((byte) 0x08, 8), CEXITFROMGAME((byte) 0x09, 9), CCONTINUEGAME(
			(byte) 0x07, 7),

	SGETUPDATE((byte) 0x02, Protocol.SGetUpdate.class), SLOGINTOGAME(
			(byte) 0x01, Protocol.SLoginToGame.class), SWANTTOPlAY((byte) 0x03,
			Protocol.SWantToPlay.class), SDIDMOVE((byte) 0x05,
			Protocol.SDidMove.class), SEXITFROMGAME((byte) 0x09,
			Protocol.SExitFromGame.class), SCONTINUEGAME((byte) 0x07,
			Protocol.SContinueGame.class), SWONGAME((byte) 0x06,
			Protocol.SWonGame.class),

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