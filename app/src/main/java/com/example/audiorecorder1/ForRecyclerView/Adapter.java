package com.example.audiorecorder1.ForRecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiorecorder1.R;
import com.example.audiorecorder1.Record;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<Record> recordList = new ArrayList<>();

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.custom_item,
                parent,
                false
        );
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Record record = recordList.get(position);
        File file = new File(record.getRecPath());

        holder.name.setText(record.getRecName());
        holder.dateBorn.setText(timeTag(file));
        Long time = Long.parseLong(record.getRecDuration()) ;
        holder.time.setText(durationTime(time));

    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    private String timeTag(File file) {

        DateFormat ft = new SimpleDateFormat("dd.MM.yyyy в HH:mm", Locale.getDefault());
        String fileDate  = ft.format(new Date(file.lastModified()));
        String todayDate = ft.format(new Date());
        if( fileDate.substring(0, 10).equals(todayDate.substring(0,10)) ) {
            fileDate = "Сегодня" + fileDate.substring(10);
        }
        return  fileDate;
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
