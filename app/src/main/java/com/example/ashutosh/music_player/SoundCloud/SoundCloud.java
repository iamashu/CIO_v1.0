package com.example.ashutosh.music_player.SoundCloud;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ashutosh on 26/2/17.
 */

public class SoundCloud
{
    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(Config.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build() ;
    private static final SCService SERVICE = RETROFIT.create(SCService.class) ;

    public static SCService getService()
    {
        return SERVICE ;
    }
}
