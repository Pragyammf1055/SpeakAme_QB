package com.speakame.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.speakame.Adapter.ImportcontactAdapter;
import com.speakame.Beans.AllBeans;
import com.speakame.Classes.AnimRootActivity;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.ConnectionDetector;
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
    RecyclerView recyclerView;
    AllBeans allBeans;
    ArrayList<AllBeans> friendlist;
    TextView toolbartext, nocontenttext;
    EditText srch_edit;
    boolean isSerch = true;
    AlertDialog mProgressDialog;
    ImportcontactAdapter importcontactAdapter;
    JSONArray alContactsname = new JSONArray();
    JSONArray alContactsnumber = new JSONArray();

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


        friendlist = new ArrayList<AllBeans>();
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
        for(Contact contact : listContacts){
            for(ContactPhone phone : contact.numbers){
                Log.d("ContactFetch", contact.name +"::"+ phone.number);
                alContactsnumber.put( phone.number);
                alContactsname.put(contact.name);
            }

        }

        /*ContentResolver cr = getApplicationContext().getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        // jsonContacts = new JSONArray();
        if (cursor.moveToFirst()) {

            //jsonContacts = new JSONObject();
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String contactname = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));



                        alContactsnumber.put(contactNumber);
                        alContactsname.put(contactname);

                        System.out.println("contactnumber"+contactNumber);
                        System.out.println("contactname"+contactname);


                        break;

                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
        }*/
        ///////endcontactimport////////////////////////

        if (AppPreferences.getAckwnoledge(ContactImport_Activity.this).equalsIgnoreCase("1")) {

            if ((ConnectionDetector
                    .isConnectingToInternet(ContactImport_Activity.this))) {

                friendlist = DatabaseHelper.getInstance(ContactImport_Activity.this).getContactList();
                if (friendlist.isEmpty()) {
                    importcontact();
                    System.out.println("listdata" + friendlist);

                } else {

                    importcontactAdapter = new ImportcontactAdapter(ContactImport_Activity.this, friendlist);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ContactImport_Activity.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(importcontactAdapter);
                }


            } else {

                friendlist = DatabaseHelper.getInstance(ContactImport_Activity.this).getContactList();

                System.out.println("listdata" + friendlist);

                if (friendlist != null) {
                    importcontactAdapter = new ImportcontactAdapter(ContactImport_Activity.this, friendlist);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ContactImport_Activity.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(importcontactAdapter);

                }
            }

        } else {

            sendallcontact();
        }
       // int numberitem= recyclerView.getAdapter().getItemCount();


      //  System.out.println("numberitm"+numberitem);

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
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.cross);
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
                        case R.id.setting:
                            Intent intent = new Intent(ContactImport_Activity.this, Setting_Activity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case R.id.edit_profile:
                            Intent intent1 = new Intent(ContactImport_Activity.this, EditProfile_Activity.class);
                            startActivity(intent1);
                            finish();
                            break;
                        case R.id.refresh:
                            alContactsnumber = new JSONArray();
                            alContactsname = new JSONArray();

                            ArrayList<Contact> listContacts = new ContactFetcher(ContactImport_Activity.this).fetchAll();
                            for(Contact contact : listContacts){
                                for(ContactPhone phone : contact.numbers){
                                    Log.d("ContactFetch", contact.name +"::"+ phone.number);
                                    alContactsnumber.put( phone.number);
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

    private void sendallcontact() {
        mProgressDialog = new SpotsDialog(ContactImport_Activity.this);
        mProgressDialog.setTitle("Your contact is updating...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("method", AppConstants.CHECKLIST);
            jsonObject.put("contactNumber", alContactsnumber);
            jsonObject.put("contactName", alContactsname);
            jsonObject.put("user_id", AppPreferences.getLoginId(ContactImport_Activity.this));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(ContactImport_Activity.this));
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));

            jsonArray.put(jsonObject);
            System.out.println("sendallcontact>>>>>" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ContactImport_Activity.this);
        jsonParser.parseVollyJsonArray(AppConstants.USER_CONNECTION_APIS, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {
                Log.d("responseallcontact>>>>>", response);
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
                            Toast.makeText(getApplicationContext(), "Contact updated", Toast.LENGTH_LONG).show();

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
        if (friendlist != null) {
            friendlist.clear();
        }
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
            System.out.println("sendJson" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ContactImport_Activity.this);
        jsonParser.parseVollyJsonArray(AppConstants.USER_CONNECTION_APIS, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {


                Log.d("response>>>>>", response);
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
                                allBeans.setFavriouteFriend(topObject.getString("faviroute"));
                                allBeans.setLanguages(topObject.getString("language"));
                                allBeans.setBlockedStatus(topObject.getString("blockedStatus"));
                                allBeans.setGroupName("");
                                DatabaseHelper.getInstance(ContactImport_Activity.this).insertContact(allBeans);
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
                                importcontactAdapter = new ImportcontactAdapter(ContactImport_Activity.this, friendlist);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ContactImport_Activity.this);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());

                                recyclerView.setAdapter(importcontactAdapter);
                            }
                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                            Toast.makeText(getApplicationContext(), "Synch", Toast.LENGTH_LONG).show();
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


    ////////////////////////////////////

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
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
