package com.tolgapirim.kitapalintilariroom.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.tolgapirim.kitapalintilariroom.databinding.RecyclerBookRowBinding;
import com.tolgapirim.kitapalintilariroom.fragments.BookListFragmentDirections;
import com.tolgapirim.kitapalintilariroom.model.Book;
import com.tolgapirim.kitapalintilariroom.roomdb.BookDao;
import com.tolgapirim.kitapalintilariroom.roomdb.BookDatabase;
import com.tolgapirim.kitapalintilariroom.roomdb.QuotationDao;
import com.tolgapirim.kitapalintilariroom.roomdb.QuotationDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder> {

    View v;
    // handleResponse fonksiyonu içerisindeki Toast mesajı kullanabilmek için
    List<Book> bookList;
    Bitmap bookImage;
    byte[] imageByte;

    BookDatabase db;
    BookDao bookDao;

    QuotationDatabase Qdb;
    QuotationDao quotationDao;


    CompositeDisposable compositeDisposable = new CompositeDisposable();



    public BookAdapter(List<Book> bookList,View v){
        this.bookList = bookList;
        this.v= v;
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerBookRowBinding recyclerBookRowBinding = RecyclerBookRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return  new BookHolder(recyclerBookRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.recyclerBookRowBinding.recylerViewBookName.setText(bookList.get(position).bookName);
        holder.recyclerBookRowBinding.recylerViewAuthorName.setText(bookList.get(position).authorName);

         imageByte= bookList.get(position).imagebyte;
        bookImage = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
        holder.recyclerBookRowBinding.recylerViewImageView.setImageBitmap(bookImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = BookListFragmentDirections.actionBookListFragmentToQuotationFragment(bookList.get(position).bookName,bookList.get(position).authorName);
                Navigation.findNavController(v).navigate(action);
            }
        });

       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {

               db= Room.databaseBuilder(view.getContext(),BookDatabase.class,"Book").build();
               bookDao = db.bookDao();

               Qdb = Room.databaseBuilder(view.getContext(),QuotationDatabase.class,"Quotation").build();
               quotationDao = Qdb.quotationDao();

               AlertDialog alert = new AlertDialog.Builder(view.getContext())
                       .setTitle(bookList.get(position).bookName+" Kitabını Sil")
                       .setMessage(bookList.get(position).bookName+" kitabını ve tüm alıntılarını silmek istediğinize emin misiniz?")
                       .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                                deleteBook(bookList.get(position).id,bookList.get(position).bookName,bookList.get(position).authorName);
                           }
                       })
                       .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               Toast.makeText(view.getContext(), "Kitap Silme İşlemi İptal Edildi.", Toast.LENGTH_SHORT).show();
                           }
                       }).show();


               return false;
           }
       });


    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class BookHolder extends RecyclerView.ViewHolder{
        RecyclerBookRowBinding recyclerBookRowBinding;

        public BookHolder(RecyclerBookRowBinding recyclerBookRowBinding) {
            super(recyclerBookRowBinding.getRoot());
            this.recyclerBookRowBinding =recyclerBookRowBinding;
        }
    }



    private void deleteBook(int id,String bookName,String authorName){




        compositeDisposable.addAll(
                bookDao.delete(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(),
                quotationDao.deleteFromBook(bookName,authorName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(BookAdapter.this::handleResponse)
        );



    }

    private void handleResponse() {
                Toast.makeText(v.getContext(),"Kitap ve tüm alıntıları silindi",Toast.LENGTH_LONG).show();
    }




}
