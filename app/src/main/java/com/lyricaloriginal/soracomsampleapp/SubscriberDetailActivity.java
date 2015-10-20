package com.lyricaloriginal.soracomsampleapp;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lyricaloriginal.soracomsampleapp.api.AuthInfo;
import com.lyricaloriginal.soracomsampleapp.api.SpeedClass;
import com.lyricaloriginal.soracomsampleapp.api.SubScriber;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * 指定したSubScriberの詳細を示すActivityです。
 */
public class SubscriberDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Response<SubScriber>>,
        SingleChoiceDialogFragment.Listener {

    private SubScriber _subScriber;
    private AuthInfo _authInfo;
    private String _imsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_detail);

        _authInfo = (AuthInfo) getIntent().getParcelableExtra("AUTH_INFO");
        _imsi = getIntent().getStringExtra("IMSI");

        Bundle bundle = new Bundle();
        bundle.putParcelable("AUTH_INFO", _authInfo);
        bundle.putString("IMSI", _imsi);
        getLoaderManager().initLoader(0, bundle, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, 0, 0, "速度クラス変更");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSelectSpeedClassDialog();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public Loader<Response<SubScriber>> onCreateLoader(int id, Bundle args) {
        AuthInfo authInfo = (AuthInfo) args.getParcelable("AUTH_INFO");
        String imsi = args.getString("IMSI");
        if (id == 0) {
            return new SubscriberLoader(this, authInfo, imsi);
        } else {
            String speedClass = args.getString("SPEED_CLASS");
            return new UpdateSpeedClassLoader(this, authInfo, imsi, speedClass);
        }
    }

    @Override
    public void onLoadFinished(Loader<Response<SubScriber>> loader, Response<SubScriber> data) {
        if (data.getResult()) {
            if (loader instanceof UpdateSpeedClassLoader) {
                Toast.makeText(this, "速度クラスを更新しました。", Toast.LENGTH_SHORT).show();
            }

            SubScriber subScriber = data.getData();
            if (subScriber != null) {
                _subScriber = subScriber;
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
        if (subScribers.sessionStatus != null) {
            details.add("sessionStatus・lastUpdateAt:\r\n" + subScribers.sessionStatus.lastUpdateAt);
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
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("AUTH_INFO", _authInfo);
                    bundle.putString("IMSI", _imsi);
                    bundle.putString("SPEED_CLASS", selectedValue);
                    getLoaderManager().restartLoader(1, bundle, SubscriberDetailActivity.this);
                }
            });
        }
    }
}
