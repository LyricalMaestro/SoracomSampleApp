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
    /**
     * 作成時間？？
     */
    public BigDecimal createdAt;
    /**
     * 最終修正日時
     */
    public BigDecimal lastModifiedAt;
    /**
     * グループID
     */
    public String groupId;
    /**
     * 解約できるかどうか？
     */
    public boolean terminationEnabled;
    /**
     * 有効期限。
     */
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
     * セッション状態
     */
    public SessionStatus sessionStatus;
    /**
     * OperatorId
     */
    public String operatorId;

    /**
     * SIMのサイズ。[mini]とか[nano]とかが入る。
     */
    public String moduleType;

    /**
     * タグ情報をまとめたクラスです。
     */
    public static class Tags {
        /**
         * 名前
         */
        public String name;
    }

    public static class SessionStatus {
        /**
         * 最終更新日時
         */
        public BigDecimal lastUpdateAt;
        /**
         * IMEI
         */
        public String imei;
        /**
         * オンラインかどうか
         */
        public boolean online;
        /**
         * IDアドレスだけど…?
         */
        public String ueIpAddress;
    }
}
