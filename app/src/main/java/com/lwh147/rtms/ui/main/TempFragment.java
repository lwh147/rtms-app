package com.lwh147.rtms.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwh147.rtms.R;
import com.lwh147.rtms.data.model.TempInfo;
import com.lwh147.rtms.util.RequestUtil;

import java.io.IOException;
import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TempFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TempFragment extends Fragment {
    private View view;
    private List<TempInfo> tempInfos;
    private ProgressBar loadingProgressBar;

    public TempFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static TempFragment newInstance() {
        return new TempFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_temp, container, false);
        loadingProgressBar = view.findViewById(R.id.loading);
        loadingProgressBar.setVisibility(View.VISIBLE);
        // 模拟数据
        initData();
        // 初始化recyclerView
        initRecyclerView();
        // Inflate the layout for this fragment
        return view;
    }

    public void initRecyclerView() {
        // 获取recyclerview
        RecyclerView recyclerView = view.findViewById(R.id.frag_temp_recycler_view);
        // 创建并设置adapter
        TempAdapter tempAdapter = new TempAdapter(tempInfos);
        //必须先设置LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(tempAdapter);
        // 设置item的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
    }

    public void initData() {
        tempInfos = new ArrayList<>();
        new Thread() {
            public void run() {
                Map<String, String> params = new HashMap<>();
                // 设置查询条件为最近15天的前20条数据
                params.put("days", "15");
                params.put("pageSize", "20");
                Log.i("【log.i】", "即将发送请求");
                try {
                    String result = RequestUtil.Get("http://lwh147.natapp1.cc/tempInfo", params);
                    Log.i("【log.i】请求结果：", result);
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.getInteger("code") != 0) {
                        // 显示错误信息
                        Log.i("【log.i】请求失败，错误信息", jsonObject.getString("message"));
                        showFailed(R.string.frag_temp_load_failed);
                        return;
                    }
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray tempList = data.getJSONArray("list");

                    // 判断数据是否为空
                    if (tempList.isEmpty()) {
                        showFailed(R.string.frag_temp_empty);
                        return;
                    }
                    // 请求的数据转换为java model
                    for (Object o : tempList) {
                        JSONObject jsonObject1 = (JSONObject) o;
                        TempInfo tempInfo = new TempInfo();
                        tempInfo.setId(jsonObject1.getLong("id"));
                        tempInfo.setTime(jsonObject1.getDate("time"));
                        tempInfo.setTemp(jsonObject1.getFloat("temp"));
                        tempInfo.setResidentId(jsonObject1.getLong("residentId"));
                        tempInfo.setResidentName(jsonObject1.getString("residentName"));
                        tempInfos.add(tempInfo);
                    }
                    updateRecyclerView();

                } catch (IOException e) {
                    e.printStackTrace();
                    showFailed(R.string.network_error);
                }
            }
        }.start();
    }

    private void showMessage(final String message) {
        final MainActivity mainActivity = (MainActivity) this.getActivity();
        assert mainActivity != null;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 隐藏加载动画
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(mainActivity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showFailed(@StringRes final Integer errorString) {
        final MainActivity mainActivity = (MainActivity) this.getActivity();
        assert mainActivity != null;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 隐藏加载动画
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(mainActivity.getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerView() {
        final MainActivity mainActivity = (MainActivity) this.getActivity();
        assert mainActivity != null;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 获取recyclerview
                RecyclerView recyclerView = view.findViewById(R.id.frag_temp_recycler_view);
                // 创建并设置adapter
                TempAdapter tempAdapter = new TempAdapter(tempInfos);
                recyclerView.setAdapter(tempAdapter);
                // 设置item的分割线
                recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
                // 隐藏加载动画
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }
}