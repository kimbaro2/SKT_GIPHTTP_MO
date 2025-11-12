package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Table(name = "CFG_ETC")
@DynamicUpdate
@Data
public class CfgEtcEntity {
    @Id
    @Column(name = "PRO_NM", length = 20)
    public String proNm;                       //LINE :: 프로세스 이름

    @Column(name = "P_VALUE", columnDefinition = "NUMERIC(5)")
    public int pvalue;                         //LINE :: 설정 값

    @Column(name = "DESCRIPTION", length = 60)
    public String description;                 //LINE :: 설명
}
