package com.rmproduct.blogapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmproduct.blogapp.Models.Post;
import com.rmproduct.blogapp.R;
import com.rmproduct.blogapp.common.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsHolder> {
    private Context context;
    private ArrayList<Post> postList;

    public PostsAdapter(Context context, ArrayList<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostsAdapter.PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        return new PostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.PostsHolder holder, int position) {
        Post post = postList.get(position);
        Picasso.get().load(Constant.URL + "storage/profiles/" + post.getUser().getPhoto()).into(holder.imgPoster);
        Picasso.get().load(Constant.URL + "storage/posts/" + post.getPhoto()).into(holder.postImg);
        holder.postName.setText(post.getUser().getUsername());
        holder.postDate.setText(post.getDate());
        holder.postDesc.setText(post.getDesc());
        holder.postLikeCount.setText(post.getLike() + " Likes");
        holder.postCommentView.setText("View all " + post.getComment());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostsHolder extends RecyclerView.ViewHolder {
        CircleImageView imgPoster;
        TextView postName, postDate, postDesc, postLikeCount, postCommentView;
        ImageButton postOption, postLike, postComment;
        ImageView postImg;

        public PostsHolder(@NonNull View itemView) {
            super(itemView);

            imgPoster = itemView.findViewById(R.id.imgPoster);
            postName = itemView.findViewById(R.id.postName);
            postDate = itemView.findViewById(R.id.postDate);
            postDesc = itemView.findViewById(R.id.postDesc);
            postLikeCount = itemView.findViewById(R.id.postLikeCount);
            postCommentView = itemView.findViewById(R.id.postCommentView);
            postOption = itemView.findViewById(R.id.postOption);
            postLike = itemView.findViewById(R.id.postLike);
            postComment = itemView.findViewById(R.id.postComment);
            postImg = itemView.findViewById(R.id.postImg);
        }
    }
}
