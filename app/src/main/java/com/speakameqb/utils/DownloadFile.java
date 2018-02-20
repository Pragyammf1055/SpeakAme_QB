package com.speakameqb.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * Created by MAX on 15-Mar-17.
 */

public class DownloadFile extends AsyncTask<String, Integer, String> {

    private static final String TAG = "DownloadFile";
    VolleyCallback volleyCallback;
    ProgressBar mProgressDialog;
    ImageView cancelDownload;
    BufferedInputStream input;
    String filePath = "";
    Context context;
    //private final WeakReference<ImageView> imageViewReference;

    View.OnClickListener cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            downloadCancel();
            volleyCallback.backResponse("11");
        }
    };

    public DownloadFile(Context context, ImageView cancelDownload, ProgressBar mProgressDialog, VolleyCallback volleyCallback) {
        this.context = context;
        this.volleyCallback = volleyCallback;
        this.mProgressDialog = mProgressDialog;
        this.cancelDownload = cancelDownload;
        this.cancelDownload.setOnClickListener(cancel);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setProgress(0);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected String doInBackground(String... params) {
        return downloadFile(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(String bitmap) {
        mProgressDialog.setVisibility(View.GONE);
        if (bitmap == null) {
            mProgressDialog.setVisibility(View.GONE);
            volleyCallback.backResponse("11");
        } else {
            mProgressDialog.setVisibility(View.GONE);
            volleyCallback.backResponse(filePath);
        }
        /*ImageView imageView = imageViewReference.get();
        if (imageView != null) {

            if (bitmap != null) {

                imageView.setImageDrawable(Drawable.createFromPath(bitmap));
            } else {
                imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.uploadimage));

            }
        }*/
    }

    String downloadFile(String urls, String filename) {

        Log.v(TAG, "Urls :- " + urls);
        Log.v(TAG, "Filename :- " + filename);
        int count = 0;
        URL url;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            url = new URL(urls);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // connection.getInputStream();
            int lenghtOfFile = connection.getContentLength();
            System.out.println("down>> Content-length: " + lenghtOfFile);
            inputStream = new BufferedInputStream(url.openStream());

            String folderType;
            String filenema = Function.getFileName(filename);
            String fileExte = Function.getFileExtention(filename);

            if (fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) {
                folderType = "SpeakAme Image";
            } else if (fileExte.equalsIgnoreCase("mp4") || fileExte.equalsIgnoreCase("3gp") || fileExte.equalsIgnoreCase("MOV")) {
                folderType = "SpeakAme Video";
            } else if (fileExte.equalsIgnoreCase("pdf")) {
                folderType = "SpeakAme Document";
            } else {
                folderType = "SpeakAme Test";
            }

            File SpeakAmeDir = Function.createFolder(folderType);
            filePath = SpeakAmeDir + "/" + filenema;


            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

            outputStream = new FileOutputStream(filePath);

            byte data[] = new byte[512];
            long total = 0;

            while ((count = inputStream.read(data)) != -1) {
                total += count;
                //publishing progress update on UI thread.
                // Invokes onProgressUpdate()
                publishProgress((int) ((total * 100) / lenghtOfFile));
                Log.v(TAG, "down>> Content-length %: " + (int) ((total * 100) / lenghtOfFile));
                System.out.println("down>> Content-length %: " + (int) ((total * 100) / lenghtOfFile));
                // writing data to byte array stream
                outputStream.write(data, 0, count);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    String downloadBitmap(String urls, String filename) {
        // urls = "http:\\/\\/www.truckslogistics.com\\/Projects-Work\\/SpeakAme\\/user\\/UploadImage\\/109-52.png";
        String url1 = urls.replace("\\", "").replace("\"", "");

        Log.w("ImageDownloader", "isValidUrl " + url1 + "\n" + URLUtil.isValidUrl(url1) + Patterns.WEB_URL.matcher(url1).matches());

        int count;
        try {
            URL url = new URL("http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_10mb.mp4");
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();
            System.out.println("down>> Content-type: " + conection.getContentType());
            System.out.println("down>> Content-encoding: " + conection.getContentEncoding());
            System.out.println("down>> Date: " + new Date(conection.getDate()));
            System.out.println("down>> Last modified: " + new Date(conection.getLastModified()));
            System.out.println("down>> Expiration date: " + new Date(conection.getExpiration()));
            System.out.println("down>> Content-length: " + conection.getContentLength());
            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            String folderType;
            String filenema = Function.getFileName(filename);
            String fileExte = Function.getFileExtention(filename);

            if (fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) {
                folderType = "SpeakAme Image";
            } else if (fileExte.equalsIgnoreCase("mp4") || fileExte.equalsIgnoreCase("3gp")) {
                folderType = "SpeakAme Video";
            } else if (fileExte.equalsIgnoreCase("pdf")) {
                folderType = "SpeakAme Document";
            } else {
                folderType = "SpeakAme Test";
            }
            File SpeakAmeDir = Function.createFolder(folderType);
            filePath = SpeakAmeDir + "/" + filenema;

            BufferedInputStream bis = new BufferedInputStream(input);

            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            int total = 0;
            while ((current = bis.read()) != -1) {
                total = total + current;
                //int pValue = (total*100)/lenghtOfFile;
                int pValue = (current / total * 100);
                publishProgress(pValue);
                baf.append((byte) current);
            }

            // Output stream to write file
            FileOutputStream output = new FileOutputStream(filePath);
            output.write(baf.toByteArray());
            /*byte data[] = new byte[1024];

            long total = 0;
            Log.w("ImageDownloader", input.read(data)+"");
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                int pValue = (int)((total*100)/lenghtOfFile);

                publishProgress(pValue);
                Log.w("ImageDownloader", pValue+" % "+ total+":"+lenghtOfFile);
                // writing data to file
                output.write(data, 0, count);
            }*/

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
            return filePath;
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        /*URL url = null;
        try {
            url = new URL(url1);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
        input = new BufferedInputStream(url.openStream());
        try {
            String folderType;
            String filenema = Function.getFileName(filename);
            String fileExte = Function.getFileExtention(filename);

            if (fileExte.equalsIgnoreCase("png") || fileExte.equalsIgnoreCase("jpg") || fileExte.equalsIgnoreCase("jpeg")) {
                folderType = "image";
            } else if (fileExte.equalsIgnoreCase("mp4") || fileExte.equalsIgnoreCase("3gp")) {
                folderType = "video";
            } else if (fileExte.equalsIgnoreCase("pdf")) {
                folderType = "document";
            } else {
                folderType = "test";
            }
            File SpeakaMeDir = Function.createFolder(folderType,"recive");
            filePath = SpeakaMeDir +"/"+ filenema;

            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream (filePath));
            try {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                int total = 0;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                    total += bytesRead;
                    publishProgress((int)((total*100)/lenghtOfFile));
                   // publishProgress(""+(int)((total*100)/lenghtOfFile));
                }
            } finally {
                output.close();
            }

            return filePath;

        } finally {
            input.close();
        }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return null;
    }


    public void downloadCancel() {
        this.cancel(true);
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*if(entity != null){
            entity.cancelUploading();
        }

        if (httppost != null) {
            httppost.abort();
            httppost = null;
        }
        if (httpclient != null) {
            httpclient.getConnectionManager().shutdown();
        }
        httpclient = null;*/
    }
}
