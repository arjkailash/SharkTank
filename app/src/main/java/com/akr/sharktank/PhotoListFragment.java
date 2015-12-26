package com.akr.sharktank;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.json.JSONException;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PhotoListFragment extends Fragment {
    RecyclerViewAdapter adapter;
    Context context;
    RecyclerView recyclerView;
    public PhotoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        new FetchSharkPhotosTask().execute();
        Log.d("OnCreate", "This finished");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("OnCreateView", "This started");
        View view = inflater.inflate(R.layout.fragment_photo_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);


        Log.d("OnCreateView", "This finished");

        return view;
    }

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
                        .appendQueryParameter(EXTRAS, "url_t")
                        //.appendQueryParameter(EXTRAS, "url_c")
                        //.appendQueryParameter(EXTRAS, "url_l")
                        //.appendQueryParameter(EXTRAS, "url_o")
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
                adapter = new RecyclerViewAdapter(context,photos);
                recyclerView.setAdapter(adapter);
                Log.d("OnPsotExecute","This finished");
            }
        }
    }
}

