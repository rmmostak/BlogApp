package com.rmproduct.blogapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmproduct.blogapp.Models.Comment;
import com.rmproduct.blogapp.R;
import com.rmproduct.blogapp.common.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private Context context;
    private ArrayList<Comment> commentList;

    public CommentAdapter(Context context, ArrayList<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_layout, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.commenterName.setText(comment.getUser().getUsername());
        holder.commentDate.setText(comment.getDate());
        holder.commentText.setText(comment.getComment());
        Picasso.get().load(Constant.URL + "storage/profiles/"+comment.getUser().getPhoto()).into(holder.commenterImg);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {

        private TextView commenterName, commentDate, commentText;
        private ImageView commenterImg;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            commenterName = itemView.findViewById(R.id.commenterName);
            commentDate = itemView.findViewById(R.id.commentDate);
            commentText = itemView.findViewById(R.id.commentText);
            commenterImg = itemView.findViewById(R.id.commenterImg);
        }
    }
}
