package com.example.audiorecorder1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private FloatingActionButton RecordingButton;
    private TextView NoRecordsText;
    private RecyclerView recyclerRecords;
    private com.example.audiorecorder1.ForRecyclerView.Adapter adapter;

    private RecordDatabase recordDatabase;
    private Handler handler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        if(checkPermission() == false) {
            askPermission();
            return;
        }

        clickOnButton();

        displayFiles();
    }



    private void initViews() {
        RecordingButton = findViewById(R.id.RecordingButton);
        NoRecordsText = findViewById(R.id.NoRecordsText);
        recyclerRecords = findViewById(R.id.recyclerRecords);
    }

    private void askPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "Разрешенно", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Разрешите доступ, пожалуйста", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();


    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);

        return result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void clickOnButton() {
        RecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission() == true) {
                    Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                    startActivity(intent);
                }
                else {
                    askPermission();
                }
            }
        });
    }

    private void displayFiles() {
        adapter = new com.example.audiorecorder1.ForRecyclerView.Adapter();
        recyclerRecords.setAdapter(adapter);

        recordDatabase = RecordDatabase.getInstance(getApplication());

        /*
        if(recordDatabase == null) {
            NoRecordsText.setVisibility(View.VISIBLE);
        }
        else {
            adapter = new com.example.audiorecorder1.ForRecyclerView.Adapter();
            recyclerRecords.setAdapter(adapter);

            recordDatabase = RecordDatabase.getInstance(getApplication());
        }
        */
    }

    private void ShowNotes() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Record> recordList = recordDatabase.recordsDao().getRecords();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setRecordList(recordList);
                    }
                });

            }
        });
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowNotes();
    }
}