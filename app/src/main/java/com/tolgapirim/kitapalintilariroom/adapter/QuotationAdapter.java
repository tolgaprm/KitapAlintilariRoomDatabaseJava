package com.tolgapirim.kitapalintilariroom.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.tolgapirim.kitapalintilariroom.R;
import com.tolgapirim.kitapalintilariroom.databinding.RecyclerQuotationRowBinding;
import com.tolgapirim.kitapalintilariroom.model.Quotation;
import com.tolgapirim.kitapalintilariroom.roomdb.QuotationDao;
import com.tolgapirim.kitapalintilariroom.roomdb.QuotationDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QuotationAdapter extends RecyclerView.Adapter<QuotationAdapter.QuotationHolder> {

    List<Quotation> quotationList;
    double quotationTextLength;
    int maxlines;
    final int DEFAULTMAXLINES =6;
    final int TEXTLENGTHPERLINE = 28;

    View view;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    QuotationDatabase db;
    QuotationDao quotationDao;

    String copyText;

    public QuotationAdapter(List<Quotation> quotationList,View view){
        this.quotationList =quotationList;
        this.view =view;
    }

    @NonNull
    @Override
    public QuotationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerQuotationRowBinding binding = RecyclerQuotationRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return  new QuotationHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuotationHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.recyclerViewBookName.setText(quotationList.get(position).bookName);
        holder.binding.recyclerViewAuthorName.setText(quotationList.get(position).authorName);
        holder.binding.recyclerViewPage.setText(holder.binding.getRoot().getContext().getString(R.string.page_quotation,quotationList.get(position).pageQuotation));
        holder.binding.recyclerViewQuotationText.setText(quotationList.get(position).textQuotation);




        // Devam?? G??r ve gizle i??lemleri
        maxlines = (int) (quotationList.get(position).textQuotation.length() / TEXTLENGTHPERLINE);

        if(maxlines > DEFAULTMAXLINES){
            holder.binding.seeMore.setVisibility(View.VISIBLE);
           holder.binding.seeMore.setOnClickListener(v ->{

               if(holder.binding.seeMore.getText().toString().equals("Devam??n?? G??ster")){
                   holder.binding.recyclerViewQuotationText.setMaxLines(maxlines);
                   holder.binding.seeMore.setText("Gizle");
               }
               else if (holder.binding.seeMore.getText().toString().equals("Gizle")){
                   holder.binding.recyclerViewQuotationText.setMaxLines(DEFAULTMAXLINES);
                   holder.binding.seeMore.setText("Devam??n?? G??ster");
               }

           });




        }




        // Al??nt?? kopyalama
        holder.binding.copyContentBtn.setOnClickListener(v->{

            copyText = quotationList.get(position).textQuotation +"\n\n\n "+ quotationList.get(position).bookName+" Sayfa:"+quotationList.get(position).pageQuotation;
            ClipboardManager clipboardManager = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(null,copyText);
            if (clipboardManager==null) return;
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(v.getContext(), "Kopyaland??", Toast.LENGTH_SHORT).show();
        });


        // Uzun bas??nca al??nt??y?? silme
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                db = Room.databaseBuilder(v.getContext(),QuotationDatabase.class,"Quotation").build();
                quotationDao = db.quotationDao();

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Al??nt?? Silme")
                        .setMessage("Al??nt??y?? silmek istedi??inize emin misiniz? ")
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleleteQuotation(quotationList.get(position).id);
                            }
                        })
                        .setNegativeButton("Hay??r", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(v.getContext(), "Silme i??lemi iptal edildi.", Toast.LENGTH_SHORT).show();
                            }
                        }).show();




                return false;
            }
        });



    }

    @Override
    public int getItemCount() {
        return quotationList.size();
    }

    public class QuotationHolder extends RecyclerView.ViewHolder{
        RecyclerQuotationRowBinding binding;

        public QuotationHolder(RecyclerQuotationRowBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    private void deleleteQuotation(int id){

        compositeDisposable.add(
                quotationDao.deleteQuotation(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        );

    }

    private void handleResponse(){
        Toast.makeText(view.getContext(), "Al??nt?? Ba??ar??yla Silindi\n L??tfen sayfay?? yenileyiniz", Toast.LENGTH_LONG).show();


    }
}
