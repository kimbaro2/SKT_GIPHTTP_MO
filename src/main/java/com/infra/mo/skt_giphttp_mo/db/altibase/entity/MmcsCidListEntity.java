package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.Search;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "CFG_MMCS_CID_LIST")
@DynamicUpdate
@Data
public class MmcsCidListEntity extends Search {
    @Id
    @Column(name = "CID", length = 10)
    private String cid;

    @Column(name = "DESCRIPTION", length = 20)
    private String description;                 //LINE :: 설명

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "REGI_DATE", columnDefinition = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date regiDate;                         //LINE :: 최초 등록날짜

    @Transient
    private List<String> idList;

    @Transient
    private String changeCode;

}

