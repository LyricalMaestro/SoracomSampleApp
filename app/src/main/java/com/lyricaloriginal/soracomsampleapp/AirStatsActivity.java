package com.lyricaloriginal.soracomsampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.lyricaloriginal.soracomapiandroid.AirStats;

import java.text.DecimalFormat;
import java.util.List;

import retrofit.Response;

/**
 * 通信量履歴をグラフ表示するためのActivityです。
 */
public class AirStatsActivity extends AppCompatActivity
        implements AirStatsFragment.Listener {

    protected BarChart mChart;
    private Auth mAuthInfo;
    private String mImsi;
    private AirStatsFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_stats);
        mChart = (BarChart) findViewById(R.id.chart1);
        initChart();

        mAuthInfo = (Auth) getIntent().getParcelableExtra("AUTH_INFO");
        mImsi = getIntent().getStringExtra("IMSI");
        setTitle("IMSI : " + mImsi);
        if (savedInstanceState == null) {
            mFragment = new AirStatsFragment();
            getFragmentManager().beginTransaction()
                    .add(mFragment, AirStatsFragment.class.getName())
                    .commit();
        }else{
            mFragment = (AirStatsFragment)getFragmentManager()
                    .findFragmentByTag(AirStatsFragment.class.getName());
        }
        onStartExecuting();
        mFragment.airSubscribers(mAuthInfo, mImsi);
    }

    @Override
    public void onResponse(Response<List<AirStats>> response, int current, int before) {
        if (response.isSuccess()) {
            onSuccessExecuting(response.body(), current, before);
        } else {
            onFailureExecuting();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        onFailureExecuting();
    }

    private void onStartExecuting() {
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        mChart.setVisibility(View.GONE);
    }

    private void onSuccessExecuting(List<AirStats> airStatsList, int current, int before) {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        mChart.setVisibility(View.VISIBLE);

        BarData data = AirStatsBarDataSetMaker.make(airStatsList,
                (long)before * 1000L, (long)current * 1000L);
        mChart.setData(data);
    }

    private void onFailureExecuting() {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

    private void initChart() {
        mChart.setDescription("");
        mChart.setMaxVisibleValueCount(60);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelsToSkip(3);
        xAxis.setLabelRotationAngle(30);
        xAxis.setXOffset(50);
        xAxis.setValueFormatter(new MyXAxisValueFormatter());

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setValueFormatter(new MyYAxisValueFormatter());

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setValueFormatter(new MyYAxisValueFormatter());

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    private class MyXAxisValueFormatter implements XAxisValueFormatter {
        @Override
        public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
            return String.format("  %s/%s", original.substring(0, 4), original.substring(4));
        }
    }

    private class MyYAxisValueFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        MyYAxisValueFormatter(){
            mFormat = new DecimalFormat("##,###,###");
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return mFormat.format(value / 1024) + "KB";
        }
    }

}
