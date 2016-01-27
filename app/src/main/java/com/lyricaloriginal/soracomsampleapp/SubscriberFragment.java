package com.lyricaloriginal.soracomsampleapp;


import android.app.Activity;
import android.app.Fragment;

import com.lyricaloriginal.soracomapiandroid.Soracom;
import com.lyricaloriginal.soracomapiandroid.SpeedClassRequest;
import com.lyricaloriginal.soracomapiandroid.SubScriber;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriberFragment extends Fragment {

    private Listener mListener = null;
    private Call<SubScriber> mCall = null;

    public SubscriberFragment() {
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

    public void subScriber(Auth authInfo, String imsi) {
        if (mCall != null) {
            return;
        }

        mCall = Soracom.API.subscriber(authInfo.apiKey, authInfo.token, imsi);
        mCall.enqueue(new Callback("subScriber"));
    }

    public void updateSpeedClass(Auth authInfo, String imsi, String speedClass) {
        SpeedClassRequest speedClassRequest = new SpeedClassRequest();
        speedClassRequest.speedClass = speedClass;
        mCall = Soracom.API.updateSpeedClass(authInfo.apiKey, authInfo.token,
                imsi, speedClassRequest);
        mCall.enqueue(new Callback("updateSpeedClass"));
    }

    public void changeStatusActivate(Auth authInfo, String imsi) {
        mCall = Soracom.API.changeStatusActivate(authInfo.apiKey, authInfo.token, imsi);
        mCall.enqueue(new Callback("changeStatusActivate"));
    }

    public void changeStatusDeactivate(Auth authInfo, String imsi) {
        mCall = Soracom.API.changeStatusDeactivate(authInfo.apiKey, authInfo.token, imsi);
        mCall.enqueue(new Callback("changeStatusDeactivate"));
    }

    public boolean isConnecting() {
        return mCall != null;
    }

    public interface Listener {
        void onResponse(Response<SubScriber> response, String tag);

        void onFailure(Throwable t, String tag);
    }

    private class Callback implements retrofit.Callback<SubScriber> {

        private String mTag;

        Callback(String tag) {
            mTag = tag;
        }

        @Override
        public void onResponse(Response<SubScriber> response, Retrofit retrofit) {
            mCall = null;
            if (mListener != null)
                mListener.onResponse(response, mTag);
        }

        @Override
        public void onFailure(Throwable t) {
            mCall = null;
            if (mListener != null)
                mListener.onFailure(t, mTag);
        }
    }

}
