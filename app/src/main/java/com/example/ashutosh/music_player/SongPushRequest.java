package com.example.ashutosh.music_player;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ashutosh on 5/4/17.
 */

public class SongPushRequest extends StringRequest
{
    private static final String SONGPUSH_REQUEST_URL = "http://ashuthesmartest.000webhostapp.com/songpush.php" ;

    private Map<String,String> params ;

    public SongPushRequest(String artist, String email, Response.Listener<String> listener){
        super(Request.Method.POST, SONGPUSH_REQUEST_URL, listener, null) ;
        params = new HashMap<>() ;
        params.put("artist", artist ) ;
        params.put("email", email ) ;

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
