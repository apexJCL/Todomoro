package me.apexjcl.todomoro.realm.handlers;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Handles realm events related to tasks
 * <p>
 * Created by apex on 22/04/17.
 */
public class TaskHandler {

    public static RealmResults<Task> getUnfinishedTasks(Realm realm) {
        return realm.where(Task.class).equalTo("finished", false).findAll();
    }

    public static OrderedRealmCollection<Task> getFinishedTasks(Realm realm) {
        return realm.where(Task.class).equalTo("finished", true).findAllAsync();
    }

    public static void createTask(final Task task, Realm.Transaction.OnSuccess success, Realm.Transaction.OnError error) {
        Realm r = Realm.getDefaultInstance();
        r.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(task);
            }
        }, success, error);
        r.close();
    }

    public static RealmObject find(String taskId) {
        Realm r = Realm.getDefaultInstance();
        RealmObject task = r.where(Task.class).equalTo(Task.PK, taskId).findFirst();
        r.close();
        return task;
    }

    public static void deleteAll(Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Task> all = realm.where(Task.class).findAll();
                for (Task task : all) {
                    task.deleteFromRealm();
                }
            }
        });
    }

    public static void delete(final String mTaskId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmObject t = realm.where(Task.class).equalTo(Task.PK, mTaskId).findFirst();
                t.deleteFromRealm();
            }
        });
        realm.close();
    }

    public static void markFinished(final String taskId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task t = realm.where(Task.class).equalTo(Task.PK, taskId).findFirst();
                t.finished = true;
                realm.copyToRealmOrUpdate(t);
            }
        });
        realm.close();
    }

    public static Task getTask(String mTaskId) {
        Realm realm = Realm.getDefaultInstance();
        Task task = realm.where(Task.class).equalTo(Task.PK, mTaskId).findFirst();
        realm.close();
        return task;
    }

    public static Task getTask(String mTaskId, Realm realm) {
        return realm.where(Task.class).equalTo(Task.PK, mTaskId).findFirstAsync();
    }

    public static Task getTaskAsync(String mTaskId) {
        Realm realm = Realm.getDefaultInstance();
        Task t = realm.where(Task.class).equalTo(Task.PK, mTaskId).findFirstAsync();
        realm.close();
        return t;
    }

    public static void saveTask(final Task task, Realm.Transaction.OnSuccess success, Realm.Transaction.OnError error) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(task);
            }
        }, success, error);
        realm.close();
    }
}
