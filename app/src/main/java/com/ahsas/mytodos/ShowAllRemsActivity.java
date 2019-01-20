package com.ahsas.mytodos;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ahsas.mytodos.dbConnection.ReminderContract;
import com.ahsas.mytodos.dbConnection.ReminderDbHelper;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ShowAllRemsActivity extends AppCompatActivity {

    private final String TAG = "ShowAllRemDebug";
    private static final String JOB_KIND = "JOB";
    private static final String HOME_KIND = "HOME";
    private static final String OTHER_KIND = "OTHER";
    private Paint p = new Paint();

    private String mSortingKind = "";
    private String mSortingStatus = "";
    private String mSortingDate = "";

    List<ReminderDataModel> dataModelArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_rems);

        loadExtras(savedInstanceState);

        final Button mSortByJobButton = findViewById(R.id.sort_by_job_type_button);
        final Button mSortByHomeButton = findViewById(R.id.sort_by_home_type_button2);
        final Button mSortByOtherButton = findViewById(R.id.sort_by_other_type_button);

        mSortByJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSortingKind = JOB_KIND;
                SetButtonColor(mSortByOtherButton, false);
                SetButtonColor(mSortByHomeButton, false);
                SetButtonColor(mSortByJobButton, true);
                setRecyclerView();
            }
        });

        mSortByHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSortingKind = HOME_KIND;
                SetButtonColor(mSortByOtherButton, false);
                SetButtonColor(mSortByHomeButton, true);
                SetButtonColor(mSortByJobButton, false);
                setRecyclerView();
            }
        });

        mSortByOtherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSortingKind = OTHER_KIND;
                SetButtonColor(mSortByOtherButton, true);
                SetButtonColor(mSortByHomeButton, false);
                SetButtonColor(mSortByJobButton, false);
                setRecyclerView();
            }
        });

        setRecyclerView();

        //Sorting recyclerView

    }

    private void loadExtras (Bundle sis){
        //Getting extras
        if (sis == null){
            Bundle extras = getIntent().getExtras();
            if (extras == null){
                mSortingStatus = "";
                mSortingDate = "";
            } else {
                mSortingStatus = extras.getString("sortByStatus");
                mSortingDate = extras.getString("sortByFinishDate");
            }
        } else {
            mSortingStatus = (String) sis.getSerializable("sortByStatus");
            mSortingDate = (String) sis.getSerializable("sortByFinishDate");
        }
    }

    private void SetButtonColor(Button bt, Boolean on_off){
        if (on_off) {
            bt.setBackground(getResources().getDrawable(R.drawable.button_layout_pressed));
        } else {
            bt.setBackground(getResources().getDrawable(R.drawable.button_layout_not_pressed));
        }
    }

    public void setRecyclerView(){
        final RecyclerView recyclerView = findViewById(R.id.show_all_recyclerView);
        dataModelArray = getRemsData();
        final RecyclerAdapter recycler = new RecyclerAdapter(dataModelArray);

        RecyclerView.LayoutManager reLayoutManager =new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycler);

        //Swiping RecyclerView
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final int mId = (int) viewHolder.itemView.getTag();
                if (direction == ItemTouchHelper.LEFT){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowAllRemsActivity.this);
                    builder.setMessage(R.string.delete_reminder_alert_message)
                            .setPositiveButton(R.string.delete_reminder_positive, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    recycler.removeItem(position);
                                    deleteRowFromDb(mId);
                                }
                            })
                            .setNegativeButton(R.string.delete_reminder_negative, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    dialog.cancel();
                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    //Log.d(TAG, "mId = " + Integer.toString(id));
                    Log.d(TAG, "Swipe to left active");
                } else {
                    updateRemStatusToDone(mId);
                    recycler.removeItem(position);
                    Log.d(TAG, "Swipe to other active");
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.done);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void deleteRowFromDb(int id){
        ReminderDbHelper dbHelper = new ReminderDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.deleteRowById(db, id);
    }

    public void updateRemStatusToDone(int id){
        ReminderDbHelper dbHelper = new ReminderDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.updateStatusToDone(db, id);
    }

    public List<ReminderDataModel> getRemsData(){
        List<ReminderDataModel> data = new ArrayList<>();

        ReminderDbHelper dbHelper = new ReminderDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ReminderContract.ReminderTable.TABLE_NAME + ";", null);

        //If data should be sorted
        if (mSortingStatus.equals("")) {
            if (!mSortingKind.equals("")) {
                String SQL_Query;
                SQL_Query = "SELECT * FROM " + ReminderContract.ReminderTable.TABLE_NAME +
                        " WHERE " + ReminderContract.ReminderTable.COLUMN_NAME_KIND + " = ? ";
                cursor = db.rawQuery(SQL_Query, new String[]{mSortingKind});
            }
        } else {
            String SQL_Query;
            if (!mSortingKind.equals("")) {
                if (mSortingDate.equals("")) {
                    SQL_Query = "SELECT * FROM " + ReminderContract.ReminderTable.TABLE_NAME +
                            " WHERE " + ReminderContract.ReminderTable.COLUMN_NAME_KIND + " = ? AND " +
                            ReminderContract.ReminderTable.COLUMN_NAME_STATUS + " = ?";
                    cursor = db.rawQuery(SQL_Query, new String[]{mSortingKind, mSortingStatus});
                } else {
                    SQL_Query = "SELECT * FROM " + ReminderContract.ReminderTable.TABLE_NAME +
                            " WHERE " + ReminderContract.ReminderTable.COLUMN_NAME_KIND + " = ? AND " +
                            ReminderContract.ReminderTable.COLUMN_NAME_STATUS + " = ? AND " +
                            ReminderContract.ReminderTable.COLUMN_NAME_FINISH_DATE + " = ?";
                    cursor = db.rawQuery(SQL_Query, new String[]{mSortingKind, mSortingStatus, mSortingDate});
                }

            } else {
                if (mSortingDate.equals("")) {
                    SQL_Query = "SELECT * FROM " + ReminderContract.ReminderTable.TABLE_NAME +
                            " WHERE " + ReminderContract.ReminderTable.COLUMN_NAME_STATUS + " = ? ";
                    cursor = db.rawQuery(SQL_Query, new String[]{mSortingStatus});
                } else {
                    SQL_Query = "SELECT * FROM " + ReminderContract.ReminderTable.TABLE_NAME +
                            " WHERE " + ReminderContract.ReminderTable.COLUMN_NAME_STATUS + " = ? AND " +
                            ReminderContract.ReminderTable.COLUMN_NAME_FINISH_DATE + " = ?";
                    cursor = db.rawQuery(SQL_Query, new String[]{mSortingStatus, mSortingDate});
                }
            }
        }


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

}
