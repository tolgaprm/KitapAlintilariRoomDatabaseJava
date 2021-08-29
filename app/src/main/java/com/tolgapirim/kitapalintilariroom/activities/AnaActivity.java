package com.tolgapirim.kitapalintilariroom.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.tolgapirim.kitapalintilariroom.databinding.ActivityAnaBinding;

public class AnaActivity extends AppCompatActivity {

    ActivityAnaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAnaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}