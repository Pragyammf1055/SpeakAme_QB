package com.speakame.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.speakame.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Peter on 6/13/2016.
 */
public class GroupmemberAdapter extends  RecyclerView.Adapter<GroupmemberAdapter.MyViewHolder> {


        Context context;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title, year, genre;
    CircleImageView circleImageView;
    public MyViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        circleImageView= (CircleImageView)view.findViewById(R.id.userpic);

    }
}
    public GroupmemberAdapter(Context context) {
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_contact_display, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
    /*    SongListBeen movie = moviesList.get(0);
        SongListBeen newlist = newList.get(position);
        holder.title.setText(newlist.getSongs_name());
        Picasso.with(context)
                .load(movie.getCover_image())
                .into(holder.circleImageView);*/
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}


