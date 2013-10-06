package com.net.online;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import net.protocol.Protocol;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

import com.config.Config;
import com.entity.Player;
import com.google.protobuf.AbstractMessageLite;
import com.net.online.protobuf.ProtoFactory;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;

public class WorkerOnlineConnection extends Thread {
    private Handler handler;
    private ArrayList<Handler> handlers;
    private static final String TAG = WorkerOnlineConnection.class.getCanonicalName();
    private OutputStream outputStream;
    private InputStream inputStream;
    private PrintWriter mOut;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private BufferedInputStream in;
    private DataInputStream dataInputStream;
    private Player player;
    private boolean isConnecting = true;
    private ProgressDialog pd;
    public static final int AMOUNT_OF_ATTEMTP_TO_CREATE_SOCKET = 2;
    public boolean isSendPacketAboutFailConnection = true;

    public WorkerOnlineConnection(Handler handler, Player player,
                                  ProgressDialog pd) {
        this.handler = handler;
        this.player = player;
        this.pd = pd;
        handlers = new ArrayList<Handler>();
        if (handler != null) handlers.add(handler);
        // this.startActivity = activity;
    }

    public void registerHandler(Handler handler) {
        handlers.add(handler);
    }

    public boolean unRegisterHandler(Handler handler) {
        return handlers.remove(handler);
    }

    public void disconnect() {
        try {
            isConnecting = false;
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

    private void createSocket() {
        try {
            Loger.printLog("start connecion");

            InetAddress inetAddress = InetAddress.getByName(Config.HOST);
            SocketAddress socketAddress = new InetSocketAddress(inetAddress, Config.PORT);
            //   socket = new Socket(Config.HOST, Config.PORT);
            socket = new Socket();
            socket.connect(socketAddress, 15000);
            isSendPacketAboutFailConnection = false;
            Loger.printLog("(socket.isClosed() " + socket.isClosed());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            in = new BufferedInputStream(socket.getInputStream());
            dataInputStream = new DataInputStream(in);
            Protocol.SLoginToGame sLoginToGame = Protocol.SLoginToGame
                    .newBuilder().setName(player.getName()).setRegistarionType(player.getRegistrationType()).build();
            sendPacket(sLoginToGame);
            while (isConnecting) {
                byte b = dataInputStream.readByte();
                int length = dataInputStream.readInt();
                byte[] data = new byte[length];
                dataInputStream.read(data);
                ProtoType protoType = ProtoType.fromByte(b);
                Loger.printLog("get message " + protoType);
                for (Handler handler : handlers) {
                    if (handler != null) {
                        Message message = handler.obtainMessage(ProtoType.getInt(b),
                                ProtoFactory.createProtoObject(data, protoType));
                        handler.sendMessage(message);
                    }
                }
            }

        } catch (UnknownHostException e) {

            Loger.printLog(e.toString());

        } catch (IOException e) {
            Loger.printLog(e.toString());

        }

    }

    public void run() {
        int n = 0;
        createSocket();
        Loger.printLog("socket == null " + (socket == null));
        while (socket.isClosed() && ++n <= AMOUNT_OF_ATTEMTP_TO_CREATE_SOCKET) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Loger.printLog(e + "");
            }
            Loger.printLog("connection fail = " + n);
            createSocket();
        }
        if (isSendPacketAboutFailConnection) {
            Message message = handler.obtainMessage(100);
            handler.sendMessage(message);
            pd.cancel();
        }

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
