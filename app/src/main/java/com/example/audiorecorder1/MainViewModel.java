package com.example.audiorecorder1;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainViewModel  extends AndroidViewModel {

    private RecordDatabase recordDatabase;
    private List<Record> List;

    public MainViewModel(@NonNull Application application) {
        super(application);
        recordDatabase = RecordDatabase.getInstance(application);
    }

    public LiveData<List<Record>> getRecords() {
        return recordDatabase.recordsDao().getRecords();
    }

    public void add(Record record) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                recordDatabase.recordsDao().add(record);
            }
        });
        thread.start();
    }

    public void remove(Record record) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                recordDatabase.recordsDao().remove(record.getId());
            }
        });
        thread.start();
    }

}
