package com.example.ashutosh.music_player;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ashutosh on 6/4/17.
 */

public class SongGetRequest extends StringRequest
{
    private static final String REGISTER_REQUEST_URL = "http://ashuthesmartest.000webhostapp.com/songget.php" ;
    private Map<String,String> params ;

    public SongGetRequest(String email, Response.Listener<String> listener){
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null) ;
        params = new HashMap<>() ;
        params.put("email", email ) ;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
