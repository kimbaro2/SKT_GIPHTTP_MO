package com.infra.mo.skt_giphttp_mo.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 * 메시지 상태값을 정의합니다.
 */

@Slf4j
public class SmsStatusAnalyzer {

    public enum StatusType {

        GI_RES_NO_ERR("0"),
        GI_RES_SUBS_INVALID("10"),
        GI_RES_SUBS_SUSPENDED("11"),
        GI_RES_SUBS_INVALID_TERM("12"),
        GI_RES_SUBS_INVALID_TERM_KOR("13"),
        GI_RES_SUBS_INVALID_TERM_ENG("14"),
        GI_RES_SUBS_INVALID_CALLNO("15"),
        GI_RES_FORMAT_INVALID("20"),
        GI_RES_FORMAT_INVALID_CID("21"),
        GI_RES_FORMAT_INVALID_MSG_CODE("22"),
        GI_RES_FORMAT_INVALID_MSG_SUBCODE("23"),
        GI_RES_FORMAT_INVALID_MSG_SEQNO("24"),
        GI_RES_FORMAT_INVALID_DATA_TYPE("25"),
        GI_RES_OPMASK_DENY_FLAG("40"),
        GI_RES_OPMASK_DENY_VLDPRD("41"),
        GI_RES_OPMASK_DENY_PRIOROTY("42"),
        GI_RES_OPMASK_DENY_REPFLG("43"),
        GI_RES_OPMASK_DENY_RGTDLV("44"),
        GI_RES_OPMASK_DENY_OPERATION("50"),
        GI_RES_OPMASK_DENY_CONNECT("51"),
        GI_RES_OPMASK_DENY_SEND("52"),
        GI_RES_OPMASK_DENY_LINK("53"),
        GI_RES_OPMASK_DENY_REPLACE("54"),
        GI_RES_OPMASK_DENY_CANCEL("55"),
        GI_RES_OPMASK_DENY_QUERY("56"),
        GI_RES_OPMASK_DENY_GET("57"),
        GI_RES_FC_NAK("60"),
        GI_RES_Q_INSERT_FAILED("61"),
        GI_RES_INVALID_VERID("62"),
        GI_RES_SPAM("63"),
        UNKNOWN_TYPE("-1");

        private final String statusType;

        public String getStatusType() {
            return statusType;
        }

        StatusType(String msgCode) {
            this.statusType = msgCode;
        }
    }

    public static StatusType analyze(String statusType) {
        switch (statusType) {
            case "0":
                return StatusType.GI_RES_NO_ERR;
            case "10":
                return StatusType.GI_RES_SUBS_INVALID;
            case "11":
                return StatusType.GI_RES_SUBS_SUSPENDED;
            case "12":
                return StatusType.GI_RES_SUBS_INVALID_TERM;
            case "13":
                return StatusType.GI_RES_SUBS_INVALID_TERM_KOR;
            case "14":
                return StatusType.GI_RES_SUBS_INVALID_TERM_ENG;
            case "15":
                return StatusType.GI_RES_SUBS_INVALID_CALLNO;
            case "20":
                return StatusType.GI_RES_FORMAT_INVALID;
            case "21":
                return StatusType.GI_RES_FORMAT_INVALID_CID;
            case "22":
                return StatusType.GI_RES_FORMAT_INVALID_MSG_CODE;
            case "23":
                return StatusType.GI_RES_FORMAT_INVALID_MSG_SUBCODE;
            case "24":
                return StatusType.GI_RES_FORMAT_INVALID_MSG_SEQNO;
            case "25":
                return StatusType.GI_RES_FORMAT_INVALID_DATA_TYPE;
            case "40":
                return StatusType.GI_RES_OPMASK_DENY_FLAG;
            case "41":
                return StatusType.GI_RES_OPMASK_DENY_VLDPRD;
            case "42":
                return StatusType.GI_RES_OPMASK_DENY_PRIOROTY;
            case "43":
                return StatusType.GI_RES_OPMASK_DENY_REPFLG;
            case "44":
                return StatusType.GI_RES_OPMASK_DENY_RGTDLV;
            case "50":
                return StatusType.GI_RES_OPMASK_DENY_OPERATION;
            case "51":
                return StatusType.GI_RES_OPMASK_DENY_CONNECT;
            case "52":
                return StatusType.GI_RES_OPMASK_DENY_SEND;
            case "53":
                return StatusType.GI_RES_OPMASK_DENY_LINK;
            case "54":
                return StatusType.GI_RES_OPMASK_DENY_REPLACE;
            case "55":
                return StatusType.GI_RES_OPMASK_DENY_CANCEL;
            case "56":
                return StatusType.GI_RES_OPMASK_DENY_QUERY;
            case "57":
                return StatusType.GI_RES_OPMASK_DENY_GET;
            case "60":
                return StatusType.GI_RES_FC_NAK;
            case "61":
                return StatusType.GI_RES_Q_INSERT_FAILED;
            case "63":
                return StatusType.GI_RES_SPAM;

        }
        return StatusType.UNKNOWN_TYPE;
    }
}
