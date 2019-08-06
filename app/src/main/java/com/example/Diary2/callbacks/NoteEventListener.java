package com.example.Diary2.callbacks;

import com.example.Diary2.model.Note;

public interface NoteEventListener {

    void onNoteClick(Note note);

    void onNoteLongClick(Note note);
}
