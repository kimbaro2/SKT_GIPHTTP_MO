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
 * 실제 테이블 Primary Key: MSGID, SRCCID, DESTCID
 * C 코드 참고: GIDBLib.c
 * - InsertGIPMOCallInfo: LINE 2228-2231 (INSERT INTO MOCALLINFO)
 * - SelectGIPMOCallInfo: LINE 2607, 2623 (WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ?)
 * - UpdateGIPMOCallInfo: LINE 3458 (WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ?)
 * 
 * 주의: C 코드는 WHERE 조건으로 SRCCALLNO, DESTCID, MSGID를 사용하지만,
 * 실제 테이블의 Primary Key는 MSGID, SRCCID, DESTCID입니다.
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
    @Column(name = "MSGID", length = 10, nullable = false)
    public String msgId;
    
    @Id
    @Column(name = "SRCCID", length = 32, nullable = false)
    public String srcCId;
    
    @Id
    @Column(name = "DESTCID", length = 32, nullable = false)
    public String destCId;
    
    // 일반 컬럼 (WHERE 조건에 사용되지만 PK 아님)
    @Column(name = "SRCCALLNO", length = 11)
    public String srcCallNo;
    
    @Column(name = "DESTCALLNO", length = 11)
    public String destCallNo;
    
    @Column(name = "MOSUBTIME", length = 16)
    public String moSubTime;  // MO 제출 시간
    
    @Column(name = "MSGLEN", columnDefinition = "NUMERIC(3)")
    public Integer msgLen;  // 메시지 길이
    
    @Column(name = "ROAMINGID", columnDefinition = "NUMERIC(38)")
    public Long roamingId;  // 로밍 ID (NUMERIC(38)이므로 Long 타입)
    
    @Column(name = "CB", length = 32)
    public String cb;  // Callback
    
    @Column(name = "ROAMPMN", length = 8)
    public String roamPMN;  // Roaming PMN
    
    @Column(name = "W_ZONE", length = 1)
    public String wZone;  // W-Zone
    
    @Column(name = "TRACE_ID", length = 40)
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
    
    @Column(name = "MORECVTIME", length = 16)
    public String moRecvTime;  // MO 수신 시간 (2020 1Q 추가)
    
    @Column(name = "VIRTUAL_NUM", length = 20)
    public String virtualNum;  // Virtual Number (AI Survey 등에서 사용)
    
    @Column(name = "EXPIRETIME")
    public java.util.Date expireTime;  // 만료 시간
    
    @Column(name = "SEGMENT", columnDefinition = "NUMERIC(10)")
    public Integer segment;  // 세그먼트 정보 (segSeq)
    
    @Column(name = "TID", length = 6)
    public String tid;  // TID
    
    @Column(name = "CENTERNO", columnDefinition = "NUMERIC(2)")
    public Integer centerno;  // Center 번호
    
    @Column(name = "MSG", length = 300)
    public String msg;  // 메시지 내용 (szMsg)
    
    @Column(name = "RETURNQNO", columnDefinition = "NUMERIC(10)")
    public Integer returnQno;  // Return Queue 번호 (ReturnQ_No)
    
    @Column(name = "W2PMSGID", length = 10)
    public String w2pMsgId;  // W2P MSGID (Relay MO 전용)
    
    @Column(name = "FWD_SRC", length = 20)
    public String fwdSrc;  // 전달 소스 (szFWD_NO)
    
    @Column(name = "NPDB_QUERY_CNT", columnDefinition = "NUMERIC(10)")
    public Integer npdbQueryCnt;  // NPDB 조회 횟수
    
    @Column(name = "ESMCLASS", columnDefinition = "NUMERIC(3)")
    public Integer esmClass;  // ESM Class (nRsv4Protocol[11])
    
    @Column(name = "SRC_TYPE", length = 1)
    public String srcType;  // SRC Type (default '1')
    
    // DB 테이블에 없을 수 있는 필드들 (C 모듈에서 저장하지 않음) - @Transient로 표시
    @Transient
    public Integer notiFlag;  // NOTI 타입 여부 (0: 일반, 1: NOTI)
    
    @Transient
    public Integer splitSeq;  // 분할 메시지 시퀀스 (segSeq)
    
    @Transient
    public Integer splitMsglen;  // 분할 메시지 총 개수 (totalSeg)
}

