package com.example.shaimaalzahrani.udacity8newsfeed;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by shaimaalzahrani on 15/04/2017.
 */

public class News implements Parcelable{
    String title;
    String description;
    String url;

    public News() {}

    public News(Parcel in) {
        title = in.readString();
        description = in.readString();
        url = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String tit) {
        title = tit;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public void setUrl(String u) {
        url = u;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {

        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
