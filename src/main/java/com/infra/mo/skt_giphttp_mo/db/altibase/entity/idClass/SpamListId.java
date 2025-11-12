package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpamListId implements Serializable {
    private String min;                 //LINE :: DSMSS로 부터 읽어온 스팸 차단 번호

    private int minType;                //LINE :: 발/회신 구분 1:발신번호 차단 2:회신번호 차단
}
