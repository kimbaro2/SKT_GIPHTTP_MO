package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.HistoryEMSDefaultEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.GipAccessHistoryId;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "GIP_ACCESS_HISTORY")
@DynamicUpdate
@Data
@ToString
@IdClass(GipAccessHistoryId.class)
public class GipAccessHistoryEntity extends HistoryEMSDefaultEntity {
    @Id
    @Column(name = "PROC_NAME", length = 10)
    private String procName;

    @Id
    @Column(name = "LOG_NO", length = 4)
    private String logNo;

    @Column(name = "CID", length = 10)
    private String cid;

    @Column(name = "IP_ADDR", length = 15)
    private String ipAddr;

    @Column(name = "PORT_NO", columnDefinition = "NUMERIC(6)")
    private int portNo;

    @Column(name = "QUEUE_NO", columnDefinition = "NUMERIC(6)")
    private int queueNo;

    @Column(name = "FX", columnDefinition = "NUMERIC(6)")
    private int fx;

    @Column(name = "FY", columnDefinition = "NUMERIC(3)")
    private int fy;

    @Column(name = "FLAG", length = 1)
    private String flag;

    @Column(name = "RC", columnDefinition = "NUMERIC(1)")
    private int rc;

    @Column(name = "TC", columnDefinition = "NUMERIC(1)")
    private int tc;

    @Column(name = "FLAG017", length = 1)
    private String flag017;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "UPDATE_FLAG", length = 1)
    private String updateFlag;

    @Column(name = "CP_NAME", length = 30)
    private String cpName;

    @Column(name = "CP_PHONE", length = 30)
    private String cpPhone;

    @Column(name = "SKT_NAME", length = 30)
    private String sktName;

    @Column(name = "SKT_PHONE", length = 30)
    private String sktPhone;

    @Column(name = "URL_FLAG", length = 1)
    private String urlFlag;

    @Column(name = "TID", length = 6)
    private String tid;

    @Column(name = "PASS_FLAG", length = 1)
    private String passFlag;

    @Column(name = "PORTED_FLAG", columnDefinition = "NUMERIC(1)")
    private int portedFlag;

    @Column(name = "POLL_QNO", columnDefinition = "NUMERIC(10)")
    private long pollQno;

    @Column(name = "CP_TEAM", length = 30)
    private String cpTeam;

    @Column(name = "CP_PERSONNEL", length = 30)
    private String cpPersonnel;

    @Column(name = "CP_SKT_TEAM", length = 30)
    private String cpSktTeam;

    @Column(name = "BILL_TYPE", length = 1)
    private String billType;

    @Column(name = "PORTED_AUTCON", length = 1)
    private String portedAutcon;

    @Column(name = "MO_TR_BILL", columnDefinition = "NUMERIC(1)")
    private Integer moTrBill;

    @Column(name = "COIS_TYPE", length = 1)
    private String coisType;

    @Column(name = "REPLY_FLAG", length = 1)
    private String replyFlag;

    @Column(name = "LOG_FLAG", columnDefinition = "SMALLINT")
    private int logFlag;

    @Column(name = "LIMIT_CHECK_FLAG", length = 1)
    private String limitCheckFlag;

    @Column(name = "DETECT_CID_FLAG", length = 2)
    private String detectCidFlag;

    @Column(name = "CB_CHECK_FLAG", length = 1)
    private String cbCheckFlag;

    @Column(name = "GIPVERID", columnDefinition = "NUMERIC(5)")
    private int gipverid;

    @Column(name = "TRUST_FLAG", length = 1)
    private String trustFlag;

    @Column(name = "AUTH_FLAG", length = 1)
    private String authFlag;

    @Column(name = "CHKPERSEC", columnDefinition = "NUMERIC(5)")
    private int chkpersec;

    @Column(name = "DESIRE_NODE", length = 20)
    private String desireNode;

    @Column(name = "CURRENT_NODE", length = 20)
    private String currentNode;

    @Column(name = "RM_FLAG", columnDefinition = "NUMERIC(1)")
    private int rmFlag;

    @Transient
    private String historySort;

    @Transient
    private List<String> sortList;

    public GipAccessHistoryEntity(){}

//    public GipAccessHistoryEntity(GipHttpAccessEntity gipAllAccessEntity){
//        this.procName = "GIPALL";
//        this.logNo = gipAllAccessEntity.getLogNo();
//        this.cid = gipAllAccessEntity.getCid();
//        this.ipAddr = gipAllAccessEntity.getIpAddr();
//        this.portNo = gipAllAccessEntity.getPortNo();
//        this.queueNo = gipAllAccessEntity.getQueueNo();
//        this.fx = gipAllAccessEntity.getFx();
//        this.fy = gipAllAccessEntity.getFy();
//        this.flag = gipAllAccessEntity.getFlag();
//        this.rc = gipAllAccessEntity.getRc();
//        this.tc = gipAllAccessEntity.getTc();
//        this.flag017 = gipAllAccessEntity.getFlag017();
//        this.description = gipAllAccessEntity.getDescription();
//        this.updateFlag = gipAllAccessEntity.getUpdateFlag();
//        this.cpName = gipAllAccessEntity.getCpName();
//        this.cpPhone = gipAllAccessEntity.getCpPhone();
//        this.sktName = gipAllAccessEntity.getSktName();
//        this.sktPhone = gipAllAccessEntity.getSktPhone();
//        this.portedFlag = gipAllAccessEntity.getPortedFlag();
//        this.cpTeam = gipAllAccessEntity.getCpTeam();
//        this.cpPersonnel = gipAllAccessEntity.getCpPersonnel();
//        this.cpSktTeam = gipAllAccessEntity.getCpSktTeam();
//        this.msgType = gipAllAccessEntity.getMsgType();
//        this.billType = gipAllAccessEntity.getBillType();
//        this.portedAutcon = gipAllAccessEntity.getPortedAutcon();
//        this.coisType = gipAllAccessEntity.getCoisType();
//        this.replyFlag = gipAllAccessEntity.getReplyFlag();
//        this.logFlag = gipAllAccessEntity.getLogFlag();
//        this.limitCheckFlag = gipAllAccessEntity.getLimitCheckFlag();
//        this.detectCidFlag = gipAllAccessEntity.getDetectCidFlag();
//        this.cbCheckFlag = gipAllAccessEntity.getCbCheckFlag();
//        this.gipverid = gipAllAccessEntity.getGipverid();
//        this.trustFlag = gipAllAccessEntity.getTrustFlag();
//        this.authFlag = gipAllAccessEntity.getAuthFlag();
//        this.desireNode = gipAllAccessEntity.getDesireNode();
//        this.currentNode = gipAllAccessEntity.getCurrentNode();
//        this.rmFlag = gipAllAccessEntity.getRmFlag();
//        this.changeCode = gipAllAccessEntity.getChangeCode();
//        this.time = new Date();
//    }
}
