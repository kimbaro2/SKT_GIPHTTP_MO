package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.HistoryEMSDefaultEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.TELEPrefixHistoryId;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "TELE_PREFIX_HISTORY")
@DynamicUpdate
@Data
@IdClass(TELEPrefixHistoryId.class)
public class TELEPrefixHistoryEntity extends HistoryEMSDefaultEntity {

    //PK : TELE_PREFIX, START_PREFIX, END_PREFIX, TIME

    @Id
    @Column(name = "TELE_PREFIX", length = 3)
    private String telePrefix;                         // LINE :: 통신사 국번

    @Id
    @Column(name = "START_PREFIX", length = 4)
    private String startPrefix;                        // LINE :: 시작 국번

    @Id
    @Column(name = "END_PREFIX", length = 4)
    private String endPrefix;                          // LINE :: 마지막 국번

    @Column(name = "TELECOM", columnDefinition = "NUMERIC(2)")
    private int telecom;                           //LINE :: 이통사 구분 번호 (11:SKT, 16:KTF, 19:LGT)

    @Column(name = "DESCRIPTION", length = 40)
    private String description;                        // LINE :: 설명
}
