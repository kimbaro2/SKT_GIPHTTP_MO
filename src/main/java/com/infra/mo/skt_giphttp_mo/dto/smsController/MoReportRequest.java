package com.infra.mo.skt_giphttp_mo.dto.smsController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.MOCallInfoEntity;
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

        /**
         * mo-report 응답 전용: 조회된 MOCALLINFO 레코드 (요청 시에는 null)
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public MoCallInfoBody moCallInfo;
    }

    /**
     * mo-report 응답용: MOCALLINFO 테이블 조회 결과
     */
    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MoCallInfoBody {
        public String msgId;
        public String srcCId;
        public String destCId;
        public String srcCallNo;
        public String destCallNo;
        public String moSubTime;
        public Integer msgLen;
        public Long roamingId;
        public String cb;
        public String roamPMN;
        public String wZone;
        public String traceId;
        public String origMvnoInfo;
        public String destMvnoInfo;
        public String rcs;
        public Integer dcsType;
        public Integer orgMsgLen;
        public String moRecvTime;
        public String virtualNum;
        public java.util.Date expireTime;
        public Integer segment;
        public String tid;
        public Integer centerno;
        public String msg;
        public Integer returnQno;
        public String w2pMsgId;
        public String fwdSrc;
        public Integer npdbQueryCnt;
        public Integer esmClass;

        /**
         * MOCALLINFO Entity를 MoCallInfoBody로 변환 (mo-report 응답용)
         */
        public static MoCallInfoBody fromEntity(MOCallInfoEntity e) {
            if (e == null) return null;
            MoCallInfoBody b = new MoCallInfoBody();
            b.msgId = e.msgId;
            b.srcCId = e.srcCId;
            b.destCId = e.destCId;
            b.srcCallNo = e.srcCallNo;
            b.destCallNo = e.destCallNo;
            b.moSubTime = e.moSubTime;
            b.msgLen = e.msgLen;
            b.roamingId = e.roamingId;
            b.cb = e.cb;
            b.roamPMN = e.roamPMN;
            b.wZone = e.wZone;
            b.traceId = e.traceId;
            b.origMvnoInfo = e.origMvnoInfo;
            b.destMvnoInfo = e.destMvnoInfo;
            b.rcs = e.rcs;
            b.dcsType = e.dcsType;
            b.orgMsgLen = e.orgMsgLen;
            b.moRecvTime = e.moRecvTime;
            b.virtualNum = e.virtualNum;
            b.expireTime = e.expireTime;
            b.segment = e.segment;
            b.tid = e.tid;
            b.centerno = e.centerno;
            b.msg = e.msg;
            b.returnQno = e.returnQno;
            b.w2pMsgId = e.w2pMsgId;
            b.fwdSrc = e.fwdSrc;
            b.npdbQueryCnt = e.npdbQueryCnt;
            b.esmClass = e.esmClass;
            return b;
        }
    }
}
