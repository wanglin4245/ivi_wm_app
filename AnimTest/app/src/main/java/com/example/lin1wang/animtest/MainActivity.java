package com.example.lin1wang.animtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (TextView) findViewById(R.id.m_text);
        Button btn1 = (Button) findViewById(R.id.btn_rotate);
        Button btn2 = (Button) findViewById(R.id.btn_scale);
        Button btn3 = (Button) findViewById(R.id.btn_alpha);
        Button btn4 = (Button) findViewById(R.id.btn_translate);
        Button btn5 = (Button) findViewById(R.id.btn_integrate);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Animation animation;
        switch (v.getId()){
            case R.id.btn_rotate:
                animation = AnimationUtils.loadAnimation(this,R.anim.rotate_demo);
                mText.startAnimation(animation);
                break;
            case R.id.btn_scale:
                animation = AnimationUtils.loadAnimation(this,R.anim.scale_demo);
                mText.startAnimation(animation);
                break;
            case R.id.btn_alpha:
                animation = AnimationUtils.loadAnimation(this,R.anim.alpha_demo);
                mText.startAnimation(animation);
                break;
            case R.id.btn_translate:
                animation = AnimationUtils.loadAnimation(this,R.anim.translate_demo);
                mText.startAnimation(animation);
                break;
            case R.id.btn_integrate:
                animation = AnimationUtils.loadAnimation(this,R.anim.set_demo);
                mText.startAnimation(animation);
                break;
        }
    }
}
