 package com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Data
public class GSMSDefaultEntity {
    @Column(name = "BILLID", length = 10)
    protected String billId;                                          //LINE :: 사업자 번호

    @Column(name = "ID", length = 20)
    protected String id;                                          //LINE :: Connection ID

    @Column(name = "PASSWORD", length = 20)
    protected String password;                                            //LINE :: Connection Password

    @Column(name = "IP_ADDR", length = 30)
    protected String ipAddr;                                          //LINE :: Client IP 주소

    @Column(name = "PORT_NO", columnDefinition = "NUMERIC(7)")
    protected int portNo;                                         //LINE :: Listening Port 번호

    @Column(name = "QUEUE_NO", columnDefinition = "NUMERIC(7)")
    protected int queueNo;                                            //LINE :: 라우팅 Queue 번호

    @Column(name = "FLOWCTL", columnDefinition = "NUMERIC(5)")
    protected int flowCtl;                                            //LINE :: MT 호 수신 제한을 위한 메시지 건수

    @Column(name = "FLOWTM", columnDefinition = "NUMERIC(3)")
    protected int flowTm;                                         //LINE :: MT 호 수신 제한을 위한 제한 시간 (초)

    @Column(name = "RCNT", columnDefinition = "NUMERIC(3)")
    protected int rcnt;                                           //LINE :: Sync 모드일 경우 TR 전송 실패시 재시도 횟수

    @Column(name = "TM_OUT", columnDefinition = "NUMERIC(7)")
    protected int tmOut;                                          //LINE :: TR Ack 수신 대기 시간 (초)

    @Column(name = "LOG_FLAG", columnDefinition = "NUMERIC(1)")
    protected int logFlag;                                            //LINE :: Log Level (0:CRITICAL,ERROR,WARNING,REPAIR, 1:NORMAL, 2:DEBUG,SYS,CONFIG,  3:MSG_DEBUG,TCP_DATA)

    @Column(name = "STAT_CID", length = 32)
    protected String statCid;                                         //LINE :: 통계 생성용 CID

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REGDATE", columnDefinition = "date")
    protected Date regDate;                                           //LINE :: Config 최종 수정일

    @Column(name = "DESCRIPTION", length = 50)
    protected String description;                                         //LINE :: 설명

    @Column(name = "ROAMINGID", columnDefinition = "NUMERIC(7)")
    protected int roamingId;                                          //LINE :: Roaming ID (미사용)

    @Column(name = "UCS_FLAG", columnDefinition = "NUMERIC(1)")
    protected int ucsFlag;                                            //LINE :: UCS2 수신 여부 0:OFF, 1:ON default 0

    @Column(name = "CONCAT_FLAG", columnDefinition = "NUMERIC(1)")
    protected int concatFlag;                                         //LINE :: Concatenate Flag 정보를 받았을 경우 SMSC로 전달 할지 여부 default 0

    @Column(name = "STATUS", columnDefinition = "NUMERIC(1)")
    protected int status;                                         //LINE :: 연동 상태 default 0

    @Column(name = "DESIRE_NODE", length = 20)
    protected String desireNode;                                  //LINE :: 지정한 VM

    @Column(name = "CURRENT_NODE", length = 20)
    protected String currentNode;                                 //LINE :: 연동된 VM

    @Transient
    protected String changeCode;
}
