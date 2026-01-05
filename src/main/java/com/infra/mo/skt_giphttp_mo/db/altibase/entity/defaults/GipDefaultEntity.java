package com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults;

import lombok.Data;

import javax.persistence.*;

@MappedSuperclass
@Data
public class GipDefaultEntity {
    @Id
    @Column(name = "LOG_NO", length = 4)
    public String logNo;                                           //LINE :: LOG 번호

    @Column(name = "CID", length = 10)
    public String cid;                                             //LINE :: CP CID

    @Column(name = "IP_ADDR", length = 15)
    public String ipAddr;                                          //LINE :: CP Client IP 주소

    @Column(name = "PORT_NO", columnDefinition = "NUMERIC(6)")
    public int portNo;                                             //LINE :: Listening Port 번호

    @Column(name = "QUEUE_NO", columnDefinition = "NUMERIC(6)")
    public int queueNo;                                            //LINE :: 라우팅 Queue 번호

    @Column(name = "FX", columnDefinition = "NUMERIC(6)")
    public int fx;                                                 //LINE :: 메시지 전송제한을 위한 제한시간(초)

    @Column(name = "FY", columnDefinition = "NUMERIC(3)")
    public int fy;                                                 //LINE :: Flow Time 내에 CP가 전송 가능한 최대 메시지 수

    @Column(name = "FLAG", length = 1)
    public String flag;                                            //LINE :: 전송모드 0:동기, 1:비동기

    @Column(name = "RC", columnDefinition = "NUMERIC(1)")
    public int rc;                                                 //LINE :: MO 및 Receipt 메시지 Rerty 횟수

    @Column(name = "TC", columnDefinition = "NUMERIC(1)")
    public int tc;                                                 //LINE :: Timeout시 몇 초 동안 기다릴 건지 나타내는 Flag

    // FLAG017 제거 - 주석처리
    // @Column(name = "FLAG017", length = 1)
    // public String flag017;                                         //LINE :: 017 수신 여부 0:미수신 1:수신

    @Column(name = "DESCRIPTION", length = 200)
    public String description;                                     //LINE :: CP 서비스 설명

    @Column(name = "UPDATE_FLAG", length = 1)
    public String updateFlag;                                      //LINE :: 연동정보 등록, 수정, 삭제 여부 A:Add, U:Update, D:Delete

    @Column(name = "CP_NAME", length = 30)
    public String cpName;                                          //LINE :: CP 명

    @Column(name = "CP_PHONE", length = 30)
    public String cpPhone;                                         //LINE :: CP의 담당자 연락처

    @Column(name = "SKT_NAME", length = 30)
    public String sktName;                                         //LINE :: CP의 SKT 담당자명

    @Column(name = "SKT_PHONE", length = 30)
    public String sktPhone;                                        //LINE :: CP의 SKT 담당자 연락처

    // PORTED_FLAG 제거 - 주석처리
    // @Column(name = "PORTED_FLAG", columnDefinition = "NUMERIC(1)")
    // public int portedFlag;                                         //LINE :: CP로 PORTED 정보 전송 여부 0:미전송, 1:전송 default 0

    @Column(name = "CP_TEAM", length = 30)
    public String cpTeam;                                          //LINE :: CP의 서비스 주관 팀

    @Column(name = "CP_PERSONNEL", length = 30)
    public String cpPersonnel;                                     //LINE :: CP의 담당자명

    @Column(name = "CP_SKT_TEAM", length = 30)
    public String cpSktTeam;                                       //LINE :: CP의 SKT 담당팀

    @Column(name = "MSG_TYPE", length = 1)
    public String msgType;                                         //LINE :: Msg 호 처리 유형 1:MO, 2:MT, 3:선물 default 0, 4:MO-TR, 5:MT-TR

    @Column(name = "BILL_TYPE", length = 1)
    public String billType;                                        //LINE :: 과금 Format 유형 1:비과금, 2:발신자 과금, 3:착신자 과금, 4:선물 과금, 5:비과금 정산용 default 0

    // COIS_TYPE 제거 - 주석처리
    // @Column(name = "COIS_TYPE", length = 1)
    // public String coisType;                                        //LINE :: COIS 정보 검색 N:비검색(일반형), Y:검색(가입형) default N

    // REPLY_FLAG 제거 - 주석처리
    // @Column(name = "REPLY_FLAG", length = 1)
    // public String replyFlag;                                       //LINE :: TR 수신 여부 설정 N:0 TR미수신 , Y:CP로 부터 수신한 값 default Y

    @Column(name = "LOG_FLAG", columnDefinition = "smallint")
    public int logFlag;                                            //LINE :: LOG Level(0:CRITICAL,ERROR,WARNING,REPAIR, 1:NORMAL, 2:DEBUG,SYS,CONFIG, 3:MSG_DEBUG,TCP_DATA)

    @Column(name = "LIMIT_CHECK_FLAG", length = 1)
    public String limitCheckFlag;                                  //LINE :: 한도차단 체크 여부 N:비체크, Y:체크 default N

    // DETECT_CID_FLAG 제거 - 주석처리
    // @Column(name = "DETECT_CID_FLAG", length = 2)
    // public String detectCidFlag;                                   //LINE :: 착신전환 차단 여부 N:비체크, Y:체크 default

    // CB_CHECK_FLAG 제거 - 주석처리
    // @Column(name = "CB_CHECK_FLAG", length = 1)
    // public String cbCheckFlag;                                     //LINE :: 회신번호 변경 알림 서비스 N:미허용, Y:허용 default N

    @Column(name = "GIPVERID", columnDefinition = "NUMERIC(5)")
    public int gipverid;                                           //LINE :: GIP Version default 462

    // AUTH_FLAG 제거 - 주석처리
    // @Column(name = "AUTH_FLAG", length = 1)
    // public String authFlag;                                        //LINE :: 안심마크 표시 N:미표시, Y:표시 default N

    @Column(name = "DESIRE_NODE", length = 20)
    public String desireNode;                                      //LINE :: 지정한 연동 VM

    @Column(name = "CURRENT_NODE", length = 20)
    public String currentNode;                                     //LINE :: 현재 연동된 VM

    @Column(name = "RM_FLAG", columnDefinition = "NUMERIC(1)")
    public int rmFlag;                                             //RM 기능 Flag (0:OFF, 1:BLOCK 처리, 2:Priority=1 로 설정, 3:BACKUP SMSC로 전송)

    // PCS_FLAG 제거 - 주석처리
    // @Column(name = "PCS_FLAG", columnDefinition = "NUMERIC(1)")
    // public int pcsFlag;                                             //PCS FLAG 기능 Flag (0:OFF, 1:ON,  ON인 경우 타사 전송 허용)

    @Transient
    private String tableName;

    @Transient
    private String gubun;

    @Transient
    private String beforeLogNo;

    @Transient
    private String beforeIpAddr;

    @Transient
    private int beforePortNo;

    @Transient
    private String changeCode;

    @Transient
    private String procName;

    public String toString() {
        return "logNo = " + logNo +
                ", cid = " + cid +
                ", ipAddr = " + ipAddr +
                ", portNo = " + portNo +
                ", queueNo = " + queueNo +
                ", fx = " + fx +
                ", fy = " + fy +
                ", flag = " + flag +
                ", rc = " + rc +
                ", tc = " + tc +
                // ", flag017 = " + flag017 +  // 제거: FLAG017
                ", description = " + description +
                ", updateFlag = " + updateFlag +
                ", cpName = " + cpName +
                ", cpPhone = " + cpPhone +
                ", sktName = " + sktName +
                ", sktPhone = " + sktPhone +
                // ", portedFlag = " + portedFlag +  // 제거: PORTED_FLAG
                ", cpTeam = " + cpTeam +
                ", cpPersonnel = " + cpPersonnel +
                ", cpSktTeam = " + cpSktTeam +
                ", msgType = " + msgType +
                ", billType = " + billType +
                // ", coisType = " + coisType +  // 제거: COIS_TYPE
                // ", replyFlag = " + replyFlag +  // 제거: REPLY_FLAG
                ", logFlag = " + logFlag +
                ", limitCheckFlag = " + limitCheckFlag +
                // ", detectCidFlag = " + detectCidFlag +  // 제거: DETECT_CID_FLAG
                // ", cbCheckFlag = " + cbCheckFlag +  // 제거: CB_CHECK_FLAG
                ", gipverid = " + gipverid +
                // ", authFlag = " + authFlag +  // 제거: AUTH_FLAG
                ", desireNode = " + desireNode +
                ", currentNode = " + currentNode +
                ", rmFlag = " + rmFlag;
    }
}
