# msgStatus 체크 로직 추가 및 C 코드 비교

## 1. 추가된 기능

### 1.1 `processSMReqTransResult()`에 `msgStatus == SEND_OK` 체크 추가

**C 코드**: `VBILLMO_c.c` LINE 438
```c
if(ptrQitem->ucMsgStatus == SEND_OK) 
{
    // 과금 처리
    // ...
    InsqStat(..., ERRORID_CP_MO_TR_SUCCESS, ST_VBILLMO_OK, ...);
}
else
{
    // NOT BILL 처리
    InsqStat(..., ERRORID_CP_MO_TR_FAIL, ST_VBILLMO_DONT_BILL_TRFAIL, ...);
}
```

**Java/Kotlin 코드**: `SmsResServiceImpl.kt` LINE 993-1100
```kotlin
val msgStatus = qItem.ucMsgStatus.toInt()
if (msgStatus == SEND_OK) {
    // 과금 처리
    if (!gMOTRBILL) {
        processMOBilling(qItem, request, gipHttpMoAccess, smsQLib)
        processESMClassBranch(qItem, request, gipHttpMoAccess, smsQLib)
    } else {
        enqueueVbillMoTR(qItem, request, gipHttpMoAccess)
    }
} else {
    // NOT BILL 처리
    qItem.RcsResult = RCS_RESULT_ETC
    smsQLib.InsqStat(
        qItem,
        MESSAGE_TR,
        0,
        gServerID,
        MODULEID_VBILLMO,
        SERVICEID_GIPEVENT,
        ERRORID_CP_MO_TR_FAIL,
        ST_VBILLMO_DONT_BILL_TRFAIL,
        IF_NULL,
        TID_NO_SAVE,
        LT_BOTH,
        0
    )
}
```

## 2. C 코드와 비교

### 2.1 `ProcessTR` 함수 구조 (C 코드)

```
ProcessTR(ptrQitem)
├─ SelectTRMOCallInfo() 호출
│
├─ if (ret > 0)  // MOCALLINFO 데이터가 있는 경우
│  ├─ if(ptrQitem->ucMsgStatus == SEND_OK)
│  │  ├─ CID 검증
│  │  ├─ CDRCId 복사
│  │  ├─ bprintf (CDR 출력)
│  │  └─ InsqStat(..., ERRORID_CP_MO_TR_SUCCESS, ST_VBILLMO_OK, ...)
│  └─ else
│     └─ InsqStat(..., ERRORID_CP_MO_TR_FAIL, ST_VBILLMO_DONT_BILL_TRFAIL, ...)
│
└─ else if (ret == ORA_NODATA)  // MOCALLINFO 데이터가 없는 경우
   ├─ SelectTRMO_NOTISEND() 호출
   │
   ├─ if (ret <= 0)  // MO_NOTISEND도 없는 경우
   │  └─ InsqStat(..., ERRORID_CP_MO_NODATA, ST_DB_NO_DATA_MONOTISEND, ...)
   │
   └─ else  // MO_NOTISEND 데이터가 있는 경우
      ├─ if(ptrQitem->ucMsgStatus == SEND_OK)
      │  ├─ CID 검증
      │  ├─ CDRCId 복사
      │  ├─ bprintf (CDR 출력)
      │  └─ InsqStat(..., ERRORID_CP_MO_TR_SUCCESS, ST_VBILLMO_NOTISEND_OK, ...)
      └─ else
         └─ InsqStat(..., ERRORID_CP_MO_TR_FAIL, ST_VBILLMO_DONT_BILL_TRFAIL, ...)
```

### 2.2 Java/Kotlin 코드 구조

```
processSMReqTransResult(qItem, request, ...)
├─ updateGIPMOCallInfo() 또는 updateMO_NOTISEND() 호출
│
├─ msgStatus == SEND_OK 체크 (추가됨)
│
├─ if (msgStatus == SEND_OK)
│  ├─ if (!gMOTRBILL)
│  │  ├─ processMOBilling() 호출
│  │  │  ├─ SelectGIPMOCallInfo() 호출
│  │  │  ├─ 실패 시 MO_NOTISEND 조회 (fallback)
│  │  │  ├─ CID 검증
│  │  │  ├─ CDRCId 복사
│  │  │  ├─ bprintf (CDR 출력)
│  │  │  └─ InsqStat(..., ERRORID_CP_MO_SUCCESS, ST_GIPEVENT_MOACK_BILL_OK, ...)
│  │  └─ processESMClassBranch() 호출
│  └─ else
│     └─ enqueueVbillMoTR() 호출
│
└─ else
   ├─ RcsResult = RCS_RESULT_ETC
   └─ InsqStat(..., ERRORID_CP_MO_TR_FAIL, ST_VBILLMO_DONT_BILL_TRFAIL, ...)
```

## 3. 주요 차이점

### 3.1 구조적 차이

| 항목 | C 코드 | Java/Kotlin 코드 |
|------|--------|------------------|
| **호출 위치** | `ProcessTR` (VBILL_MO 큐에서 dequeue 후) | `processSMReqTransResult` (HTTP 요청 처리 중) |
| **SelectTRMOCallInfo 위치** | `ProcessTR` 내부 | `processMOBilling` 내부 |
| **SelectTRMO_NOTISEND 위치** | `ProcessTR` 내부 (fallback) | `processMOBilling` 내부 (fallback) |
| **msgStatus 체크 위치** | `ProcessTR` 내부 (SelectTRMOCallInfo/SelectTRMO_NOTISEND 후) | `processSMReqTransResult` 내부 (updateGIPMOCallInfo/updateMO_NOTISEND 후) |

### 3.2 통계 코드 차이

| 항목 | C 코드 | Java/Kotlin 코드 |
|------|--------|------------------|
| **SEND_OK 성공 (MOCALLINFO)** | `ERRORID_CP_MO_TR_SUCCESS, ST_VBILLMO_OK` | `ERRORID_CP_MO_TR_SUCCESS, ST_VBILLMO_OK` ✅ |
| **SEND_OK 성공 (MO_NOTISEND)** | `ERRORID_CP_MO_TR_SUCCESS, ST_VBILLMO_NOTISEND_OK` | `ERRORID_CP_MO_TR_SUCCESS, ST_VBILLMO_NOTISEND_OK` ✅ |
| **SEND_OK 실패** | `ERRORID_CP_MO_TR_FAIL, ST_VBILLMO_DONT_BILL_TRFAIL` | `ERRORID_CP_MO_TR_FAIL, ST_VBILLMO_DONT_BILL_TRFAIL` ✅ |

**수정 완료**: 
- `ERRORID_CP_MO_SUCCESS`는 MO에서 CP에게 HTTP 발송 후 200인 경우 처리하는 통계입니다.
- `processMOBilling()`은 MO-TR 결과를 처리하므로 C 코드와 동일하게 `ERRORID_CP_MO_TR_SUCCESS`를 사용하도록 수정했습니다.
- MOCALLINFO 케이스: `ST_VBILLMO_OK` 사용
- MO_NOTISEND 케이스: `ST_VBILLMO_NOTISEND_OK` 사용

### 3.3 누락된 기능

1. **`SelectTRMO_NOTISEND` fallback 로직**: 
   - C 코드: `SelectTRMOCallInfo` 실패 시 `SelectTRMO_NOTISEND` 호출
   - Java/Kotlin: `processMOBilling()` 내부에서 `MO_NOTISEND` 조회는 있지만, `SelectTRMO_NOTISEND`와 동일한 로직인지 확인 필요

2. **`ST_VBILLMO_NOTISEND_OK` 통계**:
   - C 코드: MO_NOTISEND 케이스에서 `ST_VBILLMO_NOTISEND_OK` 사용
   - Java/Kotlin: `ST_GIPEVENT_MOACK_BILL_OK` 사용 (MO-ACK 컨텍스트)

## 4. 검증 완료 사항

✅ `msgStatus == SEND_OK` 체크 추가
✅ `SEND_OK`가 아닌 경우 `ERRORID_CP_MO_TR_FAIL, ST_VBILLMO_DONT_BILL_TRFAIL` 통계 기록
✅ `RcsResult = RCS_RESULT_ETC` 설정 (SEND_OK가 아닌 경우)
✅ `SEND_OK`인 경우에만 과금 처리 진행

## 5. 수정 완료 사항

✅ **통계 코드 수정**: `processMOBilling()`에서 C 코드와 동일하게 `ERRORID_CP_MO_TR_SUCCESS` 사용
✅ **MOCALLINFO 케이스**: `ST_VBILLMO_OK` 사용
✅ **MO_NOTISEND 케이스**: `ST_VBILLMO_NOTISEND_OK` 사용
✅ **MESSAGE_TR 사용**: `MESSAGE_MO` → `MESSAGE_TR`로 변경
✅ **MODULEID_VBILLMO 사용**: `MODULEID_GIPEVENT_C` → `MODULEID_VBILLMO`로 변경
✅ **LT_BOTH 사용**: `LT_TRACE` → `LT_BOTH`로 변경

## 6. 추가 확인 필요 사항

1. **`processMOBilling()` 내부의 `MO_NOTISEND` fallback 로직**이 C 코드의 `SelectTRMO_NOTISEND`와 동일한지 확인

