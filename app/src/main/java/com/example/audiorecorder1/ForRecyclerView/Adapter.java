package com.example.audiorecorder1.ForRecyclerView;

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
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<Record> recordList = new ArrayList<>();
    private OnRecordClickListener onRecordClickListener;

    public void setOnRecordClickListener(OnRecordClickListener onRecordClickListener) {
        this.onRecordClickListener = onRecordClickListener;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
        notifyDataSetChanged();
    }

    public List<Record> getRecordList() {
        return new ArrayList<>(recordList);
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

        if(file.exists() && file.length() != 0) {
            holder.name.setText(record.getRecName());
            holder.dateBorn.setText(timeTag(file));
            Long time = Long.parseLong(record.getRecDuration());
            holder.time.setText(durationTime(time));
            holder.line.setProgress(0);
        }
        else if (!file.exists() && file.length() == 0) {
            removeRecord(record);
        }

        clickOnItem(holder, record);

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

    private void removeRecord(Record record) {
        int position = recordList.indexOf(record);
        recordList.remove(record);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    private void clickOnItem(@NonNull RecyclerViewHolder holder, Record record) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecordClickListener.onRecordClick(record);
            }
        });
    }

    public interface OnRecordClickListener {
        void onRecordClick(Record record);
    }

}

