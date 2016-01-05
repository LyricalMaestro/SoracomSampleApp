package com.lyricaloriginal.soracomsampleapp;

import com.lyricaloriginal.soracomapiandroid.SpeedClassRequest;

/**
 * Created by LyricalMaestro on 2015/12/23.
 */
class SpeedClass {
    private static final String[] VALUE_LIST = {
            "s1.minimum", "s1.slow", "s1.standard", "s1.fast"
    };

    private SpeedClass() {
    }

    static SpeedClassRequest toRequest(String speedClass){
        SpeedClassRequest req = new SpeedClassRequest();
        req.speedClass = speedClass;
        return req;
    }

    /**
     * 速度クラスの値の一覧を取得します。
     *
     * @return 速度クラスの値の一覧
     */
    public static String[] getValues() {
        return VALUE_LIST.clone();
    }
}
