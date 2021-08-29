package com.tolgapirim.kitapalintilariroom.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.tolgapirim.kitapalintilariroom.model.Book;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface BookDao {

    @Query("SELECT * FROM Book")
    Flowable<List<Book>> getAllBook();

    @Query("DELETE FROM Book WHERE id =(:id)")
    Completable delete(int id);

    @Insert
    Completable insert(Book book);
}
