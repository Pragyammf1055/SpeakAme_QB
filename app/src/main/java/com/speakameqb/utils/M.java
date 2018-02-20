package com.speakameqb.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by yogesh on 24/5/2016.
 */
public class M {
    public static void I(Context cx, Class<?> startActivity) {
        Intent i = new Intent(cx, startActivity);
        cx.startActivity(i);
    }

    public static void E(String msg) {


    }

    public static void log(String tag, String msg) {

    }

    public static void dSimple(Context c, String title) {
        new SweetAlertDialog(c)
                .setTitleText(title)
                .show();
    }

    public static void dSimple(Context c, String title, String msg) {
        new SweetAlertDialog(c)
                .setTitleText(title)
                .setContentText(msg)
                .show();
    }

    public static void dError(Context c, String msg) {
        try {
            new SweetAlertDialog(c, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText(msg)
                    .show();
        } catch (Exception e) {
            E(e.getMessage());
        }
    }

    public static void dSuccess(Context c, String title, String msg) {
        new SweetAlertDialog(c, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(msg)
                .show();
    }

    public static void dSuccess(Context c, String msg, String btnTxt, SweetAlertDialog.OnSweetClickListener click) {
        new SweetAlertDialog(c, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText(msg)
                .setConfirmText(btnTxt)
                .setConfirmClickListener(click)
                .show();

    }


    public static void dFailuer(Context c, String msg, String btnTxt, SweetAlertDialog.OnSweetClickListener click) {
        new SweetAlertDialog(c, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(msg)
                .setConfirmText(btnTxt)
                .setConfirmClickListener(click)
                .show();

    }

    public static SweetAlertDialog dConfiremSuccess(Context c, String msg, String confirmText, String cancleText) {

        SweetAlertDialog s = new SweetAlertDialog(c, SweetAlertDialog.SUCCESS_TYPE);
        s.setTitleText("Congratulation.!");
        s.setContentText(msg);
        s.setConfirmText(confirmText);
        s.setCancelText(cancleText);
                /*s.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });*/
        s.show();
        return s;
    }

    public static SweetAlertDialog dConfirem(Context c, String msg, String confirmText, String cancleText) {

        SweetAlertDialog s = new SweetAlertDialog(c, SweetAlertDialog.WARNING_TYPE);
        s.setTitleText("Are you sure?");
        s.setContentText(msg);
        s.setConfirmText(confirmText);
        s.setCancelText(cancleText);
                /*s.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });*/
        s.show();
        return s;
    }

    public static void T(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }

    public static MaterialDialog initProgressDialog(Context c) {
        return new MaterialDialog.Builder(c)
                .title("Please wait...")
                .autoDismiss(false)
                .content("Wait for a moment.")
                .progress(true, 0)
                .show();
    }

    public static void share(Context c, String subject, String shareBody) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        c.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public static void openURL(Context cx, String url) {
        Uri uri = Uri
                .parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        cx.startActivity(intent);
    }

    public void openFolder(Context c, String folderName, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/" + folderName + "/");
        //mimeType "text/csc", "text/xlsx"
        intent.setDataAndType(uri, mimeType);
        c.startActivity(Intent.createChooser(intent, "Open folder"));
    }
}