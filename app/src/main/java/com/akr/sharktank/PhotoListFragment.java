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
import android.support.v4.widget.SwipeRefreshLayout;
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
    private FetchSharkPhotosTask fetchSharkPhotosTask;
    SwipeRefreshLayout swipeRefreshLayout;
    private final static String FLI_HASHMAP = "HashMap";
    private final static String FLI_PHOTOLIST = "PhotoList";

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
        Log.d("OnCreate","This is called when fragment is recreated");
        //if we are in photofragment and we rotate device
        if(savedInstanceState != null){
            Bundle bundle1 = savedInstanceState.getBundle(FLI_HASHMAP);
            if(bundle1 != null){
                hashMap = new HashMap<String,Bitmap>();
                Set<String> keys = bundle1.keySet();
                for (String key : keys) {
                    hashMap.put(key,(Bitmap) bundle1.get(key));
                }
            }
            Bundle bundle2 = savedInstanceState.getBundle(FLI_PHOTOLIST);
            if(bundle2 != null){
                photoList = new ArrayList<Photo>();
                Set<String> keys = bundle2.keySet();
                for (String key : keys) {
                    photoList.add(Integer.valueOf(key),(Photo)bundle2.get(key));
                }
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("OnCreateView","This is called again");
        // if we are just coming back from photofragment without rotating device
        if(savedInstanceState != null && hashMap == null && photoList == null){
            Bundle bundle1 = savedInstanceState.getBundle(FLI_HASHMAP);
            if(bundle1 != null){
                hashMap = new HashMap<String,Bitmap>();
                Set<String> keys = bundle1.keySet();
                for (String key : keys) {
                    hashMap.put(key,(Bitmap) bundle1.get(key));
                }
            }
            Bundle bundle2 = savedInstanceState.getBundle(FLI_PHOTOLIST);
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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(adapter != null && adapter.hashMap != null){
                    adapter.hashMap.clear();
                    if(hashMap != null){
                        hashMap.clear();            //for both hashmaps to be in sync
                    }
                }
                if(fetchSharkPhotosTask != null){
                    fetchSharkPhotosTask.cancel(true);   //cancel it if it is running
                }
                fetchSharkPhotosTask = new FetchSharkPhotosTask();
                fetchSharkPhotosTask.execute();
            }
        });

        if(adapter == null){
            if(hashMap != null && photoList != null){       //if we rotate device in photofragment adapter will be killed but we save hashmap and photolist
                adapter = new RecyclerViewAdapter(hashMap,photoList,context,this);
                recyclerView.setAdapter(adapter);
            }else {// loading for the first time
                fetchSharkPhotosTask = new FetchSharkPhotosTask();
                fetchSharkPhotosTask.execute();
            }
        }else {     //when fragment returns from backstack only views are destroyed so resetting the adapter
            recyclerView.setAdapter(adapter);
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
        //rotating device while in the same fragment
        if(adapter != null) {
            Bundle photoBundle = new Bundle();
            Iterator<Map.Entry<String, Bitmap>> iter = adapter.hashMap.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String, Bitmap> entry = iter.next();
                photoBundle.putParcelable(entry.getKey(), entry.getValue());
            }
            outState.putBundle(FLI_HASHMAP, photoBundle);
            Bundle Photos = new Bundle();
            for (Photo p : adapter.photos) {
                Photos.putParcelable(String.valueOf(adapter.photos.indexOf(p)), p);
            }
            outState.putBundle(FLI_PHOTOLIST, Photos);
        }
        //if we are in photofragment and we rotate device
        if(adapter == null && hashMap!=null && photoList != null) {
            Bundle photoBundle = new Bundle();
            Iterator<Map.Entry<String, Bitmap>> iter = hashMap.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String, Bitmap> entry = iter.next();
                photoBundle.putParcelable(entry.getKey(), entry.getValue());
            }
            outState.putBundle(FLI_HASHMAP, photoBundle);
            Bundle Photos = new Bundle();
            for (Photo p : photoList) {
                Photos.putParcelable(String.valueOf(photoList.indexOf(p)), p);
            }
            outState.putBundle(FLI_PHOTOLIST, Photos);
        }
    }

    @Override
    public void imageClicked(Photo p ) {
        PhotoFragment photoFragment = new PhotoFragment();

        Bundle args = new Bundle();
        args.putString(PhotoJsonParser.FLI_ID,p.getId());
        args.putString(PhotoJsonParser.FLI_URL_T,p.getUrl_t());
        args.putString(PhotoJsonParser.FLI_URL_C,p.getUrl_c());
        args.putString(PhotoJsonParser.FLI_URL_L,p.getUrl_l());
        args.putString(PhotoJsonParser.FLI_URL_O,p.getUrl_o());
        args.putString(PhotoJsonParser.FLI_TITLE,p.getTitle());
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
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    }
}

