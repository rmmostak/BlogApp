package com.rmproduct.blogapp.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rmproduct.blogapp.Fragment.HomeFragment;
import com.rmproduct.blogapp.Models.Post;
import com.rmproduct.blogapp.R;
import com.rmproduct.blogapp.Common.Constant;
import com.rmproduct.blogapp.Common.LocalStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    int postId = 0, position = 0;
    private EditText editDesc;
    private Button editPost;
    private ProgressDialog dialog;
    private LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        editDesc = findViewById(R.id.editDesc);
        editPost = findViewById(R.id.editPost);
        dialog = new ProgressDialog(EditPostActivity.this);
        dialog.setTitle("Updating...");
        dialog.setCancelable(true);
        localStorage = new LocalStorage(EditPostActivity.this);

        postId = getIntent().getIntExtra("postId", 0);
        position = getIntent().getIntExtra("position", 0);
        editDesc.setText(getIntent().getStringExtra("text"));
        editPost.setOnClickListener(view -> {
            dialog.show();
            String desc = editDesc.getText().toString().trim();
            if (!TextUtils.isEmpty(desc)) {
                updatePost(postId, desc);
            } else {
                editDesc.setError("Please write a description.");
                editDesc.requestFocus();
                return;
            }
        });
    }

    private void updatePost(int postId, String desc) {
        @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.POST, Constant.POST_UPDATE, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    Post post = HomeFragment.postList.get(position);
                    post.setDesc(desc);
                    HomeFragment.postList.set(position, post);
                    HomeFragment.recyclerView.getAdapter().notifyItemInserted(position);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    finish();
                } else {
                    Toast.makeText(EditPostActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
                Toast.makeText(EditPostActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, error -> {
            dialog.dismiss();
            Log.d("EditPostSuccess", error.getMessage() + "");
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + localStorage.getToken());
                Log.d("EditPostSuccess", "Header");
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", postId + "");
                map.put("desc", desc);
                Log.d("EditPostSuccess", "Params "+postId);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(EditPostActivity.this);
        queue.add(request);
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}