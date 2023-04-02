package com.rmproduct.blogapp.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.rmproduct.blogapp.Activity.AuthActivity;
import com.rmproduct.blogapp.Activity.EditUserActivity;
import com.rmproduct.blogapp.Activity.HomeActivity;
import com.rmproduct.blogapp.Adapter.ProfilePostAdapter;
import com.rmproduct.blogapp.Common.Constant;
import com.rmproduct.blogapp.Common.LocalStorage;
import com.rmproduct.blogapp.Models.Post;
import com.rmproduct.blogapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private View view;
    private MaterialToolbar toolbar;
    private CircleImageView imgProfile;
    private TextView txtName, txtPostsCount;
    private Button btnEditAccount;
    private RecyclerView recyclerView;
    private ArrayList<Post> postList;
    private LocalStorage localStorage;
    private ProfilePostAdapter postsAdapter;
    private String imgUrl = "";
    private ProgressDialog dialog;

    public ProfileFragment() {
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_profile, container, false);

        toolbar = view.findViewById(R.id.profileToolBar);
        ((HomeActivity) getContext()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        imgProfile = view.findViewById(R.id.imgAccountProfile);
        txtName = view.findViewById(R.id.txtAccountName);
        txtPostsCount = view.findViewById(R.id.txtAccountPostCount);
        btnEditAccount = view.findViewById(R.id.btnEditAccount);
        localStorage = new LocalStorage(getContext());

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Logging out...");
        dialog.setCancelable(true);

        recyclerView = view.findViewById(R.id.recyclerAccount);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        Picasso.get().load(Constant.URL + "storage/profiles/" + localStorage.getPhoto()).into(imgProfile);
        txtName.setText(localStorage.getName() + " " + localStorage.getLastname());

        btnEditAccount.setOnClickListener(v -> {
            Intent i = new Intent(((HomeActivity) getContext()), EditUserActivity.class);
            i.putExtra("imgUrl", imgUrl);
            startActivity(i);
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_logout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuLogout: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to logout?");
                builder.setPositiveButton("Logout", (dialog, which) -> logOut());
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                });
                builder.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            getPosts();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPosts();
    }

    private void getPosts() {
        postList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, Constant.POST_MY, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("posts"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject p = array.getJSONObject(i);

                        Post post = new Post();
                        post.setPhoto(Constant.URL + "storage/posts/" + p.getString("photo"));
                        postList.add(post);
                    }
                    txtPostsCount.setText(postList.size() + "");
                    postsAdapter = new ProfilePostAdapter(getActivity(), postList);
                    recyclerView.setAdapter(postsAdapter);

                    Log.d("HomeFrag", "JSONObject Called.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, error -> {
            Log.d("HomeFrag", error.toString() + "\t" + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + localStorage.getToken());
                Log.d("HomeFrag", "Header");
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    private void logOut() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LOGOUT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    localStorage.setToken("");
                    localStorage.setEmail("");
                    localStorage.setLogin(false);
                    localStorage.setLastname("");
                    localStorage.setName("");
                    localStorage.setPhoto("");
                    localStorage.setId(0);
                    dialog.dismiss();
                    startActivity(new Intent(getContext(), AuthActivity.class));
                } else {
                    Log.d("ProfileFrag", object.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, error -> {
            dialog.dismiss();
            Log.d("LoginFrag", error.toString() + "");
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + localStorage.getToken());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}
