package com.infra.mo.skt_giphttp_mo.db.altibase.entity.defaults;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Paging {
    private int pageSize;       // LINE :: 게시 글 수
    private int firstPageNo;    // LINE :: 첫 번째 페이지 번호
    private int prevPageNo;     // LINE :: 이전 페이지 번호
    private int startPageNo;    // LINE :: 시작 페이지 (페이징 네비 기준)
    private int pageNo;         // LINE :: 페이지 번호
    private int endPageNo;      // LINE :: 끝 페이지 (페이징 네비 기준)
    private int nextPageNo;     // LINE :: 다음 페이지 번호
    private int finalPageNo;    // LINE :: 마지막 페이지 번호
    private int totalCount;     // LINE :: 게시 글 전체 수
    private int mysqlPageNo;    // LINE :: Mysql And MariaDB 전용 페이지 번호
    private int altibasePageNo; // LINE :: Altibase 전용 페이지 번호
    private int pagingNavSize;  // LINE :: 페이징 네비 수

    /**
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        this.makePaging();
    }

    /**
     * 페이징 생성
     */
    private void makePaging() {
//        if (this.totalCount == 0) return; // 게시 글 전체 수가 없는 경우
//        if (this.pageNo == 0);this.setPageNo(1); // 기본 값 설정
//        if (this.pageSize == 0)     this.setPageSize(20); // 기본 값 설정
//        if (this.pagingNavSize == 0) this.setPagingNavSize(10); // 기본값 설정
        if (this.totalCount == 0) return; // 게시 글 전체 수가 없는 경우
        if (this.pageNo == 0) pageNo = 1; // 기본 값 설정
        if (this.pageSize == 0) pageSize = 20; // 기본 값 설정
        if (this.pagingNavSize == 0) pagingNavSize = 10; // 기본값 설정

        int finalPage = (this.totalCount + (this.pageSize - 1)) / this.pageSize; // 마지막 페이지
        if (this.pageNo > finalPage) this.setPageNo(finalPage); // 기본 값 설정

        if (this.pageNo < 0 || this.pageNo > finalPage) this.pageNo = 1; // 현재 페이지 유효성 체크

        boolean isNowFirst = pageNo == 1 ? true : false; // 시작 페이지 (전체)
        boolean isNowFinal = pageNo == finalPage ? true : false; // 마지막 페이지 (전체)

        int startPage = ((pageNo - 1) / pagingNavSize) * pagingNavSize + 1; // 시작 페이지 (페이징 네비 기준)
        int endPage = startPage + pagingNavSize - 1; // 끝 페이지 (페이징 네비 기준)

        if (endPage > finalPage) { // [마지막 페이지 (페이징 네비 기준) > 마지막 페이지] 보다 큰 경우
            endPage = finalPage;
        }

        this.setFirstPageNo(1); // 첫 번째 페이지 번호

        if (isNowFirst) {
            this.setPrevPageNo(1); // 이전 페이지 번호
        } else {
            this.setPrevPageNo(((pageNo - 1) < 1 ? 1 : (pageNo - 1))); // 이전 페이지 번호
        }

        this.setStartPageNo(startPage); // 시작 페이지 (페이징 네비 기준)
        this.setEndPageNo(endPage); // 끝 페이지 (페이징 네비 기준)

        if (isNowFinal) {
            this.setNextPageNo(finalPage); // 다음 페이지 번호
        } else {
            this.setNextPageNo(((pageNo + 1) > finalPage ? finalPage : (pageNo + 1))); // 다음 페이지 번호
        }

        this.setFinalPageNo(finalPage); // 마지막 페이지 번호

        this.mysqlPageNo = (this.pageNo - 1) * this.pageSize;

        this.altibasePageNo = this.mysqlPageNo + 1;
    }
}
