# InsqStat / ESMClass / BILL_TYPE 분석 (Java 구현 + C 대비 오차)

본 문서는 `SKT_GIPHTTP_MO` 프로젝트에서 확인된 **InsqStat 호출(라인 기반)**, **BILL_TYPE별 Java 동작**, **ESMClass 처리 흐름 및 C 대비 오차(불일치)**를 한 곳에 정리합니다.

---

## 1. InsqStat 개요

### 1.1 시그니처(참고)

```text
InsqStat(ptrQItem, nMessageType, nSMSCNo, nServerID, ModuleID, nServiceID,
         nErrorID, nStatusNo, nInforNo, nTidSaveFlag, nLogType, nLineNo)
```

### 1.2 핵심 파라미터 의미

- **nErrorID**: VSTAT(통계)로 기록되는 “에러/결과 코드” 성격의 값 (예: 15=MO-ACK 성공, 16=MO 실패, 27=MO-TR 과금 성공 등)
- **nStatusNo(STATID)**: 통계 구분 코드 (예: `ST_GIPEVENT_MOACK_BILL_OK`, `ST_VBILLMO_OK`, …)
- **nLogType**
  - `LT_TRACE(0)`: TRC만
  - `LT_BOTH(1)`: VSTAT + TRC

---

## 2. SmsResServiceImpl.kt: InsqStat 호출 라인 표기 + 라인별 로직 요약

대상 파일:
- `src/main/java/com/infra/mo/skt_giphttp_mo/service/impl/SmsResServiceImpl.kt`

### 2.1 활성 호출(실제 실행됨)

| 라인 | 상위 함수 | 기능 요약 |
|------|-----------|----------|
| **200** | `recordVstat15AfterBprintf` | MO-ACK 성공 시 `bprintf` 후 **VSTAT 15 + TRC**를 기록하여 과금 성공(MOACK) 통계를 남김. |
| **589** | `processSMReqSimple` | MO-ACK 실패(`ackResult != 0`) 시 **TRC만** 기록하여 MO RES 실패 통계를 남김(VSTAT 중복/오염 방지). |
| **939** | `processMOBilling` | 과금 데이터 조회 후 `DBGet_GIENQ_CID` 실패(`cidLen<0`) 시 Invalid CID 통계를 **TRC만** 기록. |
| **1507** | `processMOBilling` | `effectiveMoInfo == null`(조회 결과 없음) 시 ORA_NODATA 통계를 **VSTAT+TRC**에 기록. |
| **1927** | `processSMReqTransResult` | MO-TR 결과 수신 시 전송 성공(2)이 아닐 때 미과금/실패(TRFAIL) 통계를 **VSTAT+TRC**에 기록. |
| **3211** | `processTR_Roaming_GSM` | 로밍 TR에서 `MOTRBILL='N'` 또는 `msgStatus!=2`면 인자로 받은 `errorId/statId`로 **VSTAT+TRC** 기록. |
| **3266** | `processTR_Normal` | 일반 TR에서 `MOTRBILL='N'` 또는 `msgStatus!=2`면 인자로 받은 `errorId/statId`로 **VSTAT+TRC** 기록. |
| **3454** | `processMoReportDelivered` | MO-Report 전송 성공(`msgStatus=2`) 시 **VSTAT 27 + ST_VBILLMO_OK**를 **VSTAT+TRC**로 기록. |
| **3489** | `processMoReportExpired` | `MSG_STATUS=3(EXPIRED)` 만료 통계를 **VSTAT+TRC** 기록. |
| **3523** | `processMoReportUndelivered` | `MSG_STATUS=5(UNDELIVERABLE)` 전송불가 통계를 **VSTAT+TRC** 기록. |
| **3557** | `processMoReportFwdFail` | `MSG_STATUS=14(FWDFAIL)` 전달실패 통계를 **VSTAT+TRC** 기록. |
| **3590** | `processMoReportSpamErr` | `MSG_STATUS=16(SPAMERR)` 스팸오류 통계를 **VSTAT+TRC** 기록. |
| **3623** | `processMoReportUserDel` | `MSG_STATUS=17(USERDEL)` 사용자삭제 통계를 **VSTAT+TRC** 기록. |
| **3656** | `processMoReportNPrefix` | `MSG_STATUS=19(NPREFIX)` NP Prefix 오류 통계를 **VSTAT+TRC** 기록. |
| **3689** | `processMoReportAdmCanc` | `MSG_STATUS=20(ADMCANC)` 관리자 취소 통계를 **VSTAT+TRC** 기록. |
| **3723** | `processMoReportOther` | 정의되지 않은 기타 status 시 공통 실패 통계(`ST_VBILLMO_DONT_BILL_TRFAIL`)를 **VSTAT+TRC** 기록. |

### 2.2 비활성 호출(주석 처리됨)

| 라인 | 상위 함수 | 기능 요약(원래 의도) |
|------|-----------|----------------------|
| **1196** | `processMOBilling` | MO-ACK + `MOTRBILL='N'` + 문자메신저서비스 케이스에서 별도 InsqStat(TRC만) 호출(현재 주석). |
| **1256** | `processMOBilling` | MO-ACK + `MOTRBILL='N'` + 문자매니저서비스 케이스에서 별도 InsqStat 호출(현재 주석). |
| **1394** | `processMOBilling` | `MOTRBILL='Y'` MO-TR 단계에서 `effectiveMoInfo` 기반으로 **한 번의 InsqStat**로 성공 통계 기록(현재 주석, qItem swap만 유지). |
| **3039** | `processTR_NotiPlus` | 안심문자 TR에서 `MOTRBILL='N'` 또는 `msgStatus!=2`일 때 VSTAT 기록(주석). |
| **3094** | `processTR_Roaming_CDMA` | CDMA 로밍 TR에서 동일 조건 시 VSTAT 기록(주석). |
| **3156** | `processTR_Roaming_GSM` | GSM 로밍 TR에서 동일 조건 시 VSTAT 기록(주석). |

---

## 3. BILL_TYPE별 Java 동작 요약

### 3.1 공통 전제

- BILL_TYPE은 `HTTP_MOSEND_ACCESS`(또는 대응 Entity)의 `billType`에서 읽고, `BillTypeValidator`로 정규화/검증합니다.
- `MOTRBILL`(moTrBill)이 **즉시 과금(MO-ACK에서 과금)** vs **지연 과금(MO-TR에서 과금)**을 좌우합니다.

### 3.2 BILL_TYPE='1' (NOT - 비과금)

- **MO-ACK (`processSMReqSimple`)**: `billType=="1" && !gMOTRBILL`이면 레코드 삭제만 수행하고 과금/후속 처리를 스킵(단, moTrBill 값이 null이면 조기 삭제하지 않고 return).
- **MO-TR 수신 (`processSMReqTransResult`)**: `gBILLTYPE=='1'`이면 업데이트/분기 후 레코드 삭제만 하고 과금 스킵.
- **MO-Report (`processMoReport`)**: `gBILLTYPE=='1'`이면 레코드 삭제만 하고 과금 스킵.
- **InsqStat 관점**: 대체로 스킵되며, 조회 실패/예외 케이스(예: ORA_NODATA)만 남는 구조.

### 3.3 BILL_TYPE='2' (SRC - 발신자 과금)

- **전송 전 한도체크**: `MOThreadPool.kt`에서 `checkLimitMO()` 호출(발신자 한도).
- **MO-ACK**: ACK 실패 시 InsqStat 기록(라인 589). 성공 시 중복 기록 방지를 위해 일부 VSTAT(35) 제거됨(주석 참고).
- **즉시 과금(MOTRBILL='N')**: `processMOBilling`에서 `bprintf`/CID검증/통계(VSTAT 15 등) 수행(라인 200 포함).
- **지연 과금(MOTRBILL='Y')**: MO-TR 결과 기반으로 `processMOBilling`로 이어지는 흐름 사용.

### 3.4 BILL_TYPE='4' (GIVE - 선물 과금)

- **전송 전 한도체크**: `checkLimitMO()` 후 **추가로 `checkLimitGIVE()` 수행**(Java 확장/특화).
- 나머지 흐름은 `BILL_TYPE='2'`와 동일한 패턴(ACK/과금/통계).

### 3.5 BILL_TYPE='5' (CNT - 정산용)

- **한도체크 없음**
- **bprintf는 수행**(정산/로그 목적)
- 통계 기록 패턴은 `BILL_TYPE='2','4'`와 유사하게 유지됨.

---

## 4. ESMClass: Java 구현 흐름

### 4.1 도메인 분류(설정 기반)

설정:
- `src/main/resources/application.properties`
  - `witcom.mo.domain.noti-plus=20,21`
  - `witcom.mo.domain.roaming=36,37,38,40,41,42,69,70,72,73`
  - `witcom.mo.domain.normal=1`

코드:
- `service/handler/EsmClassDomainResolver.kt`가 위 설정을 사용해 `NOTI_PLUS/ROAMING/NORMAL/OTHER`로 분류.

### 4.2 TR 라우팅(ESMClass별 처리 함수)

코드:
- `SmsResServiceImpl.kt` → `processTRByEsmClass()`
  - NOTI: `90,91` → `processTR_Noti`
  - NOTI_PLUS: `20,21` → `processTR_NotiPlus`
  - CDMA 로밍: `36,37` → `processTR_Roaming_CDMA`
  - GSM/WCDMA/BIZ 로밍: `40,41,69,70,72,73` → `processTR_Roaming_GSM`
  - 일반: `NORMAL_MO, PORTED_MO, FORWARD_MO` → `processTR_Normal`

---

## 5. ESMClass: C 대비 Java 오차(불일치) 분석

비교 근거:
- `C_GIPEVENT_MSG_TYPE_분석.md`
- `.cursor/rules/ESMClass_Processing_Rules.md`
- Java 코드: `MOThreadPool.kt`, `SmsResServiceImpl.kt`, `EsmClassHandler.kt`, `EsmClassDomainResolver.kt`

### 5.1 (확정) EsmClassHandler의 NOTI(90/91) 미반영

- `EsmClassHandler.shouldInsertMO_NOTISEND()` / `shouldUpdateMO_NOTISEND()`는 **NOTI_PLUS(20/21)만** true.
- 하지만 C/규칙 문서는 **20/21/90/91 모두 MO_NOTISEND 대상**으로 기술.
- 현재 실제 실행 로직은:
  - **MO 전송 후 DB Insert**: `MOThreadPool.kt`에서 `(isNotiPlusType || isNotiType)`로 90/91 포함 처리(일치)
  - **MO-ACK(MOTRBILL='Y') update**: `SmsResServiceImpl.kt`에서도 `(isNotiPlusType || isNotiType)`로 90/91 포함 처리(일치)
- 결론: **핵심 흐름은 맞지만, Handler는 문서/의도와 불일치(기술부채/혼동 지점)**.

### 5.2 (가능성 큼) ROAMING Forward(38/42) TR 라우팅 불일치

- 설정(roaming) 및 `EsmClassHandler.isRoamingEsmClass()`에는 `38,42`가 포함됨.
- 반면 `processTRByEsmClass()`는 로밍 라우팅에 `38/42`를 포함하지 않아, 기본(normal) 처리로 떨어질 여지가 있음.
- 결론: **C에서 38/42를 로밍 TR로 처리했다면 Java는 분기 누락(오차)**.

### 5.3 (확정) 문자메신저/문자매니저 서비스 판별 미구현(TODO)

- `EsmClassHandler.isSmsMessengerService()` / `isSmsManagerService()`는 TODO로 **항상 false**.
- 따라서 “특정 서비스는 InsqStat/TRC 스킵” 같은 예외 분기가 있어도 실제로는 동작하지 않음.
- 결론: C에 해당 예외가 존재했다면 **Java가 예외를 재현하지 못함(오차)**.

### 5.4 (리스크) 설정 기반 분류와 하드코딩 분류의 혼재

- 도메인 분류는 설정 기반(`EsmClassDomainResolver`)인데,
- 일부 로직은 하드코딩 `when (gESMCLASS)`로 분류(`MOThreadPool.kt` 등).
- 설정 값이 변경될 경우 **분기 결과가 서로 달라질 가능성**이 있음.

---

## 6. 체크리스트(오차 확인/보완 포인트)

- [ ] `EsmClassHandler`의 NOTI(90/91) 처리 규칙을 문서/실코드와 일치시키기(최소: shouldInsert/shouldUpdate에 NOTI 포함).
- [ ] `processTRByEsmClass()`에 로밍 Forward(38/42) 포함 여부 확정 및 분기 추가 필요성 판단.
- [ ] 문자메신저/문자매니저 서비스 판별 로직(ESMClass 기반)을 실제 요구사항 값으로 구현.
- [ ] ESMClass 도메인 분류를 설정 기반으로 일원화할지(하드코딩 제거) 결정.

---

## 7. ESMClass별 개발 진척도(“C 코드 기준 요구” 대비)

표기:
- **✅ 완료**: C 기준 동작이 Java에 구현되어 동작 경로가 존재
- **⚠️ 부분**: 일부만 구현(예: DB/통계는 OK, 송신/라우팅은 TODO)
- **❌ 미구현**: C 기준 동작이 Java에 없음 또는 TODO만 존재

| ESMClass 그룹 | C 기준 서비스 | MO 전송 DB 저장(Insert) | TR 수신 DB 업데이트(Update) | 과금/통계(InsqStat, bprintf) | 과금 후 DB 삭제 | 과금 후 후처리(전송/라우팅) | 비고 |
|---|---|---:|---:|---:|---:|---:|---|
| **20, 21** | NOTI_PLUS(안심문자) | ✅ | ✅ | ✅ | ✅ | ❌ | `mt_NOTI_PLUS_Sending()` TODO |
| **90, 91** | NOTI(등기문자) | ✅ | ✅ | ✅ | ✅ | ❌ | `mt_NOTISending()` TODO |
| **36, 37** | ROAMING(CDMA) | ✅ | ✅ | ✅ | ✅ | ❌ | `routeTRMsg2PCS()` TODO |
| **40, 41, 69, 70, 72, 73** | ROAMING(GSM/WCDMA/BIZ) | ✅ | ✅ | ✅ | ✅ | ❌ | `routeTRMsg2PCS()` TODO |
| **38, 42** | ROAMING(FORWARD) | ✅ | ✅ | ⚠️ | ✅ | ❌ | TR 라우팅 분기 누락 가능성(38/42) |
| **그 외(예: 1,57,48 등)** | NORMAL/OTHER | ✅ | ✅ | ✅ | ✅ | ❌ | `routeTRMsg2PCS()` TODO |

---

## 8. ESMClass별 VSTAT/TRC(InsqStat) 저장 패턴 (DB 저장 외)

### 8.1 ESMClass별 “MO → MO-ACK → MO-TR → MO-Report” 타임라인(요약)

표기:
- **VSTAT+TRC**: `LT_BOTH`
- **TRC만**: `LT_TRACE`
- 값 표기: `ERRORID / ST` (주요 코드만)

| ESMClass 그룹 | MO(전송 전/전송 시) | MO-ACK 단계(= SM_REQ_SIMPLE 성격) | MO-TR 단계(= SM_REQ_TRANS_RESULT/ProcessTR 성격) | MO-Report(MSG_STATUS 기반) |
|---|---|---|---|---|
| **20/21 (NOTI_PLUS)** | (공통) 한도초과 시 **VSTAT+TRC**: `ERRORID_CP_MO_LIMIT / ST_GIP_MO_LIMIT`<br>(BILL_TYPE=4) GIVE 한도초과 **VSTAT+TRC**: `ERRORID_CP_GIVEBILL_LIMIT / ST_GIP_MT_LIMIT_GIFT`<br>msgId 없음 **VSTAT+TRC**: `ERRORID_CP_MO_FAIL / ST_GIPEVENT_MO_OK` | ACK 실패 시 **TRC만**: `ERRORID_CP_MO_FAIL / ST_GIP_MORS_FAIL`<br>ACK 성공+과금(bprintf 후) **VSTAT+TRC**: `ERRORID_CP_MO_SUCCESS(15) / ST_GIPEVENT_MOACK_BILL_OK(80)` | 전송 실패/NOT_SEND_OK 시 **VSTAT+TRC**: `ERRORID_CP_MO_TR_FAIL(28) / ST_VBILLMO_DONT_BILL_TRFAIL(-346)` | 성공(2) **VSTAT+TRC**: `ERRORID_CP_MO_TR_SUCCESS(27) / ST_VBILLMO_OK(141)`<br>기타 상태(3/5/14/16/17/19/20/기타) **VSTAT+TRC**: `ERRORID_CP_MO_TR_FAIL(28) / ST_VRECV_TR_* 또는 ST_SMSMOR_TR_* 또는 ST_VBILLMO_DONT_BILL_TRFAIL` |
| **90/91 (NOTI)** | 위 공통과 동일 | 위 공통과 동일 | 위 공통과 동일 | 위 공통과 동일 |
| **36/37 (CDMA ROAMING)** | 위 공통과 동일 | 위 공통과 동일<br>추가: “MO 전송 성공 후 ST_GIPEVENT_MOACK_BILL_OK(TRC만)” 패턴은 **CID 1584 + ESMClass 36 조합이면 스킵(중복 방지)** | 위 공통과 동일 | 위 공통과 동일 |
| **40/41/69/70/72/73 (GSM/WCDMA/BIZ ROAMING)** | 위 공통과 동일 | 위 공통과 동일 | 위 공통과 동일 | 위 공통과 동일 |
| **38/42 (ROAMING FORWARD)** | 위 공통과 동일 | 위 공통과 동일 | 위 공통과 동일 *(단, Java TR 라우팅 분기에 38/42 누락 가능성)* | 위 공통과 동일 |
| **그 외(일반 SMS 등)** | 위 공통과 동일 | 위 공통과 동일 | 위 공통과 동일 | 위 공통과 동일 |

### 8.2 MO-Report(MSG_STATUS) → ST 매핑(참고)

- `MSG_STATUS=2` → `ST_VBILLMO_OK(141)` + `ERRORID_CP_MO_TR_SUCCESS(27)`
- `MSG_STATUS=3` → `ST_VRECV_TR_EXPIRED(-349)` + `ERRORID_CP_MO_TR_FAIL(28)`
- `MSG_STATUS=5` → `ST_VRECV_TR_UNDELIVERED(-350)` + `ERRORID_CP_MO_TR_FAIL(28)`
- `MSG_STATUS=14/16/17/19/20` → `ST_SMSMOR_TR_FWDFAIL / SPAMERR / USERDEL / NPREFIX / ADMCANC` + `ERRORID_CP_MO_TR_FAIL(28)`
- 기타 → `ST_VBILLMO_DONT_BILL_TRFAIL(-346)` + `ERRORID_CP_MO_TR_FAIL(28)`

