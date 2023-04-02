package com.rmproduct.blogapp.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rmproduct.blogapp.Adapter.CommentAdapter;
import com.rmproduct.blogapp.Common.Constant;
import com.rmproduct.blogapp.Common.LocalStorage;
import com.rmproduct.blogapp.Fragment.HomeFragment;
import com.rmproduct.blogapp.Models.Comment;
import com.rmproduct.blogapp.Models.Post;
import com.rmproduct.blogapp.Models.User;
import com.rmproduct.blogapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView commentRecycler;
    private ArrayList<Comment> commentList;
    private CommentAdapter adapter;
    private EditText commentText;
    private ImageButton postComment;
    private LocalStorage localStorage;
    public static int postId = 0, postPosition = 0;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentList = new ArrayList<>();
        commentRecycler = findViewById(R.id.commentRecycler);
        commentRecycler.setHasFixedSize(true);
        commentRecycler.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        postId = getIntent().getIntExtra("postId", 0);
        postPosition = getIntent().getIntExtra("postPosition", -1);
        localStorage = new LocalStorage(CommentActivity.this);
        commentText = findViewById(R.id.comment);
        postComment = findViewById(R.id.postComment);
        dialog = new ProgressDialog(CommentActivity.this);
        dialog.setTitle("Posting comment...");
        dialog.setCancelable(true);

        postComment.setOnClickListener(view -> {
            dialog.show();
            String comments = commentText.getText().toString().trim();
            if (!TextUtils.isEmpty(comments)) {
                createComment(comments, postId);
            } else {
                dialog.dismiss();
                commentText.setError("Write something.");
                commentText.requestFocus();
                return;
            }
        });

        getComments();
    }

    private void createComment(String comments, int postId) {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.COMMENTS_CREATE, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {

                    JSONObject comment = object.getJSONObject("comment");
                    JSONObject user = comment.getJSONObject("user");

                    User mUser = new User();
                    mUser.setId(user.getInt("id"));
                    mUser.setUsername(user.getString("name") + " " + user.getString("lastname"));
                    mUser.setPhoto(user.getString("photo"));

                    Comment mComment = new Comment();
                    mComment.setUser(mUser);
                    mComment.setId(comment.getInt("id"));
                    mComment.setDate(comment.getString("created_at"));
                    mComment.setComment(comment.getString("comment"));

                    Post post = HomeFragment.postList.get(postPosition);
                    post.setComment(post.getComment() + 1);
                    HomeFragment.postList.set(postPosition, post);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                    commentList.add(mComment);
                    commentRecycler.getAdapter().notifyDataSetChanged();
                    commentText.setText("");

                    dialog.dismiss();
                } else {
                    Toast.makeText(CommentActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
                Toast.makeText(CommentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, error -> {
            dialog.dismiss();
            Log.d("EditPostSuccess", error.getCause() + "");
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + localStorage.getToken());
                Log.d("EditPostSuccess", "Header " + localStorage.getToken() + "\t" + postId + "\t" + postPosition);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", postId + "");
                map.put("comment", comments);
                Log.d("EditPostSuccess", "Params " + postId);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(CommentActivity.this);
        queue.add(request);
    }

    private void getComments() {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.COMMENTS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {

                    JSONArray comments = new JSONArray(object.getString("comments"));
                    for (int i = 0; i < comments.length(); i++) {
                        JSONObject comment = comments.getJSONObject(i);
                        JSONObject user = comment.getJSONObject("user");

                        User mUser = new User();
                        mUser.setId(user.getInt("id"));
                        mUser.setUsername(user.getString("name") + " " + user.getString("lastname"));
                        mUser.setPhoto(user.getString("photo"));

                        Comment mComment = new Comment();
                        mComment.setUser(mUser);
                        mComment.setId(comment.getInt("id"));
                        mComment.setDate(comment.getString("created_at"));
                        mComment.setComment(comment.getString("comment"));

                        commentList.add(mComment);
                    }
                    adapter = new CommentAdapter(CommentActivity.this, commentList);
                    commentRecycler.setAdapter(adapter);
                } else {
                    Toast.makeText(CommentActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(CommentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, error -> {
            Log.d("EditPostSuccess", error.getCause() + "");
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + localStorage.getToken());
                Log.d("EditPostSuccess", "Header " + localStorage.getToken() + "\t" + postId);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", postId + "");
                Log.d("EditPostSuccess", "Params " + postId);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(CommentActivity.this);
        queue.add(request);
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}