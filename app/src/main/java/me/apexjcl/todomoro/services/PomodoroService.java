package me.apexjcl.todomoro.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.activities.PomodoroActivity;
import me.apexjcl.todomoro.logic.Pomodoro;
import me.apexjcl.todomoro.logic.Timer;

/**
 * Created by apex on 03/05/2017.
 */

public class PomodoroService extends Service implements Timer.TimerListener {

    public static final String TASK_EXTRA = "taskId";
    public static final String SERVICE_NAME = "pomodoroService";
    public static final int NOTIFICATION_ID = 1337;

    private IBinder mBinder = new PomodoroBinder();
    private Timer.TimerListener mTimeListener;
    private boolean running = false;
    private Pomodoro mPomodoro;
    private String mTaskId;
    private Timer mTimer;
    private Vibrator mVibrator;
    private Ringtone mRingtone;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning())
            return START_REDELIVER_INTENT;
        running = true;
        mTaskId = intent.getStringExtra(TASK_EXTRA);
        init();
        startForeground(NOTIFICATION_ID, buildNotification());
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        if (isRunning()) {
            mPomodoro.destroyed();
            mTimer.destroy();
        }
        Log.d("PomodoroService", "onDestroy called");
        super.onDestroy();
    }

    public boolean isRunning() {
        return running;
    }

    private void init() {
        mVibrator = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        mRingtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mPomodoro = new Pomodoro(mTaskId);
        mTimer = new Timer.Builder()
                .setDuration(mPomodoro.getCycleTime())
                .setRemaining(mPomodoro.getRemainingTime())
                .setListener(this)
                .setCountUpdate(250)
                .build();
    }

    public void setTimeListener(Timer.TimerListener listener) {
        this.mTimeListener = listener;
    }

    private String getNotificationContent() {
        switch (mPomodoro.getStatus()) {
            case CYCLE:
                return getString(R.string.state_cycle);
            case BREAK:
                return getString(R.string.state_break);
            case LONG_BREAK:
                return getString(R.string.state_long_break);
        }
        return getString(R.string.state_unknown);
    }

    public String getTaskId() {
        return mTaskId;
    }

    public void removeListener() {
        mTimer.removeListener();
    }

    public void controlTimer() {
        Log.d("PomodoroService", String.format("Previous State pom: %s, timer: %s", mPomodoro.getStatus().name(), mTimer.getState().name()));
        switch (mTimer.getState()) {
            case INIT:
                mPomodoro.start();
                mTimer.start();
                break;
            case PAUSED:
                mPomodoro.start();
                mTimer.start();
                break;
            case RUNNING:
                mTimer.pause();
                mPomodoro.stop(mTimer.getRemaining());
                break;
            case FINISHED:
                break;
        }
        Log.d("PomodoroService", String.format("After State pom: %s, timer: %s", mPomodoro.getStatus().name(), mTimer.getState().name()));
    }

    private Notification buildNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.tomato)
                        .setContentTitle(mPomodoro.getTaskTitle())
                        .setContentText(getNotificationContent());
        Intent resultIntent = new Intent(this, PomodoroActivity.class);
        resultIntent.putExtra(PomodoroActivity.TASK_EXTRA, mTaskId);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.setAction(Intent.ACTION_MAIN);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = getNotificationManager();
        // mId allows you to update the notification later on.
        Notification mNotification = mBuilder.build();
        mNotification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        return mNotification;
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public Timer.STATE getTimerStatus() {
        return mTimer.getState();
    }

    public long getCycleTime() {
        return mPomodoro.getCycleTime();
    }

    @Override
    public void onTick(long milisUntilFinished) {
        if (this.mTimeListener == null)
            return;
        mTimeListener.onTick(milisUntilFinished);
    }

    @Override
    public void onFinishCountdown() {
        mPomodoro.finishCycle();
        mTimer.setRemaining(mPomodoro.getCycleTime());
        mVibrator.vibrate(mPomodoro.getVibrationPattern(), -1);
        mRingtone.play();
        buildNotification();
        if (this.mTimeListener == null)
            return;
        mTimeListener.onFinishCountdown();
    }

    public int getProgressColor() {
        int color_id;
        Log.d("Pomodoro", "Statis" + mPomodoro.getStatus().name());
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

    public long getRemaining() {
        return mTimer.getRemaining();
    }

    public String getCompletedCycles() {
        return String.valueOf(mPomodoro.getCompletedCycles());
    }

    public String getCurrentPomodoro() {
        return String.valueOf(mPomodoro.getCurrentPomodoro());
    }

    public String getTaskTitle() {
        return mPomodoro.getTaskTitle();
    }

    public void finish() {
        mTimer.pause();
        mPomodoro.stop(mTimer.getRemaining());
        running = false;
        getNotificationManager().cancel(NOTIFICATION_ID);
        stopSelf();
    }

    public class PomodoroBinder extends Binder {

        public PomodoroService getService() {
            return PomodoroService.this;
        }

    }

}
