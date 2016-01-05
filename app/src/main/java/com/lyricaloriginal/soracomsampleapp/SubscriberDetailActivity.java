package com.lyricaloriginal.soracomsampleapp;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lyricaloriginal.soracomapiandroid.Soracom;
import com.lyricaloriginal.soracomapiandroid.SubScriber;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * 指定したSubScriberの詳細を示すActivityです。
 */
public class SubscriberDetailActivity extends AppCompatActivity
        implements SingleChoiceDialogFragment.Listener {

    private SubScriber _subScriber;
    private Auth _authInfo;
    private String _imsi;

    private Call<SubScriber> _call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_detail);

        _authInfo = (Auth) getIntent().getParcelableExtra("AUTH_INFO");
        _imsi = getIntent().getStringExtra("IMSI");

        if (savedInstanceState == null) {
            _call = Soracom.API.subscriber(
                    _authInfo.apiKey, _authInfo.token, _imsi);
            _call.enqueue(getSubscriberCallback());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_call != null && isFinishing()) {
            _call.cancel();
            ;
            _call = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subscriber_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_change_activation).
                setTitle(beActivate() ? "休止" : "使用開始");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_air_stats:
                Intent intent = new Intent(this, AirStatsActivity.class);
                intent.putExtra("AUTH_INFO", _authInfo);
                intent.putExtra("IMSI", _imsi);
                startActivity(intent);
                break;
            case R.id.menu_change_speed_class:
                showSelectSpeedClassDialog();
                break;
            case R.id.menu_change_activation:
                if (beActivate()) {
                    _call = Soracom.API.changeStatusDeactivate(
                            _authInfo.apiKey, _authInfo.token, _imsi
                    );
                } else {
                    _call = Soracom.API.changeStatusActivate(
                            _authInfo.apiKey, _authInfo.token, _imsi
                    );
                }
                _call.enqueue(getChangeActivateCallback(beActivate()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(final Exception ex) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String msg = null;
                if (ex instanceof SocketTimeoutException) {
                    msg = "接続がタイムアウトしました。";
                } else if (ex instanceof EOFException) {
                    msg = "通信中にエラーが発生しました。";
                } else if (ex instanceof IllegalAccessException) {
                    msg = "メールアドレスもしくはパスワードが間違っています。";
                }
                Toast.makeText(SubscriberDetailActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showNotExistToast() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String msg = "指定したIMSIは存在しません。";
                Toast.makeText(SubscriberDetailActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSelectSpeedClassDialog() {
        String[] values = SpeedClass.getValues();
        int sel = -1;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(_subScriber.speedClass)) {
                sel = i;
            }
        }
        DialogFragment dialog = SingleChoiceDialogFragment.
                newInstance("速度クラスの設定", values, sel);
        dialog.show(getFragmentManager(), "SpeedClass");
    }

    private void updateUi(SubScriber subScribers) {
        List<String> details = new ArrayList<String>();
        details.add("IMSI:\r\n" + subScribers.imsi);
        details.add("MSISDN:\r\n" + subScribers.msisdn);
        details.add("NAME:\r\n" + subScribers.tags.get("name"));
        details.add("IP Address:\r\n" + subScribers.ipAddress);
        details.add("SpeedClass:\r\n" + subScribers.speedClass);
        details.add("STATUS:\r\n" + subScribers.status);
        details.add("APN:\r\n" + subScribers.apn);
        details.add("groupId:\r\n" + subScribers.groupId);
        details.add("moduleType:\r\n" + subScribers.moduleType);
        details.add("createAt:\r\n" + subScribers.createdAt);
        details.add("lastModifiedAt:\r\n" + subScribers.lastModifiedAt);
        details.add("expiredTime:\r\n" + subScribers.expiryTime);
        details.add("terminationEnabled:\r\n" + subScribers.terminationEnabled);
        if (subScribers.sessionStatus != null) {
            details.add("sessionStatus・lastUpdateAt:\r\n" + subScribers.sessionStatus.lastUpdatedAt);
            details.add("sessionStatus・IMEI:\r\n" + subScribers.sessionStatus.imei);
            details.add("sessionStatus・ueIpAddress:\r\n" + subScribers.sessionStatus.ueIpAddress);
            details.add("sessionStatus・online:\r\n" + subScribers.sessionStatus.online);
        } else {
            details.add("sessionStatus:\r\nNULL");
        }

        ListView listView = (ListView) findViewById(R.id.detail_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                details.toArray(new String[0]));
        listView.setAdapter(adapter);
    }

    @Override
    public void onSelectItemListener(String tag, final String selectedValue) {
        Handler handler = new Handler();
        if (tag.equals("SpeedClass")) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    _call = Soracom.API.updateSpeedClass(
                            _authInfo.apiKey, _authInfo.token, _imsi,
                            SpeedClass.toRequest(selectedValue));
                    _call.enqueue(getUpdateSpeedClass());
                }
            });
        }
    }


    private Callback<SubScriber> getSubscriberCallback() {
        return new Callback<SubScriber>() {
            @Override
            public void onResponse(Response<SubScriber> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    SubScriber subScriber = response.body();
                    if (subScriber != null) {
                        _subScriber = subScriber;
                        updateUi(subScriber);
                    } else {
                        showNotExistToast();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        };
    }

    private Callback<SubScriber> getUpdateSpeedClass() {
        return new Callback<SubScriber>() {
            @Override
            public void onResponse(Response<SubScriber> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    SubScriber subScriber = response.body();
                    if (subScriber != null) {
                        Toast.makeText(SubscriberDetailActivity.this,
                                "速度クラスを更新しました。", Toast.LENGTH_SHORT).show();
                        _subScriber = subScriber;
                        updateUi(subScriber);
                    } else {
                        showNotExistToast();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        };
    }

    private Callback<SubScriber> getChangeActivateCallback(
            boolean activateBefore) {
        return new Callback<SubScriber>() {
            @Override
            public void onResponse(Response<SubScriber> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    SubScriber subScriber = response.body();
                    if (subScriber != null) {
                        Toast.makeText(getApplicationContext(),
                                "Statusを変更しました。", Toast.LENGTH_SHORT).show();
                        _subScriber = subScriber;
                        updateUi(subScriber);
                    } else {
                        showNotExistToast();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        };
    }

    private boolean beActivate() {
        if (_subScriber == null) {
            return false;
        }

        return TextUtils.equals(_subScriber.status, "active");
    }
}
