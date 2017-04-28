package me.apexjcl.todomoro.realm;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import me.apexjcl.todomoro.TodomoroApplication;
import me.apexjcl.todomoro.fragments.LoginFragment;

/**
 * Handles Realm user configuration
 * Created by apex on 22/04/17.
 */
public class UserManager {

    private static String username;

    public static boolean isSessionAvailable() {
        SyncUser user = SyncUser.currentUser();
        return user != null && user.isValid();
    }

    public static void setActiveUser(SyncUser user) {
        SyncConfiguration configuration = new SyncConfiguration.Builder(
                user,
                TodomoroApplication.ROS_REALM_URL
        ).schemaVersion(TodomoroApplication.SCHEMA_VERSION).build();
        Realm.removeDefaultConfiguration();
        Realm.setDefaultConfiguration(configuration);
    }

    public static void logoutCurrentUser() {
        SyncUser.currentUser().logout();
    }

    public static String getUsername() {
        return username;
    }
}
