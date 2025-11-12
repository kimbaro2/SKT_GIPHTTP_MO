package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GIENQId implements Serializable {
    private String serverType;                      //LINE :: VSMSS/HSMSS 구분

    private String cid;                             //LINE :: CP를 구별하는 식별자
}
