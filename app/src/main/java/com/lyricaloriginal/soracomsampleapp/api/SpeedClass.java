package com.lyricaloriginal.soracomsampleapp.api;

/**
 * 速度クラスの情報をまとめたクラスです。
 * <p/>
 * Created by LyricalMaestro on 15/10/19.
 */
public final class SpeedClass {

    private static final String[] VALUE_LIST = {
            "s1.minimum", "s1.slow", "s1.standard", "s1.fast"
    };

    private SpeedClass() {
    }

    static boolean isValidValue(String speedClass) {
        for (String value : VALUE_LIST) {
            if (value.equals(speedClass)) {
                return true;
            }
        }
        return false;
    }

    static String toJsonString(String speedClass) {
        return String.format("{\"speedClass\":\"%s\"}", speedClass);
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
