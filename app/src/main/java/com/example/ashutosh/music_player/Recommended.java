package com.example.ashutosh.music_player;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ashutosh.music_player.SoundCloud.Config;
import com.example.ashutosh.music_player.SoundCloud.SCService3;
import com.example.ashutosh.music_player.SoundCloud.SCTrackAdapter;
import com.example.ashutosh.music_player.SoundCloud.SoundCloud;
import com.example.ashutosh.music_player.SoundCloud.Track;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Recommended extends AppCompatActivity {

    private List<Track> mListItems ;
    private SCTrackAdapter mAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended);

        ArrayList<String> tags = //getIntent().getStringArrayListExtra("tagList") ;
                 new ArrayList<String>() ;
        //tags.add("arijit") ;
        tags.add("romantic");
        tags.add("sonu") ;

        mListItems = new ArrayList<Track>() ;

        View view = findViewById(R.id.view1) ;

        ListView listView = (ListView) view.findViewById(R.id.track_list_view) ;
        mAdapter = new SCTrackAdapter(this,mListItems) ;
        listView.setAdapter(mAdapter);

        Iterator i = tags.iterator() ;
        while (i.hasNext())
        {
            System.out.println(i.next());
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build() ;

        final SCService3 scService = SoundCloud.getService3();

        scService.getRecentTracks(tags).enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
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
