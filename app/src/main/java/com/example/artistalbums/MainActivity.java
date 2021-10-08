package com.example.artistalbums;

import static com.example.artistalbums.playService.mediaPlayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    //    implementation group: 'cz.msebera.android', name: 'httpclient', version: '4.5.8'

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    RecyclerView parentRecyclerView;
    LinearLayout linearLayout;
    LocalBroadcastManager lbm;
    ListView listView;
    FloatingActionButton floatingActionButton,floatingActionButton2,floatingActionButton3;
    SeekBar seekBar;
    TextView textView,textView2,textView3,textView4;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentRecyclerView = findViewById(R.id.Parent_recyclerView);
        linearLayout = findViewById(R.id.LinearLayout);
        imageView = findViewById(R.id.imageView);
        textView2 = findViewById(R.id.textView2);
        textView = findViewById(R.id.textView3);
        textView3 = findViewById(R.id.textView4);
        textView4 = findViewById(R.id.textView);
        seekBar = findViewById(R.id.seekBar2);
        listView = findViewById(R.id.list);
        floatingActionButton2 = findViewById(R.id.floatingActionButton);
        floatingActionButton = findViewById(R.id.floatingActionButton2);
        floatingActionButton3 = findViewById(R.id.floatingActionButton3);

        linearLayout.setVisibility(View.INVISIBLE);

        LinearLayoutManager parentLayoutManager = new LinearLayoutManager(MainActivity.this);
        parentRecyclerView.setLayoutManager(parentLayoutManager);

        ArrayList<ParentModel> parentModelArrayList = new ArrayList<>();
        ParentRecyclerViewAdapter ParentAdapter = new ParentRecyclerViewAdapter(parentModelArrayList, MainActivity.this);
        parentRecyclerView.setAdapter(ParentAdapter);

        sharedpreferences = getSharedPreferences("" + R.string.app_name, MODE_PRIVATE);
        editor = sharedpreferences.edit();

        if(sharedpreferences.getString("1","1").equals("1")){
            floatingActionButton.setEnabled(false);
            floatingActionButton2.setEnabled(false);
            floatingActionButton3.setEnabled(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, playService.class));
        }else {
            startService(new Intent(this, playService.class));
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here

            HttpURLConnection conn;
            try {
                String urlString = "https://docs.google.com/spreadsheets/d/1VxQruR4Yt1Ive6qLqQZ2iV7qCr4x9GRL49yA9XM5GP8/export?format=csv";

                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                InputStream in = conn.getInputStream();
                if(conn.getResponseCode() == 200)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String inputLine;

                    while ((inputLine = br.readLine()) != null) {
                        String[] AS = inputLine.split("ALBUM");
                        String artist = AS[0].replace(",","");
                        Set<String> set1 = new HashSet<>();

                        for(String as : AS){
                            String albumName = " ",albumImage = " ";
                            Set<String> set = new HashSet<>();
                            String[] AS1 = as.split(",");
                            if(AS1.length>1){
                                albumName = AS1[1];
                                albumImage = AS1[AS1.length-1];
                            }
                            for(int i =2;i<AS1.length-1;i++){
                                if(i%2==0) {
                                    set.add(AS1[i]);
                                }
                                else {
                                    keepString(artist+"/"+albumName+"/"+AS1[i-1],AS1[i]);
                                }
                            }
                            if(set.size()>0){
                                set1.add(albumName);
                                keepString(artist+"/"+albumName+"/image",albumImage);
                                keepStringSet(artist+"/"+albumName,set);
                            }
                        }
                        keepStringSet(artist,set1);
                        parentModelArrayList.add(new ParentModel(artist));
                    }
                    //runOnUiThread(() -> parentRecyclerView.setAdapter(ParentAdapter));
                }

            }catch (Exception e){
                keepString("text", e.getMessage());
            }


            handler.post(() -> {
                //UI Thread work here
                parentRecyclerView.setAdapter(ParentAdapter);
            });
        });


    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String str = intent.getStringExtra("key");
                switch (str){
                    case "playImg":
                        floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        break;

                    case "pauseImg":
                        floatingActionButton.setImageResource(R.drawable.ic_baseline_pause_24);
                        break;

                    case "enable":
                        floatingActionButton.setEnabled(true);
                        floatingActionButton2.setEnabled(true);
                        floatingActionButton3.setEnabled(true);
                        break;

                    case "visibility":
                        parentRecyclerView.setVisibility(View.INVISIBLE);
                        linearLayout.setVisibility(View.VISIBLE);
                        String album = sharedpreferences.getString("album","");
                        Picasso.get()
                                .load(sharedpreferences.getString(album+"/image",""))
                                .into(imageView);
                        String[] splitStr = album.split("/");
                        textView2.setText(splitStr[1]);
                        Set<String> set = sharedpreferences.getStringSet(album, null);
                        ArrayList<String> albumItems = new ArrayList<>(set);
                        Collections.sort(albumItems);
                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, albumItems){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view =super.getView(position, convertView, parent);
                                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                                textView.setTextColor(Color.WHITE);
                                return view;
                            }
                        };

                        listView.setAdapter(adapter);

                        break;

                    case "PauseMax":
                        floatingActionButton.setImageResource(R.drawable.ic_baseline_pause_24);
                        int total = mediaPlayer.getDuration();
                        seekBar.setMax(total);
                        keepString("totalDuration",""+total % 3600000 / 60000+" : "+total % 3600000 % 60000 / 1000);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + str);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(receiver, new IntentFilter("UI"));

        if(mediaPlayer!=null){
            if(mediaPlayer.getCurrentPosition()>0){
                int total = mediaPlayer.getDuration();
                seekBar.setMax(total);
                keepString("totalDuration",""+total % 3600000 / 60000+" : "+total % 3600000 % 60000 / 1000);
            }
            if(mediaPlayer.isPlaying())
                floatingActionButton.setImageResource(R.drawable.ic_baseline_pause_24);
        }else {
            floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }

        floatingActionButton.setOnClickListener(view -> playService.getInstance().playpause(MainActivity.this));

        floatingActionButton2.setOnClickListener(view -> playService.getInstance().prev(MainActivity.this));

        floatingActionButton3.setOnClickListener(view -> playService.getInstance().next(MainActivity.this));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(mediaPlayer!=null) {
                    int duration = mediaPlayer.getCurrentPosition();
                    keepString("currentDuration",""+duration % 3600000 / 60000+" : "+duration % 3600000 % 60000 / 1000);
                    seekBar.setProgress(duration);
                }else {
                    keepString("currentDuration","0 : 0");
                    keepString("totalDuration","0 : 0");
                    seekBar.setProgress(0);
                }
                textView3.setText(String.format("   %s", sharedpreferences.getString("currentDuration", "0 : 0")));
                textView4.setText(sharedpreferences.getString("totalDuration", "0 : 0"));
                textView.setText(sharedpreferences.getString("text", " "));
                handler.postDelayed(this, 500);
            }
        }, 0);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) if(mediaPlayer!=null) mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            String album = sharedpreferences.getString("album","");

            String topic = ""+adapterView.getItemAtPosition(i);

            keepString("topic",topic);
            keepString("subject", album);
            keepString("image",sharedpreferences.getString(album+"/image",""));
            keepString("url",sharedpreferences.getString(album+"/"+topic,""));

            playService.getInstance().playpause(MainActivity.this);
        });

    }

    private void keepStringSet(String keyStr1, Set<String> valueStr1) {
        editor.putStringSet(keyStr1, valueStr1);
        editor.apply();
    }

    private void keepString(String keyStr1, String valueStr1) {
        editor.putString(keyStr1, valueStr1);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if(linearLayout.getVisibility()==View.VISIBLE){
            parentRecyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lbm.unregisterReceiver(receiver);
    }
}