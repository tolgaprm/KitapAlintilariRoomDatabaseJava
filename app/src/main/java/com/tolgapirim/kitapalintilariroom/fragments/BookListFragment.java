package com.tolgapirim.kitapalintilariroom.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tolgapirim.kitapalintilariroom.R;
import com.tolgapirim.kitapalintilariroom.SortByBookName;
import com.tolgapirim.kitapalintilariroom.adapter.BookAdapter;
import com.tolgapirim.kitapalintilariroom.databinding.FragmentBookListBinding;
import com.tolgapirim.kitapalintilariroom.model.Book;
import com.tolgapirim.kitapalintilariroom.roomdb.BookDao;
import com.tolgapirim.kitapalintilariroom.roomdb.BookDatabase;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class BookListFragment extends Fragment {

    FragmentBookListBinding binding;

    BookDatabase db;
    BookDao bookDao;





    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding= FragmentBookListBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment



        return binding.getRoot();




    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Kitap Alıntıları");

        binding.goToFragmentAddBook.setOnClickListener(v -> {
            NavDirections action = BookListFragmentDirections.actionBookListFragmentToAddBookFragment();
            Navigation.findNavController(v).navigate(action);
            onDestroy();
        });




        db = Room.databaseBuilder(requireContext(),BookDatabase.class,"Book").build();
        bookDao = db.bookDao();

        getData();

        binding.swipeRepleshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();

            }
        });

    }


    public void getData(){
        compositeDisposable.add(
                bookDao.getAllBook()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(BookListFragment.this::handleResponse)
        );


    }

    public void handleResponse(List<Book> bookList){

        Collections.sort(bookList, new SortByBookName());


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
         BookAdapter bookAdapter = new BookAdapter(bookList,getView());
        binding.recyclerView.setAdapter(bookAdapter);

        binding.swipeRepleshLayout.setRefreshing(false);


    }





    @Override
    public void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();

    }
}