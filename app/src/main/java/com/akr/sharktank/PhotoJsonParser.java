package com.akr.sharktank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARJUNRKAILASH on 12/24/2015.
 */
public class PhotoJsonParser {
    private static final String LOG_TAG = PhotoJsonParser.class.getSimpleName();

    public static ArrayList<Photo> getPhotosFromJson(String photosJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String FLI_PHOTOS = "photos";
        final String FLI_PHOTO = "photo";
        final String FLI_ID = "id";
        final String FLI_URL_T = "url_t";
        final String FLI_URL_C = "url_c";
        final String FLI_URL_O = "url_o";
        final String FLI_PER_PAGE = "perpage";
        final String FLI_TITLE = "title";

        JSONObject jsonObject = new JSONObject(photosJsonStr);
        JSONObject photos = jsonObject.getJSONObject(FLI_PHOTOS);
        //int numPhotos = jsonObject.getInt(FLI_PER_PAGE);
        JSONArray photosArray = photos.getJSONArray(FLI_PHOTO);

        ArrayList<Photo> photoList = new ArrayList<Photo>();
        for(int i = 0; i < photosArray.length(); i++) {

            // Get the JSON object representing the photo
            JSONObject photo =  photosArray.getJSONObject(i);
            Photo p = new Photo();

            p.setId(photo.getString(FLI_ID));
            p.setTitle(photo.getString(FLI_TITLE));
            p.setUrl_t(photo.getString(FLI_URL_T));
            //p.setUrl_o(photo.getString(FLI_URL_O));
            //p.setUrl_c(photo.getString(FLI_URL_C));

            photoList.add(p);
        }

        /*for (Photo s : photoList) {
            Log.v(LOG_TAG, "Photo id: " + s.getId());
        }*/
        return photoList;

    }

}