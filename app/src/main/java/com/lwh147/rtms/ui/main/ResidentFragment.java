package com.lwh147.rtms.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwh147.rtms.R;
import com.lwh147.rtms.data.model.Resident;
import com.lwh147.rtms.util.RequestUtil;

import java.io.IOException;
import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResidentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResidentFragment extends Fragment {
    private View view;
    private List<Resident> residents;
    private ProgressBar loadingProgressBar;
    private SearchView searchView;
    private Spinner spinnerBuilding;
    private Spinner spinnerEntrance;
    private Spinner spinnerRoom;
    private Map<String, String> searchParams = new HashMap<>();

    public ResidentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */

    public static ResidentFragment newInstance() {
        return new ResidentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_resident, container, false);
        loadingProgressBar = view.findViewById(R.id.loading);
        searchView = view.findViewById(R.id.resident_search);
        spinnerBuilding = view.findViewById(R.id.spinner_building);
        spinnerEntrance = view.findViewById(R.id.spinner_entrance);
        spinnerRoom = view.findViewById(R.id.spinner_room);

        // 直接展开搜索框
        searchView.setIconifiedByDefault(false);
        // 提交按钮是否显示
        searchView.setSubmitButtonEnabled(true);

        // 设置搜索文本监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    startSearch(query);
                    return true;
                }
                showMessage("请输入查询关键字！");
                return false;
            }

            // 当搜索内容改变时触发该方法，时刻监听输入搜索框的值
            @Override
            public boolean onQueryTextChange(String newText) {
                // if (!TextUtils.isEmpty(newText)) {
                //     info = newText;         //  newText输入搜索框的值
                //     listView.setFilterText(newText);
                // } else {
                //     listView.clearTextFilter();
                // }
                // return false;
                return true;
            }
        });

        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    searchParams.put("building", getResources().getStringArray(R.array.frag_resident_building)[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerEntrance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    searchParams.put("entrance", getResources().getStringArray(R.array.frag_resident_entrance)[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    searchParams.put("room", getResources().getStringArray(R.array.frag_resident_room)[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loadingProgressBar.setVisibility(View.VISIBLE);

        searchParams.put("pageSize", "20");

        // 模拟数据
        initData(searchParams);

        // 初始化recyclerView
        initRecyclerView();

        // Inflate the layout for this fragment
        return view;
    }

    public void initRecyclerView() {
        // 获取recyclerview
        RecyclerView recyclerView = view.findViewById(R.id.frag_temp_recycler_view);
        // 创建并设置adapter
        ResidentAdapter residentAdapter = new ResidentAdapter(residents);
        //必须先设置LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(residentAdapter);
        // 设置item的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
    }

    public void initData(final Map<String, String> params) {
        residents = new ArrayList<>();
        new Thread() {
            public void run() {
                Log.i("【log.i】", "即将发送请求");
                try {
                    String result = RequestUtil.Get("http://lwh147.natapp1.cc/resident", params);
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
                        showFailed(R.string.frag_resident_search_empty);
                        return;
                    }
                    // 请求的数据转换为java model
                    int i = 0;
                    for (Object o : tempList) {
                        JSONObject jsonObject1 = (JSONObject) o;
                        Resident resident = new Resident();
                        resident.setOrder(++i);
                        resident.setId(jsonObject1.getLong("id"));
                        resident.setName(jsonObject1.getString("name"));
                        resident.setSex(jsonObject1.getByte("sex"));
                        resident.setBuilding(jsonObject1.getByte("building"));
                        resident.setEntrance(jsonObject1.getByte("entrance"));
                        resident.setRoom(jsonObject1.getInteger("room"));
                        resident.setPhone(jsonObject1.getString("phone"));
                        residents.add(resident);
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
                ResidentAdapter residentAdapter = new ResidentAdapter(residents);
                recyclerView.setAdapter(residentAdapter);
                // 设置item的分割线
                recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
                // 隐藏加载动画
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void startSearch(String query) {
        String regex = "^\\d+$";
        if (query.matches(regex)) {
            // 输入为电话号码
            searchParams.put("phone", query);
        } else {
            // 输入作为姓名
            searchParams.put("name", query);
        }
        searchParams.put("pageSize", "20");
        // 显示加载动画
        loadingProgressBar.setVisibility(View.VISIBLE);
        initData(searchParams);
    }
}