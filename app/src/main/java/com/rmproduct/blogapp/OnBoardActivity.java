package com.rmproduct.blogapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.rmproduct.blogapp.Adapter.ViewPagerAdapter;

public class OnBoardActivity extends AppCompatActivity {

    private Button skip, next;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private LinearLayout dotLayout;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        skip = findViewById(R.id.skipBtn);
        next = findViewById(R.id.nextBtn);
        viewPager = findViewById(R.id.viewPager);
        dotLayout = findViewById(R.id.dotsLayout);
        adapter = new ViewPagerAdapter(OnBoardActivity.this);

        addDotIndicator(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addDotIndicator(position);

                if (position == 0) {
                    skip.setVisibility(View.VISIBLE);
                    skip.setEnabled(true);
                    next.setText("Next");
                } else if (position == 1) {
                    skip.setVisibility(View.VISIBLE);
                    skip.setEnabled(true);
                    next.setText("Next");
                } else {
                    skip.setVisibility(View.GONE);
                    skip.setEnabled(false);
                    next.setText("Finish");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(adapter);
        next.setOnClickListener(view -> {
            if (next.getText().toString().equals("Next")) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                startActivity(new Intent(OnBoardActivity.this, AuthActivity.class));
                finish();
            }
        });
        skip.setOnClickListener(view -> {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 2);
        });
    }

    public void addDotIndicator(int d) {
        dots = new TextView[3];
        dotLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#9675;"));
            dots[i].setTextSize(30);
            //dots[i].setTextColor(getResources().getColor(R.color.black));

            dotLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[d].setText(Html.fromHtml("&#9679;"));
            dots[d].setTextColor(getResources().getColor(R.color.black));
        }
    }
}