package com.example.audiorecorder1;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "records")
public class Record {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String recName;
    private String recDuration;
    private String recPath;

    public Record(int id, String recName, String recDuration, String recPath) {
        this.id = id;
        this.recName = recName;
        this.recDuration = recDuration;
        this.recPath = recPath;
    }
    @Ignore
    public Record(String recName, String recDuration, String recPath) {
        this.recName = recName;
        this.recDuration = recDuration;
        this.recPath = recPath;
    }

    public int getId() {
        return id;
    }

    public String getRecName() {
        return recName;
    }

    public String getRecDuration() {
        return recDuration;
    }

    public String getRecPath() {
        return recPath;
    }
}
