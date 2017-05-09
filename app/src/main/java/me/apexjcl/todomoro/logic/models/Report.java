package me.apexjcl.todomoro.logic.models;

/**
 * This object represents a single user statistics report
 * Created by apex on 06/05/2017.
 */

public class Report {

    /**
     * Identifies the user
     */
    private String username;
    /**
     * How many tasks are registered on the account
     */
    private int totalRegisteredTasks;
    /**
     * How many tasks have been finished
     */
    private int totalFinishedTasks;
    /**
     * An object that has a scope of more specific information about the tasks
     */
    private TasksReport tasksReport;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalRegisteredTasks() {
        return totalRegisteredTasks;
    }

    public void setTotalRegisteredTasks(int totalRegisteredTasks) {
        this.totalRegisteredTasks = totalRegisteredTasks;
    }

    public int getTotalFinishedTasks() {
        return totalFinishedTasks;
    }

    public void setTotalFinishedTasks(int totalFinishedTasks) {
        this.totalFinishedTasks = totalFinishedTasks;
    }

    public TasksReport getTasksReport() {
        return tasksReport;
    }

    public void setTasksReport(TasksReport tasksReport) {
        this.tasksReport = tasksReport;
    }
}
