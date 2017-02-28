package com.example.ashutosh.music_player.SoundCloud;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ashutosh on 26/2/17.
 */

public interface SCService
{
    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    Call<List<Track>> getRecentTracks(@Query("user_id") String userID) ;
}
