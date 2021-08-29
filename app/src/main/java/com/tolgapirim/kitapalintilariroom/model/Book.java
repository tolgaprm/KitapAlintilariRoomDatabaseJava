package com.tolgapirim.kitapalintilariroom.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Book {


    @PrimaryKey(autoGenerate = true)
    public int id;


    @ColumnInfo(name = "bookName")
    public String bookName;

    @ColumnInfo(name = "authorName")
    public String authorName;


    @ColumnInfo(name = "imageByte")
    public byte[] imagebyte;


    public Book(String bookName, String authorName, byte[] imagebyte) {
        this.bookName = bookName;
        this.authorName = authorName;
        this.imagebyte = imagebyte;
    }



}
