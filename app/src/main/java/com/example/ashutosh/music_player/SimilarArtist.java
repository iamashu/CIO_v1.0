package com.example.ashutosh.music_player;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ashutosh.music_player.ITunes.Adapter.CustomAdapter;
import com.example.ashutosh.music_player.ITunes.Model.Pojo;
import com.example.ashutosh.music_player.SoundCloud.Config;
import com.example.ashutosh.music_player.SoundCloud.SCService3;
import com.example.ashutosh.music_player.SoundCloud.SoundCloud;
import com.example.ashutosh.music_player.SoundCloud.Track;
import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SimilarArtist extends AppCompatActivity {
    List<String> artists = new ArrayList<>() ;
    public ArrayList<String> artist ;
    private CustomAdapter mAdapter ;
    private ListView listView ;
    private Toolbar tb ;
    private TextView mSelectedTrackTitle ;
    private ImageView mSelectedTrackImage ;
    public static MediaPlayer mMediaPlayer ;
    public static ImageView mPlayerControl ;
    private String imgurl, tit, arti,k,url ;
    private Boolean dBle ;
    private int duration ;
    int limit = 1 ;
    private ImageView mforward ;
    public SCService3 scService3 ;
    private ArrayList<Pojo> pojoList = new ArrayList<Pojo>() ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_similar_artist);

        ImageView imv = (ImageView) findViewById(R.id.imvsa) ;
        imv.setImageResource(R.drawable.similar);

        View vtbsa = findViewById(R.id.viewTSA) ;
        tb = (Toolbar) vtbsa.findViewById(R.id.bar_player) ;
        tb.setVisibility(View.GONE);

        View v1 = findViewById(R.id.viewSA) ;
        listView = (ListView) v1.findViewById(R.id.track_list_view) ;
        mAdapter = new CustomAdapter(this,R.layout.item, pojoList) ;
        artists = new ArrayList<String>() ;
        artist = new ArrayList<String>() ;
        AccessToken accessToken = AccessToken.getCurrentAccessToken() ;
        String email = "" ;
        if(accessToken != null)
        {
            email = accessToken.getUserId() ;
        }
        else {
            Bundle extras = getIntent().getExtras() ;
            if(extras != null)
            {
                email = extras.getString("em") ;
            }
        }

        MyAsyncTask mat = new MyAsyncTask(this) ;
        mat.doInBackground() ;


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    if(response.length() < 5)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SimilarArtist.this);
                        builder.setMessage("Please listen few songs, so that we can recommend you songs.");
                        builder.setNegativeButton("Go Back !" + "", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getApplicationContext(), Home.class));
                            }
                        }) ;
                        builder.create()
                                .show();

                    }
                    for(int i=0; i < array.length(); i++)
                    {
                        JSONObject jsonobject = array.getJSONObject(i);
                        artist.add(jsonobject.getString("artist"));
                        doWork(artist.size());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };


        SongGetRequest songGetRequest = new SongGetRequest(email, responseListener) ;
        RequestQueue queue = Volley.newRequestQueue(SimilarArtist.this) ;
        queue.add(songGetRequest) ;

        mSelectedTrackTitle = (TextView) vtbsa.findViewById(R.id.selected_track_title) ;
        mSelectedTrackImage = (ImageView) vtbsa.findViewById(R.id.selected_track_image) ;
        mPlayerControl = (ImageView) vtbsa.findViewById(R.id.player_control) ;
        mforward = (ImageView) vtbsa.findViewById(R.id.forward) ;

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
                            if(tracks.size() == 0)
                            {
                                return ;
                            }
                            Track track = tracks.get(0) ;
                            imgurl = pojoList.get(position).getImageView();
                            tit = pojoList.get(position).getTrackName() ;
                            arti = pojoList.get(position).getArtistName() ;
                            duration = track.getmDuration() ;
                            k = millisToMinutesAndSeconds(duration) ;
                            url = track.getStreamURL() ;
                            dBle = track.getDble() ;
                            mSelectedTrackTitle.setText(s);
                            Picasso.with(SimilarArtist.this).load(pojoList.get(position).getImageView()).into(mSelectedTrackImage);

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

    }

    private String millisToMinutesAndSeconds(int duration)
    {
        int minutes = (int) Math.floor(duration/60000) ;
        String t = String.valueOf((int)(duration % 60000)/1000) ;
        int seconds = Integer.parseInt(t) ;
        String a = minutes + ":" ;
        if(seconds < 10)
        {
            a = a + '0' + seconds ;
        }
        else
        {
            a = a + seconds ;
        }
        return a ;
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

    public void doWork(int size)
    {
        if(size <= 2)
            limit = 3 ;
        else if (size>2 && size <= 5)
            limit = 2;
        else
            limit = 1 ;
        url = "http://ashuthesmartest.000webhostapp.com/data.txt" ;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                artists = Arrays.asList(response.split("\n")) ;
                Iterator i = artist.iterator() ;
                List<String> arr = new ArrayList<>() ;
                while (i.hasNext())
                {
                    String l = i.next().toString() ;
                    Iterator j = artists.iterator() ;
                    while (j.hasNext())
                    {
                        String k = j.next().toString() ;
                        if(k.contains(l) )
                        {
                            arr = send(k,l);
                            doSend(arr, limit) ;
                        }
                    }
                }

            }
        }  , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) ;

        RequestQueue queue = Volley.newRequestQueue(this) ;
        queue.add(request) ;
    }

    private void doSend(List<String> arr, int limit)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        for(String s : arr)
        {
            String url = "http://itunes.apple.com/search?term=" + s.replace(" ", "+") + "&limit=" + limit ;
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
                            System.out.println(object.optString("trackName" + "\n \n "));
                            pojo.setCollectionName(object.optString("collectionName","Unknown"));
                            pojo.setImageView(object.optString("artworkUrl100","Unknown"));
                            if(! pojoList.contains(pojo))
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
        Toast.makeText(SimilarArtist.this, message,Toast.LENGTH_LONG).show();
    }

    public List<String> send(String s, String l)
    {
        String[] arr1 = s.split(",") ;
        List<String> arr = Arrays.asList(arr1) ;
        return arr ;
    }

}

class MyAsyncTask extends AsyncTask<String, Void, Object>
{
    SpotsDialog ad ;
    Context context ;
    public MyAsyncTask(Context c)
    {
        context = c ;
    }
    @Override
    protected void onPreExecute()
    {
    }
    @Override
    protected Object doInBackground(String... params) {
        ad = new SpotsDialog(context) ;
        ad.show();
        final Handler handler = new Handler() ;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ad.dismiss();
            }
        }, 5000) ;

        return null ;
    }

    @Override
    protected void onPostExecute(Object result)
    {
    }
}
