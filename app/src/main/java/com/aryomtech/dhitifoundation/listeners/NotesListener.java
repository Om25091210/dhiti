package com.aryomtech.dhitifoundation.listeners;

import com.aryomtech.dhitifoundation.entities.Note;

public interface NotesListener {

    void onNoteClicked(Note note, int position);

}
