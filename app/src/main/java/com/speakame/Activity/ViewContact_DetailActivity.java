package com.speakame.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.speakame.Classes.AnimRootActivity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Xmpp.ChatMessage;
import com.speakame.Xmpp.MediaAdapter;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class ViewContact_DetailActivity extends AnimRootActivity {
    TextView toolbartext, edit_status, mtextname, mtextnumber, mtextstatus;
    ImageView imageView;
    String Name, Number, Status, Image, value;
    LinearLayout l1;
    AlertDialog mProgressDialog;
    MediaAdapter mediaAdapter;
    RecyclerView recyclerView;
    private String groupName = "";
    private ArrayList<ChatMessage> chatlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact__detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        edit_status = (TextView) findViewById(R.id.editstatus);
        mtextname = (TextView) findViewById(R.id.textname);
        mtextnumber = (TextView) findViewById(R.id.textnumber);
        mtextstatus = (TextView) findViewById(R.id.textstatus);
        imageView = (ImageView) findViewById(R.id.userimage);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        l1 = (LinearLayout) findViewById(R.id.lin1);


        Typeface tf3 = Typeface.createFromAsset(getAssets(), "Raleway-SemiBold.ttf");
        edit_status.setTypeface(tf3);

        Typeface tf2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mtextname.setTypeface(tf2);
        mtextnumber.setTypeface(tf2);
        mtextstatus.setTypeface(tf2);

        Intent intent = getIntent();
        Name = intent.getStringExtra("name");
        Number = intent.getStringExtra("number");
        value = intent.getStringExtra("value");

        //Status = intent.getStringExtra("status");
        Image = intent.getStringExtra("image");

        toolbartext.setText(Name);
        mtextname.setText(Name);
        mtextnumber.setText(Number);
        Status = DatabaseHelper.getInstance(ViewContact_DetailActivity.this).getUSerStatus(Number);
        if (Status.equalsIgnoreCase("")) {
            mtextstatus.setText("Cant't talk speakame only");
        } else {
            mtextstatus.setText(Status);
        }


        if (Image.equalsIgnoreCase("")) {
            imageView.setBackgroundResource(R.drawable.user_icon);

        } else {
            Picasso.with(getApplicationContext()).load(Image)
                    .error(R.drawable.user_icon)
                    .resize(200, 200)
                    .into(imageView);
        }


        if (value.equalsIgnoreCase("1")) {
            l1.setVisibility(View.VISIBLE);

            chatlist = DatabaseHelper.getInstance(ViewContact_DetailActivity.this).getMedia("chat", Number);

            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen._5sdp);
            recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
            recyclerView.setLayoutManager(layoutManager);

            mediaAdapter = new MediaAdapter(ViewContact_DetailActivity.this, chatlist);
            recyclerView.setAdapter(mediaAdapter);

        } else {
            l1.setVisibility(View.GONE);
        }



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_detail, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
//        if (id == R.id.action_block) {
//            BlockDialog();
//
//
//            return true;
//        }


        return super.onOptionsItemSelected(item);
    }

    private void imageDialog() {
        final Dialog dialog = new Dialog(ViewContact_DetailActivity.this, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Image");
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_popup);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_dialog));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView);

        if (Image.equalsIgnoreCase("")) {
            imageView.setBackgroundResource(R.drawable.user_icon);

        } else {
            Picasso.with(this).load(Image).error(R.drawable.user_icon)
                    .resize(200, 200)
                    .into(imageView);


        }
        dialog.show();

    }



    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

}
