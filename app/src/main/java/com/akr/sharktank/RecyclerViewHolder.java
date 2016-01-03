package com.akr.sharktank;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ARJUNRKAILASH on 12/24/2015.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    private Context context;

    public RecyclerViewHolder(View itemView , Context context) {
        super(itemView);
        this.context = context;
        this.imageView = (ImageView) itemView.findViewById(R.id.image_id);
        imageView.setImageResource(R.drawable.ic_place_holder);
    }
}
