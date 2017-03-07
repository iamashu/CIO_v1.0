package com.example.ashutosh.music_player;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    LoginButton loginButton ;
    CallbackManager callbackManager ;
    private String facebook_id, full_name, email_id ;
    ProgressDialog progress ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        final EditText eUname = (EditText) findViewById(R.id.editUname) ;
        final EditText ePass = (EditText) findViewById(R.id.editPass) ;
        final Button btLogin = (Button) findViewById(R.id.btLogin) ;
        final TextView btReg = (TextView) findViewById(R.id.tvRegister) ;

        progress = new ProgressDialog(LoginActivity.this) ;
        progress.setMessage("Please Wait");
        progress.setIndeterminate(false);
        progress.setCancelable(false);


        eUname.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!Patterns.EMAIL_ADDRESS.matcher(eUname.getText()).matches())
                {
                    eUname.setError("Invalid Email");
                }
            }
        });


        loginButton = (LoginButton) findViewById(R.id.fb_login_btn) ;
        loginButton.setReadPermissions("public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progress.show() ;

                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try
                        {
                            email_id = object.getString("email") ;
                            full_name = object.getString("first_name") ;
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try
                                    {
                                        JSONObject jsonObject = new JSONObject(response) ;
                                        boolean success = jsonObject.getBoolean("success") ;

                                        Intent intent = new Intent(LoginActivity.this, Home.class) ;
                                        startActivity(intent);
                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            } ;

                            RegisterRequest registerRequest = new RegisterRequest(email_id, full_name, "qwerty", "9999999999", responseListener) ;
                            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this) ;
                            queue.add(registerRequest) ;
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }) ;
                Bundle parameters = new Bundle()  ;
                parameters.putString("fields", "id,first_name,email");
                request.setParameters(parameters);
                request.executeAsync() ;
                progress.dismiss();
            }
            @Override
            public void onCancel()
            {

            }

            @Override
            public void onError(FacebookException error)
            {

            }
        });


loginButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends", "email"));
        System.out.println(email_id) ;

};
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

                Response.Listener<String> responseListener = new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("Entering onResponse");
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response) ;
                            boolean success = jsonObject.getBoolean("success") ;

                            if(success)
                            {
                                System.out.println("Successful");
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
                            System.out.println("Entering catch");
                            e.printStackTrace();
                        }
                        System.out.println("No try-catch");
                    }
                };
                LoginRequest loginRequest = new LoginRequest(email, password, responseListener) ;
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this) ;
                queue.add(loginRequest) ;
            }
        });

    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN) ;
        intent.addCategory(Intent.CATEGORY_HOME) ;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(callbackManager.onActivityResult(requestCode,resultCode,data))
        {
            return;
        }

    }
}
