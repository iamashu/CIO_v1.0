package com.example.ashutosh.music_player.SoundCloud;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ashutosh on 26/2/17.
 */

public class Track
{
    public String getTitle() {
        return mTitle;
    }

    public String getID() {
        return mID;
    }

    public String getStreamURL() {
        return mStreamURL;
    }

    public String getArtworkURL() {
        return mArtworkURL;
    }

    public String getmTags() { return mTags; }

    public int getmDuration()   { return mDuration ;  }


    @SerializedName("title")
    private String mTitle ;

    @SerializedName("duration")
    private int mDuration;

    @SerializedName("id")
    private String mID ;

    @SerializedName("stream_url")
    private String mStreamURL ;

    @SerializedName("artwork_url")
    private String mArtworkURL ;

    @SerializedName("tag_list")
    private String mTags ;
}
