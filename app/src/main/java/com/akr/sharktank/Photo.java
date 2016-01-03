package com.akr.sharktank;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ARJUNRKAILASH on 12/24/2015.
 */
public class Photo implements Parcelable{
    private String url_c;
    private String url_t;
    private String url_l;
    private String url_o;
    private String id;
    private String title;

    public Photo(){}
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl_o() {
        return url_o;
    }

    public void setUrl_o(String url_o) {
        this.url_o = url_o;
    }

    public String getUrl_t() {
        return url_t;
    }

    public void setUrl_t(String url_t) {
        this.url_t = url_t;
    }

    public String getUrl_c() {
        return url_c;
    }

    public void setUrl_c(String url_c) {
        this.url_c = url_c;
    }

    public String getUrl_l() {
        return url_l;
    }

    public void setUrl_l(String url_l) {
        this.url_l = url_l;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url_c);
        dest.writeString(url_o);
        dest.writeString(url_l);
        dest.writeString(url_t);
        dest.writeString(id);
        dest.writeString(title);
    }

    private Photo(Parcel in){
        url_c = in.readString();
        url_o = in.readString();
        url_l = in.readString();
        url_t = in.readString();
        id = in.readString();
        title = in.readString();
    }
    public static final Parcelable.Creator<Photo> CREATOR
            = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

}
