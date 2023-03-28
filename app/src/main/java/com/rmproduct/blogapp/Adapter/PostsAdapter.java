package com.rmproduct.blogapp.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
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
import com.rmproduct.blogapp.Activity.EditPostActivity;
import com.rmproduct.blogapp.Activity.HomeActivity;
import com.rmproduct.blogapp.Models.Post;
import com.rmproduct.blogapp.R;
import com.rmproduct.blogapp.Common.Constant;
import com.rmproduct.blogapp.Common.LocalStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsHolder> {
    private Context context;
    private ArrayList<Post> postList;
    private ArrayList<Post> postAll;
    private LocalStorage localStorage;

    public PostsAdapter(Context context, ArrayList<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.postAll = new ArrayList<>(postList);
        localStorage = new LocalStorage(context);
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

        if (post.getUser().getId() == localStorage.getId()) {
            holder.postOption.setVisibility(View.VISIBLE);
        } else {
            holder.postOption.setVisibility(View.GONE);
        }

        holder.postLike.setImageResource(
                post.isSelfLike() ? R.drawable.ic_love : R.drawable.ic_love_out
        );

        holder.postLike.setOnClickListener(view -> {
            holder.postLike.setImageResource(
                    post.isSelfLike() ? R.drawable.ic_love_out : R.drawable.ic_love
            );

            StringRequest request = new StringRequest(Request.Method.POST, Constant.POST_LIKE, response -> {
                Post mPost = postList.get(position);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("success")) {
                        mPost.setSelfLike(!post.isSelfLike());
                        mPost.setLike(mPost.isSelfLike() ? post.getLike() + 1 : post.getLike() - 1);
                        postList.set(position, mPost);
                        notifyItemChanged(position);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, object.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }, error -> {
                Log.d("EditPostSuccess", error.getCause() + "");
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Authorization", "Bearer " + localStorage.getToken());
                    Log.d("EditPostSuccess", "Header " + localStorage.getToken());
                    return map;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", post.getId() + "");
                    Log.d("EditPostSuccess", "Params " + post.getId());
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        });

        holder.postComment.setOnClickListener(view -> {
            Intent intent=new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.getId());
            intent.putExtra("postPosition", position);
            context.startActivity(intent);
        });

        holder.postCommentView.setOnClickListener(view -> {
            Intent intent=new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.getId());
            intent.putExtra("postPosition", position);
            context.startActivity(intent);
        });

        holder.postOption.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.postOption);
            popupMenu.inflate(R.menu.post_option_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menuPostEdit: {
                            Intent intent = new Intent(((HomeActivity) context), EditPostActivity.class);
                            intent.putExtra("postId", post.getId());
                            intent.putExtra("position", holder.getAbsoluteAdapterPosition());
                            intent.putExtra("text", post.getDesc());
                            context.startActivity(intent);
                            return true;
                        }
                        case R.id.menuPostDelete: {
                            deletePost(post.getId(), holder.getAbsoluteAdapterPosition());
                        }
                    }

                    return false;
                }
            });
            popupMenu.show();
        });
    }

    private void deletePost(int id, int absoluteAdapterPosition) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Deleting Post...");
        dialog.setCancelable(true);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Warning");
        dialogBuilder.setMessage("Are you sure to delete this post?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.show();

                @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.POST, Constant.POST_DELETE, response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")) {
                            dialog.dismiss();
                            postList.remove(absoluteAdapterPosition);
                            notifyItemRemoved(absoluteAdapterPosition);
                            notifyDataSetChanged();
                            postAll.clear();
                            postAll.addAll(postList);
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
                    Log.d("EditPostSuccess", error.getCause() + "");
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Authorization", "Bearer " + localStorage.getToken());
                        Log.d("EditPostSuccess", "Header " + localStorage.getToken() + "\t" + id + "\t" + absoluteAdapterPosition);
                        return map;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", id + "");
                        Log.d("EditPostSuccess", "Params " + id);
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
        return postList.size();
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Post> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(postAll);
            } else {
                for (Post post : postAll) {
                    if (post.getDesc().toLowerCase().contains(charSequence.toString().toLowerCase()) || post.getUser().getUsername().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(post);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            postList.clear();
            postList.addAll((Collection<? extends Post>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
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

            postOption.setVisibility(View.GONE);
        }
    }
}
