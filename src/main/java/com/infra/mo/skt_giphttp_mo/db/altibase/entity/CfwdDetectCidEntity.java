package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.Search;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "CFG_CFWD_DETECT_CID")
@DynamicUpdate
@Data
public class CfwdDetectCidEntity extends Search {
    @Id
    @Column(name = "CID", length = 32)
    private String cid;                 //LINE :: 착신전환을 차단하기 위한 CP CID

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREDATE", columnDefinition = "date")
    @Temporal(TemporalType.TIMESTAMP)
    //@CreationTimestamp
    private Date creDate;               //LINE :: 최초 등록날짜

    @Transient private List<String> idList;

    @Transient
    private String changeCode;
}
