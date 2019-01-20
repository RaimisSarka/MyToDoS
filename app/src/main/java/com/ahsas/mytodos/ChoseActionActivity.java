package com.ahsas.mytodos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ChoseActionActivity extends AppCompatActivity {

    //parsed extras
    private int mDay;
    private int mMonth;
    private int mYear;

    public int mMonthToShow;
    public int mYearToShow;
    public String mStringDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_action);
        LoadExtras(savedInstanceState);

        Button mAddToDo = (Button) findViewById(R.id.add_new_rem_button);
        Button mShowDayRems = (Button) findViewById(R.id.show_rems_by_date_button);

        mAddToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddToDo.class);
                intent.putExtra("day", mDay);
                intent.putExtra("years", mYearToShow);
                intent.putExtra("month", mMonthToShow);
                startActivity(intent);
            }
        });

        mShowDayRems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShowAllRemsActivity.class);
                intent.putExtra("sortByStatus", "0"); //only pending
                intent.putExtra("sortByFinishDate", mStringDate);
                startActivity(intent);
            }
        });
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

        TextView mDate = (TextView) findViewById(R.id.choose_action_date_textView);
        mStringDate = String.valueOf(mYear)+"/"+String.valueOf(mMonth)+"/"+String.valueOf(mDay);
        mDate.setText(mStringDate);

        mMonthToShow = mMonth;
        mYearToShow = mYear;

    };

}
