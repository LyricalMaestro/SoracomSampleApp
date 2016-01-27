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

import com.lyricaloriginal.soracomapiandroid.SubScriber;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Response;

/**
 * 指定したSubScriberの詳細を示すActivityです。
 */
public class SubscriberDetailActivity extends AppCompatActivity
        implements SingleChoiceDialogFragment.Listener, SubscriberFragment.Listener {

    private SubScriber mSubScriber;
    private Auth mAuthInfo;
    private String mImsi;
    private SubscriberFragment mSubsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_detail);

        mAuthInfo = (Auth) getIntent().getParcelableExtra("AUTH_INFO");
        mImsi = getIntent().getStringExtra("IMSI");

        if (savedInstanceState == null) {
            mSubsFragment = new SubscriberFragment();
            getFragmentManager().beginTransaction().
                    add(mSubsFragment, SubscriberFragment.class.getName()).
                    commit();
        } else {
            mSubsFragment = (SubscriberFragment) getFragmentManager().
                    findFragmentByTag(SubscriberFragment.class.getName());
        }
        mSubsFragment.subScriber(mAuthInfo, mImsi);
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
                intent.putExtra("AUTH_INFO", mAuthInfo);
                intent.putExtra("IMSI", mImsi);
                startActivity(intent);
                break;
            case R.id.menu_change_speed_class:
                showSelectSpeedClassDialog();
                break;
            case R.id.menu_change_activation:
                if (beActivate()) {
                    mSubsFragment.changeStatusDeactivate(mAuthInfo, mImsi);
                } else {
                    mSubsFragment.changeStatusActivate(mAuthInfo, mImsi);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(Response<SubScriber> response, String tag) {

        if (response.isSuccess()) {
            SubScriber subScriber = response.body();
            if (subScriber != null) {
                if(TextUtils.equals("updateSpeedClass", tag)){
                    Toast.makeText(SubscriberDetailActivity.this,
                            "速度クラスを更新しました。", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.equals("changeStatusDeactivate", tag)){
                    Toast.makeText(getApplicationContext(),
                            "Statusを変更しました。", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.equals("changeStatusActivate", tag)){
                    Toast.makeText(getApplicationContext(),
                            "Statusを変更しました。", Toast.LENGTH_SHORT).show();
                }

                mSubScriber = subScriber;
                updateUi(subScriber);
            } else {
                showNotExistToast();
            }
        }
    }

    @Override
    public void onFailure(Throwable t, String tag) {
        if(t instanceof Exception){
            showToast((Exception)t);
        }
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
            if (values[i].equals(mSubScriber.speedClass)) {
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
                    mSubsFragment.updateSpeedClass(mAuthInfo, mImsi, selectedValue);
                }
            });
        }
    }

    private boolean beActivate() {
        if (mSubScriber == null) {
            return false;
        }

        return TextUtils.equals(mSubScriber.status, "active");
    }
}
