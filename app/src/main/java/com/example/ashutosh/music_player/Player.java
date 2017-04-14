package com.example.ashutosh.music_player;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Player extends AppCompatActivity {

    SeekBar sb ;
    private String img, title, artist ;
    private ImageView imv ;
    private TextView tv ;
    private TextView arti ;
    private String duration ;
    private ImageView pr,pp,pf ;
    private TextView tim1, tim2 ;
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
        tim1 = (TextView) findViewById(R.id.tim1) ;
        tim2 = (TextView) findViewById(R.id.tim2) ;

        pr.setImageResource(R.drawable.rewind);
        pp.setImageResource(R.drawable.pause_w);
        pf.setImageResource(R.drawable.forward);

        final SeekBar sb = (SeekBar) findViewById(R.id.sb) ;
        sb.setProgress(0);
        Bundle extras = getIntent().getExtras() ;
        if(extras != null)
        {
            img = extras.getString("img_url") ;
            title = extras.getString("title") ;
            artist = extras.getString("arti") ;
            duration = extras.getString("tim") ;
        }
        img = img.replace("100x100", "512x512") ;
        tv.setText(title);
        arti.setText(artist);

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                sb.setProgress(0);
                tim1.setText("0:00");
                tim2.setText(duration);
                int totalDuration = SearchActivity.mMediaPlayer.getDuration() ;
                int currentPosition = 0 ;
                sb.setMax(totalDuration);
                while(currentPosition < totalDuration)
                {
                    try {
                        sleep(500) ;
                        currentPosition = SearchActivity.mMediaPlayer.getCurrentPosition() ;
                        sb.setProgress(currentPosition);
                    } catch (InterruptedException e) {
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
                SearchActivity.mMediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        Picasso.with(Player.this).load(img).into(imv);

        pr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.mMediaPlayer.seekTo(SearchActivity.mMediaPlayer.getCurrentPosition() - 30000);
            }
        });

        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SearchActivity.mMediaPlayer.isPlaying())
                {
                    SearchActivity.mMediaPlayer.pause();
                    pp.setImageResource(R.drawable.play_w);
                }
                else
                {
                    SearchActivity.mMediaPlayer.start();
                    pp.setImageResource(R.drawable.pause_w);
                }
            }
        });

        pf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.mMediaPlayer.seekTo(SearchActivity.mMediaPlayer.getCurrentPosition() + 30000);
            }
        });
    }

}
