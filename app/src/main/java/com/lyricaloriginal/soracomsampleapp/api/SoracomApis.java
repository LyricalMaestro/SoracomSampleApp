package com.lyricaloriginal.soracomsampleapp.api;

import android.text.TextUtils;

import net.arnx.jsonic.JSON;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Soracom API を簡単に呼び出せるメソッドをまとめたクラスです。
 * <p/>
 * Created by LyricalMaestro on 15/10/17.
 */
public final class SoracomApis {

    private static String SORACOM_API_URL = "https://api.soracom.io/v1";
    private static int READ_TIMEOUT = 5000;

    private SoracomApis() {
    }

    /**
     * Operatorの認証を行います。
     *
     * @param email    メールアドレス
     * @param password パスワード
     * @return 認証成功：APIキー、OperatorId、tokenの情報, 認証失敗ならNULL
     * @throws IOException
     */
    public static AuthInfo auth(String email, String password) throws IOException {
        HttpsURLConnection conn = null;
        AuthInfo authInfo = null;
        try {
            URL url = makeUrl("/auth");
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(READ_TIMEOUT);

            conn.connect();

            String param = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
            conn.getOutputStream().write(param.getBytes("UTF-8"));
            conn.getOutputStream().flush();

            if (conn.getResponseCode() == 200) {
                authInfo = JSON.decode(conn.getInputStream(), AuthInfo.class);
            }
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return authInfo;
    }

    /**
     * 登録しているSubscriberのリストを取得します。
     *
     * @param authInfo 　APIキー、OperatorId、tokenの情報
     * @return 登録しているSubscriberのリスト
     * @throws IOException
     */
    public static SubScriber[] subscribers(AuthInfo authInfo) throws IOException {
        HttpsURLConnection conn = null;
        SubScriber[] subscribers = null;
        try {
            URL url = makeUrl("/subscribers?tag_value_match_mode=exact");
            conn = (HttpsURLConnection) url.openConnection();
            if (authInfo != null) {
                conn.setRequestProperty("X-Soracom-API-Key", authInfo.apiKey);
                conn.setRequestProperty("X-Soracom-Token", authInfo.token);
            }
            conn.setDoInput(true);
            conn.setConnectTimeout(5000);

            conn.connect();

            int code = conn.getResponseCode();
            if (code == 200) {
                subscribers = JSON.decode(conn.getInputStream(), SubScriber[].class);
            }
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return subscribers;
    }

    /**
     * 指定したIMSIに対するSubscriberを取得します。
     *
     * @param authInfo 　APIキー、OperatorId、tokenの情報
     * @param imsi     IMSI
     * @return 該当するSubscriber
     * @throws IOException
     */
    public static SubScriber subscriber(AuthInfo authInfo, String imsi) throws IOException {
        if (TextUtils.isEmpty(imsi)) {
            return null;
        }

        HttpsURLConnection conn = null;
        SubScriber subscriber = null;
        try {
            URL url = makeUrl("/subscribers/" + imsi);
            conn = (HttpsURLConnection) url.openConnection();
            if (authInfo != null) {
                conn.setRequestProperty("X-Soracom-API-Key", authInfo.apiKey);
                conn.setRequestProperty("X-Soracom-Token", authInfo.token);
            }
            conn.setDoInput(true);
            conn.setConnectTimeout(5000);

            conn.connect();

            int code = conn.getResponseCode();
            if (code == 200) {
                subscriber = JSON.decode(conn.getInputStream(), SubScriber.class);
            }
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return subscriber;
    }

    private static URL makeUrl(String apiPath) throws MalformedURLException {
        return new URL(SORACOM_API_URL + apiPath);
    }
}
