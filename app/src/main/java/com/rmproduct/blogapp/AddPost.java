package com.rmproduct.blogapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rmproduct.blogapp.Fragment.HomeFragment;
import com.rmproduct.blogapp.Models.Post;
import com.rmproduct.blogapp.Models.User;
import com.rmproduct.blogapp.common.Constant;
import com.rmproduct.blogapp.common.LocalStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddPost extends AppCompatActivity {

    private Button post;
    private EditText postDesc;
    private ImageView postImg;
    private TextView changeImg;
    private ProgressDialog dialog;
    private LocalStorage localStorage;
    private Bitmap bitmap = null;
    private static final int CHANGE_POST_IMAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        changeImg = findViewById(R.id.postChangeImg);
        postImg = findViewById(R.id.postAddImg);
        postDesc = findViewById(R.id.postDesc);
        post = findViewById(R.id.post);
        localStorage = new LocalStorage(AddPost.this);
        dialog = new ProgressDialog(AddPost.this);

        dialog.setTitle("Please wait...");
        dialog.setCancelable(true);

        postImg.setImageURI(getIntent().getData());
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        changeImg.setOnClickListener(view -> changeImage());
        post.setOnClickListener(view -> {
            String desc = postDesc.getText().toString().trim();
            if (!TextUtils.isEmpty(desc)) {
                dialog.show();
                postBlog(desc);
            } else {
                dialog.dismiss();
                postDesc.setError("Please describe your post.");
                postDesc.requestFocus();
                return;
            }
        });
    }

    private void postBlog(String desc) {
        @SuppressLint("NotifyDataSetChanged") StringRequest request = new StringRequest(Request.Method.POST, Constant.POST, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject postObject = object.getJSONObject("post");
                    JSONObject userObject = postObject.getJSONObject("user");

                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setUsername(userObject.getString("name") + " " + userObject.getString("lastname"));
                    user.setPhoto(userObject.getString("photo"));

                    Post post = new Post();
                    post.setUser(user);
                    post.setId(postObject.getInt("id"));
                    post.setLike(0);
                    post.setComment(0);
                    post.setSelfLike(false);
                    post.setPhoto(postObject.getString("photo"));
                    post.setDesc(postObject.getString("desc"));
                    post.setDate(postObject.getString("created_at"));

                    HomeFragment.postList.add(0, post);
                    HomeFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                    Log.d("AddPost", "JSONObject Called.");
                    dialog.dismiss();
                    startActivity(new Intent(AddPost.this, HomeActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
                Toast.makeText(AddPost.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, error -> {
            dialog.dismiss();
            Log.d("AddPost", error.toString() + "");
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + localStorage.getToken());
                Log.d("AddPost", "Header");
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("desc", desc);
                map.put("photo", bitmapToString(bitmap));
                Log.d("AddPost", "Params");
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AddPost.this);
        queue.add(request);
    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] array = outputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }
        return "";
    }

    private void changeImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CHANGE_POST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHANGE_POST_IMAGE && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            postImg.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}