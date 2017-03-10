package com.example.ashutosh.music_player;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashutosh.music_player.SoundCloud.Config;
import com.example.ashutosh.music_player.SoundCloud.SCService2;
import com.example.ashutosh.music_player.SoundCloud.SCTrackAdapter;
import com.example.ashutosh.music_player.SoundCloud.SoundCloud;
import com.example.ashutosh.music_player.SoundCloud.Track;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity
{
    private List<Track> mListItems ;
    private SCTrackAdapter mAdapter ;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
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
        final ListView listView = (ListView) findViewById(R.id.track_list_view) ;
        mAdapter = new SCTrackAdapter(this,mListItems) ;
        listView.setAdapter(mAdapter);

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
                scService.getRecentTracks(s).enqueue(new Callback<List<Track>>() {
                    @Override
                    public void onResponse(Call<List<Track>> call, retrofit2.Response<List<Track>> response) {
                        if(response.isSuccessful())
                        {
                            List<Track> tracks = response.body() ;
                            loadTracks(tracks) ;
                        }
                        else
                        {
                            showMessage("Error code " + response.code()) ;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Track>> call, Throwable t) {
                        showMessage(" Network Error: " + t.getMessage()) ;
                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Track track = mListItems.get(position) ;
                s = track.getID() ;
                String t = track.getTitle() ;
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
        });


        iv.animate().translationX(0f).setDuration(duration).setInterpolator(interp) ;

    }


     private boolean getITunesSongInfo(String t, String s) throws IOException
     {
        final String URLPath = "https://itunes.apple.com/search?term=" + t.replace(" " , "+") ;
        final HttpURLConnection request ;
         URL url = new URL(URLPath) ;
         request = (HttpURLConnection) url.openConnection() ;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    request.connect();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();


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

        if(!genres.contains(a))
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
