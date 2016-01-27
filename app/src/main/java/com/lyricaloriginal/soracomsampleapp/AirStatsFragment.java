package com.lyricaloriginal.soracomsampleapp;


import android.app.Activity;
import android.app.Fragment;

import com.lyricaloriginal.soracomapiandroid.AirStats;
import com.lyricaloriginal.soracomapiandroid.Soracom;

import java.util.List;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class AirStatsFragment extends Fragment {

    private Call<List<AirStats>> mCall = null;
    private Listener mListener = null;


    public AirStatsFragment() {
        super.setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Listener) {
            mListener = (Listener) activity;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCall != null)
            mCall.cancel();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void airSubscribers(Auth authInfo, String imsi) {
        if (mCall != null) {
            return;
        }
        int current = (int) (System.currentTimeMillis() / 1000L);
        int before = current - 60 * 60 * 24 * 365;  //  １年分

        mCall = Soracom.API.airSubscribers(authInfo.apiKey,
                authInfo.token, imsi, before, current, "month");
        mCall.enqueue(new Callback(current, before));
    }


    public boolean isConnecting() {
        return mCall != null;
    }

    public interface Listener {
        void onResponse(Response<List<AirStats>> response, int current, int before);

        void onFailure(Throwable t);
    }

    private class Callback implements retrofit.Callback<List<AirStats>> {

        private int mCurent;
        private int mBefore;

        Callback(int current, int before){
            mCurent = current;
            mBefore = before;
        }

        @Override
        public void onResponse(Response<List<AirStats>> response, Retrofit retrofit) {
            mCall = null;
            if (mListener != null)
                mListener.onResponse(response, mCurent, mBefore);
        }

        @Override
        public void onFailure(Throwable t) {
            mCall = null;
            if (mListener != null)
                mListener.onFailure(t);
        }
    }
}
