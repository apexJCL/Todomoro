package me.apexjcl.todomoro.fragments.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

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
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Create a new task :3
 * Created by apex on 22/04/17.
 */
public class CreateTaskDialogFragment extends BottomSheetDialogFragment
        implements DialogInterface.OnShowListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, FloatingActionButton.OnClickListener, Realm.Transaction.OnError,
        Realm.Transaction.OnSuccess, ColorPickerCallback {

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

    private DatePickerDialogFragment mDatePicker;
    private TimePickerDialogFragment mTimePicker;
    private DateFormat mFormatter;
    private ColorPicker mColorPicker;
    private int color;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Theme_Design_Light_BottomSheetDialog_Todomoro);
        mDatePicker = new DatePickerDialogFragment();
        mTimePicker = new TimePickerDialogFragment();
        mColorPicker = new ColorPicker(getActivity());
        mColorPicker.setCallback(this);
        mFormatter = SimpleDateFormat.getDateInstance();
        mDatePicker.setListener(this);
        mTimePicker.setListener(this);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_create_task, container, false);
        ButterKnife.bind(this, v);
        mFab.setOnClickListener(this);
        mColorDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorPicker.show();
            }
        });
        // Update labels
        updateDateLabel();
        updateTimeLabel();
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
        FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
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
        mColorDot.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
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
        getDialog().dismiss();
    }

    @Override
    public void onColorChosen(@ColorInt int color) {
        this.color = color;
        updateColorView();
        mColorPicker.dismiss();
    }
}
