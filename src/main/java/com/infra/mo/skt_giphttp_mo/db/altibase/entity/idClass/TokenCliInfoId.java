package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class TokenCliInfoId implements Serializable {
    public TokenCliInfoId(String cid, String ipAddr, Integer portNo) {
        this.cid = cid;
        this.ipAddr = ipAddr;
        this.portNo = portNo;
    }

    public String cid;
    public String ipAddr;
    public Integer portNo;
}