package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GipAccessHistoryId implements Serializable {
    private String procName;

    private String logNo;                   //LINE :: Log 번호

    private Date time;                      //LINE :: Config 변경 시간
}
