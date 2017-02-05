package com.example.ashutosh.music_player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements View.OnClickListener {

    static MediaPlayer mp ;
    ArrayList<File> mySongs ;
    int position ;
    Uri u ;
    SeekBar sb ;
    Thread updateSeekBar ;
    Button btPrev, btFB, btPlay, btFF, btNext ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btPlay = (Button) findViewById(R.id.btPlay) ;
        btFB = (Button) findViewById(R.id.btFB) ;
        btFF = (Button) findViewById(R.id.btFF) ;
        btPrev = (Button) findViewById(R.id.btPrev) ;
        btNext = (Button) findViewById(R.id.btNext) ;

        btPlay.setOnClickListener(this);
        btFF.setOnClickListener(this);
        btFB.setOnClickListener(this);
        btPrev.setOnClickListener(this);
        btNext.setOnClickListener(this);

        sb = (SeekBar) findViewById(R.id.seekBar) ;
        updateSeekBar = new Thread() {
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

        if(mp != null)
        {
            mp.stop();
            mp.release();
        }

        Intent i = getIntent() ;
        Bundle b = i.getExtras() ;
        mySongs = (ArrayList) b.getParcelableArrayList("songlist") ;
        position = b.getInt("pos",0) ;

        u = Uri.parse(mySongs.get(position).toString()) ;
        mp = MediaPlayer.create(getApplicationContext(),u) ;
        mp.start();

        sb.setMax(mp.getDuration()) ;

        updateSeekBar.start();

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

    @Override
    public void onClick(View v) {
        int id = v.getId() ;
        switch (id)
        {
            case R.id.btPlay :
                 if(mp.isPlaying())
                 {
                     btPlay.setText(">");
                     mp.pause();
                 }
                 else
                 {
                     btPlay.setText("||");
                     mp.start();
                 }
                 break ;

            case R.id.btFF :
                 mp.seekTo(mp.getCurrentPosition() + 50000);
                 break ;

            case R.id.btFB :
                 mp.seekTo(mp.getCurrentPosition() - 50000);
                 break ;

            case R.id.btNext :
                 mp.stop();
                 mp.release();
                 position = (position +1) % mySongs.size() ;
                 u = Uri.parse(mySongs.get(position).toString()) ;
                 mp = MediaPlayer.create(getApplicationContext(),u) ;
                 mp.start();
                 sb.setMax(mp.getDuration()) ;
                 break ;

            case R.id.btPrev :
                 mp.stop();
                 mp.release();
                 position = (position-1 <0) ? mySongs.size() - 1 : position - 1 ;
                 u = Uri.parse(mySongs.get(position).toString()) ;
                 mp = MediaPlayer.create(getApplicationContext(),u) ;
                 mp.start();
                 sb.setMax(mp.getDuration()) ;
                 break ;

        }
    }
}
