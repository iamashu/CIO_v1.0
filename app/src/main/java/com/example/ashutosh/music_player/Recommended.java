package com.example.ashutosh.music_player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.ashutosh.music_player.SoundCloud.SCService3;
import com.example.ashutosh.music_player.SoundCloud.SoundCloud;
import com.example.ashutosh.music_player.SoundCloud.Track;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Recommended extends AppCompatActivity
{
    private CustomAdapter mAdapter ;
    public ArrayList<String> artists ;
    public ArrayList<String> genres ;
    public ArrayList<String> titles ;
    private ListView listView ;
    private Toolbar tb ;
    private TextView mSelectedTrackTitle ;
    private ImageView mSelectedTrackImage ;
    private MediaPlayer mMediaPlayer ;
    private ImageView mPlayerControl ;
    private ImageView mforward ;
    public SCService3 scService3 ;
    private ArrayList<Pojo> pojoList = new ArrayList<Pojo>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recommended);

        View vtb = findViewById(R.id.viewT3) ;
        tb = (Toolbar) vtb.findViewById(R.id.bar_player) ;
        tb.setVisibility(View.GONE);

        View v1 = findViewById(R.id.view1) ;
        listView = (ListView) v1.findViewById(R.id.track_list_view) ;
        mAdapter = new CustomAdapter(this,R.layout.item, pojoList) ;
        artists = getIntent().getStringArrayListExtra("artist") ;



        mSelectedTrackTitle = (TextView) vtb.findViewById(R.id.selected_track_title) ;
        mSelectedTrackImage = (ImageView) vtb.findViewById(R.id.selected_track_image) ;
        mPlayerControl = (ImageView) vtb.findViewById(R.id.player_control) ;
        mforward = (ImageView) vtb.findViewById(R.id.forward) ;

        mPlayerControl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                togglePlayPause();
            }


        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build() ;

        scService3 = SoundCloud.getService3() ;

        mMediaPlayer = new MediaPlayer() ;
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                togglePlayPause() ;
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerControl.setImageResource(R.drawable.ic_play);
            }
        });

        mforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + 30000);
            }
        });

      //  genres = getIntent().getStringArrayListExtra("genre") ;
      //  titles = new ArrayList<String>() ;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                final String s = pojoList.get(position).getTrackName() ;
                scService3.getRecentTracks(s).enqueue(new Callback<List<Track>>() {
                    @Override
                    public void onResponse(Call<List<Track>> call, retrofit2.Response<List<Track>> response) {
                        if(response.isSuccessful())
                        {
                            List<Track> tracks = response.body() ;
                            Track track = tracks.get(0) ;
                            mSelectedTrackTitle.setText(s);
                            Picasso.with(Recommended.this).load(pojoList.get(position).getImageView()).into(mSelectedTrackImage);

                            if(mMediaPlayer.isPlaying())
                            {
                                mMediaPlayer.stop();
                                mMediaPlayer.reset();
                            }

                            try
                            {
                                tb.setVisibility(View.VISIBLE);
                                mMediaPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
                                mMediaPlayer.prepareAsync();

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            showMessage("Error code " + response.code()) ;
                        }
            }

                    @Override
                    public void onFailure(Call<List<Track>> call, Throwable t) {

                    }
                }) ;
        }
        }) ;

        listView.setAdapter(mAdapter);

        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void togglePlayPause()
    {
        if(mMediaPlayer.isPlaying())
        {
            mMediaPlayer.pause();
            mPlayerControl.setImageResource(R.drawable.ic_play);
        }
        else
        {
            mMediaPlayer.start();
            mPlayerControl.setImageResource(R.drawable.ic_pause);
            mforward.setImageResource(R.drawable.forward3);
        }
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
