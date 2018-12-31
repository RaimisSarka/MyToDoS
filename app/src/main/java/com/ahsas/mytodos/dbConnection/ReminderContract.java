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




}
