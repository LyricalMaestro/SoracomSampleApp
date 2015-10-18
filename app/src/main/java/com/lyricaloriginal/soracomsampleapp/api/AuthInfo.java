package com.lyricaloriginal.soracomsampleapp.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * APIキー、OperatorId、tokenを管理するためのクラスです。<BR>
 * 各種APIを実行するために必要な情報です。
 * <p/>
 * Created by LyricalMaestro on 15/10/17.
 */
public class AuthInfo implements Parcelable {
    public static final Creator<AuthInfo> CREATOR = new Creator<AuthInfo>() {
        @Override
        public AuthInfo createFromParcel(Parcel in) {
            return new AuthInfo(in);
        }

        @Override
        public AuthInfo[] newArray(int size) {
            return new AuthInfo[size];
        }
    };
    /**
     * APIキー
     */
    public String apiKey;
    /**
     * OperatorID
     */
    public String operatorId;
    /**
     * Token
     */
    public String token;

    public AuthInfo() {
    }

    private AuthInfo(Parcel in) {
        apiKey = in.readString();
        operatorId = in.readString();
        token = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(apiKey);
        dest.writeString(operatorId);
        dest.writeString(token);
    }
}
