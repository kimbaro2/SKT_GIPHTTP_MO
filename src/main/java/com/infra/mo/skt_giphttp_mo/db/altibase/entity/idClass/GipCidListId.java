package com.infra.mo.skt_giphttp_mo.db.altibase.entity.idClass;

import java.io.Serializable;
import java.util.Objects;

public class GipCidListId implements Serializable {
    private String moduleId;
    private String parentCid;
    private String childCid;

    public GipCidListId() {}

    public GipCidListId(String moduleId, String parentCid, String childCid) {
        this.moduleId = moduleId;
        this.parentCid = parentCid;
        this.childCid = childCid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GipCidListId)) return false;
        GipCidListId that = (GipCidListId) o;
        return Objects.equals(moduleId, that.moduleId) &&
                Objects.equals(parentCid, that.parentCid) &&
                Objects.equals(childCid, that.childCid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleId, parentCid, childCid);
    }
}