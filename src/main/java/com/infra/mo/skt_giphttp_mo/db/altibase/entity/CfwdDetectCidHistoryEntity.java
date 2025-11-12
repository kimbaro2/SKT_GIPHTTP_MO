package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.HistoryEMSDefaultEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.CfwdDetectCidHistoryId;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "CFG_CFWD_DETECT_CID_HISTORY")
@DynamicUpdate
@Data
@IdClass(CfwdDetectCidHistoryId.class)
public class CfwdDetectCidHistoryEntity extends HistoryEMSDefaultEntity {
    @Id
    @Column(name = "CID", length = 32)
    private String cid;                 //LINE :: 착신전환을 차단하기 위한 CP CID

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREDATE", columnDefinition = "date")
    @Temporal(TemporalType.TIMESTAMP)
    //@CreationTimestamp
    private Date creDate;               //LINE :: 최초 등록날짜

    @Transient
    private CfwdDetectCidHistoryId idClass;
    @Transient
    private List<CfwdDetectCidHistoryId> idClassList;
}
