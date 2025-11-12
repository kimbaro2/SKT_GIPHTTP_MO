package com.infra.mo.skt_giphttp_mo.db.altibase.repository;

import com.infra.mo.skt_giphttp_mo.db.altibase.entity.AuthCallBackListEntity;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults.Search;
import com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass.AuthCallBackListId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthCallBackListRepository extends JpaRepository<AuthCallBackListEntity, AuthCallBackListId> {

    AuthCallBackListEntity findAllByCidAndModuleNameAndCallback(String cid, String moduleName, String callback);

    int countAllByCidAndModuleNameAndCallback(String cid, String moduleName, String callback);

    boolean existsByCidAndModuleNameAndCallback(String cid, String moduleName,String callback);
}