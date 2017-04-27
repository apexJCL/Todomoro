package me.apexjcl.todomoro.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;
import cn.iwgang.countdownview.DynamicConfig;
import flepsik.github.com.progress_ring.ProgressRingView;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.logic.Pomodoro;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;


public class PomodoroActivity extends AppCompatActivity implements CountdownView.OnCountdownIntervalListener, CountdownView.OnCountdownEndListener {

    @BindView(R.id.progressRingView)
    ProgressRingView mRingView;
    @BindView(R.id.control_button)
    ImageView mControlButton;
    @BindView(R.id.countdownView)
    CountdownView mCountdownView;

    public static final String TASK_ID = "task_id";
    private String mTaskId;
    private Task mTask;
    private Pomodoro mPomodoro;
    private boolean autoCycle = true;
    private boolean started = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        mTaskId = getIntent().getStringExtra(TASK_ID);
        if (mTaskId == null) {
            finish();
            return;
        }
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mTask = (Task) TaskHandler.getTask(mTaskId);
        mPomodoro = new Pomodoro(mTask);
        mRingView.setProgress(calculaFill(mPomodoro.getCycleTime(), mPomodoro.getRemainingTime()));
        mRingView.setAnimated(true);
        mCountdownView.setOnCountdownEndListener(this);
        mCountdownView.setOnCountdownIntervalListener(500, this);
    }


    @OnClick(R.id.control_button)
    void controlTimer() {
        if (started) {
            started = false;
            mCountdownView.stop();
            mPomodoro.stop(mCountdownView.getRemainTime());
            setPlayButton();
            return;
        }
        started = true;
        mPomodoro.start();
        setPauseButton();
        mCountdownView.start(mPomodoro.getRemainingTime());
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
    protected void onDestroy() {
        mTask = null;
        mPomodoro = null;
        super.onDestroy();
    }

    @Override
    public void onInterval(CountdownView cv, long remainingTime) {
        mRingView.setProgress(calculaFill(mPomodoro.getCycleTime(), remainingTime));
    }

    private float calculaFill(long cycleTime, long remainingTime) {
        return 1f / (cycleTime / (cycleTime - ((float) remainingTime - 1f)));
    }

    @Override
    public void onEnd(CountdownView cv) { // Here we will handle cycle logic chain
        Log.d("CounterView", "onEnd");
        mPomodoro.setRemainingTime(0);
        mPomodoro.finishCycle();
        if (!autoCycle) {
            started = false;
            setPlayButton();
            return;
        }
        mCountdownView.start(mPomodoro.getRemainingTime());
    }
}
