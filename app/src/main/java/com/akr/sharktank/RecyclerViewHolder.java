package com.akr.sharktank;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ARJUNRKAILASH on 12/24/2015.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imageView;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        this.imageView = (ImageView) itemView.findViewById(R.id.image_id);
        imageView.setImageResource(R.drawable.ic_place_holder);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //do something
    }
}
