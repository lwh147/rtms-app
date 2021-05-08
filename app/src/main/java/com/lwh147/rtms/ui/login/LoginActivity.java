package com.lwh147.rtms.ui.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lwh147.rtms.R;
import com.lwh147.rtms.data.model.LoggedInUser;
import com.lwh147.rtms.ui.main.MainActivity;
import com.lwh147.rtms.util.MD5Util;
import com.lwh147.rtms.util.RequestUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录界面java
 **/
public class LoginActivity extends AppCompatActivity {
    public static final String LOGIN_API = "http://lwh147.natapp1.cc/admin/loginFromApp";

    /**
     * 用于保存登陆界面数据
     **/
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置基本视图布局
        setContentView(R.layout.activity_login);

        // 创建数据存储对象
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        // 获取一些空间便于之后操作
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        // 设置账号密码合法性校验器监听
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        // 设置账号密码输入监听器
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        // 添加监听器
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        // loginButton.setEnabled(true);

        // 设置输入完成，可以使用回车进行登录
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                }
                return false;
            }
        });

        // 登录按钮监听
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏颜色为白色
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
            // 实现状态栏图标和文字颜色为暗色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    // 登陆验证
    private void login(final String username, final String password) {
        new Thread() {
            public void run() {
                Map<String, String> data = new HashMap<>();
                data.put("account", username);
                data.put("password", MD5Util.getMD5Str(password));
                Log.i("【log.i】", "即将发送请求，密码为：" + MD5Util.getMD5Str(password));
                try {
                    String result = RequestUtil.Post(LOGIN_API, JSON.toJSONString(data));
                    Log.i("【log.i】请求结果：", result);
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.getInteger("code") != 0) {
                        // 显示错误信息
                        showLoginFailed(R.string.login_failed);
                        return;
                    }
                    JSONObject adminInfo = jsonObject.getJSONObject("data");
                    String id = adminInfo.getString("id");
                    String name = adminInfo.getString("name");
                    String phone = adminInfo.getString("phone");

                    // 跳转到首页
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    // 携带登陆用户信息
                    intent.putExtra("LoggedInUser", JSON.toJSONString(new LoggedInUser(id, name, phone)));
                    startActivity(intent);

                    // 登陆成功，欢迎信息
                    updateUiWithUser(name);

                    finishThis();
                } catch (IOException e) {
                    e.printStackTrace();
                    showLoginFailed(R.string.network_error);
                }
            }
        }.start();

        // 直接跳转到首页
        // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // startActivity(intent);
        // updateUiWithUser("test");
        // this.finish();
    }

    private void updateUiWithUser(final String name) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 隐藏加载动画
                findViewById(R.id.loading).setVisibility(View.GONE);
                String welcome = getString(R.string.welcome) + name;
                Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoginFailed(@StringRes final Integer errorString) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 隐藏加载动画
                findViewById(R.id.loading).setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void finishThis() {
        this.finish();
    }
}