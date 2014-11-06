package com.net.online;

import android.app.*;
import android.os.*;
import android.os.Message;

import com.config.*;
import com.entity.*;
import com.google.protobuf.*;
import com.net.online.protobuf.*;
import com.utils.*;

import net.protocol.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class OnlineConnectionManager extends Thread {
    private static final int SOCKET_TIMEOUT = 5000;
    private Handler mHandler;
    private Set<Handler> mHandlerList;
    private static final String TAG = OnlineConnectionManager.class.getCanonicalName();

    private Socket socket;
    private DataOutputStream dataOutputStream;
    private BufferedInputStream in;
    private DataInputStream dataInputStream;
    private Player player;
    private boolean isConnecting = true;
    private ProgressDialog pd;
    private static final int AMOUNT_OF_ATTEMTP_TO_CREATE_SOCKET = 3;
    private boolean isSendPacketAboutFailConnection = true;
    private boolean isNeedConnectOneMore = true;


    public OnlineConnectionManager(Handler handler, Player player,
                                   ProgressDialog pd) {
        this.mHandler = handler;
        this.player = player;
        this.pd = pd;
        mHandlerList = new HashSet<Handler>();
        if (handler != null) mHandlerList.add(handler);

    }

    public void registerHandler(Handler handler) {
        mHandlerList.add(handler);
    }

    public boolean unRegisterHandler(Handler handler) {
        return mHandlerList.remove(handler);
    }

    public void stopManager() {
        mHandler = null;
        mHandlerList = null;
        pd = null;
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

    public boolean isSockedInLive() {
        if (socket == null) return false;
        return !socket.isClosed();
    }

    private void createSocket() {
        try {
            Logger.printLog("Start connection with thread " + this.toString());
            socket = new Socket();
            socket.connect(getSocketAddress(), SOCKET_TIMEOUT);
            isSendPacketAboutFailConnection = false;
            isNeedConnectOneMore = false;
            Logger.printLog("(socket.isClosed() " + socket.isClosed());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            in = new BufferedInputStream(socket.getInputStream());
            dataInputStream = new DataInputStream(in);

            Protocol.SLoginToGame sLoginToGame = Protocol.SLoginToGame.newBuilder()
                    .setName(player.getName())
                    .setRegistarionType(player.getRegistrationType())
                    .setUuid(player.getUuid())
                    .setGooglePlayRating(player.getPlayServiceScore())
                    .setAppVersion(Config.APP_VERSION).build();
            sendPacket(sLoginToGame);

            while (isConnecting) {
                byte b = dataInputStream.readByte();
                int length = dataInputStream.readInt();
                byte[] data = new byte[length];
                dataInputStream.readFully(data);
                ProtoType protoType = ProtoType.fromByte(b);
                Logger.printLog("get message " + protoType);
                for (Handler handler : mHandlerList) {
                    if (handler != null) {
                        Message message = handler.obtainMessage(ProtoType.getInt(b),
                                ProtoFactory.createProtoObject(data, protoType));
                        handler.sendMessage(message);
                    }
                }
            }

        } catch (UnknownHostException e) {

            Logger.printLog(e.toString());

        } catch (InvalidProtocolBufferException e) {
            Logger.printError(e.toString());
        } catch (IOException e) {
            if (socket != null) try {
                socket.close();
            } catch (IOException e1) {
                Logger.printError(e.getMessage());
            }
            Logger.printError("(socket.isClosed() " + socket.isClosed());
            Logger.printError("(socket.isBound() " + socket.isBound());
            Logger.printError("(socket.isConnected() " + socket.isConnected());

            if (mHandlerList != null) {
                for (Handler handler : mHandlerList) {
                    if (handler != null) {
                        Message message = handler.obtainMessage(ProtoType.CONNECTION_TO_SERVER_LOST.index,
                                null);
                        handler.sendMessage(message);
                    }
                }
            }
        }

    }

    private SocketAddress getSocketAddress() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(Config.HOST);
        return new InetSocketAddress(inetAddress, Config.PORT);
    }

    public void run() {
        int n = 0;
        createSocket();
        Logger.printLog("socket == null " + (socket == null));
        while (socket.isClosed() && ++n <= AMOUNT_OF_ATTEMTP_TO_CREATE_SOCKET && isNeedConnectOneMore) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Logger.printLog(e + "");
            }
            Logger.printLog("connection fail = " + n);
            createSocket();
        }
        if (isSendPacketAboutFailConnection) {
            Message message = mHandler.obtainMessage(100);
            mHandler.sendMessage(message);
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
                    Logger.printLog("Sent object: " + abstractMessageLite);
                } catch (IOException e) {
                    // Log.d(LOG_TAG, e.toString());
                }

            }
        });

        th.start();

    }

}
