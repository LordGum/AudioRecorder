package com.example.audiorecorder1.ForRecyclerView;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiorecorder1.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public CardView container;
    public TextView date;
    public TextView time;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.textViewRecordName);
        container = itemView.findViewById(R.id.item);
        date = itemView.findViewById(R.id.date_of_record);
        time = itemView.findViewById(R.id.currentTime);


    }
}
