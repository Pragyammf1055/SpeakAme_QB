package com.speakame.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.speakame.Beans.AllBeans;
import com.speakame.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by MMFA-YOGESH on 16-Feb-17.
 */

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyViewHolder> {

    Activity context = null;
    ArrayList<AllBeans> objects;
    private ArrayList<AllBeans> countrylist;


    public CountryAdapter(Activity context, ArrayList<AllBeans> countrylist) {
        this.countrylist = countrylist;
        this.context = context;
        this.objects = new ArrayList<AllBeans>();
        this.objects.addAll(countrylist);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return 0;
            default:
                return 0;
        }
    }

    @Override
    public CountryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        switch (viewType) {
            case 0: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_countrylist, parent, false);

                return new CountryAdapter.MyViewHolder(view);
            }

        }
        return null;

    }

    @Override
    public void onBindViewHolder(CountryAdapter.MyViewHolder holder, int position) {

        AllBeans allBeans = countrylist.get(position);
        holder.countrynametext.setText(allBeans.getCountryName());
        holder.countrycodetext.setText(allBeans.getCountrycode());


    }

    @Override
    public int getItemCount() {
        return countrylist.size();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        countrylist.clear();
        if (charText.length() == 0) {
            countrylist.addAll(objects);
        } else {
            for (AllBeans wp : objects) {
                if (wp.getCountryName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    countrylist.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView countrynametext, countrycodetext;


        public MyViewHolder(final View view) {
            super(view);
            countrynametext = (TextView) view.findViewById(R.id.conuntryname);
            countrycodetext = (TextView) view.findViewById(R.id.conuntrycode);
            Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");

            countrynametext.setTypeface(tf1);
            countrynametext.setTypeface(tf1);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Log.d("chatvalue", getAdapterPosition() + "::" + countrylist.get(getAdapterPosition()));

            Intent intent = new Intent();
            intent.putExtra("countryname", countrylist.get(getAdapterPosition()).getCountryName());
            intent.putExtra("countrycode", countrylist.get(getAdapterPosition()).getCountrycode());
            context.setResult(-1, intent);
            context.finish();
        }
    }


}

