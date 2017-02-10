package com.example.ashutosh.music_player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    LoginButton loginButton ;
    CallbackManager callbackManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText eUname = (EditText) findViewById(R.id.editUname) ;
        final EditText ePass = (EditText) findViewById(R.id.editPass) ;
        final Button btLogin = (Button) findViewById(R.id.btLogin) ;
        final TextView btReg = (TextView) findViewById(R.id.tvRegister) ;

        loginButton = (LoginButton) findViewById(R.id.fb_login_btn) ;
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Intent intent = new Intent(LoginActivity.this, Home.class) ;
                startActivity(intent);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        btReg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(LoginActivity.this , RegisterActivity.class) ;
                startActivity(regIntent);
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = eUname.getText().toString() ;
                final String password = ePass.getText().toString() ;

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response) ;
                            boolean success = jsonObject.getBoolean("success") ;

                            if(success)
                            {
                                System.out.println("jhgjhello");
                                Intent intent = new Intent(LoginActivity.this, Home.class) ;
                                startActivity(intent);
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this) ;
                                builder.setMessage("Login Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();

                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(email, password, responseListener) ;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data) ;

    }
}
