package com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Data
public class HistoryEMSDefaultEntity extends Search {
    @Id
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "TIME", columnDefinition = "DATE(19)")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date time;                      //LINE :: Config 변경 시간

    @Column(name = "EMS_IP", length = 16)
    protected String emsIp;                      //LINE :: EMS IP

    @Column(name = "EMS_ID", length = 20)
    protected String emsId;                      //LINE :: EMS ID

    @Column(name = "CHANGE_CODE", length = 1)
    protected String changeCode;                 //LINE :: Change Code (I:Insert, D:Delete, U:Update)


}
