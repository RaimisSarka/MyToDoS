package com.ahsas.mytodos;

import android.content.Context;
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

import com.ahsas.mytodos.dbConnection.ReminderContract;
import com.ahsas.mytodos.dbConnection.ReminderDbHelper;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ShowAllRemsActivity extends AppCompatActivity {

    private final String TAG = "ShowAllRemDebug";
    private Paint p = new Paint();

    List<ReminderDataModel> dataModel = new ArrayList<ReminderDataModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_rems);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.show_all_recyclerView);
        dataModel = getRemsData();
        final RecyclerAdapter recycler = new RecyclerAdapter(dataModel);

        RecyclerView.LayoutManager reLayoutManager =new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycler);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    recycler.removeItem(position);
                    Log.d(TAG, "Swipe to left active");
                } else {
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
                        //TODO change icon "DONE"
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.day2);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        //TODO change icon "DELETE"
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.day1);
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
