package me.apexjcl.todomoro.activities;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.logic.Pomodoro;
import me.apexjcl.todomoro.logic.Timer;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;


public class PomodoroActivity extends AppCompatActivity implements Timer.TimerListener {


    @BindView(R.id.control_button)
    ImageView mControlButton;
    @BindView(R.id.fillProgress)
    CircularFillableLoaders mLoader;
    @BindView(R.id.timer_text)
    TextView mTimerText;
    @BindView(R.id.cycleCounter)
    TextView mCycleCounter;
    @BindView(R.id.pomodoroCounter)
    TextView mPomodoroCounter;

    public static final String TASK_ID = "task_id";

    private int notif_id = 1;

    private Pomodoro mPomodoro;
    private String mTaskId;
    private Timer mTimer;
    private Task mTask;

    private Vibrator mVibrator;
    private Ringtone mRingtone;

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
        mVibrator = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        mRingtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mTask = TaskHandler.getTask(mTaskId);
        getSupportActionBar().setTitle(mTask.getTitle());
        mPomodoro = new Pomodoro(mTask);
        mTimer = new Timer.Builder()
                .setDuration(mPomodoro.getCycleTime())
                .setRemaining(mPomodoro.getRemainingTime())
                .setListener(this)
                .setCountUpdate(250)
                .build();
        updateTimeLabel(mPomodoro.getRemainingTime());
        mLoader.setProgress((int) mPomodoro.getCompletion());
        mLoader.setAmplitudeRatio(0.001f);
        mCycleCounter.setText(String.valueOf(mPomodoro.getCompletedCycles()));
        mPomodoroCounter.setText(String.valueOf(mPomodoro.getCurrentPomodoro()));
    }

    @OnClick(R.id.control_button)
    void controlTimer() {
        switch (mTimer.getState()) {
            case INIT:
                mPomodoro.start();
                setPauseButton();
                mTimer.start();
                mLoader.setAmplitudeRatio(0.05f);
                break;
            case PAUSED:
                mPomodoro.start();
                setPauseButton();
                mTimer.start();
                mLoader.setAmplitudeRatio(0.05f);
                break;
            case RUNNING:
                mTimer.pause();
                mPomodoro.stop(mTimer.getRemaining());
                setPlayButton();
                mLoader.setAmplitudeRatio(0.001f);
                break;
            case FINISHED:
                setPlayButton();
                break;
        }
        mLoader.setColor(getProgressColor());
    }

    @OnClick(R.id.settingsButton)
    void settings() {
        Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
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

    private void updateTimeLabel(long tickUpdateInMillis) {
        long seconds = (tickUpdateInMillis / 1000) % 60;
        long minutes = (tickUpdateInMillis / (1000 * 60)) % 60;
        String time = String.format(Locale.US, "%02d:%02d", minutes, seconds);
        mTimerText.setText(time);
    }

    @Override
    protected void onDestroy() {
        // save status
        mTimer.destroy();
        mPomodoro.setRemainingTime(mTimer.getRemaining());
        mPomodoro.destroyed();
        mTask = null;
        mPomodoro = null;
        mTimer = null;
        mLoader = null;
        super.onDestroy();
    }

    @Override
    public void onTick(long milisUntilFinished) {
        mLoader.setProgress(calculatePercentage(mPomodoro.getCycleTime(), milisUntilFinished));
        updateTimeLabel(milisUntilFinished);
    }

    private int calculatePercentage(long cycleTime, long remainingTime) {
        return (int) ((1f / (cycleTime / (cycleTime - ((float) remainingTime - 1f)))) * 100);
    }

    @Override
    public void onFinishCountdown() {
        mPomodoro.finishCycle();

        mLoader.setColor(getProgressColor());
        mLoader.setProgress(100);
        mLoader.setAmplitudeRatio(0.001f);

        mTimer.setRemaining(mPomodoro.getCycleTime());
        updateTimeLabel(mTimer.getRemaining());

        mVibrator.vibrate(mPomodoro.getVibrationPattern(), -1);
        mRingtone.play();
        setPlayButton();
        // Update labels
        mCycleCounter.setText(String.valueOf(mPomodoro.getCompletedCycles()));
        mPomodoroCounter.setText(String.valueOf(mPomodoro.getCurrentPomodoro()));
    }

    public int getProgressColor() {
        int color_id;
        switch (mPomodoro.getStatus()) {
            case BREAK:
                color_id = R.color.breakColor;
                break;
            case LONG_BREAK:
                color_id = R.color.longBreakColor;
                break;
            default:
            case CYCLE:
                color_id = R.color.cycleColor;
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(color_id);
        }
        return getApplicationContext().getResources().getColor(color_id);
    }
}
