package com.example.audiorecorder1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiorecorder1.ForRecyclerView.Adapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton RecordingButton;
    private TextView NoRecordsText;
    private RecyclerView recyclerRecords;

    private com.example.audiorecorder1.ForRecyclerView.Adapter adapter;
    private RecordDatabase recordDatabase;
    private List<Record> recordList;

    private Handler handler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        if (checkPermission() == false) {
            askPermission();
        }

        adapter = new com.example.audiorecorder1.ForRecyclerView.Adapter();
        recyclerRecords.setAdapter(adapter);
        recordDatabase = RecordDatabase.getInstance(getApplication());

        clickOnButton();
        isEmpty();
        swipeDelete();
        clickOnRecord();

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
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Разрешенно",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Разрешите доступ, пожалуйста",
                                    Toast.LENGTH_SHORT
                            ).show();
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
                if (checkPermission() == true) {
                    Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                    startActivity(intent);
                } else {
                    askPermission();
                }
            }
        });
    }

    private void isEmpty() {
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/VkTestRecorder");
        File[] files = path.listFiles();
        if (files == null) {
            NoRecordsText.setVisibility(View.VISIBLE);
        } else {
            NoRecordsText.setVisibility(View.INVISIBLE);
        }
    }

    private void ShowNotes() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                recordList = recordDatabase.recordsDao().getRecords();
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
        if (checkPermission()) {
            ShowNotes();
        }
    }

    private void swipeDelete() {

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(
                    @NonNull RecyclerView recyclerView,
                    @NonNull RecyclerView.ViewHolder viewHolder,
                    @NonNull RecyclerView.ViewHolder target
            ) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                Record record = adapter.getRecordList().get(position);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        recordDatabase.recordsDao().remove(record.getId());
                        File file = new File(record.getRecPath());
                        if(file.exists()) {
                            file.delete();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ShowNotes();
                            }
                        });
                    }
                });
                thread.start();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(MainActivity.this, c,
                        recyclerRecords,viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red))
                        .addSwipeRightActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }}
        );

        itemTouchHelper.attachToRecyclerView(recyclerRecords);

    }

    private void clickOnRecord() {
        adapter.setOnRecordClickListener(new Adapter.OnRecordClickListener() {
            @Override
            public void onRecordClick(Record record) {

                Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
                int position = findPosition(record);
                if(position != -1) {
                    intent.putExtra("position", position);
                    //intent.putExtra("recordList", recordList);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(
                            getApplicationContext(),
                            "ОШИБКА В ПОИСКЕ ПОЗИЦИИ",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    private int findPosition(Record record) {
        for(int i=0; i<adapter.getRecordList().size(); i++) {
            Record otherRecord = adapter.getRecordList().get(i);
            if(record.getId() == otherRecord.getId()) {
                return i;
            }
        }
        return -1;
    }




}