package com.ahsas.mytodos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ahsas.mytodos.dbConnection.ReminderContract;
import com.ahsas.mytodos.dbConnection.ReminderDbHelper;

import java.util.Calendar;
import java.util.Set;

public class AddToDo extends AppCompatActivity {

    private static final String TAG = "debugToDo";

    private static final String JOB_KIND = "JOB";
    private static final String HOME_KIND = "HOME";
    private static final String OTHER_KIND = "OTHER";

    //parsed extras
    private int mDay;
    private int mMonth;
    private int mYear;

    //Reminder data
    private String mReminderKind;
    private String mReminderTitle;
    private String mReminderStartDate;
    private String mReminderFinishDate;
    private String mReminderComment;

    //db connection
    SQLiteDatabase mReminderDB;
    ReminderDbHelper mReminderDbHelper;

    //Other var's
    private Boolean mSetStartDate;

    @Override
    protected void onDestroy() {
        mReminderDbHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);

        mReminderDbHelper = new ReminderDbHelper(getBaseContext());
        mReminderDB = mReminderDbHelper.getWritableDatabase();


        //TODO create back to parrent activity button and go back after ADD button click
        Button mJobKindButton = (Button) findViewById(R.id.button_Job_reminder);
        Button mHomeKindButton = (Button) findViewById(R.id.button_home_reminder);
        Button mOtherKindButton = (Button) findViewById(R.id.button_other_reminder);
        Spinner mReminderTitleSpinner = (Spinner) findViewById(R.id.spinner_select_to_do);
        EditText mStartDateEditText = (EditText) findViewById(R.id.editTextStartDate);
        EditText mFinishDateEditText = (EditText) findViewById(R.id.editTextFinishDate);
        final EditText mCommentEditText = (EditText) findViewById(R.id.editTextShortComment);
        ImageView mStartDatePickerImageView = (ImageView) findViewById(R.id.imageViewStartDatePicker);
        ImageView mFinishDatePickerImageVeiw = (ImageView) findViewById(R.id.imageViewFinishDatePicker);

        InitializeReminderData();
        LoadExtras(savedInstanceState); //Load parsed data from LoadMonthActivity
        pushDateToLayout();

        mCommentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if ((hasFocus)){
                    Log.d(TAG, "Comment text onFocus is- " + mCommentEditText.getText().toString());
                    if (mCommentEditText.getText().toString().equals("Comment task...")) {
                        mCommentEditText.setText("");
                    }
                } else {
                    Log.d(TAG, "Comment text offFocus is - " + mCommentEditText.getText().toString());
                    if (mCommentEditText.getText().toString().equals("")) {
                        mCommentEditText.setText("Comment task...");
                    }
                }
                Log.d(TAG, "Comment text after if statement- " + mCommentEditText.getText().toString());
                mReminderComment = mCommentEditText.getText().toString();
            }
        });

        mStartDatePickerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetStartDate = true;
                showDatePickerDialog(view);
            }
        });

        mFinishDatePickerImageVeiw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetStartDate = false;
                showDatePickerDialog(view);
            }
        });

        mJobKindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReminderKind = JOB_KIND;
                pushDateToLayout();
            }
        });

        mHomeKindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReminderKind = HOME_KIND;
                pushDateToLayout();
            }
        });

        mOtherKindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReminderKind = OTHER_KIND;
                pushDateToLayout();
            }
        });

        Button mAddReminderButton = (Button) findViewById(R.id.buttonAddReminder);

        mAddReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCommentEditText.clearFocus();

                if (dataAreCorrect()){
                    //TODO add all to database

                    // Gets the data repository in write mode
                    SQLiteDatabase db = mReminderDbHelper.getWritableDatabase();

                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(ReminderContract.ReminderTable.COLUMN_NAME_KIND, mReminderKind);
                    values.put(ReminderContract.ReminderTable.COLUMN_NAME_TITLE, mReminderTitle);
                    values.put(ReminderContract.ReminderTable.COLUMN_NAME_START_DATE, mReminderStartDate);
                    values.put(ReminderContract.ReminderTable.COLUMN_NAME_FINISH_DATE, mReminderFinishDate);
                    values.put(ReminderContract.ReminderTable.COLUMN_NAME_COMMENT, mReminderComment);
                    values.put(ReminderContract.ReminderTable.COLUMN_NAME_STATUS, 0);

                    // Insert the new row, returning the primary key value of the new row
                    long newRowId = db.insert(ReminderContract.ReminderTable.TABLE_NAME, null, values);

                    Intent intent = new Intent(getBaseContext(), LoadMonthActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private boolean dataAreCorrect(){
        boolean correct = false;

        //TODO check if date are correct
        if (!mReminderKind.equals("") &&
                !mReminderTitle.equals("") &&
                !mReminderStartDate.equals(this.getResources().getString(R.string.base_date)) &&
                !mReminderFinishDate.equals(this.getResources().getString(R.string.base_date)) &&
                !mReminderComment.equals("")){
            correct = true;
        }
        Log.d(TAG, "ReminderKind = " + mReminderKind);
        Log.d(TAG, "ReminderTitle = " + mReminderTitle);
        Log.d(TAG, "ReminderStartDate = " + mReminderStartDate);
        Log.d(TAG, "ReminderFinishDate = " + mReminderFinishDate);
        Log.d(TAG, "ReminderComment = " + mReminderComment);
        return correct;
    }

    public void LoadExtras(Bundle sis){

        //Getting extras
        if (sis == null){
            Bundle extras = getIntent().getExtras();
            if (extras == null){
                mDay = 0;
                mMonth = 0;
                mYear = 0;
            } else {
                mDay = extras.getInt("day");
                mMonth = extras.getInt("month");
                mYear = extras.getInt("years");
            }
        } else {
            mDay = (int) sis.getSerializable("day");
            mMonth = (int) sis.getSerializable("month");
            mYear = (int) sis.getSerializable("years");
        }

        mReminderStartDate = getString(R.string.date_format, mYear, mMonth,mDay);
        mReminderFinishDate = getString(R.string.date_format, mYear, mMonth, mDay);

    };

    private void InitializeReminderData(){
        mReminderKind = JOB_KIND;
        Spinner mReminderTitleSpinner = (Spinner) findViewById(R.id.spinner_select_to_do);
        mReminderTitle = mReminderTitleSpinner.getSelectedItem().toString();
        mReminderStartDate = this.getResources().getString(R.string.base_date);
        mReminderFinishDate = this.getResources().getString(R.string.base_date);
        mReminderComment = "";
    }

    private void pushDateToLayout(){

        Button mJobKindButton = (Button) findViewById(R.id.button_Job_reminder);
        Button mHomeKindButton = (Button) findViewById(R.id.button_home_reminder);
        Button mOtherKindButton = (Button) findViewById(R.id.button_other_reminder);
        Spinner mReminderTitlesSpinenr = (Spinner) findViewById(R.id.spinner_select_to_do);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.default_job_reminders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Log.d(TAG, "Before entering job kind");

        //Kind of reminder
        switch (mReminderKind) {
            case JOB_KIND:
                Log.d(TAG, "Entering job kind");
                SetButtonColor(mJobKindButton, true);
                SetButtonColor(mHomeKindButton, false);
                SetButtonColor(mOtherKindButton, false);
                //create adapter for spinner
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.default_job_reminders, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                break;
            case HOME_KIND:
                Log.d(TAG, "Entering home kind");
                SetButtonColor(mJobKindButton, false);
                SetButtonColor(mHomeKindButton, true);
                SetButtonColor(mOtherKindButton, false);
                //create adapter for spinner
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.default_home_reminders, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                break;
            case OTHER_KIND:
                Log.d(TAG, "Entering other kind");
                SetButtonColor(mJobKindButton, false);
                SetButtonColor(mHomeKindButton, false);
                SetButtonColor(mOtherKindButton, true);
                //create adapter for spinner
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.default_other_reminder, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                break;
            default:
                break;
        }

        mReminderTitlesSpinenr.setAdapter(adapter);
        mReminderTitle = mReminderTitlesSpinenr.getSelectedItem().toString();
        //Dates
        EditText mStartDateEditText = (EditText) findViewById(R.id.editTextStartDate);
        EditText mFinishDateEditText = (EditText) findViewById(R.id.editTextFinishDate);

        mStartDateEditText.setText(mReminderStartDate);
        mFinishDateEditText.setText(mReminderFinishDate);
    }

    private void SetButtonColor(Button bt, Boolean on_off){
        if (on_off) {
            bt.setBackground(getResources().getDrawable(R.drawable.button_layout_pressed));
        } else {
            bt.setBackground(getResources().getDrawable(R.drawable.button_layout_not_pressed));
        }
    }

    //SubClass of TimePickerFragment

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Save new date
            if (((AddToDo) getActivity()).mSetStartDate) {
                ((AddToDo) getActivity()).mReminderStartDate = getString(R.string.date_format, year, month + 1, day);
            } else {
                ((AddToDo) getActivity()).mReminderFinishDate = getString(R.string.date_format, year, month + 1, day);
            }
            ((AddToDo) getActivity()).pushDateToLayout();
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


}
