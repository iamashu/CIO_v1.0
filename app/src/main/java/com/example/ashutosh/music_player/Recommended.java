package com.example.ashutosh.music_player;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ashutosh.music_player.ITunes.Adapter.CustomAdapter;
import com.example.ashutosh.music_player.ITunes.Model.Pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Recommended extends AppCompatActivity
{
    private CustomAdapter mAdapter ;
    public ArrayList<String> artists ;
    public ArrayList<String> genres ;
    public ArrayList<String> titles ;
    private ListView listView ;
    private ArrayList<Pojo> pojoList = new ArrayList<Pojo>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build() ;
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_recommended);

        listView = (ListView) findViewById(R.id.track_list_view) ;

        mAdapter = new CustomAdapter(this,R.layout.item, pojoList) ;

        artists = getIntent().getStringArrayListExtra("artist") ;
      //  genres = getIntent().getStringArrayListExtra("genre") ;
      //  titles = new ArrayList<String>() ;

        View view = findViewById(R.id.view1) ;

        listView.setAdapter(mAdapter);

        getData();
    }

    private void getData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        for(String s : artists)
        {
            String url = "http://itunes.apple.com/search?term=" + s + "&limit=4" ;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");

                        for(int i = 0; i < jsonArray.length(); i++){
                            Pojo pojo = new Pojo();
                            JSONObject object = jsonArray.getJSONObject(i);
                            pojo.setArtistName(object.optString("artistName","Unknown"));
                            pojo.setTrackName(object.optString("trackName","Unknown"));
                            pojo.setCollectionName(object.optString("collectionName","Unknown"));
                            pojo.setImageView(object.optString("artworkUrl100","Unknown"));
                            pojoList.add(pojo);
                            fillList();
                        }
                        Collections.shuffle(pojoList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }, new com.android.volley.Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(jsonObjectRequest);
        }
    }

    public void fillList()
    {
        listView.setAdapter(mAdapter);
    }

    private void showMessage(String message)
    {
        Toast.makeText(Recommended.this, message,Toast.LENGTH_LONG).show();
    }
}
