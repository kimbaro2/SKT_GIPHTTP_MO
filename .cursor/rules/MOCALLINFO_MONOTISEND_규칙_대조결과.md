# MOCALLINFO / MO_NOTISEND 규칙 요약 대조 결과

`MOCALLINFO_MONOTISEND_규칙요약.md` 기준으로 현재 MO 프로젝트 코드를 대조한 결과입니다.

---

## 1. 저장 규칙 (MOTRBILL=Y일 때만 저장)

| 규칙 | 항목 | 조건 | 기대 동작 |
|------|------|------|-----------|
| §1 | MOCALLINFO / MO_NOTISEND | MOTRBILL=**Y** | 저장함 |
| §1 | MOCALLINFO / MO_NOTISEND | MOTRBILL=**N** | 저장하지 않음 |

### 1.1 MO_NOTISEND 저장 — 충족

| 위치 | 동작 | 비고 |
|------|------|------|
| **NotiRegisteredHandler** (187~189) | `gMOTRBILL = isMoTrBillEnabled(actualEntity)` 후 `gMOTRBILL`일 때만 `insertMO_NOTISEND_NotiRegistered` | ✅ 규칙 충족 |
| **NotiPlusHandler** (194~196) | `gMOTRBILL`일 때만 `insertMO_NOTISEND_NotiPlus` | ✅ 규칙 충족 |

### 1.2 MOCALLINFO / Relay MOCALLINFO 저장 — 수정 완료 (2025-02 적용)

| 위치 | 수정 후 동작 | 규칙 대비 |
|------|--------------|-----------|
| **NormalMoHandler** | `gMOTRBILL = isMoTrBillEnabled(actualEntity)` 후 `gMOTRBILL`일 때만 insertRelay/insertGIPMOCallInfo | ✅ |
| **SmsManager638Handler** | 동일 | ✅ |
| **SmsMessenger2580Handler** | 동일 | ✅ |
| **Character1584Handler** | 동일 | ✅ |
| **ForwardHandler** | 동일 | ✅ |
| **GsmRoamingHandler** | gMOTRBILL일 때만 insertGIPMOCallInfo, 아니면 dbInsertOk=true | ✅ |
| **CdmaRoamingHandler** | 동일 | ✅ |

- 위 7개 핸들러에 **MOTRBILL=Y일 때만** MOCALLINFO(또는 Relay) insert 하도록 `isMoTrBillEnabled(actualEntity)` 분기 적용 완료.

---

## 2. MO-TR 분기 활용

- MOCALLINFO/MO_NOTISEND는 MO-TR에서 “어떤 정보인지” 확인용으로 사용.
- **대조**: processSMReqTransResult, processMOBilling, updateMO_NOTISEND, dbDelMO_NOTISEND 등에서 5키 조회·갱신·삭제 후 과금/통계 처리 — 목적에 맞게 사용 중.

---

## 3. MOTRBILL=N, BillType≠1 (MO 단계 과금)

- **규칙**: MO-TR 없이 MO 단계에서만 과금. 이 경우 MOCALLINFO/MO_NOTISEND 미저장 가능 → MO-ACK 시 effectiveMoInfo가 null일 수 있음.
- **현재 코드**: MOTRBILL=N, BillType≠1이면 `processMOBilling(..., isMoAckContext=true)` 호출. effectiveMoInfo는 MOCALLINFO 5키 → MO_NOTISEND 5키 fallback으로만 채워짐.
- **결과**: MOTRBILL=N으로 저장을 안 하면 해당 MO-ACK에서는 **effectiveMoInfo = null** → bprintf 블록 미진입. 규칙 요약 §3 대로 **request/access 기반 등 MOCALLINFO·MONOTISEND에 의존하지 않는 과금 경로**는 현재 미구현 상태로 유의.

---

## 4. 과금·조회 시 5키 규칙

### 4.1 processMOBilling 조회 순서 — 충족

| 단계 | 규칙 | 현재 구현 (SmsResServiceImpl) | 결과 |
|------|------|-------------------------------|------|
| 1 | MOCALLINFO 5키 조회 (5키 모두 있을 때만) | 833~837: `srcCID, srcCallNoForKey, destCID, destCallNoForKey, cpMsgId` 로 `selectGIPMOCallInfo` | ✅ |
| 2 | 실패 시 MO_NOTISEND **동일 5키** 조회 | 877~881: 5키 모두 있을 때만 `findOneForVByMsgAndCidAndCallNo(cpMsgId, srcCID, destCID, srcCallNoForKey, destCallNoForKey)` | ✅ |
| 3 | 5키 부족 시 fallback 미호출 | 5키 하나라도 비면 MOCALLINFO 조회 생략, MO_NOTISEND 호출도 생략 | ✅ |

- **findByMsgIdAndServerType** (msgId + SERVERTYPE): processMOBilling 내 호출 없음. Repository 메서드만 존재. ✅

### 4.2 기타 MO_NOTISEND 조회 — 5키 사용

| 위치 | 용도 | 조회 방식 | 결과 |
|------|------|-----------|------|
| processESMClassBranch 안심문자 분기 (3388~3393) | MO_NOTISEND 조회 후 삭제/과금 | 5키 모두 있을 때만 `findOneForVByMsgAndCidAndCallNo(msgId, srcCid, destCid, srcCallNo, destCallNo)` | ✅ |

### 4.3 삭제 시 5키 — 수정 완료 (2025-02 적용)

| 위치 | 대상 | 삭제/조회 방식 | 결과 |
|------|------|----------------|------|
| updateMO_NOTISEND (3048~3055) | MO_NOTISEND | 5키 `deleteByMsgIdAndSrcCIdAndDestCIdAndSrcCallNoAndDestCallNoAndServerType` | ✅ |
| dbDelMO_NOTISEND (3695~3698) | MO_NOTISEND | 동일 5키 삭제 | ✅ |
| processSMReqSimple **BILLTYPE='1'** MO_NOTISEND fallback | MO_NOTISEND | **5키가 모두 있으면** 5키 삭제 메서드 호출. **5키 부족 시에만** traceId+srcCid+destCid로 `selectTRMO_NOTISEND` | ✅ 수정 완료 |

- BILLTYPE='1' MO_NOTISEND 삭제: request에 5키가 있으면 5키 삭제 우선, 없을 때만 기존 traceId 기반 경로 사용.

---

## 5. 요약 표

| 구분 | 항목 | 상태 | 비고 |
|------|------|------|------|
| 저장 | MO_NOTISEND (NotiRegistered, NotiPlus) | ✅ | MOTRBILL=Y일 때만 insert |
| 저장 | MOCALLINFO/Relay (NormalMo, 638, 2580, 1584, Forward, Roaming) | ✅ | 수정 완료: MOTRBILL=Y일 때만 insert |
| 조회 | processMOBilling MOCALLINFO → MO_NOTISEND fallback | ✅ | 동일 5키, 5키 부족 시 fallback 미호출 |
| 조회 | msgId+SERVERTYPE 단독 조회 | ✅ | 과금 경로에서 미사용 |
| 삭제 | updateMO_NOTISEND, dbDelMO_NOTISEND | ✅ | 5키 삭제 |
| 삭제 | BILLTYPE='1' MO_NOTISEND | ✅ | 수정 완료: 5키 우선 삭제, 부족 시 traceId 경로 |
| 유의 | MOTRBILL=N, BillType≠1 MO 단계 과금 | ⚠️ | effectiveMoInfo null 가능, 비-DB 기반 과금 경로 미구현 |

---

## 6. 참고 코드 위치 (라인은 변경 가능)

- **저장**:  
  - Noti: `NotiRegisteredHandler.kt` 187~189, `NotiPlusHandler.kt` 194~196  
  - MOCALLINFO/Relay: `NormalMoHandler.kt` 192/207, `SmsManager638Handler.kt` 188/203, `SmsMessenger2580Handler.kt` 187/202, `Character1584Handler.kt` 188/202, `ForwardHandler.kt` 183/197, `GsmRoamingHandler.kt` 191, `CdmaRoamingHandler.kt` 193  
- **조회**: `SmsResServiceImpl.kt` processMOBilling 833~881, processESMClassBranch 3388~3393  
- **삭제**: `SmsResServiceImpl.kt` updateMO_NOTISEND 3048~3055, dbDelMO_NOTISEND 3695~3698, selectTRMO_NOTISEND 2721 (findOneForV) / processSMReqSimple 544~558  

---

## 7. 적용 이력

- **2025-02**: §1 저장 규칙 — 7개 핸들러(NormalMo, SmsManager638, SmsMessenger2580, Character1584, Forward, GsmRoaming, CdmaRoaming)에 MOTRBILL=Y일 때만 MOCALLINFO/Relay insert 적용.
- **2025-02**: §4.3 삭제 규칙 — processSMReqSimple BILLTYPE='1' MO_NOTISEND fallback을 5키 삭제 우선, 5키 부족 시 traceId 기반 삭제로 수정.

이 문서는 규칙 요약 대조용이며, 위 적용 이력에 따라 반영된 수정을 반영합니다.
