# GIPEVENT에서 ESMClass = 20 (NOTI_PLUS_NORMAL_MO) 로직 분석

**기준:** C 코드 `GIPEVENT_c.c`, `GIDBLib.sc`  
**ESMClass 20:** `NOTI_PLUS_NORMAL_MO` (안심문자 일반 MO) — `SmsDef.h` LINE 64

---

## 1. ESMClass 20 정의

| 상수 | 값 | 의미 |
|------|-----|------|
| NOTI_PLUS_NORMAL_MO | 20 | 안심문자 일반 MO |
| NOTI_PLUS_PORTED_MO | 21 | 안심문자 번호이동 MO |

C 코드에서는 **20, 21뿐 아니라 90(NOTI_NORMAL_MO), 91(NOTI_PORTED_MO)** 도 같은 NOTI 경로로 처리한다.

- **저장 위치:** `QITEM.nRsv4Protocol[11]` = ESMClass

---

## 2. MO 전송 시점 (CP로 MO 내보낼 때)

**함수:** `SendTcpMsgSimpleGetQ()` — `GIPEVENT_c.c` **LINE 1508~1665**

### 2.1 호출 경로

1. 메인 루프 `GITCPCMainLoop()` → `ProcessSMRepQ()`  
2. `GetAMsgFromSmsQ(gQNo, &gstQItem)` 으로 MO 1건 조회  
3. `MakePacketFromQItem()` 후 **MO 단문**이면 `SendTcpMsgSimpleGetQ(ptrGIMsgHdr, &nLen)` 호출  
4. `SendTcpMsgSimpleGetQ()` 내부에서 **DB INSERT 분기** (LINE 1618~1665)

### 2.2 DB INSERT 분기 (LINE 1618~1650)

```c
// LINE 1618-1650
if (gBILLTYPE != '1')   // 비과금(HBILL)이 아닐 때만 DB 저장
{ 
    if(gstQItem.nRsv4Protocol[11] == NOTI_PLUS_NORMAL_MO   // 20
            || gstQItem.nRsv4Protocol[11] == NOTI_PLUS_PORTED_MO   // 21
            || gstQItem.nRsv4Protocol[11] == NOTI_NORMAL_MO         // 90
            || gstQItem.nRsv4Protocol[11] == NOTI_PORTED_MO )      // 91
    {
        // ESMClass 20, 21, 90, 91 → MO_NOTISEND 테이블에 INSERT
        if(InsertMO_NOTISEND(ptrGIMsgHdr, &stLog_AddrList, &gstQItem,
                (char *)sSimple.szCB, (char *)&stSegment, hostname) < 0)
        {
            InsqStat(..., ST_DB_NO_DATA_MONOTISEND, ...);
            InsertHistory(..., GIPMOCALLINFO_INSERT_FAIL, ...);
        }
    }
    else 
    {
        // 그 외 ESMClass: Relay MO 또는 일반 MOCALLINFO
        if(gstQItem.nRsv4Protocol[0] != 0 && gstQItem.nRsv4Protocol[1] != 0) {
            DBInsertRelayMOCALLINFO(&gstQItem, gMsgSerialNo);
        }
        else if(InsertGIPMOCallInfo(...) < 0) { ... }
    }
}
else  // gBILLTYPE == '1' (HBILL 비과금)
{
    // HBILL인 경우에도 MOCALLINFO에 INSERT (로그/통계용)
    if (InsertGIPMOCallInfo(...) < 0) { ... }
}
```

**정리:**

- **ESMClass = 20** 이고 **gBILLTYPE != '1'** 이면  
  → **InsertMO_NOTISEND()** 만 호출 (MO_NOTISEND 테이블).
- ESMClass 20이어도 **gBILLTYPE == '1'** 이면  
  → **InsertGIPMOCallInfo()** (MOCALLINFO).

### 2.3 InsertMO_NOTISEND (GIDBLib.sc)

- **SERVERTYPE:** `cServerType = 'V'` (LINE 1455)  
  → MO 전송 시점에는 **MO_NOTISEND에 SERVERTYPE='V'** 로 INSERT.
- ESMClass는 `ptrQItem->nRsv4Protocol[11]` → `nNoti_esmclass` 로 넘겨서 **ESMCLASS 컬럼**에 저장.

---

## 3. TR 수신 시점 (CP가 MO 수신 결과/과금 정보 회신)

**함수:** `ProcessSMRes()` — `GIPEVENT_c.c` **LINE 1951~2293**

### 3.1 메시지 종류

- **MsgSubCode:** `SM_REQ_SIMPLE` (MO 단문 응답)
- `MsgHdrToQItem(&gstQItem, ptrMsgHdr)` 로 헤더 → QItem 반영  
  → **ptrMsgHdr->nRsv4Protocol[11]** 에 ESMClass 유지.

### 3.2 gBILLTYPE == '1' (HBILL) — LINE 2016~2087

- **SelectGIPMOCallInfo()** 로 콜정보 조회 (MOCALLINFO 등).
- RCS TR 필요 시 `InsertIntoSmsQnQNo(&gstQItem, &nQueueNo)`.
- **MOCALLINFO/MO_NOTISEND 업데이트는 이 블록 안에 없음** → HBILL일 때는 DB 업데이트 생략.

### 3.3 공통: 결과 코드 반영 — LINE 2092~2107

- `ptrMsgHdr->ucData[3]` (결과 코드):
  - `GI_RES_NO_ERR` → InsqStat(..., ST_GIPEVENT_MORS_OK, ...)
  - 그 외 → InsqStat(..., ST_GIP_MORS_FAIL, ...), InsertHistory(ACK_RESULT_FAIL)

### 3.4 과금/NOTI 분기 — LINE 2110~2265

```c
if (!gMOTRBILL)   // 즉시 과금 처리
{
    // SelectGIPMOCallInfo() 로 MOCALLINFO 조회
    ret = SelectGIPMOCallInfo(ptrMsgHdr, ...);
    if(ret > 0) {
        // bprintf() 등 과금 전송, InsqStat(ST_GIPEVENT_MOACK_BILL_OK)
        // RCS TR 필요 시 InsertIntoSmsQnQNo()
    }
}
else   // gMOTRBILL (VBILL 등 지연 과금)
{
    if( ptrMsgHdr->nRsv4Protocol[11] == NOTI_PLUS_NORMAL_MO   // 20
            || ptrMsgHdr->nRsv4Protocol[11] == NOTI_PLUS_PORTED_MO   // 21
            || ptrMsgHdr->nRsv4Protocol[11] == NOTI_NORMAL_MO         // 90
            || ptrMsgHdr->nRsv4Protocol[11] == NOTI_PORTED_MO )       // 91
    {
        UpdateMO_NOTISEND(ptrMsgHdr, &stLog_AddrList);   // LINE 2262
    }
    else 
    {
        UpdateGIPMOCallInfo(ptrMsgHdr, &stLog_AddrList);
    }
}
```

**정리:**

- **ESMClass = 20** 이고 **gMOTRBILL != 0** (TR 수신 시 과금을 별도 처리)이면  
  → **UpdateMO_NOTISEND()** 호출.
- ESMClass 20이어도 **gMOTRBILL == 0** 이면  
  → **SelectGIPMOCallInfo()** 로 MOCALLINFO 기준 즉시 과금(bprintf 등), **UpdateMO_NOTISEND 호출 없음.**

즉, C 코드상으로는:

- **즉시 과금(!gMOTRBILL):** MOCALLINFO만 사용 (SelectGIPMOCallInfo).
- **지연 과금(gMOTRBILL):** ESMClass 20/21/90/91 → **UpdateMO_NOTISEND**, 그 외 → **UpdateGIPMOCallInfo**.

### 3.5 UpdateMO_NOTISEND (GIDBLib.sc LINE 1606~)

- **동작:**  
  `SRCCALLNO`, `DESTCID`, `MSGID`, **SERVERTYPE = 'V'** 인 기존 행을 찾아  
  **AckMsgId(ptrMsgHdr->ucData+4)** 를 새 MSGID로 한 행을 **INSERT**.
- **쿼리 요약:**  
  `INSERT INTO MO_NOTISEND (..., SERVERTYPE, MSGID, ...)  
   SELECT NODE, SERVERTYPE, :ackMsgId, ...  
   FROM MO_NOTISEND  
   WHERE SRCCALLNO = ? AND DESTCID = ? AND MSGID = ? AND SERVERTYPE = 'V'`
- C 코드에서는 SELECT 시 **SERVERTYPE을 그대로 가져오므로** 새 행도 **SERVERTYPE='V'** 로 들어감.  
  (규칙 문서에서 말하는 SERVERTYPE='H' 로의 변경은 Java 이식/비즈니스 규격 상의 요구일 수 있음.)

---

## 4. ESMClass 20 요약 표

| 구간 | 조건 | 동작 |
|------|------|------|
| **MO 전송** | gBILLTYPE != '1' && ESMClass == 20 | **InsertMO_NOTISEND** (MO_NOTISEND, SERVERTYPE='V') |
| **MO 전송** | gBILLTYPE == '1' && ESMClass == 20 | **InsertGIPMOCallInfo** (MOCALLINFO) |
| **TR 수신** | gBILLTYPE == '1' | DB 업데이트 없음 (HBILL 전용 처리) |
| **TR 수신** | !gMOTRBILL (즉시 과금) | **SelectGIPMOCallInfo** + bprintf 등, UpdateMO_NOTISEND 호출 안 함 |
| **TR 수신** | gMOTRBILL && ESMClass == 20 | **UpdateMO_NOTISEND** |

---

## 5. C 코드 라인 참조

| 내용 | 파일 | 라인 |
|------|------|------|
| ESMClass 20 상수 | inc/SmsDef.h | 64 (NOTI_PLUS_NORMAL_MO) |
| MO 전송 시 INSERT 분기 | GIPEVENT_c.c | 1618~1650 |
| TR 수신 시 Update 분기 | GIPEVENT_c.c | 2257~2266 |
| InsertMO_NOTISEND | GIPEVENT/GIDBLib.sc | 1374~1595 |
| UpdateMO_NOTISEND | GIPEVENT/GIDBLib.sc | 1606~ |

---

## 6. Java/Kotlin 이식 시 참고

- **BillTypeUtil.kt**  
  - MO INSERT: `shouldInsertMO_NOTISEND(billType, isNotiPlus)`  
    → C의 `gBILLTYPE != '1' && (20|21|90|91)` 에 대응.
  - TR 즉시 과금: `shouldProcessImmediateBilling(billType)`  
    → C의 `gBILLTYPE == '1'` (HBILL) 및 `!gMOTRBILL` 구간과 연계해 해석 필요.
- **ESMClass_Processing_Rules.md**  
  - ESMClass 20, 21, 90, 91 공통 규칙 및 SERVERTYPE 'V'/'H', updateMO_NOTISEND 후 DELETE 등은 위 C 로직과 비교해 이식 규격으로 적용하면 됨.

이 문서는 **GIPEVENT C 코드 기준**으로 ESMClass = 20인 경우의 MO 저장·TR 수신 로직을 정리한 것입니다.
