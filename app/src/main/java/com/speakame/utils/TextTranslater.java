package com.speakame.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.speakame.Activity.Languagelearn_activity;
import com.speakame.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

/**
 * Created by MAX on 27-Dec-16.
 */
public class TextTranslater {
    private static TextTranslater ourInstance = new TextTranslater();
    public static TextTranslater getInstance() {
        return ourInstance;
    }

    private TextTranslater() {

    }
    public void translate(Context context, final String sortranslate, final String destranslate, final String text, final VolleyCallback translateResponse)  {
        final String[] translated = {""};
       /* final ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progressbarbg));
        progressBar.setIndeterminate(false);
        progressBar.setSecondaryProgress(100);*/

        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String query = URLEncoder.encode(text, "UTF-8");
                    String langpair = URLEncoder.encode(sortranslate+"|"+destranslate, "UTF-8");
                    String url = "http://mymemory.translated.net/api/get?q="+query+"&langpair="+langpair;
                    Log.d("translate", url);
                    HttpClient hc = new DefaultHttpClient();
                    HttpGet hg = new HttpGet(url);
                    final HttpResponse hr = hc.execute(hg);
                    Log.d("translate", hr.getEntity().getContentLength()+"\n"+hr.getEntity().toString());
                   /* BufferedReader rd = new BufferedReader(new InputStreamReader(
                            hr.getEntity().getContent()));
                    long content = hr.getEntity().getContentLength();
//Do not need 'String line'
                    String line = "";
                    StringBuilder htmlBuilder = new StringBuilder();
                    long bytesRead = 0;
                    while ((line = rd.readLine()) != null) {
                        htmlBuilder.append(line);
                        bytesRead = bytesRead + line.getBytes().length + 2;
                        publishProgress(new Integer[]{(int) (((double) bytesRead / (double) content) * 100)});
                    }*/

                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            if (hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                                final JSONObject[] response = {null};

                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                        @Override
                                        public void run() {

                                            try {
                                                response[0] = new JSONObject(EntityUtils.toString(hr.getEntity()));

                                            if (response[0].getString("responseStatus").equalsIgnoreCase("200")) {
                                                String tt = response[0].getJSONObject("responseData").getString("translatedText");
                                                if(tt.equalsIgnoreCase(null)){
                                                    translateResponse.backResponse(text);
                                                }else{
                                                    translateResponse.backResponse(tt);
                                                }

                                            } else {
                                                translateResponse.backResponse(text);
                                            }
                                            //translated[0] = response[0].getJSONObject("responseData").getString("translatedText");

                                            }catch (NetworkOnMainThreadException e) {
                                                    e.printStackTrace();
                                                    translateResponse.backResponse(text);
                                            } catch (IllegalStateException e) {
                                                    e.printStackTrace();
                                                    translateResponse.backResponse(text);
                                            }  catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                            } else if (hr.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
                                translateResponse.backResponse(text);
                            } else {
                                translateResponse.backResponse(text);
                            }
                        }
                    });

                    return translated[0];
                }catch (NetworkOnMainThreadException e) {
                    e.printStackTrace();
                    translateResponse.backResponse(text);
                } catch (Exception e) {
                    e.printStackTrace();
                    translateResponse.backResponse(text);
                }

                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

               // progressBar.setVisibility(View.GONE);
                return ;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
             //   progressBar.setProgress(values[0]);
            }
        }.execute();

    }

}
