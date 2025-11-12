package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthCallBackListId implements Serializable {
    private String cid;                         //LINE :: 안심마크 표시를 위한 CP CID

    private String moduleName;                  //LINE :: GIPALL/GIPM/GIPPCS/GIPURL/GIPEVENT 구분

    private String callback;                    //LINE :: 안심마크 표시를 위한 회신번호
}
