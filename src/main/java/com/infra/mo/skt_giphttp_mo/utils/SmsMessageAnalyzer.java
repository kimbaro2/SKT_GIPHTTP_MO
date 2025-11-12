package com.infra.mo.skt_giphttp_mo.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 * 메시지 MsgCode 와 MsgSubCode를 받아서 어떤 메시지 타입인지 판별합니다.
 */

@Slf4j
public class SmsMessageAnalyzer {
    public enum MessageType {
        MSG_CODE_SM_REQ_LINK("11", "3"),
        MSG_CODE_SM_REQ_MDN("11", "10"),
        MSG_CODE_SM_REQ_PORTED("11", "13"),
        MSG_CODE_SM_REQ_TRANS_RESULT("11", "9"),

        MSG_CODE_SM_RES_LINK("12", "3"),
        MSG_CODE_SM_RES_MDN("12", "10"),
        MSG_CODE_SM_RES_PORTED("12", "13"),
        MSG_CODE_SM_RES_TRANS_RESULT("12", "9"),

        UNKNOWN_TYPE("-1", "-1");

        private final String msgCode;
        private final String msgSubCode;

        MessageType(String msgCode, String msgSubCode) {
            this.msgCode = msgCode;
            this.msgSubCode = msgSubCode;
        }

        public String getMsgCode() {
            return msgCode;
        }

        public String getMsgSubCode() {
            return msgSubCode;
        }
    }

    public static MessageType analyze(String msgCode, String msgSubCode) {
        switch (msgCode) {
            case "11":
                switch (msgSubCode) {
                    case "3":
                        return MessageType.MSG_CODE_SM_REQ_LINK;
                    case "10":
                        return MessageType.MSG_CODE_SM_REQ_MDN;
                    case "13":
                        return MessageType.MSG_CODE_SM_REQ_PORTED;
                    case "9":
                        return MessageType.MSG_CODE_SM_REQ_TRANS_RESULT;
                }
                break;
            case "12":
                switch (msgSubCode) {
                    case "3":
                        return MessageType.MSG_CODE_SM_RES_LINK;
                    case "10":
                        return MessageType.MSG_CODE_SM_RES_MDN;
                    case "13":
                        return MessageType.MSG_CODE_SM_RES_PORTED;
                    case "9":
                        return MessageType.MSG_CODE_SM_RES_TRANS_RESULT;
                }
                break;
        }
        return MessageType.UNKNOWN_TYPE;
    }

}
