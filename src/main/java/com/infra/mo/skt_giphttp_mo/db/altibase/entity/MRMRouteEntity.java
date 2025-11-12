package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.Search;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "MRMROUTE")
@DynamicUpdate
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MRMRouteEntity extends Search {
    @Id
    @Column(name = "DESTCID", length = 5)
    private String destCid;                         //LINE :: WEB_TO_PHONE 이 전송되어야 할 사업자 CID
    
    @Column(name = "V_MIN_PREFIX", length = 5)
    private String vminPrefix;                      //LINE :: Virtual Min에 부여되는 Prefix 현재는 “200” 으로 고정
    
    @Column(name = "STARTMIN", columnDefinition = "NUMERIC(5)")
    private long startMin;                           //LINE :: Virtual Min의 시작 번호
    
    @Column(name = "ENDMIN", columnDefinition = "NUMERIC(5)")
    private long endMin;                             //LINE :: Virtual Min의 끝 번호
    
    @Column(name = "TRQNO", columnDefinition = "NUMERIC(7)")
    private long trqNo;                              //LINE :: PCSIF_SEND 에서 WEB_TO_PHONE  MO_TR 을 라우팅할 Queue 번호

    @Column(name = "MOQNO", columnDefinition = "NUMERIC(7)")
    private long moqNo;                              //LINE :: PCSIF_SEND 가 읽어 갈 MO 라우팅 Queue 번호
    
    @Column(name = "VSMSS_NO", columnDefinition = "NUMERIC(3)")
    private long vsmssNo;                            //LINE :: WEB_TO_PHONE 메시지를 전송하는 VSMSS 의 번호
    
    @Column(name = "DESCRIPTION", length = 50)
    private String description;                     //LINE :: 설명

    @Transient
    private String changeCode;

    @Transient private List<String> idList;    //LINE :: 복합키 리스트
}
