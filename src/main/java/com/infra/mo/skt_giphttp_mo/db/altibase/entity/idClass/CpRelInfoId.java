package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.Data;

import java.io.Serializable;

@Data
public class CpRelInfoId implements Serializable {
    private String pcid;        //LINE :: 인증용 CID로서 연결 세션 연동 용

    private String ccid;        //LINE :: GIP메시지를 전송할때 메시지 내에 발신 CID로 가능한 CID
}
