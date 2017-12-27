package com.speakame.Services;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.speakame.AppController;
import com.speakame.Beans.AllBeans;
import com.speakame.Xmpp.LocalBinder;
import com.speakame.utils.AppConstants;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Contactloader.Contact;
import com.speakame.utils.Contactloader.ContactFetcher;
import com.speakame.utils.Contactloader.ContactPhone;
import com.speakame.utils.Function;
import com.speakame.utils.JSONParser;
import com.speakame.utils.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dmax.dialog.SpotsDialog;

/**
 * Created by Pragya on 18-May-17.
 */

public class ContactImportService extends Service {

    public static String TAG = "ContactImportService";
    Context context;
    AlertDialog mProgressDialog;
    JSONArray alContactsname = new JSONArray();
    JSONArray alContactsnumber = new JSONArray();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate: Services !!!!!!!!!!!!!!!!!");
        Log.d(TAG, " AppController.privateMessageCountravi sercvice " + AppController.privateMessageCount);

        ArrayList<Contact> listContacts = new ContactFetcher(getApplicationContext()).fetchAll();
        for (Contact contact : listContacts) {
            for (ContactPhone phone : contact.numbers) {
                Log.d("ContactFetch", contact.name + "::" + phone.number);
                alContactsnumber.put(phone.number);
                alContactsname.put(contact.name);
            }
        }

        sendallcontact();

    }


    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        Log.d(TAG, "onCreate: Services !!!!!!!!!!!!!!!!!");
        ArrayList<Contact> listContacts = new ContactFetcher(getApplicationContext()).fetchAll();
        for (Contact contact : listContacts) {
            for (ContactPhone phone : contact.numbers) {
                Log.d("ContactFetch", contact.name + "::" + phone.number);
                alContactsnumber.put(phone.number);
                alContactsname.put(contact.name);
            }
        }

        sendallcontact();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder<ContactImportService>(this);
    }


    private void sendallcontact() {
        mProgressDialog = new SpotsDialog(getApplicationContext());
        mProgressDialog.setTitle("Your contact is updating...");
        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("method", AppConstants.CHECKLIST);
            jsonObject.put("contactNumber", alContactsnumber);
            jsonObject.put("contactName", alContactsname);
            jsonObject.put("user_id", AppPreferences.getLoginId(getApplicationContext()));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(getApplicationContext()));
            jsonObject.put("fcm_mobile_id", FirebaseInstanceId.getInstance().getToken());
            // jsonObject.put("mobile_number", AppPreferences.getMobileuser(MainScreenActivity.this));

            jsonArray.put(jsonObject);
            System.out.println("sendallcontact>>>>>" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(getApplicationContext());
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
                                AppPreferences.setAckwnoledge(getApplicationContext(), topObject.getString("acknoledgeinsert"));
                                System.out.println("valueallcontact" + AppPreferences.getAckwnoledge(getApplicationContext()));
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

        final AlertDialog mProgressDialog = new SpotsDialog(getApplicationContext());
        mProgressDialog.setTitle("Your contact is retrieving...");
        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("method", AppConstants.GETCHECKLIST);
            jsonObject.put("user_id", AppPreferences.getLoginId(getApplicationContext()));
            jsonObject.put("mobile_uniquekey", Function.getAndroidID(getApplicationContext()));

            jsonArray.put(jsonObject);
            System.out.println("sendJson" + jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser(getApplicationContext());
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
                                allBeans.setGroupName("");
                                // DatabaseHelper.getInstance(getApplicationContext()).insertContact(allBeans);
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
//                            Toast.makeText(getApplicationContext(), "Contact updated", Toast.LENGTH_LONG).show();
                            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ContactImportService.this);
                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(friendlist);
                            editor.putString(AppPreferences.CONTACT_ARRAY_LIST_SAVE, json);
                            editor.commit();

                        } else if (mainObject.getString("status").equalsIgnoreCase("400")) {

                            Toast.makeText(getApplicationContext(), "There is a problem while syncing contact !!!", Toast.LENGTH_LONG).show();
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
//                    mProgressDialog.dismiss();
                }

            }
        });
        System.out.println("AppConstants.COMMONURL---------" + AppConstants.DEMOCOMMONURL);
        System.out.println("jsonObject" + jsonObject);
    }
}
