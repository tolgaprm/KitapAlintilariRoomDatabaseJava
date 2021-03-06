package com.tolgapirim.kitapalintilariroom.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.tolgapirim.kitapalintilariroom.R;
import com.tolgapirim.kitapalintilariroom.databinding.FragmentAddBookBinding;
import com.tolgapirim.kitapalintilariroom.model.Book;
import com.tolgapirim.kitapalintilariroom.roomdb.BookDao;
import com.tolgapirim.kitapalintilariroom.roomdb.BookDatabase;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddBookFragment extends Fragment {


    FragmentAddBookBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    Bitmap selectedImage;


    BookDatabase db;
    BookDao bookDao;

    String bookName;
    String authorName;


    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding= FragmentAddBookBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Kitap Ekle");

        registerLauncher();
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });


        // set up Database
        db = Room.databaseBuilder(requireContext(),BookDatabase.class,"Book").build();
        bookDao = db.bookDao();
    }

    public void selectImage(View v){

        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){

                Snackbar.make(v,"Kitap resmine eri??meniz i??in izne ihtiya?? var",Snackbar.LENGTH_INDEFINITE).setAction("??zin Ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // request PErmission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                // request PErmission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }else{
            // go to gallery
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
   }



   public void save(){
        if (binding.bookName.getText().toString().equals("") || binding.authorName.getText().toString().equals("")|| selectedImage == null ){
            if(binding.bookName.getText().toString().equals("")){
                binding.LayoutBookName.setError("L??tfen bo?? B??rakmay??n??z!");
                binding.LayoutBookName.setErrorEnabled(true);
            }else{
                binding.LayoutBookName.setErrorEnabled(false);
            }

            if(binding.authorName.getText().toString().equals("")){

                binding.LayoutAuthorName.setError("L??tfen bo?? b??rakmay??n??z");
                binding.LayoutAuthorName.setErrorEnabled(true);
            }else{
                binding.LayoutAuthorName.setErrorEnabled(false);
            }

            if(selectedImage==null){
                Toast.makeText(requireContext(), "Resim se??melisiniz", Toast.LENGTH_SHORT).show();
            }
        }else{

            bookName = binding.bookName.getText().toString();
            authorName = binding.authorName.getText().toString();

            compositeDisposable.add(
                    bookDao.getAllBook()
                            .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(AddBookFragment.this::handleResponseForGetAllBook)
            );



        }
    }

    /*

    Bu fonksiyonda kitap kaydetmeden ??nce ilk ??nce yukar??da
    b??t??n kitaplar?? veritaban??ndan ??ektik.
    Bu bize bir book listesi d??nd??r??yor.

   - Bu listeyi kullanarak eleyece??imiz kitab?? kar????lar??t??rarak bu kitap
    daha ??nceden eklenmmi?? mi eklenmemi?? mi kontrol ediyoruz.

    -E??er daha ??nceden kitap eklenmi?? ise isHave true oluyor ve bize Toast mesaj?? verecek.
    e??er kitap daha ??nceden eklenmemi?? ise isHAve false olacak ve biz bu durumda yeni kitab??
    ekleyebilece??iz.

    Yaln??z bir kitab?? ilk ekledi??imizde de kitab?? kaydediyor  ama "Bu kitap daha ??nceden kaydedilmi??."
    diye Toast mesaj?? ????k??yor ama daha sonradan ayn?? kitab?? eklemeye ??al??????rsak hem hata mesaj??n?? veriyor
    hem de kitab?? veritaban??na kaydetmiyor.

    */
    private void handleResponseForGetAllBook(List<Book> bookList) {

        boolean ishave=false;

        for (int i=0; i<bookList.size();i++){

            if(bookList.get(i).bookName.equals(bookName) && bookList.get(i).authorName.equals(authorName)){
               if (i==0){
                   System.out.println("");
               }else{
                   Toast.makeText(requireContext(), "Bu kitap daha ??nceden kaydedilmi??.", Toast.LENGTH_SHORT).show();
               }
                ishave = true;
                break;
            }

        }

        if (!ishave){
            Bitmap smallerBitmap = makeSmallerBitmap(selectedImage,400);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            smallerBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);

            byte[] bytes = outputStream.toByteArray();

            Book book = new Book(bookName,authorName,bytes);

            compositeDisposable.add(
                    bookDao.insert(book)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(AddBookFragment.this::handleResponse)
            );

        }
    }


    /*
      Bu fonksiyonda veriyi ba??ar??l?? bir kaydedilirse
      ne yapmam??z gerekti??ini yazaca????z.
      */
    public void handleResponse(){

        NavDirections action = AddBookFragmentDirections.actionAddBookFragmentToBookListFragment();
        Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(action);

        Toast.makeText(requireContext(), "Kitap kaydedildi", Toast.LENGTH_SHORT).show();
        onDestroy();

    }

    public void registerLauncher(){


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK){
                    Intent resultFromIntent = result.getData();

                    if(resultFromIntent!=null){
                        Uri uri = resultFromIntent.getData();

                        try {
                            if (Build.VERSION.SDK_INT>=28){
                                ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(),uri);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }else{
                                selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),uri);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                }
            }
        });
    }


    public Bitmap makeSmallerBitmap(Bitmap selectedImage, int maxBoyut){
        int width = selectedImage.getWidth();
        int height= selectedImage.getHeight();

        float bitmapRotaion = (float)width / (float)height;

        if (bitmapRotaion>1){
            // yatay bir resimdir
            width = maxBoyut;
            height =  (int)(width / bitmapRotaion);
        }else{
            height = maxBoyut;
            width = (int) (height * bitmapRotaion);
        }

        return Bitmap.createScaledBitmap(selectedImage,width,height,true);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.clear();

        /*
         Burada yapt??????m i??lem belki bu uygulamada ??ok gerekli olmayan bir durum ama b??y??k uygulamalarda kullan??lmas?? ??ok ??nemlidir.

         Disposable kullan at anlam??na gelen bir terim. Biz Veritaban??m??zda baz?? veriler ??ekece??iz yada kaydedece??iz ve er b??r bu i??elm
         yapt??????m??zda uygulaman??n haf??zas??nda yer tutar ve zamanla uygulama verimsiz ??al????maya ba??layabilir.

         ??zellikle ??nternettende veri ald??????m??z zamanda ordada veri alaabilmek i??in istekler yapaca????z ve bu da uygulamam??z??n haf??zas??nda yer tutar.

         Bu i??lemi CompositeDisposable ile kulland??????m??zda yapt??????m??z her bir i??lemi kaydeder ve en son biz bu fragment sona erdi??inde
         bu i??lemleri siler ve haf??zadan temizleriz ki uygulamam??z daha verimli ??al????abilsin.

        * */


    }


}