package com.lyricaloriginal.soracomsampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

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

    private Auth _authInfo;
    private String _imsi;
    private AirStats _airStats;

    private Call<List<AirStats>> _call = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_stats);

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
        findViewById(R.id.air_stats_chart).setVisibility(View.GONE);
    }

    private void onSuccessExecuting(List<AirStats> airStatsList) {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        findViewById(R.id.air_stats_chart).setVisibility(View.VISIBLE);

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------------------\n");
        for(AirStats airStats : airStatsList){
            sb.append(airStats.date + "\n");
            sb.append(airStats.unixtime + "\n");
            sb.append("-------------------------------\n");
        }
        TextView textView = (TextView) findViewById(R.id.air_stats_chart);
        textView.setVisibility(View.VISIBLE);
        textView.setText(sb.toString());
    }

    private void onFailureExecuting() {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        TextView textView = (TextView) findViewById(R.id.air_stats_chart);
        textView.setVisibility(View.VISIBLE);
        textView.setText("サーバからの通信量履歴取得失敗!");
    }
}
