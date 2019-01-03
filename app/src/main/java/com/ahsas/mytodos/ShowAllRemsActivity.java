package com.ahsas.mytodos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ahsas.mytodos.dbConnection.ReminderContract;
import com.ahsas.mytodos.dbConnection.ReminderDbHelper;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ShowAllRemsActivity extends AppCompatActivity {

    List<ReminderDataModel> dataModel = new ArrayList<ReminderDataModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_rems);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.show_all_recyclerView);
        dataModel = getRemsData();
        RecyclerAdapter recycler = new RecyclerAdapter(dataModel);

        RecyclerView.LayoutManager reLayoutManager =new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycler);
    }

    public List<ReminderDataModel> getRemsData(){
        List<ReminderDataModel> data = new ArrayList<>();

        ReminderDbHelper dbHelper = new ReminderDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ReminderContract.ReminderTable.TABLE_NAME +";", null);

        StringBuffer buffer = new StringBuffer();
        ReminderDataModel reminderDataModel = null;

        while (cursor.moveToNext()) {
            reminderDataModel= new ReminderDataModel();
            String mKind = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderTable.COLUMN_NAME_KIND));
            String mTitle = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderTable.COLUMN_NAME_TITLE));
            String mStartDate = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderTable.COLUMN_NAME_START_DATE));
            String mFinishDate = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderTable.COLUMN_NAME_FINISH_DATE));
            String mComment = cursor.getString(cursor.getColumnIndexOrThrow(ReminderContract.ReminderTable.COLUMN_NAME_COMMENT));
            reminderDataModel.setKind(mKind);
            reminderDataModel.setTitle(mTitle);
            reminderDataModel.setStartDate(mStartDate);
            reminderDataModel.setFinishDate(mFinishDate);
            reminderDataModel.setComment(mComment);

            buffer.append(reminderDataModel);
            // stringBuffer.append(dataModel);
            data.add(reminderDataModel);
        }

        return data;
    }
}
