package com.example.android_projectnoteapp.adapter;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_projectnoteapp.R;
import com.example.android_projectnoteapp.entities.Note;
import com.example.android_projectnoteapp.listeners.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

// ADAPTER lấy dữ liệu từ bộ dữ liệu và tạo ra các đối tượngView
//RecyclerView.Adapter Quản lý dữ liệu và cập nhật dữ liệu cần hiện thị vào View
public class NoteAdapters extends  RecyclerView.Adapter<NoteAdapters.NoteViewHolder>{
    private  List<Note> noteList;

    //VIEW AND UPDATE
    private NotesListener notesListener;

    //Search
    private Timer timer;
    private List<Note> noteSource;

    public NoteAdapters(List<Note> noteList, NotesListener notesListener) {
        this.noteList = noteList;
        this.notesListener = notesListener;
        noteSource = noteList;
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

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {
        holder.setNote(noteList.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListener.onNoteClicked(noteList.get(position),position);
            }
        });
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
        RoundedImageView imageNote;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTittle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateView);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
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

            //IMAGE
            if(note.getImagePath() !=null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
            }else{
                imageNote.setVisibility(View.GONE);
            }
        }
    }

    //SEARCH NOTE
    public void searchNotes(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()){
                    noteList = noteSource;
                }else{
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : noteSource) {
                        if(note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(note);
                        }
                    }
                    noteList = temp;
                }
               new Handler(Looper.getMainLooper()).post(new Runnable() {
                   @Override
                   public void run() {
                        notifyDataSetChanged();
                   }
               });
            }
        }, 500);
    }

    public void cancelTimer(){
        if(timer != null ){
            timer.cancel();
        }
    }
}
