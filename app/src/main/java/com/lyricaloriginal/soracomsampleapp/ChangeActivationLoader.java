package com.lyricaloriginal.soracomsampleapp;

import android.content.Context;

import com.lyricaloriginal.soracomsampleapp.api.AuthInfo;
import com.lyricaloriginal.soracomsampleapp.api.SoracomApis;
import com.lyricaloriginal.soracomsampleapp.api.SubScriber;

/**
 * Created by LyricalMaestro on 2015/10/31.
 */
public class ChangeActivationLoader extends BaseLoader<SubScriber> {

    private final AuthInfo _authInfo;
    private final String _imsi;
    private final boolean _beActivate;

    protected ChangeActivationLoader(
            Context context, AuthInfo authInfo, String imsi, boolean beActivate) {
        super(context);
        _authInfo = authInfo;
        _imsi = imsi;
        _beActivate = beActivate;
    }

    @Override
    protected Response<SubScriber> load() throws Exception {
        SubScriber subScriber = SoracomApis.changeActivateState(_authInfo, _imsi, _beActivate);
        return new Response<SubScriber>(subScriber);
    }
}
