package com.tolgapirim.kitapalintilariroom.fragments;

import android.app.ActionBar;
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
import com.tolgapirim.kitapalintilariroom.adapter.QuotationAdapter;
import com.tolgapirim.kitapalintilariroom.databinding.FragmentQuotationBinding;
import com.tolgapirim.kitapalintilariroom.model.Quotation;
import com.tolgapirim.kitapalintilariroom.roomdb.QuotationDao;
import com.tolgapirim.kitapalintilariroom.roomdb.QuotationDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class QuotationFragment extends Fragment {

    String bookNameFromRecyclerView;
    String authorNameFromRecyclerView;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    FragmentQuotationBinding binding;
    QuotationDatabase db;
    QuotationDao quotationDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQuotationBinding.inflate(inflater,container,false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments()!=null){
            bookNameFromRecyclerView = QuotationFragmentArgs.fromBundle(getArguments()).getBookName();
            authorNameFromRecyclerView= QuotationFragmentArgs.fromBundle(getArguments()).getAuthorName();
            //  System.out.println("Kitap İsim: "+bookNameFromRecyclerView +"  Yazar: "+authorNameFromRecyclerView  );
        }

        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(bookNameFromRecyclerView+" Alıntıları" );

        binding.goToFragmentAddQuotation.setOnClickListener(v -> {
            NavDirections action = QuotationFragmentDirections.actionQuotationFragmentToAddQuotationFragment(bookNameFromRecyclerView,authorNameFromRecyclerView);
            Navigation.findNavController(v).navigate(action);
        });




        db = Room.databaseBuilder(requireContext(),QuotationDatabase.class,"Quotation").build();
        quotationDao = db.quotationDao();

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
                quotationDao.getQuotation(bookNameFromRecyclerView)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(QuotationFragment.this::handleResponse)
        );
    }

    private void handleResponse(List<Quotation> quotationList) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        QuotationAdapter quotationAdapter = new QuotationAdapter(quotationList,getView());
        binding.recyclerView.setAdapter(quotationAdapter);
        binding.swipeRepleshLayout.setRefreshing(false);


    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}