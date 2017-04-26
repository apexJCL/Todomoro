package me.apexjcl.todomoro.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimerView;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.logic.Pomodoro;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;

import static me.apexjcl.todomoro.logic.Pomodoro.POMODORO_CYCLE;


public class PomodoroActivity extends AppCompatActivity implements LinearTimer.TimerListener {

    @BindView(R.id.timer)
    LinearTimerView mTimerView;
    @BindView(R.id.control_button)
    ImageButton mControlButton;
    @BindView(R.id.timer_text)
    TextView mTimerText;

    private LinearTimer mTimer;

    public static final String TASK_ID = "task_id";
    private String mTaskId;
    private Task mTask;
    private Pomodoro mPomodoro;
    private long remaining;

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
        remaining = mPomodoro.getRemainingTime();
        // initialize pomodoro status
        mTimer = new LinearTimer.Builder()
                .linearTimerView(mTimerView)
                .duration(POMODORO_CYCLE, mPomodoro.getElapsed())
                .progressDirection(LinearTimer.CLOCK_WISE_PROGRESSION)
                .timerListener(this)
                .getCountUpdate(LinearTimer.COUNT_DOWN_TIMER, 500)
                .build();
        updateTimeLabel(mPomodoro.getRemainingTime());
    }


    @OnClick(R.id.control_button)
    void controlTimer() {
        if (mTask.isFinished())
            return;
        updatePomodoro();
        switch (mTimer.getState()) {
            case INITIALIZED:
                if (!mPomodoro.canStart())
                    return;
                mTimer.startTimer();
                mControlButton.setImageDrawable(
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                                getDrawable(R.drawable.ic_pause_black_24dp) :
                                getResources().getDrawable(R.drawable.ic_pause_black_24dp)
                );
                break;
            case ACTIVE:
                mTimer.pauseTimer();
                mControlButton.setImageDrawable(
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                                getDrawable(R.drawable.ic_play_arrow_24dp) :
                                getResources().getDrawable(R.drawable.ic_play_arrow_24dp)
                );
                break;
            case PAUSED:
                if (!mPomodoro.canStart())
                    return;
                mTimer.resumeTimer();
                mControlButton.setImageDrawable(
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                                getDrawable(R.drawable.ic_pause_black_24dp) :
                                getResources().getDrawable(R.drawable.ic_pause_black_24dp)
                );
                break;
            case FINISHED:
                break;
        }
    }

    @Override
    public void animationComplete() { // When finished, call a finishCycle, so the entries are generated
        mPomodoro.finishCycle();    // also, this updates the new remaining time for a work cycle or break/long break cycle
        remaining = mPomodoro.getRemainingTime();
        reinitializeTimer();
    }

    @Override
    public void timerTick(long tickUpdateInMillis) {
        remaining = tickUpdateInMillis;
        mPomodoro.update(tickUpdateInMillis);
        updateTimeLabel(tickUpdateInMillis);
    }

    @Override
    public void onTimerReset() {

    }

    private void reinitializeTimer() {
        mTimer = new LinearTimer.Builder()
                .linearTimerView(mTimerView)
                .duration(remaining)
                .progressDirection(LinearTimer.CLOCK_WISE_PROGRESSION)
                .timerListener(this)
                .getCountUpdate(LinearTimer.COUNT_DOWN_TIMER, 500)
                .build();
        updateTimeLabel(remaining);
    }

    private void updatePomodoro() {
        switch (mPomodoro.getActualStatus()) {
            case INIT:
                mPomodoro.start();
                break;
            case LONG_BREAK:
            case BREAK:
            case CYCLE:
                mPomodoro.pause();
                break;
            case PAUSED:
                mPomodoro.resume();
                break;
            case DONE:
                mPomodoro.finish();
                break;
            case UNKNOWN:
                break;
        }
    }

    private void updateTimeLabel(long tickUpdateInMillis) {
        long seconds = (tickUpdateInMillis / 1000) % 60;
        long minutes = (tickUpdateInMillis / (1000 * 60)) % 60;
        String time = String.format(Locale.US, "%02d:%02d", minutes, seconds);
        mTimerText.setText(time);
    }

    @Override
    protected void onDestroy() {
        mTask = null;
        super.onDestroy();
    }
}
