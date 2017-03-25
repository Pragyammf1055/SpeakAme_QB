package com.speakame.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;

public class HelpFeedback_Activity extends AnimRootActivity {
    TextView toolbartext, text_about, text_faq, text_term, text_contact, text_refer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_feedback_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        text_about = (TextView) findViewById(R.id.about);
        text_faq = (TextView) findViewById(R.id.faq);
        text_term = (TextView) findViewById(R.id.termcondition);
        text_contact = (TextView) findViewById(R.id.contactus);
        text_refer = (TextView) findViewById(R.id.referfrndtext);
        toolbartext.setText("Help & Feedback");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);

        Typeface tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        text_about.setTypeface(tf2);
        text_faq.setTypeface(tf2);
        text_term.setTypeface(tf2);
        text_contact.setTypeface(tf2);
        text_refer.setTypeface(tf2);

        text_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpFeedback_Activity.this, Contactus_Activity.class);
                startActivity(intent);
            }
        });
        text_refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpFeedback_Activity.this, Invitefrnd_activity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
