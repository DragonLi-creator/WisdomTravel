package com.example.holidaytest4.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.holidaytest4.R;
import com.example.holidaytest4.application.MyApplication;
import com.example.holidaytest4.utils.LoginRegisterUtils;
import com.example.holidaytest4.utils.UiUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_userId_login;
    private EditText et_password_login;
    private ImageView return_logo;
    private Button btn_login;
    private TextView register_login_btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initView();
    }

    //获取XML控件
    private void initView() {
        et_userId_login = findViewById(R.id.et_userId_login);
        et_password_login = findViewById(R.id.et_password_login);
        return_logo = findViewById(R.id.iv_return_login);
        //实现了View.OnClickListener接口
        return_logo.setOnClickListener(this);
        btn_login = findViewById(R.id.btn_login);
        register_login_btn = findViewById(R.id.register_login_btn);
        btn_login.setOnClickListener(this);
        register_login_btn.setOnClickListener(this);
        //将状态栏字体变为黑色
        UiUtils.changeStatusBarTextImgColor(this, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                startLogin();
                break;
            case R.id.register_login_btn:
                //跳转到注册界面
                Intent intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_return_login:
                finish();
                break;
        }
    }

    private void startLogin() {
        String userId = et_userId_login.getText().toString();
        String password = et_password_login.getText().toString();
        //用户名存在且密码与用户名对应
        if (LoginRegisterUtils.userExisted(userId) && LoginRegisterUtils.passwordCorrected(userId, password)) {
            Toast.makeText(LoginActivity.this, "登入成功!", Toast.LENGTH_SHORT).show();
            //记录登入状态
            saveLoginState(userId);
            finish();
        } else {
            Toast.makeText(MyApplication.getContext(), "登入出错!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 保存登入状态
     */
    private void saveLoginState(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences("login_state", MODE_PRIVATE).edit();
        editor.putString("userId", userId);
        editor.apply();
    }
}
