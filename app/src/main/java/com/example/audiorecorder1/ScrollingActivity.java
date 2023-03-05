package com.example.audiorecorder1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.room.Database;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private TextView songName,currentTime, durationTime;
    private ImageView pauseImage, nextImage, previousImage;
    private ImageView gifImage, noteImage;
    private SeekBar seekBar;

    private int songPosition;
    static MediaPlayer mediaPlayer;
    private List<Record> recordList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        init();


        if(recordList == null) {
            Log.d("Mistake", "ОШИБКА");

        }
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        setResources();
        startAnimation();

        clickPlayButton();
        songEnd_GoToNext();
        clickNextButton();
        clickPreviousButton();


    }

    private void init() {
        songName = findViewById(R.id.night_we_met);
        currentTime = findViewById(R.id.currentTime);
        durationTime = findViewById(R.id.durationTime);

        pauseImage = findViewById(R.id.pause_image);
        nextImage = findViewById(R.id.nextImage);
        previousImage = findViewById(R.id.previousImage);

        noteImage = findViewById(R.id.noteImage);
    }

    private void setResources() {
        Intent intent = getIntent();
        songPosition = intent.getIntExtra("position", 0);
        String path = intent.getStringExtra("path");
        String name = intent.getStringExtra("name");
        String dur = intent.getStringExtra("duration");

        File file = new File(path);
        Uri uri = Uri.parse(file.toString());

        songName.setText(name);
        Long time = Long.parseLong(dur);
        durationTime.setText(durationTime(time));

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();



        Thread thread = new Thread(new Runnable() {
            RecordDatabase recordDatabase = RecordDatabase.getInstance(getApplication());
            @Override
            public void run() {
                recordList = recordDatabase.recordsDao().getRecordList();
            }
        });
        thread.start();

        //recordList = viewModel.getList();

        workWithSeekbar();
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

    private void startAnimation() {

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(noteImage, "rotation", 0f, 360f);
        animator1.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1);
        animatorSet.start();
    }

    private void clickNextButton() {
        nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                songPosition = ( (songPosition+1)%(recordList.size()) );
                Record record1 = recordList.get(songPosition);
                File file1 = new File(record1.getRecPath());
                Uri uri1 = Uri.parse(file1.toString());

                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri1);
                mediaPlayer.start();

                songName.setText(record1.getRecName());
                Long time = Long.parseLong(record1.getRecDuration());
                durationTime.setText(durationTime(time));

                pauseImage.setBackgroundResource(R.drawable.ic_pause_scrolling_activity);
                startAnimation();
            }
        });
    }

    private void clickPreviousButton() {
        previousImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                songPosition = ( (songPosition-1)<0 ) ? (recordList.size()-1) : (songPosition-1);
                Record record2 = recordList.get(songPosition);
                File file2 = new File(record2.getRecPath());
                Uri uri2 = Uri.parse(file2.toString());

                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri2);
                mediaPlayer.start();

                songName.setText(record2.getRecName());
                Long time = Long.parseLong(record2.getRecDuration());
                durationTime.setText(durationTime(time));

                pauseImage.setBackgroundResource(R.drawable.ic_pause_scrolling_activity);
                startAnimation();
            }
        });
    }

    private void songEnd_GoToNext() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                pauseImage.setBackgroundResource(R.drawable.ic_play_scrolling_activity);
                mediaPlayer.pause();
            }
        });
    }

    private void workWithSeekbar() {
        //ниже работаю с seekBar,те обновляю
        seekBar = findViewById(R.id.seekBar);

        Thread updateSeekbar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentTimePosition = 0;

                while (currentTimePosition < totalDuration) {
                    try{
                        sleep(500);
                        currentTimePosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentTimePosition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();

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

        //обновляю время
        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Long time = Long.parseLong( String.valueOf(mediaPlayer.getCurrentPosition()));
                String currentTimeValue = durationTime(time);
                currentTime.setText(currentTimeValue);
                handler.postDelayed(this, delay);
            }
        }, delay);
    }


}