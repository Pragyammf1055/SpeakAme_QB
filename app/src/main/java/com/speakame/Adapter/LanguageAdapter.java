package com.speakame.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.speakame.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.MyViewHolder> {

    private static final String TAG = "LanguageAdapter";
    Activity context = null;
    List<String> languageArrayList;
    List<String> objects;
    private View itemView;

    public LanguageAdapter(Activity context, List<String> languageArrayList) {
        this.context = context;
        this.objects = new ArrayList<>();
        this.objects.addAll(languageArrayList);
        this.languageArrayList = languageArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        MyViewHolder myViewHolder;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.language_recycler_view, parent, false);

        myViewHolder = new MyViewHolder(itemView);
//        return new MyViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.languageNameTextView.setText(languageArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        int listSize = languageArrayList.size();
        Log.v(TAG, "List Size :-" + listSize);
        return listSize;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        languageArrayList.clear();
        if (charText.length() == 0) {
            languageArrayList.addAll(objects);
        } else {
            for (String wp : objects) {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                    languageArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout languageLinear;
        public TextView languageNameTextView;

        public MyViewHolder(View view) {

            super(view);

            languageNameTextView = (TextView) view.findViewById(R.id.languageNameTextView);
            languageLinear = (LinearLayout) view.findViewById(R.id.languageLinear);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("chatvalue", getAdapterPosition() + "::" + languageArrayList.get(getAdapterPosition()));

            String languageName = languageArrayList.get(getAdapterPosition());
            Log.v(TAG, "Language name:-" + languageName);
            Intent intent = new Intent();
            intent.putExtra("language", languageArrayList.get(getAdapterPosition()));
            context.setResult(-1, intent);
            context.finish();
        }
    }
}
