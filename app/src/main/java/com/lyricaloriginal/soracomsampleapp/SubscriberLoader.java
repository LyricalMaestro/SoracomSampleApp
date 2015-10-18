package com.lyricaloriginal.soracomsampleapp;

import android.content.Context;

import com.lyricaloriginal.soracomsampleapp.api.AuthInfo;
import com.lyricaloriginal.soracomsampleapp.api.SoracomApis;
import com.lyricaloriginal.soracomsampleapp.api.SubScriber;

/**
 * Created by LyricalMaestro on 15/10/18.
 */
public class SubscriberLoader extends BaseLoader<SubScriber> {

    private final AuthInfo _authInfo;
    private final String _imsi;

    protected SubscriberLoader(Context context, AuthInfo authInfo, String imsi) {
        super(context);
        _authInfo = authInfo;
        _imsi = imsi;
    }

    @Override
    protected Response<SubScriber> load() throws Exception {
        SubScriber subScriber = SoracomApis.subscriber(_authInfo, _imsi);
        return new Response<SubScriber>(subScriber);
    }
}
