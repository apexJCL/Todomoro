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
import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimerView;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.logic.Pomodoro;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;


public class PomodoroActivity extends AppCompatActivity implements LinearTimer.TimerListener {

    @BindView(R.id.control_button)
    ImageView mControlButton;
    @BindView(R.id.linear_timer)
    LinearTimerView mTimerView;

    public static final String TASK_ID = "task_id";

    private LinearTimer mTimer;
    private Pomodoro mPomodoro;
    private String mTaskId;
    private Task mTask;

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
        initTimer();
    }

    private void initTimer() {
        mTimer = new LinearTimer.Builder()
                .linearTimerView(mTimerView)
                .duration(30_000)
                .timerListener(this)
                .getCountUpdate(LinearTimer.COUNT_DOWN_TIMER, 500)
                .build();
    }


    @OnClick(R.id.control_button)
    void controlTimer() {
        switch (mTimer.getState()) {
            case INITIALIZED:
                setPauseButton();
                mTimer.startTimer();
                break;
            case ACTIVE:
                setPlayButton();
                mTimer.pauseTimer();
                break;
            case PAUSED:
                setPauseButton();
                mTimer.resumeTimer();
                break;
            case FINISHED:
                break;
        }
        Log.d("timer", mTimer.getState().name());
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
        mTimer.pauseTimer();
        mTimer = null;
        super.onDestroy();
    }

    @Override
    public void animationComplete() {
        Log.d("timer", "animation complete");
    }

    @Override
    public void timerTick(long tickUpdateInMillis) {
        Log.d("timer", "Tick " + String.valueOf(tickUpdateInMillis));
    }

    @Override
    public void onTimerReset() {
        Log.d("timer", "reset");
    }

}
