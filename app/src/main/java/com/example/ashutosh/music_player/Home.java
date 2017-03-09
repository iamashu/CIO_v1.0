package com.example.ashutosh.music_player;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;

public class Home extends AppCompatActivity {

    private static final String URL_DATA = "http://www.radiomirchi.com/more/mirchi-top-20" ;
    private RecyclerView rv ;
    private RecyclerView.Adapter adapter ;
    ArrayList<String> arrayList ;
    ArrayList<String> arrayList1 ;
    private ImageView party  ;
    private ImageView love  ;
    private ImageView sad  ;
    private ImageView dance  ;
    private ImageView motivation  ;
    private ImageView journey ;
    ScrollView sc ;
    private AnimatedCircleLoadingView animatedCircleLoadingView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        startLoading() ;
        startPercentMockThread() ;
        sc = (ScrollView) findViewById(R.id.scroll1) ;
        rv = (RecyclerView) findViewById(R.id.rview) ;
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv.setHasFixedSize(true);

        sc.setVisibility(View.GONE);
        BoomMenuButton bmb = (BoomMenuButton) findViewById(R.id.boom);


        loadRecyclerViewData() ;

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
                startActivity(intent);
            }
        });
        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Main2Activity.class) ;
                intent.putExtra("category" , "two") ;
                startActivity(intent);
            }
        });
        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Main2Activity.class) ;
                intent.putExtra("category" , "three") ;
                startActivity(intent);
            }
        });
        dance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Main2Activity.class) ;
                intent.putExtra("category" , "four") ;
                startActivity(intent);
            }
        });
        motivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Main2Activity.class) ;
                intent.putExtra("category" , "five") ;
                startActivity(intent);
            }
        });
        journey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Main2Activity.class) ;
                intent.putExtra("category" , "six") ;
                startActivity(intent);
            }
        });

        SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder() ;
        builder.normalImageRes(R.drawable.radio) ;
        bmb.addBuilder(builder);
        SimpleCircleButton.Builder builder1 = new SimpleCircleButton.Builder() ;
        builder1.normalImageRes(R.drawable.power) ;
        bmb.addBuilder(builder1);
        SimpleCircleButton.Builder builder2 = new SimpleCircleButton.Builder() ;
        builder2.normalImageRes(R.drawable.recom) ;
        bmb.addBuilder(builder2);

        builder.listener(new OnBMClickListener() {
            @Override
            public void onBoomButtonClick(int index) {
                startActivity(new Intent(getApplicationContext(), activity_radio.class));
            }
        }) ;

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
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        }) ;

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
                            arrayList = new ArrayList<String>() ;
                            arrayList1 = new ArrayList<String>() ;
                            Iterator i1 = arr1.listIterator() ;

                            while(i1.hasNext())
                            {
                                arrayList.add(i1.next().toString().replace("<h2>", "").replace("</h2>", "").replace("Video", "").replace("Song", "")) ;
                            }
                            for(Element el : arr2)
                            {
                                    String s = "http://www.radiomirchi.com" + el.attr("src");
                                    arrayList1.add(s) ;
                            }

                            adapter = new MyAdapter(arrayList,arrayList1,getApplicationContext()) ;
                            rv.setAdapter(adapter);
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

}
