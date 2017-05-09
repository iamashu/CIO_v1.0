package com.example.ashutosh.music_player;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ashutosh on 15/4/17.
 */

public class FavRequest extends StringRequest
{
    private static final String FAV_REQUEST_URL = "http://ashuthesmartest.000webhostapp.com/fav.php" ;
    private Map<String,String> params ;

    public FavRequest(String song, Response.Listener<String> listener){
        super(Request.Method.POST, FAV_REQUEST_URL, listener, null) ;
        params = new HashMap<>() ;
        params.put("song", song ) ;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
