package me.apexjcl.todomoro.realm.models;

import java.util.Date;

import io.realm.RealmObject;
import me.apexjcl.todomoro.logic.Pomodoro;

/**
 * Created by apex on 24/04/2017.
 */

public class PomodoroStatus extends RealmObject {

    /**
     * Defines the status in which the pomodoro was changed
     * ex: PAUSED- when the pomodoro was put to a pause
     */
    private String status;
    /**
     * To mark WHEN did the event ocurred.
     * <p>
     * ex: 27 Apr at 20:12
     */
    private Date time = new Date();
    /**
     * Inidicates if the current cycle was terminated
     */
    private boolean finished = false;
    /**
     * If any, remaining millis on the timer
     */
    private long remaining = Pomodoro.POMODORO_CYCLE;
    /**
     * Indicates the current pomodoro cycle (1 cycle = 4 pomodoros + 4 breaks)
     */
    private int cycle = 0;
    /**
     * Used to mark the actual pomodoro.
     * ex: After 1 pomodoro = 1, after 2 = 2, after 3 = 3, after 4 = 0 and cycle goes up 1
     */
    private int pomodoro_count = 0;

    public String getStatus() {
        return status;
    }

    public Date getTime() {
        return time;
    }

    public long getRemaining() {
        return remaining;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getPomodoro_count() {
        return pomodoro_count;
    }

    public void setPomodoro_count(int pomodoro_count) {
        this.pomodoro_count = pomodoro_count;
    }
}
