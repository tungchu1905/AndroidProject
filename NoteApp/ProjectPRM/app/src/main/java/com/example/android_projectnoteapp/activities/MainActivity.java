package com.example.android_projectnoteapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.android_projectnoteapp.R;
import com.example.android_projectnoteapp.adapter.NoteAdapters;
import com.example.android_projectnoteapp.database.NoteDatabase;
import com.example.android_projectnoteapp.entities.Note;
import com.example.android_projectnoteapp.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE = 5;
    public static final int REQUEST_CODE_SHOW_NOTE= 6;
    //RecylerView để hiện thị dữ liệu dưới dạng danh sách ngang đứng LinearLayoutManager
    private RecyclerView noteRecyclerView;
    private List<Note> noteList;
    private NoteAdapters noteAdapter;

    private int noteClickPosition = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView addNewNote = findViewById(R.id.imageAddNoteMain);
        // Click vào image add => chuyển sang CreateNoteActivity
        addNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), CreateNoteActivity.class),REQUEST_CODE_ADD_NOTE);
            }
        });

        noteRecyclerView = findViewById(R.id.noteRecyclerView);
        noteRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapters(noteList,this);
        noteRecyclerView.setAdapter(noteAdapter);
        getNotes(REQUEST_CODE_SHOW_NOTE);
    }

    @Override
    public void onNoteClicked(Note note, int position) {
            noteClickPosition = position;
            Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class );
            intent.putExtra("isViewOrUpdate", true);
            intent.putExtra("note", note);
            startActivityForResult(intent, REQUEST_CODE_UPDATE);
    }

    private void getNotes( final int requestCode){
        @SuppressLint("StaticFieldLeak")
        class  GetNotesTask extends AsyncTask<Void, Void, List<Note>>{

            @Override
            protected List<Note> doInBackground(Void... voids) {
                // lưu note vào database
                return NoteDatabase.
                        getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                // hiển thị note lên mainActivityLayout

                if(requestCode == REQUEST_CODE_SHOW_NOTE){
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                }else if(requestCode == REQUEST_CODE_ADD_NOTE){
                    noteList.add(0,notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                    noteRecyclerView.smoothScrollToPosition(0);
                }else if(requestCode == REQUEST_CODE_UPDATE){
                    noteList.remove(noteClickPosition);
                    noteList.add(noteClickPosition, notes.get(noteClickPosition));
                    noteAdapter.notifyItemChanged(noteClickPosition);
                }
            }
        }

        new GetNotesTask().execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checck chuyển sang add, và kết quả done -> getNotes
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTE);
        }else if(requestCode == REQUEST_CODE_UPDATE && resultCode == RESULT_OK){
            getNotes(REQUEST_CODE_UPDATE);
        }
    }
}