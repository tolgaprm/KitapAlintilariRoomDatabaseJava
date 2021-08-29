package com.tolgapirim.kitapalintilariroom.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.tolgapirim.kitapalintilariroom.model.Quotation;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface QuotationDao {

    @Query("SELECT * FROM Quotation WHERE  bookName =:bookname")
    Flowable<List<Quotation>> getQuotation(String bookname);

    @Insert
    Completable insert(Quotation quotation);



    @Query("DELETE FROM Quotation WHERE bookName= :bookName AND " +"authorName= :authorName " )
    Completable deleteFromBook(String bookName, String authorName);


    @Query("DELETE FROM Quotation WHERE id =:id ")
    Completable deleteQuotation(int id);

}
