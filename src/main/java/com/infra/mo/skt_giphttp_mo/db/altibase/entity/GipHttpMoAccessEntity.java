package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.GipDefaultEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "HTTP_MOSEND_ACCESS")
@DynamicUpdate
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GipHttpMoAccessEntity extends GipDefaultEntity {
    // TRUST_FLAG 제거 - 주석처리
    // @Column(name = "TRUST_FLAG", length = 1)
    // private String trustFlag;                                       //LINE :: 인증용 CID N:인증 CID정보 미참조, Y:인증 CID정보 참조 default N

    // PORTED_AUTCON 제거 - 주석처리
    // @Column(name = "PORTED_AUTCON", length = 1)
    // private Integer portedAutcon;                                    //LINE :: PORTED OUT MT로 자동 변환 기능 0:OFF, 1:ON default 0


    @Column(name = "AES_KEY_BASE64", columnDefinition = "VARCHAR(2048)")
    public String aesKeyBase64;                                             //평문 암호화 키 (Base64 인코딩 문자열)

    @Column(name = "IV_BASE64", columnDefinition = "VARCHAR(2048)")
    public String ivBase64;                                             //평문 암호화 키 (Base64 인코딩 문자열)

    @Column(name = "CP_URL", columnDefinition = "VARCHAR(200) not null")
    public String cpUrl;                                             //평문 암호화 키 (Base64 인코딩 문자열)

    @Column(name = "MO_TR_BILL", columnDefinition = "NUMERIC(1) default 0")
    public Integer moTrBill;                                     //LINE :: 회신번호 변경 알림 서비스 0:미허용, 1:허용 default 0

    public GipHttpMoAccessEntity(GipHttpMoAccessEntity entity) {
        this.logNo = entity.getLogNo();
        this.cid = entity.getCid();
        this.ipAddr = entity.getIpAddr();
        this.portNo = entity.getPortNo();
        this.queueNo = entity.getQueueNo();
        this.fx = entity.getFx();
        this.fy = entity.getFy();
        this.flag = entity.getFlag();
        this.rc = entity.getRc();
        this.tc = entity.getTc();
        // this.flag017 = entity.getFlag017();  // 제거: FLAG017
        this.description = entity.getDescription();
        this.updateFlag = entity.getUpdateFlag();
        this.cpName = entity.getCpName();
        this.cpPhone = entity.getCpPhone();
        this.sktName = entity.getSktName();
        this.sktPhone = entity.getSktPhone();
        // this.portedFlag = entity.getPortedFlag();  // 제거: PORTED_FLAG
        this.cpTeam = entity.getCpTeam();
        this.cpPersonnel = entity.getCpPersonnel();
        this.cpSktTeam = entity.getCpSktTeam();
        this.billType = entity.getBillType();
        // this.portedAutcon = entity.getPortedAutcon();  // 제거: PORTED_AUTCON
        // this.coisType = entity.getCoisType();  // 제거: COIS_TYPE
        // this.replyFlag = entity.getReplyFlag();  // 제거: REPLY_FLAG
        this.logFlag = entity.getLogFlag();
        this.limitCheckFlag = entity.getLimitCheckFlag();
        // this.detectCidFlag = entity.getDetectCidFlag();  // 제거: DETECT_CID_FLAG
        // this.cbCheckFlag = entity.getCbCheckFlag();  // 제거: CB_CHECK_FLAG
        this.gipverid = entity.getGipverid();
        // this.trustFlag = entity.getTrustFlag();  // 제거: TRUST_FLAG
        // this.authFlag = entity.getAuthFlag();  // 제거: AUTH_FLAG
        this.desireNode = entity.getDesireNode();
        this.currentNode = entity.getCurrentNode();
        this.rmFlag = entity.getRmFlag();
        // this.pcsFlag = entity.getPcsFlag();  // 제거: PCS_FLAG
        this.aesKeyBase64 = entity.getAesKeyBase64();
        this.ivBase64 = entity.getIvBase64();
        this.cpUrl = entity.getCpUrl();
        this.moTrBill = entity.getMoTrBill();
    }

    public String toString() {
        return super.toString();
        // ", trustFlag = " + trustFlag;  // 제거: TRUST_FLAG
    }


}
