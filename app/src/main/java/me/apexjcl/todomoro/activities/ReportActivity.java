package me.apexjcl.todomoro.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;
import me.apexjcl.todomoro.R;

/**
 * This activity will display a series of specific info about a single task
 */
public class ReportActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
        realm = null;
    }
}
