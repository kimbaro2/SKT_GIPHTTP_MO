package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * CFG_RELAY_CID_LIST 테이블 Entity
 * 
 * 타사 트래픽 중개사 라우팅 설정 정보를 저장하는 테이블
 * HTTP_MOSEND_ACCESS 테이블의 CID, QUEUE_NO 값을 활용하여 조회
 */
@Entity
@Table(name = "CFG_RELAY_CID_LIST", schema = "SMS")
@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CfgRelayCidListEntity {
    
    @Id
    @Column(name = "CID", length = 10, nullable = false)
    public String cid;  // CID (Primary Key)
    
    @Column(name = "RELAY_FLAG_KT", columnDefinition = "NUMERIC(1)")
    public Integer relayFlagKt;  // KT Relay Flag (0: OFF, 1: ON)
    
    @Column(name = "RELAY_FLAG_LGU", columnDefinition = "NUMERIC(1)")
    public Integer relayFlagLgu;  // LGU Relay Flag (0: OFF, 1: ON)
    
    @Column(name = "RELAY_QUEUE", columnDefinition = "NUMERIC(5)")
    public Integer relayQueue;  // Relay Queue 번호
    
    @Column(name = "MO_BILLCID", length = 20)
    public String moBillCid;  // MO Billing CID
    
    @Column(name = "NP_PREFIX", columnDefinition = "NUMERIC(3)")
    public Integer npPrefix;  // NP Prefix
    
    @Column(name = "REGI_DATE")
    public Date regiDate;  // 등록일자
    
    @Column(name = "DESCRIPTION", length = 40, nullable = false)
    public String description;  // 설명
}
