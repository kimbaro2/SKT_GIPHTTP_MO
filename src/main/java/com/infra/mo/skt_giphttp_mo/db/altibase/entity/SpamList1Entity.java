package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.SpamListId;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "SPAM_LIST1")
@DynamicUpdate
@Data
@IdClass(SpamListId.class)
public class SpamList1Entity extends SpamListId {
    @Id
    @Column(name = "MIN", length = 32)
    private String min;                 //LINE :: DSMSS로 부터 읽어온 스팸 차단 번호

    @Id
    @Column(name = "MIN_TYPE", columnDefinition = "NUMERIC(2) default 1")
    private int minType;                //LINE :: 발/회신 구분 1:발신번호 차단 2:회신번호 차단

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "REGI_DATE", columnDefinition = "date")
    @Temporal(TemporalType.TIMESTAMP)
    //@CreationTimestamp
    private Date regiDate;              //LINE :: 등록 날짜

    @Transient private SpamListId idClass;
    @Transient private List<SpamListId> idClassList;
}
