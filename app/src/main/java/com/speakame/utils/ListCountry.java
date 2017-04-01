package com.speakame.utils;

import android.content.Context;
import android.util.Log;

import com.speakame.Activity.CountryListActivity;
import com.speakame.Beans.AllBeans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by MAX on 24-Mar-17.
 */

public class ListCountry {

    public static String[] getAllCointries() {

        ListCountry obj = new ListCountry();
        return obj.run();

    }

    public String[] run() {

        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {

            Locale obj = new Locale("", countryCode);

            System.out.println("Country Code = " + obj.getCountry()
                    + ", Country Name = " + obj.getDisplayCountry());

        }

        System.out.println("Done");
        return locales;
    }

    public List<String> getAllLanguages(Context context){
        List<String> l1 = new ArrayList<String>();

        try {
            JSONObject object  = new JSONObject(Function.loadJSONFromAsset(context, "languages.json"));

            JSONArray m_jArry = object.getJSONArray("languages");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jsonObject = m_jArry.getJSONObject(i);

                l1.add(jsonObject.getString("name"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

       /* final Locale[] availableLocales = Locale.getAvailableLocales();
        List<Locale> availableLocalesList = Arrays.asList(availableLocales);

        List<String> l1 = new ArrayList<String>();
        for(int i = 0; i<availableLocalesList.size(); i++) {
            Locale locale = availableLocalesList.get(i);
            String[] a = locale.getDisplayName().split(" ");
               l1.add(a[0]);
        }
        HashSet hs = new HashSet();
        hs.addAll(l1);
        l1.clear();
        l1.addAll(hs);
       // Collections.sort(l1);
        Collections.sort(l1, Collator.getInstance(new Locale(Locale.getDefault().getDisplayLanguage())));
        */
        return l1;
    }

    public String getCode(Context context,String lang){

        try {
            JSONObject object  = new JSONObject(Function.loadJSONFromAsset(context, "languages.json"));

            JSONArray m_jArry = object.getJSONArray("languages");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jsonObject = m_jArry.getJSONObject(i);

                if(jsonObject.getString("name").equalsIgnoreCase(lang)){
                    return jsonObject.getString("code");
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

      /*  final Locale[] availableLocales = Locale.getAvailableLocales();
        for(final Locale locale : availableLocales){
            if(locale.getDisplayName().equalsIgnoreCase(lang)){
                return locale.getLanguage();
            }
        }*/
        return "";
    }

   /* Log.d("Languagesss::",":"+locale.getDisplayName()+":"+locale.getLanguage()+":"
            +locale.getCountry()+":values-"+locale.toString().replace("_","-r"));*/
}
