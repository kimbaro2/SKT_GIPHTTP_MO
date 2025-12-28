package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MOCALLINFO 테이블의 복합 키 클래스
 * 
 * 실제 테이블 Primary Key: MSGID, SRCCID, DESTCID
 * C 코드 참고: GIDBLib.c LINE 2228 (INSERT INTO MOCALLINFO)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MOCallInfoId implements Serializable {
    private String msgId;      // MSGID (첫 번째 PK)
    private String srcCId;     // SRCCID (두 번째 PK)
    private String destCId;    // DESTCID (세 번째 PK)
}

