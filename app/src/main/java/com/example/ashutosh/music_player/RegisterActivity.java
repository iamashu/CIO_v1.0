package com.example.ashutosh.music_player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etEmail = (EditText) findViewById(R.id.editText3) ;
        final EditText etPass = (EditText) findViewById(R.id.editText) ;
        final EditText etUname = (EditText) findViewById(R.id.etUname) ;
        final EditText etPhone = (EditText) findViewById(R.id.etMobile) ;
        Button btregister = (Button) findViewById(R.id.btRegister) ;

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches())
                {
                    etEmail.setError("Invalid Email");
                }
            }
        });

        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(! Patterns.PHONE.matcher(etPhone.getText()).matches())
                {
                    etPhone.setError("Invalid Phone No.");
                }
            }
        });

        btregister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString() ;
                final String username = etUname.getText().toString() ;
                final String password = etPass.getText().toString() ;
                final String phone = etPhone.getText().toString() ;


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try
                        {
                            JSONObject jsonObject = new JSONObject(response) ;
                            boolean success = jsonObject.getBoolean("success") ;
                            String msg = jsonObject.getString("msg") ;

                            if(success)
                            {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class) ;
                                startActivity(intent);
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this) ;
                                builder.setMessage(msg)
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
                } ;

                RegisterRequest registerRequest = new RegisterRequest(email, username, password, phone, responseListener) ;
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this) ;
                queue.add(registerRequest) ;
            }
        });



    }
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
