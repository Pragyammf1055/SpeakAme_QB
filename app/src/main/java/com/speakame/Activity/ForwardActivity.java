package com.speakame.Activity;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.speakame.Adapter.ForwardAdapter;
import com.speakame.Adapter.GroupListAdapter;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Xmpp.ChatMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ForwardActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton forward;
    public static TextView forwardedName;
    public static RelativeLayout footer,content_forward;
    RecyclerView recyclerView;
    ForwardAdapter forwardAdapter;
    JSONArray forwardMsg ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        forwardedName = (TextView) findViewById(R.id.forwardedName);
        forward = (ImageButton) findViewById(R.id.forward);
        footer = (RelativeLayout) findViewById(R.id.footer);
        content_forward = (RelativeLayout) findViewById(R.id.content_forward);

        try {
            forwardMsg = new JSONArray(getIntent().getStringExtra("message"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        List<ChatMessage> chatMessageList = DatabaseHelper.getInstance(ForwardActivity.this).getReciever();
        forwardAdapter = new ForwardAdapter(ForwardActivity.this, chatMessageList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ForwardActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(forwardAdapter);

        forward.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == android.R.id.home){
            supportFinishAfterTransition();
            finish();
        }if(id == R.id.forward){
            forwardAdapter.messageForward(forwardMsg);
            finish();
        }
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }
    public static void footerShow() {
        footer.setVisibility(View.VISIBLE);

    }

    public static void footerHide() {
        footer.setVisibility(View.GONE);
    }
}
