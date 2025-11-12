package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "CALLINFO", schema = "SMS")
@IdClass(CallInfoId.class)
@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallInfoEntity {

    @Column(name = "SERVERTYPE", length = 1)
    public String servertype;

    @Id
    @Column(name = "MSGIDCENTER", length = 9, nullable = false)
    public String msgidcenter;

    @Column(name = "MSGIDSERVER", length = 9, nullable = false)
    public String msgidserver;

    @Column(name = "MTSUBMISSIONTIME", columnDefinition = "NUMERIC(11)", nullable = false)
    public Long mtsubmissiontime;

    @Column(name = "MSGLEN", columnDefinition = "NUMERIC(3)")
    public Integer msglen;

    @Column(name = "SRCCID", length = 16, nullable = false)
    public String srccid;

    @Column(name = "SRCCALLNO", length = 11)
    public String srccallno;

    @Column(name = "DESTCID", length = 16, nullable = false)
    public String destcid;

    @Id
    @Column(name = "DESTCALLNO", length = 11, nullable = false)
    public String destcallno;

    @Column(name = "EXPIRETIME", columnDefinition = "NUMERIC(11)", nullable = false)
    public Long expiretime;

    @Column(name = "RECV_QNO", columnDefinition = "NUMERIC(10)")
    public Long recvQno;

    @Column(name = "TID", columnDefinition = "NUMERIC(6)")
    public Integer tid;

    @Column(name = "SEG", columnDefinition = "NUMERIC(3)")
    public Integer seg;

    @Column(name = "CENTERNO", columnDefinition = "NUMERIC(2)")
    public Integer centerno;

    @Column(name = "MSG", length = 300)
    public String msg;

    @Column(name = "CB", length = 32)
    public String cb;

    @Column(name = "ESMCLASS", columnDefinition = "NUMERIC(2)", nullable = false)
    public Integer esmclass = 0;

    @Column(name = "NOTI_FLAG", columnDefinition = "NUMERIC(1)", nullable = false)
    public Integer notiFlag = 0;

    @Column(name = "SRC_TYPE", length = 1)
    public String srcType = "1";

    @Column(name = "ROAMPMN", length = 8)
    public String roampmn;

    @Column(name = "RGTDLVFLAG", columnDefinition = "NUMERIC(2)")
    public Integer rgtdlvflag;

    @Column(name = "TERMTYPE", columnDefinition = "NUMERIC(2)")
    public Integer termtype;

    @Column(name = "VLDPERIOD", columnDefinition = "NUMERIC(10)")
    public Long vldperiod;

    @Column(name = "FWD_SRC", length = 20)
    public String fwdSrc;

    @Column(name = "FWD_SRC2", length = 20)
    public String fwdSrc2;

    @Column(name = "SESSION_ID", columnDefinition = "NUMERIC(3)")
    public Integer sessionId;

    @Column(name = "RD", length = 5)
    public String rd;

    @Column(name = "W_ZONE", length = 1)
    public String wZone = "0";

    @Column(name = "FWD_CNT", columnDefinition = "NUMERIC(3)")
    public Integer fwdCnt = 0;

    @Column(name = "TRACE_ID", length = 20)
    public String traceId;

    @Column(name = "ORIG_MVNO_INFO", length = 26)
    public String origMvnoInfo;

    @Column(name = "DEST_MVNO_INFO", length = 26)
    public String destMvnoInfo;

    @Column(name = "BILL_TYPE", length = 1)
    public String billType;

    @Column(name = "RCS", length = 15)
    public String rcs;

    @Column(name = "DCS_TYPE", columnDefinition = "NUMERIC(3)")
    public Integer dcsType = 14;

    @Column(name = "SPLIT_SEQ", columnDefinition = "NUMERIC(1)")
    public Integer splitSeq = 0;

    @Column(name = "ORG_MSGLEN", columnDefinition = "NUMERIC(3)")
    public Integer orgMsglen = 0;

    @Column(name = "SMS_OSFI", length = 5)
    public String smsOsfi = "0";

    @Column(name = "ORIGCID", length = 9)
    public String origcid;

    @Column(name = "RELAYCID", length = 4)
    public String relaycid;

    @Column(name = "VIRTUAL_NUM", length = 20)
    public String virtualNum = "NULL";
}


