---
name: InsqStat 문서화
overview: SKT_GIPHTTP_MO 프로젝트에서 파악된 InsqStat 호출을 정리한 문서를 작성합니다. API 시그니처, 파라미터 의미, SmsResServiceImpl.kt 내 모든 활성/비활성 호출 위치·조건·목적을 함수별로 정리합니다.
todos: []
isProject: false
---

호출 목적 및 기능 정의
# InsqStat

## 1. 문서 목적 및 범위

- **대상**: [SmsResServiceImpl.kt](D:\dykim\project\SKT_GIPHTTP_MO\src\main\java\com\infra\mo\skt_giphttp_mo\service\impl\SmsResServiceImpl.kt) 내 InsqStat 호출 전수
- **참고 문서**: [BILL_TYPE_InsqStat_ANALYSIS.md](D:\dykim\project\SKT_GIPHTTP_MO\BILL_TYPE_InsqStat_ANALYSIS.md)(BILL_TYPE별 C/Java 매핑), [C_JAVA_기능_이식_체크.md](D:\dykim\project\SKT_GIPHTTP_MO\C_JAVA_기능_이식_체크.md), [msgStatus_체크_비교.md](D:\dykim\project\SKT_GIPHTTP_MO\msgStatus_체크_비교.md)
- **상수 정의**: [SmsDef.kt](D:\dykim\project\SKT_GIPHTTP_MO\src\main\java\com\infra\mo\skt_giphttp_mo\dto\jna\SmsDef.kt)(ERRORID/ST_GIP*/MODULEID/SERVICEID), [TraceDef.kt](D:\dykim\project\SKT_GIPHTTP_MO\src\main\java\com\infra\mo\skt_giphttp_mo\dto\jna\TraceDef.kt)(ST_VBILLMO_*, ST_VRECV_TR_*, ST_SMSMOR_TR_*)

## 2. 문서에 넣을 내용 구조

### 2.1 InsqStat API 개요

- **시그니처** (RECV [5.mdc](D:\dykim\project\SKT_GIPHTTP_RECV\.cursor\rules\5.mdc) / SmsQLib 기준):
- `InsqStat(ptrQItem, nMessageType, nSMSCNo, nServerID, ModuleID, nServiceID, nErrorID, nStatusNo, nInforNo, nTidSaveFlag, nLogType, nLineNo)`
- **주요 파라미터**:
- **nErrorID**: VSTAT 파일에 기록되는 값(VSTAT 코드). 예: 15=MO-ACK 성공, 16=MO 실패, 27=MO-TR 과금 성공
- **nStatusNo (STATID)**: 통계 구분 코드. 예: ST_GIPEVENT_MOACK_BILL_OK(80), ST_VBILLMO_OK(141)
- **nLogType**: `LT_TRACE`(0)=TRC 파일만, `LT_BOTH`(1)=VSTAT + TRC 모두 기록

### 2.2 InsqStat 호출 라인 표기 및 라인별 기능 요약

**SmsResServiceImpl.kt** 내 모든 InsqStat 호출 위치(라인)와, 각 라인이 수행하는 로직을 한 줄로 요약한 목록입니다.

#### 활성 호출 (실제 실행됨)

| 라인 | 상위 함수 | 기능 요약 (해당 라인이 수행하는 로직) |
|------|-----------|--------------------------------------|
| **200** | recordVstat15AfterBprintf | MO-ACK 성공 시 bprintf 호출 후, VSTAT 15와 TRC를 동시에 기록하여 과금 성공(MOACK) 통계를 남김. |
| **589** | processSMReqSimple | MO-ACK 실패(ackResult≠0) 시 TRC에만 기록하여 MO RES 실패 통계를 남김(VSTAT 중복 방지). |
| **939** | processMOBilling | 과금 데이터 조회 후 목적지 CID(DBGet_GIENQ_CID) 검증 실패 시 Invalid CID 통계를 TRC에만 기록. |
| **1507** | processMOBilling | MOCALLINFO 조회 결과가 없을 때(effectiveMoInfo==null) ORA_NODATA 통계를 VSTAT+TRC에 기록. |
| **1927** | processSMReqTransResult | MO-TR 결과 수신 시 전송 성공(2)이 아닐 때, 미과금/실패(TRFAIL) 통계를 VSTAT+TRC에 기록. |
| **3211** | processTR_Roaming_GSM | GSM/WCDMA/BIZ 로밍 TR에서 MOTRBILL='N'이거나 msgStatus≠2일 때, 인자로 받은 errorId/statId로 VSTAT+TRC 기록. |
| **3266** | processTR_Normal | 일반 SMS TR에서 MOTRBILL='N'이거나 msgStatus≠2일 때, 인자로 받은 errorId/statId로 VSTAT+TRC 기록. |
| **3454** | processMoReportDelivered | MO-Report 수신 결과 전송 성공(msgStatus=2) 시 MO-TR 과금 성공(VSTAT 27, ST_VBILLMO_OK)을 VSTAT+TRC에 기록. |
| **3489** | processMoReportExpired | MSG_STATUS=3(만료) 시 TR 만료 통계(ERRORID_CP_MO_TR_FAIL, ST_VRECV_TR_EXPIRED)를 VSTAT+TRC에 기록. |
| **3523** | processMoReportUndelivered | MSG_STATUS=5(전송 불가) 시 TR 전송 불가 통계(ST_VRECV_TR_UNDELIVERED)를 VSTAT+TRC에 기록. |
| **3557** | processMoReportFwdFail | MSG_STATUS=14(전달 실패) 시 TR 전달 실패 통계(ST_SMSMOR_TR_FWDFAIL)를 VSTAT+TRC에 기록. |
| **3590** | processMoReportSpamErr | MSG_STATUS=16(스팸 오류) 시 TR 스팸 오류 통계(ST_SMSMOR_TR_SPAMERR)를 VSTAT+TRC에 기록. |
| **3623** | processMoReportUserDel | MSG_STATUS=17(사용자 삭제) 시 TR 사용자 삭제 통계(ST_SMSMOR_TR_USERDEL)를 VSTAT+TRC에 기록. |
| **3656** | processMoReportNPrefix | MSG_STATUS=19(NP Prefix 오류) 시 TR NPREFIX 통계(ST_SMSMOR_TR_NPREFIX)를 VSTAT+TRC에 기록. |
| **3689** | processMoReportAdmCanc | MSG_STATUS=20(관리자 취소) 시 TR 관리자 취소 통계(ST_SMSMOR_TR_ADMCANC)를 VSTAT+TRC에 기록. |
| **3723** | processMoReportOther | 정의되지 않은 기타 msgStatus일 때 MO-TR 미과금/실패(ST_VBILLMO_DONT_BILL_TRFAIL) 통계를 VSTAT+TRC에 기록. |

#### 비활성 호출 (주석 처리됨)

| 라인 | 상위 함수 | 기능 요약 (원래 의도된 로직) |
|------|-----------|------------------------------|
| **1196** | processMOBilling | MO-ACK 단계 + MOTRBILL='N' + 문자메신저 서비스일 때 MODULEID_GIPEVENT_C로 성공(MO) 통계(ST_GIPEVENT_MO_OK) TRC만 기록 — 현재 주석. |
| **1256** | processMOBilling | MO-ACK 단계 + MOTRBILL='N' + 문자매니저 서비스일 때 동일 패턴(ST_GIPEVENT_MO_OK) — 현재 주석. |
| **1394** | processMOBilling | MOTRBILL='Y'이고 MO-TR 단계에서 effectiveMoInfo 있을 때, MOCALLINFO/MO_NOTISEND 구분에 따라 errorId/statTraceId로 과금 성공 통계 한 번 기록 — 현재 주석, qItem 스왑만 유지. |
| **3039** | processTR_NotiPlus | 안심문자 TR에서 MOTRBILL='N' 또는 msgStatus≠2일 때 VSTAT 기록 — 주석. |
| **3094** | processTR_Roaming_CDMA | CDMA 로밍 TR에서 동일 조건 시 VSTAT 기록 — 주석. |
| **3156** | processTR_Roaming_GSM | GSM 로밍 TR에서 동일 조건 시 VSTAT 기록 — 주석(실제 활성 호출은 3211 라인에서 동일 함수 내 다른 분기). |

#### 파라미터 요약 (활성 호출 기준)

| 라인 | nErrorID(VSTAT) | nStatusNo(STATID) | nLogType |
|------|-----------------|-------------------|----------|
| 200 | ERRORID_CP_MO_SUCCESS(15) | ST_GIPEVENT_MOACK_BILL_OK(80) | LT_BOTH |
| 589 | ERRORID_CP_MO_FAIL(16) | ST_GIP_MORS_FAIL(-154) | LT_TRACE |
| 939 | ERRORID_CP_MO_FAIL(16) | ST_GIP_INVALID_CID(-144) | LT_TRACE |
| 1507 | ERRORID_CP_MO_NODATA(25) | ST_DB_NO_DATA_GIPMOCALLINFO(-125) | LT_BOTH |
| 1927 | ERRORID_CP_MO_TR_FAIL(28) | ST_VBILLMO_DONT_BILL_TRFAIL(-346) | LT_BOTH |
| 3211, 3266 | errorId/statId 인자 | (호출부에서 전달) | LT_BOTH |
| 3454 | ERRORID_CP_MO_TR_SUCCESS(27) | ST_VBILLMO_OK(141) | LT_BOTH |
| 3489 | ERRORID_CP_MO_TR_FAIL(28) | ST_VRECV_TR_EXPIRED(-349) | LT_BOTH |
| 3523 | ERRORID_CP_MO_TR_FAIL(28) | ST_VRECV_TR_UNDELIVERED(-350) | LT_BOTH |
| 3557 | ERRORID_CP_MO_TR_FAIL(28) | ST_SMSMOR_TR_FWDFAIL | LT_BOTH |
| 3590 | ERRORID_CP_MO_TR_FAIL(28) | ST_SMSMOR_TR_SPAMERR | LT_BOTH |
| 3623 | ERRORID_CP_MO_TR_FAIL(28) | ST_SMSMOR_TR_USERDEL | LT_BOTH |
| 3656 | ERRORID_CP_MO_TR_FAIL(28) | ST_SMSMOR_TR_NPREFIX | LT_BOTH |
| 3689 | ERRORID_CP_MO_TR_FAIL(28) | ST_SMSMOR_TR_ADMCANC | LT_BOTH |
| 3723 | ERRORID_CP_MO_TR_FAIL(28) | ST_VBILLMO_DONT_BILL_TRFAIL(-346) | LT_BOTH |

### 2.3 InsqStat 스킵/중복 방지 로직

- **shouldSkipInsqStat** (processMOBilling): `isMOACK == true`이면 InsqStat 호출 스킵. MO-ACK 단계는 processSMReqSimple에서 이미 InsqStat/VSTAT 처리했기 때문.
- **recordVstat15AfterBprintf**: MO-ACK 성공 시에만 VSTAT 15 기록; processMOBilling 내 bprintf 호출 후 별도 함수로 한 번만 호출.

### 2.4 로그 타입(LT_*) 사용 정책

- **LT_BOTH**: VSTAT에 반영이 필요한 경우(과금 성공 15, ORA_NODATA, MO-TR 성공/실패 등).
- **LT_TRACE**: TRC만 남기고 VSTAT 중복/불필요 기록을 막는 경우(ACK 실패, Invalid CID 등). 주석에 “processMOBilling에서만 TRC 생성”이라도 되어 있어 일관성 유지 목적.

### 2.5 BILL_TYPE별 Java 코드 동작 및 구현 내용 요약

Java 코드에서 BILL_TYPE 값에 따라 달라지는 처리 흐름과 InsqStat 호출 여부를 정리합니다.

#### BILL_TYPE='1' (NOT - 비과금)

**처리 흐름:**
- **processSMReqSimple** (라인 477-534): `billType == "1" && !gMOTRBILL`이면 MOCALLINFO/MO_NOTISEND 레코드 삭제만 수행하고 과금 처리(processMOBilling) 스킵. MOTRBILL 값이 null이면 조기 삭제하지 않고 return(데이터 유실 방지).
- **processSMReqTransResult** (라인 1800-1852): `gBILLTYPE == '1'`이면 MOCALLINFO/MO_NOTISEND 레코드 삭제만 수행하고 과금 처리 스킵.
- **processMoReport** (라인 3322-3339): `gBILLTYPE == '1'`이면 MOCALLINFO 레코드 삭제만 수행하고 과금 처리 스킵.

**InsqStat 호출:**
- ❌ MO-ACK 성공/실패 시 InsqStat 호출 없음 (과금 처리 자체가 스킵됨)
- ✅ **라인 1507**: `effectiveMoInfo == null`일 때만 ORA_NODATA 통계 기록 (processMOBilling 진입 전 조회 실패 케이스)
- ❌ MO-TR 단계에서 InsqStat 호출 없음 (과금 처리 스킵)

**특징:**
- bprintf(과금 데이터 출력) 호출 없음
- 통계 기록 최소화 (ORA_NODATA 케이스만)

#### BILL_TYPE='2' (SRC - 발신자 과금)

**처리 흐름:**
- **MOThreadPool** (라인 720-721): MO 메시지 전송 전 `checkLimitMO()` 호출하여 발신자 한도 체크. 한도 초과 시 InsqStat 호출(한도 초과 통계).
- **processSMReqSimple** (라인 538 이후): `billType != "1"` 분기로 진입. ACK 성공/실패에 따라 InsqStat 호출.
  - **라인 589**: ACK 실패 시 MO RES 실패 통계(LT_TRACE)
- **processMOBilling** (라인 634 이후): MOTRBILL='N'이면 즉시 과금 처리(bprintf 호출, VSTAT 15 기록).
  - **라인 200**: MO-ACK 성공 시 bprintf 후 VSTAT 15 기록(LT_BOTH)
  - **라인 939**: Invalid CID 시 통계 기록(LT_TRACE)
  - **라인 1507**: ORA_NODATA 시 통계 기록(LT_BOTH)
- **processSMReqTransResult** (라인 1888 이후): MOTRBILL='Y'이면 processMOBilling 호출하여 MO-TR 과금 처리.
  - **라인 1927**: msgStatus != SEND_OK(2)일 때 미과금/실패 통계 기록

**InsqStat 호출:**
- ✅ 한도 초과 시: ERRORID_CP_MO_LIMIT, ST_GIP_MO_LIMIT (MOThreadPool)
- ✅ MO-ACK 실패: ERRORID_CP_MO_FAIL, ST_GIP_MORS_FAIL (라인 589, LT_TRACE)
- ✅ MO-ACK 성공(과금): ERRORID_CP_MO_SUCCESS(15), ST_GIPEVENT_MOACK_BILL_OK (라인 200, LT_BOTH)
- ✅ Invalid CID: ERRORID_CP_MO_FAIL, ST_GIP_INVALID_CID (라인 939, LT_TRACE)
- ✅ ORA_NODATA: ERRORID_CP_MO_NODATA, ST_DB_NO_DATA_GIPMOCALLINFO (라인 1507, LT_BOTH)
- ✅ MO-TR 실패: ERRORID_CP_MO_TR_FAIL, ST_VBILLMO_DONT_BILL_TRFAIL (라인 1927, LT_BOTH)

**특징:**
- 발신자 한도 체크 수행
- 즉시 과금(MOTRBILL='N') 또는 지연 과금(MOTRBILL='Y') 모두 지원
- 과금 데이터 출력(bprintf) 및 통계 기록 수행

#### BILL_TYPE='4' (GIVE - 선물 과금)

**처리 흐름:**
- **MOThreadPool** (라인 720-768): `checkLimitMO()` 호출 후, BILL_TYPE='4'이면 추가로 `checkLimitGIVE()` 호출하여 선물 한도 체크. 한도 초과 시 InsqStat 호출.
- **나머지 처리**: BILL_TYPE='2'와 동일한 흐름(processSMReqSimple, processMOBilling, processSMReqTransResult).

**InsqStat 호출:**
- ✅ 한도 초과 시: ERRORID_CP_MO_LIMIT, ST_GIP_MO_LIMIT (MOThreadPool, checkLimitGIVE 결과)
- ✅ MO-ACK/MO-TR 처리: BILL_TYPE='2'와 동일한 InsqStat 호출 패턴

**특징:**
- **Java 프로젝트 특화**: C 코드에서는 MO 경로에서 `CheckLimitGiveBill()` 호출되지 않으나, Java에서는 `checkLimitMO()` 후 `checkLimitGIVE()` 추가 호출
- 발신자 한도 + 선물 한도 이중 체크
- 과금 처리 및 통계 기록은 BILL_TYPE='2'와 동일

#### BILL_TYPE='5' (CNT - 비과금 정산용)

**처리 흐름:**
- **MOThreadPool**: 한도 체크 함수 호출 없음
- **processSMReqSimple**: BILL_TYPE='2'와 동일한 흐름(ACK 성공/실패 InsqStat 호출)
- **processMOBilling**: 과금 데이터 출력(bprintf)은 수행하나 실제 과금은 하지 않음(정산용 데이터만 기록)
- **processSMReqTransResult**: MO-TR 과금 처리 수행

**InsqStat 호출:**
- ❌ 한도 체크 관련 InsqStat 호출 없음
- ✅ MO-ACK/MO-TR 처리: BILL_TYPE='2', '4'와 동일한 InsqStat 호출 패턴

**특징:**
- 한도 체크 없음
- 과금 데이터 출력(bprintf)은 수행하나 실제 과금은 하지 않음
- 통계 기록은 BILL_TYPE='2', '4'와 동일 (정산용 데이터 수집 목적)

#### BILL_TYPE별 비교 요약

| BILL_TYPE | 한도 체크 | 과금 처리 | bprintf 호출 | InsqStat 호출 패턴 |
|-----------|-----------|-----------|--------------|-------------------|
| '1' (NOT) | ❌ | ❌ | ❌ | 최소화(ORA_NODATA만) |
| '2' (SRC) | ✅ checkLimitMO | ✅ | ✅ | 전체 패턴 |
| '4' (GIVE) | ✅ checkLimitMO + checkLimitGIVE | ✅ | ✅ | 전체 패턴 |
| '5' (CNT) | ❌ | ❌ (정산용만) | ✅ | 전체 패턴 |

**공통 사항:**
- BILL_TYPE='2', '4', '5' 모두 MO-ACK/MO-TR 단계에서 동일한 InsqStat 호출 패턴 사용
- BILL_TYPE='1'만 과금 처리 및 대부분의 InsqStat 호출 스킵
- MOTRBILL 값에 따라 즉시 과금(MOTRBILL='N') 또는 지연 과금(MOTRBILL='Y') 결정

### 2.6 다른 프로젝트 참고

- **SKT_GIPHTTP_RECV**: SmsSendService.insertStatistics, RECVThreadPool(InsertIntoSmsQWithQNo 성공/실패 시), SmsController 등에서 InsqStat 호출. MO 프로젝트와 동일한 SmsQLib API 사용.

## 3. 산출물

- **파일명**: `InsqStat_문서화.md` (또는 기존 `BILL_TYPE_InsqStat_ANALYSIS.md` 확장 여부는 선택)
- **위치**: SKT_GIPHTTP_MO 루트
- **형식**: 마크다운, 표·목차 사용. 필요 시 C 코드 라인 번호(LINE 2219-2220 등) 참조 유지.

## 4. 구현 시 참고 사항

- ST_SMSMOR_TR_FWDFAIL, ST_SMSMOR_TR_SPAMERR, ST_SMSMOR_TR_USERDEL 등은 TraceDef.kt 또는 SmsDef.kt에 정의되어 있는지 한 번 더 확인 후 문서에 값 기입.
- 주석 처리된 InsqStat 블록은 “향후 복구 시 참고용”으로 구분해 적어 두면 유지보수에 도움이 됨.