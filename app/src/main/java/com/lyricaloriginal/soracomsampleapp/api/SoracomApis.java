package com.lyricaloriginal.soracomsampleapp.api;

import android.text.TextUtils;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import net.arnx.jsonic.JSON;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
        String param = String.format("{\"email\":\"%s\",\"password\":\"%s\"}",
                email, password);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), param.getBytes("UTF-8"));

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(makeUrl("/auth")).post(requestBody).build();

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        Response resp = client.newCall(request).execute();
        if (resp.code() == 200) {
            return JSON.decode(resp.body().string(), AuthInfo.class);
        }
        return null;
    }

    /**
     * 登録しているSubscriberのリストを取得します。
     *
     * @param authInfo 　APIキー、OperatorId、tokenの情報
     * @return 登録しているSubscriberのリスト
     * @throws IOException
     */
    public static SubScriber[] subscribers(AuthInfo authInfo) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.url(makeUrl("/subscribers?tag_value_match_mode=exact")).get();
        if (authInfo != null) {
            builder.addHeader("X-Soracom-API-Key", authInfo.apiKey)
                    .addHeader("X-Soracom-Token", authInfo.token);
        }
        Request request = builder.build();

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        Response resp = client.newCall(request).execute();
        if (resp.code() == 200) {
            return JSON.decode(resp.body().string(), SubScriber[].class);
        }
        return null;
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

        Request.Builder builder = new Request.Builder();
        builder.url(makeUrl("/subscribers/" + imsi)).get();
        if (authInfo != null) {
            builder.addHeader("X-Soracom-API-Key", authInfo.apiKey)
                    .addHeader("X-Soracom-Token", authInfo.token);
        }
        Request request = builder.build();

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        Response resp = client.newCall(request).execute();
        if (resp.code() == 200) {
            return JSON.decode(resp.body().string(), SubScriber.class);
        }
        return null;
    }

    private static String makeUrl(String apiPath) {
        return SORACOM_API_URL + apiPath;
    }

    /**
     * 指定されたSubscriberの速度クラスを変更します。
     *
     * @param authInfo   　APIキー、OperatorId、tokenの情報
     * @param imsi       IMSI
     * @param speedClass 変更したいSpeedClass
     * @throws IOException
     * @return　変更後のSubscriber。nullの場合は変更失敗。
     */
    public static SubScriber updateSpeedClass(AuthInfo authInfo, String imsi, String speedClass)
            throws IOException {
        if (TextUtils.isEmpty(imsi)) {
            return null;
        } else if (!SpeedClass.isValidValue(speedClass)) {
            return null;
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                SpeedClass.toJsonString(speedClass).getBytes("UTF-8"));

        Request.Builder builder = new Request.Builder();
        builder.url(makeUrl("/subscribers/" + imsi + "/update_speed_class"))
                .post(requestBody);
        if (authInfo != null) {
            builder.addHeader("X-Soracom-API-Key", authInfo.apiKey)
                    .addHeader("X-Soracom-Token", authInfo.token);
        }
        Request request = builder.build();

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        Response resp = client.newCall(request).execute();
        if (resp.code() == 200) {
            return JSON.decode(resp.body().string(), SubScriber.class);
        }
        return null;
    }
}
