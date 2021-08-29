package com.tolgapirim.kitapalintilariroom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tolgapirim.kitapalintilariroom.R;
import com.tolgapirim.kitapalintilariroom.databinding.FragmentAddQuotationBinding;
import com.tolgapirim.kitapalintilariroom.model.Quotation;
import com.tolgapirim.kitapalintilariroom.roomdb.QuotationDao;
import com.tolgapirim.kitapalintilariroom.roomdb.QuotationDatabase;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class AddQuotationFragment extends Fragment {


    FragmentAddQuotationBinding binding;

    String bookName;
    String authorName;

    String textQuotation;
    String pageQuotation;

    QuotationDatabase db;
    QuotationDao quotationDao;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddQuotationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null){
            bookName = AddQuotationFragmentArgs.fromBundle(getArguments()).getBookName();
            authorName = AddQuotationFragmentArgs.fromBundle(getArguments()).getAuthorName();

            binding.bookName.setText(bookName);
            binding.autorName.setText(authorName);
        }

        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Alıntı Ekle");


        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });



        db = Room.databaseBuilder(requireContext(),QuotationDatabase.class,"Quotation").build();

        quotationDao = db.quotationDao();


    }

    public void save(View v){

        textQuotation = binding.textQuotation.getText().toString();
        pageQuotation = binding.pageQuotation.getText().toString();

        if(textQuotation.equals("") || pageQuotation.equals("")){

            if (textQuotation.equals(""))  {
                binding.quotationLayout.setErrorEnabled(true);
                binding.quotationLayout.setError("Boş bırakamazsınız!");
            }else{
                binding.quotationLayout.setErrorEnabled(false);
            }

            if(pageQuotation.equals("")){
                binding.pageLayout.setError("Boş bırakamazsınız!");
                binding.pageLayout.setErrorEnabled(true);
            }else{
                binding.pageLayout.setErrorEnabled(false);
            }

        }else{
            // Veritabanı kayıt
            binding.quotationLayout.setErrorEnabled(false);
            binding.pageLayout.setErrorEnabled(false);
            Quotation quotation = new Quotation(bookName,authorName,binding.textQuotation.getText().toString(),binding.pageQuotation.getText().toString());

           compositeDisposable.add(
                   quotationDao.insert(quotation)
                    .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(AddQuotationFragment.this::handleResponse)
           );

        }



    }

    public void handleResponse(){
        NavDirections action = AddQuotationFragmentDirections.actionAddQuotationFragmentToQuotationFragment(bookName,authorName);
        Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(action);

        Toast.makeText(requireContext(), "Alıntı başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();

        binding.textQuotation.setText("");
        binding.pageQuotation.setText("");



    }

}
