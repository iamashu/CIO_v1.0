package com.example.ashutosh.music_player.SoundCloud;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ashutosh on 9/3/17.
 */

public interface SCService3
{
    @GET("/tracks/?client_id=" + Config.CLIENT_ID + "&limit=1")
    Call<List<Track>> getRecentTracks(@Query("q") String tit) ;
}

