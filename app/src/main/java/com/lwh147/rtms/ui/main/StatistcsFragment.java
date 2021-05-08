package com.lwh147.rtms.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.lwh147.rtms.R;
import com.lwh147.rtms.data.model.TempInfo;
import com.lwh147.rtms.util.ExcelUtil;
import com.lwh147.rtms.util.LineChartUtil;
import com.lwh147.rtms.util.RequestUtil;
import jxl.write.WriteException;

import java.io.IOException;
import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatistcsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatistcsFragment extends Fragment {
    private static final String TOTAL_API = "http://lwh147.natapp1.cc/tempInfo/total";
    private static final String CHAR_DATA_API = "http://lwh147.natapp1.cc/tempInfo/charData";
    private static final String CHAR_DATA_RESIDENT_API = "http://lwh147.natapp1.cc/tempInfo/charData/all";

    private Integer total;
    private Integer unnormal;

    private JSONArray charDataTotal;
    private JSONArray charDataUnnormal;

    private MainActivity mainActivity;

    private ProgressBar loadingProgressBarTotal;
    private ProgressBar loadingProgressBarUnnormal;
    private LineChart lineChartTotal;
    private LineChart lineChartUnnormal;
    private TextView textViewTotal;
    private TextView textViewUnnormal;
    private Spinner spinnerDurationUnnormal;
    private Spinner spinnerDurationTotal;

    private final Map<String, Integer> durationDesToInt;

    public StatistcsFragment() {
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
    public static StatistcsFragment newInstance() {
        return new StatistcsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistcs, container, false);

        loadingProgressBarTotal = view.findViewById(R.id.loading_total);
        loadingProgressBarUnnormal = view.findViewById(R.id.loading_unnormal);
        lineChartTotal = view.findViewById(R.id.line_chart_total);
        lineChartUnnormal = view.findViewById(R.id.line_chart_unnormal);
        textViewTotal = view.findViewById(R.id.text_total);
        textViewUnnormal = view.findViewById(R.id.text_unnormal);
        spinnerDurationUnnormal = view.findViewById(R.id.spinner_duration_unnormal);
        spinnerDurationTotal = view.findViewById(R.id.spinner_duration_total);

        spinnerDurationInit();

        getTotal("false");
        getTotal("true");

        getLineChartUnnormalData(1);
        getLineChartTotalData(1);

        return view;
    }

    private void textViewUpdate() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (total != null) {
                    textViewTotal.setText(String.valueOf(total));
                }
                if (unnormal != null) {
                    textViewUnnormal.setText(String.valueOf(unnormal));
                }
            }
        });
    }

    private void spinnerDurationInit() {
        List<String> list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.frag_statistcs_duration)));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item, list);
        // 设置数据列表
        spinnerDurationUnnormal.setAdapter(spinnerAdapter);
        spinnerDurationTotal.setAdapter(spinnerAdapter);
        // 不触发监听事件的情况下设置被选择项
        spinnerDurationUnnormal.setSelection(0, false);
        spinnerDurationTotal.setSelection(0, false);

        spinnerDurationUnnormal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("【log.i】", "异常人次折线图时间选择变更");
                String days = getResources().getStringArray(R.array.frag_statistcs_duration)[position];
                getLineChartUnnormalData(durationDesToInt.get(days));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDurationTotal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("【log.i】", "总共人次折线图时间选择变更");
                String days = getResources().getStringArray(R.array.frag_statistcs_duration)[position];
                getLineChartTotalData(durationDesToInt.get(days));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getTotal(final String normal) {
        new Thread() {
            public void run() {
                Log.i("【log.i】", "即将发送请求");
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("normal", normal);
                    String result = RequestUtil.Get(TOTAL_API, params);
                    Log.i("【log.i】请求结果：", result);
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.getInteger("code") != 0) {
                        // 显示错误信息
                        Log.i("【log.i】请求失败，错误信息", jsonObject.getString("message"));
                        showFailed(R.string.frag_temp_load_failed);
                        return;
                    }

                    Integer num = jsonObject.getInteger("data");

                    if (Boolean.parseBoolean(normal)) {
                        total = num;
                    } else {
                        unnormal = num;
                    }

                    textViewUpdate();

                } catch (IOException e) {
                    e.printStackTrace();
                    showFailed(R.string.network_error);
                }
            }
        }.start();
    }

    private void lineChartUnnormalInit() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Entry> list = new ArrayList<>();
                // 其中两个参数对应的分别是   X轴   Y轴
                int size = charDataUnnormal.size();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = charDataUnnormal.getJSONObject(i);
                    list.add(new Entry(i, jsonObject.getInteger("num")));
                }

                LineChartUtil lineChartUtil = new LineChartUtil(lineChartUnnormal);
                lineChartUtil.setLineChartData(list, "人次变化", charDataUnnormal);

                loadingProgressBarUnnormal.setVisibility(View.GONE);
            }
        });
    }

    private void lineChartTotalInit() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Entry> list = new ArrayList<>();
                // 其中两个参数对应的分别是   X轴   Y轴
                int size = charDataTotal.size();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = charDataTotal.getJSONObject(i);
                    list.add(new Entry(i, jsonObject.getInteger("num")));
                }

                LineChartUtil lineChartUtil = new LineChartUtil(lineChartTotal);
                lineChartUtil.setLineChartData(list, "人次变化", charDataTotal);

                loadingProgressBarTotal.setVisibility(View.GONE);
            }
        });
    }

    private void getLineChartUnnormalData(final Integer days) {
        loadingProgressBarUnnormal.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                Log.i("【log.i】", "即将发送请求");
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("normal", "false");
                    params.put("days", String.valueOf(days));
                    String result = RequestUtil.Get(CHAR_DATA_API, params);
                    Log.i("【log.i】请求结果：", result);
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.getInteger("code") != 0) {
                        // 显示错误信息
                        Log.i("【log.i】请求失败，错误信息", jsonObject.getString("message"));
                        showFailed(R.string.frag_temp_load_failed);
                        showLoading(loadingProgressBarUnnormal, false);
                        return;
                    }

                    charDataUnnormal = jsonObject.getJSONArray("data");

                    lineChartUnnormalInit();

                } catch (IOException e) {
                    e.printStackTrace();
                    showFailed(R.string.network_error);
                }
            }
        }.start();
    }

    private void getLineChartTotalData(final Integer days) {
        loadingProgressBarTotal.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                Log.i("【log.i】", "即将发送请求");
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("normal", "true");
                    params.put("days", String.valueOf(days));
                    String result = RequestUtil.Get(CHAR_DATA_API, params);
                    Log.i("【log.i】请求结果：", result);
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.getInteger("code") != 0) {
                        // 显示错误信息
                        Log.i("【log.i】请求失败，错误信息", jsonObject.getString("message"));
                        showFailed(R.string.frag_temp_load_failed);
                        showLoading(loadingProgressBarTotal, false);
                        return;
                    }

                    charDataTotal = jsonObject.getJSONArray("data");

                    lineChartTotalInit();

                } catch (IOException e) {
                    e.printStackTrace();
                    showFailed(R.string.network_error);
                }
            }
        }.start();
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

    private void showLoading(final ProgressBar progressBar, final boolean show) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void download() {
        loadingProgressBarUnnormal.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                Log.i("【log.i】", "即将发送请求");
                try {
                    Map<String, String> params = new HashMap<>();
                    String result = RequestUtil.Get(CHAR_DATA_RESIDENT_API);
                    Log.i("【log.i】请求结果：", result);
                    JSONObject jsonObject = JSON.parseObject(result);
                    if (jsonObject.getInteger("code") != 0) {
                        // 显示错误信息
                        Log.i("【log.i】请求失败，错误信息", jsonObject.getString("message"));
                        showFailed(R.string.frag_temp_load_failed);
                        showLoading(loadingProgressBarUnnormal, false);
                        return;
                    }

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<TempInfo> tempInfoList = jsonArray.toJavaList(TempInfo.class);
                    String fileName = "最近15天所有体温数据";
                    if (ExcelUtil.generateExcel(fileName, mainActivity, tempInfoList)) {
                        showMessage("导出成功！文件位置：" +
                                Objects.requireNonNull(mainActivity.getExternalFilesDir(null)).getPath() +
                                "/" + fileName + ".xls");
                    } else {
                        showMessage("导出失败！");
                    }

                    showLoading(loadingProgressBarUnnormal, false);
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                    showFailed(R.string.download_error);
                }
            }
        }.start();
    }
}