package com.example.lin1wang.simplemvpdemo.view;

/**
 * Created by lin1.wang on 2018/1/24.
 */

public interface ILoginView {
    void initView();
    void initEvent();
    void onClearText();
    void showProgress();
    void hideProgress();
    void loginSuccess();
    void loginFail();
}
