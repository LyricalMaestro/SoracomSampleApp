package com.lyricaloriginal.soracomsampleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lyricaloriginal.soracomapiandroid.AuthInfo;

import net.arnx.jsonic.util.Base64;

import java.io.EOFException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

import retrofit.Response;

/**
 * ログイン画面のActivityです。
 */
public class LoginActivity extends AppCompatActivity
        implements LoginFragment.Listener {

    private LoginFragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initEditText();
        initLoginFragment(savedInstanceState);

        if (savedInstanceState == null) {
            String email = loadEmailAddress();
            if (!TextUtils.isEmpty(email)) {
                EditText emailEditText = (EditText) findViewById(R.id.email_edit_text);
                emailEditText.setText(email);
            }
        }

        //  認証ボタン
        final Button authBtn = (Button) findViewById(R.id.auth_button);
        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUiComponentEnable(false);
                EditText emailEditText = (EditText) findViewById(R.id.email_edit_text);
                EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);
                final String email = emailEditText.getEditableText().toString();
                final String password = passwordEditText.getEditableText().toString();
                mLoginFragment.login(email, password);
            }
        });
        setUiComponentEnable(!mLoginFragment.isConnecting());
    }

    @Override
    public void onResponse(Response<AuthInfo> response) {
        setUiComponentEnable(true);
        if (response.isSuccess()) {
            saveEmailAddress();
            AuthInfo authInfo = response.body();
            Intent intent = new Intent(this, SubscriberListActivity.class);
            intent.putExtra("AUTH_INFO", new Auth(authInfo));
            startActivity(intent);
        } else if (response.code() / 100 == 4) {
            showErrorDialog("メールアドレスもしくはパスワードが間違っています。");
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if (t instanceof Exception) {
            showErrorDialog((Exception) t);
        }
    }

    private void showErrorDialog(final Exception ex) {
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
                AlertDialogFragment.newInstance(msg).show(getFragmentManager(),
                        AlertDialogFragment.class.getName());
            }
        });
    }

    private void showErrorDialog(final String msg) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialogFragment.newInstance(msg).show(getFragmentManager(),
                        AlertDialogFragment.class.getName());
            }
        });
    }

    private void initEditText() {
        //  この辺りちょっと汚い。。。
        EditText emailEditText = (EditText) findViewById(R.id.email_edit_text);
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (source.toString().matches("^[0-9a-zA-Z@¥.¥_¥¥-]+$")) {
                    return source;
                } else {
                    return "";
                }
            }
        };
        InputFilter[] filters = new InputFilter[]{inputFilter};
        emailEditText.setFilters(filters);

        EditText PassEditText = (EditText) findViewById(R.id.password_edit_text);
        InputFilter inputFilter2 = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (source.toString().matches("^[0-9a-zA-Z]+$")) {
                    return source;
                } else {
                    return "";
                }
            }
        };
        InputFilter[] filters2 = new InputFilter[]{inputFilter2};
        PassEditText.setFilters(filters2);
    }

    private void initLoginFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mLoginFragment = new LoginFragment();
            getFragmentManager().beginTransaction()
                    .add(mLoginFragment, LoginFragment.class.getName())
                    .commit();
        } else {
            mLoginFragment = (LoginFragment) getFragmentManager()
                    .findFragmentByTag(LoginFragment.class.getName());
        }
    }

    private void saveEmailAddress() {
        EditText emailEditText = (EditText) findViewById(R.id.email_edit_text);
        String email = emailEditText.getEditableText().toString();
        try {
            String acount = Base64.encode(email.getBytes("UTF-8"));
            SharedPreferences pref = getSharedPreferences("acount", MODE_PRIVATE);
            pref.edit().putString("ACOUNT", acount).commit();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String loadEmailAddress() {
        SharedPreferences pref = getSharedPreferences("acount", MODE_PRIVATE);
        String acount = pref.getString("ACOUNT", "");
        if (!TextUtils.isEmpty(acount)) {
            try {
                return new String(Base64.decode(acount), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void setUiComponentEnable(boolean enable) {
        findViewById(R.id.auth_button).setEnabled(enable);
        findViewById(R.id.email_edit_text).setEnabled(enable);
        findViewById(R.id.password_edit_text).setEnabled(enable);

    }
}
