package com.net.online;

import com.net.online.protobuf.ProtoType;

public class Envelope {

	public ProtoType type;
	public int length;
	public byte[] data;

	public Envelope() {
	}

	public Envelope(ProtoType type, int length, byte[] data) {
		this.type = type;
		this.length = length;
		this.data = data;
	}
}
