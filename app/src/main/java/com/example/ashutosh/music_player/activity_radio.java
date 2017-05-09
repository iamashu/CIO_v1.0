package com.example.ashutosh.music_player ;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;

public class activity_radio extends AppCompatActivity {

    ImageView b_play;

    MediaPlayer mediaPlayer;

    boolean prepared = false;
    boolean started = false;

    String stream = "www.youtube.com/embed/1YBl3Zbt80A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_radio);

        b_play = (ImageView) findViewById(R.id.b_play);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        new PlayerTask().execute(stream);

        b_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(started)
                {
                    started = false;
                    mediaPlayer.pause();

                }
                else
                {
                    started = true;
                    mediaPlayer.start();
                }

            }
        });
    }

    class PlayerTask extends AsyncTask<String, Void, Boolean>
    {
        protected Boolean doInBackground(String... strings)
        {
            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return prepared;
        }

        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            mediaPlayer.start();
        }
    }

    protected void onPause()
    {
        super.onPause();
        if(started)
        {
            mediaPlayer.pause();
        }
    }

    protected void onResume()
    {
        super.onResume();
        if(started)
        {
            mediaPlayer.start();
        }
    }

    protected void onDestroy()
    {
        super.onDestroy();
        if(prepared)
        {
            mediaPlayer.release();
        }
    }
}