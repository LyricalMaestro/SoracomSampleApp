package com.lyricaloriginal.soracomsampleapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by LyricalMaestro on 15/10/18.
 */
public abstract class BaseLoader<D> extends AsyncTaskLoader<Response<D>> {

    protected BaseLoader(Context context) {
        super(context);
    }

    @Override
    public Response<D> loadInBackground() {
        Response<D> resp = null;
        try {
            resp = load();
        } catch (Exception ex) {
            resp = new Response<D>(ex);
        }
        return resp;
    }

    protected abstract Response<D> load() throws Exception;

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}