package com.rmproduct.blogapp.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.rmproduct.blogapp.HomeActivity;
import com.rmproduct.blogapp.R;
import com.rmproduct.blogapp.UserInfo;
import com.rmproduct.blogapp.common.Constant;
import com.rmproduct.blogapp.common.LocalStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    private View view;
    private TextInputEditText emailIn, passwordIn;
    private Button login, signup;
    private ProgressDialog dialog;
    private LocalStorage localStorage;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        localStorage = new LocalStorage(getActivity());
        if (localStorage.getLogin()) {
            startActivity(new Intent(getContext(), HomeActivity.class));
        }

        view = inflater.inflate(R.layout.login_fragment, container, false);

        emailIn = view.findViewById(R.id.email);
        passwordIn = view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);
        signup = view.findViewById(R.id.signup);
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setTitle("Loading...");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SignupFragment()).commit();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                String email = emailIn.getText().toString().trim();
                String pass = passwordIn.getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {
                    if (!TextUtils.isEmpty(pass)) {
                        loginHere(email, pass);
                    } else {
                        dialog.dismiss();
                        passwordIn.setError("Please enter password.");
                        passwordIn.requestFocus();
                        return;
                    }
                } else {
                    dialog.dismiss();
                    emailIn.setError("Please enter your registered email.");
                    emailIn.requestFocus();
                    return;
                }
            }
        });

        return view;
    }

    private void loginHere(String email, String password) {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.LOGIN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject user = object.getJSONObject("user");
                    localStorage.setName(user.getString("name"));
                    localStorage.setLastname(user.getString("lastname"));
                    localStorage.setPhoto(user.getString("photo"));
                    localStorage.setToken(object.getString("token"));
                    localStorage.setLogin(true);
                    dialog.dismiss();
                    if (!TextUtils.isEmpty(user.getString("name"))) {
                        startActivity(new Intent(getContext(), HomeActivity.class));
                    } else {
                        startActivity(new Intent(getContext(), UserInfo.class));
                    }
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
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("password", password);
                Log.d("LoginFrag", "LoginMap Called.");
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}
