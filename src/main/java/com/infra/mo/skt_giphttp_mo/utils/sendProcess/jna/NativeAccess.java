package com.infra.mo.skt_giphttp_mo.utils.sendProcess.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class NativeAccess {

   public interface CLibrary extends Library {

      String RCS_HOME = System.getenv("RCS_HOME") + File.separator + "src" + File.separator + "lib" + File.separator+ "libRCSQ_java.so";
      String RCS_LIB = System.getenv("RCS_HOME") + File.separator + "src" + File.separator + "lib" + File.separator+ "mysqllib.so";
      String MYSQL_SO_FILE = System.getenv("MYSQL_LIB") + File.separator + System.getenv("MYSQL_SO_FILE");

      CLibrary INSTANCE = (CLibrary) Native.load(RCS_HOME, CLibrary.class);
      CLibrary INSTANCE2 = (CLibrary) Native.load(RCS_LIB, CLibrary.class);
      CLibrary INSTANCE3 = (CLibrary) Native.load(MYSQL_SO_FILE, CLibrary.class);

      public int InitQInfo_java(resultMsg ptrResultMsg);

      public int GetAMsgFromRcsQ_java(int nQNo, QueueField ptrQItem, resultMsg ptrResultMsg);

      public int InsertIntoRcsQWithQNo_java(QueueField ptrQItem, int QNo, resultMsg ptrResultMsg);

      class QueueField extends Structure {

         public byte[] szInServerId = new byte[2 + 1];
         public byte[] szMcpCode = new byte[7 + 1];
         public byte[] szCompanyId = new byte[20 + 1];
         public byte[] szBrandId = new byte[20 + 1];
         public byte[] szSrcCId = new byte[20 + 1];
         public byte[] szSrcMinNo = new byte[20 + 1];
         public byte[] szDestCId = new byte[7 + 1];
         public byte[] szDestMinNo = new byte[20 + 1];
         public byte[] szSafenCId = new byte[20+1];
         public byte[] szSafenMinNo = new byte[20+1];
         public byte[] szPostNrn = new byte[24 + 1];
         public byte[] szNatCd = new byte[10 + 1];
         public int nReturnQ;
         public int nMsgLen;
         public byte[] szMsgCode = new byte[2];
         public byte[] szMsgSubCode = new byte[2];
         public byte[] cSvcType = new byte[1];
         public byte[] cNextMaapFlag = new byte[1];
         public byte[] szCpMsgId = new byte[40 + 1];
         public byte[] szSerialNo = new byte[40 + 1];
         public byte[] szCpCreateTime = new byte[14 + 1];
         public byte[] szTelSendTime = new byte[14 + 1];
         public byte[] szDeliveryTime = new byte[14 + 1];
         public byte[] szReportRecvTime = new byte[14  + 1];
         public byte[] szErrCode = new byte[5 + 1];
         public byte[] szErrCodeMaap = new byte[6 + 1];
         public byte[] szErrCodeFallback = new byte[10 + 1];
         public byte[] szBillCode = new byte[20 + 1];
         public byte[] szBillDvCode = new byte[20 + 1];
         public byte[] szGroupId = new byte[20 + 1];
         public byte[] cMsgType = new byte[1];
         public int nFallBackFlag;
         public byte[] szMessageBaseId = new byte[40 + 1];
         public int nExpiryOption;
         public int nHeader;
         public byte[] szFooter = new byte[64];
         public byte[] szFallbackType = new byte[24 + 1];
//         public byte[] szBtnType = new byte[250];
//         public byte[] szBtnName = new byte[650];
//         public byte[] szBtnValue = new byte[2000];
//         public byte[] szRcsBody = new byte[8000];
//         public byte[] szFallbackTitle = new byte[200];
//         public byte[] szFallbackMsg = new byte[200];
//         public int nFallbackContentCnt;
//         public byte[] szFallbackContentMimeType = new byte[128];
//         public byte[] szFallbackContentPath = new byte[1024];
//         public byte[] szRcsContentFileId = new byte[400];
         //public int nRcsContentCnt;
         //public byte[] szRcsContentPath = new byte[1024];
         //public byte[] szRcsContentSize = new byte[40];
         
         public QueueField(){
            Arrays.fill(szInServerId, (byte) 0x00);
            Arrays.fill(szMcpCode, (byte) 0x00);
            Arrays.fill(szCompanyId, (byte) 0x00);
            Arrays.fill(szBrandId, (byte) 0x00);
            Arrays.fill(szSrcCId, (byte) 0x00);
            Arrays.fill(szSrcMinNo, (byte) 0x00);
            Arrays.fill(szDestCId, (byte) 0x00);
            Arrays.fill(szDestMinNo, (byte) 0x00);
            Arrays.fill(szNatCd, (byte) 0x00);
            Arrays.fill(szMsgCode, (byte) 0x00);
            Arrays.fill(szMsgSubCode, (byte) 0x00);
            Arrays.fill(cSvcType, (byte) 0x00);
            Arrays.fill(cNextMaapFlag, (byte) 0x00);
            Arrays.fill(szCpMsgId, (byte) 0x00);
            Arrays.fill(szSerialNo, (byte) 0x00);
            Arrays.fill(szCpCreateTime, (byte) 0x00);
            Arrays.fill(szTelSendTime, (byte) 0x00);
            Arrays.fill(szDeliveryTime, (byte) 0x00);
            Arrays.fill(szReportRecvTime, (byte) 0x00);
            Arrays.fill(szErrCode, (byte) 0x00);
            Arrays.fill(szErrCodeMaap, (byte) 0x00);
            Arrays.fill(szErrCodeFallback, (byte) 0x00);
            Arrays.fill(szBillCode, (byte) 0x00);
            Arrays.fill(szBillDvCode, (byte) 0x00);
            Arrays.fill(szGroupId, (byte) 0x00);
            Arrays.fill(cMsgType, (byte) 0x00);
            Arrays.fill(szMessageBaseId, (byte) 0x00);
            Arrays.fill(szFooter, (byte) 0x00);
            Arrays.fill(szFallbackType, (byte) 0x00);
//            Arrays.fill(szBtnType, (byte) 0x00);
//            Arrays.fill(szBtnName, (byte) 0x00);
//            Arrays.fill(szBtnValue, (byte) 0x00);
//            Arrays.fill(szRcsBody, (byte) 0x00);
//            Arrays.fill(szFallbackTitle, (byte) 0x00);
//            Arrays.fill(szFallbackMsg, (byte) 0x00);
//            Arrays.fill(szFallbackContentMimeType, (byte) 0x00);
//            Arrays.fill(szFallbackContentPath, (byte) 0x00);
//            Arrays.fill(szRcsContentFileId, (byte) 0x00);
            //Arrays.fill(szRcsContentPath, (byte) 0x00);
            //Arrays.fill(szRcsContentSize, (byte) 0x00);
         }

         @SuppressWarnings({ "unchecked", "rawtypes" })
         protected List getFieldOrder() {
            return Arrays.asList(new String[] { "szInServerId", "szMcpCode", "szCompanyId", "szBrandId", "szSrcCId", "szSrcMinNo",
                  "szDestCId", "szDestMinNo", "szSafenCId", "szSafenMinNo", "szPostNrn", "szNatCd", "nReturnQ", "nMsgLen", "szMsgCode", "szMsgSubCode",
                  "cSvcType", "cNextMaapFlag", "szCpMsgId", "szSerialNo", "szCpCreateTime", "szTelSendTime", "szDeliveryTime", "szReportRecvTime",
                  "szErrCode", "szErrCodeMaap", "szErrCodeFallback", "szBillCode", "szBillDvCode", "szGroupId", "cMsgType", "nFallBackFlag",
                  "szMessageBaseId", "nExpiryOption", "nHeader", "szFooter", "szFallbackType"});
//                  "szBtnType", "szBtnName",
//                  "szBtnValue", "szRcsBody", "szFallbackTitle", "szFallbackMsg", "nFallbackContentCnt",
//                  "szFallbackContentMimeType", "szFallbackContentPath", "szRcsContentFileId"});		// "nRcsContentCnt", "szRcsContentPath", "szRcsContentSize" 
         }
      }

      class resultMsg extends Structure {
         public byte[] szResultMsg = new byte[200];

         @SuppressWarnings({ "unchecked", "rawtypes" })
         protected List getFieldOrder() {
            return Arrays.asList(new String[] { "szResultMsg" });
         }
      }

   }

}