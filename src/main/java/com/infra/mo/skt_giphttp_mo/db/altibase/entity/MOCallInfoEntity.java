package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.MOCallInfoId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * MOCALLINFO 테이블 Entity
 * 
 * C 코드 참고: GIDBLib.c LINE 2603-2607 (SelectGIPMOCallInfo)
 * - SELECT MOSUBTIME, MSGLEN, ROAMINGID, CB, ROAMPMN, W_ZONE, TRACE_ID, 
 *   ORIG_MVNO_INFO, DEST_MVNO_INFO, RCS, DCS_TYPE, ORG_MSGLEN, MORECVTIME
 *   FROM MOCALLINFO
 *   WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ?
 */
@Entity
@Table(name = "MOCALLINFO", schema = "SMS")
@IdClass(MOCallInfoId.class)
@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MOCallInfoEntity {
    
    @Id
    @Column(name = "SRCCALLNO", length = 11, nullable = false)
    public String srcCallNo;
    
    @Id
    @Column(name = "DESTCID", length = 16, nullable = false)
    public String destCId;
    
    @Id
    @Column(name = "MSGID", length = 11, nullable = false)
    public String msgId;
    
    @Column(name = "SRCCID", length = 16)
    public String srcCId;
    
    @Column(name = "DESTCALLNO", length = 11)
    public String destCallNo;
    
    @Column(name = "MOSUBTIME", length = 17)
    public String moSubTime;  // MO 제출 시간
    
    @Column(name = "MSGLEN", columnDefinition = "NUMERIC(3)")
    public Integer msgLen;  // 메시지 길이
    
    @Column(name = "ROAMINGID", columnDefinition = "NUMERIC(3)")
    public Integer roamingId;  // 로밍 ID
    
    @Column(name = "CB", length = 32)
    public String cb;  // Callback
    
    @Column(name = "ROAMPMN", length = 9)
    public String roamPMN;  // Roaming PMN
    
    @Column(name = "W_ZONE", length = 1)
    public String wZone;  // W-Zone
    
    @Column(name = "TRACE_ID", length = 21)
    public String traceId;  // Trace ID
    
    @Column(name = "ORIG_MVNO_INFO", length = 26)
    public String origMvnoInfo;  // 원본 MVNO 정보
    
    @Column(name = "DEST_MVNO_INFO", length = 26)
    public String destMvnoInfo;  // 목적지 MVNO 정보
    
    @Column(name = "RCS", length = 15)
    public String rcs;  // RCS 태그
    
    @Column(name = "DCS_TYPE", columnDefinition = "NUMERIC(3)")
    public Integer dcsType;  // DCS Type
    
    @Column(name = "ORG_MSGLEN", columnDefinition = "NUMERIC(3)")
    public Integer orgMsgLen;  // 원본 메시지 길이
    
    @Column(name = "MORECVTIME", length = 17)
    public String moRecvTime;  // MO 수신 시간 (2020 1Q 추가)
}

