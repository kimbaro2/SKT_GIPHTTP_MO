package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.Search;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "MSG_LIMIT_LIST")
@DynamicUpdate
@Data
public class MsgLimitListEntity extends Search {
    @Id
    @Column(name = "MDN", length = 12)
    private String mdn;                 //LINE :: DSMSS로 부터 읽어온 한도 차단 번호

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "REGI_DATE", columnDefinition = "date")
    @Temporal(TemporalType.TIMESTAMP)
    //@CreationTimestamp
    private Date regiDate;              //LINE :: 등록 날짜

    @Transient private String idClass;
    @Transient private List<String> idClassList;
}
