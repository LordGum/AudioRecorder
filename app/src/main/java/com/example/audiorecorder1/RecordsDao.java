package com.example.audiorecorder1;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordsDao {

    @Query("SELECT * FROM records")
    List<Record> getRecords();

    @Insert
    void add(Record record);

    @Query("DELETE FROM records WHERE id = :id")
    void remove(int id);
}
