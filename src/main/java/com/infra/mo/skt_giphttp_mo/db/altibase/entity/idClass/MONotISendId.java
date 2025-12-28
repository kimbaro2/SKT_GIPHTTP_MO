package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MO_NOTISEND 테이블 Composite Primary Key
 * 
 * C 코드 참고: GIDBLib.c
 * - DbReadMO_NOTISEND: LINE 2280 (WHERE MSGID = ? AND DESTCID = ? AND SRCCID = ? AND SERVERTYPE = 'H')
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MONotISendId implements Serializable {
    private String msgId;
    private String srcCId;
    private String destCId;
    private String serverType;
}




