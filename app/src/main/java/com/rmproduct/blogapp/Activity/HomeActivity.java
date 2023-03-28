package com.rmproduct.blogapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rmproduct.blogapp.Fragment.HomeFragment;
import com.rmproduct.blogapp.Fragment.ProfileFragment;
import com.rmproduct.blogapp.R;

public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FloatingActionButton addPost;
    private BottomNavigationView navigationView;
    private static final int GALLERY_ADD_POST = 2;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.homeContainer, new HomeFragment(), HomeFragment.class.getSimpleName()).commit();

        navigationView = findViewById(R.id.bottomNav);
        addPost = findViewById(R.id.addPost);
        addPost.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_ADD_POST);
        });

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navHome: {
                        Fragment account = fragmentManager.findFragmentByTag(ProfileFragment.class.getSimpleName());
                        if (account != null) {
                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(ProfileFragment.class.getSimpleName())).commit();
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName())).commit();
                        }
                        break;
                    }

                    case R.id.navProfile: {
                        Fragment account = fragmentManager.findFragmentByTag(ProfileFragment.class.getSimpleName());
                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName())).commit();
                        if (account != null) {
                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(ProfileFragment.class.getSimpleName())).commit();
                        } else {
                            fragmentManager.beginTransaction().add(R.id.homeContainer, new ProfileFragment(), ProfileFragment.class.getSimpleName()).commit();
                        }
                        break;
                    }
                }

                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_ADD_POST && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            Intent intent = new Intent(HomeActivity.this, AddPostActivity.class);
            intent.setData(imgUri);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "Press Again to Exit!", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}