package com.speakame.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;

public class Main_Activity extends AnimRootActivity {
    Button mbtn_sign, mbtn_signup;
    TextView mhelp_textview;
    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);
        getSupportActionBar().hide();
        mbtn_sign = (Button) findViewById(R.id.btn_signin);
        mbtn_signup = (Button) findViewById(R.id.btn_signup);
        mhelp_textview = (TextView) findViewById(R.id.helpful);
        typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Regular.ttf");
        mbtn_sign.setTypeface(typeface);
        mbtn_signup.setTypeface(typeface);
        mhelp_textview.setTypeface(typeface);
        mbtn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Activity.this, SignIn_Activity.class);
                startActivity(intent);
                finish();

            }
        });
        mbtn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main_Activity.this, SignUp_Activity.class);
                startActivity(intent);
                finish();

            }
        });
        mhelp_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


    }


}
