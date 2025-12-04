package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MOCALLINFO 테이블의 복합 키 클래스
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MOCallInfoId implements Serializable {
    private String srcCallNo;
    private String destCId;
    private String msgId;
}

