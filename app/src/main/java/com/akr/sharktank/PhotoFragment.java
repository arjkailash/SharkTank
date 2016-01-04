package com.akr.sharktank;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.DownloadListener;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v7.widget.CardView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class PhotoFragment extends Fragment {
    private String photoID;
    private String photoTitle;
    private String url_t;
    private String url_c;
    private String url_l;
    private String url_o;
    private String bestUrlToDisplay;
    private Bitmap photoImage;
    private String photoDescription;
    private String flickrUrl;
    private BroadcastReceiver receiverDownloadComplete;

    private static final String PHOTOIMAGE = "PhotoImage";
    private static final String PHOTODESCRIPTION = "PhotoDescription";
    private static final String PHOTOBUNDLE = "PhotoBundle";
    private static final String IMAGEHEIGHT = "ImageHeight";
    private static final String IMAGEWEIGHT = "ImageWeight";

    TextView textView;

    public PhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        photoID = args.getString(PhotoJsonParser.FLI_ID);
        url_t = args.getString(PhotoJsonParser.FLI_URL_T);
        url_c = args.getString(PhotoJsonParser.FLI_URL_C);
        url_l = args.getString(PhotoJsonParser.FLI_URL_L);
        url_o = args.getString(PhotoJsonParser.FLI_URL_O);
        photoTitle = args.getString(PhotoJsonParser.FLI_TITLE);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        final ImageView imageView = (ImageView) view.findViewById(R.id.photoFullFrame);

        textView = (TextView) view.findViewById(R.id.photoDescription);
        textView.setText(photoTitle);

        if(url_o != null){
            bestUrlToDisplay = url_o;
        }else if(url_l != null){
            bestUrlToDisplay = url_l;
        }else if(url_c != null){
            bestUrlToDisplay = url_c;
        }else if(url_t != null){
            bestUrlToDisplay = url_t;
        }
        if(savedInstanceState != null){
            Bundle bundle = savedInstanceState.getBundle(PHOTOBUNDLE);
            photoDescription = bundle.getString(PHOTODESCRIPTION);
            photoImage = bundle.getParcelable(PHOTOIMAGE);
        }

        if(photoImage != null){
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.loadingSpinner);
            progressBar.setVisibility(View.GONE);
            imageView.setImageBitmap(photoImage);
        }else {
            new SetImageTask(view).execute(bestUrlToDisplay);
        }

        if(photoDescription != null){
            textView.setText(photoDescription);
        }else {
            new GetPhotoDetailsTask().execute(photoID);
        }

        //buttons
        final Button download = (Button) view.findViewById(R.id.button1);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager;
                final long myDownloadReference;
                downloadManager = (DownloadManager) PhotoFragment.this.getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(bestUrlToDisplay);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDescription("Downloading image...");
                request.setTitle(PhotoFragment.this.getActivity().getString(R.string.app_name));
                request.setDestinationInExternalFilesDir(PhotoFragment.this.getActivity(), Environment.DIRECTORY_DOWNLOADS, photoID + ".jpg");
                request.setVisibleInDownloadsUi(true);
                myDownloadReference = downloadManager.enqueue(request);


                IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                receiverDownloadComplete =  new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                        if (myDownloadReference == reference){

                            CharSequence text = photoID + ".jpg" + " Downloaded!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.show();
                        }

                    }
                };
                PhotoFragment.this.getActivity().registerReceiver(receiverDownloadComplete,intentFilter);
            }
        });

        final Button openInFlickr = (Button) view.findViewById(R.id.button2);
        openInFlickr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flickrUrl != null && !flickrUrl.isEmpty()) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(flickrUrl));
                    startActivity(i);
                }
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        bundle.putParcelable(PHOTOIMAGE,photoImage);
        bundle.putString(PHOTODESCRIPTION, photoDescription);
        outState.putBundle(PHOTOBUNDLE,bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(receiverDownloadComplete!=null) {
            PhotoFragment.this.getActivity().unregisterReceiver(receiverDownloadComplete);
        }
    }

    private class SetImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ProgressBar progressBar;
        String url;
        public SetImageTask(View v) {
            this.bmImage = (ImageView) v.findViewById(R.id.photoFullFrame);
            this.progressBar = (ProgressBar) v.findViewById(R.id.loadingSpinner);
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
            photoImage = result;
            progressBar.setVisibility(View.GONE);
            bmImage.setImageBitmap(result);
        }
    }

    private class GetPhotoDetailsTask extends AsyncTask<String, Void, String> {
        String id;
        final String LOG_TAG = GetPhotoDetailsTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            id = params[0];
            // Will contain the raw JSON response as a string.
            String photosJsonString = null;

            try {
                // Construct the URL for the Flickr Photo Search query

                final String FORECAST_BASE_URL = "https://api.flickr.com/services/rest/?";
                final String METHOD = "method";
                final String METHODGETINFO = "flickr.photos.getInfo";
                final String APIKEY = "api_key";
                final String KEY = "949e98778755d1982f537d56236bbb42";
                final String PHOTOID = "photo_id";
                final String FORMAT = "format";
                final String NOJSONCALLBACK = "nojsoncallback";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD, METHODGETINFO)
                        .appendQueryParameter(APIKEY, KEY)
                        .appendQueryParameter(PHOTOID, id)
                        .appendQueryParameter(FORMAT, "json")
                        .appendQueryParameter(NOJSONCALLBACK, "1")
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
            } finally {
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

            try {
                String jsonStringCopy = new String(photosJsonString);
                flickrUrl = PhotoJsonParser.getFlickrUrlFromJson(jsonStringCopy);
                return PhotoJsonParser.getPhotoDetailsFromJson(photosJsonString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            textView.setText(photoTitle+":\n"+s);
            photoDescription = photoTitle+":\n"+s;
        }
    }
}
