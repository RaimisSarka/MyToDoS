package com.ahsas.mytodos;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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

import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;

public class LoadMonthActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "ToDos_reminders";
    private static final int NOTIFICATION_ID = 777;

    private String[] mMonths;
    private static String TAG = "debugInformation";


    public Date mTodayDate;
    public int mDayOfWeekToday;
    public int mMainMonth;
    public int mMainYear;
    public int mMainDay;

    public int mMonthToShow;
    public int mYearToShow;


    public TextView[] mSquares = new TextView[43];
    //public int[] mDaysBitmaps = new int[32];


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_month);

        loadImageViews();
        loadDaysToday();
        //TODO create logic of notification
        //sendNotification();

        Log.d(TAG, "onCreate");
        Toast.makeText(this,"test toast", Toast.LENGTH_LONG);

        if (savedInstanceState !=  null){
            mMonthToShow = savedInstanceState.getInt("month_to_show");
            mYearToShow = savedInstanceState.getInt("year_to_show");
            Log.d(TAG,"= not null");
        }

        final FrameLayout mFlButtonPrevious = (FrameLayout) findViewById(R.id.frameLayoutPreviousMonth);
        final FrameLayout mFlButtonNext = (FrameLayout) findViewById(R.id.frameLayoutNextMonth);
        final ConstraintLayout mBaseLayout = (ConstraintLayout) findViewById(R.id.base_constraint_layout);
        final Button mShowAllButton = (Button) findViewById(R.id.show_all_button);
        final Button mShowActiveButton = (Button) findViewById(R.id.show_not_done_button);
        final Button mShowClosedButton = (Button) findViewById(R.id.show_closed_button);

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
                startActivity(intent);
            }
        });

        mShowClosedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShowAllRemsActivity.class);
                intent.putExtra("sortByStatus", "3");
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
                final TextView mTextViewPreviousButton = (TextView) findViewById(R.id.textViewPreviousMonth);
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
                final TextView mTextViewNextButton = (TextView) findViewById(R.id.textViewNextMonth);
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
        TextView mTextViewCurrentMonth = (TextView) findViewById(R.id.textViewCurrentMonth);
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
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "unResume");
        Log.d(TAG, "monthToShow" + String.valueOf(mMonthToShow));
    }

    public void loadDaysToday(){
        Log.d(TAG, "loadDaysToday");
        Calendar calendarToday = Calendar.getInstance();

        mTodayDate = calendarToday.getTime();
        mDayOfWeekToday = calendarToday.get(Calendar.DAY_OF_WEEK);
        mMainMonth = calendarToday.get(Calendar.MONTH);
        mMainYear = calendarToday.get(Calendar.YEAR);
        mMainDay = calendarToday.get(Calendar.DAY_OF_YEAR);
        String[] mDayNamesArray = getResources().getStringArray(R.array.weekdays_name);
        mMonths = getResources().getStringArray(R.array.months_names);

        mMonthToShow = mMainMonth;
        mYearToShow = mMainYear;
        TextView mTextViewCurrentMonth = (TextView) findViewById(R.id.textViewCurrentMonth);
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
            mSquares[i+mFirstOfMonthIsDayOfWeek-1].setText(Integer.toString(i));
            mSquares[i+mFirstOfMonthIsDayOfWeek-1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, mDay + " day");
                    Intent intent = new Intent(getBaseContext(), AddToDo.class);
                    intent.putExtra("day", mDay);
                    intent.putExtra("years", mYearToShow);
                    intent.putExtra("month", mMonthToShow + 1);
                    startActivity(intent);
                }
            });
        }

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

    public void loadImageViews(){
        mSquares[1] = (TextView) findViewById(R.id.TextViewSquare1);
        mSquares[2] = (TextView) findViewById(R.id.TextViewSquare2);
        mSquares[3] = (TextView) findViewById(R.id.TextViewSquare3);
        mSquares[4] = (TextView) findViewById(R.id.TextViewSquare4);
        mSquares[5] = (TextView) findViewById(R.id.TextViewSquare5);
        mSquares[6] = (TextView) findViewById(R.id.TextViewSquare6);
        mSquares[7] = (TextView) findViewById(R.id.TextViewSquare7);

        mSquares[8] = (TextView) findViewById(R.id.TextViewSquare8);
        mSquares[9] = (TextView) findViewById(R.id.TextViewSquare9);
        mSquares[10] = (TextView) findViewById(R.id.TextViewSquare10);
        mSquares[11] = (TextView) findViewById(R.id.TextViewSquare11);
        mSquares[12] = (TextView) findViewById(R.id.TextViewSquare12);
        mSquares[13] = (TextView) findViewById(R.id.TextViewSquare13);
        mSquares[14] = (TextView) findViewById(R.id.TextViewSquare14);

        mSquares[15] = (TextView) findViewById(R.id.TextViewSquare15);
        mSquares[16] = (TextView) findViewById(R.id.TextViewSquare16);
        mSquares[17] = (TextView) findViewById(R.id.TextViewSquare17);
        mSquares[18] = (TextView) findViewById(R.id.TextViewSquare18);
        mSquares[19] = (TextView) findViewById(R.id.TextViewSquare19);
        mSquares[20] = (TextView) findViewById(R.id.TextViewSquare20);
        mSquares[21] = (TextView) findViewById(R.id.TextViewSquare21);

        mSquares[22] = (TextView) findViewById(R.id.TextViewSquare22);
        mSquares[23] = (TextView) findViewById(R.id.TextViewSquare23);
        mSquares[24] = (TextView) findViewById(R.id.TextViewSquare24);
        mSquares[25] = (TextView) findViewById(R.id.TextViewSquare25);
        mSquares[26] = (TextView) findViewById(R.id.TextViewSquare26);
        mSquares[27] = (TextView) findViewById(R.id.TextViewSquare27);
        mSquares[28] = (TextView) findViewById(R.id.TextViewSquare28);

        mSquares[29] = (TextView) findViewById(R.id.TextViewSquare29);
        mSquares[30] = (TextView) findViewById(R.id.TextViewSquare30);
        mSquares[31] = (TextView) findViewById(R.id.TextViewSquare31);
        mSquares[32] = (TextView) findViewById(R.id.TextViewSquare32);
        mSquares[33] = (TextView) findViewById(R.id.TextViewSquare33);
        mSquares[34] = (TextView) findViewById(R.id.TextViewSquare34);
        mSquares[35] = (TextView) findViewById(R.id.TextViewSquare35);

        mSquares[36] = (TextView) findViewById(R.id.TextViewSquare36);
        mSquares[37] = (TextView) findViewById(R.id.TextViewSquare37);
        mSquares[38] = (TextView) findViewById(R.id.TextViewSquare38);
        mSquares[39] = (TextView) findViewById(R.id.TextViewSquare39);
        mSquares[40] = (TextView) findViewById(R.id.TextViewSquare40);
        mSquares[41] = (TextView) findViewById(R.id.TextViewSquare41);
        mSquares[42] = (TextView) findViewById(R.id.TextViewSquare42);

    }
}
