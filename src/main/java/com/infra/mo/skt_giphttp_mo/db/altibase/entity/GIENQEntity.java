package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.Search;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.GIENQId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "GIENQ")
@DynamicUpdate
@Data
@IdClass(GIENQId.class)
@AllArgsConstructor
@NoArgsConstructor
public class GIENQEntity extends Search {
    @Id
    @Column(name = "SERVERTYPE", length = 5)
    private String serverType;                      //LINE :: VSMSS/HSMSS 구분

    @Id
    @Column(name = "CID", length = 16)
    private String cid;                             //LINE :: CP를 구별하는 식별자

    @Column(name = "TR_QUEUE_NO", columnDefinition = "NUMERIC(10)")
    private long trQueueNo;                         //LINE :: TR 전송 시 Queue 번호

    @Column(name = "MO_QUEUE_NO", columnDefinition = "NUMERIC(10)")
    private long moQueueNo;                         //LINE :: MO 전송 시 Queue 번호

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "REGI_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    //@CreationTimestamp
    private Date regiDate;                          //LINE :: Config 등록 날짜

    @Column(name = "CP_DESCRIPTION", length = 40)
    private String cpDescription;                   //LINE :: CP 설명

    @Transient
    private String changeCode;

    @Transient
    private GIENQId idClass;             //LINE :: 복합키
    @Transient
    private List<GIENQId> idList;   //LINE :: 복합키 리스트
}
