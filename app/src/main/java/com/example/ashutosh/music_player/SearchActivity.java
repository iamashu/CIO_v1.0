package com.example.ashutosh.music_player;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ashutosh.music_player.ITunes.Adapter.CustomAdapter;
import com.example.ashutosh.music_player.ITunes.Model.Pojo;
import com.example.ashutosh.music_player.SoundCloud.Config;
import com.example.ashutosh.music_player.SoundCloud.SCService2;
import com.example.ashutosh.music_player.SoundCloud.SCService3;
import com.example.ashutosh.music_player.SoundCloud.SoundCloud;
import com.example.ashutosh.music_player.SoundCloud.Track;
import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
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
    private TextView mSelectedTrackTitle ;
    private ImageView mSelectedTrackImage ;
    public static MediaPlayer mMediaPlayer ;
    public static ImageView mPlayerControl ;
    private ImageView mforward ;
    public SCService3 scService3 ;
    private Toolbar tb ;
    private int duration ;
    private float offset ;
    private boolean expanded = true;
    String s ;
    private String imgurl, tit, arti,k,url ;
    private Boolean dBle ;
    private ArrayList<String> al ;
    private ArrayList<String> artists ;
    private ArrayList<String> genres ;
    private ListView listView ;
    String email = "" ;
    private ArrayList<Pojo> pojoList = new ArrayList<Pojo>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_search);

        View vtb = findViewById(R.id.viewT4) ;
        tb = (Toolbar) vtb.findViewById(R.id.bar_player) ;
        tb.setVisibility(View.GONE);

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

        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    addFromEditText();
                    return true ;
                }
                return false ;
            }

        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                addFromEditText();
            }
        });

        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, Player.class) ;
                intent.putExtra("img_url", imgurl) ;
                intent.putExtra("title", tit) ;
                intent.putExtra("arti", arti) ;
                intent.putExtra("dura", duration) ;
                intent.putExtra("tim", k) ;
                intent.putExtra("url", url) ;
                intent.putExtra("dbl", dBle) ;
                intent.putExtra("intent", 1) ;
                startActivity(intent);
            }
        });




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                final String s = pojoList.get(position).getTrackName() ;
                final String artist ;
                String t = pojoList.get(position).getArtistName() ;
                if(t.indexOf(',') >=0)
                    artist= t.substring(0, t.indexOf(",")) ;
                else if(t.indexOf('&') >=0)
                    artist = t.substring(0,t.indexOf("&"));
                else
                    artist = t;
                AccessToken accessToken = AccessToken.getCurrentAccessToken() ;
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

                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response) ;
                            boolean success = jsonObject.getBoolean("success") ;
                            if(success)
                            {
                                System.out.println("Successful");
                            }
                            else
                            {
                                System.out.println("Not Successful");
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                } ;

                SongPushRequest songPushRequest = new SongPushRequest(artist, email, responseListener) ;
                RequestQueue queue = Volley.newRequestQueue(SearchActivity.this) ;
                queue.add(songPushRequest) ;

                AudioManager manager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE) ;
                if(manager.isMusicActive())
                {
                    Home.mMediaPlayer.pause();
                    Home.mPlayerControl.setImageResource(R.drawable.ic_play);
                }
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
                            Picasso.with(SearchActivity.this).load(pojoList.get(position).getImageView()).into(mSelectedTrackImage);

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


        iv.animate().translationX(0f).setDuration(duration).setInterpolator(interp) ;

    }

    @Override
    public void onBackPressed()
    {
        if(mMediaPlayer.isPlaying())
        {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        Intent intent = new Intent(SearchActivity.this, Home.class) ;
        startActivity(intent);
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
            if(Player.pp != null)
                Player.pp.setImageResource(R.drawable.ic_play);
        }
        else
        {
            mMediaPlayer.start();
            mPlayerControl.setImageResource(R.drawable.ic_pause);
            if(Player.pp != null)
                Player.pp.setImageResource(R.drawable.ic_pause);
            mforward.setImageResource(R.drawable.forward3);
        }
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

    public void addFromEditText()
    {
        View view = getCurrentFocus() ;
        if(view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
        s = text.getText().toString() ;
        pojoList.clear();
        mAdapter.notifyDataSetChanged();
        getData(s);
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
