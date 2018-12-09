package com.example.liuyu.finalproject;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    View mView;

    TextView textTitle,textTime,textType;

    CardView noteCard;

    ImageView lockIcon;

    public NoteViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

        textTitle = mView.findViewById(R.id.note_title);
        textTime = mView.findViewById(R.id.note_time);
        textType = mView.findViewById(R.id.note_type);
        noteCard = mView.findViewById(R.id.note_card);
        lockIcon = mView.findViewById(R.id.lock_icon);
    }

    public void setNotetitle(String title){
        textTitle.setText(title);
    }

    public void setNoteTime(String time){
        textTime.setText(time);
    }

    public void setNoteType(String type){textType.setText(type);}
}
