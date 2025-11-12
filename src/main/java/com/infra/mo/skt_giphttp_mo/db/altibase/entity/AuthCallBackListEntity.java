package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.Search;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.AuthCallBackListId;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "CFG_AUTH_CALLBACKLIST")
@DynamicUpdate
@Data
@IdClass(AuthCallBackListId.class)
public class AuthCallBackListEntity extends Search {
    @Id
    @Column(name = "CID", length = 10)
    private String cid;                         //LINE :: 안심마크 표시를 위한 CP CID

    @Id
    @Column(name = "MODULE_NAME", length = 10)
    private String moduleName;                  //LINE :: GIPALL/GIPM/GIPPCS/GIPURL/GIPEVENT 구분

    @Id
    @Column(name = "CALLBACK", length = 21)
    private String callback;                    //LINE :: 안심마크 표시를 위한 회신번호

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "REGI_DATE", columnDefinition = "date")
    @Temporal(TemporalType.TIMESTAMP)
    //@CreationTimestamp
    private Date regiDate;                      //LINE :: 최초 등록날짜

    @Column(name = "DESCRIPTION", length = 100)
    private String description;                 //LINE :: 문자매니저 CID(특번)

    @Transient private List<AuthCallBackListId> idList;

    @Transient
    private String changeCode;
}
