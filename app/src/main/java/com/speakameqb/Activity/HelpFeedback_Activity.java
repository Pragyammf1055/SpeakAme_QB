package com.speakameqb.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.R;

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

        text_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String url = "http://www.speakame.com/aboutus";
                String url = "http://www.speakame.com/about-us/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        text_faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.speakame.com/faqs";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        text_term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.speakame.com/termsandconditions/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        text_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpFeedback_Activity.this, Contactus_Activity.class);
                startActivity(intent);
                finish();
            }
        });
        text_refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(HelpFeedback_Activity.this, Invitefrnd_activity.class);
                startActivity(intent);
                finish();*/

                String text = "Download SpeakAme messenger to chat with me in all languages.\n\n http://speakame.com/dl/";
                // Uri uri = Uri.parse("android.resource://com.speakameqb/mipmap/ic_launcher.png");
                //Uri pictureUri = Uri.parse("file://my_picture");
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                // shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("text/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Invite Friend"));
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
