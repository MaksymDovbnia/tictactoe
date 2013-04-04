package com.net.online;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import net.protocol.Protocol;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.config.Config;
import com.entity.Player;
import com.google.protobuf.AbstractMessageLite;
import com.net.online.protobuf.ProtoFactory;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;

public class OnlineConectionGameWorker extends Thread {
	private Handler handler;
	// public static final String TAG = "myLogs";
	private OutputStream outputStream;
	private InputStream inputStream;
	private PrintWriter mOut;
	private Socket socket;
	private DataOutputStream dataOutputStream;
	private BufferedInputStream in;
	private DataInputStream dataInputStream;
	private Player player;
	private boolean conecting = true;
	private ProgressDialog pd;

	public OnlineConectionGameWorker(Handler handler, Player player,
			ProgressDialog pd) {
		this.handler = handler;
		this.player = player;
		this.pd = pd;
		// this.startActivity = activity;
	}

	public void setHanlerd(Handler handler) {
		this.handler = handler;
	}

	public void disconect() {
		try {
			conecting = false;
			if (dataInputStream != null && socket != null
					&& socket.isConnected()) {
				dataOutputStream.close();
				dataOutputStream.close();
				socket.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void connectToServer() {
		try {
			Loger.printLog("start connecion");

			socket = new Socket(Config.HOST, Config.PORT);

			Loger.printLog("(socket.isClosed() " + socket.isClosed());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			in = new BufferedInputStream(socket.getInputStream());
			dataInputStream = new DataInputStream(in);
			Protocol.SLoginToGame sLoginToGame = Protocol.SLoginToGame
					.newBuilder().setName(player.getName()).build();
			sendPacket(sLoginToGame);
			while (conecting) {
				byte b = dataInputStream.readByte();
				int length = dataInputStream.readInt();
				byte[] data = new byte[length];
				dataInputStream.read(data);
				ProtoType protoType = ProtoType.fromByte(b);
				Loger.printLog("get message " + protoType);
				Message message = handler.obtainMessage(ProtoType.getInt(b),
						ProtoFactory.createProtoObject(data, protoType));
				handler.sendMessage(message);
			}

		} catch (UnknownHostException e) {

			Loger.printLog(e.toString());

		} catch (IOException e) {
			Loger.printLog(e.toString());

		}

	}

	public void run() {
		int n = 0;
		connectToServer();
		Loger.printLog("sc " + (socket == null));
		// Continue 5 times connection to server in 1000 ms
		while (socket == null && ++n <= 5) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Loger.printLog("new con " + n);
			connectToServer();
		}
		Loger.printLog("------");
		// pd.setMessage("Sorry, server not avaible, plese try later");
		// pd.setTitle("111");
		// pd.cancel();
		Message message = handler.obtainMessage(100);
		handler.sendMessage(message);
		// pd.dismiss();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pd.cancel();

	}

	public void sendPacket(final AbstractMessageLite abstractMessageLite) {
		if (dataOutputStream == null)
			return;

		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					byte[] data = abstractMessageLite.toByteArray();
					int length = data.length;
					dataOutputStream.writeByte(ProtoType.fromClass(
							abstractMessageLite.getClass()).getByteValue());
					dataOutputStream.writeInt(length);
					dataOutputStream.write(data);
					dataOutputStream.flush();
					// dataOutputStream.close();
					Loger.printLog("SEND " + abstractMessageLite);
				} catch (IOException e) {
					// Log.d(TAG, e.toString());
				}

			}
		});

		th.start();

	}

}
