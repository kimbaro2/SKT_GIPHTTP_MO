package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallInfoId implements Serializable {
    private String msgidcenter;   // MSGIDCENTER (VARCHAR(9))
    private String destcallno;    // DESTCALLNO (VARCHAR(11))
}


