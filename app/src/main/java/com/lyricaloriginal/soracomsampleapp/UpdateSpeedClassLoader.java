package com.lyricaloriginal.soracomsampleapp;

import android.content.Context;

import com.lyricaloriginal.soracomsampleapp.api.AuthInfo;
import com.lyricaloriginal.soracomsampleapp.api.SoracomApis;
import com.lyricaloriginal.soracomsampleapp.api.SubScriber;

/**
 * Created by LyricalMaestro on 15/10/18.
 */
public class UpdateSpeedClassLoader extends BaseLoader<SubScriber> {

    private final AuthInfo _authInfo;
    private final String _imsi;
    private final String _speedClass;

    protected UpdateSpeedClassLoader(
            Context context, AuthInfo authInfo, String imsi, String speedClass) {
        super(context);
        _authInfo = authInfo;
        _imsi = imsi;
        _speedClass = speedClass;
    }

    @Override
    protected Response<SubScriber> load() throws Exception {
        SubScriber subScriber = SoracomApis.updateSpeedClass(_authInfo, _imsi, _speedClass);
        return new Response<SubScriber>(subScriber);
    }
}
