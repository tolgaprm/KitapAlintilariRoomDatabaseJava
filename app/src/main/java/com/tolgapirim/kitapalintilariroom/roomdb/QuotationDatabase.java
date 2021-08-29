package com.tolgapirim.kitapalintilariroom.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.tolgapirim.kitapalintilariroom.model.Quotation;

@Database(entities = {Quotation.class},version = 1)
public abstract class QuotationDatabase extends RoomDatabase {

    public abstract QuotationDao quotationDao();

}
