package com.ahsas.mytodos.dbConnection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ahsas.mytodos.dbConnection.ReminderContract;

public class ReminderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Reminder.db";

    private static final String SQL_CREATE_REMINDER_TABLE =
            "CREATE TABLE " + ReminderContract.ReminderTable.TABLE_NAME + " (" +
                    ReminderContract.ReminderTable._ID + " INTEGER PRIMARY KEY," +
                    ReminderContract.ReminderTable.COLUMN_NAME_KIND + " TEXT," +
                    ReminderContract.ReminderTable.COLUMN_NAME_TITLE + " TEXT," +
                    ReminderContract.ReminderTable.COLUMN_NAME_START_DATE + " TEXT," +
                    ReminderContract.ReminderTable.COLUMN_NAME_FINISH_DATE + " TEXT," +
                    ReminderContract.ReminderTable.COLUMN_NAME_COMMENT + " TEXT," +
                    ReminderContract.ReminderTable.COLUMN_NAME_STATUS + " TEXT)";

    private static final String SQL_DELETE_REMINDER_TABLE =
            "DROP TABLE IF EXISTS " + ReminderContract.ReminderTable.TABLE_NAME;

    private static final String SQL_DELETE_FROM_REMINDER_TABLE_WHERE =
        "DELETE FROM " + ReminderContract.ReminderTable.TABLE_NAME + " WHERE ";

    private static final String SQL_UPDATE_STATUS_TO_DONE_WHERE =
            "UPDATE " + ReminderContract.ReminderTable.TABLE_NAME + " SET " + ReminderContract.ReminderTable.COLUMN_NAME_STATUS + " = 3 WHERE";

    public ReminderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_REMINDER_TABLE);
    }

    public void deleteRowById(SQLiteDatabase db, int id){
        db.execSQL(SQL_DELETE_FROM_REMINDER_TABLE_WHERE + "_ID = " + String.valueOf(id));
    }

    public void updateStatusToDone(SQLiteDatabase db, int id){
        db.execSQL(SQL_UPDATE_STATUS_TO_DONE_WHERE + " _ID = " + String.valueOf(id));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_REMINDER_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

}