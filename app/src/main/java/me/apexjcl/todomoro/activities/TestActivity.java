package me.apexjcl.todomoro.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import me.apexjcl.todomoro.R;

/**
 * Created by apex on 03/05/2017.
 */

public class TestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Activity", savedInstanceState == null ? "Null Instance" : "Not Null Instance");
        Log.d("Activity", "onCreate Called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Activity", "onStart Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Activity", "onResume Called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Activity", "onRestart Called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Activity", "onPause Called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Activity", "onStop Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Activity", "onDestroy Called");
    }
}
