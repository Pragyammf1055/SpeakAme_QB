package com.speakameqb.utils;

/**
 * AppConstants to store the constant variables and common tags to be used
 * within the application
 */
public class AppConstants {

    public static final String PROPERTY_OCCUPANTS_IDS = "occupants_ids";
    public static final String PROPERTY_DIALOG_TYPE = "dialog_type";
    public static final String PROPERTY_DIALOG_NAME = "dialog_name";
    public static final String PROPERTY_NOTIFICATION_TYPE = "notification_type";
    public static final String CREATING_DIALOG = "creating_dialog";

    public static final int NETWORK_TIMEOUT_CONSTANT = 15000;
    public static final int NETWORK_CONNECTION_TIMEOUT_CONSTANT = 15000;
    public static final byte ROUTE_ACTIVITY_CONSTANT = (byte) 23;
    public static final int NETWORK_SOCKET_TIMEOUT_CONSTANT = 25000;

    public static final String INTENT_EXTRA_ALBUM = "album";
    public static final String INTENT_EXTRA_IMAGES = "images";
    public static final String INTENT_EXTRA_LIMIT = "limit";
    public static final int DEFAULT_LIMIT = 9;

    public static final int PERMISSION_REQUEST_CODE = 1000;
    public static final int PERMISSION_GRANTED = 1001;
    public static final int PERMISSION_DENIED = 1002;

    public static final int REQUEST_CODE = 2000;
    public static final int ALBUMSELECT_REQUEST_CODE = 4001;

    public static final int FETCH_STARTED = 2001;
    public static final int FETCH_COMPLETED = 2002;
    public static final int ERROR = 2005;
    //    public static final String APPURL = "http://truckslogistics.com/Projects-Work/SpeakAme/user/";
//        public static final String APPURL = "http://fxpips.co.uk/SpeakAme/user/";
//    public static final String APPURL = "http://www.speakameqb.com/SpeakAme/user/";
    public static final String APPURL = "http://www.speakame.com/SpeakAme/user/";
    public static final String EVERYONE = "Everyone";
    public static final String MYFRIENDS = "My Friends";
    public static final String NOBODY = "Nobody";
    public static final String TAG = "SPEAKAME";
    public static final String KEY_CONTACT = ",";
    public static final boolean IS_DEV_BUILD = true;
    public static final String KEY_CONTACT_SPLIT = ",";
    public static final String COMMONURL = APPURL + "genral_api.php";
    public static final String USERGROUPURL = APPURL + "user_group_api.php";
    public static final String LOGINURL = APPURL + "login.php";
    public static final String DEMOCOMMONURL = APPURL + "general_apis_speakMe.php";
    public static final String REGISTER_LOG = APPURL + "register_log.php";
    public static final String USER_CONNECTION_APIS = APPURL + "user_connection_apis.php";
    public static final String IOS_APIS = APPURL + "IOS_notify_background_api.php";
    public static final String XMPPURL = "http://35.165.126.230:9090/plugins/restapi/v1/users";
    public static final String XMPPURLGROUP = "http://35.165.126.230:9090/plugins/restapi/v1/chatrooms";
    public static final String CHANGELANGUAGE = "changeLanguage";
    public static final String PAYPAL_CLIENT_ID = "ARIIAHBdfznBpy_N2iV3xWmwMshyRAe4FyulDoXCjhw_c8yjL8YfqLdkMPH1UJiljS-a_TF9F081jbhf";
    public static final String GROUP_IMAGE_UPDATE = "group_image_update";
    public static final String GROUP_DELETE_GROUP = "delete_group";
    public static final String GROUP_EXIT_GROUP = "exit_group";
    public static final String GROUP_NAME_UPDATE = "group_name_update";
    public static final String USER_SIGNUP = "user_signup";
    public static final String OTP = "otp";
    public static final String LOGIN = "login";
    public static final String CHECKLIST = "checkList";
    public static final String GETCHECKLIST = "getCheckList";
    public static final String CREATEGROUP = "createGroup";
    public static final String USERMAKEGROUPDETAIL = "userMakeGroupDetail";
    public static final String FB_OTP = "fb_otp";
    public static final String CHECK_NUMBER = "checkNumber";
    public static final String NOTIFICATION_IOS = "notification_ios";
    public static int limit;

}
