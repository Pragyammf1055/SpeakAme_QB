package com.speakame.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBPresence;
import com.speakame.Adapter.ImportcontactAdapter;
import com.speakame.Beans.AllBeans;
import com.speakame.Beans.User;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Contactloader.Contact;
import com.speakame.utils.Contactloader.ContactFetcher;
import com.speakame.utils.Contactloader.ContactPhone;
import com.speakame.utils.Function;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class ContactImport_Activity extends AnimRootActivity {
    private static final String TAG = "ContactImport";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final String EXTRA_QB_DIALOG = "qb_dialog";
    RecyclerView recyclerView;
    AllBeans allBeans;
    TextView toolbartext, nocontenttext;
    EditText srch_edit;
    boolean isSerch = true;
    AlertDialog mProgressDialog;
    ImportcontactAdapter importcontactAdapter;
    JSONArray alContactsname = new JSONArray();
    JSONArray alContactsnumber = new JSONArray();
    LinearLayout linearLayout;


    public static void startForResult(Activity activity, int code) {
        startForResult(activity, code, null);
    }

    public static void startForResult(Activity activity, int code, QBChatDialog dialog) {
        Intent intent = new Intent(activity, ContactImport_Activity.class);
        intent.putExtra(EXTRA_QB_DIALOG, dialog);
        activity.startActivityForResult(intent, code);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_import_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.search);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        nocontenttext = (TextView) findViewById(R.id.novaluetext);
        srch_edit = (EditText) findViewById(R.id.serch_edit);
        toolbartext.setText("Contacts");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

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
                importcontactAdapter.filter(value.toLowerCase());
            }
        });


        /////////getiingallcontact in list///////////

        ArrayList<Contact> listContacts = new ContactFetcher(ContactImport_Activity.this).fetchAll();
        for (Contact contact : listContacts) {
            for (ContactPhone phone : contact.numbers) {
                Log.d(TAG, "ContactFetch :- " + contact.name + "::" + phone.number);

                String number = phone.number;

                if (number.replace("-", "").replace(" ", "").length() > 10) {

                    Log.v(TAG, "Length_10 if Length is more than 10 :- " + contact.name + "::" + phone.number);

                    number = getLastnCharacters(phone.number.toString(), 10);

                    Log.v(TAG, "Length_10 Getting last 10 characters from string  :- " + number);
                } else {
                    number = phone.number;
                }

                Log.v(TAG, "Length_10 Phone number after conditions :- " + number);

                alContactsnumber.put(number);
                alContactsname.put(contact.name);
            }
        }

        sendallcontact();
        checkPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contactlist, menu);
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
            View menuItemView = findViewById(R.id.action_contact);
            PopupMenu popup = new PopupMenu(this, menuItemView);
            popup.getMenuInflater().inflate(R.menu.menu_tabitem_second, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.favorites:

                            Intent intent = new Intent(ContactImport_Activity.this, Favoirite_Activity.class);
                            startActivity(intent);
                            break;

                        case R.id.addcontact:
//                            Intent intent2 = new Intent(ContactImport_Activity.this, SocialContactActivity.class);
//                            startActivity(intent2);

                            Intent intent2 = new Intent(ContactImport_Activity.this, ContactList_Activity.class);
                            startActivity(intent2);
                            break;
                        case R.id.creategroup:
                            Intent intent3 = new Intent(ContactImport_Activity.this, CreateGroupChatActivity.class);
                            startActivity(intent3);
                            break;
                        case R.id.refresh:
                            alContactsnumber = new JSONArray();
                            alContactsname = new JSONArray();
                            ArrayList<Contact> listContacts = new ContactFetcher(ContactImport_Activity.this).fetchAll();
                            for (Contact contact : listContacts) {
                                for (ContactPhone phone : contact.numbers) {

                                    Log.d(TAG, " Length_10 ContactFetch in contact import activity :- " + contact.name + "::" + phone.number);

                                    String number = phone.number;

                                    if (number.replace("-", "").replace(" ", "").length() > 10) {

                                        Log.v(TAG, "Length_10 if Length is more than 10 :- " + contact.name + "::" + phone.number);

                                        number = getLastnCharacters(phone.number.toString(), 10);

                                        Log.v(TAG, "Length_10 Getting last 10 characters from string  :- " + number);
                                    } else {
                                        number = phone.number;
                                    }

                                    Log.v(TAG, "Length_10 Phone number after conditions :- " + number);
                                    alContactsnumber.put(number);
                                    alContactsname.put(contact.name);
                                }
                            }
                            sendallcontact();
                            break;
                    }
                    return true;
                }
            });
            popup.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getLastnCharacters(String inputString,
                                     int subStringLength) {
        int length = inputString.length();
        if (length <= subStringLength) {
            return inputString;
        }
        int startIndex = length - subStringLength;
        return inputString.substring(startIndex);
    }

    private void sendallcontact() {

//        7313398300
        Log.v(TAG, "Contact Array List Number :- " + alContactsnumber.toString());
        Log.v(TAG, "Contact Array List Name :- " + alContactsname.toString());
        mProgressDialog = new SpotsDialog(ContactImport_Activity.this);
        mProgressDialog.setTitle("Your contact is updating...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("method", AppConstants.CHECKLIST);
            jsonObject.put("user_id", AppPreferences.getLoginId(ContactImport_Activity.this));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(ContactImport_Activity.this));
            jsonObject.put("contactNumber", alContactsnumber);
            jsonObject.put("contactName", alContactsname);
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));
            jsonArray.put(jsonObject);
            Log.d(TAG, "JSON REQUEST CHECKLIST :- " + jsonArray.toString());
            System.out.println("sendallcontact>>>>>" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ContactImport_Activity.this);
        jsonParser.parseVollyJsonArray(AppConstants.USER_CONNECTION_APIS, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {
                Log.d(TAG, "JSON RESPONSE CHECKLIST :- " + response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray orderArray = mainObject.getJSONArray("result");

                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject topObject = orderArray.getJSONObject(i);
                                AppPreferences.setAckwnoledge(ContactImport_Activity.this, topObject.getString("acknoledgeinsert"));
                                System.out.println("valueallcontact" + AppPreferences.getAckwnoledge(ContactImport_Activity.this));
                            }
                            importcontact();

                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                            Toast.makeText(getApplicationContext(), "Contact Not Updated", Toast.LENGTH_LONG).show();
                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                }

            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonArray);
    }

    /////////////////////////////////////

    private void importcontact() {

        final AlertDialog mProgressDialog = new SpotsDialog(ContactImport_Activity.this);
        mProgressDialog.setTitle("Your contact is retrieving...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", AppConstants.GETCHECKLIST);
            jsonObject.put("user_id", AppPreferences.getLoginId(ContactImport_Activity.this));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(ContactImport_Activity.this));

            jsonArray.put(jsonObject);
            Log.v(TAG, "JSON REQUEST GETCHECKLIST :- " + jsonArray);
            System.out.println("sendJson" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ContactImport_Activity.this);
        jsonParser.parseVollyJsonArray(AppConstants.USER_CONNECTION_APIS, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                Log.v(TAG, "JSON RESPONSE GETCHECKLIST :- " + response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);

                        if (mainObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray orderArray = mainObject.getJSONArray("result");
                            ArrayList<AllBeans> friendlist = new ArrayList<AllBeans>();
                            for (int i = 0; orderArray.length() > i; i++) {
                                JSONObject topObject = orderArray.getJSONObject(i);
                                AllBeans allBeans = new AllBeans();
                                allBeans.setFriendid(topObject.getString("speaka_id"));
                                allBeans.setFriendname(topObject.getString("person_name"));
                                allBeans.setFriendmobile(topObject.getString("speaka_number"));
                                allBeans.setFriendimage(topObject.getString("user_image"));
                                allBeans.setFriendStatus(topObject.getString("userProfileStatus"));
                                allBeans.setFavriouteFriend(topObject.getString("faviroute"));
                                allBeans.setLanguages(topObject.getString("language"));
                                allBeans.setBlockedStatus(topObject.getString("blockedStatus"));
                                if (!topObject.getString("qb_id").equalsIgnoreCase("")) {
                                }
                                allBeans.setFriendQB_id(Integer.parseInt(topObject.getString("qb_id")));
                                allBeans.setGroupName("");
                                DatabaseHelper.getInstance(ContactImport_Activity.this).insertContact(allBeans);
                                friendlist.add(allBeans);
                                Log.v(TAG, "QB ID of user :- " + allBeans.getFriendQB_id());

                                checkUserPresence(allBeans.getFriendQB_id());

                                //////Sorting name////////
                                Collections.sort(friendlist, new Comparator<AllBeans>() {
                                    @Override
                                    public int compare(AllBeans lhs, AllBeans rhs) {
                                        return lhs.getFriendname().compareTo(rhs.getFriendname());
                                    }
                                });
                                //////Sorting name////////
                            }
                            Toast.makeText(getApplicationContext(), "Contact updated", Toast.LENGTH_LONG).show();
                            importcontactAdapter = new ImportcontactAdapter(ContactImport_Activity.this, friendlist);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ContactImport_Activity.this);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
                            recyclerView.setItemAnimator(new DefaultItemAnimator());

                            recyclerView.setAdapter(importcontactAdapter);

                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                            Toast.makeText(getApplicationContext(), "No contact found", Toast.LENGTH_LONG).show();
                            //    sendallcontact();

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {

//                            nocontenttext.setVisibility(View.VISIBLE);
//                            nocontenttext.setText("no internet connection");
//                            recyclerView.setVisibility(View.GONE);
                            // sendallcontact();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                }

            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }

    private void checkUserPresence(int qbUserId) {

        if (TwoTab_Activity.сhatRoster != null) {
            QBPresence qbPresence = TwoTab_Activity.сhatRoster.getPresence(qbUserId);
            Log.v(TAG, "qbPresence :- " + qbPresence);

            if (qbPresence.getType() == QBPresence.Type.online) {
                Toast.makeText(getApplicationContext(), "User " + qbUserId + " is online", Toast.LENGTH_SHORT).show();
                Log.v(TAG, "User " + qbUserId + " is online");
                User user = new User();
                user.setFriend_id(qbUserId);
                user.setStatus("Online");
                DatabaseHelper.getInstance(ContactImport_Activity.this).InsertStatus(user);
                Log.v(TAG, "QB status online from database 111111111 :- " + DatabaseHelper.getInstance(ContactImport_Activity.this).getLastSeenQB(user.getFriend_id()));

            } else {
                Log.v(TAG, "User " + qbUserId + " is offline");
                Toast.makeText(getApplicationContext(), "User " + qbUserId + " is offline", Toast.LENGTH_SHORT).show();
            }
        }
    }
    ////////////////////////////////////

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, TwoTab_Activity.class));
        finish();
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

//    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
//        private GestureDetector gestureDetector;
//        private ContactImport_Activity.ClickListener clickListener;
//
//        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ContactImport_Activity.ClickListener clickListener) {
//            this.clickListener = clickListener;
//            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//                @Override
//                public boolean onSingleTapUp(MotionEvent e) {
//                    return true;
//                }
//
//                @Override
//                public void onLongPress(MotionEvent e) {
//                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                    if (child != null && clickListener != null) {
//                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
//                    }
//                }
//            });
//        }
//
//        @Override
//        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//            View child = rv.findChildViewUnder(e.getX(), e.getY());
//            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
//                clickListener.onClick(child, rv.getChildPosition(child));
//            }
//            return false;
//        }
//
//        @Override
//        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//        }
//
//        @Override
//        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {

            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // startAction();
                } else {
                    finish();
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

    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(ContactImport_Activity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ContactImport_Activity.this,
                    Manifest.permission.READ_CONTACTS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]
                                    {Manifest.permission.READ_CONTACTS}
                            , 1);
                }

            } else {

                ActivityCompat.requestPermissions(ContactImport_Activity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);

            }
        }
        if (ContextCompat.checkSelfPermission(ContactImport_Activity.this,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ContactImport_Activity.this,
                    Manifest.permission.WRITE_CONTACTS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]
                                    {Manifest.permission.WRITE_CONTACTS}
                            , 1);
                }

            } else {

                ActivityCompat.requestPermissions(ContactImport_Activity.this,
                        new String[]{Manifest.permission.WRITE_CONTACTS},
                        1);

            }
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
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
