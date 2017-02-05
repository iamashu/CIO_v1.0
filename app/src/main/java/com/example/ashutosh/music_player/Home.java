package com.example.ashutosh.music_player;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private static final String URL_DATA = "http://davidpots.com/jakeworry/017%20JSON%20Grouping,%20part%203/data.json" ;
    private RecyclerView rv ;
    private RecyclerView.Adapter adapter ;
    private List<ListItem> listItems ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rv = (RecyclerView) findViewById(R.id.rview1) ;
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv.setHasFixedSize(true);


        listItems = new ArrayList<>() ;

        loadRecyclerViewData() ;
    }

    private void loadRecyclerViewData()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this) ;
        progressDialog.setMessage("Loading data...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response) ;
                            JSONArray array = jsonObject.getJSONArray("songs") ;

                            for(int i=0 ; i<array.length() ; i++)
                            {
                                JSONObject o = array.getJSONObject(i) ;
                                ListItem item = new ListItem(
                                        o.getString("title") ,
                                        o.getString("artist") ,
                                        o.getString("img_url")
                                ) ;
                                listItems.add(item) ;
                            }

                            adapter = new MyAdapter(listItems, getApplicationContext()) ;
                            rv.setAdapter(adapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) ;

        RequestQueue requestQueue = Volley.newRequestQueue(this) ;
        requestQueue.add(stringRequest) ;
    }

}
