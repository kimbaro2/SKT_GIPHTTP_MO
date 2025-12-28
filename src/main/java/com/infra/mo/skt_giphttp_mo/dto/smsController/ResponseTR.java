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
public class ResponseTR {

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
        
        // Body 필드
        // SM_REQ_SIMPLE (10) 케이스: ACK 결과 코드
        public Integer ackResult; // GI_RES_NO_ERR = 0 (성공), 그 외는 실패
        
        // SM_REQ_TRANS_RESULT (9) 케이스: MO-TR ACK 결과
        public Integer result; // Result (int) - 0이면 성공, 0이 아니면 오류 종류
        
        // SM_REQ_TRANS_RESULT (9) 케이스: MO-TR 상태 및 메시지 ID
        public Integer msgStatus = 0; // MsgStatus (int) - 2:DELIVERED, 3:EXPIRED, 5:UNDELIVERED 등
        public String msgId; // MsgId (String) - 메시지 ID
    }
}
