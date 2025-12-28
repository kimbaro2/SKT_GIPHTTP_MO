package com.infra.mo.skt_giphttp_mo.dto.smsController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMO {

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
        public Short msgCode;

        @NotNull
        public Short msgSubCode;

        @NotNull
        public Integer bodyDataLen = 0;

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

        // MO 메시지 전용 필드 (SMPPSIMPLE 구조체에서)
        @NotNull
        public Integer nVldPrd = 0;              // 유효기간

        @NotNull
        public Byte ucRgtDlvFlg = 0;             // Right Delivery Flag

        @NotBlank
        public String callback = "";             // Callback 번호 (szCB[21])

        @NotNull
        public Byte msgLen = 0;                  // 메시지 길이 (ucMsgLen)

        @NotBlank
        public String msg = "";                  // 메시지 내용 (szMsg)

        @NotNull
        public Byte orgMsgTotalLen = 0;          // 원본 메시지 전체 길이

        @NotBlank
        public String msgId = "";                // MsgId (String) - 메시지 ID (MOCALLINFO 저장 시 사용된 msgId)
    }
}

