package me.apexjcl.todomoro.logic;

import android.os.CountDownTimer;
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

    public Timer(long duration, long remaining, long countUpdate, @Nullable final TimerListener listener) {
        this.listener = listener;
        this.remaining = remaining;
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
     * Sets the timer remaining time
     *
     * @param remaining
     */
    public void setRemaining(long remaining) {
        this.remaining = remaining;
        this.duration = remaining;
        this.state = STATE.INIT;
    }

    public float getCompletion() {
        return 1f / (duration / (duration - ((float) remaining - 1f)));
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
        lastCalled = System.currentTimeMillis();
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
        long called = System.currentTimeMillis();
        if ((called - lastCalled) < countUpdate)
            return false;
        lastCalled = called;
        return true;
    }

    public long getRemaining() {
        return remaining;
    }

    public void destroy() {
        if (mTimer == null)
            return;
        this.mTimer.cancel();
        this.listener = null;
    }

    public enum STATE {
        INIT, PAUSED, RUNNING, FINISHED
    }


    public static class Builder {

        private long remaining = -1;
        private long duration;
        private long countUpdate = 1000;
        private TimerListener listener;

        public Builder() {

        }

        public Builder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder setListener(TimerListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setCountUpdate(long countUpdate) {
            this.countUpdate = countUpdate;
            return this;
        }

        public Builder setRemaining(long remaining) {
            this.remaining = remaining;
            return this;
        }

        public Timer build() {
            if (remaining == -1)
                return new Timer(duration, duration, countUpdate, listener);
            return new Timer(duration, remaining, countUpdate, listener);
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
