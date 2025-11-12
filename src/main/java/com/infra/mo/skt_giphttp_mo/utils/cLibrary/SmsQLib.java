package com.infra.mo.skt_giphttp_mo.utils.cLibrary;

import com.infra.mo.skt_giphttp_mo.dto.jna.QITEM;
import com.sun.jna.Library;

/**
 * TODO : CLang SMSS SmsQLib Module
 */

public interface SmsQLib extends Library {

//    SmsQLib getINSTANCE();


    int InsqStat(QITEM ptrQItem, int nMessageType, int nSMSCNo, int nServerID, int ModuleID, int nServiceID, int nErrorID, int nStatusNo, int nInforNo, int nTidSaveFlag, int nLogType, int nLineNo);

    void CloseFDForLogAsync();

    void DprintfInit(String cTmp, char cLogType);

    void LvDprintfInit(String cTmp, char cLogType, int _iLevel);

    int Lvdprintf(int _iLevel, String strFormat, Object... args);

    int DBinit();

    int LockThisQ(int nSemId, int nSemNo);

    int UnlockThisQ(int nSemId, int nSemNo);

    int IsQFull(int nQHead, int nQTail, int nQSize);

    int IsQEmpty(int nQHead, int nQTail, int nQSize);

    int InsertIntoSmsQWithQNo(QITEM ptrQItem, int nQNo);
    int InsertIntoSmsQnQNo(QITEM ptrQItem, int nQNo);

    int InsertIntoSmsQ(QITEM ptrQItem);

    int GetAMsgFromSmsQ(int nQNo, QITEM ptrQItem);

    int InitQSem(int nQNo);

    int CreateSmsQ();

    int InitQInfo();

    int SaveQ(QITEM _pstQItem, int _nQNo);

    int QUsage(int nQNO);

    int ReCreateSem(int nQIdx);

    int GetQNoFromMrmRoute(String szCId, String szMinNo, short usMsgCode);

    int SendW2P(QITEM ptrQItem, int[] nQueueNo);





}




/*
* GIP SMSS CLibrary 지원 함수
1. LockThisQ
 설명: 지정된 세마포어를 잠급니다. 세마포어 값(semval)을 1 감소시켜 잠금 상태로 만듭니다.
 파라미터:
o int nSemId: 세마포어 ID.
o int nSemNo: 세마포어 번호.
 반환값:
o SMS_Q_LOCK_SUCCESS: 성공적으로 잠금.
o SMS_Q_LOCK_FAIL_*: 실패 원인에 따른 오류 코드.
2. UnlockThisQ
 설명: 지정된 세마포어를 잠금 해제합니다. 세마포어 값(semval)을 1 증가시켜 잠금 해제 상태로 만듭니다.
 파라미터:
o int nSemId: 세마포어 ID.
o int nSemNo: 세마포어 번호.
 반환값:
o SMS_Q_UNLOCK_SUCCESS: 성공적으로 잠금 해제.
o SMS_Q_UNLOCK_FAIL_*: 실패 원인에 따른 오류 코드.
3. IsQFull
 설명: 원형 큐가 가득 찼는지 확인합니다.
 파라미터:
o int nQHead: 큐의 헤드 포인터.
o int nQTail: 큐의 테일 포인터.
o int nQSize: 큐의 크기.
 반환값:
o 1: 큐가 가득 참.
o 0: 큐가 비어 있지 않음.

4. IsQEmpty
 설명: 원형 큐가 비어 있는지 확인합니다.
 파라미터:
o int nQHead: 큐의 헤드 포인터.
o int nQTail: 큐의 테일 포인터.
o int nQSize: 큐의 크기.
 반환값:
o 1: 큐가 비어 있음.
o 0: 큐가 비어 있지 않음.
5. InsertIntoSmsQWithQNo
 설명: 지정된 큐 번호에 메시지를 삽입합니다.
 파라미터:
o QITEMPTR ptrQItem: 삽입할 메시지 항목.
o int nQNo: 큐 번호.
 반환값:
o Q_INSERT_SUCCESS: 성공적으로 삽입.
o Q_INSERT_FAIL_*: 실패 원인에 따른 오류 코드.
6. GetAMsgFromSmsQ
 설명: 지정된 큐 번호에서 메시지를 가져옵니다.
 파라미터:
o int nQNo: 큐 번호.
o QITEMPTR ptrQItem: 가져온 메시지를 저장할 포인터.
 반환값:
o Q_DELETE_SUCCESS: 성공적으로 메시지 가져옴.
o Q_DELETE_FAIL_*: 실패 원인에 따른 오류 코드.

7. InitQSem
 설명: 지정된 큐 번호에 대한 세마포어를 초기화합니다.
 파라미터:
o int nQNo: 큐 번호.
 반환값:
o 세마포어 ID.
o -1: 실패.
8. CreateSmsQ
 설명: SMS 큐를 생성하거나 기존 큐를 연결합니다.
 파라미터: 없음.
 반환값:
o SMSQPTR: 생성된 또는 연결된 큐의 포인터.
o NULL: 실패.
9. InitQInfo
 설명: 큐 정보를 초기화합니다. 데이터베이스에서 큐 정보를 읽어옵니다.
 파라미터: 없음.
 반환값:
o 1: 성공.
o INIT_QINFO_*: 실패 원인에 따른 오류 코드.
10. InsertIntoSmsQ
 설명: 메시지를 적절한 큐에 삽입합니다. 큐 번호는 내부적으로 결정됩니다.
 파라미터:
o QITEMPTR ptrQItem: 삽입할 메시지 항목.
 반환값:
o Q_INSERT_SUCCESS: 성공적으로 삽입.
o Q_INSERT_FAIL_*: 실패 원인에 따른 오류 코드.

11. SaveQ
 설명: 큐가 가득 찬 경우 메시지를 파일에 저장합니다.
 파라미터:
o QITEMPTR _pstQItem: 저장할 메시지 항목.
o int _nQNo: 큐 번호.
 반환값:
o 1: 성공적으로 저장.
o -1: 실패.
12. QUsage
 설명: 큐의 사용률을 계산합니다.
 파라미터:
o int nQNO: 큐 번호.
 반환값:
o 사용률(0~100).
13. ReCreateSem
 설명: 지정된 큐의 세마포어를 재생성합니다.
 파라미터:
o int nQIdx: 큐 인덱스.
 반환값: 없음.
14. GetQNoFromMrmRoute
 설명: MRM 라우팅 테이블에서 큐 번호를 가져옵니다.
 파라미터:
o char *szCId: CID.
o char *szMinNo: MinNo.
o unsigned short usMsgCode: 메시지 코드.
 반환값:
o 큐 번호.
o -1: 실패.
15. SendW2P
 설명: Web-to-Phone 메시지를 처리하고 큐에 삽입합니다.
 파라미터:
o QITEMPTR ptrQItem: 메시지 항목.
o int *nQueueNo: 큐 번호를 저장할 포인터.
 반환값:
o Q_INSERT_SUCCESS: 성공적으로 삽입.
o Q_INSERT_FAIL_*: 실패 원인에 따른 오류 코드.
*/