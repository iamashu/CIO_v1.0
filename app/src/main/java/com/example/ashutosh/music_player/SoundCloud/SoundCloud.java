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
    private static final SCService2 SERVICE2 = RETROFIT.create(SCService2.class) ;
    private static final SCService3 SERVICE3 = RETROFIT.create(SCService3.class) ;

    public static SCService getService()
    {
        return SERVICE ;
    }
    public static SCService2 getService2()
    {
        return SERVICE2 ;
    }
    public static SCService3 getService3()
    {
        return SERVICE3 ;
    }
}
