package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Data
public class TELEPrefixHistoryId implements Serializable {
    private String telePrefix;                  // LINE :: 통신사 국번

    private String startPrefix;                 // LINE :: 시작 국번

    private String endPrefix;                   // LINE :: 마지막 국번

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;                          // LINE :: Config 변경 시간
}
