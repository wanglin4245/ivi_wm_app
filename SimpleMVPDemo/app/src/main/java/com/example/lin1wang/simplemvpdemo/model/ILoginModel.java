package com.example.lin1wang.simplemvpdemo.model;

/**
 * Created by lin1.wang on 2018/1/24.
 */

public interface ILoginModel {

    interface OnLoginListener {
        void onLoginSuccess();
        void onLoginFail();
    }

    void login(String username, String password, OnLoginListener listener);
}
