# GIPEVENT_c.c 로직 분석 정리

**대상 파일:** `smslib.20251201/src/GIPEVENT/GIPEVENT_c.c`  
**역할:** GIP(Gateway Interface Protocol) TCP Child 프로세스 — CP(Content Provider)와 TCP로 SMS MT/MO/TR 메시지를 주고받는 인터페이스

---

## 1. 개요

- **프로세스명:** `GIPEVENT_C_XXXX` (Child ID 4자리)
- **실행:** `GIPEVENT_c [TCP Port] [Child ID] [QNo] [url_flag]`
- **아키텍처:** 부모 프로세스(`GIPEVENT_p`)가 TCP 포트에서 accept 후, 각 연결마다 이 Child 프로세스를 fork하여 **한 TCP 연결당 1 Child**가 MT 수신·MO 발송·TR 처리 수행

---

## 2. main() 초기화 흐름

1. **인자 파싱**  
   TCP Port, Child ID, QNo, url_flag 설정
2. **InitQInfo()**  
   큐 정보 초기화 (실패 시 exit -3)
3. **DBinit()**  
   AltiBase(DB) 연결
4. **DBGetVbillMO_Queue()**  
   VBILL MO 전용 큐 번호 조회
5. **Read_BLOCKMSG_File()**  
   블록/과금제한 알림 메시지 설정
6. **ReadLogTrace() / LprintfInit()**  
   로그 트레이스 주소 목록 로드
7. **ReadConfig()**  
   GIPEVENT Access Child 설정, CPtoCP 리스트, TID, NoSpam, Flow Control 등 일괄 로드
8. **SignalInit()**  
   시그널 핸들러 등록
9. **GITCPCMainLoop()**  
   메인 이벤트 루프 진입 (무한 루프)

---

## 3. ReadConfig() — 설정 로드

- **ReadGIPEVENTAccessChild(gChildId, &gipm)**  
  - 해당 Child ID의 접속 설정 조회  
  - **주요 설정:** `gQNo`, `gFlag`, `gRetryCountMax`, `gTimeOutCount`, `g017Flag`, `gszCId`, `gFX/gFY`(Flow Control), `gPorted_flag`, `gPoll_Qno`, `gMOTRBILL`, `gBILLTYPE`, `gSendLimitCount`, `gAuto_Conv`, `gnGIPVerID`, `gsAuthFlag`, `gKISAVldChkFlag` 등
- **ReadCPtoCPList()**  
  CP to CP 라우팅용 CId–큐 매핑
- **ReadGIPMTID()**  
  차단할 TID 목록
- **ReadPtoPList() / NS_ReadCfg()**  
  PtoP, NoSpam(스팸) 설정
- **Read_AuthCallBackList()**  
  AUTH 사용 시 콜백 허용 목록
- **ReadDB_ConvChar()**  
  문자 변환(TRANSFORM) 리스트 (140바이트/UCS2 등)

---

## 4. GITCPCMainLoop() — 메인 루프

- **select()**  
  - `gTcpSd`(TCP 소켓)에 대한 읽기 대기, 타임아웃 `GITCP_TIMEOUT`
- **주기 처리**
  - **Log Trace 주기:** `TABLE_LOGTRACE_READ_INTERVAL` / `Log_TraceTime` 마다 `ReadLogTrace()` 재조회
  - **Config 주기:** `TABLE_READ_INTERVAL` 마다 `ReadConfig()`, 로그 레벨, MO/MT/GIVE 한도, Call History 설정 갱신
  - **TID 주기:** 600초마다 `ReadGIPMTID()`로 차단 TID 목록 갱신
- **이벤트 분기**
  - **TCP 수신:** `FD_ISSET(gTcpSd)` → `ProcessTcpOfGItcpc(GETATCP_TYPE_TCP_SELECTED)`
  - **버퍼에 미수신 데이터:** `nLeft > 0` → `ProcessTcpOfGItcpc(GETATCP_TYPE_READ_BUF)`
  - **송신 가능 시:** `gMsgSendReady` 이고 `CheckFlowControl()` 통과 시 `ProcessSMRepQ()` 호출  
    - 큐에서 메시지 꺼내 MT/TR 패킷 만들어 IP(CP)로 전송
- **타임아웃/재시도**
  - `gMsgSendReady == 0`인 상태가 `gTimeOutCount` 초과 시 재시도  
  - `gRetryCount > gRetryCountMax`면 `InsertIntoSmsQ(&gstQItem)` 후 `GITCPCExit(7)`
- **고아 프로세스:** `getppid() <= 1` 이면 부모 종료로 간주하고 `GITCPCExit(8)`

---

## 5. ProcessTcpOfGItcpc() — TCP 메시지 수신·분배

- **2단계 수신**
  - **GETTING_HEADER:** `GetATcpMsg()`로 헤더(`GIMSGHDR` - 4바이트 제외) 수신 → `gTcpBufState = GETTING_DATA`
  - **GETTING_DATA:** `uDataLen`만큼 Body 수신, `GIPMTBodySizeCheck()`로 길이 검증 후 **MsgCode 분기**
- **MsgCode 처리**
  - **MSG_CODE_SM_REQ:** `ProcessSMReq(ptrGIMsgHdr)` — CP가 보낸 **MT 요청** 처리
  - **MSG_CODE_SM_RES:** `ProcessSMRes(ptrGIMsgHdr)` — CP가 보낸 **MO 응답** 처리
  - **그 외:** `SendAckNakTcp(..., GI_RES_FORMAT_INVALID_MSG_CODE)` 후 로그
- 처리 후 `gTcpBufState = GETTING_HEADER`, 버퍼 초기화

---

## 6. ProcessSMReq() — MT 요청 (CP → SMSS)

**MsgSubCode별 분기:**

| SubCode | 의미 | 처리 |
|--------|------|------|
| SM_REQ_LINK | 링크 체크 | ACK만 응답 |
| SM_REQ_TRANS_RESULT | 전달 결과(TR) | `ProcessTRVBILLMO()`(과금 시), ACK |
| SM_REQ_SIMPLE | 단문 MT | `ProcessSmReqSimple()` |
| SM_REQ_PORTED | 번호이동 MT | `ProcessSmReqPorted()` |
| 그 외 | 비허용 | `SendAckNakTcp(..., GI_RES_FORMAT_INVALID_MSG_SUBCODE)` |

---

## 7. ProcessSmReqSimple() — MT 단문 처리 (핵심)

- **헤더/바디 → QItem:** `MsgHdrToQItem()`, `MsgBodyToQItem()`
- **검증 및 제한 (실패 시 NAK + 로그/InsqStat/InsertHistory 후 return)**
  - GIP 버전(4.6.2 / 5.x) 일치 여부
  - KISA: `OrigRelayCIDCheck()` (OrigCID/RelayCID)
  - `CIDCheck()` — Source CId 유효
  - `DestMinCheck()` — 수신번호
  - `CheckRgtDlvFlag()` — RgtDlvFlag 50 등
  - `CheckLimitMT()` — MT 한도
  - `CheckLimitGiveBill()` — 선물하기 한도
  - `DBReadSpamList()` — 스팸(콜백) 블록
  - 통신사/번호이동: 010·011·012·017 등 정책, `gAuto_Conv`에 따라 SIMPLE ↔ PORTED 자동 전환
  - Flow Control: `gFX` 패킷 / `gFY` 초 초과 시 `GI_RES_FC_NAK`
  - Auth: `CheckAuthCB()` (콜백 허용 목록)
  - `stQItem.ucMsgLen == 0` → 포맷 오류
  - 메시지 길이/타입: 4.6.2 최대 길이, 5.x `MAX_SHORT_MSG_LEN_MT`, `gnMaxShortMsgLen_462MT` 등
  - `CheckInvalidCID()`, `CheckTID()` 등
- **MMS:** `MMSCtrl()` 호출로 MMS 제어
- **성공 시:** `InsertIntoSmsQtoCP2(&stQItem, nQueueNo)` 로 **SMS 큐에 적재** 후 `SendAckNakTcp(..., GI_RES_NO_ERR)`

---

## 8. ProcessSmReqPorted() — 번호이동 MT

- SIMPLE과 유사한 검증(한도, 스팸, CID, DestMin, RgtDlvFlag, Flow Control 등) 후
- 최종적으로 `InsertIntoSmsQtoCP2()` 로 큐 적재 및 ACK

---

## 9. ProcessSMRes() — MO 응답 (CP → SMSS)

- **MsgSubCode:** `SM_REQ_SIMPLE` (MO 단문 응답) 등
- `MsgHdrToQItem(&gstQItem, ptrMsgHdr)` 로 현재 대기 중인 MO와 매칭
- **과금(gBILLTYPE 등):** `SelectGIPMOCallInfo()` 로 콜정보/트레이스ID/MVNO/RCS 등 조회, 필요 시 별도 큐(RCS TR 등) 적재
- **비과금:** `SelectGIPMOCallInfo()` 로 로그/통계용 정보만 조회
- **결과 반영:** `ptrMsgHdr->ucData[3]`(결과코드)에 따라 `InsqStat()` (성공/실패), `InsertHistory()` (실패 시)
- **상태 정리:** `gMsgSendReady = 1`, `gRetryCount = 0` → 다음 MO 메시지를 큐에서 꺼내 보낼 수 있음

---

## 10. ProcessSMRepQ() — 큐에서 꺼내 CP로 전송 (MO/TR)

- **큐에서 1건 조회:** `GetAMsgFromSmsQ(gQNo, &gstQItem)`  
  - 빈 큐: return 0  
  - 락 실패: return -1
- **Poll 큐:** `SM_REQ_SEND` 이고 `gPoll_Qno != 0` 이면 `InsertIntoSmsQWithQNo(&gstQItem, gPoll_Qno)` 로 Poll 전용 큐에 다시 넣을 수 있음
- **패킷 생성:** `MakePacketFromQItem(&gstQItem, ptrMsgHdr)` → `cSendBuf`에 GIP 패킷 생성
- **SubCode별 전송**
  - **SM_REQ_SIMPLE (MO):**
    - `CheckLimitMO()` 한도 초과 시: 블록 알림(`GIPEVENT_BLK_NOTI`) 발송, RCS TR 큐 적재 등 후 return
    - 한도 통과 시: `SendTcpMsgSimpleGetQ()` → TCP 전송, `PrintHexa(..., "SEND")`
  - **SM_REQ_TRANS_RESULT (TR):** `TransHtoN_GIMSGHDR()` 후 `SendTcpMsg()` 로 전송, `InsqStat()` (TR 성공/실패)
- 전송 성공 시 `gMsgSendReady = gFlag`, `TimeOutAction` 갱신

---

## 11. InsertIntoSmsQtoCP2() — MT 메시지 큐 적재

- **CPtoCP:** `ptrQItem->szCId`가 `stCPtoCPList`에 있으면 `DBGet_QNoFromGIEnqTblWithCid()` 로 큐 번호 조회 후 `InsertIntoSmsQWithQNo(ptrQItem, nQNo)`
- **그 외:** `InsertIntoSmsQnQNo(ptrQItem, nQueueNo)` 또는 `InsertIntoSmsQ(ptrQItem)` (빌드 옵션에 따라)

---

## 12. 송수신/유틸 함수

- **SendTcpMsg():** `cBuf`, `nMsgLen` TCP 전송
- **SendAckNakTcp() / SendAckNakTcpID():** GIP ACK/NAK 응답 (방향: MT_ACK, MO_ACK 등)
- **SendTcpMsgSimpleGetQ():** MO 단문 패킷 조립 후 전송
- **MakePacketFromQItem():** QItem → GIMSGHDR + Body 패킷 생성
- **MsgHdrToQItem() / QItemToMsgHdr():** GIP 헤더 ↔ QItem 상호 변환
- **PrintHeaderBuf() / PrintHexa():** 로그용 헤더/Hex 덤프
- **CheckTID():** 차단 TID 목록에 포함 여부
- **CheckFlowControl():** MO Flow Control (시간당/구간당 전송 수 제한)

---

## 13. 데이터/상태 요약

- **cTCPBuf / cSendBuf / cRecvBuf:** TCP 수신/송신 버퍼
- **gTcpBufState:** GETTING_HEADER / GETTING_DATA
- **gstQItem:** 현재 처리 중인 MO/TR용 QItem (타임아웃 시 재적재용)
- **gMsgSendReady:** 1이면 큐에서 다음 메시지 꺼내 전송 가능, 0이면 CP 응답 대기
- **gQNo:** 이 Child가 사용하는 CP 큐 번호
- **stCPtoCPList:** CId별 CP-to-CP 큐 매핑
- **stBlockNOTI:** 과금/한도 블록 시 발송할 알림 메시지

---

## 14. 흐름 요약

1. **MT (CP → 단말)**  
   CP가 TCP로 `SM_REQ` (SIMPLE/PORTED) 전송 → `ProcessSMReq` → `ProcessSmReqSimple`/`ProcessSmReqPorted`에서 검증 후 `InsertIntoSmsQtoCP2()` 로 큐 적재 → ACK 전송.

2. **MO (단말 → CP)**  
   SMSS가 큐에 MO를 넣어 두면, Child는 `ProcessSMRepQ()`에서 `GetAMsgFromSmsQ()`로 꺼내 `MakePacketFromQItem()` 후 `SendTcpMsgSimpleGetQ()`로 CP에 전송 → CP가 `SM_RES` 로 응답 → `ProcessSMRes()`에서 과금/통계 처리 후 `gMsgSendReady=1`로 다음 MO 전송 가능.

3. **TR (전달결과)**  
   CP가 `SM_REQ` TRANS_RESULT 보내면 `ProcessTRVBILLMO()` 등 처리 후 ACK.  
   SMSS가 TR을 보낼 때는 `ProcessSMRepQ()`에서 `SM_REQ_TRANS_RESULT` 패킷으로 `SendTcpMsg()`.

---

## 15. ESMClass 정의

**출처:** `smslib.20251201/src/inc/SmsDef.h`, `TraceDef.h`, `AlarmDef.h`

### 15.1 개요

- **ESMClass**: SMS 서비스 유형을 구분하는 코드(정수). MO/TR 과금·통계·전달결과(TR) 라우팅 시 사용.
- **QITEM 저장 위치:** `QITEM.nRsv4Protocol[11]` (EsmClass 값), H2100·Noti Send 등에서 사용.
- **MRMQITEM / QITEM** 구조체 주석: `nRsv4Protocol[11] => EsmClass : H2100 : Noti Send value`

### 15.2 SmsDef.h — ESM CLASS 상수 (정수)

| 값 | 상수명 | 비고 |
|----|--------|------|
| 1 | NORMAL_MO | 일반 MO |
| 4 | NORMAL_TR | 일반 TR |
| 6 | KTF_2G_PORTED_MO | KTF 2G 번호이동 MO |
| 9 | LGT_2G_PORTED_MO | LGT 2G 번호이동 MO |
| 10 | KTF_3G_PORTED_MO | KTF 3G 번호이동 MO |
| 11 | LGT_3G_PORTED_MO | LGT 3G 번호이동 MO |
| 12 | KTF_3G_PORTED_CDMA_ROAMING_MO | KTF 3G 번호이동 CDMA 로밍 MO |
| 13 | KTF_3G_PORTED_GSM_ROAMING_MO | KTF 3G 번호이동 GSM 로밍 MO |
| 14 | LGT_3G_PORTED_CDMA_ROAMING_MO | LGT 3G 번호이동 CDMA 로밍 MO |
| 15 | LGT_3G_PORTED_GSM_ROAMING_MO | LGT 3G 번호이동 GSM 로밍 MO |
| 16 | KTF_2G_PORTED_CDMA_ROAMING_MO | KTF 2G 번호이동 CDMA 로밍 MO |
| 17 | LGT_2G_PORTED_CDMA_ROAMING_MO | LGT 2G 번호이동 CDMA 로밍 MO |
| 18 | KTF_2G_PORTED_GSM_ROAMING_MO | KTF 2G 번호이동 GSM 로밍 MO |
| 19 | LGT_2G_PORTED_GSM_ROAMING_MO | LGT 2G 번호이동 GSM 로밍 MO |
| 20 | NOTI_PLUS_NORMAL_MO | 안심문자 일반 MO |
| 21 | NOTI_PLUS_PORTED_MO | 안심문자 번호이동 MO |
| 22 | NOTI_PLUS_PORTED_MT | 안심문자 번호이동 MT (HSMSS) |
| 23 | KTF_2G_NOTI_PLUS_PORTED_MO | KTF 2G 안심문자 번호이동 MO |
| 24 | LGT_2G_NOTI_PLUS_PORTED_MO | LGT 2G 안심문자 번호이동 MO |
| 25 | KTF_3G_NOTI_PLUS_PORTED_MO | KTF 3G 안심문자 번호이동 MO |
| 26 | LGT_3G_NOTI_PLUS_PORTED_MO | LGT 3G 안심문자 번호이동 MO |
| 36 | CDMA_ROAMING | CDMA 로밍 |
| 37 | PORTED_CDMA_ROAMING | 번호이동 CDMA 로밍 |
| 38 | FORWARD_CDMA_ROAMING_MO | 전달 CDMA 로밍 MO |
| 40 | GSM_WCDMA_ROAMING | GSM/WCDMA 로밍 |
| 41 | PORTED_GSM_WCDMA_ROAMING | 번호이동 GSM/WCDMA 로밍 |
| 42 | FORWARD_GSM_ROAMING_MO | 전달 GSM 로밍 MO |
| 48 | FORWARD_MO | 전달 MO |
| 49 | FORWARD_MT | 전달 MT |
| 50 | FORWARD_TR | 전달 TR |
| 52 | FORWARD_PORTED_MT / PORTED_FORWARD_MT | 전달 번호이동 MT |
| 53 | DOUBLE_FORWARD_TR | 이중전달 TR |
| 56 | PORTED_TR | 번호이동 TR |
| 57 | PORTED_MO | 번호이동 MO |
| 64 | NUMBER_PLUS_MO | 넘버플러스 MO |
| 65 | NUMBER_PLUS_TR | 넘버플러스 TR |
| 69 | NUMBER_PLUS_CDMA_ROAMING / NUMBER_PLUS_CDMA_ROAMING_MO | 넘버플러스 CDMA 로밍 MO |
| 70 | NUMBER_PLUS_GSM_ROAMING / NUMBER_PLUS_GSM_ROAMING_MO | 넘버플러스 GSM 로밍 MO |
| 71 | BIZ_NUMBER_MO | 비즈넘버 MO |
| 72 | BIZ_NUMBER_CDMA_ROAMING_MO | 비즈넘버 CDMA 로밍 MO |
| 73 | BIZ_NUMBER_GSM_ROAMING_MO | 비즈넘버 GSM 로밍 MO |
| 80 | BIZ_NUMBER_TR | 비즈넘버 TR |
| 81 | BIZ_NUMBER_MT | 비즈넘버 MT |
| 82 | BIZ_NUMBER_PORTED_MT | 비즈넘버 번호이동 MT |
| 90 | NOTI_NORMAL_MO | 알림 일반 MO |
| 91 | NOTI_PORTED_MO | 알림 번호이동 MO |
| 92 | KTF_2G_NOTI_PORTED_MO | KTF 2G 알림 번호이동 MO |
| 93 | KTF_3G_NOTI_PORTED_MO | KTF 3G 알림 번호이동 MO |
| 94 | LGT_2G_NOTI_PORTED_MO | LGT 2G 알림 번호이동 MO |
| 95 | LGT_3G_NOTI_PORTED_MO | LGT 3G 알림 번호이동 MO |
| 100 | MO_MESSAGE | MO 메시지 (AlarmDef.h) |
| 200 | TR_MESSAGE | TR 메시지 (AlarmDef.h) |

### 15.3 TraceDef.h — ESMClass 관련 상태/문자열

| 상수 | 값 | 설명 |
|------|-----|------|
| ST_CALLFW_INVALID_ESMCLASS | -110 | 잘못된 ESMCLASS (통계/TRC) |
| ST_CALLFW_INVALID_ESM_CLASS | -111 | 잘못된 ESMCLASS (통계, `ptrQItem->nRsv4Protocol[11]` 사용) |
| SZ_ST_CALLFW_INVALID_ESMCLASS | "잘못된ESMCLASS" | 상태 설명 문자열 |
| SZ_ST_CALLFW_INVALID_ESM_CLASS | "잘못된ESMCLASS" | 상태 설명 문자열 |
| ST_SMSMOR_WLONG_ESM | -338 | WLONG TR 관련, ESM_CLASS(`QItem.nRsv4Protocol[11]`) |

InsqStat 등에서 MT/MO/TR 성공 시 **ESM_CLASS** 로 `stQItem.nRsv4Protocol[11]` 을 넘기는 경우: `ST_SMSMOT_OK`(96), `ST_SMSMOR_MO_OK`(98), `ST_SMSMOR_MO_1415_1_OK`(111), `ST_SMSMOR_MO_1415_2_OK`(112), `ST_SMSMOR_MO_NORMAL_OK`(113) 등.

### 15.4 AlarmDef.h

| 상수 | 값 | 설명 |
|------|-----|------|
| INVALID_ESMCLASS | 8 | 전달 TR 시 EsmClass가 맞지 않을 때 알람 처리 |

---

이 문서는 `GIPEVENT_c.c`의 역할과 메인 루프, MT/MO/TR 처리 경로, 설정·큐·타임아웃 동작, ESMClass 정의를 한눈에 보기 위해 정리한 것입니다.
