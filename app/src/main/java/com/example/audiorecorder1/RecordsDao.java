package com.example.audiorecorder1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordsDao {

    @Query("SELECT * FROM records")
    LiveData<List<Record>> getRecords();

    @Query("SELECT * FROM records")
    List<Record> getRecordList();

    @Insert
    void add(Record record);

    @Query("DELETE FROM records WHERE id = :id")
    void remove(int id);

    @Query("SELECT COUNT(*) FROM records")
    int size();
}
