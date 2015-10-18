package com.lyricaloriginal.soracomsampleapp;

/**
 * Created by LyricalMaestro on 15/10/18.
 */
public final class Response<D> {

    private final D _data;
    private final boolean _result;
    private final Exception _ex;

    /**
     * コンストラクタ
     *
     * @param data
     */
    Response(D data) {
        _data = data;
        _result = true;
        _ex = null;
    }

    /**
     * コンストラクタ
     *
     * @param ex
     */
    Response(Exception ex) {
        _data = null;
        _result = false;
        _ex = ex;
    }

    public D getData() {
        return _data;
    }

    public boolean getResult() {
        return _result;
    }

    public Exception getException() {
        return _ex;
    }
}
