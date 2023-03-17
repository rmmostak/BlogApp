package com.rmproduct.blogapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rmproduct.blogapp.Fragment.HomeFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportFragmentManager().beginTransaction().replace(R.id.homeContainer, new HomeFragment()).commit();
    }
}