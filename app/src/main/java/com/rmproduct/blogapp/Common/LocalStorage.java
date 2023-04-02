package com.rmproduct.blogapp.Common;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;
    String name, lastname, photo, token, email;
    int id;
    boolean login;

    public LocalStorage(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("LOCAL_STORAGE", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public String getName() {
        name = preferences.getString("NAME", "");
        return name;
    }

    public void setName(String name) {
        editor.putString("NAME", name);
        editor.commit();
        this.name = name;
    }

    public String getLastname() {
        lastname = preferences.getString("LAST_NAME", "");
        return lastname;
    }

    public void setLastname(String lastname) {
        editor.putString("LAST_NAME", lastname);
        editor.commit();
        this.lastname = lastname;
    }

    public String getPhoto() {
        photo = preferences.getString("PHOTO", "");
        return photo;
    }

    public void setPhoto(String photo) {
        editor.putString("PHOTO", photo);
        editor.commit();
        this.photo = photo;
    }

    public String getToken() {
        token = preferences.getString("TOKEN", "");
        return token;
    }

    public void setToken(String token) {
        editor.putString("TOKEN", token);
        editor.commit();
        this.token = token;
    }

    public int getId() {
        id = preferences.getInt("id", 0);
        return id;
    }

    public void setId(int id) {
        editor.putInt("id", id);
        editor.commit();
        this.id = id;
    }

    public boolean getLogin() {
        login = preferences.getBoolean("LOGIN", false);
        return login;
    }

    public void setLogin(boolean login) {
        editor.putBoolean("LOGIN", login);
        editor.commit();
        this.login = login;
    }

    public String getEmail() {
        email = preferences.getString("EMAIL", "");
        return email;
    }

    public void setEmail(String email) {
        editor.putString("EMAIL", email);
        editor.commit();
        this.email = email;
    }
}
