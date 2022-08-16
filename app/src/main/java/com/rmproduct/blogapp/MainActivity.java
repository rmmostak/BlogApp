package com.rmproduct.blogapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //redirect to next page after 1.5sec
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstTime();
            }
        }, 1500);
    }

    private void isFirstTime() {
        SharedPreferences preferences = getApplication().getSharedPreferences("first", MODE_PRIVATE);
        boolean isTrue=preferences.getBoolean("isFirst", true);
        if (isTrue) {
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("isFirst", true);
            editor.apply();

            startActivity(new Intent(MainActivity.this, OnBoardActivity.class));
            finish();
        } else {
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            finish();
        }
    }
}