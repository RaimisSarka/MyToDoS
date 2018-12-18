package com.ahsas.mytodos.dbConnection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

public final class ReminderContract {

    private ReminderContract() {
    }

    public static class ReminderTable implements BaseColumns {
        public static final String TABLE_NAME = "reminders";
        public static final String COLUMN_NAME_KIND = "kind";                         //JOB HOME OTHER
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_START_DATE = "start_date";
        public static final String COLUMN_NAME_FINISH_DATE = "finish_date";
        public static final String COLUMN_NAME_COMMENT = "comment";
        public static final String COLUMN_NAME_STATUS  =  "status";                   //0 - pending 1 - in progress 2 - canceled 3 - done
    }

    private static final String SQL_CREATE_REMINDER_TABLE =
            "CREATE TABLE " + ReminderTable.TABLE_NAME + " (" +
                    ReminderTable._ID + " INTEGER PRIMARY KEY," +
                    ReminderTable.COLUMN_NAME_KIND + " TEXT," +
                    ReminderTable.COLUMN_NAME_TITLE + " TEXT," +
                    ReminderTable.COLUMN_NAME_START_DATE + " TEXT," +
                    ReminderTable.COLUMN_NAME_FINISH_DATE + " TEXT," +
                    ReminderTable.COLUMN_NAME_COMMENT + " TEXT," +
                    ReminderTable.COLUMN_NAME_STATUS + " TEXT)";

    private static final String SQL_DELETE_REMINDER_TABLE =
            "DROP TABLE IF EXISTS " + ReminderTable.TABLE_NAME;

    public class ReminderDbHelper extends SQLiteOpenHelper {

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Reminder.db";

        public ReminderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_REMINDER_TABLE);
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
}
