package com.net.online.protobuf;

/**
 * Created with IntelliJ IDEA. User: legioner Date: 13.08.12 Time: 16:38 To
 * change this template use File | Settings | File Templates.
 */
public class Envelope {
	private ProtoType type;
	private int length;
	private byte[] data;

	public ProtoType getType() {
		return type;
	}

	public void setType(ProtoType type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Envelope() {
	}

	public Envelope(ProtoType type, int length, byte[] data) {
		this.type = type;
		this.length = length;
		this.data = data;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("Envelope{").append("type=")
				.append(type).append(", data=")
				.append(data == null ? null : data.length + "bytes")
				.append('}').toString();
	}

}
