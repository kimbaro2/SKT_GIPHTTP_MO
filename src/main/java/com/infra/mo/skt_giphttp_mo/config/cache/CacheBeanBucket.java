package com.infra.mo.skt_giphttp_mo.config.cache;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.CfgEtcEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.GipHttpAccessEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.SpcodeEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/*TODO 캐시메모리로 활용하기 위한 객체를  DI 합니다.*/
@Configuration
public class CacheBeanBucket {

    @Bean("ApplicationCacheMap")
    public ConcurrentHashMap<String, Object> cacheMap() {
        return new ConcurrentHashMap<String, Object>();
    }

    @Bean("ApplicationCacheQueue")
    public ConcurrentLinkedQueue<Object> cacheQueue() {
        return new ConcurrentLinkedQueue<Object>();
    }

    /*GIPHTTPACCESS 캐시 풀 - CID-IP_PORT 관리*/
    @Bean("GipHttpAccessList")
    public CopyOnWriteArrayList<GipHttpAccessEntity> gipHttpAccessList() {
        return new CopyOnWriteArrayList<GipHttpAccessEntity>();
    }

    /*CFGETC 캐시 풀 - PRO_NM | Entity  관리*/
    @Bean("CfgEtcMap")
    public HashMap<String, CfgEtcEntity> cfgEtcMap() {
        return new HashMap<String, CfgEtcEntity>();
    }

    /*CFG_SPCODE 캐시 풀 - CID:SPCODE | Entity  관리*/
    @Bean("SpcodeMap")
    public ConcurrentHashMap<String, SpcodeEntity> spcodeMap() {
        return new ConcurrentHashMap<String, SpcodeEntity>();
    }
}
