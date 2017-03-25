package com.speakame.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {
    public static final String MBPREFERENCES = "SpeakAme";
    public static final String UID = "user_id";
    public static final String DEVICEID = "deviceId";
    public static final String USERSTATUS = "status";
    public static final String USERPROFILEPIC = "pro_pic";
    public static final String USERCITY = "user_name";
    public static final String USERBLOCKNUMBER = "block_user";
    public static final String USERPROFILEREVIEW = "pro_view";
    public static final String USERPRODUCTITEMS = "pro_items";
    public static final String USERADDRESS = "user_add";
    public static final String USERGENDER = "user_gender";
    public static final String USERFAVORITECATEGORY = "user_fav_category";
    public static final String PICPRIVACY = "pic_text";
    public static final String STATUSPRIVACY = "status_text";
    public static final String USERLANGUAGE = "company_name";
    public static final String ENTERSEND = "enter_send";
    public static final String NOTIFICATIONFAVOURITES = "Notification_favourites";
    public static final String NOTIFICATION_RINGTONE_URI = "notification_ringtone_uri";
    /* Verification email id*/
    public static final String NAME = "user_name";

    public static final String VERIFYWEB = "verify_web";
    public static final String VERIFYMOBILE = "verify_mobile";
    public static final String VERIFYFB = "verify_fb";
    public static final String VERIFYGPLUS = "verify_gplus";
    public static final String MOBILENUMBERUSER = "mobilenumber";
    public static final String FNAME = "fname";
    public static final String TOTF = "totf";
    public static final String CountryCode = "country_code";
    public static final String EMAIL = "email";
    public static final String MOBILENO = "mobile_num";
    public static final String PASSWORD = "password";
    public static final String USERPROFILE = "user_profile";
    public static final String LOGINID = "login_id";
    public static final String SOCIALID = "social_id";
    public static final String VERYFYE = "vryfy_id";
    public static final String ACKNOWLEDGE = "acknow_value";
    public static final String CONVERSATIONTONE = "convert_tone";
    public static final String NOTIFICATION_RINGTONE = "notification_ringtone";
    public static final String VIBRATION_TYPE = "vibration_type";
    public static final String STATUSUSER = "user_status";
    public static final String REGISTERDATE = "reg_date";
    public static final String REGISTERENDDATE = "end_status";
    public static final String REMAININGDAYS = "remain_day";
    public static final String VIBRATION_INDEX = "vibration_index";
    public static final String LAST_SEEN_TYPE = "last_seen_type";
    public static final String STATUS_INDEX = "status_index";
    public static final String PROFILE_PIC_INDEX = "profile_pic_index";
    public static final String LAST_SEEN_INDEX = "last_seen_index";


    private static AppPreferences instance;

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public AppPreferences(Context context) {
        instance = this;
        String prefsFile = context.getPackageName();
        sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public static String getConvertTone(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(CONVERSATIONTONE, "");
    }

    public static void setConvertTone(Context context, String enterflag) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(CONVERSATIONTONE, enterflag);
        editor.commit();
    }

    public static String getMobileuser(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(MOBILENUMBERUSER, "");
    }

    public static void setMobileuser(Context context, String Date) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(MOBILENUMBERUSER, Date);
        editor.commit();
    }


    public static String getCountrycode(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(CountryCode, "");
    }

    public static void setCountrycode(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(CountryCode, id);
        editor.commit();
    }

    public static String getUserprofile(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERPROFILE, "");
    }

    public static void setUserprofile(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERPROFILE, id);
        editor.commit();
    }


    public static String getFirstUsername(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(FNAME, "");
    }

    public static void setFirstUsername(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(FNAME, id);
        editor.commit();
    }

    public static String getTotf(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(TOTF, "1");
    }

    public static void setTotf(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(TOTF, id);
        editor.commit();
    }

    public static String getEmail(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(EMAIL, "");
    }

    public static void setEmail(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(EMAIL, id);
        editor.commit();
    }


    public static void setPassword(Context context,
                                   String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(PASSWORD, id);
        editor.commit();
    }

    public static String getPassword(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(PASSWORD, "");
    }


    public static boolean getVerifymobile(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getBoolean(VERIFYMOBILE, false);
    }

    public static void setVerifymobile(Context context, boolean Verifymobile) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putBoolean(VERIFYMOBILE, Verifymobile);
        editor.commit();
    }


    public static boolean getVerifyweb(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getBoolean(VERIFYWEB, false);
    }

    public static void setVerifyweb(Context context, boolean Verifyweb) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putBoolean(VERIFYWEB, Verifyweb);
        editor.commit();
    }

    public static boolean getVerifyfb(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getBoolean(VERIFYFB, false);
    }

    public static void setVerifyfb(Context context, boolean Verifyfb) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putBoolean(VERIFYFB, Verifyfb);
        editor.commit();
    }

    public static boolean getVerifygplus(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getBoolean(VERIFYGPLUS, false);
    }

    public static void setVerifygplus(Context context, boolean Verifygplus) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putBoolean(VERIFYGPLUS, Verifygplus);
        editor.commit();
    }


    public static String getId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(UID, "");
    }

    public static void setId(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(UID, id);
        editor.commit();
    }

    public static String getUserprofilereview(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERPROFILEREVIEW, "");
    }

    public static void setUserprofilereview(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERPROFILEREVIEW, id);
        editor.commit();
    }


    public static String getUserproductitems(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERPRODUCTITEMS, "");
    }

    public static void setUserproductitems(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERPRODUCTITEMS, id);
        editor.commit();
    }


    public static String getUserstatus(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERSTATUS, "");
    }

    public static void setUserstatus(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERSTATUS, id);
        editor.commit();
    }

    public static String getUSERLANGUAGE(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERLANGUAGE, "");
    }

    public static void setUSERLANGUAGE(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERLANGUAGE, id);
        editor.commit();
    }

    public static String getUseraddress(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERADDRESS, "");
    }

    public static void setUseraddress(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERADDRESS, id);
        editor.commit();
    }


    public static String getUserprofilepic(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERPROFILEPIC, "");
    }

    public static void setUserprofilepic(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERPROFILEPIC, id);
        editor.commit();
    }

    public static String getUsercity(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERCITY, "");
    }

    public static void setUsercity(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERCITY, id);
        editor.commit();
    }




    public static String getBlockList(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERBLOCKNUMBER, "");
    }

    public static void setBlockList(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERBLOCKNUMBER, id);
        editor.commit();
    }


    public static void setLoginId(Context context,
                                  int id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putInt(LOGINID, id);
        editor.commit();
    }

    public static int getLoginId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getInt(LOGINID, 0);
    }


    public static String getPicprivacy(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(PICPRIVACY, "");
    }

    public static void setPicprivacy(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(PICPRIVACY, id);
        editor.commit();
    }

    public static String getStatusprivacy(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(STATUSPRIVACY, "");
    }

    public static void setStatusprivacy(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(STATUSPRIVACY, id);
        editor.commit();
    }


    public static String getSocialId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(SOCIALID, "");
    }

    public static void setSocialId(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(SOCIALID, id);
        editor.commit();
    }


    public static String getUsergender(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERGENDER, "");
    }

    public static void setUsergender(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERGENDER, id);
        editor.commit();
    }

    public static String getUserfavoritecategory(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(USERFAVORITECATEGORY, "");
    }

    public static void setUserfavoritecategory(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(USERFAVORITECATEGORY, id);
        editor.commit();
    }


    public static String getAckwnoledge(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(ACKNOWLEDGE, "");
    }

    public static void setAckwnoledge(Context context, String deviceId) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(ACKNOWLEDGE, deviceId);
        editor.commit();
    }


    public static String getEnetrSend(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(ENTERSEND, "");
    }

    public static void setEnetrSend(Context context, String enterflag) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(ENTERSEND, enterflag);
        editor.commit();
    }

    public static boolean getNotificationFavourites(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getBoolean(NOTIFICATIONFAVOURITES, true);
    }

    public static void setNotificationFavourites(Context context, boolean reviewFlag) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putBoolean(NOTIFICATIONFAVOURITES, reviewFlag);
        editor.commit();
    }


    public static void setVerifytype(Context context,
                                     int id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putInt(VERYFYE, id);
        editor.commit();
    }

    public static int getVerifytype(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getInt(VERYFYE, 0);
    }

    public static String getVibrationType(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(VIBRATION_TYPE, "");
    }

    public static void setVibrationType(Context context, String vibrationType) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(VIBRATION_TYPE, vibrationType);
        editor.commit();
    }

    public static String getNotificationRingtone(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(NOTIFICATION_RINGTONE, "");
    }


    public static String getLoginStatus(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(STATUSUSER, "");
    }

    public static void setLoginStatus(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(STATUSUSER, id);
        editor.commit();
    }


    public static String getRegisterDate(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(REGISTERDATE, "");
    }

    public static void setRegisterDate(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(REGISTERDATE, id);
        editor.commit();
    }

    public static String getRegisterEndDate(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(REGISTERENDDATE, "");
    }

    public static void setRegisterEndDate(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(REGISTERENDDATE, id);
        editor.commit();
    }

    public static String getRemainingDays(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(REMAININGDAYS, "");
    }

    public static void setRemainingDays(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(REMAININGDAYS, id);
        editor.commit();
    }




    public static int getStatusIndex(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getInt(STATUS_INDEX, 0);
    }

    public static void setStatusIndex(Context context, int statusIndex) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putInt(STATUS_INDEX, statusIndex);
        editor.commit();
    }

    public static int getProfilePicIndex(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getInt(PROFILE_PIC_INDEX, 0);
    }

    public static void setProfilePicIndex(Context context, int profilePicIndex) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putInt(PROFILE_PIC_INDEX, profilePicIndex);
        editor.commit();
    }


    public static int getLastSeenIndex(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getInt(LAST_SEEN_INDEX, 0);
    }

    public static void setLastSeenIndex(Context context, int lastSeenIndex) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putInt(LAST_SEEN_INDEX, lastSeenIndex);
        editor.commit();
    }

    public static String getLastSeenType(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(LAST_SEEN_TYPE, "");
    }

    public static void setLastSeenType(Context context, String lastSeenType) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(LAST_SEEN_TYPE, lastSeenType);
        editor.commit();
    }

    public static int getVibrationIndex(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getInt(VIBRATION_INDEX, 0);
    }

    public static void setVibrationIndex(Context context, int vibrationIndex) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putInt(VIBRATION_INDEX, vibrationIndex);
        editor.commit();
    }

    public static void setNotificationRingtone(Context context, String ringtoneName, String ringtoneUri) {
        SharedPreferences preferences = context.getSharedPreferences(
                MBPREFERENCES, 0);
        Editor editor = preferences.edit();
        editor.putString(NOTIFICATION_RINGTONE, ringtoneName);
        editor.putString(NOTIFICATION_RINGTONE_URI, ringtoneUri);
        editor.commit();
    }
    public static String getNotificationRingtoneUri(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(
                MBPREFERENCES, 0);
        return pereference.getString(NOTIFICATION_RINGTONE_URI, "");
    }

}
