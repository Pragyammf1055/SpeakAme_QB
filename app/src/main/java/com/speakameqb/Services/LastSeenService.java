package com.speakameqb.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.model.QBPresence;
import com.speakameqb.Beans.AllBeans;
import com.speakameqb.Beans.User;
import com.speakameqb.Database.DatabaseHelper;
import com.speakameqb.utils.Function;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.Collection;

/**
 * Created by Peter on 09-Nov-17.
 */

public class LastSeenService extends Service {

    private static final String TAG = "LastSeenService";
    AllBeans allBeans;
    Integer QB_Friend_Id;
    int userID;
    Handler mHandler = new Handler();
    String status;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent != null && intent.getExtras() != null) {
            userID = intent.getIntExtra("QB_Friend_Id", 0);
            Log.v(TAG, "number........." + userID);

//            callHaldler(userID);
        }


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void callHaldler(final int userID) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(1000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                subscribeUserForStatus(userID);
//
                                Intent in = new Intent("Status");

                                in.putExtra("Status", status);
//
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

    }

    private void subscribeUserForStatus(int userID) {
        Log.v(TAG, "ContactlistUserAdd..............");
//        QBRoster chatRoster = QBChatService.getInstance().getRoster();

//        int userID = QB_Friend_Id;

        QBRosterListener rosterListener = new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> userIds) {

            }

            @Override
            public void entriesAdded(Collection<Integer> userIds) {

            }

            @Override
            public void entriesUpdated(Collection<Integer> userIds) {

            }

            @Override
            public void presenceChanged(QBPresence presence) {

            }
        };

        QBSubscriptionListener subscriptionListener = new QBSubscriptionListener() {
            @Override
            public void subscriptionRequested(int userId) {

            }
        };

        QBRoster chatRoster = QBChatService.getInstance().getRoster(QBRoster.SubscriptionMode.mutual, subscriptionListener);
        chatRoster.addRosterListener(rosterListener);

        if (chatRoster.contains(userID)) {

            try {
                chatRoster.subscribe(userID);
                Log.v(TAG, "chatRoster.subscribe..............");
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.e(TAG, " Error in  last seen service :- " + e.getMessage());
            }
        } else {

            try {
                chatRoster.createEntry(userID, null);
                Log.v(TAG, "chatRoster.createEntry................");
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            }
        }

        try {
            chatRoster.confirmSubscription(userID);
            Log.v(TAG, "chatRoster.confirmSubscription................");

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }

        QBPresence presence = chatRoster.getPresence(userID);

        Log.v(TAG, "userID...........=" + userID);
        Log.v(TAG, "QBPresence presence............=" + presence);

        if (presence == null) {
            Log.v(TAG, "presence.........=" + presence);
            // No user in your roster
            return;
        }

        if (presence.getType() == QBPresence.Type.online) {
            Log.v(TAG, "presence.getType().........=" + presence.getType());
            status = "Online";
            User user = new User();
            user.setFriend_id(userID);
            user.setStatus(status);
            DatabaseHelper.getInstance(LastSeenService.this).InsertStatus(user);

        } else {

            status = "Offline";
            status = Function.getCurrentDateTime();
            Log.v(TAG, "strDate :- " + status);

            User user = new User();

            user.setFriend_id(this.userID);
            user.setStatus(status);

            DatabaseHelper.getInstance(LastSeenService.this).InsertStatus(user);

        }
    }

}
