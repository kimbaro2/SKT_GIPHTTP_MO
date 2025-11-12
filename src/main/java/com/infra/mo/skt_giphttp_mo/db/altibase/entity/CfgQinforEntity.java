package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CFG_QINFOR")
@DynamicUpdate
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CfgQinforEntity {
    // PK : cid
    @Id
    @Column(name = "Q_NO", nullable = false)
    public Long qNo;

    @Column(name = "SMSC", nullable = false)
    public Long smsc;

    @Column(name = "QTYPE", nullable = false)
    public Long qtype;

    @Column(name = "QSIZE", nullable = false)
    public Long qsize;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "REGDATE", columnDefinition = "date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date regdate;     // LINE :: 생성 일시

    @Column(name = "DESCRIPTION", length = 2048, nullable = false)
    public String description;   // LINE :: 개인 키
}