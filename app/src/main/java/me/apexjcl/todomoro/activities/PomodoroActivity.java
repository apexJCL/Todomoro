package me.apexjcl.todomoro.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.Locale;

import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.logic.Timer;
import me.apexjcl.todomoro.services.PomodoroService;

/**
 * Activity that will handle the pomodoro stuff :3
 */
public class PomodoroActivity extends TestActivity implements ServiceConnection, View.OnClickListener,
        Timer.TimerListener {

    public static final String TASK_EXTRA = "task_id";

    public static final int NOTIFICATION_ID = 1337; // h4x0r

    private CircularFillableLoaders mLoader;
    private TextView mPomodoroCounter;
    private ImageView mControlButton;
    private TextView mCycleCounter;
    private TextView mTimerText;

    private PomodoroService mService;
    private boolean mBound = false;
    private String mTaskId;

    ////////////// Activity Status and Config Functionality /////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        mTaskId = getIntent().getStringExtra(TASK_EXTRA);
        if (mTaskId == null) {
            finish();
            return;
        }
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(this, PomodoroService.class);
        i.putExtra(PomodoroService.TASK_EXTRA, mTaskId);
        bindService(i, this, Context.BIND_AUTO_CREATE);
        startService(i);
    }

    @Override
    protected void onStop() {
        unbindService(this);
        super.onStop();
    }

    /*
    ////////////// General Functionality ////////////////////////////////////
     */

    private void init() {
        mLoader = (CircularFillableLoaders) findViewById(R.id.fillProgress);
        mTimerText = (TextView) findViewById(R.id.timer_text);
        mCycleCounter = (TextView) findViewById(R.id.cycleCounter);
        mPomodoroCounter = (TextView) findViewById(R.id.pomodoroCounter);
        mControlButton = (ImageView) findViewById(R.id.control_button);
        mControlButton.setOnClickListener(this);
    }

    ////////////// Service Methods ///////////////////////////////////////////

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.mService = ((PomodoroService.PomodoroBinder) service).getService();
        this.mService.setTimeListener(this);
        initUI();
        mBound = true;
    }

    private void initUI() {
        getSupportActionBar().setTitle(mService.getTaskTitle());
        try {
            mLoader.setColor(mService.getProgressColor());
        } catch (Exception e) {// La vieja confiable :v
        }
        updateTimeLabel(mService.getRemaining());
        mCycleCounter.setText(mService.getCompletedCycles());
        mPomodoroCounter.setText(mService.getCurrentPomodoro());
        Log.d("PomodoroService", String.format("Status to update button is: %s", mService.getTimerStatus()));
        switch (mService.getTimerStatus()) {
            case INIT:
                setPlayButton();
                break;
            case PAUSED:
                setPlayButton();
                break;
            case RUNNING:
                setPauseButton();
                break;
            case FINISHED:
                setPlayButton();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mService.finish();
        super.onBackPressed();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService.removeListener();
        mBound = false;
    }

    @Override
    public void onClick(View v) {
        mService.controlTimer();
        initUI();
    }

    private void setPlayButton() {
        mControlButton.setImageDrawable(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                        getDrawable(R.drawable.ic_play_arrow_24dp) :
                        getResources().getDrawable(R.drawable.ic_play_arrow_24dp)
        );
    }

    private void setPauseButton() {
        mControlButton.setImageDrawable(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                        getDrawable(R.drawable.ic_pause_black_24dp) :
                        getResources().getDrawable(R.drawable.ic_pause_black_24dp)
        );
    }

    @Override
    public void onTick(long milisUntilFinished) {
        int progress = calculatePercentage(mService.getCycleTime(), milisUntilFinished);
        mLoader.setProgress(progress);
        updateTimeLabel(milisUntilFinished);
    }

    @Override
    public void onFinishCountdown() {
        setPlayButton();
        mLoader.setColor(mService.getProgressColor());
        mLoader.setProgress(100);
        mLoader.setAmplitudeRatio(0.001f);
        updateTimeLabel(mService.getRemaining());
        mCycleCounter.setText(mService.getCompletedCycles());
        mPomodoroCounter.setText(mService.getCurrentPomodoro());
    }

    private void updateTimeLabel(long tickUpdateInMillis) {
        long seconds = (tickUpdateInMillis / 1000) % 60;
        long minutes = (tickUpdateInMillis / (1000 * 60)) % 60;
        String time = String.format(Locale.US, "%02d:%02d", minutes, seconds);
        mTimerText.setText(time);
    }

    private int calculatePercentage(long cycleTime, long remainingTime) {
        Log.d("Pomodoro", String.format("Time cycle: %d, remaining: %d ", cycleTime, remainingTime));
        return (int) ((1f / (cycleTime / (cycleTime - ((float) remainingTime - 1f)))) * 100);
    }
}
