package me.apexjcl.todomoro.realm.models;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import me.apexjcl.todomoro.fragments.EditTaskFragment;

/**
 * Defines a task and it's properties
 * Created by apex on 19/04/17.
 */
public class Task extends RealmObject {


    /**
     * Defines the field name that's currently the primary key
     */
    @Ignore
    public static final String PK = "id";

    /**
     * Task primary key, for easy handling :)
     */
    @PrimaryKey
    public String id = UUID.randomUUID().toString();
    /**
     * Title of the task, also primary key and indexing type
     */
    public String title = "";
    /**
     * Task description, can contain some notes. Supposed to support
     * Markdown
     */
    public String description = "";
    /**
     * Indicates whether the task is done
     */
    public boolean finished = false;
    /**
     * Indicates how many pomodoro cycles were used to accomplish the task
     */
    public int pomodoroCycles = 0;
    /**
     * When's the maximum date to accomplish the task
     */
    public Date due = new Date();
    /**
     * When the task was created
     */
    public Date createdAt = new Date();
    /**
     * When the task was updated
     */
    public Date updatedAt = new Date();
    /**
     * When the task was marked as "finished" or "completed"
     */
    public Date finishedAt = null;
    /**
     * Defines the task color, by default it's white
     */
    public int color = -1;
    /**
     * Each task can hold a list of pomodoro actions, such as
     * started, paused, finished
     */
    public RealmList<PomodoroStatus> pomodoroStatusList = new RealmList<>();

    public int getColor() {
        return color;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getPomodoroCycles() {
        return pomodoroCycles;
    }

    public Date getDue() {
        return due;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public RealmList<PomodoroStatus> getPomodoroStatusList() {
        return pomodoroStatusList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setPomodoroCycles(int pomodoroCycles) {
        this.pomodoroCycles = pomodoroCycles;
    }

    public void setDue(Date due) {
        this.due = due;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPomodoroStatusList(RealmList<PomodoroStatus> pomodoroStatusList) {
        this.pomodoroStatusList = pomodoroStatusList;
    }
}


