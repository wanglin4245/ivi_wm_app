package com.example.lin1wang.simplemvpdemo.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lin1wang.simplemvpdemo.R;
import com.example.lin1wang.simplemvpdemo.model.LoginModel;
import com.example.lin1wang.simplemvpdemo.presenter.ILoginPresenter;
import com.example.lin1wang.simplemvpdemo.presenter.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements ILoginView{

    private EditText mUserName;
    private EditText mPassword;
    private Button mLogin;
    private Button mClear;
    private ProgressDialog progressDialog;

    private ILoginPresenter mPresenter;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEvent();
    }

    @Override
    public void initView() {
        mUserName = (EditText) findViewById(R.id.et_name);
        mPassword = (EditText) findViewById(R.id.et_password);
        mLogin = (Button) findViewById(R.id.btn_login);
        mClear = (Button) findViewById(R.id.btn_clear);

        mPresenter = new LoginPresenter(this, new LoginModel());
    }

    @Override
    public void initEvent() {

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mUserName.getText().toString();
                String password = mPassword.getText().toString();
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("登录中...");
                Log.d(TAG, "onClick: name=="+name+"  password=="+password);
                mPresenter.doLogin(name,password);
            }
        });
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clear();
            }
        });
    }

    @Override
    public void onClearText() {
        mUserName.setText("");
        mPassword.setText("");
        Toast.makeText(this, "Clear", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void loginSuccess() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginFail() {
        Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
    }
}
