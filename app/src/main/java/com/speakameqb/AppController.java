package com.speakameqb;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.auth.session.QBSessionParameters;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.LogLevel;
import com.quickblox.core.ServiceZone;
import com.quickblox.core.StoringMechanism;
import com.quickblox.messages.services.QBPushManager;
import com.speakameqb.QuickBlox.QBResRequestExecutor;
import com.speakameqb.Recivers.NetworkStateChecker;
import com.speakameqb.utils.AppPreferences;


public class AppController extends MultiDexApplication {

    public static final String TAG = "AppController";
    static final String APP_ID = "63489";
    static final String AUTH_KEY = "c86BerVmF9Cehe-";
    static final String AUTH_SECRET = "bDVLyTkttPnCzju";
    static final String ACCOUNT_KEY = "dX-fDSZAx81rLHJL6ZAy";
    static final String API_DOMAIN = "https://api.quickblox.com";
    static final String CHAT_DOMAIN = "chat.quickblox.com";


    private static final int REQUEST_READ_PHONE_STATE = 100;
    public static int privateMessageCount = 0;
    private static AppController mInstance;
    private static AppController instance;
    public boolean isInForeground = false;
    private RequestQueue mRequestQueue;
    private QBResRequestExecutor qbResRequestExecutor;

    public static AppController getAppInstance() {
        return mInstance;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MultiDex.install(this);
        new AppPreferences(this);
        QBSettings qbSettings = QBSettings.getInstance();

        qbSettings.init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        qbSettings.setLogLevel(LogLevel.DEBUG);
        qbSettings.setStoringMehanism(StoringMechanism.SECURED);
        qbSettings.setAccountKey(ACCOUNT_KEY);
        qbSettings.setAutoCreateSession(true);
        qbSettings.setEnablePushNotification(true);
        qbSettings.setEndpoints(API_DOMAIN, CHAT_DOMAIN, ServiceZone.DEVELOPMENT);
        qbSettings.setZone(ServiceZone.DEVELOPMENT);

        QBChatService.setDebugEnabled(true);
//        qbSettings.setLogLevel(LogLevel.DEBUG); 
        initQBPushNotificationSubscription();
//        initQBSessionManager();
        initApplication();
    }

    private void initQBPushNotificationSubscription() {

        QBPushManager.getInstance().addListener(new QBPushManager.QBSubscribeListener() {
            @Override
            public void onSubscriptionCreated() {
                Log.d(TAG, "ppp onSubscriptionCreated");
            }

            @Override
            public void onSubscriptionError(final Exception e, int resultCode) {
                Log.d(TAG, "ppp onSubscriptionError :- " + e);
                if (resultCode >= 0) {
                    Log.d(TAG, "Google play service exception :- " + resultCode);
                }
                Log.d(TAG, "ppp onSubscriptionError:- " + e.getMessage());
            }

            @Override
            public void onSubscriptionDeleted(boolean b) {

            }
        });

    }

    private void initApplication() {
        instance = this;
    }

    public synchronized QBResRequestExecutor getQbResRequestExecutor() {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor()
                : qbResRequestExecutor;
    }

    private void initQBSessionManager() {

        QBSessionManager.getInstance().addListener(new QBSessionManager.QBSessionListener() {
            @Override
            public void onSessionCreated(QBSession qbSession) {
                Log.d(TAG, "Session Created");
            }

            @Override
            public void onSessionUpdated(QBSessionParameters qbSessionParameters) {
                Log.d(TAG, "Session Updated");
            }

            @Override
            public void onSessionDeleted() {
                Log.d(TAG, "Session Deleted");
            }

            @Override
            public void onSessionRestored(QBSession qbSession) {
                Log.d(TAG, "Session Restored");
            }

            @Override
            public void onSessionExpired() {
                Log.d(TAG, "Session Expired");
            }
        });
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "App Just onTerminate !!");
    }

    public void setConnectionListener(NetworkStateChecker.ConnectionReceiverListener listener) {
        NetworkStateChecker.connectionReceiverListener = listener;
    }
}
