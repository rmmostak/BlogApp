package com.rmproduct.blogapp.Activity;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.rmproduct.blogapp.R;
import com.rmproduct.blogapp.Common.Constant;
import com.rmproduct.blogapp.Common.LocalStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    private TextView choosePic;
    private CircleImageView pPic;
    private TextInputEditText name, lastName;
    private Button cont;
    private static final int GALLERY_ADD_PROFILE = 1;
    private Bitmap bitmap = null;
    private ProgressDialog dialog;
    private LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        choosePic = findViewById(R.id.choosePic);
        pPic = findViewById(R.id.pPic);
        name = findViewById(R.id.name);
        lastName = findViewById(R.id.lastName);
        cont = findViewById(R.id.cont);
        localStorage = new LocalStorage(UserInfoActivity.this);
        dialog = new ProgressDialog(UserInfoActivity.this);
        dialog.setTitle("Loading...");
        dialog.setCancelable(false);

        choosePic.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_ADD_PROFILE);
        });

        cont.setOnClickListener(view -> {
            dialog.show();
            String _name = name.getText().toString().trim();
            String _lastName = lastName.getText().toString().trim();
            if (!TextUtils.isEmpty(_name)) {
                if (!TextUtils.isEmpty(_lastName)) {
                    saveData(_name, _lastName);
                } else {
                    dialog.dismiss();
                    lastName.setError("Please enter your last name.");
                    lastName.requestFocus();
                    return;
                }
            } else {
                dialog.dismiss();
                name.setError("Please enter name.");
                name.requestFocus();
                return;
            }
        });
    }

    private void saveData(String name, String lastName) {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.USER_DATA, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    dialog.dismiss();
                    localStorage.setPhoto(object.getString("photo"));
                    Log.d("UserInfo", "JSONObject Called.");
                    Toast.makeText(UserInfoActivity.this, "Registration success! Login now.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(UserInfoActivity.this, HomeActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
                Toast.makeText(UserInfoActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, error -> {
            dialog.dismiss();
            Log.d("UserInfo", error.toString() + "");
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + localStorage.getToken());
                Log.d("UserInfo", "Header");
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("lastname", lastName);
                map.put("photo", bitmapToString(bitmap));
                Log.d("UserInfo", "Params");
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(UserInfoActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_ADD_PROFILE && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            pPic.setImageURI(imgUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(UserInfoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}