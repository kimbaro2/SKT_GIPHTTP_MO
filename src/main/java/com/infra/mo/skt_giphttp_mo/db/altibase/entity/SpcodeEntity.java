package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import javax.persistence.Transient;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.DynamicUpdate;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.Search;

import lombok.Data;

@Entity
@Table(name = "CFG_SPCODE")
@DynamicUpdate
@Data
public class SpcodeEntity extends Search {
    @Id
    @Column(name = "CID", length = 10)
    public  String cid; // LINE :: 문자매니저 연동 CP CID

    @Column(name = "SPCODE", length = 16)
    public  String spcode; // LINE :: 문자매니저 CID(특번) - 4자리 형식 (예: 6381, 6451, 6384)

    @Transient
    public  List<String> idList;

    @Transient
    public  String changeCode;
}
