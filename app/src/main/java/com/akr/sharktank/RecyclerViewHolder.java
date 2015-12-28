package com.akr.sharktank;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ARJUNRKAILASH on 12/24/2015.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imageView;
    private Context context;

    public RecyclerViewHolder(View itemView , Context context) {
        super(itemView);
        this.context = context;
        this.imageView = (ImageView) itemView.findViewById(R.id.image_id);
        imageView.setImageResource(R.drawable.ic_place_holder);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //do something
        PhotoFragment photoFragment = new PhotoFragment();
        FragmentTransaction fragmentTransaction = ((Activity)context).getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.photo_list_holder, photoFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }
}
