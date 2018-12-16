package com.ahsas.mytodos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class AddToDo extends AppCompatActivity {

    private int mDay;
    private int mMonth;
    private int mYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);

        LoadExtras(savedInstanceState);

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

        //Puting extras
        EditText mStartDateEditText = (EditText) findViewById(R.id.editTextStartDate);
        EditText mFinishDateEditText = (EditText) findViewById(R.id.editTextFinishDate);

        mStartDateEditText.setText(getString(R.string.date_format, mYear, mMonth,mDay));
        mFinishDateEditText.setText(getString(R.string.date_format, mYear, mMonth, mDay));
    };

}
