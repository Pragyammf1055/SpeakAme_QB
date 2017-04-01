package com.speakame.utils;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MAX on 11/9/2015.
 */
public class Function {

    public static final TreeSet<String> CANADA_CODES = new TreeSet<String>();
    public static final TreeSet<String> US_CODES = new TreeSet<String>();
    public static final TreeSet<String> DO_CODES = new TreeSet<String>();
    public static final TreeSet<String> PR_CODES = new TreeSet<String>();

    public static JSONArray alContactsname = new JSONArray();
    public static JSONArray alContactsnumber = new JSONArray();

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

    }

    public static boolean CheckGpsEnableOrNot(Context context) {
        boolean gpsStatus = false;
        try {
            LocationManager locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            gpsStatus = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
            gpsStatus = false;
        }
        return gpsStatus;
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;
    }

    public static boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null && ni.isConnected())
            return true;
        else
            return false;
    }

    public static boolean isServiceRunning(Context context, String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }

    public static String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    public static String getAddressFromLatlng(Context context, double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                Log.d("Addresss", address.toString());

                result.append(address.getAddressLine(0) + " " + address.getAddressLine(1) + " " +
                        address.getAddressLine(2) + " " + address.getAddressLine(3));
                /*result.append(address.getLocality()).append("\n");
                result.append(address.getCountryName());
                result.append(address.getCountryName());
                result.append(address.getCountryName());*/
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    public static String datetime() {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String currentDateTimeString = dateFormatter.format(today);
        Log.d("currentdatetime", currentDateTimeString);
        return currentDateTimeString;

    }

    public static void callPermisstion(Activity activity, int request) {

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CALL_PHONE)) {

// Show an expanation to the user *asynchronously* -- don't block
// this thread waiting for the user's response! After the user
// sees the explanation, try again to request the permission.

            } else {

// No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CALL_PHONE},
                        request);

// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
// app-defined int constant. The callback method gets the
// result of the request.
            }
        }
    }

    public static void cameraPermisstion(Activity activity, int request) {


        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {


// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {

// Show an expanation to the user *asynchronously* -- don't block
// this thread waiting for the user's response! After the user
// sees the explanation, try again to request the permission.

            } else {

// No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA},
                        request);

// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
// app-defined int constant. The callback method gets the
// result of the request.
            }
        }
    }


    public static void contactPermisstion(Activity activity, int request) {


        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {


// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_CONTACTS)) {

// Show an expanation to the user *asynchronously* -- don't block
// this thread waiting for the user's response! After the user
// sees the explanation, try again to request the permission.

            } else {

// No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        request);

// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
// app-defined int constant. The callback method gets the
// result of the request.
            }
        }
    }


    public static void readphonestatePermisstion(Activity activity, int request) {


        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {


// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_PHONE_STATE)) {

// Show an expanation to the user *asynchronously* -- don't block
// this thread waiting for the user's response! After the user
// sees the explanation, try again to request the permission.

            } else {

// No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        request);

// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
// app-defined int constant. The callback method gets the
// result of the request.
            }
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

   /* public void dialPhoneNumber(Context context, String number) {
        String uri = "tel:" + number.trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        context.startActivity(intent);
    }*/

    public static void importcontact(Context context) {


        /////////getiingallcontact in list///////////


        ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        // jsonContacts = new JSONArray();
        if (cursor.moveToFirst()) {

            //jsonContacts = new JSONObject();
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String contactname = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        alContactsnumber.put(contactNumber);
                        if (TextUtils.isEmpty(contactname)) {
                            alContactsname.put("");
                        } else {
                            alContactsname.put(contactname);
                        }

                        break;

                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
        }
        ///////endcontactimport////////////////////////
    }

    public static File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/SpeakaMe/Image/");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MS_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public static String encodeFileToBase64Binary(String fileName)
            throws IOException {

        File file = new File(fileName);
        byte[] bytes = loadFile(file);
        byte[] encoded = Base64.encodeBase64(bytes);
        String encodedString = new String(encoded);

        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    public static File decodeBase64BinaryToFile(String path, String fileName, String contents)
            throws IOException {
        File file = saveToFile(path + "/" + fileName, base64ToBytes(contents));
        return file;
    }

    public static byte[] base64ToBytes(String data) {
        return android.util.Base64.decode(data, android.util.Base64.DEFAULT);
    }
    public static byte[] bitmapToBytes(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static File saveToFile(String filePath, byte[] contents) throws IOException {
        File newFile = new File(filePath);
        OutputStream os = new FileOutputStream(newFile);
        os.write(contents);
        os.close();
        return newFile;
    }

    public static String getAndroidID(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static String loadJSONFromAsset(Context context, String assetName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(assetName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public Address getLatLongFromGivenAddress(String addresses, Context context) {
        double lat = 0.0, lng = 0.0;

        Geocoder coder = new Geocoder(context);
        List<Address> address = new ArrayList<Address>();

        try {
            address = coder.getFromLocationName(addresses, 5);
            if (address == null) {
                return null;
            }
            System.out.println("adress----------------" + address);

            Address location = address.get(0);
            lat = location.getLatitude();
            lng = location.getLongitude();
            return location;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public float distancebetweenTwoLatLong(double latA, double lngA, double latB, double lngB) {
        Location locationA = new Location("point A");

        locationA.setLatitude(latA);
        locationA.setLongitude(lngA);

        Location locationB = new Location("point B");

        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);
        float distance = locationA.distanceTo(locationB);
        return distance;
    }

    public int calculateZoomLevel(int screenWidth, float distance) {
        double equatorLength = 40075004; // in meters
        double widthInPixels = screenWidth;
        double metersPerPixel = equatorLength / 256;
        int zoomLevel = 1;
        while ((metersPerPixel * widthInPixels) > distance) {
            metersPerPixel /= 2;
            ++zoomLevel;
        }
        Log.i("ADNAN", "zoom level = " + zoomLevel);
        return zoomLevel;
    }

    public void showGpsSettingsAlert(String tittle, String message, final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(tittle);

        // Setting Dialog Message
        alertDialog.setMessage(message);   // "GPS is not enabled. Do you want to go to settings menu ?"

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
//						MainActivity.mainactivityinstance.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    public static byte[] fileToByte(String path){
        File file = new File(path);

        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            for (int i = 0; i < b.length; i++) {
                System.out.print((char)b[i]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("byteArray File Not Found.");
            e.printStackTrace();
        }
        catch (IOException e1) {
            System.out.println("byteArray Error Reading The File.");
            e1.printStackTrace();
        }
        return b;
    }

    public static Bitmap getBitmapFromByte(byte[] byteArray){
        System.out.println("byteArray ."+byteArray);
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static String getFileName(String urlStr){
        return urlStr.substring(urlStr.lastIndexOf('/')+1, urlStr.length());
    }

    public static String getFileExtention(String filename){
        return filename.substring(filename.lastIndexOf(".")+1);
    }

    public static File createFolder(String folder, String type){
        File SpeakaMe = Environment.getExternalStorageDirectory();
        File SpeakaMeDirectory = new File(SpeakaMe + "/SpeakaMe/" + folder + "/"+ type);
        if (!SpeakaMeDirectory.exists()) {
            SpeakaMeDirectory.mkdirs();
        }
        return SpeakaMeDirectory;
    }

    public static String generateNewFileName(String fileExte){
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmssSSS").format(new Date());

        String s = "MS_" + timeStamp + "."+fileExte;
        return s;
    }

    public static String copyFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag :", fnfe1.getMessage());
            fnfe1.printStackTrace();
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
            e.printStackTrace();
        }
        return outputPath;
    }
    public static int getFileSize(String selectedPath){
        File file = new File(selectedPath);
        return Integer.parseInt(String.valueOf(file.length()/1024));
    }
    public static Bitmap getBitmap(String filePath){
        File imgFile = new  File(filePath);

        if(imgFile.exists()){

            return  BitmapFactory.decodeFile(imgFile.getAbsolutePath());


        }else {

            return null;
        }
    }
    static {
        //USA
        US_CODES.add("201");
        US_CODES.add("202");
        US_CODES.add("203");
        US_CODES.add("205");
        US_CODES.add("206");
        US_CODES.add("207");
        US_CODES.add("208");
        US_CODES.add("209");
        US_CODES.add("210");
        US_CODES.add("212");
        US_CODES.add("213");
        US_CODES.add("214");
        US_CODES.add("215");
        US_CODES.add("216");
        US_CODES.add("217");
        US_CODES.add("218");
        US_CODES.add("219");
        US_CODES.add("224");
        US_CODES.add("225");
        US_CODES.add("228");
        US_CODES.add("229");
        US_CODES.add("231");
        US_CODES.add("234");
        US_CODES.add("239");
        US_CODES.add("240");
        US_CODES.add("248");
        US_CODES.add("251");
        US_CODES.add("252");
        US_CODES.add("253");
        US_CODES.add("254");
        US_CODES.add("256");
        US_CODES.add("260");
        US_CODES.add("262");
        US_CODES.add("267");
        US_CODES.add("269");
        US_CODES.add("270");
        US_CODES.add("276");
        US_CODES.add("281");
        US_CODES.add("301");
        US_CODES.add("302");
        US_CODES.add("303");
        US_CODES.add("304");
        US_CODES.add("305");
        US_CODES.add("307");
        US_CODES.add("308");
        US_CODES.add("309");
        US_CODES.add("310");
        US_CODES.add("312");
        US_CODES.add("313");
        US_CODES.add("314");
        US_CODES.add("315");
        US_CODES.add("316");
        US_CODES.add("317");
        US_CODES.add("318");
        US_CODES.add("319");
        US_CODES.add("320");
        US_CODES.add("321");
        US_CODES.add("323");
        US_CODES.add("325");
        US_CODES.add("330");
        US_CODES.add("334");
        US_CODES.add("336");
        US_CODES.add("337");
        US_CODES.add("339");
        US_CODES.add("347");
        US_CODES.add("351");
        US_CODES.add("352");
        US_CODES.add("360");
        US_CODES.add("361");
        US_CODES.add("386");
        US_CODES.add("401");
        US_CODES.add("402");
        US_CODES.add("404");
        US_CODES.add("405");
        US_CODES.add("406");
        US_CODES.add("407");
        US_CODES.add("408");
        US_CODES.add("409");
        US_CODES.add("410");
        US_CODES.add("412");
        US_CODES.add("413");
        US_CODES.add("414");
        US_CODES.add("415");
        US_CODES.add("417");
        US_CODES.add("419");
        US_CODES.add("423");
        US_CODES.add("425");
        US_CODES.add("430");
        US_CODES.add("432");
        US_CODES.add("434");
        US_CODES.add("435");
        US_CODES.add("440");
        US_CODES.add("443");
        US_CODES.add("469");
        US_CODES.add("478");
        US_CODES.add("479");
        US_CODES.add("480");
        US_CODES.add("484");
        US_CODES.add("501");
        US_CODES.add("502");
        US_CODES.add("503");
        US_CODES.add("504");
        US_CODES.add("505");
        US_CODES.add("507");
        US_CODES.add("508");
        US_CODES.add("509");
        US_CODES.add("510");
        US_CODES.add("512");
        US_CODES.add("513");
        US_CODES.add("515");
        US_CODES.add("516");
        US_CODES.add("517");
        US_CODES.add("518");
        US_CODES.add("520");
        US_CODES.add("530");
        US_CODES.add("540");
        US_CODES.add("541");
        US_CODES.add("551");
        US_CODES.add("559");
        US_CODES.add("561");
        US_CODES.add("562");
        US_CODES.add("563");
        US_CODES.add("567");
        US_CODES.add("570");
        US_CODES.add("571");
        US_CODES.add("573");
        US_CODES.add("574");
        US_CODES.add("575");
        US_CODES.add("580");
        US_CODES.add("585");
        US_CODES.add("586");
        US_CODES.add("601");
        US_CODES.add("602");
        US_CODES.add("603");
        US_CODES.add("605");
        US_CODES.add("606");
        US_CODES.add("607");
        US_CODES.add("608");
        US_CODES.add("609");
        US_CODES.add("610");
        US_CODES.add("612");
        US_CODES.add("614");
        US_CODES.add("615");
        US_CODES.add("616");
        US_CODES.add("617");
        US_CODES.add("618");
        US_CODES.add("619");
        US_CODES.add("620");
        US_CODES.add("623");
        US_CODES.add("626");
        US_CODES.add("630");
        US_CODES.add("631");
        US_CODES.add("636");
        US_CODES.add("641");
        US_CODES.add("646");
        US_CODES.add("650");
        US_CODES.add("651");
        US_CODES.add("660");
        US_CODES.add("661");
        US_CODES.add("662");
        US_CODES.add("678");
        US_CODES.add("682");
        US_CODES.add("701");
        US_CODES.add("702");
        US_CODES.add("703");
        US_CODES.add("704");
        US_CODES.add("706");
        US_CODES.add("707");
        US_CODES.add("708");
        US_CODES.add("712");
        US_CODES.add("713");
        US_CODES.add("714");
        US_CODES.add("715");
        US_CODES.add("716");
        US_CODES.add("717");
        US_CODES.add("718");
        US_CODES.add("719");
        US_CODES.add("720");
        US_CODES.add("724");
        US_CODES.add("727");
        US_CODES.add("731");
        US_CODES.add("732");
        US_CODES.add("734");
        US_CODES.add("740");
        US_CODES.add("754");
        US_CODES.add("757");
        US_CODES.add("760");
        US_CODES.add("763");
        US_CODES.add("765");
        US_CODES.add("770");
        US_CODES.add("772");
        US_CODES.add("773");
        US_CODES.add("774");
        US_CODES.add("775");
        US_CODES.add("781");
        US_CODES.add("785");
        US_CODES.add("786");
        US_CODES.add("801");
        US_CODES.add("802");
        US_CODES.add("803");
        US_CODES.add("804");
        US_CODES.add("805");
        US_CODES.add("806");
        US_CODES.add("808");
        US_CODES.add("810");
        US_CODES.add("812");
        US_CODES.add("813");
        US_CODES.add("814");
        US_CODES.add("815");
        US_CODES.add("816");
        US_CODES.add("817");
        US_CODES.add("818");
        US_CODES.add("828");
        US_CODES.add("830");
        US_CODES.add("831");
        US_CODES.add("832");
        US_CODES.add("843");
        US_CODES.add("845");
        US_CODES.add("847");
        US_CODES.add("848");
        US_CODES.add("850");
        US_CODES.add("856");
        US_CODES.add("857");
        US_CODES.add("858");
        US_CODES.add("859");
        US_CODES.add("860");
        US_CODES.add("862");
        US_CODES.add("863");
        US_CODES.add("864");
        US_CODES.add("865");
        US_CODES.add("866");
        US_CODES.add("870");
        US_CODES.add("901");
        US_CODES.add("903");
        US_CODES.add("904");
        US_CODES.add("906");
        US_CODES.add("907");
        US_CODES.add("908");
        US_CODES.add("909");
        US_CODES.add("910");
        US_CODES.add("912");
        US_CODES.add("913");
        US_CODES.add("914");
        US_CODES.add("915");
        US_CODES.add("916");
        US_CODES.add("917");
        US_CODES.add("918");
        US_CODES.add("919");
        US_CODES.add("920");
        US_CODES.add("925");
        US_CODES.add("928");
        US_CODES.add("931");
        US_CODES.add("936");
        US_CODES.add("937");
        US_CODES.add("940");
        US_CODES.add("941");
        US_CODES.add("947");
        US_CODES.add("949");
        US_CODES.add("951");
        US_CODES.add("952");
        US_CODES.add("954");
        US_CODES.add("956");
        US_CODES.add("970");
        US_CODES.add("971");
        US_CODES.add("972");
        US_CODES.add("973");
        US_CODES.add("978");
        US_CODES.add("979");
        US_CODES.add("980");
        US_CODES.add("985");
        US_CODES.add("989");

        //Dominican Republic
        DO_CODES.add("809");
        DO_CODES.add("829");
        DO_CODES.add("849");

        //Puerto Rico
        PR_CODES.add("787");
        PR_CODES.add("939");

        //Canada
        CANADA_CODES.add("204");
        CANADA_CODES.add("226");
        CANADA_CODES.add("236");
        CANADA_CODES.add("249");
        CANADA_CODES.add("250");
        CANADA_CODES.add("289");
        CANADA_CODES.add("306");
        CANADA_CODES.add("343");
        CANADA_CODES.add("365");
        CANADA_CODES.add("387");
        CANADA_CODES.add("403");
        CANADA_CODES.add("416");
        CANADA_CODES.add("418");
        CANADA_CODES.add("431");
        CANADA_CODES.add("437");
        CANADA_CODES.add("438");
        CANADA_CODES.add("450");
        CANADA_CODES.add("506");
        CANADA_CODES.add("514");
        CANADA_CODES.add("519");
        CANADA_CODES.add("548");
        CANADA_CODES.add("579");
        CANADA_CODES.add("581");
        CANADA_CODES.add("587");
        CANADA_CODES.add("604");
        CANADA_CODES.add("613");
        CANADA_CODES.add("639");
        CANADA_CODES.add("647");
        CANADA_CODES.add("672");
        CANADA_CODES.add("705");
        CANADA_CODES.add("709");
        CANADA_CODES.add("742");
        CANADA_CODES.add("778");
        CANADA_CODES.add("780");
        CANADA_CODES.add("782");
        CANADA_CODES.add("807");
        CANADA_CODES.add("819");
        CANADA_CODES.add("825");
        CANADA_CODES.add("867");
        CANADA_CODES.add("873");
        CANADA_CODES.add("902");
        CANADA_CODES.add("905");
    }


}
