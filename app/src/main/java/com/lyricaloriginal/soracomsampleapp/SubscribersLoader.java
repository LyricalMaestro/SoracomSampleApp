package com.lyricaloriginal.soracomsampleapp;

import android.content.Context;

import com.lyricaloriginal.soracomsampleapp.api.AuthInfo;
import com.lyricaloriginal.soracomsampleapp.api.SoracomApis;
import com.lyricaloriginal.soracomsampleapp.api.SubScriber;

/**
 * Created by LyricalMaestro on 15/10/18.
 */
public class SubscribersLoader extends BaseLoader<SubScriber[]> {

    private final AuthInfo _authInfo;

    protected SubscribersLoader(Context context, AuthInfo authInfo) {
        super(context);
        _authInfo = authInfo;
    }

    @Override
    protected Response<SubScriber[]> load() throws Exception {
        SubScriber[] subScribers = SoracomApis.subscribers(_authInfo);
        if (subScribers != null) {
            return new Response<SubScriber[]>(subScribers);
        } else {
            //  エラーコード４０１に対応するレスポンス
            return new Response<SubScriber[]>(new IllegalAccessException("認証エラー"));
        }
    }
}
