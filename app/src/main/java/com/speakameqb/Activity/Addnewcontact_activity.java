package com.speakameqb.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.speakameqb.Classes.AnimRootActivity;
import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.R;
import com.speakameqb.utils.AppPreferences;

public class Addnewcontact_activity extends AnimRootActivity {
    TextView title_name;
    EditText medit_name, medit_phone, medit_email;
    String Name, MobileNumber = "", Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewcontact_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("Add New Contact");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Raleway-Regular.ttf");
        title_name.setTypeface(tf1);
        medit_name = (EditText) findViewById(R.id.name_edit);
        medit_phone = (EditText) findViewById(R.id.number_edit);
        medit_email = (EditText) findViewById(R.id.edit_email);

        Intent intent = getIntent();
        MobileNumber = intent.getStringExtra("contactnumber");
        medit_phone.setText(MobileNumber);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add:
                Name = medit_name.getText().toString();
                MobileNumber = medit_phone.getText().toString();
                Email = medit_email.getText().toString();
                dismissKeyboard(Addnewcontact_activity.this);

                if (Name.length() == 0) {
                    medit_name.setError(getResources().getString(R.string.error_field_required));
                } else if (MobileNumber.length() == 0) {
                    medit_phone.setError(getResources().getString(R.string.error_field_required));
                }
//                else if (Email.length() == 0) {
//                    medit_email.setError(getResources().getString(R.string.error_field_required));
//                }
                else {

                    addContact(Name, MobileNumber);


                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addContact(String name, String phone) {
        ContentValues values = new ContentValues();
        values.put(Contacts.People.NUMBER, phone);
        values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM + " ASC ");
        values.put(Contacts.People.LABEL, name);
        values.put(Contacts.People.NAME, name);
        //  values.put(Contacts.People.PRIMARY_EMAIL_ID, email);

        Uri dataUri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
        Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
        values.clear();
        values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
        values.put(Contacts.People.NUMBER, phone);
        updateUri = getContentResolver().insert(updateUri, values);

        DatabaseHelper.getInstance(getApplicationContext()).UpdateContactName(name, phone);
        Toast.makeText(getApplicationContext(), "Contact Added", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), TwoTab_Activity.class);
        AppPreferences.setAckwnoledge(Addnewcontact_activity.this, "");
        intent.setAction("");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

}
