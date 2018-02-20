package com.speakameqb.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakameqb.Adapter.Favourite_Adapter;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.R;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.VolleyCallback;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class Favoirite_Activity extends AnimRootActivity implements VolleyCallback {
    private static final String TAG = "Favourite_Activity";
    TextView toolbartext, nocontenttext;
    Favourite_Adapter favourite_adapter;
    ImageView language, language_blue, chat, chat_blue, setting, setting_blue, star, star_blue, user, user_blue;

    RecyclerView recyclerView;
    AllBeans allBeans;
    ArrayList<AllBeans> friendlist;
    AlertDialog mProgressDialog;
    EditText srch_edit;
    boolean isSerch = true;
    private String requestFilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoirite_);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        favourite_adapter = new Favourite_Adapter();

//        getIntentVal();
        initViews();
        setListener();
        apiCallingMyFav();
    }

    private void apiCallingMyFav() {

        mProgressDialog = new SpotsDialog(Favoirite_Activity.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("method", "myfavirioute");
            jsonObject.put("userid", AppPreferences.getLoginId(Favoirite_Activity.this));
            jsonArray.put(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Json Request MyFavorite :- " + jsonArray);
        Log.v(TAG, "Json Request MyFavorite URL :- " + AppConstants.DEMOCOMMONURL);
        JSONParser jsonParser = new JSONParser(getApplicationContext());
        jsonParser.parseVollyJsonArray(AppConstants.DEMOCOMMONURL, 1, jsonArray, Favoirite_Activity.this);
    }

    private void initViews() {
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        srch_edit = (EditText) findViewById(R.id.serch_edit);
        toolbartext.setText("Favorites");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        friendlist = new ArrayList<AllBeans>();

        language = (ImageView) findViewById(R.id.iv_language);
        language_blue = (ImageView) findViewById(R.id.iv_bluelanguage);

        chat = (ImageView) findViewById(R.id.iv_chat_footer);
        chat_blue = (ImageView) findViewById(R.id.iv_chatbluefooter);
        setting = (ImageView) findViewById(R.id.iv_setting);
        setting_blue = (ImageView) findViewById(R.id.iv_bluesetting);

        star = (ImageView) findViewById(R.id.iv_star);
        star_blue = (ImageView) findViewById(R.id.iv_starblue);
        user = (ImageView) findViewById(R.id.iv_user_footer);
        user_blue = (ImageView) findViewById(R.id.iv_userbluefooter);
        nocontenttext = (TextView) findViewById(R.id.novaluetext);
        star_blue.setVisibility(View.VISIBLE);
        star.setVisibility(View.GONE);

        if (AppPreferences.getTotf(Favoirite_Activity.this).equalsIgnoreCase("1")) {
            user.setVisibility(View.VISIBLE);
            user_blue.setVisibility(View.GONE);
        }

        if (AppPreferences.getTotf(Favoirite_Activity.this).equalsIgnoreCase("0")) {
            user_blue.setVisibility(View.VISIBLE);
            user.setVisibility(View.GONE);
        }

        if (user.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Favoirite_Activity.this, "1");
        }

        if (user_blue.getVisibility() == View.VISIBLE) {

            AppPreferences.setTotf(Favoirite_Activity.this, "0");

        }

    }

    private void setListener() {

        user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user_blue.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Favoirite_Activity.this, "0");
                user.setVisibility(View.GONE);
                return true;
            }
        });
        user_blue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                user.setVisibility(View.VISIBLE);
                AppPreferences.setTotf(Favoirite_Activity.this, "1");
                user_blue.setVisibility(View.GONE);
                return true;
            }
        });

        srch_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                String value = srch_edit.getText().toString().toLowerCase(Locale.getDefault());
                Log.v(TAG, "Search txt :- " + value);
                if (favourite_adapter != null) {
                    favourite_adapter.filter(value.toLowerCase());
                }
//                if (value.length() != 0) {
//                    checkBox.setVisibility(View.GONE);
//
//                } else {
//                    checkBox.setVisibility(View.VISIBLE);
//                }
            }
        });

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language_blue.setVisibility(View.VISIBLE);
                language.setVisibility(View.GONE);
                star_blue.setVisibility(View.GONE);
                star.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Favoirite_Activity.this, Languagelearn_activity.class);
                startActivity(intent);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat_blue.setVisibility(View.VISIBLE);
                chat.setVisibility(View.GONE);
                star_blue.setVisibility(View.GONE);
                star.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Favoirite_Activity.this, TwoTab_Activity.class);
                intent.setAction("");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                // finish();
            }
        });


//        user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                user.setVisibility(View.GONE);
//                user_blue.setVisibility(View.VISIBLE);
//                star_blue.setVisibility(View.GONE);
//                star.setVisibility(View.VISIBLE);
//                Intent intent = new Intent(Favoirite_Activity.this, EditProfile_Activity.class);
//                startActivity(intent);
//            }
//        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting.setVisibility(View.GONE);
                setting_blue.setVisibility(View.VISIBLE);
                star_blue.setVisibility(View.GONE);
                star.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Favoirite_Activity.this, Setting_Activity.class);
                startActivity(intent);
            }
        });
    }

    private void getIntentVal() {

        if (getIntent().getAction().equalsIgnoreCase("ImagefromGall")) {
            requestFilePath = getIntent().getStringExtra("requestFilePath");
            Log.v(TAG, "Image sending to fav user from gallery :- " + requestFilePath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);

        //-*------------------------------------------------------------------------------------//
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {

                    favourite_adapter.filter("");
// listView.clearTextFilter();
                } else {
//String value = srch_edit.getText().toString().toLowerCase(Locale.getDefault());
                    Log.v(TAG, "Search txt gggggg:- " + newText);
                    if (favourite_adapter != null) {
                        favourite_adapter.filter(newText.toLowerCase());
                    }
// adapter.filter(newText);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isSerch) {
                isSerch = false;
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
                srch_edit.setVisibility(View.VISIBLE);
            } else {
                isSerch = true;
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.search);
                srch_edit.setVisibility(View.GONE);
            }

            return true;
        }
        if (id == R.id.action_contact) {
            Intent intent = new Intent(Favoirite_Activity.this, ContactImport_Activity.class);
            intent.setAction("");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        star.setVisibility(View.GONE);
        star_blue.setVisibility(View.VISIBLE);

        language_blue.setVisibility(View.GONE);
        language.setVisibility(View.VISIBLE);

        chat.setVisibility(View.VISIBLE);
        chat_blue.setVisibility(View.GONE);

//        user.setVisibility(View.VISIBLE);
//        user_blue.setVisibility(View.GONE);

        setting.setVisibility(View.VISIBLE);
        setting_blue.setVisibility(View.GONE);
    }

    @Override
    public void backResponse(String response) {
        Log.d("response", response);
        Log.v(TAG, "Json Response add Favoriute :- " + response);
        //  mProgressDialog.dismiss();
        if (response != null) {
            try {
                JSONObject mainObject = new JSONObject(response);

                if (mainObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray orderArray = mainObject.getJSONArray("result");

                    for (int i = 0; orderArray.length() > i; i++) {
                        JSONObject topObject = orderArray.getJSONObject(i);
                        allBeans = new AllBeans();
                        allBeans.setFriendid(topObject.getString("speaka_id"));
                        allBeans.setFriendname(topObject.getString("person_name"));
                        allBeans.setFriendmobile(topObject.getString("speaka_number"));
                        allBeans.setFriendimage(topObject.getString("user_image"));
                        allBeans.setFriendStatus(topObject.getString("userProfileStatus"));
                        allBeans.setFriendQB_id(Integer.parseInt(topObject.getString("qb_id")));
                        allBeans.setGroupName("");

                        friendlist.add(allBeans);

                        //////Sorting name////////
                        Collections.sort(friendlist, new Comparator<AllBeans>() {
                            @Override
                            public int compare(AllBeans lhs, AllBeans rhs) {
                                return lhs.getFriendname().compareTo(rhs.getFriendname());
                            }
                        });
                        //////Sorting name////////
                    }
                    if (friendlist != null) {

                        favourite_adapter = new Favourite_Adapter(Favoirite_Activity.this, friendlist, requestFilePath);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Favoirite_Activity.this);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(favourite_adapter);
                    }

                } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                    nocontenttext.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    //    Toast.makeText(getApplicationContext(), "No data found in Menu section", Toast.LENGTH_LONG).show();
                } else if (mainObject.getString("status").equalsIgnoreCase("100")) {

                    nocontenttext.setVisibility(View.VISIBLE);
                    nocontenttext.setText("no internet connection");
                    recyclerView.setVisibility(View.GONE);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ContactImport_Activity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ContactImport_Activity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
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
}
