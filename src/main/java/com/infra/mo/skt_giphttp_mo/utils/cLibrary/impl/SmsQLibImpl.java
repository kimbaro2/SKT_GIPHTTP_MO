package com.infra.mo.skt_giphttp_mo.utils.cLibrary.impl;

import com.infra.mo.skt_giphttp_mo.dto.jna.LibC;
import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM;
import com.infra.mo.skt_giphttp_mo.config.application.LiveReloadCLibraryFile;
import com.infra.mo.skt_giphttp_mo.utils.cLibrary.SmsQLib;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@Scope("prototype")
@RequiredArgsConstructor
@Slf4j
public class SmsQLibImpl implements SmsQLib {
    private final ApplicationContext context;
    private final LiveReloadCLibraryFile liveReloadCLibraryFile;

    public SmsQLib getINSTANCE() {
        try {


            String cLibraryFilePath = context.getEnvironment().getProperty("witcom.performance.smsQLibcLibraryFilePath");

            Map<String, Object> options = new HashMap<>();
            options.put(Library.OPTION_OPEN_FLAGS, LibC.RTLD_GLOBAL);
            NativeLibrary.getInstance(cLibraryFilePath, options);

            return (SmsQLib) Native.load(cLibraryFilePath, SmsQLib.class);
        } catch (UnsatisfiedLinkError e) {
            log.error("❌ SmsQLib INSTANCE not found \n {}", e);
            int exitCode = SpringApplication.exit(context, () -> 0);
            System.exit(exitCode);
            return null; // Unreachable, but required
        }
    }

    @Override
    public void CloseFDForLogAsync() {
        getINSTANCE().CloseFDForLogAsync();
    }

    @Override
    public int DBinit() {
        return getINSTANCE().DBinit();
    }

    @Override
    public int Lvdprintf(int _iLevel, String strFormat, Object... args) {
        return getINSTANCE().Lvdprintf(_iLevel, strFormat, args);
    }

    @Override
    public void LvDprintfInit(String cTmp, char cLogType, int _iLevel) {
        getINSTANCE().LvDprintfInit(cTmp, cLogType, _iLevel);

    }

    @Override
    public void DprintfInit(String cTmp, char cLogType) {
        getINSTANCE().DprintfInit(cTmp, cLogType);
    }

    @Override
    public int LockThisQ(int nSemId, int nSemNo) {
        return getINSTANCE().LockThisQ(nSemId, nSemNo);
//        return 0;
    }

    @Override
    public int UnlockThisQ(int nSemId, int nSemNo) {
        getINSTANCE().UnlockThisQ(nSemId, nSemNo);
        return 0;
    }

    @Override
    public int IsQFull(int nQHead, int nQTail, int nQSize) {
        getINSTANCE().IsQFull(nQHead, nQTail, nQSize);
        return 0;
    }

    @Override
    public int IsQEmpty(int nQHead, int nQTail, int nQSize) {
        getINSTANCE().IsQEmpty(nQHead, nQTail, nQSize);
        return 0;
    }

    @Override
    public int InsertIntoSmsQWithQNo(QITEM ptrQItem, int nQNo) {
        getINSTANCE().InsertIntoSmsQWithQNo(ptrQItem, nQNo);
        return 0;
    }

    @Override
    public int InsertIntoSmsQnQNo(QITEM ptrQItem, int nQNo) {
        getINSTANCE().InsertIntoSmsQnQNo(ptrQItem, nQNo);
        return 0;
    }

    @Override
    public int GetAMsgFromSmsQ(int nQNo, QITEM ptrQItem) {
        getINSTANCE().GetAMsgFromSmsQ(nQNo, ptrQItem);
        return 0;
    }

    @Override
    public int InitQSem(int nQNo) {
        getINSTANCE().InitQSem(nQNo);
        return 0;
    }

    @Override
    public int CreateSmsQ() {
        getINSTANCE().CreateSmsQ();
        return 0;
    }

    @Override
    public int InsqStat(QITEM ptrQItem, int nMessageType, int nSMSCNo, int nServerID, int ModuleID, int nServiceID, int nErrorID, int nStatusNo, int nInforNo, int nTidSaveFlag, int nLogType, int nLineNo) {
        return getINSTANCE().InsqStat(ptrQItem, nMessageType, nSMSCNo, nServerID, ModuleID, nServiceID, nErrorID, nStatusNo, nInforNo, nTidSaveFlag, nLogType, nLineNo);
    }

    @Override
    public int InitQInfo() {
        return getINSTANCE().InitQInfo();
    }

    @Override
    public int InsertIntoSmsQ(QITEM ptrQItem) {
        log.info("InsertIntoSmsQ ptrQItem {}", ptrQItem);
        return getINSTANCE().InsertIntoSmsQ(ptrQItem);
    }

    @Override
    public int SaveQ(QITEM _pstQItem, int _nQNo) {
        getINSTANCE().SaveQ(_pstQItem, _nQNo);
        return 0;
    }

    @Override
    public int QUsage(int nQNO) {
        getINSTANCE().QUsage(nQNO);
        return 0;
    }

    @Override
    public int ReCreateSem(int nQIdx) {
        getINSTANCE().ReCreateSem(nQIdx);
        return 0;
    }

    @Override
    public int GetQNoFromMrmRoute(String szCId, String szMinNo, short usMsgCode) {
        return getINSTANCE().GetQNoFromMrmRoute(szCId, szMinNo, usMsgCode);
    }

    @Override
    public int SendW2P(QITEM ptrQItem, int[] nQueueNo) {
        getINSTANCE().SendW2P(ptrQItem, nQueueNo);
        return 0;
    }
}
