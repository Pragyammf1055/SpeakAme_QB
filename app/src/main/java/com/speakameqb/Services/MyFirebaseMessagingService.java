package com.speakameqb.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBSubscription;
import com.speakameqb.Activity.ChatActivity;
import com.speakameqb.Activity.Main_Activity;
import com.speakameqb.Activity.TwoTab_Activity;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.R;
import com.speakameqb.utils.AppPreferences;
import com.speakameqb.utils.Contactloader.Contact;
import com.speakameqb.utils.Contactloader.ContactFetcher;
import com.speakameqb.utils.Contactloader.ContactPhone;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by MMFA-YOGESH on 03-Feb-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    boolean makeHeadsUpNotification = true;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "RemoteMessage :- : " + remoteMessage.getData().toString());
        Log.d(TAG, "From :- : " + remoteMessage.getFrom());
        Log.d(TAG, "RemoteMessage 123 :- : " + remoteMessage.getData().get("message"));
        Log.d(TAG, "RemoteMessage 456 :- : " + remoteMessage.getData().get("notification_alert"));
        String newDeviceLogin = remoteMessage.getData().get("notification_alert");
        Log.d(TAG, "RemoteMessage 789 :- : " + newDeviceLogin);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyFirebaseMessagingService.this);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(AppPreferences.CONTACT_ARRAY_LIST_SAVE, null);
        Type type = new TypeToken<ArrayList<AllBeans>>() {
        }.getType();

        Log.v(TAG, "Get shared preference data :- " + json);

//        Log.v(TAG, "Notification for logging out 1 :- " + remoteMessage.getData().get("notification_alert"));
//        String message = remoteMessage.getData().get("notification_alert");
//        Log.v(TAG, "Notification for logging out 4 :- " + message.replace("[", "").replace("]", "").replaceAll("\"", ""));

        ArrayList<AllBeans> arrayList = gson.fromJson(json, type);

        if (newDeviceLogin != null) {
            if (remoteMessage.getData().get("notification_alert").replace("[", "").replace("]", "").replaceAll("\"", "").equalsIgnoreCase("You are already logged in to another device")) {

                String message = "" + remoteMessage.getData().get("notification_alert");

                Log.d(TAG, "Message :-" + message.replace("[", "").replace("]", "").replaceAll("\"", ""));

                String Msg = message.replace("[", "").replace("]", "").replaceAll("\"", "");

                sendNotification(Msg);

            }
        } else {
/*
            String s = remoteMessage.getData().get("message");
            Log.d(TAG, "Normal message  :- : " + s);

            if (*//*TwoTab_Activity.instance == null &&*//* ChatActivity.instance == null) {
                sendNotificationNormal(s);
            }*/
/*    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */


            String s = "";
            String qb_sender = "";
            String qb_sender_no = "";  // sender no  chatMessage.sender

            if (remoteMessage.getData().get("message_new") != null) {

                Log.d(TAG, "INSIDE firebase response null :- : " + qb_sender_no);
                s = remoteMessage.getData().get("message_new");
                qb_sender = remoteMessage.getData().get("qb_sender");
                qb_sender_no = remoteMessage.getData().get("qb_sender_no");
            }

            Log.d(TAG, "Normal message 002000 first :- : " + qb_sender_no);
            Log.d(TAG, "Normal message 002000 first :- : " + qb_sender_no);

            if (!qb_sender_no.equalsIgnoreCase("")) {

                if (qb_sender_no != null) {

                    if (arrayList != null) {
                        for (int j = 0; j < arrayList.size(); j++) {
                            if (qb_sender_no.equalsIgnoreCase(arrayList.get(j).getFriendmobile().replace("+", "").replace(" ", ""))) {
                                Log.d(TAG, " SaveArrayListShared ..... " + arrayList.get(j).getFriendname() + " : " + arrayList.get(j).getFriendmobile().replace("+", "").replace(" ", ""));
                                qb_sender_no = arrayList.get(j).getFriendname();
                            }
                        }
                    } else {
                        qb_sender_no = qb_sender;
                    }
                }

                Log.d(TAG, "Normal message :- : " + s);
                Log.d(TAG, "Normal message 00 :- : " + qb_sender);
                Log.d(TAG, "Normal message 002000 second :- : " + qb_sender_no);

                if (/*TwoTab_Activity.instance == null &&*/ ChatActivity.instance == null) {
                    if (s == null || s.equalsIgnoreCase("null")) {
                        Log.d(TAG, " If S is null " + s + " : ");
                    } else {
                        Log.d(TAG, " If S is not null " + s + " : ");
                        sendNotificationNormal(s, qb_sender_no);
                    }
                } else if (ChatActivity.instance != null) {

                    Log.d(TAG, " ChatActivity.instance 123 :- " + ChatActivity.FriendMobileTWO + " :  1234");
                    Log.d(TAG, " ChatActivity.instance qb_sender_no 123 :-  " + qb_sender_no + " :  1234");
                    if (ChatActivity.FriendMobileTWO.equalsIgnoreCase(remoteMessage.getData().get("qb_sender_no"))) {
                        Log.d(TAG, " If S is not null " + s + " : ");
//                    Toast.makeText(getApplicationContext(), "dont show notification !!!", Toast.LENGTH_SHORT).show();
                    } else {
                        sendNotificationNormal(s, qb_sender_no);
                    }
                }
            }
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {

        Intent intent = new Intent(this, Main_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("SpeakAme Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
//                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        AppPreferences.setEmail(this, "");
        AppPreferences.setMobileuser(this, "");
        AppPreferences.setFirstUsername(this, "");
        AppPreferences.setLoginId(this, 0);

        AppPreferences.setUserprofile(this, "");
        AppPreferences.setTotf(this, "");
        AppPreferences.setEnetrSend(this, "");
        AppPreferences.setConvertTone(this, "");
        AppPreferences.setLoginStatus(this, "");
        AppPreferences.setRemainingDays(this, "");
        AppPreferences.setAckwnoledge(this, "");

        Log.v(TAG, "vygvkj fguguyg :- " + AppPreferences.getQB_LoginId(this));
        int subscriptionId = AppPreferences.getQBFcmSubscribeId(this);

        Log.i(TAG, "removing subscription :- " + AppPreferences.getQBFcmSubscribeId(this));
        Log.i(TAG, "removing subscription :- " + subscriptionId);

//        SubscribeService.subscribeToPushes(this, false);
//        SubscribeService.unSubscribeFromPushes(this);
//        QBPushNotifications.deleteSubscription(subscriptionId);

//        deleteSubs(subscriptionId);

        AppPreferences.setQBFcmSubscribeId(this, 0);
        DatabaseHelper.getInstance(this).deleteDB();
    }

    private void deleteSubs(final int subscriptionId) {

        QBPushNotifications.getSubscriptions().performAsync(new QBEntityCallback<ArrayList<QBSubscription>>() {
            @Override
            public void onSuccess(ArrayList<QBSubscription> qbSubscriptions, Bundle bundle) {

                Log.v(TAG, "QBPushNotificatio arraylist :== " + qbSubscriptions);

                for (QBSubscription qbSubscription : qbSubscriptions) {

                    Log.v(TAG, "All Subscription id :--  " + qbSubscription.getId());

                    if (qbSubscription.getId() != subscriptionId) {

                        Log.v(TAG, "Subscription id :== " + qbSubscription.getId());

                        QBPushNotifications.deleteSubscription(subscriptionId).performAsync(new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {
                                Log.d(TAG, "push on sucess deleted");
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Log.d(TAG, "push on on ERROR");
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void sendNotificationNormal(String messageBody, String senderName) {

        Intent intent = new Intent(this, TwoTab_Activity.class);
        intent.setAction("");

//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String ringtoneName = AppPreferences.getNotificationRingtone(this);

        String ringtoneURI = AppPreferences.getNotificationRingtoneUri(this);

        Uri sound_notification = Uri.parse(ringtoneURI);

        String vibrationType = AppPreferences.getVibrationType(this);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(senderName)
                .setContentText(messageBody);

        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher));

        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        notificationBuilder.setLights(0xff493C7C, 1000, 1000);
//        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
        if (vibrationType.equalsIgnoreCase("Off")) {
            notificationBuilder.setVibrate(null);
        } else if (vibrationType.equalsIgnoreCase("Short")) {
            notificationBuilder.setVibrate(new long[]{0, 1000, 500, 1000, 500, 1000});
        } else if (vibrationType.equalsIgnoreCase("Long")) {
            notificationBuilder.setVibrate(new long[]{0, 3000, 500, 3000, 500, 3000});
        }


        if (!ringtoneName.equals("") && !ringtoneName.isEmpty()) {

            notificationBuilder.setSound(sound_notification);

            if (vibrationType.equalsIgnoreCase("Default")) {

                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            }
        } else {
//            notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
            if (vibrationType.equalsIgnoreCase("Default")) {

                notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            } else {
                notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
            }
        }

        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }

    public String getContactName(String number) {
        String name = number;
        Log.v(TAG, "Firebase 1:- " + number);
        ArrayList<Contact> listContacts = new ContactFetcher(getApplicationContext()).fetchAll();
        for (Contact contact : listContacts) {
            for (ContactPhone phone : contact.numbers) {
                Log.v(TAG, "Firebase number:- " + phone.number);
                Log.v(TAG, "Firebase name :- " + contact.name);

                Log.d("ContactFetch", contact.name + "::" + phone.number);
                if (number.contains(phone.number) && phone.number.length() > 9) {

                    Log.v(TAG, "Firebase if condition :-  " + contact.name + "::" + phone.number);

                    return contact.name;
                }
            }
        }
        Log.v(TAG, "Firebse 1:- " + name);
        return name;
    }

}
