package com.speakameqb.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.speakameqb.Adapter.ContactListAdapter;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.R;
import com.speakameqb.utils.AppConstants;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.Contactloader.Contact;
import com.speakameqb.utils.Contactloader.ContactFetcher;
import com.speakameqb.utils.Contactloader.ContactPhone;
import com.speakameqb.utils.Function;
import com.speakameqb.utils.JSONParser;
import com.speakameqb.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dmax.dialog.SpotsDialog;

public class ContactList_Activity extends AnimRootActivity {
    private static final String TAG = "ContactList_Activity";
    TextView title_name, createcontact;

//    GroupmemberAdapter mAdapter;
//    SimpleCursorAdapter mAdapter;
//    MatrixCursor mMatrixCursor;
//    ListView recycler_top_result;

    RecyclerView recyclerView;
    ArrayList<AllBeans> contactList = new ArrayList<AllBeans>();
    JSONArray alContactsname = new JSONArray();
    JSONArray alContactsnumber = new JSONArray();
    AlertDialog mProgressDialog;
    ContactListAdapter contactListAdapter;
    /////////////////////////////////////
    ArrayList<AllBeans> allContactArrayListAllBeans;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("Add New Contact");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        title_name.setTypeface(tf1);

//        recycler_top_result = (ListView) findViewById(R.id.recycler_top_result);
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recycler_top_result.setLayoutManager(mLayoutManager);
//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recycler_top_result.setItemAnimator(new DefaultItemAnimator());
//        mAdapter = new GroupmemberAdapter(getApplicationContext());
//        recycler_top_result.setAdapter(mAdapter);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        createcontact = (TextView) findViewById(R.id.createcontact);

        createcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactList_Activity.this, Addnewcontact_activity.class);
                intent.putExtra("contactnumber", "");
                startActivity(intent);
            }
        });

/*
        // The contacts from the contacts content provider is stored in this cursor
        mMatrixCursor = new MatrixCursor(new String[]{"_id", "name", "photo", "details"});

        // Adapter to set data in the listview
        mAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.row_contact_second, null,
                new String[]{"name", "photo", "details"},
                new int[]{R.id.name, R.id.userpic, R.id.number}, 0);

        // Getting reference to listview


        // Setting the adapter to listview
        recycler_top_result.setAdapter(mAdapter);


        // Creating an AsyncTask object to retrieve and load listview with contacts
        ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();

        // Starting the AsyncTask process to retrieve and load listview with contacts
        listViewContactsLoader.execute();*/
        allContactArrayListAllBeans = new ArrayList<>();


        ArrayList<Contact> listContacts = new ContactFetcher(ContactList_Activity.this).fetchAll();
        for (Contact contact : listContacts) {
            for (ContactPhone phone : contact.numbers) {
                Log.d(TAG, "ContactFetch new kk :-   " + contact.name + "::" + phone.number);

                String number = phone.number.replace("-", "").replace(" ", "");
                if (number.replace("-", "").replace(" ", "").length() > 10) {

                    Log.v(TAG, "Length_10 if Length is more than 10 :- " + contact.name + "::" + phone.number);

                    number = getLastnCharacters(number, 10);

                    Log.v(TAG, "Length_10 Getting last 10 characters from string  :- " + number);
                } else {
                    number = phone.number;
                }

                alContactsnumber.put(number);
                alContactsname.put(contact.name);

                AllBeans allBeans = new AllBeans();
                allBeans.setFriendid("");
                allBeans.setFriendname(contact.name);
                allBeans.setFriendmobile(number);
                allBeans.setFriendimage("");
                allBeans.setFriendStatus("");
                allBeans.setFavriouteFriend("");
                allBeans.setLanguages("");
                allBeans.setBlockedStatus("");
                allBeans.setGroupName("");
                // DatabaseHelper.getInstance(ContactImport_Activity.this).insertContact(allBeans);
                contactList.add(allBeans);
                allContactArrayListAllBeans.add(allBeans);
            }
        }
        Log.v(TAG, "List of all contacts ");

        /*for (AllBeans allBeans : contactList) {

            Log.v(TAG, "Total contacts :- " + contactList.size());
            Log.v(TAG, "Friend Name :- " + allBeans.getFriendname() + " ~~~~~~~~ Friend Number :- " + allBeans.getFriendmobile());
        }*/

        sendAllcontact();

    }

    public String getLastnCharacters(String inputString, int subStringLength) {
        int length = inputString.length();
        if (length <= subStringLength) {
            return inputString;
        }
        int startIndex = length - subStringLength;
        return inputString.substring(startIndex);
    }

    private void sendAllcontact() {

        mProgressDialog = new SpotsDialog(ContactList_Activity.this);
        mProgressDialog.setTitle("Your contact is updating...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {

            jsonObject.put("method", AppConstants.CHECKLIST);
            jsonObject.put("contactNumber", alContactsnumber);
            jsonObject.put("contactName", alContactsname);
            jsonObject.put("user_id", AppPreferences.getLoginId(ContactList_Activity.this));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(ContactList_Activity.this));
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));

            jsonArray.put(jsonObject);
            System.out.println("sendallcontact>>>>>" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ContactList_Activity.this);
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
                                AppPreferences.setAckwnoledge(ContactList_Activity.this, topObject.getString("acknoledgeinsert"));
                                System.out.println("valueallcontact" + AppPreferences.getAckwnoledge(ContactList_Activity.this));
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

    private void importcontact() {

        final AlertDialog mProgressDialog = new SpotsDialog(ContactList_Activity.this);
        mProgressDialog.setTitle("Your contact is retrieving...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", AppConstants.GETCHECKLIST);
            jsonObject.put("user_id", AppPreferences.getLoginId(ContactList_Activity.this));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(ContactList_Activity.this));

            Log.v(TAG, "Json request :- " + jsonObject);
            jsonArray.put(jsonObject);
            System.out.println("sendJson" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(ContactList_Activity.this);
        jsonParser.parseVollyJsonArray(AppConstants.USER_CONNECTION_APIS, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                Log.v(TAG, "Json response :- " + response);
                //  mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject mainObject = new JSONObject(response);
                        AllBeans allBeans;
                        if (mainObject.getString("status").equalsIgnoreCase("200")) {

                            JSONArray orderArray = mainObject.getJSONArray("result");
                            ArrayList<AllBeans> friendlist = new ArrayList<AllBeans>();

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

                                friendlist.add(allBeans);
                                getCheckedTraitsDetails(friendlist, contactList);

                                Log.v(TAG, "Size of Friends list :- " + friendlist.size());

                                //////Sorting name////////
                                Collections.sort(friendlist, new Comparator<AllBeans>() {
                                    @Override
                                    public int compare(AllBeans lhs, AllBeans rhs) {
                                        return lhs.getFriendname().compareTo(rhs.getFriendname());
                                    }
                                });
                                //////Sorting name////////
                            }
                            //-------------------------------------------------------------------------------//
                            for (int k = 0; k < friendlist.size(); k++) {
                                Log.d(TAG, " getUserNameFromArrayList : -- " + friendlist.get(k).getFriendmobile());
                                for (int j = 0; j < allContactArrayListAllBeans.size(); j++) {
                                    String num[] = friendlist.get(k).getFriendmobile().split(" ");
                                    Log.d(TAG, " getUserNameFromArrayList : -- " + allContactArrayListAllBeans.get(j).getFriendmobile() + " :: " + num[1]);

                                    if (allContactArrayListAllBeans.get(j).getFriendmobile().equalsIgnoreCase(num[1])) {
                                        Log.d(TAG, " allContactArrayListAllBeans : --  " + allContactArrayListAllBeans.get(j).getFriendmobile());
                                        allContactArrayListAllBeans.remove(j);
                                    }
                                }
                            }
                            //-------------------------------------------------------------------------------//


                            Toast.makeText(getApplicationContext(), "Contact updated", Toast.LENGTH_LONG).show();

                            contactListAdapter = new ContactListAdapter(ContactList_Activity.this, allContactArrayListAllBeans);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ContactList_Activity.this);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.addItemDecoration(new ContactList_Activity.VerticalSpaceItemDecoration(5));
                            recyclerView.setItemAnimator(new DefaultItemAnimator());

                            recyclerView.setAdapter(contactListAdapter);

                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                            Toast.makeText(getApplicationContext(), "Sync", Toast.LENGTH_LONG).show();
                            //    sendallcontact();

                        } else if (mainObject.getString("status").equalsIgnoreCase("100")) {

//                            nocontenttext.setVisibility(View.VISIBLE);
//                            nocontenttext.setText("no internet connection");
//                            recyclerView.setVisibility(View.GONE);
//                            sendallcontact();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                }

            }
        });

        System.out.println(" AppConstants :- " + AppConstants.DEMOCOMMONURL);
        System.out.println(" JsonObject :- " + jsonObject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<AllBeans> getCheckedTraitsDetails(ArrayList<AllBeans> friendlist, ArrayList<AllBeans> contactList) {

        if (friendlist != null) {

            for (int i = 0; i < contactList.size(); i++) {
                String traits = contactList.get(i).getFriendmobile();
                Log.v(TAG, "Contact name :- " + contactList.get(i).getFriendname());
                Log.v(TAG, "Contact mobile :- " + traits);
                for (int j = 0; j < friendlist.size(); j++) {

                    String mobile = friendlist.get(j).getFriendmobile();
                    String[] charSequence = mobile.split(" ");

                    Log.v(TAG, "CountryCode :- " + charSequence[0]);
                    Log.v(TAG, "Mobile Number:- " + charSequence[1]);

                    if (traits.contains(friendlist.get(j).getFriendmobile())) {

                        Log.v(TAG, "friendlist  name :;- " + friendlist.get(j).getFriendname());
                        Log.v(TAG, "friendlist selected mobile :;- " + friendlist.get(j).getFriendmobile());
                    } else {
//                        Log.v(TAG, "not in friendlist name :;- " +  friendlist.get(j).getFriendname());
//                        Log.v(TAG, "not in friendlist mobile :;- " +  friendlist.get(j).getFriendmobile());
                    }
                }
            }
            return contactList;
        }
        return contactList;
    }

    /*private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor> {
        ProgressDialog mprogressdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mprogressdialog = new ProgressDialog(ContactList_Activity.this);
            mprogressdialog.setMessage("Loading...");
            mprogressdialog.setCancelable(false);
            mprogressdialog.show();

        }

        @Override
        protected Cursor doInBackground(Void... params) {

            try {


                Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

                // Querying the table ContactsContract.Contacts to retrieve all the contacts
                Cursor contactsCursor = getContentResolver().query(contactsUri, null, null, null,
                        ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

                if (contactsCursor.moveToFirst()) {
                    do {
                        long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));


                        Uri dataUri = ContactsContract.Data.CONTENT_URI;

                        // Querying the table ContactsContract.Data to retrieve individual items like
                        // home phone, mobile phone, work email etc corresponding to each contact
                        Cursor dataCursor = getContentResolver().query(dataUri, null,
                                ContactsContract.Data.CONTACT_ID + "=" + contactId,
                                null, null);


                        String displayName = "";
                        String nickName = "";
                        String homePhone = "";
                        String mobilePhone = "";
                        String workPhone = "";
                        String photoPath = "" + R.drawable.user_icon;
                        byte[] photoByte = null;
                        String homeEmail = "";
                        String workEmail = "";
                        String companyName = "";
                        String title = "";
                        if (dataCursor == null) {

                        } else if (dataCursor.moveToFirst()) {
                            // Getting Display Name
                            displayName = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                            do {

                                // Getting NickName
                                if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE))
                                    nickName = dataCursor.getString(dataCursor.getColumnIndex("data1"));

                                // Getting Phone numbers
                                if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                                    switch (dataCursor.getInt(dataCursor.getColumnIndex("data2"))) {
                                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                            homePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                            break;
                                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                            mobilePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                            break;
                                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                            workPhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                            break;
                                    }
                                }

                                // Getting EMails
                                if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                                    switch (dataCursor.getInt(dataCursor.getColumnIndex("data2"))) {
                                        case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                                            homeEmail = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                            break;
                                        case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                                            workEmail = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                            break;
                                    }
                                }

                                // Getting Organization details
                                if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
                                    companyName = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                    title = dataCursor.getString(dataCursor.getColumnIndex("data4"));
                                }

                                // Getting Photo
                                if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
                                    photoByte = dataCursor.getBlob(dataCursor.getColumnIndex("data15"));

                                    if (photoByte != null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);

                                        // Getting Caching directory
                                        File cacheDirectory = getBaseContext().getCacheDir();

                                        // Temporary file to store the contact files
                                        File tmpFile = new File(cacheDirectory.getPath() + "/wpta_" + contactId + ".png");

                                        // The FileOutputStream to the temporary file
                                        try {
                                            FileOutputStream fOutStream = new FileOutputStream(tmpFile);

                                            // Writing the bitmap to the temporary file as png file
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOutStream);

                                            // Flush the FileOutputStream
                                            fOutStream.flush();

                                            //Close the FileOutputStream
                                            fOutStream.close();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        photoPath = tmpFile.getPath();
                                    }

                                }

                            } while (dataCursor.moveToNext());

                            String details = "";

                            // Concatenating various information to single string
//                        if (homePhone != null && !homePhone.equals(""))
//                            details = "HomePhone : " + homePhone + "\n";
                            if (mobilePhone != null && !mobilePhone.equals(""))
                                details += "" + mobilePhone + "\n";
//                        if (workPhone != null && !workPhone.equals(""))
//                            details += "WorkPhone : " + workPhone + "\n";
//                        if (nickName != null && !nickName.equals(""))
//                            details += "NickName : " + nickName + "\n";
//                        if (homeEmail != null && !homeEmail.equals(""))
//                            details += "HomeEmail : " + homeEmail + "\n";
//                        if (workEmail != null && !workEmail.equals(""))
//                            details += "WorkEmail : " + workEmail + "\n";
//                        if (companyName != null && !companyName.equals(""))
//                            details += "CompanyName : " + companyName + "\n";
//                        if (title != null && !title.equals(""))
//                            details += "Title : " + title + "\n";

                            // Adding id, display name, path to photo and other details to cursor
                            mMatrixCursor.addRow(new Object[]{Long.toString(contactId), displayName, photoPath, details});
                        }

                    } while (contactsCursor.moveToNext());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return mMatrixCursor;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            // Setting the cursor containing contacts to listview
            mAdapter.swapCursor(result);
            mprogressdialog.dismiss();
        }
    }*/

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
