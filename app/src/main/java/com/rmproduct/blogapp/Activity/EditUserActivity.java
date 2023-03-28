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
import com.rmproduct.blogapp.Common.Constant;
import com.rmproduct.blogapp.Common.LocalStorage;
import com.rmproduct.blogapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserActivity extends AppCompatActivity {

    private TextView editChoosePic;
    private CircleImageView editPic;
    private TextInputEditText editName, editLastName;
    private Button editSave;
    private static final int GALLERY_ADD_PROFILE = 1;
    private Bitmap bitmap = null;
    private ProgressDialog dialog;
    private LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        editChoosePic = findViewById(R.id.editChoosePic);
        editPic = findViewById(R.id.editPic);
        editName = findViewById(R.id.editName);
        editLastName = findViewById(R.id.editLastName);
        editSave = findViewById(R.id.editSave);
        localStorage = new LocalStorage(EditUserActivity.this);
        dialog = new ProgressDialog(EditUserActivity.this);
        dialog.setTitle("Loading...");
        dialog.setCancelable(false);

        Picasso.get().load(Constant.URL+"storage/profiles/"+localStorage.getPhoto()).into(editPic);
        editName.setText(localStorage.getName());
        editLastName.setText(localStorage.getLastname());

        editChoosePic.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_ADD_PROFILE);
        });

        editSave.setOnClickListener(view -> {
            dialog.show();
            String _name = editName.getText().toString().trim();
            String _lastName = editLastName.getText().toString().trim();
            if (!TextUtils.isEmpty(_name)) {
                if (!TextUtils.isEmpty(_lastName)) {
                    saveInfo(_name, _lastName);
                } else {
                    dialog.dismiss();
                    editLastName.setError("Please enter your last name.");
                    editLastName.requestFocus();
                    return;
                }
            } else {
                dialog.dismiss();
                editName.setError("Please enter name.");
                editName.requestFocus();
                return;
            }
        });
    }

    private void saveInfo(String name, String lastName) {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.USER_DATA, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    localStorage.setName(name);
                    localStorage.setLastname(lastName);
                    dialog.dismiss();
                    Log.d("UserInfo", "JSONObject Called.");
                    Toast.makeText(EditUserActivity.this, "Registration success! Login now.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(EditUserActivity.this, HomeActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
                Toast.makeText(EditUserActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

        RequestQueue queue = Volley.newRequestQueue(EditUserActivity.this);
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
            editPic.setImageURI(imgUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(EditUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goBack(View view) {
        super.onBackPressed();
    }
}