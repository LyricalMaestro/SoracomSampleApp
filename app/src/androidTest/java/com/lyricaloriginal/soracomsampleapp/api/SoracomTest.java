package com.lyricaloriginal.soracomsampleapp.api;

/**
 * Created by shinichitanimoto on 2015/12/17.
 */

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.lyricaloriginal.soracomapiandroid.AuthInfo;
import com.lyricaloriginal.soracomapiandroid.AuthRequest;
import com.lyricaloriginal.soracomapiandroid.Soracom;
import com.lyricaloriginal.soracomapiandroid.SubScriber;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

/**
 * Created by LyricalMaestro on 2015/12/17.
 */
@RunWith(Enclosed.class)
public class SoracomTest {

    public static class Auth {

        private String mEmail;
        private String mPassword;

        @Before
        public void setup() throws IOException {
            Context context = InstrumentationRegistry.getContext();
            List<String> lines = AssetsUtils.readAssetsText(context, "Account.txt");
            mEmail = lines.get(0).split("=")[1];
            mPassword = lines.get(1).split("=")[1];
        }

        @Test
        public void auth1() throws IOException {
            Call<AuthInfo> call = Soracom.API.auth(new AuthRequest(mEmail, mPassword));
            Response<AuthInfo> response = call.execute();

            if (!response.isSuccess()) {
                Log.d("Retrofit", response.code() + " ddd");
                Assert.fail(response.code() + " ddd");
                return;
            }

            AuthInfo authInfo = response.body();
            Log.d("Retrofit", authInfo.apiKey == null ? "NULL" : authInfo.apiKey);
            Log.d("Retrofit", authInfo.operatorId == null ? "NULL" : authInfo.operatorId);
            Log.d("Retrofit", authInfo.token == null ? "NULL" : authInfo.token);
        }
    }

    public static class Logined {

        private String mApiKey;
        private String mToken;

        @Before
        public void setup() throws IOException {
            Context context = InstrumentationRegistry.getContext();
            List<String> lines = AssetsUtils.readAssetsText(context, "Account.txt");
            String email = lines.get(0).split("=")[1];
            String pass = lines.get(1).split("=")[1];

            Call<AuthInfo> call = Soracom.API.auth(new AuthRequest(email, pass));
            Response<AuthInfo> response = call.execute();
            mApiKey = response.body().apiKey;
            mToken = response.body().token;
        }

        @Test
        public void subscribe1() throws IOException {
            Call<List<SubScriber>> call = Soracom.API.subscribers(mApiKey, mToken);
            Response<List<SubScriber>> response = call.execute();
            if (!response.isSuccess()) {
                Log.d("Retrofit", "errorCode = " + response.code());
                return;
            }
            List<SubScriber> list = response.body();

            for (SubScriber subScriber : list) {
                Log.d("Retrofit", "---------------------------------");
                Log.d("Retrofit", subScriber.imsi);
                Log.d("Retrofit", subScriber.ipAddress);
                Log.d("Retrofit", subScriber.msisdn);
                Log.d("Retrofit", subScriber.speedClass);
                Log.d("Retrofit", subScriber.status);
                Log.d("Retrofit", subScriber.tags.get("name"));
            }
        }
    }
}