package me.apexjcl.todomoro.realm.handlers;

import io.realm.Realm;
import me.apexjcl.todomoro.realm.models.PomodoroStatus;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Takes charge on pomodoro's stuff :3 (i'm half-drunk)
 * <p>
 * Created by apex on 24/04/2017.
 */

public class PomodoroListHandler {

    public static long getRemaining(Task task, long defaultValue) {
        return task.getPomodoroStatusList().size() == 0 ? defaultValue : task.getPomodoroStatusList().last().getRemaining();
    }

    // TODO: fix the add entry mess

    public static void addEntry(final String status, final boolean finished, final long remaining, final String task_id, final int cycle, final int pomodoro_count) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task t = realm.where(Task.class).equalTo(Task.PK, task_id).findFirst();
                PomodoroStatus s = realm.createObject(PomodoroStatus.class);
                s.setStatus(status);
                s.setFinished(finished);
                s.setRemaining(remaining);
                t.getPomodoroStatusList().add(s);
            }
        });
        realm.close();
    }

}