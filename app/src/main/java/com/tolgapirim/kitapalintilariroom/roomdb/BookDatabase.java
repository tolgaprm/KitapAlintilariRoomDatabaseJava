package com.tolgapirim.kitapalintilariroom.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.tolgapirim.kitapalintilariroom.model.Book;

@Database(entities = {Book.class},version = 1)
public abstract class BookDatabase extends RoomDatabase {
    public abstract BookDao bookDao();
}
