package me.apexjcl.todomoro.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.labo.kaji.fragmentanimations.MoveAnimation;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmObjectChangeListener;
import me.apexjcl.todomoro.BuildConfig;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.fragments.dialogs.DatePickerDialogFragment;
import me.apexjcl.todomoro.fragments.dialogs.TimePickerDialogFragment;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Created by apex on 24/04/2017.
 */

public class EditTaskFragment extends Fragment implements FloatingActionButton.OnClickListener,
        Realm.Transaction.OnSuccess, Realm.Transaction.OnError, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, ColorPickerSwatch.OnColorSelectedListener {

    public static final String TAG = "editTask";
    public static final String TASK_ID = "taskId";

    private int color;

    private Task mTask;
    private ColorPickerDialog mColorPicker;
    private DatePickerDialogFragment mDatePicker;
    private TimePickerDialogFragment mTimePicker;
    private DateFormat mFormatter;

    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.title)
    TextInputLayout mTaskTitle;
    @BindView(R.id.description)
    EditText mDescription;
    @BindView(R.id.dateLabel)
    TextView mDateLabel;
    @BindView(R.id.timeLabel)
    TextView mTimeLabel;
    @BindView(R.id.color_picker)
    ImageView mColorDot;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments() == null) {
            getFragmentManager().popBackStack();
            return;
        }
        initCreate();
    }

    private void initCreate() {
        String task_id = getArguments().getString(TASK_ID);
        if (task_id == null) {
            getFragmentManager().popBackStack();
            return;
        }
        mDatePicker = new DatePickerDialogFragment();
        mTimePicker = new TimePickerDialogFragment();
        mFormatter = SimpleDateFormat.getDateInstance();
        mDatePicker.setListener(this);
        mTimePicker.setListener(this);
        int[] mColors = getActivity().getResources().getIntArray(R.array.default_rainbow);
        color = ContextCompat.getColor(getContext(), R.color.flamingo);
        mColorPicker = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                mColors, color, 5, ColorPickerDialog.SIZE_SMALL, true);
        mColorPicker.setOnColorSelectedListener(this);
        mTask = TaskHandler.getTaskAsync(task_id);
        mTask.addChangeListener(new RealmObjectChangeListener<Task>() {
            @Override
            public void onChange(Task object, ObjectChangeSet changeSet) {
                if (!object.isValid() || !object.isLoaded() || mTask == null)
                    return;
                init();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_task, container, false);
        ButterKnife.bind(this, v);
        mFab.setOnClickListener(this);
        mColorDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorPicker.show(getActivity().getFragmentManager(), "picker");
            }
        });
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTask = null;
    }

    private void init() {
        mTaskTitle.getEditText().setText(mTask.getTitle());
        color = mTask.getColor();
        mDescription.setText(mTask.getDescription());
        mColorDot.getDrawable().mutate().setColorFilter(mTask.getColor(), PorterDuff.Mode.SRC_IN);
        mDatePicker.setDate(mTask.getDue());
        mTimePicker.setTime(mTask.getDue());
        updateDateLabel();
        updateTimeLabel();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            mFab.show();
            return MoveAnimation.create(MoveAnimation.UP, enter, 600);
        }
        mFab.hide();
        return MoveAnimation.create(MoveAnimation.DOWN, enter, 250);
    }

    @Override
    public void onClick(View v) {
        if (!checkFields())
            return;
        Task t = new Task();
        t.id = mTask.getId();
        t.title = mTaskTitle.getEditText().getText().toString();
        t.description = mDescription.getText().toString();
        t.due = getDueDate();
        t.color = color;
        t.pomodoroCycles = mTask.pomodoroCycles;
        t.finished = mTask.finished;
        t.finishedAt = mTask.finishedAt;
        t.createdAt = mTask.getCreatedAt();
        t.updatedAt = mTask.getUpdatedAt();
        TaskHandler.saveTask(t, this, this);
    }

    private boolean checkFields() {
        String title = mTaskTitle.getEditText().getText().toString();
        if (title.length() == 0) {
            mTaskTitle.setError(getString(R.string.error_blank_title));
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onError(Throwable error) {
        if (BuildConfig.debug)
            error.printStackTrace();
        Toast.makeText(getContext(), R.string.error_creating_task, Toast.LENGTH_SHORT).show();
        mFab.show();
    }

    /**
     * Date / Time related stuff ----------------------------------------------------------------->
     */

    @OnClick(R.id.dateLabel)
    void showDatepicker() {
        mDatePicker.show(getFragmentManager(), "datePicker");
    }

    @OnClick(R.id.timeLabel)
    void showTimePicker() {
        mTimePicker.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mDatePicker.setDate(year, month, dayOfMonth);
        updateDateLabel();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mTimePicker.setTime(hourOfDay, minute);
        updateTimeLabel();
    }

    private void updateDateLabel() {
        mDateLabel.setText(mFormatter.format(mDatePicker.getDate()));
    }

    private void updateTimeLabel() {
        mTimeLabel.setText(android.text.format.DateFormat.getTimeFormat(getContext())
                .format(mTimePicker.getTime()));
    }

    private Date getDueDate() {
        Calendar c = Calendar.getInstance();
        c.set(
                mDatePicker.getYear(),
                mDatePicker.getMonth(),
                mDatePicker.getDay(),
                mTimePicker.getHour(),
                mTimePicker.getMinutes()
        );
        return c.getTime();
    }

    @Override
    public void onColorSelected(int color) {
        this.color = color;
        updateColorView();
        mColorPicker.dismiss();
    }

    private void updateColorView() {
        mColorDot.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
}
