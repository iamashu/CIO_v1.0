package com.example.ashutosh.music_player;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by ashutosh on 3/5/17.
 */

public class TopSongRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://ashuthesmartest.000webhostapp.com/topget.php" ;
    public TopSongRequest(Response.Listener<String> listener)
    {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null) ;
    }

}
