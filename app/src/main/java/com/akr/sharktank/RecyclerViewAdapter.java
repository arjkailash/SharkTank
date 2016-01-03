package com.akr.sharktank;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ARJUNRKAILASH on 12/24/2015.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    public interface AdapterInterface{
        public void imageClicked(String id,String url_t, String url_c, String url_l, String url_o, String photoTitle );
    }
    protected AdapterInterface buttonListener;
    protected ArrayList<Photo> photos;
    private Context context;
    //using hash map to cache bitmaps. (Not a great idea when there are large number of images, but we have just 100 small thumbnails.(found by playing with apis)
    //This is done to improve performance. executing an async task everytime we need an image is not a good idea.
    protected HashMap<String,Bitmap> hashMap = new HashMap<String,Bitmap>();

    public RecyclerViewAdapter(){}

    public RecyclerViewAdapter(HashMap<String,Bitmap> hashMap,ArrayList<Photo> photos, Context context,AdapterInterface buttonListener){
        this.hashMap = hashMap;
        this.photos = photos;
        this.context = context;
        this.buttonListener = buttonListener;
    }

    public RecyclerViewAdapter(Context context, ArrayList<Photo> photos,AdapterInterface buttonListener){
        this.photos = photos;
        this.context = context;
        this.buttonListener = buttonListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view , context);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        //make async task to get image as a bitmap and set imageview as the bitmap.
        //check if already loaded and use from hash map
        if(hashMap.containsKey(photos.get(position).getUrl_t())){
            holder.imageView.setImageBitmap(hashMap.get(photos.get(position).getUrl_t()));
        }else {
            new SetImageTask(holder.imageView).execute(photos.get(position).getUrl_t());
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass intent to fragment to launch a new fragment
                buttonListener.imageClicked(photos.get(position).getId(),
                        photos.get(position).getUrl_t(),
                        photos.get(position).getUrl_c(),
                        photos.get(position).getUrl_l(),
                        photos.get(position).getUrl_o(),
                        photos.get(position).getTitle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    private class SetImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String url;
        public SetImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            this.url = urldisplay;
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            hashMap.put(url,result);
            bmImage.setImageBitmap(result);
        }
    }
}
