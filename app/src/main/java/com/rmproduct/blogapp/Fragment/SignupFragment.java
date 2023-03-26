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
import com.rmproduct.blogapp.R;
import com.rmproduct.blogapp.UserInfo;
import com.rmproduct.blogapp.common.Constant;
import com.rmproduct.blogapp.common.LocalStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {
    private View view;
    private TextInputEditText emailIn, passwordIn, cPasswordIn;
    private Button login, signup;
    private ProgressDialog dialog;
    private LocalStorage localStorage;

    public SignupFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_fragment, container, false);

        emailIn = view.findViewById(R.id.emails);
        passwordIn = view.findViewById(R.id.passwords);
        cPasswordIn = view.findViewById(R.id.cPasswords);
        login = view.findViewById(R.id.logins);
        signup = view.findViewById(R.id.signups);
        localStorage = new LocalStorage(getActivity());
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setTitle("Loading...");

        login.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new LoginFragment()).commit();
        });

        signup.setOnClickListener(view -> {
            dialog.show();
            String email = emailIn.getText().toString().trim();
            String pass = passwordIn.getText().toString().trim();
            String cPass = cPasswordIn.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                if (!TextUtils.isEmpty(pass)) {
                    if (!TextUtils.isEmpty(cPass)) {
                        if (pass.equals(cPass)) {
                            signUp(email, pass, cPass);
                        } else {
                            dialog.dismiss();
                            cPasswordIn.setError("Your confirm password is not matched.");
                            cPasswordIn.requestFocus();
                            return;
                        }
                    } else {
                        dialog.dismiss();
                        cPasswordIn.setError("Please enter confirm password.");
                        cPasswordIn.requestFocus();
                        return;
                    }
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
        });

        return view;
    }

    private void signUp(String email, String pass, String cPass) {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.REGISTER, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject user = object.getJSONObject("user");
                    localStorage.setId(user.getInt("id"));
                    localStorage.setName(user.getString("name"));
                    localStorage.setLastname(user.getString("lastname"));
                    localStorage.setPhoto(user.getString("photo"));
                    localStorage.setToken(object.getString("token"));
                    localStorage.setLogin(true);
                    dialog.dismiss();
                    Log.d("SignupFrag", "JSONObject Called.");
                    Toast.makeText(getActivity(), "Registration success! Login now.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity(), UserInfo.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, error -> {
            dialog.dismiss();
            Log.d("SignupFrag", error.toString() + "");
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("password", pass);
                Log.d("SignupFrag", "LoginMap Called.");
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}
