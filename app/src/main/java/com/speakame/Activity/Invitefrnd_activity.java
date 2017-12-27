package com.speakame.Activity;

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

import com.speakame.Classes.AnimRootActivity;
import com.speakame.R;

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
                String shareBody = "Download SpeakAme messenger to chat with me in all languages.\n\n https://play.google.com/store/apps/details?id=com.speakame";

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
//            Intent intent=new Intent(Invitefrnd_activity.this,TwoTab_Activity.class);
//            //intent.setAction("");
//            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * An AsyncTask class to retrieve and load listview with contacts
     */
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
