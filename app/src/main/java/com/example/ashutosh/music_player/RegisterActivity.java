package com.example.ashutosh.music_player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {
    String email = "" ;
    String password = "" ;
    String username = "" ;
    String phone = "" ;
    boolean flag1 = true ;
    boolean flag2 = true ;
    SpotsDialog ad ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);

        final EditText etEmail = (EditText) findViewById(R.id.editText3) ;
        final EditText etPass = (EditText) findViewById(R.id.editText) ;
        final EditText etPhone = (EditText) findViewById(R.id.etMobile) ;
        Button btregister = (Button) findViewById(R.id.btRegister) ;

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches())
                {
                    etEmail.setError("Invalid Email");
                    flag1 = true ;
                }
                else
                    flag1 = false ;
            }
        });


     /*   etPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    addFromEditText();
                    return true ;
                }
                return false ;
            }
        }); */
        btregister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                 email = etEmail.getText().toString() ;
                 String password1 = etPass.getText().toString() ;
                 username = "anon";
                try {
                    password = encrypt(password1, "ashu") ;
                } catch (Exception e) {
                    e.printStackTrace();
                }


                phone = etPhone.getText().toString() ;

                if(email.equals("") || password.equals("") || phone.equals(""))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this) ;
                    builder.setMessage("Make sure none of the field is blank !")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                    return ;
                }

                if(flag1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this) ;
                    builder.setMessage("Please enter a valid email address !")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                    return;
                }
                ad = new SpotsDialog(RegisterActivity.this) ;
                ad.show();
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
                                ad.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class) ;
                                startActivity(intent);
                            }
                            else
                            {
                                ad.dismiss();
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

    public static String encrypt(String strClearText, String strKey) throws Exception {
        String strData = "" ;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(strKey.getBytes(), "Blowfish") ;
            Cipher cipher = Cipher.getInstance("Blowfish") ;
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(strClearText.getBytes()) ;
            strData = new String(encrypted) ;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception(e) ;
        }
        return strData ;
    }

}


