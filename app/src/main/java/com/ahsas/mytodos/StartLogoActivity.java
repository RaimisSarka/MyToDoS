package com.ahsas.mytodos;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class StartLogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_logo);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        LoadMonth();
                        finish();
                    }
                },
                500
        );

    }

    public void LoadMonth (){
        Intent intent = new Intent(this, LoadMonthActivity.class);
        startActivity(intent);
    }
}
