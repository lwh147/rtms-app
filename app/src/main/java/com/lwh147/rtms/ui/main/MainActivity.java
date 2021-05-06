package com.lwh147.rtms.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.navigation.NavigationView;
import com.lwh147.rtms.R;
import com.lwh147.rtms.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    //定义一个变量，来标识是否退出
    private static boolean isExit = false;

    //实现按两次后退才退出
    @SuppressLint("HandlerLeak")
    private static final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置基本视图布局
        setContentView(R.layout.activity_main);

        // 设置toolbar
        setSupportActionBar(this.<Toolbar>findViewById(R.id.toolbar));

        // 获取导航栏实体
        final ActionBar actionBar = getSupportActionBar();
        final NavigationView navigationView = findViewById(R.id.nav_header);

        // 显示home按钮
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 设置home按钮图标
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        // 设置home按钮标题
        actionBar.setTitle(R.string.nav_temp_info);

        // 设置导航默认激活菜单项
        navigationView.setCheckedItem(R.id.nav_temp_info);

        // 设置默认激活菜单项内容
        replaceFragment(TempFragment.newInstance());

        // 设置菜单监听回调
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // 获取滑动菜单
                final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
                // 点击某个菜单项后关闭滑动菜单
                drawerLayout.closeDrawers();

                // 设置home按钮标题
                final ActionBar actionBar = getSupportActionBar();
                assert actionBar != null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_temp_info:
                        actionBar.setTitle(R.string.nav_temp_info);
                        // 其他操作
                        replaceFragment(TempFragment.newInstance());
                        break;
                    case R.id.nav_resident:
                        actionBar.setTitle(R.string.nav_resident);
                        // 其他操作
                        replaceFragment(ResidentFragment.newInstance());
                        break;
                    case R.id.nav_statistcs:
                        actionBar.setTitle(R.string.nav_statistcs);
                        // 其他操作
                        replaceFragment(StatistcsFragment.newInstance());
                        break;
                    case R.id.nav_logout:
                        // 跳转到登录页面
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        // 携带登陆用户信息
                        // intent.putExtra("LoggedInUser", JSON.toJSONString(new LoggedInUser(id, name, phone)));
                        startActivity(intent);
                        // 结束当前activity，使其不能通过返回返回至当前界面
                        exit();
                        break;
                    default:
                        break;
                }

                return true;
            }
        });

        // 管理员信息初始化
        adminInfoInit();
    }

    /**
     * 管理员信息初始化
     **/
    public void adminInfoInit() {
        // 获取跳转过来携带的参数
        final Intent intent = getIntent();
        String params = intent.getStringExtra("LoggedInUser");
        if (params == null) {
            return;
        }
        Log.i("获取到的已登录信息：", params);
        JSONObject jsonObject = JSON.parseObject(params);
        final NavigationView navigationView = findViewById(R.id.nav_header);
        final View headerView = navigationView.getHeaderView(0);
        final TextView textViewAdminName = headerView.findViewById(R.id.admin_name);
        final TextView textViewAdminPhone = headerView.findViewById(R.id.admin_phone);
        String name = getString(R.string.nav_admin_name_prefix) + jsonObject.getString("displayName");
        String phone = getString(R.string.nav_admin_phone_prefix) + jsonObject.getString("phone");
        textViewAdminName.setText(name);
        textViewAdminPhone.setText(phone);
    }

    /**
     * 导航栏菜单点击处理
     *
     * @param item
     * @return boolean
     **/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 当点击home按钮时打开滑动菜单
        if (item.getItemId() == android.R.id.home) {
            ((DrawerLayout) findViewById(R.id.drawerLayout)).openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 切换fragment
     *
     * @param fragment 要切换的fragment实例
     * @return void
     **/
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    /**
     * 重写按下返回键处理
     **/
    @Override
    public void onBackPressed() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), R.string.exit_confirm, Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            // 退出应用
            this.finish();
        }
    }

    /**
     * 给回调函数使用
     **/
    private void exit() {
        this.finish();
    }

}