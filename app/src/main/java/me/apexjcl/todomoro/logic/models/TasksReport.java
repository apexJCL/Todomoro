package me.apexjcl.todomoro.logic.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import me.apexjcl.todomoro.logic.Pomodoro;
import me.apexjcl.todomoro.realm.models.PomodoroStatus;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Describes a more in-depth tasks details like
 * time averages and whatsoever
 * <p>
 * Created by apex on 06/05/2017.
 */

public class TasksReport {

    /**
     * Defines an average of how many days between a task
     * has been created and the 'due to' date.
     * <p>
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

    public static TasksReport generateReport(Realm realm) {
        TasksReport t = new TasksReport();
        t.setDaySpanAverage(getDaySpanAverage(realm.where(Task.class).equalTo("finished", true).findAll()));
        OrderedRealmCollection<Task> tasks = realm.where(Task.class).findAll();
        ArrayList<LTask> strippedTasks = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            LTask ltask = new LTask();
            ltask.setCreatedAt(task.getCreatedAt().getTime());
            ltask.setUpdatedAt(task.getUpdatedAt().getTime());
            ltask.setFinished(task.isFinished());
            if (task.isFinished()) {
                ltask.setFinishedAt(task.getFinishedAt().getTime());
                // Gather pomodoro stuff
                RealmList<PomodoroStatus> pomodoroStatusList = task.getPomodoroStatusList();
                long pomodoroTime = 0;
                if (pomodoroStatusList.size() > 0) {
                    // Iter through each register and calculate time spent on a pomodoro
                    Pomodoro.STATUS mStatus = Pomodoro.STATUS.CYCLE;
                    for (int i = 0; i < pomodoroStatusList.size(); i++) {
                        Pomodoro.STATUS actualStatus = Pomodoro.stringToStatus(pomodoroStatusList.get(i).getStatus());
                        if (actualStatus != mStatus) {
                            switch (mStatus) {
                                case CYCLE:
                                    pomodoroTime += Pomodoro.POMODORO_CYCLE;
                                    break;
                                case BREAK:
                                    pomodoroTime += Pomodoro.BREAK_CYCLE;
                                    break;
                                case LONG_BREAK:
                                    pomodoroTime += Pomodoro.LONG_BREAK_CYCLE;
                                    break;
                            }
                            mStatus = actualStatus;
                        }
                        if (i == pomodoroStatusList.size() - 1) {
                            switch (actualStatus) {
                                case CYCLE:
                                    pomodoroTime += (Pomodoro.POMODORO_CYCLE - pomodoroStatusList.get(i).getRemaining());
                                    break;
                                case BREAK:
                                    pomodoroTime += (Pomodoro.BREAK_CYCLE - pomodoroStatusList.get(i).getRemaining());
                                    break;
                                case LONG_BREAK:
                                    pomodoroTime += (Pomodoro.LONG_BREAK_CYCLE - pomodoroStatusList.get(i).getRemaining());
                                    break;
                            }
                        }
                    }
                    long totalTime = pomodoroStatusList.last().getTime().getTime() - pomodoroStatusList.first().getTime().getTime();
                    ltask.setPomodoroTime(pomodoroTime);
                    ltask.setTotalTime(totalTime);
                    ltask.setCycles(pomodoroStatusList.last().getCycle());
                }
            }
            strippedTasks.add(ltask);
        }
        t.setTasks(strippedTasks);
        return t;
    }

    private static int getDaySpanAverage(OrderedRealmCollection<Task> finishedTasks) {
        float span = 0;
        float total = finishedTasks.size();
        for (Task t : finishedTasks) {
            Calendar createdAt = Calendar.getInstance();
            createdAt.setTime(t.getCreatedAt());
            Calendar finishedAt = Calendar.getInstance();
            finishedAt.setTime(t.getFinishedAt());
            Calendar dueAt = Calendar.getInstance();
            dueAt.setTime(t.getDue());
            span += finishedAt.get(Calendar.DAY_OF_YEAR) - createdAt.get(Calendar.DAY_OF_YEAR);
        }
        return (int) Math.ceil(span / total);
    }
}
