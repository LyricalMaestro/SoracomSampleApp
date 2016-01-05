package com.lyricaloriginal.soracomsampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.lyricaloriginal.soracomapiandroid.AirStats;
import com.lyricaloriginal.soracomapiandroid.Soracom;

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
            int before = current - 60 * 60 * 24 * 365;  //  １年分
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

        long current = System.currentTimeMillis();
        long before = current - 60L * 60L * 24L * 365L * 1000L;  //  １年分
        BarData data = AirStatsBarDataSetMaker.make(airStatsList, before, current);
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
