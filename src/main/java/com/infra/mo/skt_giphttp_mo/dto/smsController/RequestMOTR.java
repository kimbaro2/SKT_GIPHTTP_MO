package com.infra.mo.skt_giphttp_mo.dto.smsController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * MO-TR 결과를 CP 서버로 전송하기 위한 DTO
 * 
 * C 코드 참고: GIPEVENT_c.c
 * - LINE 1309-1470: MakePacketFromQItem() 함수 내부 SM_REQ_TRANS_RESULT 케이스 (Body 구성)
 *   - LINE 1314: ptrTransRes->ucMsgStatus = ptrQItem->ucMsgStatus (MsgStatus 설정)
 *   - LINE 1444: ptrTransRes->ucGSMErrCode = ptrQItem->ucGSMErrCode (Rsv 설정)
 *   - LINE 1445: memcpy(ptrTransRes->ucMsgId, ptrQItem->ucMsgId, QITEM_SIZE_MSGID) (MsgId 복사)
 * - LINE 1056-1074: SM_REQ_TRANS_RESULT 케이스 (실제 전송)
 *   - LINE 1063: SendTcpMsg((char *)ptrMsgHdr, nMsgLen) (TCP 전송)
 * 
 * 구조:
 * - Header: MsgCode=11 (MSG_CODE_SM_REQ), MsgSubCode=9 (SM_REQ_TRANS_RESULT)
 * - Body: MsgStatus (char), Rsv (char), MsgId (char[9])
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RequestMOTR {

    @NotNull
    public Integer msgVerId;

    @NotNull
    public Integer encFlag = 0; /*암호화여부*/

    @NotNull
    public DataBody data;

    @Data
    public static class DataBody {

        @NotBlank
        public String srcCID = "";

        @NotBlank
        public String srcCallNo = ""; // int (HTTP JSON에서는 String으로 전송)

        public Integer srcAddrRsv = 0; // SrcAddrRsv int (0)

        @NotBlank
        public String destCID = "";

        @NotBlank
        public String destCallNo = ""; // int (HTTP JSON에서는 String으로 전송)

        public Integer destAddrRsv = 0; // DestAddrRsv int (0)

        @NotNull
        public Short msgCode = 11; // MSG_CODE_SM_REQ

        @NotNull
        public Short msgSubCode = 9; // SM_REQ_TRANS_RESULT

        @NotNull
        public Integer bodyDataLen = 11; // MsgStatus(1) + Rsv(1) + MsgId(9) = 11

        @NotNull
        public Integer msgSeqNo = 0; // MsgSeqNo int (1,2,3,4,5...)

        @NotBlank
        public String termtype = ""; // char (HTTP JSON에서는 String으로 전송)

        public Byte dataType = 0; // DataType char (0)

        @NotNull
        public Integer dataEncoding = 0;

        @NotBlank
        public String concatenateflag = ""; // char (HTTP JSON에서는 String으로 전송)

        @NotBlank
        public String concatenateInfo = ""; // char (HTTP JSON에서는 String으로 전송)

        @NotNull
        public List<Rsv4ProtocolItem> rsv4Protocol;

        // Header 추가 필드
        public Short teleServiceID = 0; // TeleServiceID

        public Short msgCodeRsv = 0; // MsgCodeRsv

        public List<Integer> reserved2; // reserved2 Int[2]

        public String time = ""; // TIME Char[11] - 'YYMMDDHHMM' 형식

        // Body 필드 (MO-TR 전송용)
        @NotNull
        public Byte msgStatus = 0; 
        // 2: 성공
        // 3: 시간초과
        // 5: 전송불가
        // 7: 번호 이동 가입자
        // 8: KTF로 번호 이동
        // 9: LGT로 번호 이동
        // 10: 세대간 번호이동
        // 11: NPDB 오류

        @NotNull
        public Byte rsv = 0;

        @NotBlank
        public String msgId = ""; // char[9] - MT 시 전송된 ID
    }
}

