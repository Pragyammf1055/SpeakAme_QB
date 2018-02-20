package com.speakameqb.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.speakameqb.AppController;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    private Context cx;

    // constructor
    public JSONParser(Context cx) {
        this.cx = cx;
    }

    public static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, int met,
                                      List<NameValuePair> params) {

        // Making HTTP request
        try {
            String method;
            if (met == 1) {
                method = "POST";
            } else {
                method = "GET";
            }

            // check for request method
            if (method == "POST") {
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } else if (method == "GET") {
                System.out.println("3");
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                if (params != null) {
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                }
                System.out.println(url);
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            json = convertInputStreamToString(is);
            M.E(json.toString());
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;
    }

    public void parseVollyJsonArray(String url, int method, final JSONArray param, final VolleyCallback h) {

        M.log("Request", param.toString());

        if (method == 0 || method == 1) {

            JsonObjectRequest jsObjRequest = null;
            try {
                jsObjRequest = new JsonObjectRequest
                        (method, url, param.toString(), new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSONParseResponse: ", response.toString());
                                if (response != null) {
                                    h.backResponse(response.toString());

                                } else {
                                    M.log("", "Something went wrong.!");
                                }
                            }

                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String err = (error.getMessage() == null) ? "Parse Fail" : error.getMessage();
                                M.log("", "Something went wrong.!" + error);
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("status", "100");
                                    h.backResponse(jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }

            jsObjRequest.setShouldCache(true);
            // Adding request to request queue
            AppController.getInstance().
                    addToRequestQueue(jsObjRequest);
        } else {
            M.log("", "Invalid Request Method");
        }
    }

    public void parseVolleyStringRequest(String url, final VolleyCallback h) {

        Log.d("Response url", url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response", response);
                h.backResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("Response volley Error .................");
                h.backResponse("");
            }
        }) {

        };

        /*StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                h.backResponse(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", "null");
                h.backResponse("");
            }

        });*/

        AppController.getInstance().
                addToRequestQueue(stringRequest);
    }


}
