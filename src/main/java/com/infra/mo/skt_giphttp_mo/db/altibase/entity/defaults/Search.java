package com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults;

import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

@Data
public class Search extends Paging {
    @Transient private String searchType;
    @Transient private String searchTxt;
    @Transient private String searchStartDate;
    @Transient private String searchEndDate;

    /** Common Search Param **/
    @Transient private String searchTxt1;
    @Transient private String searchTxt2;
    @Transient private String searchTxt3;
    @Transient private String searchTxt4;
    @Transient private String searchTxt5;
    @Transient private String searchTxt6;

    /** Common Sort Param **/
    @Transient private String sort;
    @Transient private String sort2;
    @Transient private List<String> sortList;
    @Transient private String historySort;

    @Transient private String saveType;

    public String getSearchStartDate(){
        return searchStartDate == null ? null : searchStartDate;
    }

    public String getSearchEndDate(){
        return searchEndDate == null ? null : searchEndDate;
    }
}
