package com.speakame.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.utils.AppPreferences;

public class AlertDaysSpeakameActivity extends AnimRootActivity {
    Button mbtn_continue;
    TextView toolbartext,mremainingdays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_days_speakame);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Welcome to Speakame");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        mbtn_continue = (Button) findViewById(R.id.btn_continue);
        mremainingdays = (TextView) findViewById(R.id.textviewdays);
        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        mbtn_continue.setTypeface(typeface1);
        mremainingdays.setTypeface(typeface1);
        mremainingdays.setText(AppPreferences.getRemainingDays(AlertDaysSpeakameActivity.this));
        mbtn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlertDaysSpeakameActivity.this, MainScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();

            }
        });


    }

}
