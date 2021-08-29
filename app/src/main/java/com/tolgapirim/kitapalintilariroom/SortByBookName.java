package com.tolgapirim.kitapalintilariroom;

import com.tolgapirim.kitapalintilariroom.model.Book;

import java.util.Comparator;

public class SortByBookName implements Comparator<Book> {

    @Override
    public int compare(Book a, Book b) {
        return a.bookName.compareTo(b.bookName);
    }


}
