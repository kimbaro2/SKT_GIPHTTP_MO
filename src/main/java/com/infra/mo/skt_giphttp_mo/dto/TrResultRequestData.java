package com.infra.mo.skt_giphttp_mo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

//Layer2 - data 내부 구조
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TrResultRequestData {
	public String srcCID;
	public String srcCallNo;
	public String destCID; // <-
	public String destCallNo; // <-
	public String teleServiceId;
	public String clientRequestId;
	public String termtype;
	public int    dataEncoding;
	public int    validPeriodTime;
	public int    rgtDlvFlg;
	public String callback; // <-
	public String orgCid;
	public String relayCid;
}
