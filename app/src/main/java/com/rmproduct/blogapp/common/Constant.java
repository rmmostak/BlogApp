package com.rmproduct.blogapp.common;

public class Constant {
    public static final String URL = "http://192.168.0.106:8000/";
    public static final String HOME = URL + "api";
    public static final String LOGIN = HOME + "/login";
    public static final String REGISTER = HOME + "/register";
    public static final String USER_DATA = HOME + "/save";
    public static final String POSTS = HOME + "/posts";
    public static final String POST = POSTS + "/create";
    public static final String POST_UPDATE = POSTS + "/update";
    public static final String POST_DELETE = POSTS + "/delete";
    public static final String POST_LIKE = POSTS + "/like";
    public static final String COMMENTS = POSTS + "/comments";
}
