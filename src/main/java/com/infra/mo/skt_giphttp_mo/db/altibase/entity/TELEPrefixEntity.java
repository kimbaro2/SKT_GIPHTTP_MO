package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.Search;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.TELEPrefixId;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "TELE_PREFIX")
@DynamicUpdate
@Data
@IdClass(TELEPrefixId.class)
public class TELEPrefixEntity extends Search {
    @Id
    @Column(name = "TELE_PREFIX", length = 3)
    public String telePrefix;                     //LINE :: 통신사 국번

    @Id
    @Column(name = "START_PREFIX", length = 4)
    public String startPrefix;                    //LINE :: 시작 국번

    @Id
    @Column(name = "END_PREFIX", length = 4)
    public String endPrefix;                      //LINE :: 마지막 국번

    @Column(name = "TELECOM", columnDefinition = "NUMERIC(2)")
    public int telecom;                           //LINE :: 이통사 구분 번호 (11:SKT, 16:KTF, 19:LGT)

    @Column(name = "DESCRIPTION", length = 40)
    public String description;                    //LINE :: 설명

    @Transient
    public String changeCode;

    @Transient public TELEPrefixId idClass;           //LINE :: TelePrefixEntity 복합키
    @Transient public List<TELEPrefixId> idList;

    public TELEPrefixEntity(){}

    public TELEPrefixEntity(TELEPrefixHistoryEntity entity){
        this.telePrefix = entity.getTelePrefix();
        this.startPrefix = entity.getStartPrefix();
        this.endPrefix = entity.getEndPrefix();
        this.telecom = entity.getTelecom();
        this.description = entity.getDescription();
    }
}
