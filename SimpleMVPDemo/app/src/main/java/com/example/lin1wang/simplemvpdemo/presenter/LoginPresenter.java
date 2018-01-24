package com.example.lin1wang.simplemvpdemo.presenter;

import android.util.Log;

import com.example.lin1wang.simplemvpdemo.model.ILoginModel;
import com.example.lin1wang.simplemvpdemo.view.ILoginView;

/**
 * Created by lin1.wang on 2018/1/24.
 */

public class LoginPresenter implements ILoginPresenter,ILoginModel.OnLoginListener{

    private ILoginView mView;
    private ILoginModel mModel;
    private static final String TAG = "LoginPresenter";

    public LoginPresenter(ILoginView view, ILoginModel model){
        mView = view;
        mModel = model;
    }

    @Override
    public void clear() {
        mView.onClearText();
    }

    @Override
    public void doLogin(String name, String password) {
        mView.showProgress();
        Log.d(TAG, "doLogin: name=="+name+"  password=="+password);
        mModel.login(name, password, this);
    }

    @Override
    public void onLoginSuccess() {
        mView.hideProgress();
        mView.loginSuccess();
    }

    @Override
    public void onLoginFail() {
        mView.hideProgress();
        mView.loginFail();
    }
}
