# C 프로젝트 GIPEVENT MSG_TYPE 로직 분석

## 1. 개요

C 코드의 GIPEVENT 프로젝트에서 MSG_TYPE 처리 로직을 분석한 결과, **MSG_TYPE 필드를 직접 사용하지 않고**, 대신 `gMOTRBILL`과 `gBILLTYPE` 변수를 사용하여 분기 처리하고 있습니다.

## 2. 주요 변수

### 2.1 전역 변수

```c
int gMOTRBILL;      // MO-TR 과금 여부 (0: MO-ACK만, 1: MO-ACK + MO-TR)
char gBILLTYPE;     // 과금 타입 ('1': 비과금, '2': SRC, '3': DESC, '4': GIVE)
```

### 2.2 변수 초기화 위치

**파일**: `GIPEVENT_c.c`  
**함수**: `ReadConfig()` (라인 407-408)

```c
gMOTRBILL = gipm.nMO_TR_BILL;	
gBILLTYPE = gipm.cBILL_TYPE;
```

이 변수들은 `HTTP_MOSEND_ACCESS` 테이블에서 조회한 값으로 설정됩니다.

## 3. MSG_TYPE 처리 로직

### 3.1 ProcessSMRes() 함수 - MO-ACK 처리

**파일**: `GIPEVENT_c.c`  
**라인**: 1951-2298

#### 3.1.1 SM_REQ_SIMPLE 케이스 (MO-ACK)

```c
case SM_REQ_SIMPLE :  // 라인 2006
    // 1. BILLTYPE == '1' (비과금) 체크
    if (gBILLTYPE == '1') {  // 라인 2018
        // MOCALLINFO 조회 후 레코드 삭제만 수행
        ret = SelectGIPMOCallInfo(...);
        // 과금 처리 스킵
        break;
    }
    
    // 2. ACK 결과 확인
    if( ptrMsgHdr->ucData[3] ==  GI_RES_NO_ERR ) {  // 라인 2094
        // 성공 통계 기록
        InsqStat(..., ERRORID_CP_MO_SUCCESS, ST_GIPEVENT_MORS_OK, ...);
    } else {
        // 실패 통계 기록
        InsqStat(..., ERRORID_CP_MO_FAIL, ST_GIP_MORS_FAIL, ...);
    }
    
    // 3. MOTRBILL 분기
    if (!gMOTRBILL) {  // 라인 2110
        // MOTRBILL='N': 즉시 과금 처리 (MO-ACK만)
        // - CID 검증 (DBGet_GIENQ_CID)
        // - MOCALLINFO 조회 (SelectGIPMOCallInfo)
        // - bprintf (CDR 출력)
        // - InsqStat (VSTAT 15 기록)
        // - RCS 처리
    } else {  // 라인 2257
        // MOTRBILL='Y': MOCALLINFO 업데이트만 수행 (MO-TR 대기)
        if (ESMClass == NOTI_PLUS || ESMClass == NOTI) {
            UpdateMO_NOTISEND(ptrMsgHdr, ...);
        } else {
            UpdateGIPMOCallInfo(ptrMsgHdr, ...);
        }
    }
    break;
```

**핵심 로직**:
- `gBILLTYPE == '1'`: 비과금 → MOCALLINFO 삭제만, 과금 처리 스킵
- `!gMOTRBILL`: 즉시 과금 (MO-ACK만) → bprintf, InsqStat 호출
- `gMOTRBILL`: MO-TR 대기 → UpdateGIPMOCallInfo/UpdateMO_NOTISEND 호출

#### 3.1.2 SM_REQ_TRANS_RESULT 케이스 (MO-TR)

```c
case SM_REQ_TRANS_RESULT :  // 라인 2271
    // 통계 기록만 수행
    InsqStat(..., ERRORID_CP_TR_SUCCESS, ST_GIPEVENT_MTTR_OK, ...);
    // 실제 과금 처리는 ProcessTRVBILLMO()에서 수행
    break;
```

### 3.2 ProcessSMReq() 함수 - MO-TR 요청 처리

**파일**: `GIPEVENT_c.c`  
**라인**: 3342-3420

```c
case SM_REQ_TRANS_RESULT :  // 라인 3361
    if (gMOTRBILL) {
        ProcessTRVBILLMO(ptrGIMsgHdr);  // MO-TR 과금 처리
    }
    SendAckNakTcp(ptrGIMsgHdr, GI_RES_NO_ERR, ...);
    break;
```

## 4. MSG_TYPE과의 관계

### 4.1 C 코드에서 MSG_TYPE 미사용

C 코드에서는 **MSG_TYPE 필드를 직접 사용하지 않습니다**. 대신:

- **MSG_TYPE='1' (MO-ACK만)**: `gMOTRBILL = 0`과 동일한 동작
- **MSG_TYPE='4' (MO-TR 대기)**: `gMOTRBILL = 1`과 동일한 동작

### 4.2 Java 코드와의 차이점

| 항목 | C 코드 | Java 코드 |
|------|--------|-----------|
| MSG_TYPE 사용 | ❌ 직접 사용 안 함 | ✅ 직접 사용 |
| 분기 변수 | `gMOTRBILL`, `gBILLTYPE` | `msgType`, `moTrBill`, `billType` |
| MSG_TYPE='2', '3' 처리 | ❌ 없음 (HTTP_MOSEND_ACCESS에 없음) | ⚠️ 처리 없음 (버그 가능성) |

## 5. BILLTYPE 정의

**파일**: `GITcp.h` (라인 98-104)

```c
#define BILLTYPE_NONE     0
#define BILLTYPE_NOT      1  // 비과금
#define BILLTYPE_SRC      2  // SRC 과금
#define BILLTYPE_DESC     3  // DESC 과금
#define BILLTYPE_GIVE     4  // GIVE 과금
#define BILLTYPE_CNT      5
```

## 6. 처리 흐름도

### 6.1 MO-ACK 처리 흐름 (ProcessSMRes)

```
ProcessSMRes()
  └─ SM_REQ_SIMPLE
      ├─ gBILLTYPE == '1'?
      │   └─ YES: MOCALLINFO 삭제만, 과금 스킵
      │   └─ NO: 계속 진행
      │
      ├─ ACK 결과 확인
      │   ├─ 성공: InsqStat(ERRORID_CP_MO_SUCCESS, ST_GIPEVENT_MORS_OK)
      │   └─ 실패: InsqStat(ERRORID_CP_MO_FAIL, ST_GIP_MORS_FAIL)
      │
      └─ gMOTRBILL 분기
          ├─ !gMOTRBILL (MSG_TYPE='1'와 동일)
          │   ├─ CID 검증 (DBGet_GIENQ_CID)
          │   ├─ MOCALLINFO 조회 (SelectGIPMOCallInfo)
          │   ├─ bprintf (CDR 출력)
          │   ├─ InsqStat(VSTAT 15 기록)
          │   └─ RCS 처리
          │
          └─ gMOTRBILL (MSG_TYPE='4'와 동일)
              ├─ UpdateGIPMOCallInfo 또는 UpdateMO_NOTISEND
              └─ MO-TR 대기
```

### 6.2 MO-TR 처리 흐름 (ProcessSMReq)

```
ProcessSMReq()
  └─ SM_REQ_TRANS_RESULT
      └─ gMOTRBILL?
          └─ YES: ProcessTRVBILLMO() 호출 (과금 처리)
```

## 7. 결론

### 7.1 C 코드의 특징

1. **MSG_TYPE 필드를 직접 사용하지 않음**
   - `HTTP_MOSEND_ACCESS` 테이블의 `MSG_TYPE` 필드는 조회하지 않음
   - 대신 `MOTRBILL`과 `BILLTYPE` 필드만 사용

2. **MOTRBILL로 분기**
   - `MOTRBILL = 0`: MO-ACK만 처리 (즉시 과금) → MSG_TYPE='1'와 동일
   - `MOTRBILL = 1`: MO-ACK + MO-TR 처리 → MSG_TYPE='4'와 동일

3. **BILLTYPE로 비과금 처리**
   - `BILLTYPE = '1'`: 비과금 → MOCALLINFO 삭제만, 과금 스킵

### 7.2 Java 코드와의 매핑

| C 코드 | Java 코드 |
|--------|-----------|
| `gMOTRBILL = 0` | `msgType = "1"` |
| `gMOTRBILL = 1` | `msgType = "4"` |
| `gBILLTYPE = '1'` | `billType = "1"` |

### 7.3 MSG_TYPE='2', '3' 처리

C 코드에서는:
- `HTTP_MOSEND_ACCESS` 테이블에 MSG_TYPE='2' (MT) 또는 '3' (선물) 레코드가 존재하지 않음
- MO 프로젝트에서는 MO 메시지만 처리하므로 MSG_TYPE='2', '3'은 사용되지 않음

Java 코드에서는:
- MSG_TYPE='2' 또는 '3'이 할당되면 처리 로직이 없어 스킵됨
- 이는 버그 가능성이 있으므로 검증 로직 추가 필요

## 8. 참고 사항

### 8.1 주요 함수 위치

- `ProcessSMRes()`: `GIPEVENT_c.c` 라인 1951
- `ProcessSMReq()`: `GIPEVENT_c.c` 라인 3342
- `ReadConfig()`: `GIPEVENT_c.c` 라인 402-446
- `SelectGIPMOCallInfo()`: `GIDBLib.c` 라인 2903
- `UpdateGIPMOCallInfo()`: `GIDBLib.c` 라인 3773
- `UpdateMO_NOTISEND()`: `GIDBLib.c` 라인 4772

### 8.2 관련 상수

- `SM_REQ_SIMPLE = 10`: MO-ACK
- `SM_REQ_TRANS_RESULT = 9`: MO-TR
- `ERRORID_CP_MO_SUCCESS = 15`: MO 성공
- `ERRORID_CP_MO_FAIL`: MO 실패
- `ST_GIPEVENT_MORS_OK`: MO ACK 성공 통계
- `ST_GIPEVENT_MOACK_BILL_OK = 80`: MO ACK 과금 성공 통계
