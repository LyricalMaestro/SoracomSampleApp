package com.lyricaloriginal.soracomsampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lyricaloriginal.soracomapiandroid.SubScriber;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit.Response;

/**
 * Subscriberの一覧を表示するためのActivityです。
 */
public class SubscriberListActivity extends AppCompatActivity
        implements SubscriberListFragment.Listener {

    private Auth mAuthInfo;
    private SubscriberListFragment mSubsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuthInfo = (Auth) getIntent().getParcelableExtra("AUTH_INFO");

        if (savedInstanceState == null) {
            mSubsFragment = new SubscriberListFragment();
            getFragmentManager().beginTransaction()
                    .add(mSubsFragment, SubscriberListFragment.class.getName())
                    .commit();
        } else {
            mSubsFragment = (SubscriberListFragment) getFragmentManager()
                    .findFragmentByTag(SubscriberListFragment.class.getName());
        }
        mSubsFragment.subScribers(mAuthInfo);
    }

    @Override
    public void onResponse(Response<List<SubScriber>> response) {
        if (response.isSuccess()) {
            List<SubScriber> subScribers = response.body();
            updateUi(subScribers.toArray(new SubScriber[0]));
        }
    }

    @Override
    public void onFailure(Throwable t) {
        Log.e(getClass().getName(), "失敗", t);
        if (t instanceof Exception) {
            showToast((Exception) t);
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
                intent.putExtra("AUTH_INFO", mAuthInfo);
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
                }
                Toast.makeText(SubscriberListActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
