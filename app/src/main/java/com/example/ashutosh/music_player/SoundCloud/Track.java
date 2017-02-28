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

    @SerializedName("title")
    private String mTitle ;

    @SerializedName("id")
    private String mID ;

    @SerializedName("stream_url")
    private String mStreamURL ;

    @SerializedName("artwork_url")
    private String mArtworkURL ;
}
