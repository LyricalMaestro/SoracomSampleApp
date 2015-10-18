package com.lyricaloriginal.soracomsampleapp;

import android.content.Context;

import com.lyricaloriginal.soracomsampleapp.api.AuthInfo;
import com.lyricaloriginal.soracomsampleapp.api.SoracomApis;

/**
 * Created by LyricalMaestro on 15/10/18.
 */
public class AuthLoader extends BaseLoader<AuthInfo> {

    private final String _email;
    private final String _password;

    public AuthLoader(Context context, String email, String password) {
        super(context);
        _email = email;
        _password = password;
    }

    @Override
    protected Response<AuthInfo> load() throws Exception {
        AuthInfo authInfo = SoracomApis.auth(_email, _password);
        if (authInfo != null) {
            return new Response<AuthInfo>(authInfo);
        } else {
            //  エラーコード４０１に対応するレスポンス
            return new Response<AuthInfo>(new IllegalAccessException("認証エラー"));
        }
    }
}
