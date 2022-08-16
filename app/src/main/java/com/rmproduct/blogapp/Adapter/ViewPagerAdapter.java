package com.rmproduct.blogapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.rmproduct.blogapp.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    public int[] slideImage = {
            R.drawable.ic_arrow_left,
            R.drawable.ic_arrow_drop,
            R.drawable.ic_arrow_right
    };

    public String[] slideTitle = {
            "Apply to local\njobs fast",
            "Stand out from\nthe crowd",
            "Get hired fast\nand start earning!"
    };

    public String[] slideSubTitle = {
            "Scroll through the job offers and apply in just a single tap!",
            "Use personalised videos and messages to build your profile and apply to job offers",
            "Stay active in Our community to meet new people and gain advice!"
    };

    @Override
    public int getCount() {
        return slideTitle.length;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_pager, container, false);

        ImageView vpImage = view.findViewById(R.id.vpImg);
        TextView vpTitle = view.findViewById(R.id.vpTitle);
        TextView vpDesc = view.findViewById(R.id.vpDesc);

        vpImage.setImageResource(slideImage[position]);
        vpTitle.setText(slideTitle[position]);
        vpDesc.setText(slideSubTitle[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;


    }
}
