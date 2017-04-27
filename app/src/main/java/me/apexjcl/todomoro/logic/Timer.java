package me.apexjcl.todomoro.logic;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.Nullable;

/**
 * Defines a timer (countdown only by now)
 * <p>
 * Created by apex on 27/04/2017.
 */

public class Timer {

    private TimerListener listener;
    private CountDownTimer mTimer;
    private STATE state;
    /**
     * This will be used for the countUpdate
     */
    private long refreshRate = 100;
    private long lastCalled;
    private long countUpdate;
    private long duration;
    private long remaining;

    public Timer(long duration, long countUpdate, @Nullable final TimerListener listener) {
        this.listener = listener;
        this.remaining = duration;
        this.duration = duration;
        this.countUpdate = countUpdate;
        state = STATE.INIT;
    }

    /**
     * Starts the timer
     */
    public void start() {
        if (state == STATE.INIT || state == STATE.PAUSED) {
            state = STATE.RUNNING;
            this.mTimer = buildTimer();
            mTimer.start();
        }
    }

    public void pause() {
        mTimer.cancel();
        state = STATE.PAUSED;
    }

    public void reset() {
        remaining = duration;
        state = STATE.INIT;
    }

    /**
     * Returns the timer actual state
     *
     * @return
     */
    public STATE getState() {
        return state;
    }

    private CountDownTimer buildTimer() {
        lastCalled = SystemClock.currentThreadTimeMillis();
        return new CountDownTimer(remaining, refreshRate) {
            @Override
            public void onTick(long millisUntilFinished) {
                remaining = millisUntilFinished;
                if (listener == null)
                    return;
                if (isCallable())
                    listener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                remaining = 0;
                if (listener == null)
                    return;
                listener.onFinishCountdown();
                state = STATE.FINISHED;
            }
        };
    }

    private boolean isCallable() {
        long called = SystemClock.currentThreadTimeMillis();
        if ((called - lastCalled) < countUpdate)
            return false;
        lastCalled = called;
        return false;
    }

    enum STATE {
        INIT, PAUSED, RUNNING, FINISHED
    }


    public class Builder {

        private long duration;
        private long countUpdate = 1000;
        private TimerListener listener;

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public void setListener(TimerListener listener) {
            this.listener = listener;
        }

        public void setCountUpdate(long countUpdate) {
            this.countUpdate = countUpdate;
        }

        public Timer build() {
            return new Timer(duration, countUpdate, listener);
        }

    }

    public interface TimerListener {

        /**
         * It will be called on a regular basis, by default, each 1000ms, or
         * what countUpdate was specified
         *
         * @param milisUntilFinished
         */
        void onTick(long milisUntilFinished);

        /**
         * Called when the coundown ends
         */
        void onFinishCountdown();

    }

}
