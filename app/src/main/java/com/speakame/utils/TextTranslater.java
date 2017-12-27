package com.speakame.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.speakame.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by MAX on 27-Dec-16.
 */
public class TextTranslater {
    private static TextTranslater ourInstance = new TextTranslater();

    private TextTranslater() {

    }

    public static TextTranslater getInstance() {
        return ourInstance;
    }

    public void translate(final Context context, final String sortranslate, final String destranslate, final String text, final VolleyCallback translateResponse)  {
        final String[] translated = {""};

        String baseUrl = "https://translation.googleapis.com/language/translate/v2?";
        String key = "key=AIzaSyBTMyy_rZY_Uc0ohx7xSDOdE0nuJB2nYBM";//"AIzaSyDDsoz6NW5CJekFVAI34OjrjeEsYvrDoFw";
        String target = "&target="+destranslate;
        String query = "";
        try {
            query = "&q="+ URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = baseUrl+key+target+query;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response", response);
                if(response.equalsIgnoreCase("")){
                    translateResponse.backResponse(text);
                }else {
                    try {
                        String text = new JSONObject(response)
                                .getJSONObject("data")
                                .getJSONArray("translations")
                                .getJSONObject(0)
                                .getString("translatedText");
                        translateResponse.backResponse(text);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("Response volley Error .................");
                translateResponse.backResponse("");
            }
        }) {

        };

        AppController.getInstance().
                addToRequestQueue(stringRequest);

        /*JSONParser jsonParser = new JSONParser(context);
        jsonParser.parseVolleyStringRequest(url, new VolleyCallback() {
            @Override
            public void backResponse(String response) {
                if(response.equalsIgnoreCase("")){
                    translateResponse.backResponse(text);
                }else {
                    try {
                        String text = new JSONObject(response)
                                .getJSONObject("data")
                                .getJSONArray("translations")
                                .getJSONObject(0)
                                .getString("translatedText");
                        translateResponse.backResponse(text);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/

        /*new AsyncTask<Void, Integer, String>() {
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
        }.execute();*/

    }

}
