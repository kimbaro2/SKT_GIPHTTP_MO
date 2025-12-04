package com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults;

import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

@Data
public class Search extends Paging {
    @Transient public String searchType;
    @Transient public String searchTxt;
    @Transient public String searchStartDate;
    @Transient public String searchEndDate;

    /** Common Search Param **/
    @Transient public String searchTxt1;
    @Transient public String searchTxt2;
    @Transient public String searchTxt3;
    @Transient public String searchTxt4;
    @Transient public String searchTxt5;
    @Transient public String searchTxt6;

    /** Common Sort Param **/
    @Transient public String sort;
    @Transient public String sort2;
    @Transient public List<String> sortList;
    @Transient public String historySort;

    @Transient public String saveType;

    public String getSearchStartDate(){
        return searchStartDate == null ? null : searchStartDate;
    }

    public String getSearchEndDate(){
        return searchEndDate == null ? null : searchEndDate;
    }
}
