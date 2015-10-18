package com.lyricaloriginal.soracomsampleapp.api;

import java.math.BigDecimal;

/**
 * SubScriberの情報を保持するためのクラスです。<BR>
 * <p/>
 * Created by LyricalMaestro on 15/10/17.
 */
public class SubScriber {
    /**
     * IMSI
     */
    public String imsi;
    /**
     * MSISDN
     */
    public String msisdn;
    /**
     * 確保済みIPアドレス
     */
    public String ipAddress;
    /**
     * APN。(soracom.ioしか入らないと思うが)
     */
    public String apn;
    /**
     * 速度クラス。
     */
    public String speedClass;
    public BigDecimal createdAt;
    public BigDecimal lastModifiedAt;
    public BigDecimal expirtyTime;
    /**
     * ステータス。状態。<BR>
     * 「準備完了」、「使用中」、「休止中」など。
     */
    public String status;
    /**
     * タグ情報
     */
    public Tags tags;
    /**
     * OperatorId
     */
    public String operatorId;

    /**
     * タグ情報をまとめたクラスです。
     */
    public static class Tags {
        /**
         * 名前
         */
        public String name;
    }
}
