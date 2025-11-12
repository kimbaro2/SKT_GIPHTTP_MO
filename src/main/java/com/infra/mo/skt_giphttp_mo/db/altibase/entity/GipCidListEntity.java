package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.GipCidListId;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "GIPCIDLIST")
@Data
@DynamicUpdate
@IdClass(GipCidListId.class)
public class GipCidListEntity {
    @Id
    @Column(name = "MODULEID", length = 10, nullable = false)
    private String moduleId;

    @Id
    @Column(name = "PARENT_CID", length = 10, nullable = false)
    private String parentCid;

    @Id
    @Column(name = "CHILD_CID", length = 10, nullable = false)
    private String childCid;

    @Column(name = "REGDATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date regDate;

    @Column(name = "DESCRIPTION", length = 60)
    private String description;
}