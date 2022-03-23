package com.aryomtech.dhitifoundation.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.aryomtech.dhitifoundation.dao.NoteDao;
import com.aryomtech.dhitifoundation.entities.Note;

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {

    public static NotesDatabase notesDatabase;
    public abstract NoteDao noteDao();


    public static synchronized NotesDatabase getDatabase(Context context){

        if (notesDatabase == null){
            notesDatabase = Room.databaseBuilder( context, NotesDatabase.class, "notes_db").build();
        }

        return notesDatabase;
    }

}
