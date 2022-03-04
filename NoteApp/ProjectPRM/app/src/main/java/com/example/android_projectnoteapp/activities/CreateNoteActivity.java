package com.example.android_projectnoteapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_projectnoteapp.R;
import com.example.android_projectnoteapp.database.NoteDatabase;
import com.example.android_projectnoteapp.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;

    private String selectNoteColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        inputNoteTitle= findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle= findViewById(R.id.inputNoteSubtitle);
        inputNoteText= findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a"
                        , Locale.getDefault()).format(new Date())
        );

        // CLICK VÀO DẤU TÍCH V ĐỂ SAVE
        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        selectNoteColor = "#333333";
        initMixedLayout();
        setViewSubtitleIndicatorColor();
    }


    //SAVE NOTE
     private void saveNote(){
        // check input
        if(inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note title can't empty", Toast.LENGTH_SHORT).show();
            return;
        }else if(inputNoteSubtitle.getText().toString().trim().isEmpty()
        &&inputNoteText.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // set các phần từ vào note
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        //SET COLOR
         note.setColor(selectNoteColor);

        // CÔNG VIỆC SAVE
        class SaveNoteTask extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                // SAVE vào back ground
                NoteDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                //nhận kết quả
                super.onPostExecute(unused);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
     }

     // Click or swipe MixedLayout (co the bo)
     private void initMixedLayout(){
        final LinearLayout layoutMixed = findViewById(R.id.layout_mixed);
        /*
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMixed);
        layoutMixed.findViewById(R.id.textMixed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState()!= BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
         */
        final  ImageView imageColor1 = layoutMixed.findViewById(R.id.imageColor1);
        final  ImageView imageColor2 = layoutMixed.findViewById(R.id.imageColor2);
        final  ImageView imageColor3 = layoutMixed.findViewById(R.id.imageColor3);
        final  ImageView imageColor4 = layoutMixed.findViewById(R.id.imageColor4);
        final  ImageView imageColor5 = layoutMixed.findViewById(R.id.imageColor5);

        // CHỌN MÀU CHO SUBTITLE
         // COLOR 1
        layoutMixed.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setViewSubtitleIndicatorColor();
            }
        });
        //COLOR 2
         layoutMixed.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 selectNoteColor = "#FDBE3B";
                 imageColor1.setImageResource(0);
                 imageColor2.setImageResource(R.drawable.ic_done);
                 imageColor3.setImageResource(0);
                 imageColor4.setImageResource(0);
                 imageColor5.setImageResource(0);
                 setViewSubtitleIndicatorColor();
             }
         });
         //COLOR 3
         layoutMixed.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 selectNoteColor = "#FF4842";
                 imageColor1.setImageResource(0);
                 imageColor2.setImageResource(0);
                 imageColor3.setImageResource(R.drawable.ic_done);
                 imageColor4.setImageResource(0);
                 imageColor5.setImageResource(0);
                 setViewSubtitleIndicatorColor();
             }
         });
         //COLOR 4
         layoutMixed.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 selectNoteColor = "#3A52FC";
                 imageColor1.setImageResource(0);
                 imageColor2.setImageResource(0);
                 imageColor3.setImageResource(0);
                 imageColor4.setImageResource(R.drawable.ic_done);
                 imageColor5.setImageResource(0);
                 setViewSubtitleIndicatorColor();
             }
         });
         //COLOR 5
         layoutMixed.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 selectNoteColor = "#000000";
                 imageColor1.setImageResource(0);
                 imageColor2.setImageResource(0);
                 imageColor3.setImageResource(0);
                 imageColor4.setImageResource(0);
                 imageColor5.setImageResource(R.drawable.ic_done);
                 setViewSubtitleIndicatorColor();
             }
         });
     }

    // CHON MAU CHO PHAN viewSubtitleIndicator
     private void setViewSubtitleIndicatorColor(){
         GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
         gradientDrawable.setColor(Color.parseColor(selectNoteColor));
     }
}