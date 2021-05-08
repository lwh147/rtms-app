package com.lwh147.rtms.util;

import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.List;

/**
 * @description: MPAndroid LineChar
 * @author: lwh
 * @create: 2021/5/8 14:42
 * @version: v1.0
 **/
public class LineChartUtil {
    private static final String TAG = "LineChartUtils";
    private final LineChart lineChart;
    private final Handler mHandler = new Handler();
    Runnable hideHighLight = new Runnable() {
        @Override
        public void run() {
            lineChart.highlightValue(null);
        }
    };

    public LineChartUtil(LineChart lineChart) {
        this.lineChart = lineChart;
        initSetting();
    }

    /**
     * 常用设置
     */
    private void initSetting() {
        lineChart.getDescription().setText("时间点");
        lineChart.getDescription().setTextColor(Color.RED);
        lineChart.getDescription().setTextSize(16);//设置描述的文字 ,颜色 大小
        lineChart.setNoDataText("没有数据"); //没数据的时候显示
        lineChart.setDrawBorders(false);//是否显示边框
        lineChart.setTouchEnabled(true); // 设置是否可以触摸
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(true);// 是否可以缩放 x和y轴, 默认是true
        lineChart.setScaleXEnabled(true); //是否可以缩放 仅x轴
        lineChart.setScaleYEnabled(true); //是否可以缩放 仅y轴
        lineChart.setPinchZoom(false);  //设置x轴和y轴能否同时缩放。默认是否
        lineChart.setDoubleTapToZoomEnabled(true);//设置是否可以通过双击屏幕放大图表。默认是true
        lineChart.setHighlightPerDragEnabled(true);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        lineChart.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        lineChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止

        // 通过选中监听,来实现不点击图表后3秒,定位线自动消失
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mHandler.removeCallbacks(hideHighLight);
                mHandler.postDelayed(hideHighLight, 3000);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // 设置X轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴位置
        // xAxis.setAxisMinimum(1);//设置x轴最小
        // xAxis.setAxisMaximum(8);//设置x轴最大
        xAxis.setGranularity(1f);
        xAxis.setTextSize(14);
        xAxis.setTextColor(Color.RED);
        xAxis.setEnabled(true);//是否显示x轴是否开启
        xAxis.setDrawLabels(true); //设置x轴标签 即x轴上显示的数值
        xAxis.setDrawGridLines(true);//是否设置x轴上每个点对应的线 即 竖向的网格线
        xAxis.enableGridDashedLine(2, 2, 2); //竖线 虚线样式  lineLength控制虚线段的长度 spaceLength控制线之间的空间
        xAxis.setLabelRotationAngle(90f);//设置x轴标签的旋转角度

        // 设置左右滑动
        Matrix matrix = new Matrix();
        // x轴缩放1.5倍
        matrix.postScale(1.5f, 1.0f);
        // 在图表动画显示之前进行缩放
        lineChart.getViewPortHandler().refresh(matrix, lineChart, false);
        // x轴执行动画
        lineChart.animateX(500);

        // 设置Y轴
        YAxis yAxisLef = lineChart.getAxisLeft();
        yAxisLef.setTextSize(14);
        yAxisLef.setAxisMinimum(0);

        YAxis yAxisRight = lineChart.getAxisRight();//获取右侧y轴
        yAxisRight.setEnabled(false);//设置是否禁止
    }

    public void setLineChartData(List<Entry> yValue, String label, final JSONArray array) {
        // 设置X轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelCount(array.size());// 设置标签数量，即x轴数据个数
        // 设置x轴文字样式
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                // 超出范围返回空
                if (value < 0 || value > array.size() - 1) {
                    return "";
                }
                JSONObject object = (JSONObject) array.get((int) value);
                return object.getString("duration");
            }
        });

        LineDataSet lineDataSet = new LineDataSet(yValue, label);
        lineDataSet.setHighLightColor(Color.RED); //设置高亮线的颜色
        lineDataSet.setColor(Color.BLACK);//设置折线颜色
        lineDataSet.setCircleColor(Color.BLUE);//设置交点的圆圈的颜色
        lineDataSet.setDrawCircles(false);//设置是否显示交点
        lineDataSet.setDrawValues(true); //设置是否显示交点处的数值
        lineDataSet.setValueTextColor(Color.RED); //设置交点上值的颜色
        lineDataSet.setValueTextSize(14);//设置交点上值的字体大小

        // 设置焦点的格式化方式
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        LineData lineData = new LineData(lineDataSet);

        lineChart.setData(lineData);
    }
}