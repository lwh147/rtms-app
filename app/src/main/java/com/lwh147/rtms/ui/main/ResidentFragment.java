package com.lwh147.rtms.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.lwh147.rtms.R;
import com.lwh147.rtms.data.model.Resident;
import com.lwh147.rtms.util.LineChartUtil;
import com.lwh147.rtms.util.RequestUtil;

import java.io.IOException;
import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResidentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResidentFragment extends Fragment {
    public static final String RESIDENT_API = "http://lwh147.natapp1.cc/resident";
    public static final String RESIDENT_TEMP_API = "http://lwh147.natapp1.cc/tempInfo/charData/resident";

    private ProgressBar loadingProgressBar;
    private SearchView searchView;
    private Spinner spinnerBuilding;
    private Spinner spinnerEntrance;
    private Spinner spinnerRoom;
    private Spinner spinnerPage;
    private RecyclerView recyclerView;

    private JSONObject pageInfo;
    private final List<Resident> residents = new ArrayList<>();
    private final Map<String, String> searchParams = new HashMap<>();

    private ArrayAdapter<String> spinnerAdapter;

    private MainActivity mainActivity;

    private JSONArray chartData;
    private Resident selectedResident;
    private Spinner spinnerDuration;
    private LineChart residentTempChart;
    private ProgressBar loadingResident;

    private final Map<String, Integer> durationDesToInt;

    public ResidentFragment() {
        // Required empty public constructor
        durationDesToInt = new HashMap<>();
        durationDesToInt.put("24小时内", 1);
        durationDesToInt.put("三天内", 3);
        durationDesToInt.put("七天内", 7);
        durationDesToInt.put("15天内", 15);
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
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resident, container, false);

        loadingProgressBar = view.findViewById(R.id.loading);
        searchView = view.findViewById(R.id.resident_search);
        spinnerBuilding = view.findViewById(R.id.spinner_building);
        spinnerEntrance = view.findViewById(R.id.spinner_entrance);
        spinnerRoom = view.findViewById(R.id.spinner_room);
        spinnerPage = view.findViewById(R.id.spinner_page);
        recyclerView = view.findViewById(R.id.frag_temp_recycler_view);

        searchViewInit();
        spinnerInit();
        recyclerViewInit();

        // 默认查询一页为20条数据
        searchParams.put("pageSize", "20");

        startSearch();

        return view;
    }

    private void searchViewInit() {
        // 直接展开搜索框
        searchView.setIconifiedByDefault(false);
        // 提交按钮是否显示
        searchView.setSubmitButtonEnabled(true);

        // 设置搜索文本监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                // searchView会自动判断query是否为空决定是否触发此事件
                startSearch(query);
                Log.i("【log.i】查询条件：", JSON.toJSONString(searchParams));
                return true;
            }

            // 当搜索内容改变时触发该方法，时刻监听输入搜索框的值
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    private void spinnerInit() {
        spinnerBuildingInit();
        spinnerEntranceInit();
        spinnerRoomInit();
        spinnerPageInit();
    }

    private void spinnerBuildingInit() {
        // 获取楼栋列表，不能在xml中直接初始化，因为xml的初始化操作会触发选择变更监听
        List<String> list = Arrays.asList(getResources().getStringArray(R.array.frag_resident_building));
        SpinnerAdapter adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item, list);
        spinnerBuilding.setAdapter(adapter);
        // 不触发监听事件的情况下设置楼栋列表
        spinnerBuilding.setSelection(0, false);
        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("【log.i】", "楼号选择变更");
                if (position != 0) {
                    searchParams.put("building", getResources().getStringArray(R.array.frag_resident_building)[position]);
                    startSearch(searchView.getQuery().toString(), 1);
                    return;
                }
                searchParams.remove("building");
                startSearch(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void spinnerEntranceInit() {
        // 获取单元列表，不能在xml中直接初始化，因为xml的初始化操作会触发选择变更监听
        List<String> list = Arrays.asList(getResources().getStringArray(R.array.frag_resident_entrance));
        SpinnerAdapter adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item, list);
        spinnerEntrance.setAdapter(adapter);
        // 不触发监听事件的情况下设置被选择项
        spinnerEntrance.setSelection(0, false);
        spinnerEntrance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("【log.i】", "单元号选择变更");
                if (position != 0) {
                    searchParams.put("entrance", getResources().getStringArray(R.array.frag_resident_entrance)[position]);
                    startSearch(searchView.getQuery().toString(), 1);
                    return;
                }
                searchParams.remove("entrance");
                startSearch(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void spinnerRoomInit() {
        // 获取房间列表，不能在xml中直接初始化，因为xml的初始化操作会触发选择变更监听
        List<String> list = Arrays.asList(getResources().getStringArray(R.array.frag_resident_room));
        SpinnerAdapter adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item, list);
        spinnerRoom.setAdapter(adapter);
        // 不触发监听事件的情况下设置被选择项
        spinnerRoom.setSelection(0, false);
        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("【log.i】", "房间号选择变更");
                if (position != 0) {
                    searchParams.put("room", getResources().getStringArray(R.array.frag_resident_room)[position]);
                    startSearch(searchView.getQuery().toString(), 1);
                    return;
                }
                searchParams.remove("room");
                startSearch(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void spinnerPageInit() {
        // 获取页码列表，不能在xml中直接初始化，因为xml的初始化操作会触发选择变更监听
        // 因为分页信息之后会更新，所以使用ArrayList<String>类型初始化adapter，String[]初始化的adapter不能更新，只能重新new
        // adapter，重新setAdapter又会触发onSelectedItemChanged事件
        List<String> list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.frag_resident_page)));
        spinnerAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item, list);
        spinnerPage.setAdapter(spinnerAdapter);
        // 不触发监听事件的情况下设置被选择项
        spinnerPage.setSelection(0, false);
        spinnerPage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("【log.i】", "页码选择变更");
                startSearch(Integer.parseInt((String) spinnerPage.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void recyclerViewInit() {
        // 创建并设置adapter
        ResidentAdapter residentAdapter = new ResidentAdapter(residents);
        //必须先设置LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(residentAdapter);
        //设置动画效果
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mainActivity, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    return;
                }
                selectedResident = residents.get(position - 1);
                showDialog();
            }
        }));
        // 设置item的分割线
        if (recyclerView.getItemDecorationCount() == 0) {
            recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
        }
    }

    private void getData() {
        // 加载动画
        loadingProgressBar.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                Log.i("【log.i】", "即将发送请求");
                try {
                    String result = RequestUtil.Get(RESIDENT_API, searchParams);
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
                        // 关闭加载动画
                        showLoading(false);
                        return;
                    }
                    // 加载新数据前清空旧数据
                    residents.clear();
                    // 请求的数据转换为java model
                    pageInfo = data.getJSONObject("pageInfo");
                    int startRow = pageInfo.getInteger("startRow");
                    for (Object o : tempList) {
                        JSONObject jsonObject1 = (JSONObject) o;
                        Resident resident = new Resident();
                        resident.setOrder(startRow++);
                        resident.setId(jsonObject1.getLong("id"));
                        resident.setName(jsonObject1.getString("name"));
                        resident.setSex(jsonObject1.getByte("sex"));
                        resident.setBuilding(jsonObject1.getByte("building"));
                        resident.setEntrance(jsonObject1.getByte("entrance"));
                        resident.setRoom(jsonObject1.getInteger("room"));
                        resident.setPhone(jsonObject1.getString("phone"));
                        residents.add(resident);
                    }

                    // 更新分页信息
                    updatePage();
                    // 更新数据列表
                    updateRecyclerView();
                    // 关闭加载动画
                    showLoading(false);

                } catch (IOException e) {
                    e.printStackTrace();
                    showFailed(R.string.network_error);
                }
            }
        }.start();
    }

    private void updatePage() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 获取页码列表
                Log.i("【log.i】", "即将更新页码");
                // 泛型T必须在使用之前确定，否则类似? extends T或者T ... items 类型的参数不能正常传递，因为T没有被指定
                spinnerAdapter.clear();
                spinnerAdapter.addAll(JSON.parseArray(pageInfo.getString("navigatepageNums"), String.class));
                spinnerAdapter.notifyDataSetChanged();
                // SpinnerAdapter adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item, list);
                // spinnerPage.setAdapter(adapter);
                // 设置当前选中页码
                spinnerPage.setSelection(pageInfo.getInteger("pageNum") - 1, false);
            }
        });
    }

    private void updateRecyclerView() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ResidentAdapter residentAdapter = new ResidentAdapter(residents);
                //必须先设置LayoutManager
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(residentAdapter);
            }
        });
    }

    private void startSearch(String query, Integer pageNum) {
        if (query != null) {
            if (query.isEmpty()) {
                // 查询条件为空
                searchParams.remove("phone");
                searchParams.remove("name");
            } else {
                String regex = "^\\d+$";
                if (query.matches(regex)) {
                    // 输入为电话号码
                    searchParams.put("phone", query);
                } else {
                    // 输入作为姓名
                    searchParams.put("name", query);
                }
            }
        }
        if (pageNum != null) {
            searchParams.put("pageNum", String.valueOf(pageNum));
        }
        getData();
    }

    private void startSearch(String query) {
        startSearch(query, null);
    }

    private void startSearch(Integer pageNum) {
        startSearch(null, pageNum);
    }

    private void startSearch() {
        startSearch(null, null);
    }

    private void showMessage(final String message) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainActivity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showFailed(@StringRes final Integer errorString) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainActivity.getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(final boolean show) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    //初始化并弹出对话框方法
    private void showDialog() {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.dialog, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(mainActivity).setView(view).create();

        TextView residentNameTextView = view.findViewById(R.id.resident_name);
        residentNameTextView.setText(selectedResident.getName() + "最近的体温信息");

        spinnerDuration = view.findViewById(R.id.spinner_duration_resident);
        residentTempChart = view.findViewById(R.id.line_chart_resident);
        loadingResident = view.findViewById(R.id.loading_resident);
        Button btn_cancel_high_opion = view.findViewById(R.id.btn_cancel_high_opion);

        btn_cancel_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        loadingResident.setVisibility(View.VISIBLE);

        spinnerDurationInit();
        getResidentTemp(1);

        dialog.show();
    }

    private void spinnerDurationInit() {
        List<String> list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.frag_statistcs_duration)));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item, list);
        // 设置数据列表
        spinnerDuration.setAdapter(spinnerAdapter);
        // 不触发监听事件的情况下设置被选择项
        spinnerDuration.setSelection(0, false);

        spinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("【log.i】", "时间选择变更");
                String days = getResources().getStringArray(R.array.frag_statistcs_duration)[position];
                getResidentTemp(durationDesToInt.get(days));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getResidentTemp(final Integer days) {
        new Thread() {
            public void run() {
                Log.i("【log.i】", "即将发送请求");
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", String.valueOf(selectedResident.getId()));
                    params.put("days", String.valueOf(days));
                    String result = RequestUtil.Get(RESIDENT_TEMP_API, params);
                    Log.i("【log.i】请求结果：", result);
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.getInteger("code") != 0) {
                        // 显示错误信息
                        Log.i("【log.i】请求失败，错误信息", jsonObject.getString("message"));
                        showFailed(R.string.frag_temp_load_failed);
                        return;
                    }

                    chartData = jsonObject.getJSONArray("data");

                    charInit();

                } catch (IOException e) {
                    e.printStackTrace();
                    showFailed(R.string.network_error);
                }
            }
        }.start();
    }

    private void charInit() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Entry> list = new ArrayList<>();
                // 其中两个参数对应的分别是   X轴   Y轴
                int size = chartData.size();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = chartData.getJSONObject(i);
                    list.add(new Entry(i, jsonObject.getFloat("temp")));
                }

                LineChartUtil lineChartUtil = new LineChartUtil(residentTempChart);
                lineChartUtil.setLineChartData(list, "体温变化", chartData);

                loadingResident.setVisibility(View.GONE);
            }
        });
    }
}