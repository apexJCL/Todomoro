package me.apexjcl.todomoro.logic;

import io.realm.Realm;
import io.realm.Sort;
import me.apexjcl.todomoro.realm.handlers.PomodoroListHandler;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.PomodoroStatus;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Created by apex on 25/04/2017.
 * <p>
 * Handles pomodoro logic.
 * <p>
 * A pomodoro is divided in two cycles: Work cycle and Rest cycle.
 * <p>
 * Order goes:
 * <p>
 * Work cycle -> Rest cycle ->
 * Work cycle -> Rest cycle ->
 * Work cycle -> Rest cycle ->
 * Work cycle -> Long rest cycle
 * <p>
 * This completes 1 full cycle
 */

public class Pomodoro {

    public static final long POMODORO_CYCLE = 1_500_000;
    public static final long BREAK_CYCLE = 300_000;
    public static final long LONG_BREAK_CYCLE = 1_200_000;
//    public static final long POMODORO_CYCLE = 15_000;
//    public static final long BREAK_CYCLE = 5_000;
//    public static final long LONG_BREAK_CYCLE = 10_000;

    /**
     * How long will pomodoro run :3
     */
    private long remainingTime = POMODORO_CYCLE;

    private int elapsedPomodoros = 0;
    private int elapsedCycles = 0;

    private Realm realm;
    private STATUS actualStatus = STATUS.CYCLE;
    private Task mTask;

    private boolean finished = false;

    public Pomodoro(String task_id) {
        realm = Realm.getDefaultInstance();
        mTask = TaskHandler.getTaskSync(task_id, realm);
        initProperties();
    }

    public void start() {
        addEntry(false);
    }

    public void stop(long remainTime) {
        this.remainingTime = remainTime;
        addEntry(false);
    }

    private void addEntry(boolean finished) {
        if (this.finished)
            return;
        PomodoroListHandler.addEntry(
                actualStatus.name(),
                finished,
                getRemainingTime(),
                mTask.getId(),
                elapsedCycles,
                elapsedPomodoros,
                realm
        );
        this.finished = finished;
    }

    /**
     * Used to mark the end of a cycle, it automatically
     * sets the new remaining time according to events and whatsoever
     */
    public void finishCycle() {
        switch (actualStatus) {
            case CYCLE:
                elapsedPomodoros++;
                if (elapsedPomodoros < 4) { // switch to normal break
                    actualStatus = STATUS.BREAK;
                    remainingTime = BREAK_CYCLE;
                    break;
                }
                elapsedPomodoros = 0;
                elapsedCycles++;
                actualStatus = STATUS.LONG_BREAK;
                remainingTime = LONG_BREAK_CYCLE;
                break;
            case BREAK:
            case LONG_BREAK:
                actualStatus = STATUS.CYCLE;
                remainingTime = POMODORO_CYCLE;
                break;
        }
        addEntry(false);
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public STATUS stringToStatus(String status) {
        switch (status) {
            case "CYCLE":
                return STATUS.CYCLE;
            case "BREAK":
                return STATUS.BREAK;
            case "LONG_BREAK":
                return STATUS.LONG_BREAK;
            default:
                return STATUS.CYCLE;
        }
    }

    public long getCycleTime() {
        switch (actualStatus) {
            case CYCLE:
                return POMODORO_CYCLE;
            case BREAK:
                return BREAK_CYCLE;
            case LONG_BREAK:
                return LONG_BREAK_CYCLE;
            default:
                return 0;
        }
    }

    public float getCompletion() {
        return (1f / (getCycleTime() / (getCycleTime() - ((float) remainingTime - 1f)))) * 100;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void destroyed() {
        addEntry(finished);
        mTask.removeAllChangeListeners();
        realm.close();
        mTask = null;
    }

    public long[] getVibrationPattern() {
        switch (actualStatus) {
            case BREAK:
                return new long[]{0l, 1000l, 200l, 100l};
            case LONG_BREAK:
                return new long[]{0l, 500l, 200l, 1000l, 200l, 1000l};
            default:
            case CYCLE:
                return new long[]{0l, 300l, 200l, 300l};
        }
    }

    public STATUS getStatus() {
        return actualStatus;
    }

    public long getCompletedCycles() {
        return elapsedCycles;
    }

    public long getCurrentPomodoro() {
        return elapsedPomodoros;
    }

    public String getTaskTitle() {
        return mTask.isValid() ? mTask.getTitle() : "<loading data>";
    }

    private void initProperties() {
        if (mTask.getPomodoroStatusList().size() == 0)
            return;
        // We have a history, so load previous values
        PomodoroStatus status = mTask.getPomodoroStatusList().sort("time", Sort.ASCENDING).last();
        finished = status.isFinished();
        remainingTime = status.getRemaining();
        actualStatus = stringToStatus(status.getStatus());
        elapsedCycles = status.getCycle();
        elapsedPomodoros = status.getPomodoro_count();
    }

    public enum STATUS {
        CYCLE, BREAK, LONG_BREAK
    }

}
