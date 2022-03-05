package com.example.android_projectnoteapp.listeners;

import com.example.android_projectnoteapp.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
