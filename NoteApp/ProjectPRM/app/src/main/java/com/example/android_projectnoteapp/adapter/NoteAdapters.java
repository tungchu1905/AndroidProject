package com.example.android_projectnoteapp.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_projectnoteapp.R;
import com.example.android_projectnoteapp.entities.Note;

import java.util.List;

// ADAPTER lấy dữ liệu từ bộ dữ liệu và tạo ra các đối tượngView
//RecyclerView.Adapter Quản lý dữ liệu và cập nhật dữ liệu cần hiện thị vào View
public class NoteAdapters extends  RecyclerView.Adapter<NoteAdapters.NoteViewHolder>{
    private  List<Note> noteList;

    public NoteAdapters(List<Note> noteList) {
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(noteList.get(position));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //RecyclerView.Viewholder lớp dùng để gán / cập nhật dữ liệu vào các phần tử.
    static class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTittle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateView);
            layoutNote = itemView.findViewById(R.id.layoutNote);
        }

        void setNote(Note note){
            textTitle.setText(note.getTitle());
            if(note.getSubtitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }else{
                textSubtitle.setText(note.getSubtitle());
            }
            textDateTime.setText(note.getDateTime());


            // ĐỔI MÀU COLOR HIỂN THỊ SANG BÊN MAINACIVITY
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if(note.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }else{
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
        }
    }
}
