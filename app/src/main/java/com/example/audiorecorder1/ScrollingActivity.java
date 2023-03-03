package com.example.audiorecorder1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ScrollingActivity extends AppCompatActivity {

    private TextView songName,currentTime, durationTime;
    private ImageView pauseImage, nextImage, previousImage;
    private SeekBar seekBar;

    private com.example.audiorecorder1.ForRecyclerView.Adapter adapter;
    private RecordDatabase recordDatabase;
    private Handler handler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        init();
    }

    private void init() {
        songName = findViewById(R.id.night_we_met);
        currentTime = findViewById(R.id.currentTime);
        durationTime = findViewById(R.id.durationTime);

        pauseImage = findViewById(R.id.pause_image);
        nextImage = findViewById(R.id.nextImage);
        previousImage = findViewById(R.id.previousImage);

        seekBar = findViewById(R.id.seekBar);

        recordDatabase = RecordDatabase.getInstance(getApplication());
    }

    private void setResources() {
    }
}