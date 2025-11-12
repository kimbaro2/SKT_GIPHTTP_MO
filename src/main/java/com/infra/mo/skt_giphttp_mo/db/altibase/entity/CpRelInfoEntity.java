package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.Search;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.CpRelInfoId;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "CP_REL_INFO")
@DynamicUpdate
@Data
@IdClass(CpRelInfoId.class)
public class CpRelInfoEntity extends Search {
    @Column(name = "IP_ADDR", length = 15)
    private String ipAddr;                      //LINE :: Client IP 주소

    @Id
    @Column(name = "P_CID", length = 15)
    private String pcid;                        //LINE :: 인증용 CID로서 연결 세션 연동 용

    @Id
    @Column(name = "C_CID", length = 15)
    private String ccid;                        //LINE :: GIP메시지를 전송할때 메시지 내에 발신 CID로 가능한 CID

    @Column(name = "TARGET_SVR", columnDefinition = "NUMERIC(2)")
    private int targetSvr;                      //LINE :: P_CID가 연동된 VSMSS 서버 ID값

    @Column(name = "DESCRIPTION", length = 40)
    private String description;                 //LINE :: 설명

    @Transient
    private String changeCode;

    @Transient
    private CpRelInfoId idClass;
    @Transient
    private List<CpRelInfoId> idList;
}
