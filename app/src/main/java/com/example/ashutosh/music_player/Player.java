package com.example.ashutosh.music_player;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Player extends AppCompatActivity {

    private String img, title, artist ;
    private ImageView imv ;
    private TextView tv ;
    private TextView arti ;
    private String duration, url ;
    public static ImageView pr,pp,pf, fav, dl ;
    private TextView tim1, tim2 ;
    private Boolean dBle ;
    private int intentit ;
    Thread updateSeekBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        imv = (ImageView) findViewById(R.id.imv) ;
        tv = (TextView) findViewById(R.id.tvt) ;
        arti = (TextView) findViewById(R.id.tva) ;
        pr = (ImageView) findViewById(R.id.pr) ;
        pp = (ImageView) findViewById(R.id.pp) ;
        pf = (ImageView) findViewById(R.id.pf) ;
        fav = (ImageView) findViewById(R.id.fav) ;
        dl = (ImageView) findViewById(R.id.dl) ;

        pr.setImageResource(R.drawable.rewind);
        pp.setImageResource(R.drawable.pause_w);
        pf.setImageResource(R.drawable.forward);
        fav.setImageResource(R.drawable.fav_border);
        dl.setImageResource(R.drawable.download);

        Bundle extras = getIntent().getExtras() ;
        if(extras != null)
        {
            img = extras.getString("img_url") ;
            title = extras.getString("title") ;
            artist = extras.getString("arti") ;
            duration = extras.getString("tim") ;
            url = extras.getString("url") ;
            dBle = extras.getBoolean("dbl");
            intentit = extras.getInt("intent") ;
        }
        img = img.replace("100x100", "512x512") ;
        tv.setText(title);
        arti.setText(artist);
        if(!dBle)
        {
            dl.setImageAlpha(70);
        }
  /*      updateSeekBar = new Thread() {
            @Override
            public void run() {
                sb.setProgress(0);
                int totalDuration = 0 ;
                tim1.setText("0:00");
                tim2.setText(duration);
                if(intentit == 1)
                    totalDuration= SearchActivity.mMediaPlayer.getDuration() ;
                else if(intentit == 2)
                    totalDuration= Recommended.mMediaPlayer.getDuration() ;
                int currentPosition = 0 ;
                sb.setMax(totalDuration);
                while(currentPosition < totalDuration)
                {
                    try {
                        sleep(500) ;
                        if(intentit == 1)
                            currentPosition = SearchActivity.mMediaPlayer.getCurrentPosition() ;
                        else if(intentit == 2)
                            currentPosition = Recommended.mMediaPlayer.getCurrentPosition() ;
                        sb.setProgress(currentPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } ;
        updateSeekBar.start();

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int minutes = (int) Math.floor(progress/60000) ;
                String t = String.valueOf((int)(progress % 60000)/1000) ;
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
                tim1.setText(a);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(intentit == 1)
                    SearchActivity.mMediaPlayer.seekTo(seekBar.getProgress());
                else if(intentit == 2)
                    Recommended.mMediaPlayer.seekTo(seekBar.getProgress());
            }
        }); */
        Picasso.with(Player.this).load(img).into(imv);

        pr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intentit == 1)
                    SearchActivity.mMediaPlayer.seekTo(SearchActivity.mMediaPlayer.getCurrentPosition() - 30000);
                else if(intentit == 2)
                    Recommended.mMediaPlayer.seekTo(Recommended.mMediaPlayer.getCurrentPosition() - 30000);
            }
        });

        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intentit == 1) {
                    if (SearchActivity.mMediaPlayer.isPlaying()) {
                        SearchActivity.mMediaPlayer.pause();
                        SearchActivity.mPlayerControl.setImageResource(R.drawable.ic_play);
                        pp.setImageResource(R.drawable.play_w);
                    } else {
                        SearchActivity.mMediaPlayer.start();
                        SearchActivity.mPlayerControl.setImageResource(R.drawable.ic_pause);
                        pp.setImageResource(R.drawable.pause_w);
                    }
                }
                else if(intentit == 2) {
                    if (Recommended.mMediaPlayer.isPlaying()) {
                        Recommended.mMediaPlayer.pause();
                        Recommended.mPlayerControl.setImageResource(R.drawable.ic_play);
                        pp.setImageResource(R.drawable.play_w);
                    } else {
                        Recommended.mMediaPlayer.start();
                        Recommended.mPlayerControl.setImageResource(R.drawable.ic_pause);
                        pp.setImageResource(R.drawable.pause_w);
                    }
                }
            }
        });

        pf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intentit == 1)
                    SearchActivity.mMediaPlayer.seekTo(SearchActivity.mMediaPlayer.getCurrentPosition() + 30000);
                else if(intentit == 2)
                    Recommended.mMediaPlayer.seekTo(Recommended.mMediaPlayer.getCurrentPosition() + 30000);
            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fav.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.fav_border).getConstantState())
                {
                    fav.setImageResource(R.drawable.favw);
                    TastyToast.makeText(getApplicationContext(), "Added to Favourites !", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS) ;
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try
                            {
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }


                    };

                    FavRequest favRequest = new FavRequest(tv.getText().toString(), responseListener) ;
                    RequestQueue queue = Volley.newRequestQueue(Player.this) ;
                    queue.add(favRequest) ;
                }
                else
                {
                    fav.setImageResource(R.drawable.fav_border);
                    TastyToast.makeText(getApplicationContext(), "Removed from Favourites !", TastyToast.LENGTH_SHORT, TastyToast.ERROR) ;
                }
            }
        });

        dl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dBle) {
                    new DownloadFileFromURL().execute(url.replace("stream", "download"));
                }
                else
                    Toast.makeText(getApplicationContext(), "Not available for Download.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class DownloadFileFromURL extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            URL u = null;
            InputStream is = null;

            try {
                u = new URL(f_url[0]);
                is = u.openStream();
                HttpURLConnection huc = (HttpURLConnection)u.openConnection();//to know the size of video
                int size = huc.getContentLength();

                if(huc != null){
                    String fileName = "FILE2.mp3";
                    String storagePath = Environment.getExternalStorageDirectory().toString();
                    File f = new File(storagePath,fileName);

                    FileOutputStream fos = new FileOutputStream(f);
                    byte[] buffer = new byte[1024];
                    long total = 0;
                    int len1 = 0;
                    if(is != null){
                        while ((len1 = is.read(buffer)) > 0) {
                            total+=len1;
                            publishProgress((int)((total*100)/size));
                            fos.write(buffer,0, len1);
                        }
                    }
                    if(fos != null){
                        fos.close();
                    }
                }
            }catch (Exception mue) {
                mue.printStackTrace();
            }
            finally {
                try {
                    if(is != null){
                        is.close();
                    }
                }catch (IOException ioe) {
                    // just going to ignore this one
                }
            }
            return "";
        }


        @Override
        protected void onPostExecute(String file_url) {


        }

    }


}
