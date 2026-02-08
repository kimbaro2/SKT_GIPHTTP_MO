# ESMClass 일반 항목 — C 코드 vs 도메인 클래스 비즈니스 로직 비교표

**기준:** `smslib.20251201/src/GIPEVENT/GIPEVENT_c.c` 일반 항목 흐름 vs 현재 Kotlin 도메인(핸들러 + MoSmReqSimpleProcessServiceImpl) 로직.

**규칙: MO는 subcode를 구분하지 않는다.** (usMsgSubCode / SM_REQ_SIMPLE / SM_REQ_TRANS_RESULT 등 subcode별 분기 없이 MO 처리. 관련 로직 수정은 별도 반영.)

---

## 1. 처리 흐름·분기 위치

| 구분 | C 코드 (GIPEVENT_c.c) | 현재 도메인 (Kotlin) |
|------|------------------------|----------------------|
| **일반 항목 진입** | `ProcessSmReqSimple` 내 단일 함수; ESMClass는 `gstQItem.nRsv4Protocol[11]`로 분기 | `MoServiceTypeResolver.resolve(esmClass, destCID)` → 서비스 타입 → `MoServiceHandlerRegistry.getHandler(serviceType)` |
| **MO 본문(SM_REQ_SIMPLE)** | 한 흐름에서 INSERT 분기 후 SendTcpMsg, InsqStat | `handler.handle()` → `moProcessorOps.processSMReqSimple()` → INSERT 분기·전송·통계 |
| **MO-ACK(SM_REQ_SEND)** | `ProcessSMRes` switch에서 `SM_REQ_SEND` → SelectGIPMOCallInfo / bprintf / InsqStat / Update | `SmsResServiceImpl.processSMReqSimple` (MO-ACK) → SelectGIPMOCallInfo·processMOBilling·Update |
| **MO-TR(SM_REQ_TRANS_RESULT)** | `ProcessSMRes` case `SM_REQ_TRANS_RESULT` → InsqStat ST_GIPEVENT_MTTR_OK | 핸들러 내 `SM_REQ_TRANS_RESULT` 분기 → `moTrSendService.processMoTrResult()` |

---

## 2. DB INSERT (MO 수신 직후)

| 항목 | C 코드 | 현재 도메인 |
|------|--------|-------------|
| **분기 조건** | `gBILLTYPE != '1'` 일 때: `nRsv4Protocol[11]` == NOTI_PLUS_NORMAL_MO 또는 NOTI_PLUS_PORTED_MO 또는 NOTI_NORMAL_MO 또는 NOTI_PORTED_MO | `serviceTypeForInsert == NOTI_PLUS` → InsertMO_NOTISEND<br>`serviceTypeForInsert == NOTI_REGISTERED` → InsertMO_NOTISEND |
| **NOTI 시** | `InsertMO_NOTISEND(..., stSegment, hostname)`<br>실패 시 InsqStat ST_DB_NO_DATA_MONOTISEND | `moDbInsertService.insertMONotISend()`<br>실패 시 InsqStat ST_DB_NO_DATA_MONOTISEND |
| **Relay MO** | `nRsv4Protocol[0]!=0 && nRsv4Protocol[1]!=0` → `DBInsertRelayMOCALLINFO`<br>아니면 `InsertGIPMOCallInfo` | `isRelayMo` (nRsv4Protocol[0],[1]) → `moDbInsertService.insertRelayMOCallInfo()` |
| **일반 MO** | `InsertGIPMOCallInfo(ptrGIMsgHdr, &stLog_AddrList, &gstQItem, szCB)`<br>실패 시 InsqStat ST_DB_INS_FAIL_GIPMOCALLINFO | `moDbInsertService.insertGIPMOCallInfo()`<br>실패 시 InsqStat ST_DB_INS_FAIL_GIPMOCALLINFO |
| **gBILLTYPE=='1'** | RCS 경로; 위 NOTI/Relay 분기 없이 `InsertGIPMOCallInfo`만 수행 (1655~1665) | gBILLTYPE에 따른 INSERT 분기는 동일 개념(NotiPlus/NotiRegistered/Relay/일반) |

---

## 3. MO 전송 성공 통계 (InsqStat)

| 항목 | C 코드 | 현재 도메인 |
|------|--------|-------------|
| **호출 위치** | `SendTcpMsgSimpleGetQ` 마지막, `#ifdef __SMSTRC__` 내 (1768~1769) | MOThreadPool에서 `handler.recordMoSuccessInsqStat(qItem, context)` |
| **통계 코드** | `InsqStat(..., ERRORID_CP_MO_SUCCESS, ST_GIPEVENT_MO_OK, ...)` | 각 핸들러 `recordMoSuccessInsqStat()` → `InsqStat(..., ERRORID_CP_MO_SUCCESS, ST_GIPEVENT_MO_OK, ...)` |
| **일반 항목** | 동일한 InsqStat 한 번 호출 | NormalMoHandler / Character1584Handler / ForwardHandler 등 동일 InsqStat 호출 |

---

## 4. MO-ACK 과금·통계 (ProcessSMRes / SM_REQ_SEND)

| 항목 | C 코드 | 현재 도메인 |
|------|--------|-------------|
| **gBILLTYPE=='1'** | SelectGIPMOCallInfo 후 RCS 전용 처리; InsqStat ST_GIPEVENT_MORS_OK/ST_GIP_MORS_FAIL | SmsResServiceImpl 등에서 billType 분기 후 동일 개념 처리 |
| **!gMOTRBILL** | SelectGIPMOCallInfo → destCID 검사 → bprintf (형식만 다름) → InsqStat ST_GIPEVENT_MOACK_BILL_OK | SelectGIPMOCallInfo 후 processMOBilling(bprintf)·InsqStat ST_GIPEVENT_MOACK_BILL_OK |
| **bprintf 호출 조건** | destCID가 COLORSMS("200") 또는 AVATASMS("1584") → bprintf 포맷 A (uDestCallNo 포함 등)<br>그 외 → bprintf 포맷 B | `handler.shouldSkipBprintf()` == false → bprintf 호출<br>638/2580 핸들러는 `shouldSkipBprintf() == true` → 스킵 |
| **bprintf 스킵** | C에는 “638/2580이면 bprintf 스킵” 분기 없음. 모든 일반 항목에서 bprintf 호출 (포맷만 200/1584 vs 그 외) | SMS_MANAGER_638, SMS_MESSENGER_2580 핸들러는 bprintf 스킵 (비즈니스 규칙 차이 가능) |
| **gMOTRBILL 일 때** | NOTI_PLUS/NOTI_PORTED/NOTI_NORMAL/NOTI_PORTED → UpdateMO_NOTISEND<br>그 외 → UpdateGIPMOCallInfo | ESMClass/서비스 타입에 따라 UpdateMO_NOTISEND vs UpdateGIPMOCallInfo |

---

## 5. QITEM 변수 검증

| 항목 | C 코드 | 현재 도메인 |
|------|--------|-------------|
| **검증 시점** | MO 전송 직전/전후 흐름 내에서 필요한 경우 부분 검증 | ESMClass 확정 후 `handler.checkQItemVariables(qItem, context)` |
| **msgId/traceId** | C에는 “일반 항목 전용” msgId/traceId 필수 검증 블록이 명시적으로 보이지 않음 | NormalMoHandler 등에서 msgId 비어 있음 → InsqStat(ERRORID_CP_MO_FAIL, ST_GIPEVENT_MO_OK) 후 false<br>traceId 없음 → 동일 InsqStat 후 false |

---

## 6. 특번·번호 규칙 (1584 / 638 / 2580 / #)

| 항목 | C 코드 | 현재 도메인 |
|------|--------|-------------|
| **분기** | ESMClass로 “일반 vs NOTI”만 구분; 1584/638/2580는 destCID로 bprintf 포맷만 구분 (2186) | `MoServiceTypeResolver`: destCID 선행 적용 → 1584→CHARACTER_1584, 638→SMS_MANAGER_638, 2580→SMS_MESSENGER_2580, #→SPECIAL_SHARP |
| **638 메시지 길이** | `MakePacketFromQItem`: `szCId` "638" 3자 → nMaxShortMsgLen462MO = 128, 그 외 80 (1261~1264) | 서비스 타입별 핸들러만 분리; 638 전용 메시지 길이 로직은 MoSmReqSimpleProcessServiceImpl/전송 레이어에서 처리 여부 확인 필요 |

---

## 7. MO-TR (SM_REQ_TRANS_RESULT)

| 항목 | C 코드 | 현재 도메인 |
|------|--------|-------------|
| **처리** | ProcessSMRes case SM_REQ_TRANS_RESULT → InsqStat(ST_GIPEVENT_MTTR_OK) 등 | 핸들러 `handle()` 내 `usMsgSubCode == SM_REQ_TRANS_RESULT` → `moBillTypeService.isMoTrBillEnabled` 체크 후 `moTrSendService.processMoTrResult()` |
| **일반 항목** | TR 수신 시 별도 ESMClass 분기 없이 동일 InsqStat | NormalMoHandler/ForwardHandler 등 동일하게 MOTRBILL 체크 후 processMoTrResult 위임 |

---

## 8. 요약 대응표 (일반 항목 기준)

| 비즈니스 기능 | C 코드 위치 (라인 근사) | Kotlin 도메인 위치 |
|---------------|-------------------------|---------------------|
| INSERT 테이블 선택 (NOTI vs GIPMOCallInfo vs Relay) | 1619~1665 | MoSmReqSimpleProcessServiceImpl (isNotiPlusType, isNotiType, isRelayMo 분기) |
| InsertGIPMOCallInfo | 1643, 1657, 1655~1665 | moDbInsertService.insertGIPMOCallInfo() |
| MO 전송 성공 InsqStat (ST_GIPEVENT_MO_OK) | 1768~1769 | handler.recordMoSuccessInsqStat() |
| MO-ACK SelectGIPMOCallInfo | 2026, 2145 | processSMReqSimple (MO-ACK) 내 SelectGIPMOCallInfo 호출 |
| MO-ACK bprintf | 2186~2216 | processMOBilling; handler.shouldSkipBprintf() == false일 때만 호출 |
| MO-ACK InsqStat (ST_GIPEVENT_MOACK_BILL_OK) | 2218~2220 | handler.recordMoAckBilling() |
| MO-ACK Update (NOTISEND vs GIPMOCallInfo) | 2257~2266 | ESMClass/서비스 타입에 따른 Update 분기 |
| msgId/traceId 검증 | C에는 명시적 블록 없음 | handler.checkQItemVariables() |
| 638 메시지 길이(128 vs 80) | 1261~1264 | 별도 확인 필요 (전송/패킷 생성 레이어) |

---

**참고:** C 코드의 COLORSMS는 `"200"`(3자), AVATASMS는 `"1584"`(4자)로 2580은 "200" 접두사로 구분됨. 도메인에서는 2580 전용 서비스 타입(SMS_MESSENGER_2580)으로 분리하고 bprintf는 스킵하도록 되어 있어, C와 정책이 다를 수 있음.
