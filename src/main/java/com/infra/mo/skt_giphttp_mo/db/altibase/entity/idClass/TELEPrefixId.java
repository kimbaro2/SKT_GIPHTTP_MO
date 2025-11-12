package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.Data;

import java.io.Serializable;

@Data
public class TELEPrefixId implements Serializable {
    private String telePrefix;                  // LINE :: 통신사 국번

    private String startPrefix;                 // LINE :: 시작 국번

    private String endPrefix;                   // LINE :: 마지막 국번
}
