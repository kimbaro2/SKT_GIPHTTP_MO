package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.MONotISendId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * MO_NOTISEND 테이블 Entity
 * 
 * C 코드 참고: GIDBLib.c
 * - InsertMO_NOTISEND: LINE 4054-4070 (INSERT INTO MO_NOTISEND)
 * - UpdateMO_NOTISEND: LINE 4418-4425 (INSERT INTO ... SELECT FROM MO_NOTISEND)
 * - DbReadMO_NOTISEND: LINE 2274-2280 (SELECT FROM MO_NOTISEND WHERE MSGID = ? AND DESTCID = ? AND SRCCID = ? AND SERVERTYPE = 'H')
 * 
 * Primary Key: MSGID, SRCCID, DESTCID, SERVERTYPE (추정)
 */
@Entity
@Table(name = "MO_NOTISEND", schema = "SMS")
@IdClass(MONotISendId.class)
@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MONotISendEntity {
    
    @Id
    @Column(name = "MSGID", length = 10, nullable = false)
    public String msgId;
    
    @Id
    @Column(name = "SRCCID", length = 32, nullable = false)
    public String srcCId;
    
    @Id
    @Column(name = "DESTCID", length = 32, nullable = false)
    public String destCId;
    
    @Id
    @Column(name = "SERVERTYPE", length = 1, nullable = false)
    public String serverType;  // 'V' 또는 'H'
    
    @Column(name = "NODE", length = 20)
    public String node;
    
    @Column(name = "MOSUBTIME")
    @Temporal(TemporalType.TIMESTAMP)
    public Date moSubTime;  // MO 제출 시간
    
    @Column(name = "SRCCALLNO", length = 11)
    public String srcCallNo;
    
    @Column(name = "DESTCALLNO", length = 11)
    public String destCallNo;
    
    @Column(name = "EXPIRETIME")
    @Temporal(TemporalType.TIMESTAMP)
    public Date expireTime;  // 만료 시간
    
    @Column(name = "SEGMENT", columnDefinition = "NUMERIC(10)")
    public Integer segment;  // 세그먼트 정보
    
    @Column(name = "TID", length = 6)
    public String tid;  // TID
    
    @Column(name = "CB", length = 32)
    public String cb;  // Callback
    
    @Column(name = "ESMCLASS", columnDefinition = "NUMERIC(3)")
    public Integer esmClass;  // ESM Class
    
    @Column(name = "W_ZONE", length = 1)
    public String wZone;  // W-Zone
    
    @Column(name = "TRACE_ID", length = 40)
    public String traceId;  // Trace ID
    
    @Column(name = "ORIG_MVNO_INFO", length = 26)
    public String origMvnoInfo;  // 원본 MVNO 정보
    
    @Column(name = "DEST_MVNO_INFO", length = 26)
    public String destMvnoInfo;  // 목적지 MVNO 정보
    
    @Column(name = "MSGLEN", columnDefinition = "NUMERIC(3)")
    public Integer msgLen;  // 메시지 길이
    
    @Column(name = "DCS_TYPE", columnDefinition = "NUMERIC(3)")
    public Integer dcsType;  // DCS Type
    
    @Column(name = "ORG_MSGLEN", columnDefinition = "NUMERIC(3)")
    public Integer orgMsgLen;  // 원본 메시지 길이
    
    @Column(name = "MORECVTIME", length = 16)
    public String moRecvTime;  // MO 수신 시간 (2020 1Q 추가)
}

