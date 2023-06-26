package com.example.soultunebygopal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.icu.text.Transliterator;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
Button playbtn,fastf,fastr,nextbtn,prevbtn;
TextView txtname,txtstar,txtstop;
SeekBar seekBar;
BarVisualizer barVisualizer;
ImageView img;
String sname;
public static final String EXTRA_NAME="song_name";
static MediaPlayer mediaPlayer;
int position;
ArrayList<File> mysongs;
Thread updateseekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(barVisualizer!=null){
            barVisualizer.release();
        }
        super.onDestroy();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("SoulTune");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_home);
        playbtn = findViewById(R.id.playbtn);
        fastf = findViewById(R.id.fastfbtn);
        fastr = findViewById(R.id.fastrbtn);
        nextbtn = findViewById(R.id.nextbtn);
        prevbtn = findViewById(R.id.prevbtn);
        txtname = findViewById(R.id.txtsn);
        txtstar = findViewById(R.id.txtstart);
        txtstop = findViewById(R.id.txtstop);
        seekBar = findViewById(R.id.seekbar);
        barVisualizer = findViewById(R.id.blast);
        img = findViewById(R.id.imagehome);

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mysongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songname = i.getStringExtra("songname");
        position = bundle.getInt("pos",0);
        txtname.setSelected(true);
        Uri uri = Uri.parse(mysongs.get(position).toString());
        sname = mysongs.get(position).getName();
        txtname.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        updateseekbar = new Thread(){
            @Override
            public void run() {
                int totalduration = mediaPlayer.getDuration();
                int currentposition = 0;
                while (currentposition < totalduration){
                    try {
                        sleep(500);
                        currentposition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentposition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekBar.setMax(mediaPlayer.getDuration());
        updateseekbar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.black),PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        final Handler handler = new Handler();
        final int delay =1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currenttime = createTime(mediaPlayer.getCurrentPosition());
                txtstar.setText(currenttime);
                String endtime = createTime(mediaPlayer.getDuration());
                txtstop.setText(endtime);
                handler.postDelayed(this,delay);
            }
        },delay);


        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    playbtn.setBackgroundResource(R.drawable.baseline_play);
                    mediaPlayer.pause();
                }
                else {
                    playbtn.setBackgroundResource(R.drawable.baseline_pause_24);
                    mediaPlayer.start();
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextbtn.performClick();
            }
        });

        int audioSessionid = mediaPlayer.getAudioSessionId();
        if(audioSessionid!=-1){
            barVisualizer.setAudioSessionId(audioSessionid);
        }

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mysongs.size());
                Uri uri = Uri.parse(mysongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                sname= mysongs.get(position).getName();
                txtname.setText(sname);
                mediaPlayer.start();
                playbtn.setBackgroundResource(R.drawable.baseline_pause_24);
                startAnimation(img);
                int audioSessionid = mediaPlayer.getAudioSessionId();
                if(audioSessionid!=-1){
                    barVisualizer.setAudioSessionId(audioSessionid);
                }
            }
        });
        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(mysongs.size()-1):(position-1);
                Uri u = Uri.parse(mysongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname= mysongs.get(position).getName();
                txtname.setText(sname);
                mediaPlayer.start();
                playbtn.setBackgroundResource(R.drawable.baseline_pause_24);
                startAnimation(img);
                int audioSessionid = mediaPlayer.getAudioSessionId();
                if(audioSessionid!=-1){
                    barVisualizer.setAudioSessionId(audioSessionid);
                }
            }
        });
        fastf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        fastr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });
    }
    public void startAnimation(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(img,"Rotation",0f,360f);
                animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public String createTime(int duration){
        String time ="";
        int minute = duration/1000/60;
        int sec = duration/1000%60;
        time+=minute+":";
        if(sec<10){
            time+="0";
        }
        time+=sec;
        return time;
    }
}