package com.speakame.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;
import com.speakame.Services.ContactImportService;
import com.speakame.utils.Function;

import org.json.JSONArray;

public class MainScreenActivity extends AnimRootActivity {
    //public static ArrayList<String> alContactsname;
    //public static ArrayList<String> alContactsnumber;
    // JSONArray jsonContacts;
    TextView headtext;
    TextView textView1, textView2, textView3, textView4;
    Button btn_ok, btn_refernow;
    Typeface tf1, tf2, tf3;
    ImageView backimage;
    JSONArray alContactsname = new JSONArray();
    JSONArray alContactsnumber = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
       // getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow);
        headtext = (TextView) findViewById(R.id.toolbar_title);
        headtext.setText("Speakame");

        Function.contactPermisstion(MainScreenActivity.this, 1);

        textView1 = (TextView) findViewById(R.id.textview1);
        textView2 = (TextView) findViewById(R.id.textview2);
        textView3 = (TextView) findViewById(R.id.textview3);
        textView4 = (TextView) findViewById(R.id.textview4);
        btn_ok = (Button) findViewById(R.id.ok_btn);
        btn_refernow = (Button) findViewById(R.id.refernow);
        backimage = (ImageView) findViewById(R.id.backarrow);
        headtext.setText("SpeakAme");

        tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        headtext.setTypeface(tf1);
        tf2 = Typeface.createFromAsset(getAssets(), "Raleway-SemiBold.ttf");
        textView1.setTypeface(tf2);
        textView3.setTypeface(tf2);
        tf3 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        textView2.setTypeface(tf3);
        textView4.setTypeface(tf3);
        btn_ok.setTypeface(tf3);
        btn_refernow.setTypeface(tf3);

        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                startService(new Intent(getBaseContext(), MyService.class));
                Intent intent = new Intent(MainScreenActivity.this, ContactImport_Activity.class);
                intent.setAction("bypass");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btn_refernow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                startService(new Intent(getBaseContext(), MyService.class));
                Intent intent = new Intent(MainScreenActivity.this, Invitefrnd_activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {

            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }

            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainscreen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_skip) {
//            startService(new Intent(getBaseContext(), MyService.class));
            startService(new Intent(getBaseContext(), ContactImportService.class));

            Intent intent = new Intent(MainScreenActivity.this, TwoTab_Activity.class);
            intent.setAction("bypass");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }
}
