package com.speakame.Xmpp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import com.speakame.Beans.User;
import com.speakame.Database.DatabaseHelper;
import com.speakame.utils.AppPreferences;
import org.jivesoftware.smack.chat.Chat;

/**
 * Created by MAX on 21-Sep-16.
 */
public class MyService extends Service {
   // public static final String DOMAIN = "104.238.72.61";
    public static final String DOMAIN = "35.165.126.230";

    public static ConnectivityManager cm;
    public static MyXMPP xmpp;
    public static boolean ServerchatCreated = false;
    public Chat chat;
    String text = "";

    public static boolean isNetworkConnected() {
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new LocalBinder<MyService>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        User user = DatabaseHelper.getInstance(MyService.this).getUser(AppPreferences.getMobileuser(MyService.this));

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        xmpp = MyXMPP.getInstance(MyService.this, DOMAIN, user.getMobile(), user.getMobile());
        Log.d("checkmobilepaas1", user.getMobile()+">>>>>"+user.getMobile());
        Log.d("xmppconnection", "MyService");
        xmpp.connect("onCreate");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // xmpp.connection.disconnect();
    }

}
