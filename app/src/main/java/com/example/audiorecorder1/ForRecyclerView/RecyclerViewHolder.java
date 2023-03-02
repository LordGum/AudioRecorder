package com.example.audiorecorder1.ForRecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiorecorder1.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public CardView container;
    public TextView dateBorn;
    public TextView time;
    //public LinearProgressIndicator line;
    public ImageView image;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.textViewRecordName);
        container = itemView.findViewById(R.id.item);
        dateBorn = itemView.findViewById(R.id.date_of_record);
        time = itemView.findViewById(R.id.currentTime);
        //line = itemView.findViewById(R.id.line);
        image = itemView.findViewById(R.id.imageButton);

    }
}
