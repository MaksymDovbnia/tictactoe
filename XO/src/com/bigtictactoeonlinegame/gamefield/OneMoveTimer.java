package com.bigtictactoeonlinegame.gamefield;

import android.os.Handler;
import android.os.Looper;


/**
 * Created by Maksym on 06.01.14.
 */
public class OneMoveTimer {
    private TimerListener timerListener;
    private int time;
    private static final long ONE_SEC = 1000;
    private Handler handler;
    private volatile boolean isNeedInvokeFinish = false;
    private TimerThread timerThread;

    public OneMoveTimer(int timerTime, TimerListener timerListener) {
        time = timerTime;
        this.timerListener = timerListener;
        handler = new android.os.Handler(Looper.getMainLooper());
    }

    public void startNewTimer(boolean isNeedInvokeFinish) {
        this.isNeedInvokeFinish = isNeedInvokeFinish;
        if (timerThread != null) {
            timerThread.interrupt();
            timerThread = null;
        }
        timerThread = new TimerThread();
        timerThread.start();
    }

    public void setTimerListener(TimerListener timerListener) {
        this.timerListener = timerListener;
    }

    public void unRegisterListenerAndFinish() {
        timerListener = null;
        timerThread.interrupt();
        timerThread = null;
    }

    public static interface TimerListener {
        public void timeChanged(int time);

        public void timeFinished();
    }

    private class TimerThread extends Thread {
        private int passedSec = time;

        @Override
        public void run() {

            while (passedSec > 0) {
                try {
                    Thread.sleep(ONE_SEC);
                } catch (InterruptedException e) {
                    return;
                }
                passedSec--;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (timerListener != null) {
                            timerListener.timeChanged(passedSec);
                        }
                    }
                });
            }
            if (isNeedInvokeFinish) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (timerListener != null) {
                            timerListener.timeFinished();
                        }
                    }
                });
            }
        }
    }

}
