package com.speakameqb.Activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class CollapsingToolbarActivity extends AppCompatActivity {
    private static final String TAG = "CollapsingToolbar";
    TextView name_text_view, contact_text_view, staus_text_view, busy_text_view, block_text_view;
    String myName, myNumber, myValue, myImage, status;
    CollapsingToolbarLayout toolbar_layout;
    ImageView profilePicImageView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collapsing_toolbar);
/*
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setTintColor(ContextCompat.getColor(this, R.color.primaryDark));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
*/

        setToolbar();
        getFromIntent();
        initViews();
        status = DatabaseHelper.getInstance(CollapsingToolbarActivity.this).getUSerStatus(myNumber);
        setDataIntoTheViewFromIntent();
    }

    private void setToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar_layout.setCollapsedTitleTextColor(Color.WHITE);

    }

    public void getFromIntent() {

        Intent intent = getIntent();
        myName = intent.getStringExtra("name");
        myNumber = intent.getStringExtra("number");
        myValue = intent.getStringExtra("value");
        //Status = intent.getStringExtra("status");
        myImage = intent.getStringExtra("image");

    }

    public void initViews() {
        name_text_view = (TextView) findViewById(R.id.name_text_view);
        contact_text_view = (TextView) findViewById(R.id.contact_text_view);
        staus_text_view = (TextView) findViewById(R.id.staus_text_view);
        busy_text_view = (TextView) findViewById(R.id.busy_text_view);
        block_text_view = (TextView) findViewById(R.id.block_text_view);

        profilePicImageView = (ImageView) findViewById(R.id.profilePicImageView);
    }

    public void setDataIntoTheViewFromIntent() {
        name_text_view.setText(myName);
//        toolbar_layout.setTitle(myName);
        contact_text_view.setText(myNumber);
        busy_text_view.setText(status);
        /*name_text_view.setText(myName);
        name_text_view.setText(myName);*/

        int displayWidth = getWindowManager().getDefaultDisplay().getHeight();

        Picasso.with(CollapsingToolbarActivity.this)
                .load(myImage)
                .error(R.drawable.profile_pic_default_800)
                .placeholder(R.drawable.profile_pic_default_800)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {

                        profilePicImageView.setImageBitmap(bitmap);
                        Log.v(TAG, "Inside this 123");
                        Palette.from(bitmap)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        Log.v(TAG, "palette generated ...");
                                        Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                        if (textSwatch == null) {
//                                            Toast.makeText(CollapsingToolbarActivity.this, "Null swatch :(", Toast.LENGTH_SHORT).show();
                                            Log.v(TAG, "Null swatch :(");
                                            return;
                                        }

//                                        backgroundGroup.setBackgroundColor(textSwatch.getRgb());
//                                        titleColorText.setTextColor(textSwatch.getTitleTextColor());
//                                        bodyColorText.setTextColor(textSwatch.getBodyTextColor());

                                        ActionBar actionbar = getActionBar();
                                        int color = textSwatch.getRgb();
                                        Log.v(TAG, "Color COde :- " + color);
//                                        actionbar.setBackgroundDrawable(new ColorDrawable(color));

//                                        toolbar.setBackgroundDrawable(new ColorDrawable(textSwatch.getRgb()));
//                                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(textSwatch.getRgb()));

                                    }
                                });

                    }

                    @Override
                    public void onBitmapFailed(Drawable drawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable drawable) {

                    }
                });

        profilePicImageView.getLayoutParams().height = displayWidth / 2;
/*

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth(); // ((display.getWidth()*20)/100)
        int height = display.getHeight();// ((display.getHeight()*30)/100)
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
        profilePicImageView.setLayoutParams(parms);

*/

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


