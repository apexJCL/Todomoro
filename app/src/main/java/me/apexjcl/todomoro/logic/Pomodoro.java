package me.apexjcl.todomoro.logic;

import me.apexjcl.todomoro.realm.handlers.PomodoroListHandler;
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

    private long remaining = POMODORO_CYCLE;
    private Status previousStatus = Status.INIT;
    private Status actualStatus = Status.INIT;
    /**
     * Indicates how many pomodoros has been completed (1 pomodoro = 25 minutes)
     */
    private int completedPomodoros = 0;
    /**
     * Indicates how many cycles has been completed (1 cycle = 4 Pomodoros + 4 breaks)
     */
    private int completedCycles = 0;
    /**
     * Represents the task that the pomodoro belongs to
     */
    private Task mTask;

    /**
     * Initializes a new pomodoro
     */
    public Pomodoro(Task task) {
        this.mTask = task;
        if (task.getPomodoroStatusList().size() == 0)
            return;
        PomodoroStatus status = task.getPomodoroStatusList().last();
        remaining = status.getRemaining();
        previousStatus = stringToStatus(status.getPreviousStatus());
        actualStatus = stringToStatus(status.getStatus());
    }

    /**
     * Used to begin the pomodoro
     */
    public void start() {
        actualStatus = Status.CYCLE;
        addEntry(false);
    }

    public void pause() {
        previousStatus = actualStatus;
        actualStatus = Status.PAUSED;
        addEntry(false);
    }

    public void resume() {
        actualStatus = previousStatus;
        previousStatus = Status.PAUSED;
        addEntry(false);
    }

    public void finish() {
        previousStatus = actualStatus;
        actualStatus = Status.DONE;
        addEntry(true);
    }

    /**
     * Used to indicate that a cycle (either a break or a normal work cycle) has come
     * to an end
     */
    public void finishCycle() {
        previousStatus = actualStatus;
        update(0);
    }

    /**
     * This method should be called on the timing thread, it keeps up
     * to date Pomodoro data and checks for cycle logic
     */
    public void update(long timeRemaining) {
        this.remaining = timeRemaining;
        if (remaining > 0) // nothing to do here, so go on :)
            return;

        switch (actualStatus) {
            case CYCLE:
                fromCycleToBreak();
                break;
            case BREAK:
                fromBreakToCycle();
                break;
        }
    }

    private void fromCycleToBreak() {
        completedPomodoros++;
        if (completedPomodoros < 4) { // Put remaining for regular break cycle
            remaining = BREAK_CYCLE;
            actualStatus = Status.BREAK;
            addEntry(false);
            return;
        }
        completedCycles++;
        completedPomodoros = 0;
        remaining = LONG_BREAK_CYCLE;
        actualStatus = Status.LONG_BREAK;
        addEntry(true);
    }

    private void fromBreakToCycle() {
        actualStatus = Status.CYCLE;
        remaining = POMODORO_CYCLE;
        addEntry(true);
    }

    private Status stringToStatus(String status) {
        switch (status) {
            case "INIT":
                return Status.INIT;
            case "CYCLE":
                return Status.CYCLE;
            case "BREAK":
                return Status.BREAK;
            case "LONG_BREAK":
                return Status.LONG_BREAK;
            case "PAUSED":
                return Status.PAUSED;
            case "DONE":
                return Status.DONE;
            default:
                return Status.UNKNOWN;
        }
    }

    public long getRemainingTime() {
        return remaining;
    }

    public long getElapsed() {
        switch (previousStatus) {
            case CYCLE:
                return POMODORO_CYCLE - remaining;
            case BREAK:
                return BREAK_CYCLE - remaining;
            case LONG_BREAK:
                return LONG_BREAK_CYCLE - remaining;
            default:
                return 0;
        }
    }

    /**
     * Tells whether or not the Pomodoro can start.
     * <p>
     * A pomodoro can only start if:
     * <ul>
     * <li>
     * It's not marked as done yet
     * </li>
     * <li>
     * If the remaining time it's greater than 0
     * </li>
     * <li>
     * If it's paused
     * </li>
     * </ul>
     *
     * @return If the pomodoro can start
     */
    public boolean canStart() {
        return actualStatus != Status.DONE && remaining > 0;
    }

    public Status getActualStatus() {
        return actualStatus;
    }

    private void addEntry(boolean finished) {
        PomodoroListHandler.addEntry(
                previousStatus.name(),
                actualStatus.name(),
                finished,
                remaining,
                mTask.getId(),
                completedCycles,
                completedPomodoros
        );
    }

    public enum Status {
        INIT, CYCLE, BREAK, LONG_BREAK, PAUSED, DONE, RESUMING, UNKNOWN
    }

}
