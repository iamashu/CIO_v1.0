package com.example.ashutosh.music_player;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity implements TextWatcher,
        CompoundButton.OnCheckedChangeListener{

    LoginButton loginButton ;
    CallbackManager callbackManager ;
    public String facebook_id, full_name, email_id ;
    private CheckBox rem_userpass ;
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor ;
    private static final String PREF_NAME = "prefs" ;
    private static final String KEY_REMEMBER = "remember" ;
    private static final String KEY_USERNAME = "username" ;
    private static final String KEY_PASS = "password" ;
    ProgressDialog progress ;
    String password = "";
    EditText eUname ;
    EditText ePass ;
    Button btLogin ;
    TextView btReg ;
    SpotsDialog ad ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) ;
        editor = sharedPreferences.edit() ;

        rem_userpass = (CheckBox) findViewById(R.id.cbc) ;


        eUname = (EditText) findViewById(R.id.editUname) ;
        ePass = (EditText) findViewById(R.id.editPass) ;
        btLogin = (Button) findViewById(R.id.btLogin) ;
        btReg = (TextView) findViewById(R.id.tvRegister) ;

        if(sharedPreferences.getBoolean(KEY_REMEMBER, false))
            rem_userpass.setChecked(true);
        else
            rem_userpass.setChecked(false);

        eUname.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        ePass.setText(sharedPreferences.getString(KEY_PASS, ""));

        eUname.addTextChangedListener(this);
        ePass.addTextChangedListener(this);
        rem_userpass.setOnCheckedChangeListener(this);

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
                ad = new SpotsDialog(LoginActivity.this) ;
                ad.show();

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
                                        ad.dismiss();
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
ePass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH) {
            addFromEditText();
            return true ;
        }
        return false ;
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
                addFromEditText();
            }
        });

    }

    private void addFromEditText() {

        final String email = eUname.getText().toString() ;
        String password1 = ePass.getText().toString() ;
        if(email.equals("")  || password1.equals(""))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this) ;
            builder.setMessage("Login Failed")
                    .setNegativeButton("Retry", null)
                    .create()
                    .show();
            return;
        }
        ad = new SpotsDialog(LoginActivity.this) ;
        ad.show();
        View view = getCurrentFocus() ;
        if(view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }

        try {
            password = encrypt(password1 , "ashu") ;
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                        ad.dismiss();
                        Intent intent = new Intent(LoginActivity.this, Home.class) ;
                        intent.putExtra("em", email) ;
                        startActivity(intent);
                    }
                    else
                    {
                        ad.dismiss();
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        managePrefs() ;
    }

    private void managePrefs()
    {
        if(rem_userpass.isChecked())
        {
            editor.putString(KEY_USERNAME, eUname.getText().toString().trim() ) ;
            editor.putString(KEY_PASS, ePass.getText().toString().trim() ) ;
            editor.putBoolean(KEY_REMEMBER, true) ;
            editor.apply();
        }
        else
        {
            editor.putBoolean(KEY_REMEMBER, false) ;
            editor.remove(KEY_PASS) ;
            editor.remove(KEY_USERNAME) ;
            editor.apply();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        managePrefs() ;
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
