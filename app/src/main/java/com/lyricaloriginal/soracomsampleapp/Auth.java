package com.lyricaloriginal.soracomsampleapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.lyricaloriginal.soracomapiandroid.AuthInfo;

/**
 * Created by LyricalOriginal on 2015/12/23.
 */
public class Auth implements Parcelable{
    public final String apiKey;
    public final String token;

    public Auth(AuthInfo authInfo){
        this.apiKey = authInfo.apiKey;
        this.token = authInfo.token;
    }

    private Auth(Parcel in) {
        apiKey = in.readString();
        token = in.readString();
    }

    public static final Creator<Auth> CREATOR = new Creator<Auth>() {
        @Override
        public Auth createFromParcel(Parcel in) {
            return new Auth(in);
        }

        @Override
        public Auth[] newArray(int size) {
            return new Auth[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(apiKey);
        dest.writeString(token);
    }
}
