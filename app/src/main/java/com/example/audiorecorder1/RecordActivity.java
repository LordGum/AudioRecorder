package com.example.audiorecorder1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class RecordActivity extends AppCompatActivity {

    private FloatingActionButton stopRecordButton;
    private Chronometer timeRecord;
    private GifImageView gifLoading;

    private static String fileName;
    private String recordName = "";
    private MediaRecorder recorder;
    private boolean isRecording;

    private MainViewModel viewModel;


    File path = new File(
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    +
                    "/VkTestRecorder"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        initView();
        viewModel = new MainViewModel(getApplication());
        isRecording = false;

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = format.format(new Date());

        fileName = path + "/recording_" + date + ".arm";

        if(!path.exists()) {
            // этот метод создает папку
            path.mkdirs();
        }


        if(true) {
            Log.d("RecordActivity", "начинаем запись");
            try {
                startRecording();
                timeRecord.setBase(SystemClock.elapsedRealtime());
                timeRecord.start();
                isRecording = true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.toast_cant_recording, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Log.d("RecordActivity", "не можем начать ");
        }

        clickStopButton();


    }

    private void initView() {
        stopRecordButton = findViewById(R.id.stopRecordButton);
        timeRecord = findViewById(R.id.timeRecord);
        gifLoading = findViewById(R.id.gifLoading);
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("RecordActivity", " до начать запиь");
        recorder.start();
        Log.d("RecordActivity", " после начала записи");
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void clickStopButton() {
        stopRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording) {
                    stopRecording();
                    timeRecord.setBase(SystemClock.elapsedRealtime());
                    timeRecord.stop();
                    isRecording = false;

                    giveName();

                }
                else {
                    Toast.makeText(
                            getApplicationContext(),
                            "ОШИБКА. Запись не была начата",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isRecording) {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.toast_to_stop_recording,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Intent intent = new Intent(RecordActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void giveName() {
        Log.d("RecordActivity", "зашли в giveName");
        AlertDialog.Builder myDialog = new AlertDialog.Builder(RecordActivity.this);
        myDialog.setTitle(R.string.edit_text_dialog);
        myDialog.setCancelable(false);

        final EditText nameInput = new EditText(RecordActivity.this);
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        myDialog.setView(nameInput);

        myDialog.setPositiveButton(R.string.Ok_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(nameInput.getText().toString().equals("")) {
                    Toast.makeText(
                            getApplicationContext(),
                            R.string.give_name_toast,
                            Toast.LENGTH_SHORT
                    ).show();
                    giveName();
                }
                else {
                    recordName = nameInput.getText().toString();

                    addRecord();

                    Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });


        myDialog.setNegativeButton(R.string.cancel_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        myDialog.show();
    }

    private void addRecord() {

        MediaMetadataRetriever mediaMetadataRetriever= new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(fileName);
        String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        Record record = new Record(recordName, duration, fileName);
        viewModel.add(record);
    }

}
