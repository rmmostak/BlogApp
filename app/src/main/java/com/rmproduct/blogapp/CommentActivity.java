package com.rmproduct.blogapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.rmproduct.blogapp.Models.Comment;
import com.rmproduct.blogapp.Models.User;
import com.rmproduct.blogapp.common.Constant;
import com.rmproduct.blogapp.common.LocalStorage;

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
    private LocalStorage localStorage;
    private int postId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentList = new ArrayList<>();
        commentRecycler = findViewById(R.id.commentRecycler);
        commentRecycler.setHasFixedSize(true);
        commentRecycler.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        postId = getIntent().getIntExtra("postId", 0);
        localStorage = new LocalStorage(CommentActivity.this);

        getComments();
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