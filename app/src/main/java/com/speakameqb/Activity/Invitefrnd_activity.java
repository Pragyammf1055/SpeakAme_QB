package com.speakameqb.Activity;

import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.speakameqb.Adapter.InviteFrndAdapter;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.Classes.DividerItemDecoration;
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

public class Invitefrnd_activity extends AnimRootActivity {
    private static final String TAG = "Invitefrnd_activity";
    TextView toolbartext;

    JSONArray alContactsname = new JSONArray();
    JSONArray alContactsnumber = new JSONArray();
    AlertDialog mProgressDialog;
    RecyclerView recyclerView;
    RecyclerView.ItemDecoration itemDecoration;
    InviteFrndAdapter inviteFrndAdapter;
    ArrayList<AllBeans> mainArrayListAllBeans;
    ArrayList<AllBeans> allContactArrayListAllBeans;
    ArrayList<AllBeans> filterConatacArrayListAllBeans;
    ImageView back_button_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referfrnd_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        back_button_icon = (ImageView) findViewById(R.id.backButtonImageView);
        toolbartext.setText("Invite a Friend");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);

        //get the ListView Reference from xml file
        getAllContactsFromContactList();
        sendallcontact();

        back_button_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* Intent intent = new Intent(Invitefrnd_activity.this, TwoTab_Activity.class);
        startActivity(intent);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_skip) {
            Intent intent = new Intent(Invitefrnd_activity.this, TwoTab_Activity.class);
            intent.setAction("");
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getAllContactsFromContactList() {
        allContactArrayListAllBeans = new ArrayList<>();

        alContactsnumber = new JSONArray();
        alContactsname = new JSONArray();
        ArrayList<Contact> listContacts = new ContactFetcher(Invitefrnd_activity.this).fetchAll();

        for (Contact contact : listContacts) {
            for (ContactPhone phone : contact.numbers) {
                AllBeans allBeans = new AllBeans();
                Log.d(TAG, "ContactFetch :- " + contact.name + "::" + phone.number);

                String number = phone.number.replace("-", "").replace(" ", "");

                if (number.replace("-", "").replace(" ", "").length() > 10) {

                    Log.v(TAG, "Length_10 if Length is more than 10 :- " + contact.name + "::" + phone.number);

                    number = getLastnCharacters(number, 10);

                    Log.v(TAG, "Length_10 Getting last 10 characters from string  :- " + number);
                } else {
                    number = phone.number;
                }

                Log.v(TAG, "Length_10 Phone number after conditions :- " + number + " :: " + contact.name);

                allBeans.setFriendname(contact.name);
                allBeans.setFriendmobile(number);

                alContactsnumber.put(number);
                alContactsname.put(contact.name);

                allContactArrayListAllBeans.add(allBeans);
            }
        }
    }

    //------------------------------------------------------------------------------------//
    public String getLastnCharacters(String inputString, int subStringLength) {
        int length = inputString.length();
        if (length <= subStringLength) {
            return inputString;
        }
        int startIndex = length - subStringLength;
        return inputString.substring(startIndex);
    }

    //---------------------------------------------------------------------------------------------//

    private void sendallcontact() {

//        7313398300
        Log.v(TAG, "Contact Array List Number :- " + alContactsnumber.toString());
        Log.v(TAG, "Contact Array List Name :- " + alContactsname.toString());
        mProgressDialog = new SpotsDialog(Invitefrnd_activity.this);
        mProgressDialog.setTitle("Your contact is updating...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("method", AppConstants.CHECKLIST);
            jsonObject.put("user_id", AppPreferences.getLoginId(Invitefrnd_activity.this));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(Invitefrnd_activity.this));
            jsonObject.put("contactNumber", alContactsnumber);
            jsonObject.put("contactName", alContactsname);
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));
            jsonArray.put(jsonObject);
            Log.d(TAG, "JSON REQUEST CHECKLIST :- " + jsonArray.toString());
            System.out.println("sendallcontact>>>>>" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(Invitefrnd_activity.this);
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
                                AppPreferences.setAckwnoledge(Invitefrnd_activity.this, topObject.getString("acknoledgeinsert"));
                                System.out.println("valueallcontact" + AppPreferences.getAckwnoledge(Invitefrnd_activity.this));
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

    //---------------------------------------------------------------------------------------------------//
    private void importcontact() {
        final AlertDialog mProgressDialog = new SpotsDialog(Invitefrnd_activity.this);
        mProgressDialog.setTitle("Your contact is retrieving...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("method", AppConstants.GETCHECKLIST);
            jsonObject.put("user_id", AppPreferences.getLoginId(Invitefrnd_activity.this));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(Invitefrnd_activity.this));
            jsonArray.put(jsonObject);
            Log.v(TAG, "JSON REQUEST GETCHECKLIST :- " + jsonArray);
            System.out.println("sendJson" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(Invitefrnd_activity.this);
        jsonParser.parseVollyJsonArray(AppConstants.USER_CONNECTION_APIS, 1, jsonArray, new VolleyCallback() {
            @Override
            public void backResponse(String response) {

                jsonParserMethod(response);
                mProgressDialog.dismiss();

            }
        });

    }

    //-------------------------------------------------------------------------------------------//
    private void jsonParserMethod(String response) {
        filterConatacArrayListAllBeans = new ArrayList<>();

        if (response != null) {
            try {
                JSONObject mainObject = new JSONObject(response);

                if (mainObject.getString("status").equalsIgnoreCase("200")) {
                    JSONArray orderArray = mainObject.getJSONArray("result");
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
                        Log.d(TAG, " id for contact from speakAMe " + topObject.getString("speaka_id"));

                        // friendlist.add(allBeans);
                        filterConatacArrayListAllBeans.add(allBeans);
                        Collections.sort(filterConatacArrayListAllBeans, new Comparator<AllBeans>() {
                            @Override
                            public int compare(AllBeans lhs, AllBeans rhs) {
                                return lhs.getFriendname().compareTo(rhs.getFriendname());
                            }
                        });
                    }

                    Toast.makeText(getApplicationContext(), "Contact updated", Toast.LENGTH_LONG).show();
                    Log.d(TAG, " allcontactArrauListSize before remove items : -- " + allContactArrayListAllBeans.size());


                    for (int k = 0; k < filterConatacArrayListAllBeans.size(); k++) {
                        Log.d(TAG, " getUserNameFromArrayList : -- " + filterConatacArrayListAllBeans.get(k).getFriendmobile());
                        for (int j = 0; j < allContactArrayListAllBeans.size(); j++) {
                            String num[] = filterConatacArrayListAllBeans.get(k).getFriendmobile().split(" ");
                            Log.d(TAG, " getUserNameFromArrayList : -- " + allContactArrayListAllBeans.get(j).getFriendmobile() + " :: " + num[1]);

                            if (allContactArrayListAllBeans.get(j).getFriendmobile().equalsIgnoreCase(num[1])) {
                                Log.d(TAG, " allContactArrayListAllBeans : --  " + allContactArrayListAllBeans.get(j).getFriendmobile());
                                allContactArrayListAllBeans.remove(j);
                            }
                        }
                    }
                    Log.d(TAG, " allcontactArrauListSize after remove items : -- " + allContactArrayListAllBeans.size());

                   /* ddddddddddddddddd
                  */
                    setAdapter(allContactArrayListAllBeans);

                } else if (mainObject.getString("status").equalsIgnoreCase("400")) {
                    Toast.makeText(getApplicationContext(), "No contact found", Toast.LENGTH_LONG).show();
                } else if (mainObject.getString("status").equalsIgnoreCase("100")) {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //--------------------------------------------------------------------------------------------//

    private void setAdapter(ArrayList<AllBeans> friendlist) {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        itemDecoration = new DividerItemDecoration(Invitefrnd_activity.this, DividerItemDecoration.VERTICAL_LIST);

        inviteFrndAdapter = new InviteFrndAdapter(Invitefrnd_activity.this, friendlist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Invitefrnd_activity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(inviteFrndAdapter);
        inviteFrndAdapter.notifyDataSetChanged();

    }


}


/*
package com.speakameqb.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.R;

import java.io.File;
import java.io.FileOutputStream;

import dmax.dialog.SpotsDialog;

public class Invitefrnd_activity extends AnimRootActivity {
    private static final String TAG = "Invitefrnd_activity";
    TextView toolbartext;

    ListView listViewPhoneBook;
    SimpleCursorAdapter mAdapter;
    MatrixCursor mMatrixCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referfrnd_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbartext = (TextView) findViewById(R.id.toolbar_title);
        toolbartext.setText("Invite a friend");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        toolbartext.setTypeface(tf1);

        //get the ListView Reference from xml file
        listViewPhoneBook = (ListView) findViewById(R.id.listview);


        // The contacts from the contacts content provider is stored in this cursor
        mMatrixCursor = new MatrixCursor(new String[]{"_id", "name", "photo", "details"});

        // Adapter to set data in the listview
        mAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.custom_invitefriend, null,

                new String[]{"name", "photo", "details"},
                new int[]{R.id.tv_name, R.id.iv_photo, R.id.tv_number}, 0);

        // Getting reference to listview


        // Setting the adapter to listview
        listViewPhoneBook.setAdapter(mAdapter);
        listViewPhoneBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String MobileNo = ((TextView) view.findViewById(R.id.tv_number)).getText().toString();
                Log.v(TAG, "Mobile no :- " + MobileNo);
                Uri phoneNoUri = Uri.parse("sms:" + MobileNo);
                String shareBody = "Download SpeakAme messenger to chat with me in all languages.\n\n http://speakame.com/dl/";

                Intent sharingIntent = new Intent(Intent.ACTION_SENDTO);
                sharingIntent.putExtra("sms_body", shareBody);
                sharingIntent.setData(phoneNoUri);
                startActivity(sharingIntent);
            }
        });
        // Creating an AsyncTask object to retrieve and load listview with contacts
        ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();
        // Starting the AsyncTask process to retrieve and load listview with contacts
        listViewContactsLoader.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_skip) {
            Intent intent=new Intent(Invitefrnd_activity.this,TwoTab_Activity.class);
            intent.setAction("");
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    */
/**
 * An AsyncTask class to retrieve and load listview with contacts
 *//*

    private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor> {
        // ProgressDialog mprogressdialog;
        AlertDialog mprogressdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mprogressdialog = new SpotsDialog(Invitefrnd_activity.this);
            //  mprogressdialog=new ProgressDialog(Invitefrnd_activity.this);
            mprogressdialog.setMessage("Loading...");
            mprogressdialog.setCancelable(false);
            mprogressdialog.show();

        }

        @Override
        protected Cursor doInBackground(Void... params) {
            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

            // Querying the table ContactsContract.Contacts to retrieve all the contacts
            Cursor contactsCursor = getContentResolver().query(contactsUri, null, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

            if (contactsCursor.moveToFirst()) {
                do {
                    long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));


                    Uri dataUri = ContactsContract.Data.CONTENT_URI;
                    Cursor dataCursor = null;
                    // Querying the table ContactsContract.Data to retrieve individual items like
                    // home phone, mobile phone, work email etc corresponding to each contact
                    try {
                        dataCursor = getContentResolver().query(dataUri, null,
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


                        if (dataCursor.moveToFirst()) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (dataCursor != null)
                            dataCursor.close();
                    }

                } while (contactsCursor.moveToNext());
            }
            return mMatrixCursor;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            // Setting the cursor containing contacts to listview
            mAdapter.swapCursor(result);
            mprogressdialog.dismiss();
        }
    }

}
*/
