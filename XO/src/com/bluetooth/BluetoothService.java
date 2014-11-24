package com.bluetooth;

/**
 * @author Maksim Dovbnya(m.dovbnya@samsung.com)
 */

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

public interface BluetoothService<E> {
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_LISTENING = 2;
    public static final int STATE_NONE = 3;
    public static final int STATE_CONNECTING = 4;


    public void start();

    public void sentPacket(E o);

    public void registerHandler(Handler handler);

    public boolean unRegisterHandler(Handler handler);

    public void stop();

    public int getState();

    public void connect(BluetoothDevice device, boolean secure);

    public void registerListener(IBluetoothGameListener iServiceListener);

    public void unRegisterListener();

}