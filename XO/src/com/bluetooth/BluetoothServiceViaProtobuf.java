package com.bluetooth;
/**
 * Created by Maksym on 6/20/13.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.bluetooth.protocol.BluetoothProtocol;
import com.entity.OneMove;
import com.entity.TypeOfMove;
import com.google.protobuf.AbstractMessageLite;
import com.bluetooth.protobuf.BluetoothProtoFactory;
import com.bluetooth.protobuf.BluetoothProtoType;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothServiceViaProtobuf implements BluetoothService<AbstractMessageLite> {

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    public static final int MESSAGE_STATE_CHANGE = 1;
    private static final int MAX_QUANTITY_CONNECT = 3;

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean D = true;
    public static final String TAG = BluetoothServiceViaProtobuf.class.getCanonicalName();
    private int mState;
    private final BluetoothAdapter mAdapter;
    private List<Handler> handlerList;

    //THREADS
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private TransferThread mTransferThread;

    // private Activity activity;
    private Handler handler;
    private IBluetoothGameListener bluetoothGameListener;
    private String playerName;
    private int quantityOfAttemptToConnect = 0;

    public BluetoothServiceViaProtobuf(String playerName) {

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        handler = new Handler(Looper.getMainLooper());
        handlerList = new ArrayList<Handler>();
        this.playerName = playerName;
    }

    @Override
    public void registerListener(IBluetoothGameListener iServiceListener) {
        bluetoothGameListener = iServiceListener;
    }

    @Override
    public void unRegisterListener() {
        bluetoothGameListener = null;
    }


    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        for (Handler mHandler : handlerList)
            mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    @Override
    public void start() {
        if (D) Log.d(TAG, "start");
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // Cancel any thread currently running a connection
        if (mTransferThread != null) {
            mTransferThread.cancel();
            mTransferThread = null;
        }
        setState(STATE_LISTENING);
        // Start the thread to listen on a BluetoothServerSocket
//        if (mSecureAcceptThread == null) {
//            mSecureAcceptThread = new AcceptThread(true);
//            mSecureAcceptThread.start();
//        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
    }

    @Override
    public void sentPacket(AbstractMessageLite o) {
        mTransferThread.write(o);
    }

    @Override
    public void registerHandler(Handler handler) {
        handlerList.add(handler);
    }

    @Override
    public boolean unRegisterHandler(Handler handler) {
        return handlerList.remove(handler);
    }

    @Override
    public void stop() {
        if (D) Log.d(TAG, "stop");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mTransferThread != null) {
            mTransferThread.cancel();
            mTransferThread = null;
        }
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    @Override
    public int getState() {
        return mState;  //
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    @Override
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        if (D) Log.d(TAG, "connect to: " + device);
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        // Cancel any thread currently running a connection
        if (mTransferThread != null) {
            mTransferThread.cancel();
            mTransferThread = null;
        }
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the TransferThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been startTranferThread
     */
    public synchronized void startTranferThread(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        if (D) Log.d(TAG, "startTranferThread, Socket FieldType:" + socketType);
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // Cancel any thread currently running a connection
        if (mTransferThread != null) {
            mTransferThread.cancel();
            mTransferThread = null;
        }
        // Cancel the accept thread because we only want to connect to one device
//        if (mSecureAcceptThread != null) {
//            mSecureAcceptThread.cancel();
//            mSecureAcceptThread = null;
//        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        // Start the thread to manage the connection and perform transmissions
        mTransferThread = new TransferThread(socket, socketType);
        mTransferThread.start();
        // Send the name of the startTranferThread device back to the UI Activity
//        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothChat.TOAST, "Unable to connect device");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
        // Start the service over to restart listening mode
//        BluetoothServiceViaProtobuf.this.start();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (bluetoothGameListener != null) {
                    bluetoothGameListener.connectionFailed();
                }
            }
        });
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothChat.TOAST, "Device connection was lost");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
        // Start the service over to restart listening mode
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (bluetoothGameListener != null) {
                    bluetoothGameListener.playerExitFromGame();
                }
            }
        });
    }


    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    public class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(
                        NAME_INSECURE, MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "Socket FieldType: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "Socket FieldType: " + mSocketType +
                    "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);
            BluetoothSocket socket = null;
            // Listen to the server socket if we're not startTranferThread
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    Log.d(TAG, "Socket " + socket + " | ServerSocket " + mmServerSocket);
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast toast = Toast.makeText(activity, "created server for connecting ", Toast.LENGTH_LONG);
//                            toast.show();
//                        }
//                    });
                    socket = mmServerSocket.accept();
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast toast = Toast.makeText(activity, "! new accepted Socket !", Toast.LENGTH_LONG);
//                            toast.show();
//                        }
//                    });
                    Log.d(TAG, "new accepted Socket with " + socket.getRemoteDevice().getName());
                } catch (final Exception e) {
                    Log.e(TAG, "Socket FieldType: " + mSocketType + "accept() failed", e);
                    //    BluetoothServiceViaProtobuf.this.start();
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast toast = Toast.makeText(activity, "EXCEPTION " + e.toString(), Toast.LENGTH_LONG);
//                            toast.show();
//                        }
//                    });
                    return;
                }
                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothServiceViaProtobuf.this) {
                        switch (mState) {
                            case STATE_LISTENING:
                            case STATE_CONNECTING:
                                // Situation normal. Start the startTranferThread thread.
                                startTranferThread(socket, socket.getRemoteDevice(),
                                        mSocketType);
                                mState = STATE_CONNECTED;
                                return;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already startTranferThread. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D)
                Log.i(TAG, "END mAcceptThread, socket FieldType: " + mSocketType + "  " + BluetoothServiceViaProtobuf.this.getState());
        }

        public void cancel() {
            if (D) Log.d(TAG, "Socket FieldType" + mSocketType + "stop " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket FieldType" + mSocketType + "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;
        private boolean secure;


        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket bluetoothSocketTmp = null;
            this.secure = secure;
            mSocketType = secure ? "Secure" : "Insecure";
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    bluetoothSocketTmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID_SECURE);
                } else {
                    bluetoothSocketTmp = device.createInsecureRfcommSocketToServiceRecord(
                            MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket FieldType: " + mSocketType + "create() failed", e);
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity, "Fail in creating socket from device", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
            mmSocket = bluetoothSocketTmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);
            // Always stop discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast toast = Toast.makeText(activity, "SUCCESS startTranferThread  to " + mmSocket.getRemoteDevice().getName(), Toast.LENGTH_LONG);
//                        toast.show();
//                    }
//                });
            } catch (final Exception e) {
                // Close the socket
                Log.e(TAG, "connection failed : ", e);
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast toast = Toast.makeText(activity, "connection failed in creating socket: " + e.toString(), Toast.LENGTH_LONG);
//                        toast.show();
//                    }
//                });

                if (quantityOfAttemptToConnect++ < MAX_QUANTITY_CONNECT) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    connect(mmDevice, secure);
                } else {
                    connectionFailed();
                }
                return;
            }
            // Reset the ConnectThread because we're done
            synchronized (BluetoothServiceViaProtobuf.this) {
                mConnectThread = null;
            }
            // Start the transfer thread
            startTranferThread(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class TransferThread extends Thread {
        private final BluetoothSocket mmSocket;

        private final DataOutputStream dataOutputStream;
        private final DataInputStream dataInputStream;
        private final InputStream inputStream;
        private boolean isThreadWorking = true;

        public TransferThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create TransferThread: " + socketType);
            mmSocket = socket;
            DataInputStream tempDI = null;
            DataOutputStream tempDO = null;
            InputStream teStream = null;
            // Get the BluetoothSocket input and output streams
            try {
                teStream = socket.getInputStream();
                tempDI = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                tempDO = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            dataOutputStream = tempDO;
            dataInputStream = tempDI;
            inputStream = teStream;
        }

        public void run() {
            Log.i(TAG, "BEGIN mTransferThread");
            // Keep listening to the InputStream while startTranferThread
            sentPacket(BluetoothProtocol.StartGame.newBuilder().setOponentName(playerName).build());
            while (isThreadWorking) {
                try {
                    byte type = dataInputStream.readByte();
                    int length = dataInputStream.readInt();
                    byte data[] = new byte[length];
                    dataInputStream.read(data);
                    final BluetoothProtoType protoType = BluetoothProtoType.fromByte(type);
                    Log.d(TAG, "received  type " + protoType + " t " + type);

                    switch (protoType) {
                        case DID_MOVE:
                            final BluetoothProtocol.DidMove didMove =
                                    BluetoothProtocol.DidMove.parseFrom(data);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    OneMove oneMove = new OneMove(didMove.getI(), didMove.getJ(),
                                            (didMove.getType() == BluetoothProtocol.TypeMove.X)
                                                    ? TypeOfMove.X
                                                    : TypeOfMove.O);
                                    bluetoothGameListener.receivedNewOneMove(oneMove);
                                }
                            });
                            break;
                        case CHAT_MESSAGE:
                            final BluetoothProtocol.ChatMessage chatMessage =
                                    BluetoothProtocol.ChatMessage.parseFrom(data);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    bluetoothGameListener.receivedNewChatMessage(chatMessage.getMessage());
                                }
                            });
                            break;
                        case EXIT_FROM_GAME:
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    bluetoothGameListener.playerExitFromGame();
                                }
                            });
                            break;
                        case STAR_GAME:
                            final BluetoothProtocol.StartGame startGame =
                                    BluetoothProtocol.StartGame.parseFrom(data);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    bluetoothGameListener.startGame(startGame.getOponentName());
                                }
                            });
                            break;
                        case CONTINUE_GAME:
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    bluetoothGameListener.continueGame();
                                }
                            });
                            break;
                        case TIME_FINISHED:
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    bluetoothGameListener.opponentsTimeFinished();
                                }
                            });
                            break;
                    }
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(activity, "NEW MESSAGE + " + protoType.toString(), Toast.LENGTH_LONG).show();
//                        }
//                    });
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    return;
                }
            }
        }

        /**
         * Write to the startTranferThread OutStream.
         */
        public void write(AbstractMessageLite messageLite) {
            byte type = BluetoothProtoType.fromClass(messageLite.getClass()).getByteValue();
            byte data[] = messageLite.toByteArray();
            int length = data.length;
            Log.d(TAG, "send " + " type " + type);
            try {
                dataOutputStream.writeByte(type);
                dataOutputStream.writeInt(length);
                dataOutputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
//                        .sendToTarget();
        }

        public void cancel() {
            isThreadWorking = false;
            try {
                mmSocket.close();
                dataOutputStream.close();
                dataInputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
