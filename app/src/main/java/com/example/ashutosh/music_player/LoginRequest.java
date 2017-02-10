package com.example.ashutosh.music_player;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ashutosh on 11/2/17.
 */

public class LoginRequest extends StringRequest
{
    private static final String REGISTER_REQUEST_URL = "http://ashuthesmartest.000webhostapp.com/login.php" ;
    private Map<String,String> params ;

    public LoginRequest(String email, String password, Response.Listener<String> listener){
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null) ;
        params = new HashMap<>() ;
        params.put("email", email ) ;
        params.put("password", password ) ;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
