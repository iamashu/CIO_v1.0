package com.example.ashutosh.music_player;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import com.example.ashutosh.music_player.SoundCloud.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Player extends AppCompatActivity {

    static MediaPlayer mp ;
    ArrayList<File> mySongs ;
    int position ;
    Uri u ;
    SeekBar sb ;
    Thread updateSeekBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        sb = (SeekBar) findViewById(R.id.sb) ;

        try
        {
            mp = new MediaPlayer() ;
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(Main2Activity.str  + "?client_id=" + Config.CLIENT_ID);
            mp.prepareAsync();
            mp.start();
            sb.setMax(mp.getDuration()) ;
            System.out.println(Main2Activity.str);
   /*         updateSeekBar = new Thread() {
                @Override
                public void run() {
                    int totalDuration = mp.getDuration() ;
                    int currentPosition = 0 ;
                    while(currentPosition < totalDuration)
                    {
                        try {
                            sleep(500);
                            currentPosition = mp.getCurrentPosition() ;
                            sb.setProgress(currentPosition);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    //super.run();
                }
            };

            updateSeekBar.start(); */
        }
        catch (IOException e) {
            e.printStackTrace();
        }



        if(mp != null)
        {
            mp.stop();
            mp.release();
        }

        Intent i = getIntent() ;
        Bundle b = i.getExtras() ;



        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });


    }

}
