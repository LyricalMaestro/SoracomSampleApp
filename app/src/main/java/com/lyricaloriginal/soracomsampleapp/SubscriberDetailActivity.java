package com.lyricaloriginal.soracomsampleapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lyricaloriginal.soracomsampleapp.api.AuthInfo;
import com.lyricaloriginal.soracomsampleapp.api.SubScriber;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * 指定したSubScriberの詳細を示すActivityです。
 */
public class SubscriberDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Response<SubScriber>> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_detail);

        AuthInfo authInfo = (AuthInfo) getIntent().getParcelableExtra("AUTH_INFO");
        String imsi = getIntent().getStringExtra("IMSI");

        Bundle bundle = new Bundle();
        bundle.putParcelable("AUTH_INFO", authInfo);
        bundle.putString("IMSI", imsi);
        getLoaderManager().initLoader(0, bundle, this);
    }

    @Override
    public Loader<Response<SubScriber>> onCreateLoader(int id, Bundle args) {
        AuthInfo authInfo = (AuthInfo) args.getParcelable("AUTH_INFO");
        String imsi = args.getString("IMSI");
        return new SubscriberLoader(this, authInfo, imsi);
    }

    @Override
    public void onLoadFinished(Loader<Response<SubScriber>> loader, Response<SubScriber> data) {
        if (data.getResult()) {
            SubScriber subScriber = data.getData();
            if (subScriber != null) {
                updateUi(subScriber);
            } else {
                showNotExistToast();
            }
        } else {
            Log.e(getClass().getName(), "失敗", data.getException());
            showToast(data.getException());
        }
    }

    @Override
    public void onLoaderReset(Loader<Response<SubScriber>> loader) {

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

    private void updateUi(SubScriber subScribers) {
        List<String> details = new ArrayList<String>();
        details.add("IMSI:\r\n" + subScribers.imsi);
        details.add("MSISDN:\r\n" + subScribers.msisdn);
        details.add("NAME:\r\n" + subScribers.tags.name);
        details.add("IP Address:\r\n" + subScribers.ipAddress);
        details.add("SpeedClass:\r\n" + subScribers.speedClass);
        details.add("STATUS:\r\n" + subScribers.status);
        details.add("APN:\r\n" + subScribers.apn);
        details.add("groupId:\r\n" + subScribers.groupId);
        details.add("moduleType:\r\n" + subScribers.moduleType);
        details.add("createAt:\r\n" + subScribers.createdAt);
        details.add("lastModifiedAt:\r\n" + subScribers.lastModifiedAt);
        details.add("expiredTime:\r\n" + subScribers.expirtyTime);
        details.add("terminationEnabled:\r\n" + subScribers.terminationEnabled);
        if(subScribers.sessionStatus != null){
            details.add("sessionStatus・lastUpdateAt:\r\n" + subScribers.sessionStatus.lastUpdateAt);
            details.add("sessionStatus・IMEI:\r\n" + subScribers.sessionStatus.imei);
            details.add("sessionStatus・ueIpAddress:\r\n" + subScribers.sessionStatus.ueIpAddress);
            details.add("sessionStatus・online:\r\n" + subScribers.sessionStatus.online);
        }else{
            details.add("sessionStatus:\r\nNULL");
        }

        ListView listView = (ListView) findViewById(R.id.detail_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                details.toArray(new String[0]));
        listView.setAdapter(adapter);
    }
}
