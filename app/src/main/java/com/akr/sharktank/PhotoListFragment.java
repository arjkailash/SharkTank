package com.akr.sharktank;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.json.JSONException;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PhotoListFragment extends Fragment implements RecyclerViewAdapter.AdapterInterface{
    private RecyclerViewAdapter adapter;
    private Context context;
    private HashMap<String,Bitmap> hashMap;
    private ArrayList<Photo> photoList;
    private RecyclerView recyclerView;
    public PhotoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("OnCreateView","This is called again");
        // Inflate the layout for this fragment
        if(savedInstanceState != null){
            Bundle bundle1 = savedInstanceState.getBundle("HashMap");
            if(bundle1 != null){
                hashMap = new HashMap<String,Bitmap>();
                Set<String> keys = bundle1.keySet();
                for (String key : keys) {
                    hashMap.put(key,(Bitmap) bundle1.get(key));
                }
            }
            Bundle bundle2 = savedInstanceState.getBundle("Photos");
            if(bundle2 != null){
                photoList = new ArrayList<Photo>();
                Set<String> keys = bundle2.keySet();
                for (String key : keys) {
                    photoList.add(Integer.valueOf(key),(Photo)bundle2.get(key));
                }
            }
        }

        View view = inflater.inflate(R.layout.fragment_photo_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        if(adapter == null){
            if(hashMap != null && photoList != null){
                adapter = new RecyclerViewAdapter(hashMap,photoList,context,this);
                recyclerView.setAdapter(adapter);
            }else {
                new FetchSharkPhotosTask().execute();
            }
        }else {
            recyclerView.setAdapter(adapter);  //when fragment returns from backstack only views are destroyed so resetting the adapter
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(adapter != null) {
            Bundle photoBundle = new Bundle();
            Iterator<Map.Entry<String, Bitmap>> iter = adapter.hashMap.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String, Bitmap> entry = iter.next();
                photoBundle.putParcelable(entry.getKey(), entry.getValue());
            }
            outState.putBundle("HashMap", photoBundle);
            Bundle Photos = new Bundle();
            for (Photo p : adapter.photos) {
                Photos.putParcelable(String.valueOf(adapter.photos.indexOf(p)), p);
            }
            outState.putBundle("Photos", Photos);
        }
    }

    @Override
    public void imageClicked(String id,String url_t, String url_c, String url_l, String url_o, String photoTitle ) {
        PhotoFragment photoFragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(PhotoJsonParser.FLI_ID,id);
        args.putString(PhotoJsonParser.FLI_URL_T,url_t);
        args.putString(PhotoJsonParser.FLI_URL_C,url_c);
        args.putString(PhotoJsonParser.FLI_URL_L,url_l);
        args.putString(PhotoJsonParser.FLI_URL_O,url_o);
        args.putString(PhotoJsonParser.FLI_TITLE,photoTitle);
        photoFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.photo_list_holder, photoFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    //Async task to get the list of ids and urls for the thumbnails to be displayed
    public class FetchSharkPhotosTask extends AsyncTask<Void, Void, ArrayList<Photo>> {

        private final String LOG_TAG = FetchSharkPhotosTask.class.getSimpleName();
        @Override
        protected ArrayList<Photo> doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String photosJsonString = null;

            try {
                // Construct the URL for the Flickr Photo Search query

                final String FORECAST_BASE_URL = "https://api.flickr.com/services/rest/?";
                final String METHOD = "method";
                final String METHODSEARCH = "flickr.photos.search";
                final String APIKEY = "api_key";
                final String KEY = "949e98778755d1982f537d56236bbb42";
                final String TAGS = "tags";
                final String FORMAT = "format";
                final String NOJSONCALLBACK = "nojsoncallback";
                final String PAGE = "page";
                final String EXTRAS = "extras";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD, METHODSEARCH)
                        .appendQueryParameter(APIKEY,KEY)
                        .appendQueryParameter(TAGS,"shark")
                        .appendQueryParameter(FORMAT, "json")
                        .appendQueryParameter(NOJSONCALLBACK, "1")
                        .appendQueryParameter(PAGE, "1")
                        .appendQueryParameter(EXTRAS, "url_t,url_c,url_l,url_o")
                        .build();

                URL url = new URL(builtUri.toString());
                //url = new URL("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=949e98778755d1982f537d56236bbb42&tags=shark&format=json&nojsoncallback=1&page=1&extras =url_t,url_c,url_l,url_o");
                //Log.v(LOG_TAG,"Built URI: "+builtUri.toString());

                // Create the request to Flickr, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                photosJsonString = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the json, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try{
                return PhotoJsonParser.getPhotosFromJson(photosJsonString);
            }
            catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }

        }


        @Override
        protected void onPostExecute(ArrayList<Photo> photos) {
            if (photos != null) {
                photoList = photos;
                adapter = new RecyclerViewAdapter(context,photos,PhotoListFragment.this);
                recyclerView.setAdapter(adapter);
            }
        }
    }
}

