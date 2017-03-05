package com.example.ashutosh.music_player.SoundCloud;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by ashutosh on 26/2/17.
 */

public interface SCService
{
    @GET("/users/{id}/favorites?client_id=" + Config.CLIENT_ID)
    Call<List<Track>> getRecentTracks(@Path("id") String userID) ;
}
