package me.apexjcl.todomoro.logic.models;

/**
 * Created by apex on 06/05/2017.
 */

public class LTask {

    private long createdAt;
    private long updatedAt;
    private long finishedAt;
    private boolean finished;
    private long totalTime;
    private int cycles;
    private long pomodoroTime;

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(long finishedAt) {
        this.finishedAt = finishedAt;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public int getCycles() {
        return cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public void setPomodoroTime(long pomodoroTime) {
        this.pomodoroTime = pomodoroTime;
    }
}
