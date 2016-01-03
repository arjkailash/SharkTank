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
    // These are the names of the JSON objects that need to be extracted.
    public static final String FLI_PHOTOS = "photos";
    public static final String FLI_PHOTO = "photo";
    public static final String FLI_ID = "id";
    public static final String FLI_URL_T = "url_t";
    public static final String FLI_URL_C = "url_c";
    public static final String FLI_URL_L = "url_l";
    public static final String FLI_URL_O = "url_o";
    public static final String FLI_PER_PAGE = "perpage";
    public static final String FLI_TITLE = "title";
    public static final String FLI_PHOTO_DESCRIPTION = "description";
    public static final String FLI_CONTENT = "_content";

    public static ArrayList<Photo> getPhotosFromJson(String photosJsonStr)
            throws JSONException {


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
            if(photo.has(FLI_URL_T)) {
                p.setUrl_t(photo.getString(FLI_URL_T));
            }
            if(photo.has(FLI_URL_O)) {
                p.setUrl_o(photo.getString(FLI_URL_O));
            }
            if(photo.has(FLI_URL_C)) {
                p.setUrl_c(photo.getString(FLI_URL_C));
            }
            if(photo.has(FLI_URL_L)) {
                p.setUrl_c(photo.getString(FLI_URL_L));
            }

            photoList.add(p);
        }

        /*for (Photo s : photoList) {
            Log.v(LOG_TAG, "Photo id: " + s.getId());
        }*/
        return photoList;

    }

    public static String getPhotoDetailsFromJson(String photosJsonStr)
            throws JSONException {


        JSONObject jsonObject = new JSONObject(photosJsonStr);
        JSONObject photo = jsonObject.getJSONObject(FLI_PHOTO);


        // Get the JSON object representing the photo
        JSONObject description = photo.getJSONObject(FLI_PHOTO_DESCRIPTION);
        String content = description.getString(FLI_CONTENT);
        return content;
    }

}