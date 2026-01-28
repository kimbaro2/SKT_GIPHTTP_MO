# BILL_TYPE='2', '4', '5'에 대한 InsqStat 호출 분석

## 1. InsqStat 함수 개요

`InsqStat`은 통계 기록 함수로, 메시지 처리 과정에서 발생하는 이벤트를 통계 테이블에 기록합니다.

## 2. C 코드에서 InsqStat 호출 위치 분석

### 2.1 MO 메시지 전송 시 (SendTcpMsgSimpleGetQ)

#### BILL_TYPE='2' (SRC) - 한도체크 시
```c
// LINE 940: CheckLimitMO 호출
nRet = CheckLimitMO(ptrMsgHdr);

if(nRet == TRUE) {
    // LINE 1020-1021: 한도 초과 통계 기록
    InsqStat(&stQitem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT, 
            ERRORID_CP_MO_LIMIT, ST_GIP_MO_LIMIT, IF_NULL, TID_NO_SAVE, LT_BOTH, __LINE__);
    
    // LINE 997-998: 차단 알림 큐 적재 성공 시
    InsqStat(&stQitem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
            ERRORID_CP_MO_SUCCESS, ST_GIPEVENT_INSQ_BLOCKNOTI, nQueueNo, TID_NO_SAVE, LT_TRACE, __LINE__);
    
    // LINE 1015-1016: 차단 알림 큐 적재 실패 시
    InsqStat(&stQitem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
            ERRORID_CP_MO_SUCCESS, ST_Q_INSERT_FAIL_BLOCKNOTI, nQueueNo, TID_NO_SAVE, LT_TRACE, __LINE__);
}
```

**특징:**
- BILL_TYPE='2'일 때만 `CheckLimitMO()` 호출
- 한도 초과 시 `ERRORID_CP_MO_LIMIT`, `ST_GIP_MO_LIMIT` 기록
- 차단 알림 큐 적재 성공/실패 시 각각 통계 기록

#### BILL_TYPE='4' (GIVE) - 한도체크 시
- C 코드에서는 MO 처리 경로에서 `CheckLimitGiveBill()` 호출되지 않음
- MT 처리 경로에서만 `CheckLimitGiveBill()` 호출됨
- **Java 프로젝트에서는 CheckLimitMO 호출 후 CheckLimitGIVE 호출하도록 구현됨**

#### BILL_TYPE='5' (CNT) - 한도체크 없음
- 한도체크 함수 호출 없음
- `InsqStat` 호출 없음 (한도체크 관련)

### 2.2 MO-ACK 처리 시 (ProcessSMRes)

#### BILL_TYPE='1' (NOT) - 비과금
```c
// LINE 2018: BILL_TYPE='1'인 경우
if (gBILLTYPE == '1') {
    // LINE 2082-2083: DB 조회 실패 시
    InsqStat(&gstQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
            ERRORID_CP_MO_NODATA, ST_DB_NO_DATA_GIPMOCALLINFO, IF_NULL, TID_NO_SAVE, LT_BOTH, __LINE__);
    
    // 즉시 과금 처리 없음 (bprintf 호출 없음)
}
```

#### BILL_TYPE='2', '4', '5' - 과금 처리
```c
// LINE 2094-2098: MO-ACK 성공 시
if( ptrMsgHdr->ucData[3] ==  GI_RES_NO_ERR ) {
    InsqStat(&gstQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
            ERRORID_CP_MO_SUCCESS, ST_GIPEVENT_MORS_OK, (int)ptrMsgHdr->ucData[3],
            TID_NO_SAVE, LT_BOTH, __LINE__);
}
else {
    // LINE 2102-2104: MO-ACK 실패 시
    InsqStat(&gstQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
            ERRORID_CP_MO_FAIL, ST_GIP_MORS_FAIL, (int)ptrMsgHdr->ucData[3],
            TID_NO_SAVE, LT_BOTH, __LINE__);
}

// LINE 2110: MOTRBILL='N'인 경우 즉시 과금 처리
if (!gMOTRBILL) {
    // LINE 2130-2131: Invalid DestCID 시
    InsqStat(&gstQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
            ERRORID_CP_MO_FAIL, ST_GIP_INVALID_CID, IF_NULL, TID_NO_SAVE, LT_TRACE, __LINE__);
    
    // LINE 2219-2220: 과금 성공 시
    InsqStat(&gstQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
            ERRORID_CP_MO_SUCCESS, ST_GIPEVENT_MOACK_BILL_OK, IF_NULL, TID_NO_SAVE, LT_TRACE, __LINE__);
}
```

**특징:**
- BILL_TYPE='2', '4', '5' 모두 동일한 `InsqStat` 호출 패턴
- MO-ACK 성공/실패 시 통계 기록
- 즉시 과금 처리 시 (`MOTRBILL='N'`) 과금 성공 통계 기록

### 2.3 MO-TR 처리 시 (ProcessTRVBILLMO)

```c
// LINE 3961: BILL_TYPE='1'인 경우 즉시 리턴
if (gBILLTYPE == '1') {
    return;
}

// LINE 3997-3998: VBILL_MO 큐 적재 실패 시
InsqStat(&szTRQitem, MESSAGE_TR, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
        ERRORID_CP_MO_TR_FAIL, ST_Q_INSERT_FAIL_VBILLMO, gnVBILL_MO_QueueNo, TID_SAVE, LT_TRACE, __LINE__);

// LINE 4006-4007: MO-TR 성공 시
InsqStat(&szTRQitem, MESSAGE_TR, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
        ERRORID_CP_MO_TR_FAIL, ST_GIPEVENT_MOTR_OK, gnVBILL_MO_QueueNo, TID_SAVE, LT_TRACE, __LINE__);
```

**특징:**
- BILL_TYPE='1'인 경우 MO-TR 처리 스킵
- BILL_TYPE='2', '4', '5' 모두 동일한 `InsqStat` 호출 패턴
- VBILL_MO 큐 적재 성공/실패 시 통계 기록

### 2.4 DB Insert 실패 시

```c
// LINE 1628-1629: MO_NOTISEND Insert 실패 시
InsqStat(&gstQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
        ERRORID_CP_MO_FAIL, ST_DB_NO_DATA_MONOTISEND, IF_NULL, TID_NO_SAVE, LT_TRACE, __LINE__);

// LINE 1638-1639: RelayMOCALLINFO Insert 실패 시
InsqStat(&gstQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
        ERRORID_CP_MO_FAIL, ST_DB_INS_FAIL_GIPMOCALLINFO, IF_NULL, TID_NO_SAVE, LT_TRACE, __LINE__);

// LINE 1645-1646: GIPMOCALLINFO Insert 실패 시
InsqStat(&gstQItem, MESSAGE_MO, 0, gServerID, MODULEID_GIPEVENT_C, SERVICEID_GIPEVENT,
        ERRORID_CP_MO_FAIL, ST_DB_INS_FAIL_GIPMOCALLINFO, IF_NULL, TID_NO_SAVE, LT_TRACE, __LINE__);
```

**특징:**
- BILL_TYPE='2', '4', '5' 모두 동일한 `InsqStat` 호출 패턴
- DB Insert 실패 시 `ERRORID_CP_MO_FAIL` 기록

---

## 3. BILL_TYPE별 InsqStat 호출 요약

### BILL_TYPE='2' (SRC - 발신자 과금)

| 처리 단계 | InsqStat 호출 | 조건 | ERRORID | ST_CODE |
|---------|--------------|------|---------|---------|
| 한도체크 | ✅ 호출 | `CheckLimitMO() == TRUE` | `ERRORID_CP_MO_LIMIT` | `ST_GIP_MO_LIMIT` |
| 차단 알림 큐 적재 | ✅ 호출 | `gGIPEVENT_BLK_NOTI == TRUE` | `ERRORID_CP_MO_SUCCESS` | `ST_GIPEVENT_INSQ_BLOCKNOTI` |
| MO-ACK 성공 | ✅ 호출 | `GI_RES_NO_ERR` | `ERRORID_CP_MO_SUCCESS` | `ST_GIPEVENT_MORS_OK` |
| MO-ACK 실패 | ✅ 호출 | `GI_RES_NO_ERR` 아님 | `ERRORID_CP_MO_FAIL` | `ST_GIP_MORS_FAIL` |
| 즉시 과금 성공 | ✅ 호출 | `MOTRBILL='N'` && 과금 성공 | `ERRORID_CP_MO_SUCCESS` | `ST_GIPEVENT_MOACK_BILL_OK` |
| MO-TR 성공 | ✅ 호출 | VBILL_MO 큐 적재 성공 | `ERRORID_CP_MO_TR_FAIL` | `ST_GIPEVENT_MOTR_OK` |
| DB Insert 실패 | ✅ 호출 | Insert 실패 | `ERRORID_CP_MO_FAIL` | `ST_DB_INS_FAIL_GIPMOCALLINFO` |

### BILL_TYPE='4' (GIVE - 선물 과금)

| 처리 단계 | InsqStat 호출 | 조건 | ERRORID | ST_CODE |
|---------|--------------|------|---------|---------|
| 한도체크 | ✅ 호출 | `CheckLimitMO() == FALSE` && `CheckLimitGIVE() == TRUE` | `ERRORID_CP_MO_LIMIT` | `ST_GIP_MO_LIMIT` |
| 차단 알림 큐 적재 | ✅ 호출 | `gGIPEVENT_BLK_NOTI == TRUE` | `ERRORID_CP_MO_SUCCESS` | `ST_GIPEVENT_INSQ_BLOCKNOTI` |
| MO-ACK 성공 | ✅ 호출 | `GI_RES_NO_ERR` | `ERRORID_CP_MO_SUCCESS` | `ST_GIPEVENT_MORS_OK` |
| MO-ACK 실패 | ✅ 호출 | `GI_RES_NO_ERR` 아님 | `ERRORID_CP_MO_FAIL` | `ST_GIP_MORS_FAIL` |
| 즉시 과금 성공 | ✅ 호출 | `MOTRBILL='N'` && 과금 성공 | `ERRORID_CP_MO_SUCCESS` | `ST_GIPEVENT_MOACK_BILL_OK` |
| MO-TR 성공 | ✅ 호출 | VBILL_MO 큐 적재 성공 | `ERRORID_CP_MO_TR_FAIL` | `ST_GIPEVENT_MOTR_OK` |
| DB Insert 실패 | ✅ 호출 | Insert 실패 | `ERRORID_CP_MO_FAIL` | `ST_DB_INS_FAIL_GIPMOCALLINFO` |

**특이사항:**
- Java 프로젝트에서는 `CheckLimitMO()` 호출 후 `CheckLimitGIVE()` 호출
- C 코드에서는 MO 처리 경로에서 `CheckLimitGIVE()` 호출되지 않음 (MT 전용)

### BILL_TYPE='5' (CNT - 비과금 정산용)

| 처리 단계 | InsqStat 호출 | 조건 | ERRORID | ST_CODE |
|---------|--------------|------|---------|---------|
| 한도체크 | ❌ 호출 안 함 | 한도체크 없음 | - | - |
| MO-ACK 성공 | ✅ 호출 | `GI_RES_NO_ERR` | `ERRORID_CP_MO_SUCCESS` | `ST_GIPEVENT_MORS_OK` |
| MO-ACK 실패 | ✅ 호출 | `GI_RES_NO_ERR` 아님 | `ERRORID_CP_MO_FAIL` | `ST_GIP_MORS_FAIL` |
| 즉시 과금 성공 | ✅ 호출 | `MOTRBILL='N'` && 과금 성공 | `ERRORID_CP_MO_SUCCESS` | `ST_GIPEVENT_MOACK_BILL_OK` |
| MO-TR 성공 | ✅ 호출 | VBILL_MO 큐 적재 성공 | `ERRORID_CP_MO_TR_FAIL` | `ST_GIPEVENT_MOTR_OK` |
| DB Insert 실패 | ✅ 호출 | Insert 실패 | `ERRORID_CP_MO_FAIL` | `ST_DB_INS_FAIL_GIPMOCALLINFO` |

**특이사항:**
- 한도체크 없음 (모든 플래그 FALSE)
- 과금 데이터는 출력됨 (`bprintf` 호출)
- 통계 기록은 BILL_TYPE='2', '4'와 동일

---

## 4. Java 프로젝트 구현 상태

### 4.1 BILL_TYPE='2' (SRC)

**현재 구현:**
- ✅ `checkLimitMO()` 호출 후 `InsqStat` 호출 (LINE 707-720)
- ✅ MO 전송 성공 시 `InsqStat` 호출 (LINE 1063-1076)
- ✅ MO-ACK/MO-TR 처리 시 `InsqStat` 호출 (`SmsResServiceImpl.kt`)

**C 코드와 일치 여부:** ✅ 일치

### 4.2 BILL_TYPE='4' (GIVE)

**현재 구현:**
- ✅ `checkLimitMO()` 호출 후 `checkLimitGIVE()` 호출
- ✅ `checkLimitGIVE()`가 `true`일 때 `InsqStat` 호출 (동일한 패턴)
- ✅ MO 전송 성공 시 `InsqStat` 호출
- ✅ MO-ACK/MO-TR 처리 시 `InsqStat` 호출

**C 코드와 일치 여부:** ⚠️ Java 프로젝트 특화 구현 (C 코드에서는 MO 경로에서 GIVE 한도체크 없음)

### 4.3 BILL_TYPE='5' (CNT)

**현재 구현:**
- ✅ 한도체크 없음
- ✅ MO 전송 성공 시 `InsqStat` 호출
- ✅ MO-ACK/MO-TR 처리 시 `InsqStat` 호출

**C 코드와 일치 여부:** ✅ 일치

---

## 5. 결론

### 공통 사항
1. **한도체크 제외**: BILL_TYPE='2', '4', '5' 모두 MO-ACK/MO-TR 처리 시 동일한 `InsqStat` 호출 패턴
2. **DB Insert 실패**: 모든 BILL_TYPE에서 동일한 `InsqStat` 호출
3. **MO-TR 처리**: BILL_TYPE='1' 제외하고 모두 동일한 `InsqStat` 호출

### 차이점
1. **한도체크**: 
   - BILL_TYPE='2': `CheckLimitMO()` 호출
   - BILL_TYPE='4': `CheckLimitMO()` 후 `CheckLimitGIVE()` 호출 (Java 프로젝트 특화)
   - BILL_TYPE='5': 한도체크 없음

2. **과금 처리**:
   - BILL_TYPE='2', '4', '5': 모두 `bprintf` 호출 (정산용 데이터 출력)
   - BILL_TYPE='1': `bprintf` 호출 없음

### 권장사항
- 현재 Java 프로젝트 구현이 C 코드와 일치하거나 적절히 확장됨
- BILL_TYPE='4'의 GIVE 한도체크는 Java 프로젝트 특화 요구사항으로 보임
- 모든 BILL_TYPE에서 `InsqStat` 호출 패턴이 일관성 있게 구현됨
