package com.lyricaloriginal.soracomsampleapp;


import android.app.Activity;
import android.app.Fragment;

import com.lyricaloriginal.soracomapiandroid.AuthInfo;
import com.lyricaloriginal.soracomapiandroid.AuthRequest;
import com.lyricaloriginal.soracomapiandroid.Soracom;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private Listener mListener = null;
    private Call<AuthInfo> mCall = null;

    public LoginFragment() {
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
        mListener = null;
        if (mCall != null) {
            mCall.cancel();
            mCall = null;
        }
    }

    public void login(String email, String password) {
        mCall = Soracom.API.auth(new AuthRequest(email, password));
        mCall.enqueue(new Callback<AuthInfo>() {
            @Override
            public void onResponse(Response<AuthInfo> response, Retrofit retrofit) {
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
        });
    }

    public boolean isConnecting(){
        return mCall != null;
    }

    public interface Listener {
        void onResponse(Response<AuthInfo> response);

        void onFailure(Throwable t);
    }

}
