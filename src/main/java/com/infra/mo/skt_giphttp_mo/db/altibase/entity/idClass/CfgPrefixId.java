package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.Data;

import java.io.Serializable;

@Data
public class CfgPrefixId implements Serializable {
    private String telePre;                  // LINE :: 통신사 국번

    private String stPre;                 // LINE :: 시작 국번

    private String endPre;                   // LINE :: 마지막 국번
}
