package com.lyricaloriginal.soracomsampleapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.lyricaloriginal.soracomapiandroid.AirStats;
import com.lyricaloriginal.soracomapiandroid.Soracom;
import com.lyricaloriginal.soracomapiandroid.TrafficStats;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * 通信量履歴をグラフ表示するためのActivityです。
 */
public class AirStatsActivity extends AppCompatActivity implements Callback<List<AirStats>> {

    protected BarChart _chart;
    private Auth _authInfo;
    private String _imsi;
    private Call<List<AirStats>> _call = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_stats);
        _chart = (BarChart) findViewById(R.id.chart1);
        initChart();

        _authInfo = (Auth) getIntent().getParcelableExtra("AUTH_INFO");
        _imsi = getIntent().getStringExtra("IMSI");
        if (savedInstanceState == null) {
            int current = (int) (System.currentTimeMillis() / 1000L);
            int before = current - 60 * 60 * 24 * 365;
            _call = Soracom.API.airSubscribers(
                    _authInfo.apiKey,
                    _authInfo.token,
                    _imsi,
                    before,
                    current,
                    "month"
            );
            onStartExecuting();
            _call.enqueue(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_call != null && isFinishing()) {
            _call.cancel();
            _call = null;
        }
    }

    @Override
    public void onResponse(Response<List<AirStats>> response, Retrofit retrofit) {
        _call = null;
        if (response.isSuccess()) {
            onSuccessExecuting(response.body());
        } else {
            onFailureExecuting();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        _call = null;
        onFailureExecuting();
    }

    private void onStartExecuting() {
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        _chart.setVisibility(View.GONE);
    }

    private void onSuccessExecuting(List<AirStats> airStatsList) {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        _chart.setVisibility(View.VISIBLE);

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        for (int i = 0; i < airStatsList.size(); i++) {
            AirStats airStats = airStatsList.get(i);
            long val1 = 0;
            long val2 = 0;
            for (TrafficStats traffics : airStats.dataTrafficStatsMap.values()) {
                val1 += traffics.uploadByteSizeTotal;
                val2 += traffics.downloadByteSizeTotal;
            }
            xVals.add(airStats.date);
            yVals1.add(new BarEntry(new float[]{val1, val2}, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "通信量(KB)");
        set1.setBarSpacePercent(35f);
        set1.setColors(new int[]{
                Color.BLUE, Color.YELLOW
        });
        set1.setStackLabels(new String[]{"Upload", "Download"});

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);

        _chart.setData(data);
    }

    private void onFailureExecuting() {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

    private void initChart() {
        _chart.setMaxVisibleValueCount(60);
        _chart.setPinchZoom(false);
        _chart.setDrawGridBackground(false);

        XAxis xAxis = _chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxis leftAxis = _chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);

        YAxis rightAxis = _chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);

        Legend l = _chart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }
}
