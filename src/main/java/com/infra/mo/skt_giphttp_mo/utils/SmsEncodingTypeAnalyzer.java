package com.infra.mo.skt_giphttp_mo.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 * 메시지 인코딩 타입을 분류합니다.
 */

@Slf4j
public class SmsEncodingTypeAnalyzer {
    public enum MessageType {
        UNKNOWN_TYPE(-1,null),
        GSM_7BIT(0,"GSM"),
        ASCII_7BIT(1,"ASCII"),
        BINARY_8BIT(4,"ISO-8859-1"),
        UCS2_BIGENDIAN(8, "UTF-16BE"),
        KSC5601_CP949(14,"CP949");

        private final Integer encodingType;
        private final String encodingTypeName;


        MessageType(Integer encodingType, String encodingTypeName) {

            this.encodingType = encodingType;
            this.encodingTypeName = encodingTypeName;
        }

        public String getEncodingTypeName() {
            return encodingTypeName;
        }

        public Integer getEncodingType() {
            return encodingType;
        }
    }

    public static MessageType analyze(Integer encodingType) {
        switch (encodingType) {
            case 0:
                return MessageType.GSM_7BIT;
            case 1:
                return MessageType.ASCII_7BIT;
            case 4:
                return MessageType.BINARY_8BIT;
            case 8:
                return MessageType.UCS2_BIGENDIAN;
            case 14:
                return MessageType.KSC5601_CP949;
            default:
                log.error("Unknown encoding type: {}", encodingType);
                return MessageType.UNKNOWN_TYPE;
        }
    }
}
