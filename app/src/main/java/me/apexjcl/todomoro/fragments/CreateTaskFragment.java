package me.apexjcl.todomoro.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import io.realm.Realm;
import me.apexjcl.todomoro.BuildConfig;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.fragments.dialogs.DatePickerDialogFragment;
import me.apexjcl.todomoro.fragments.dialogs.TimePickerDialogFragment;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Created by apex on 24/04/2017.
 */

public class CreateTaskFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, FloatingActionButton.OnClickListener,
        Realm.Transaction.OnSuccess, Realm.Transaction.OnError,
        ColorPickerSwatch.OnColorSelectedListener {

    public static final String TAG = "createTask";

    @BindView(R.id.dateLabel)
    TextView mDateLabel;
    @BindView(R.id.timeLabel)
    TextView mTimeLabel;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.title)
    TextInputLayout mTitleLayout;
    @BindView(R.id.description)
    EditText mDescription;
    @BindView(R.id.color_picker)
    ImageView mColorDot;

    private ColorPickerDialog mColorPicker;
    private DatePickerDialogFragment mDatePicker;
    private TimePickerDialogFragment mTimePicker;
    private DateFormat mFormatter;
    private int color;
    private int mSelectedColor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_create_task, container, false);
        ButterKnife.bind(this, v);
        init();
        return v;
    }

    private void init() {
        int[] mColors = getActivity().getResources().getIntArray(R.array.default_rainbow);
        mSelectedColor = ContextCompat.getColor(getContext(), R.color.flamingo);
        color = mSelectedColor;
        mDatePicker = new DatePickerDialogFragment();
        mTimePicker = new TimePickerDialogFragment();
        mColorPicker = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                mColors, mSelectedColor, 5, ColorPickerDialog.SIZE_SMALL, true);
        mColorPicker.setOnColorSelectedListener(this);
        mFormatter = SimpleDateFormat.getDateInstance();
        mDatePicker.setListener(this);
        mTimePicker.setListener(this);
        mFab.setOnClickListener(this);
        mColorDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorPicker.show(getActivity().getFragmentManager(), "picker");
            }
        });
        // Update labels
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
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mDatePicker.setDate(year, month, dayOfMonth);
        updateDateLabel();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mTimePicker.setTime(hourOfDay, minute);
        updateTimeLabel();
    }

    @Override
    public void onClick(View v) {
        if (!checkFields())
            return;
        mFab.hide();
        Task t = new Task();
        t.title = mTitleLayout.getEditText().getText().toString();
        t.description = mDescription.getText().toString();
        t.due = getDueDate();
        t.color = color;
        TaskHandler.createTask(t, this, this);
    }

    @Override
    public void onError(Throwable error) {
        if (BuildConfig.debug)
            error.printStackTrace();
        Toast.makeText(getContext(), R.string.error_creating_task, Toast.LENGTH_SHORT).show();
        mFab.show();
    }

    @Override
    public void onSuccess() {
        getFragmentManager().popBackStack();
    }

    private boolean checkFields() {
        String title = mTitleLayout.getEditText().getText().toString();
        if (title.length() == 0) {
            mTitleLayout.setError(getString(R.string.error_blank_title));
            return false;
        }
        return true;
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

    private void updateColorView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mColorDot.getDrawable().setTint(color);
        else
            mColorDot.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void updateDateLabel() {
        mDateLabel.setText(mFormatter.format(mDatePicker.getDate()));
    }

    private void updateTimeLabel() {
        mTimeLabel.setText(android.text.format.DateFormat.getTimeFormat(getContext()).format(mTimePicker.getTime()));
    }

    @OnClick(R.id.dateLabel)
    void showDatepicker() {
        mDatePicker.show(getFragmentManager(), "datePicker");
    }

    @OnClick(R.id.timeLabel)
    void showTimePicker() {
        mTimePicker.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onColorSelected(int color) {
        this.color = color;
        updateColorView();
        mColorPicker.dismiss();
    }
}
