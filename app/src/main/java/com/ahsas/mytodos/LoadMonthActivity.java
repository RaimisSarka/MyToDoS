package com.ahsas.mytodos;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahsas.mytodos.dbConnection.ReminderContract;
import com.ahsas.mytodos.dbConnection.ReminderDbHelper;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoadMonthActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "ToDos_reminders";
    private static final int NOTIFICATION_ID = 777;

    private String[] mMonths;
    List<ReminderDataModel> dataModelArray = new ArrayList<>();
    private static String TAG = "debugInformation";


    public Date mTodayDate;
    public int mDayOfWeekToday;
    public int mMainMonth;
    public int mMainYear;
    public int mMainDay;
    public int mMainDayOfYear;

    public int mMonthToShow;
    public int mYearToShow;


    public TextView[] mSquares = new TextView[43];
    public TextView[] mRemCounts = new TextView[43];
    //public int[] mDaysBitmaps = new int[32];


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_month);

        dataModelArray = getRemsData();
        loadImageViews();
        loadCountViews();
        loadDaysToday();
        //TODO create logic of notification
        //sendNotification();

        Log.d(TAG, "onCreate");
        //Toast.makeText(this,"test toast", Toast.LENGTH_LONG).show();

        if (savedInstanceState !=  null){
            mMonthToShow = savedInstanceState.getInt("month_to_show");
            mYearToShow = savedInstanceState.getInt("year_to_show");
            Log.d(TAG,"= not null");
        }

        final FrameLayout mFlButtonPrevious = findViewById(R.id.frameLayoutPreviousMonth);
        final FrameLayout mFlButtonNext = findViewById(R.id.frameLayoutNextMonth);
        final ConstraintLayout mBaseLayout = findViewById(R.id.base_constraint_layout);
        final Button mShowAllButton = findViewById(R.id.show_all_button);
        final Button mShowActiveButton = findViewById(R.id.show_not_done_button);
        final Button mShowClosedButton = findViewById(R.id.show_closed_button);

        mShowAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShowAllRemsActivity.class);
                startActivity(intent);
            }
        });

        mShowActiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShowAllRemsActivity.class);
                intent.putExtra("sortByStatus", "0");
                intent.putExtra("sortByFinishDate", "");
                startActivity(intent);
            }
        });

        mShowClosedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShowAllRemsActivity.class);
                intent.putExtra("sortByStatus", "3");
                intent.putExtra("sortByFinishDate", "");
                startActivity(intent);
            }
        });

        mBaseLayout.setOnTouchListener(new OnSwipeTouchListener(LoadMonthActivity.this){

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                nextMonth();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                previousMonth();
            }
        });

        mFlButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonth();
                //Change temporary style
                final TextView mTextViewPreviousButton = findViewById(R.id.textViewPreviousMonth);
                mTextViewPreviousButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorButtonBackgroundOnClick));

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTextViewPreviousButton.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.layout_borders));
                        int mButtonPadding = getBaseContext().getResources().getDimensionPixelSize(R.dimen.buttonPadingSize);
                        mTextViewPreviousButton.setPadding(mButtonPadding,mButtonPadding,mButtonPadding,mButtonPadding);
                    }
                }, 200);
            }
        });

        mFlButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
                //Change temporary style
                final TextView mTextViewNextButton = findViewById(R.id.textViewNextMonth);
                mTextViewNextButton.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorButtonBackgroundOnClick));

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTextViewNextButton.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.layout_borders));
                        int mButtonPadding = getBaseContext().getResources().getDimensionPixelSize(R.dimen.buttonPadingSize);
                        mTextViewNextButton.setPadding(mButtonPadding,mButtonPadding,mButtonPadding,mButtonPadding);
                    }
                }, 200);
            }
        });
    }


    public void nextMonth(){
        if (mMonthToShow == 11) {
            mMonthToShow = 0;
            mYearToShow++;
        } else {
            mMonthToShow++;
        }

        reloadNewMonth();
    }

    public void previousMonth(){
        if (mMonthToShow == 0) {
            mMonthToShow = 11;
            mYearToShow--;
        } else {
            mMonthToShow--;
        }

        reloadNewMonth();
    }

    public void reloadNewMonth(){
        Log.d(TAG, "reloadNewMonth");
        TextView mTextViewCurrentMonth = findViewById(R.id.textViewCurrentMonth);
        mTextViewCurrentMonth.setText(mMonths[mMonthToShow]);
        drawSquares();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt("month_to_show ", mMonthToShow);
        outState.putInt("year_to_show ", mYearToShow);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        mMonthToShow = savedInstanceState.getInt("month_to_show");
        mYearToShow = savedInstanceState.getInt("year_to_show");
        reloadNewMonth();
        drawSquares();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        dataModelArray = getRemsData();
        loadImageViews();
        loadCountViews();
        loadDaysToday();
    }

    public void loadDaysToday(){
        Log.d(TAG, "loadDaysToday");
        Calendar calendarToday = Calendar.getInstance();

        mTodayDate = calendarToday.getTime();
        mDayOfWeekToday = calendarToday.get(Calendar.DAY_OF_WEEK);
        mMainMonth = calendarToday.get(Calendar.MONTH);
        mMainYear = calendarToday.get(Calendar.YEAR);
        mMainDay = calendarToday.get(Calendar.DAY_OF_MONTH);
        mMainDayOfYear = calendarToday.get(Calendar.DAY_OF_YEAR);
        String[] mDayNamesArray = getResources().getStringArray(R.array.weekdays_name);
        mMonths = getResources().getStringArray(R.array.months_names);

        mMonthToShow = mMainMonth;
        mYearToShow = mMainYear;
        TextView mTextViewCurrentMonth = findViewById(R.id.textViewCurrentMonth);
        mTextViewCurrentMonth.setText(mMonths[mMonthToShow]);

        drawSquares();
    }

    public void drawSquares(){
        Calendar mCalendarTemp = Calendar.getInstance();
        mCalendarTemp.set(mYearToShow, mMonthToShow, 1);
        int mFirstOfMonthIsDayOfWeek = mCalendarTemp.get(Calendar.DAY_OF_WEEK); //1 - Sunday, 2 - Monday ...
        int mDaysInMonth;

        //Set days count in month
        mDaysInMonth = mCalendarTemp.getActualMaximum(Calendar.DAY_OF_MONTH);

        //Clear squares
        for (int i=1; i<43; i++){
            mSquares[i].setText("");
            mSquares[i].setBackgroundColor(getResources().getColor(R.color.colorButtonBackground));
            mRemCounts[i].setText("");
            mRemCounts[i].setVisibility(View.INVISIBLE);
        }

        //Make first day of week - monday
        if (mFirstOfMonthIsDayOfWeek == 1) {
            mFirstOfMonthIsDayOfWeek = 7;
        } else {
            mFirstOfMonthIsDayOfWeek--;
        }

        //Checking if its current month and if it is mark today
        if ((mYearToShow == mMainYear) && (mMonthToShow == mMainMonth)){
            mSquares[mMainDay + mFirstOfMonthIsDayOfWeek-1].setBackgroundColor(getResources().getColor(R.color.colorTodayButton));
        }

        //Set values to squares
        for (int i=1; i<=mDaysInMonth; i++){
            final int mDay = i;
            int mCount = getReminderCount(i);
            if (mCount > 0) {
                //TODO set counters visibility
                Log.d(TAG, "Count: not null" );
                mRemCounts[i+mFirstOfMonthIsDayOfWeek-1].setText(String.valueOf(mCount));
                mRemCounts[i+mFirstOfMonthIsDayOfWeek-1].setVisibility(View.VISIBLE);

            }
            mSquares[i+mFirstOfMonthIsDayOfWeek-1].setText(Integer.toString(i));
            mSquares[i+mFirstOfMonthIsDayOfWeek-1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, mDay + " day");
                    Intent intent = new Intent(getBaseContext(), ChoseActionActivity.class);
                    intent.putExtra("day", mDay);
                    intent.putExtra("years", mYearToShow);
                    intent.putExtra("month", mMonthToShow + 1);
                    startActivity(intent);
                }
            });
        }

    }

    public int getReminderCount(int dayNumber){
        int count = 0;
        int i;
        String mDate = String.valueOf(mYearToShow)+"/"+String.valueOf(mMonthToShow+1)+"/"+String.valueOf(dayNumber);
        if (!dataModelArray.isEmpty()){
            for (i=0; i<dataModelArray.size(); i++){
                String mRemsStatus = dataModelArray.get(i).mStatus;
                String mRemsFinishDate = dataModelArray.get(i).mFinishDate;
                if ((mDate.equals(mRemsFinishDate) && mRemsStatus.equals("0"))){
                    count++;
                }
            }
        }
        return count;
    }

    public void sendNotification(){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_todos)
                .setContentTitle("ToDos reminder")
                .setContentText("Nothing to remind.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }

    }

    public List<ReminderDataModel> getRemsData(){
        List<ReminderDataModel> data = new ArrayList<>();

        ReminderDbHelper dbHelper = new ReminderDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ReminderContract.ReminderTable.TABLE_NAME + ";", null);

        StringBuilder buffer = new StringBuilder();
        ReminderDataModel reminderDataModel;

        while (cursor.moveToNext()) {
            reminderDataModel= new ReminderDataModel();
            int mId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            String mKind = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderTable.COLUMN_NAME_KIND));
            String mTitle = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderTable.COLUMN_NAME_TITLE));
            String mStartDate = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderTable.COLUMN_NAME_START_DATE));
            String mFinishDate = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderTable.COLUMN_NAME_FINISH_DATE));
            String mComment = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderTable.COLUMN_NAME_COMMENT));
            String mStatus = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderTable.COLUMN_NAME_STATUS));

            reminderDataModel.setId(mId);
            reminderDataModel.setKind(mKind);
            reminderDataModel.setTitle(mTitle);
            reminderDataModel.setStartDate(mStartDate);
            reminderDataModel.setFinishDate(mFinishDate);
            reminderDataModel.setComment(mComment);
            reminderDataModel.setStatus(mStatus);

            buffer.append(reminderDataModel);
            // stringBuffer.append(dataModel);
            data.add(reminderDataModel);
        }

        db.close();
        cursor.close();
        return data;
    }


    public void loadImageViews(){
        mSquares[1] = findViewById(R.id.TextViewSquare1);
        mSquares[2] = findViewById(R.id.TextViewSquare2);
        mSquares[3] = findViewById(R.id.TextViewSquare3);
        mSquares[4] = findViewById(R.id.TextViewSquare4);
        mSquares[5] = findViewById(R.id.TextViewSquare5);
        mSquares[6] = findViewById(R.id.TextViewSquare6);
        mSquares[7] = findViewById(R.id.TextViewSquare7);

        mSquares[8] = findViewById(R.id.TextViewSquare8);
        mSquares[9] = findViewById(R.id.TextViewSquare9);
        mSquares[10] = findViewById(R.id.TextViewSquare10);
        mSquares[11] = findViewById(R.id.TextViewSquare11);
        mSquares[12] = findViewById(R.id.TextViewSquare12);
        mSquares[13] = findViewById(R.id.TextViewSquare13);
        mSquares[14] = findViewById(R.id.TextViewSquare14);

        mSquares[15] = findViewById(R.id.TextViewSquare15);
        mSquares[16] = findViewById(R.id.TextViewSquare16);
        mSquares[17] = findViewById(R.id.TextViewSquare17);
        mSquares[18] = findViewById(R.id.TextViewSquare18);
        mSquares[19] = findViewById(R.id.TextViewSquare19);
        mSquares[20] = findViewById(R.id.TextViewSquare20);
        mSquares[21] = findViewById(R.id.TextViewSquare21);

        mSquares[22] = findViewById(R.id.TextViewSquare22);
        mSquares[23] = findViewById(R.id.TextViewSquare23);
        mSquares[24] = findViewById(R.id.TextViewSquare24);
        mSquares[25] = findViewById(R.id.TextViewSquare25);
        mSquares[26] = findViewById(R.id.TextViewSquare26);
        mSquares[27] = findViewById(R.id.TextViewSquare27);
        mSquares[28] = findViewById(R.id.TextViewSquare28);

        mSquares[29] = findViewById(R.id.TextViewSquare29);
        mSquares[30] = findViewById(R.id.TextViewSquare30);
        mSquares[31] = findViewById(R.id.TextViewSquare31);
        mSquares[32] = findViewById(R.id.TextViewSquare32);
        mSquares[33] = findViewById(R.id.TextViewSquare33);
        mSquares[34] = findViewById(R.id.TextViewSquare34);
        mSquares[35] = findViewById(R.id.TextViewSquare35);

        mSquares[36] = findViewById(R.id.TextViewSquare36);
        mSquares[37] = findViewById(R.id.TextViewSquare37);
        mSquares[38] = findViewById(R.id.TextViewSquare38);
        mSquares[39] = findViewById(R.id.TextViewSquare39);
        mSquares[40] = findViewById(R.id.TextViewSquare40);
        mSquares[41] = findViewById(R.id.TextViewSquare41);
        mSquares[42] = findViewById(R.id.TextViewSquare42);

    }

    public void loadCountViews(){
        mRemCounts[1] = findViewById(R.id.TextViewSquare1_counter);
        mRemCounts[2] = findViewById(R.id.TextViewSquare2_counter);
        mRemCounts[3] = findViewById(R.id.TextViewSquare3_counter);
        mRemCounts[4] = findViewById(R.id.TextViewSquare4_counter);
        mRemCounts[5] = findViewById(R.id.TextViewSquare5_counter);
        mRemCounts[6] = findViewById(R.id.TextViewSquare6_counter);
        mRemCounts[7] = findViewById(R.id.TextViewSquare7_counter);

        mRemCounts[8] = findViewById(R.id.TextViewSquare8_counter);
        mRemCounts[9] = findViewById(R.id.TextViewSquare9_counter);
        mRemCounts[10] = findViewById(R.id.TextViewSquare10_counter);
        mRemCounts[11] = findViewById(R.id.TextViewSquare11_counter);
        mRemCounts[12] = findViewById(R.id.TextViewSquare12_counter);
        mRemCounts[13] = findViewById(R.id.TextViewSquare13_counter);
        mRemCounts[14] = findViewById(R.id.TextViewSquare14_counter);

        mRemCounts[15] = findViewById(R.id.TextViewSquare15_counter);
        mRemCounts[16] = findViewById(R.id.TextViewSquare16_counter);
        mRemCounts[17] = findViewById(R.id.TextViewSquare17_counter);
        mRemCounts[18] = findViewById(R.id.TextViewSquare18_counter);
        mRemCounts[19] = findViewById(R.id.TextViewSquare19_counter);
        mRemCounts[20] = findViewById(R.id.TextViewSquare20_counter);
        mRemCounts[21] = findViewById(R.id.TextViewSquare21_counter);

        mRemCounts[22] = findViewById(R.id.TextViewSquare22_counter);
        mRemCounts[23] = findViewById(R.id.TextViewSquare23_counter);
        mRemCounts[24] = findViewById(R.id.TextViewSquare24_counter);
        mRemCounts[25] = findViewById(R.id.TextViewSquare25_counter);
        mRemCounts[26] = findViewById(R.id.TextViewSquare26_counter);
        mRemCounts[27] = findViewById(R.id.TextViewSquare27_counter);
        mRemCounts[28] = findViewById(R.id.TextViewSquare28_counter);

        mRemCounts[29] = findViewById(R.id.TextViewSquare29_counter);
        mRemCounts[30] = findViewById(R.id.TextViewSquare30_counter);
        mRemCounts[31] = findViewById(R.id.TextViewSquare31_counter);
        mRemCounts[32] = findViewById(R.id.TextViewSquare32_counter);
        mRemCounts[33] = findViewById(R.id.TextViewSquare33_counter);
        mRemCounts[34] = findViewById(R.id.TextViewSquare34_counter);
        mRemCounts[35] = findViewById(R.id.TextViewSquare35_counter);

        mRemCounts[36] = findViewById(R.id.TextViewSquare36_counter);
        mRemCounts[37] = findViewById(R.id.TextViewSquare37_counter);
        mRemCounts[38] = findViewById(R.id.TextViewSquare38_counter);
        mRemCounts[39] = findViewById(R.id.TextViewSquare39_counter);
        mRemCounts[40] = findViewById(R.id.TextViewSquare40_counter);
        mRemCounts[41] = findViewById(R.id.TextViewSquare41_counter);
        mRemCounts[42] = findViewById(R.id.TextViewSquare42_counter);

    }
}
