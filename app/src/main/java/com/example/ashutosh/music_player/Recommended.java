package com.example.ashutosh.music_player;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ashutosh.music_player.SoundCloud.Config;
import com.example.ashutosh.music_player.SoundCloud.SCService3;
import com.example.ashutosh.music_player.SoundCloud.SCTrackAdapter;
import com.example.ashutosh.music_player.SoundCloud.SoundCloud;
import com.example.ashutosh.music_player.SoundCloud.Track;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Recommended extends AppCompatActivity {

    private List<Track> mListItems ;
    private SCTrackAdapter mAdapter ;
    public ArrayList<String> artists ;
    public ArrayList<String> genres ;
    public ArrayList<String> titles ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build() ;
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_recommended);

        artists = getIntent().getStringArrayListExtra("artist") ;
        genres = getIntent().getStringArrayListExtra("genre") ;
        titles = new ArrayList<String>() ;

        try
        {
            getSongs();
            Iterator i = titles.iterator() ;
            while (i.hasNext())
            {
                System.out.println(i.next());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        mListItems = new ArrayList<Track>() ;

        View view = findViewById(R.id.view1) ;

        ListView listView = (ListView) view.findViewById(R.id.track_list_view) ;
        mAdapter = new SCTrackAdapter(this,mListItems) ;
        listView.setAdapter(mAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build() ;

        final SCService3 scService = SoundCloud.getService3();


    }


    private void getSongs() throws IOException
    {
        for(String s : artists)
        {
            for(String t : genres)
            {
                String URLPath = "https://itunes.apple.com/search?term=" + s.replace(" " , "+") + t.replace(" " , "+")  ;
                URL url = new URL(URLPath) ;
                HttpURLConnection request = (HttpURLConnection) url.openConnection() ;
                request.connect();
                JsonParser jp = new JsonParser() ;
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent(), StandardCharsets.UTF_8)) ;
                JsonObject rootJsonobj = root.getAsJsonObject() ;
                JsonPrimitive js = rootJsonobj.getAsJsonPrimitive("resultCount") ;
                if(js.getAsInt() == 0)
                {
                    break ;
                }
                else
                {
                    JsonArray arr = rootJsonobj.getAsJsonArray("results") ;
                    int i = 5 ;
                    int itunesIndex = 0 ;
                    while(i != 0)
                    {
                        rootJsonobj = arr.get(0).getAsJsonObject() ;
                        rootJsonobj = arr.get(itunesIndex).getAsJsonObject() ;
                        String a = rootJsonobj.get("trackName").toString().replace("\"","" ) ;
                        titles.add(a) ;
                        itunesIndex++ ;
                        i-- ;
                    }
                }

            }
        }

    }



    private void loadTracks(List<Track> tracks)
    {
        mListItems.clear();
        mListItems.addAll(tracks) ;
        mAdapter.notifyDataSetChanged();
    }

    private void showMessage(String message)
    {
        Toast.makeText(Recommended.this, message,Toast.LENGTH_LONG).show();
    }
}
