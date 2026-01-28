package com.infra.mo.skt_giphttp_mo.dto.smsController;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * MO-TR 결과 전송/수신을 위한 공통 DTO
 * 
 * 사용처:
 * 1. mo-report API: CP로부터 MO-TR 결과 수신
 * 2. MO-TR Request: CP로 MO-TR 결과 전송
 * 
 * 규격:
 * {
 *   "msgVerId": 510,
 *   "encFlag": 0,
 *   "data": {
 *     "logNo": "",
 *     "cid": "",
 *     "msgId": "",
 *     "traceId": "",
 *     "status": "MSG_STATUS 상수값",
 *     "msgType": 4
 *   }
 * }
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MoReportRequest {
    
    /**
     * 메시지 규격 버전 (510 고정)
     */
    @NotNull
    public Integer msgVerId = 510;
    
    /**
     * 암호화 여부 (0: 평문, 1: 암호화)
     */
    @NotNull
    public Integer encFlag = 0;
    
    /**
     * 데이터 본문
     */
    @NotNull
    public DataBody data;
    
    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataBody {
        /**
         * HTTP_MOSEND_ACCESS의 CID (선택, 없으면 MOCALLINFO에서 destCId로 조회)
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String cid;
        
        /**
         * MOCALLINFO에서 생성한 MSGID (필수)
         */
        @NotBlank
        public String msgId;
        
        /**
         * MOCALLINFO의 TRACE_ID (필수)
         */
        @NotBlank
        public String traceId;
        
        /**
         * 메시지 상태 (필수, MO Send Request 시점에는 null 가능)
         * 0:FWD_DETECT_CID, 1:MRMSPAM, 2:DELIVERED, 3:EXPIRED, 4:DELETED, 5:UNDELIVERABLE,
         * 6:ACCEPTED, 7:PORTED_OUT, 8:PORTEDOUT_KTF, 9:PORTEDOUT_LGT, 10:PORTEDOUT_SKT,
         * 12:FORWARD, 13:NCHANGE, 14:FWDFAIL, 16:SPAMERR, 17:USERDEL, 19:NPREFIX, 20:ADMCANC
         */
        public Integer status;
        
        /**
         * 메시지 타입 (MO-TR 요청 시 4만 허용, 에러 응답에서는 제외됨)
         * 1:MO, 4:MO-TR
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Integer msgType;
    }
}
