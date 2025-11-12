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
        public String srcCallNo = "";

        @NotBlank
        public String destCID = "";

        @NotBlank
        public String destCallNo = "";

        @NotNull
        public Short msgCode;

        @NotNull
        public Short msgSubCode;

        @NotNull
        public Integer bodyDataLen = 0;

        @NotBlank
        public String termtype = "";

        @NotNull
        public Integer dataEncoding = 0;

        @NotBlank
        public String concatenateflag = "";

        @NotBlank
        public String concatenateInfo = "";

        @NotNull
        public List<Rsv4ProtocolItem> rsv4Protocol;
    }
}
