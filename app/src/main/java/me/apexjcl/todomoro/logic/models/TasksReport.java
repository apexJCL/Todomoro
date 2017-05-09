package me.apexjcl.todomoro.logic.models;

import java.util.List;

/**
 * Describes a more in-depth tasks details like
 * time averages and whatsoever
 *
 * Created by apex on 06/05/2017.
 */

public class wTasksReport {

    /**
     * Defines an average of how many days between a task
     * has been created and the 'due to' date.
     *
     * Calculated using basic media approach
     */
    private int daySpanAverage;
    /**
     * This is an average of a pomodoro-effective time taken
     * for a user to accomplish a given task
     */
    private long averageFinishTime;
    /**
     * This is an average time of slacking between or during a pomodoro's lifecycle
     */
    private long averageSlackTime;
    /**
     * A list of tasks that contain only general information
     */
    private List<LTask> tasks;

    public int getDaySpanAverage() {
        return daySpanAverage;
    }

    public void setDaySpanAverage(int daySpanAverage) {
        this.daySpanAverage = daySpanAverage;
    }

    public long getAverageFinishTime() {
        return averageFinishTime;
    }

    public void setAverageFinishTime(long averageFinishTime) {
        this.averageFinishTime = averageFinishTime;
    }

    public long getAverageSlackTime() {
        return averageSlackTime;
    }

    public void setAverageSlackTime(long averageSlackTime) {
        this.averageSlackTime = averageSlackTime;
    }

    public List<LTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<LTask> tasks) {
        this.tasks = tasks;
    }
}
