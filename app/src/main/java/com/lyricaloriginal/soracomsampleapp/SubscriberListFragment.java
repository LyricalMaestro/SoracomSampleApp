package com.lyricaloriginal.soracomsampleapp;


import android.app.Activity;
import android.app.Fragment;

import com.lyricaloriginal.soracomapiandroid.Soracom;
import com.lyricaloriginal.soracomapiandroid.SubScriber;

import java.util.List;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriberListFragment extends Fragment {

    private Listener mListener = null;
    private Call<List<SubScriber>> mCall = null;

    public SubscriberListFragment() {
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

    public void subScribers(Auth authInfo) {
        if (mCall != null) {
            return;
        }

        mCall = Soracom.API.subscribers(authInfo.apiKey, authInfo.token);
        mCall.enqueue(new Callback());
    }


    public boolean isConnecting() {
        return mCall != null;
    }

    public interface Listener {
        void onResponse(Response<List<SubScriber>> response);

        void onFailure(Throwable t);
    }

    private class Callback implements retrofit.Callback<List<SubScriber>> {

        @Override
        public void onResponse(Response<List<SubScriber>> response, Retrofit retrofit) {
            mCall = null;
            if (mListener != null)
                mListener.onResponse(response);
        }

        @Override
        public void onFailure(Throwable t) {
            mCall = null;
            if (mListener != null)
                mListener.onFailure(t);
        }
    }

}
