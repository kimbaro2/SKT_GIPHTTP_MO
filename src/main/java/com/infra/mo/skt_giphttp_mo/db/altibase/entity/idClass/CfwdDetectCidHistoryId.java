package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Data
public class CfwdDetectCidHistoryId implements Serializable {
    private String cid;        //LINE :: 인증용 CID로서 연결 세션 연동 용

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;                      //LINE :: Config 변경 시간
}
