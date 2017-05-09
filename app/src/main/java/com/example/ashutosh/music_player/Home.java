package com.example.ashutosh.music_player;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ashutosh.music_player.SoundCloud.Config;
import com.example.ashutosh.music_player.SoundCloud.SCService3;
import com.example.ashutosh.music_player.SoundCloud.SoundCloud;
import com.example.ashutosh.music_player.SoundCloud.Track;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends AppCompatActivity implements ItemClickListener {

    private static final String URL_DATA = "http://www.radiomirchi.com/more/mirchi-top-20" ;
    private RecyclerView rv ;
    private MyAdapter adapter ;
    public ArrayList<String> arrayList ;
    public ArrayList<String> arrayList1 ;
    private TextView mSelectedTrackTitle ;
    private ImageView mSelectedTrackImage ;
    public static MediaPlayer mMediaPlayer ;
    public static ImageView mPlayerControl ;
    private ImageView mforward ;
    private ImageView party  ;
    private ImageView love  ;
    private ImageView sad  ;
    private ImageView dance  ;
    private ImageView motivation  ;
    private ImageView journey ;
    private ImageView isearch ;
    private Toolbar tb ;
    ScrollView sc ;
    public  SCService3 scService3 ;
    String artist = "" ;
    private AnimatedCircleLoadingView animatedCircleLoadingView ;
    public String email ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);

        View vtb = findViewById(R.id.viewT2) ;

        tb = (Toolbar) vtb.findViewById(R.id.bar_player) ;

        tb.setVisibility(View.GONE);


        Bundle extras = getIntent().getExtras() ;
        if(extras != null)
        {
            email = extras.getString("em") ;
        }

        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        startLoading() ;
        startPercentMockThread() ;
        sc = (ScrollView) findViewById(R.id.scroll1) ;
        rv = (RecyclerView) findViewById(R.id.rview) ;
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv.setHasFixedSize(true);

        mSelectedTrackTitle = (TextView) vtb.findViewById(R.id.selected_track_title) ;
        mSelectedTrackImage = (ImageView) vtb.findViewById(R.id.selected_track_image) ;
        mPlayerControl = (ImageView) vtb.findViewById(R.id.player_control) ;
        mforward = (ImageView) vtb.findViewById(R.id.forward) ;
        isearch = (ImageView) findViewById(R.id.ic_s) ;

        mPlayerControl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                togglePlayPause();
            }


        });

        sc.setVisibility(View.GONE);
        BoomMenuButton bmb = (BoomMenuButton) findViewById(R.id.boom);

        arrayList = new ArrayList<String>() ;
        arrayList1 = new ArrayList<String>() ;

        loadRecyclerViewData() ;

        adapter = new MyAdapter(arrayList,arrayList1,getApplicationContext()) ;
        rv.setAdapter(adapter);
        adapter.setClickListener(this);

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



        party = (ImageView) findViewById(R.id.iv1) ;
        love = (ImageView) findViewById(R.id.iv2) ;
        sad = (ImageView) findViewById(R.id.iv3) ;
        dance = (ImageView) findViewById(R.id.iv4) ;
        motivation = (ImageView) findViewById(R.id.iv5) ;
        journey = (ImageView) findViewById(R.id.iv6) ;

        party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Main2Activity.class) ;
                intent.putExtra("category" , "one") ;
                intent.putExtra("em", email) ;
                startActivity(intent);
            }
        });
        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Main2Activity.class) ;
                intent.putExtra("category" , "two") ;
                intent.putExtra("em", email) ;
                startActivity(intent);
            }
        });
        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Main2Activity.class) ;
                intent.putExtra("category" , "three") ;
                intent.putExtra("em", email) ;
                startActivity(intent);
            }
        });
        dance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Main2Activity.class) ;
                intent.putExtra("category" , "four") ;
                intent.putExtra("em", email) ;
                startActivity(intent);
            }
        });
        motivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Main2Activity.class) ;
                intent.putExtra("category" , "five") ;
                intent.putExtra("em", email) ;
                startActivity(intent);
            }
        });
        journey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Main2Activity.class) ;
                intent.putExtra("category" , "six") ;
                intent.putExtra("em", email) ;
                startActivity(intent);
            }
        });

        SimpleCircleButton.Builder builder1 = new SimpleCircleButton.Builder() ;
        builder1.normalImageRes(R.drawable.power) ;
        bmb.addBuilder(builder1);
        SimpleCircleButton.Builder builder2 = new SimpleCircleButton.Builder() ;
        builder2.normalImageRes(R.drawable.recom) ;
        bmb.addBuilder(builder2);
        SimpleCircleButton.Builder builder3 = new SimpleCircleButton.Builder() ;
        builder3.normalImageRes(R.drawable.lead) ;
        bmb.addBuilder(builder3);
        SimpleCircleButton.Builder builder4 = new SimpleCircleButton.Builder() ;
        builder4.normalImageRes(R.drawable.infinity) ;
        bmb.addBuilder(builder4);

        builder1.listener(new OnBMClickListener() {
            @Override
            public void onBoomButtonClick(int index) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken() ;
                if(accessToken != null)
                {
                    LoginManager.getInstance().logOut();
                }
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        }) ;
        builder2.listener(new OnBMClickListener() {
            @Override
            public void onBoomButtonClick(int index) {
                Intent intent = new Intent(getApplicationContext(), Recommended.class);
                intent.putExtra("em", email) ;
                startActivity(intent) ;
            }
        }) ;

        builder3.listener(new OnBMClickListener() {
            @Override
            public void onBoomButtonClick(int index) {
                startActivity(new Intent(getApplicationContext(), Topsongs.class));
            }
        }) ;

        builder4.listener(new OnBMClickListener() {
            @Override
            public void onBoomButtonClick(int index) {
                Intent intent = new Intent(getApplicationContext(), SimilarArtist.class);
                intent.putExtra("em", email) ;
                startActivity(intent) ;
            }
        });

        isearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("em", email) ;
                startActivity(intent) ;
            }
        });

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


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN) ;
        intent.addCategory(Intent.CATEGORY_HOME) ;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void loadRecyclerViewData()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_DATA,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            Document document = Jsoup.parse(response) ;
                            Elements arr1 = document.getElementsByTag("h2") ;
                            Elements arr2 = document.select("div.movieImg img") ;
                            Iterator i1 = arr1.listIterator() ;
                            while(i1.hasNext())
                            {
                                String a = i1.next().toString() ;
                                arrayList.add(a.toString().replace("<h2>", "").replace("</h2>", "").replace("Video", "").replace("Song", "")) ;
                            }
                            for(Element el : arr2)
                            {
                                    String s = "http://www.radiomirchi.com" + el.attr("src");
                                    arrayList1.add(s) ;
                            }


                            animatedCircleLoadingView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    animatedCircleLoadingView.setVisibility(View.GONE);
                                    FrameLayout fl = (FrameLayout) findViewById(R.id.lay) ;
                                    fl.setBackgroundColor(Color.WHITE);
                                    sc.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) ;

        RequestQueue requestQueue = Volley.newRequestQueue(this) ;
        requestQueue.add(stringRequest) ;

    }

    private void getData(String s)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://itunes.apple.com/search?term=" + s.replace(" ", "+");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    JSONArray jsonArray = response.getJSONArray("results");
                    if(jsonArray.length() != 0)
                    {
                        JSONObject object = jsonArray.getJSONObject(0);
                        String t = (object.optString("artistName", "Unknown"));
                        if(t.indexOf(',') >=0)
                            artist= t.substring(0, t.indexOf(",")) ;
                        else if(t.indexOf('&') >=0)
                            artist = t.substring(0,t.indexOf("&"));
                        else
                            artist = t;
                        com.android.volley.Response.Listener<String> responseListener = new com.android.volley.Response.Listener<String>()
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
                        RequestQueue queue = Volley.newRequestQueue(Home.this) ;
                        queue.add(songPushRequest) ;
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

    private void startLoading()
    {
        animatedCircleLoadingView.startDeterminate();
    }

    private void startPercentMockThread()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(1500);
                    for (int i=0 ; i<=100 ; i++)
                    {
                        Thread.sleep(15);
                        changePercent(i) ;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void changePercent(final int percent)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedCircleLoadingView.setPercent(percent);
            }
        });
    }

    public void resetLoading()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedCircleLoadingView.resetLoading();
            }
        });
    }

    @Override
    public void onClick(View view, final int position)
    {
        tb.setVisibility(View.VISIBLE);
        scService3.getRecentTracks(arrayList.get(position)).enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, retrofit2.Response<List<Track>> response) {
                if(response.isSuccessful())
                {
                    List<Track> tracks = response.body() ;
                    Track track = tracks.get(0) ;

                    AST ast = new AST(arrayList.get(position)) ;
                    ast.doInBackground() ;
                    mSelectedTrackTitle.setText(arrayList.get(position));
                    Picasso.with(Home.this).load(track.getArtworkURL()).into(mSelectedTrackImage);

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
                showMessage(" Network Error: " + t.getMessage()) ;
            }
        });
    }

    private void showMessage(String message)
    {
        Toast.makeText(Home.this, message,Toast.LENGTH_LONG).show();
    }

    class AST extends AsyncTask<String, Void, Object>
    {
        String song ;
        public AST(String s)
        {
           this.song = s  ;
        }
        @Override
        protected void onPreExecute()
        {
        }
        @Override
        protected Object doInBackground(String... params) {
            getData(song);
            return null ;
        }

        @Override
        protected void onPostExecute(Object result)
        {
        }
    }
}
