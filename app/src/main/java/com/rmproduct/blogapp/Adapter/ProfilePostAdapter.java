package com.rmproduct.blogapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmproduct.blogapp.Common.Constant;
import com.rmproduct.blogapp.Common.LocalStorage;
import com.rmproduct.blogapp.Models.Post;
import com.rmproduct.blogapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ProfilePostViewHolder> {

    private Context context;
    private ArrayList<Post> postList;
    private LocalStorage localStorage;

    public ProfilePostAdapter(Context context, ArrayList<Post> postList) {
        this.context = context;
        this.postList = postList;
        localStorage = new LocalStorage(context);
    }

    @NonNull
    @Override
    public ProfilePostAdapter.ProfilePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_my_post, parent, false);
        return new ProfilePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfilePostAdapter.ProfilePostViewHolder holder, int position) {
        Post post = postList.get(position);
        Picasso.get().load(post.getPhoto()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ProfilePostViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ProfilePostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgAccountPost);
        }
    }
}
