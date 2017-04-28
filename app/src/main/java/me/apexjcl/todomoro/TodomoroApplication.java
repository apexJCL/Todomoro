package me.apexjcl.todomoro;

import android.app.Application;
import io.realm.Realm;
import io.realm.SyncSession;
import io.realm.SyncUser;

/**
 * Created by apex on 22/04/17.
 */
public class TodomoroApplication extends Application {

    public static final String ROS_BASE_URL = BuildConfig.OBJECT_SERVER_IP + BuildConfig.OBJECT_SERVER_PORT;
    public static final String ROS_AUTH_URL = BuildConfig.OBJECT_SERVER_PROTOCOL + ROS_BASE_URL + "/auth";
    public static final String ROS_REALM_URL = "realm://" + ROS_BASE_URL + "/~/todomoro";
    public static final long SCHEMA_VERSION = BuildConfig.SCHEMA_VERSION;
    public static final String SHARED_PREFS = "todomoro_prefs";
    public static final String PREFS_USERNAME = "username";

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
