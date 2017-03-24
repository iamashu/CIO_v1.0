package com.example.ashutosh.music_player;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ashutosh.music_player.ITunes.Adapter.CustomAdapter;
import com.example.ashutosh.music_player.ITunes.Model.Pojo;
import com.example.ashutosh.music_player.SoundCloud.Config;
import com.example.ashutosh.music_player.SoundCloud.SCService2;
import com.example.ashutosh.music_player.SoundCloud.SoundCloud;
import com.example.ashutosh.music_player.SoundCloud.Track;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity
{
    private List<Track> mListItems ;
    private CustomAdapter mAdapter ;
    private ImageView iv ;
    private TextView text ;
    private AnimatedVectorDrawable searchToBar ;
    private AnimatedVectorDrawable barToSearch ;
    private Interpolator interp ;
    private int duration ;
    private float offset ;
    private boolean expanded = true;
    String s ;
    private ArrayList<String> al ;
    private ArrayList<String> artists ;
    private ArrayList<String> genres ;
    private ListView listView ;
    private ArrayList<Pojo> pojoList = new ArrayList<Pojo>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listView = (ListView) findViewById(R.id.track_list_view) ;
        iv = (ImageView) findViewById(R.id.search) ;
        text = (TextView) findViewById(R.id.text1) ;
        Button btn = (Button) findViewById(R.id.btn) ;
        searchToBar = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_search_to_bar) ;
        barToSearch = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_bar_to_search) ;
        interp = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in) ;
        duration = 2000 ;
        offset = -71f * (int) getResources().getDisplayMetrics().scaledDensity ;
        iv.setTranslationX(offset);
        text.setAlpha(1f);

        al = new ArrayList<String>() ;
        artists = new ArrayList<String>() ;
        genres = new ArrayList<String>() ;
        mListItems = new ArrayList<Track>() ;
     //   final ListView listView = (ListView) findViewById(R.id.track_list_view) ;
        mAdapter = new CustomAdapter(this,R.layout.item, pojoList) ;
     //   listView.setAdapter(mAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build() ;

        final SCService2 scService = SoundCloud.getService2();

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER :
                        case KeyEvent.KEYCODE_ENTER:
                             s = text.getText().toString() ;
                             return true ;
                        default:
                            break;
                    }
                }
                return false ;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = text.getText().toString() ;
                pojoList.clear();
                mAdapter.notifyDataSetChanged();
                getData(s);
            }
        });

     /*   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Track track = mListItems.get(position) ;
                System.out.println(track.getArtworkURL());
                String t = track.getTitle() ;
                String[] arr = t.split(" ") ;
                t = arr[0] + " " + arr[1] + " " + arr[2] ;
                try
                {
                    if(!getITunesSongInfo(t,s))
                        throw new Exception("Hello") ;
                    if(al.contains(s))
                    {
                        al.remove(s) ;
                        TastyToast.makeText(getApplicationContext(), "Song Removed !", TastyToast.LENGTH_SHORT, TastyToast.ERROR) ;
                    }
                    else
                    {
                        al.add(s) ;
                        TastyToast.makeText(getApplicationContext(), "Song Added !", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS) ;
                    }
                    if(al.size() == 5)
                    {
                        Intent intent = new Intent(SearchActivity.this, Recommended.class);
                        intent.putStringArrayListExtra("artist", artists) ;
                        intent.putStringArrayListExtra("genre", genres) ;
                        startActivity(intent);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }); */


        iv.animate().translationX(0f).setDuration(duration).setInterpolator(interp) ;

    }

    private void getData(String s) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://itunes.apple.com/search?term=" + s.replace(" ", "+");
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

    public void fillList()
    {
        listView.setAdapter(mAdapter);
    }


    private boolean getITunesSongInfo(String t, String s) throws IOException {
        String URLPath = "https://itunes.apple.com/search?term=" + t.replace(" " , "+") ;
        URL url = new URL(URLPath) ;
        HttpURLConnection request = (HttpURLConnection) url.openConnection() ;
        request.connect();

        JsonParser jp = new JsonParser() ;
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent(), StandardCharsets.UTF_8)) ;
        JsonObject rootJsonobj = root.getAsJsonObject() ;
        JsonPrimitive js = rootJsonobj.getAsJsonPrimitive("resultCount") ;
        if(js.getAsInt() == 0)
        {
            TastyToast.makeText(getApplicationContext(), "Song Not Found in SoundCloud Database !", TastyToast.LENGTH_SHORT, TastyToast.ERROR) ;
            return false;
        }
        JsonArray arr = rootJsonobj.getAsJsonArray("results") ;
        ArrayList<String> artist = new ArrayList<String>() ;

        try
        {
            rootJsonobj = arr.get(0).getAsJsonObject() ;
        }
        catch (IndexOutOfBoundsException e)
        {
            TastyToast.makeText(getApplicationContext(), "Song Not Found in SoundCloud Database !", TastyToast.LENGTH_SHORT, TastyToast.ERROR) ;
            al.remove(s) ;
        }

        int itunesIndex = 0 ;

        rootJsonobj = arr.get(itunesIndex).getAsJsonObject() ;
        String a = rootJsonobj.get("artistName").toString().replace("\"","" ) ;
        String b = rootJsonobj.get("primaryGenreName").toString().replace("\"","") ;

        if(!artists.contains(a))
            artists.add(a) ;

        if(!genres.contains(b))
            genres.add(b) ;

        return true ;

    }

    public void animate(View view)
    {
        if(!expanded)
        {
            iv.setImageDrawable(searchToBar);
            searchToBar.start();
            iv.animate().translationX(0f).setDuration(duration).setInterpolator(interp) ;
            text.animate().alpha(1f).setStartDelay(duration-100).setDuration(100).setInterpolator(interp) ;
        }
        else
        {
            iv.setImageDrawable(barToSearch);
            barToSearch.start();
            iv.animate().translationX(offset).setDuration(duration).setInterpolator(interp) ;
            text.setAlpha(0f);
        }
        expanded = !expanded ;
    }

    private void loadTracks(List<Track> tracks)
    {
        mListItems.clear();
        mListItems.addAll(tracks) ;
        mAdapter.notifyDataSetChanged();
    }
    private void showMessage(String message)
    {
        Toast.makeText(SearchActivity.this, message,Toast.LENGTH_LONG).show();
    }
}
