package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.Search;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.CfgPrefixId;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CFG_PREFIX")
@DynamicUpdate
@Data
@IdClass(CfgPrefixId.class)
public class CFGPrefixEntity extends Search {
    @Id
    @Column(name = "TELE_PRE", length = 3)
    public String telePre;                     //LINE :: 통신사 국번

    @Id
    @Column(name = "ST_PRE", length = 4)
    public String stPre;                    //LINE :: 시작 국번

    @Id
    @Column(name = "END_PRE", length = 4)
    public String endPre;                      //LINE :: 마지막 국번

    @Column(name = "FSMSC", length = 4)
    public String fsmsc;

    @Column(name = "BSMSC", length = 4)
    public String bsmsc;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "REGDATE")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    public Date regDate;

    @Column(name = "DESCRIPTION", length = 40)
    public String description;

    @Transient
    public String descriptionSubData;
}
