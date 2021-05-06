package com.lwh147.rtms.ui.login;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.lwh147.rtms.R;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // 用户名校验器
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        Log.i("【log.i】输入的用户名：", username);
        // 用户名为10位，由字母数字下划线组成
        String regex = "^[\\w.]{10}$";
        return username.matches(regex);
    }

    // 密码校验器
    private boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }
        Log.i("【log.i】输入的密码：", password);
        // 密码为6位纯数字
        String regex = "^[\\d]{6}";
        return password.matches(regex);
    }
}