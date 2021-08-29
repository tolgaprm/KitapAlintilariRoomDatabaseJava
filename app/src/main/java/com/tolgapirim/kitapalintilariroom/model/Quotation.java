package com.tolgapirim.kitapalintilariroom.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Quotation {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "bookName")
    public String bookName;

    @ColumnInfo(name = "authorName")
    public String authorName;


    @ColumnInfo(name = "textQuotation")
    public String textQuotation;

    @ColumnInfo(name = "pageQuotation")
    public String pageQuotation;

    public Quotation(String bookName,String authorName, String textQuotation, String pageQuotation ){
        this.bookName= bookName;
        this.authorName =authorName;
        this.textQuotation = textQuotation;
        this.pageQuotation = pageQuotation;
    }
}
