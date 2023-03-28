package com.rmproduct.blogapp.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rmproduct.blogapp.Activity.CommentActivity;
import com.rmproduct.blogapp.Fragment.HomeFragment;
import com.rmproduct.blogapp.Models.Comment;
import com.rmproduct.blogapp.Models.Post;
import com.rmproduct.blogapp.R;
import com.rmproduct.blogapp.Common.Constant;
import com.rmproduct.blogapp.Common.LocalStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private Context context;
    private ArrayList<Comment> commentList;
    private LocalStorage localStorage;

    public CommentAdapter(Context context, ArrayList<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        localStorage = new LocalStorage(context);
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
        Picasso.get().load(Constant.URL + "storage/profiles/" + comment.getUser().getPhoto()).into(holder.commenterImg);

        if (comment.getUser().getId() == localStorage.getId()) {
            holder.commentOption.setVisibility(View.VISIBLE);
        } else {
            holder.commentOption.setVisibility(View.GONE);
        }

        holder.commentOption.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.commentOption);
            popupMenu.inflate(R.menu.delete_comment_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.deleteComment: {
                            deleteComment(comment.getId(), holder.getAbsoluteAdapterPosition());
                        }
                    }

                    return false;
                }
            });
            popupMenu.show();
        });
    }

    private void deleteComment(int id, int absoluteAdapterPosition) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Deleting Comment...");
        dialog.setCancelable(true);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Warning");
        dialogBuilder.setMessage("Are you sure to delete this comment?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.show();

                @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.POST, Constant.COMMENTS_DELETE, response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")) {
                            dialog.dismiss();
                            commentList.remove(absoluteAdapterPosition);
                            Post post = HomeFragment.postList.get(CommentActivity.postPosition);
                            post.setComment(post.getComment() - 1);
                            HomeFragment.postList.set(CommentActivity.postPosition, post);
                            HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, object.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }, error -> {
                    dialog.dismiss();
                    Log.d("DeleteCommentSuccess", error.getCause() + "");
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Authorization", "Bearer " + localStorage.getToken());
                        Log.d("DeleteCommentSuccess", "Header " + localStorage.getToken() + "\t" + id + "\t" + absoluteAdapterPosition);
                        return map;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", id + "");
                        Log.d("DeleteCommentSuccess", "Params " + id);
                        return map;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialogBuilder.setCancelable(true);
        dialogBuilder.show();
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {

        private TextView commenterName, commentDate, commentText;
        private ImageView commenterImg;
        private ImageButton commentOption;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            commenterName = itemView.findViewById(R.id.commenterName);
            commentDate = itemView.findViewById(R.id.commentDate);
            commentText = itemView.findViewById(R.id.commentText);
            commenterImg = itemView.findViewById(R.id.commenterImg);
            commentOption = itemView.findViewById(R.id.commentOption);
        }
    }
}
