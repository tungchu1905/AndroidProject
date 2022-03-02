package com.example.android_projectnoteapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.android_projectnoteapp.R;
import com.example.android_projectnoteapp.database.NoteDatabase;
import com.example.android_projectnoteapp.entities.Note;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_ADD_NOTE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView addNewNote = findViewById(R.id.imageAddNoteMain);
        addNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), CreateNoteActivity.class)
                        ,REQUEST_CODE_ADD_NOTE);
            }
        });
        getNotes();
    }

    private void getNotes(){

        @SuppressLint("StaticFieldLeak")
        class  GetNotesTask extends AsyncTask<Void, Void, List<Note>>{

            @Override
            protected List<Note> doInBackground(Void... voids) {

                return NoteDatabase.
                        getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                Log.d("My_Notes", notes.toString());
            }
        }

        new GetNotesTask().execute();

    }
}