package com.infra.mo.skt_giphttp_mo.db.altibase.entity;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.TokenCliInfoId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TOKEN_CLI_INFO")
@DynamicUpdate
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(TokenCliInfoId.class)
public class TokenCliInfoEntity {

    @Id
    @Column(name = "CID", length = 16, nullable = false)
    public String cid;

    @Id
    @Column(name = "IP_ADDR", length = 15, nullable = false)
    public String ipAddr;

    @Id
    @Column(name = "PORT_NO", nullable = false)
    public Integer portNo;

    @Column(name = "PUBLIC_KEY", length = 512)
    public String publicKey;

    @Column(name = "PRIVATE_KEY", length = 2048)
    public String privateKey;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date createAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date updateAt;
}