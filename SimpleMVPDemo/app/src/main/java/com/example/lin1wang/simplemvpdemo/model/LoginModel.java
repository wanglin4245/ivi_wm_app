package com.example.lin1wang.simplemvpdemo.model;

import android.util.Log;

/**
 * Created by lin1.wang on 2018/1/24.
 */

public class LoginModel implements ILoginModel{
    private static final String TAG = "LoginModel::";
    @Override
    public void login(final String username, final String password, final OnLoginListener listener) {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(username.equals("wl") && password.equals("123")){
                    Log.d(TAG, "run: 00000000000");
                    listener.onLoginSuccess();
                } else {
                    Log.d(TAG, "run: 111111111" + username+"2222222222"+password);
                    listener.onLoginFail();
                }
            }
        }, 3000);
    }
}
