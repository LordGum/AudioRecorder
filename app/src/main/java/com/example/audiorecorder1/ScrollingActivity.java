package com.example.audiorecorder1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private TextView songName,currentTime, durationTime;
    private ImageView pauseImage, nextImage, previousImage;
    private SeekBar seekBar;

    private int songPosition;
    static MediaPlayer mediaPlayer;
    List<Record> recordList;

    private RecordDatabase recordDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        init();

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        setResources_startMediaPlayer();
        clickPlayButton();
    }

    private void init() {
        songName = findViewById(R.id.night_we_met);
        currentTime = findViewById(R.id.currentTime);
        durationTime = findViewById(R.id.durationTime);

        pauseImage = findViewById(R.id.pause_image);
        nextImage = findViewById(R.id.nextImage);
        previousImage = findViewById(R.id.previousImage);

        seekBar = findViewById(R.id.seekBar);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                recordDatabase = RecordDatabase.getInstance(getApplication());
                recordList = recordDatabase.recordsDao().getRecords();
                if( recordList == null) {
                    Log.d("ScrollingActivity", "список пуст, это грустно");
                }
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void setResources_startMediaPlayer() {
        Intent intent = getIntent();
        songPosition = intent.getIntExtra("position", 0);
        if(songPosition > 0) {
            Log.d("ScrollingActivity", "позиция больше 0");
        }
        if(recordList== null) {
            Log.d("ScrollingActivity", "recordList ПУСТ");
        }
        Record record = recordList.get(songPosition);

        File file = new File(record.getRecPath());
        Uri uri = Uri.parse(file.toString());

        songName.setText(record.getRecName());
        Long time = Long.parseLong(record.getRecDuration());
        durationTime.setText(durationTime(time));

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
    }

    private void clickPlayButton() {
        pauseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    pauseImage.setBackgroundResource(R.drawable.ic_play_scrolling_activity);
                    mediaPlayer.pause();
                }
                else {
                    pauseImage.setBackgroundResource(R.drawable.ic_pause_scrolling_activity);
                    mediaPlayer.start();
                }
            }
        });
    }

    private String durationTime(Long time) {

        String sec = String.valueOf((time / 1000) % 60);
        String min = String.valueOf((time / 60_000) % 60);
        String hour = String.valueOf((time / 3_600_000));

        if(min.length() < 2) {
            min = "0" + min;
        }
        if(sec.length() < 2) {
            sec = "0" + sec;
        }

        if(hour.equals("0")) {
            return min + ":" + sec;
        }
        else {
            return hour + ":" + min + ":" + sec;
        }
    }
}