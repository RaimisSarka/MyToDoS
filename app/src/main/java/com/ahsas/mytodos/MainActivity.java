package com.ahsas.mytodos;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShowLogo();

        Button mFinishApplicationButton = (Button) findViewById(R.id.close_app_button);
        Button mShowMonthButton = (Button) findViewById(R.id.show_month_activity_button);

        mFinishApplicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mShowMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoadMonthActivity.class);
                startActivity(intent);
            }
        });

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }

    public void ShowLogo (){
        Intent intent = new Intent(this, StartLogoActivity.class);
        startActivity(intent);
    }

}
