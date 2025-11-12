package com.infra.mo.skt_giphttp_mo.dto;

import com.infra.mo.skt_giphttp_mo.dto.TrResultRequestData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TrResultRequest {
    public int msgVerId; //버전
    public String msgId;
    public int status;
    public String resultCode;
    public String time;
    public TrResultRequestData data;
}