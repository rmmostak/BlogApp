package com.rmproduct.blogapp.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rmproduct.blogapp.Fragment.LoginFragment;
import com.rmproduct.blogapp.Fragment.SignupFragment;
import com.rmproduct.blogapp.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new LoginFragment()).commit();
    }
}