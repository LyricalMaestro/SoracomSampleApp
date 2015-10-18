package com.lyricaloriginal.soracomsampleapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lyricaloriginal.soracomsampleapp.api.AuthInfo;
import com.lyricaloriginal.soracomsampleapp.api.SubScriber;

import java.io.EOFException;
import java.net.SocketTimeoutException;

/**
 * Subscriberの一覧を表示するためのActivityです。
 */
public class SubscriberListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Response<SubScriber[]>> {

    private AuthInfo _authInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _authInfo = (AuthInfo) getIntent().getParcelableExtra("AUTH_INFO");

        Bundle bundle = new Bundle();
        bundle.putParcelable("AUTH_INFO", _authInfo);
        getLoaderManager().initLoader(0, bundle, this);
    }

    @Override
    public Loader<Response<SubScriber[]>> onCreateLoader(int id, Bundle args) {
        AuthInfo authInfo = (AuthInfo) args.getParcelable("AUTH_INFO");
        return new SubscribersLoader(this, authInfo);
    }

    @Override
    public void onLoadFinished(Loader<Response<SubScriber[]>> loader, Response<SubScriber[]> data) {
        if (data.getResult()) {
            SubScriber[] subScribers = data.getData();
            updateUi(subScribers);
        } else {
            Log.e(getClass().getName(), "失敗", data.getException());
            showToast(data.getException());
        }
    }

    private void updateUi(final SubScriber[] subScribers) {
        SubScriberListAdapter adapter = new SubScriberListAdapter(this, subScribers);
        ListView listView = (ListView) findViewById(R.id.subscriber_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imsi = subScribers[position].imsi;
                Intent intent = new Intent(SubscriberListActivity.this, SubscriberDetailActivity.class);
                intent.putExtra("AUTH_INFO", _authInfo);
                intent.putExtra("IMSI", imsi);
                startActivity(intent);
            }
        });
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
                Toast.makeText(SubscriberListActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Response<SubScriber[]>> loader) {

    }
}
