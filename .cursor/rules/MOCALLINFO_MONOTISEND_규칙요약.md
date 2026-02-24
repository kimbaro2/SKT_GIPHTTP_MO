# MOCALLINFO / MO_NOTISEND / MOTRBILL 규칙 요약

## 1. 저장 규칙

| 항목 | 조건 | 동작 |
|------|------|------|
| **MOCALLINFO** | MOTRBILL = **Y** | 저장함 |
| **MOCALLINFO** | MOTRBILL = **N** | 저장하지 않음 |
| **MO_NOTISEND** | MOTRBILL = **Y** | 저장함 |
| **MO_NOTISEND** | MOTRBILL = **N** | 저장하지 않음 |

- 핸들러별: NotiRegisteredHandler, NotiPlusHandler 등은 `isMoTrBillEnabled()`(MOTRBILL) 체크 후 MOTRBILL=Y일 때만 insert 수행.
- NormalMoHandler / SmsManager638Handler / SmsMessenger2580Handler 에서 MOCALLINFO(또는 Relay) insert 시에도 동일 규칙 적용 여부는 도메인 정책에 따름.

## 2. MO-TR 분기에서의 활용

- MOCALLINFO 또는 MO_NOTISEND에 저장하는 목적 중 하나는 **MO-TR 구간에서 “어떤 정보인지” 확인**하기 위함.
- MO-TR 수신 시 5키로 MOCALLINFO/MO_NOTISEND 조회·갱신·삭제 후 과금(bprintf) 또는 통계 처리.

## 3. MOTRBILL = N 이고 BillType ≠ 1 인 경우 (유의)

- **MO-TR 과정 없이 MO 단계에서만 과금** 처리해야 함.
- 이 경우 MOCALLINFO/MO_NOTISEND에는 **저장하지 않으므로** MO-ACK 시점에 해당 테이블에 레코드가 없을 수 있음.
- MO 단계에서 과금(bprintf)을 수행하려면 **MOCALLINFO/MONOTISEND에 의존하지 않는 경로**(예: request/access 기반 과금 정보 구성) 또는 별도 데이터 소스 설계가 필요할 수 있음.

## 4. 과금·조회 시 5키 규칙

### 4.1 5키 정의

- **srcCID**, **srcCallNo**, **destCID**, **destCallNo**, **msgId**
- (코드 변수명: `srcCallNoForKey`, `destCallNoForKey`, `cpMsgId` 등으로 동일 5변수 사용)

### 4.2 processMOBilling 조회 순서

1. **MOCALLINFO 5키 조회**  
   - 5키가 모두 비어 있지 않을 때만 `selectGIPMOCallInfo(srcCID, srcCallNo, destCID, destCallNo, msgId)` 호출.
2. **MO_NOTISEND fallback (동일 5키)**  
   - MOCALLINFO 조회 결과가 없을 때, **같은 5키**로 MO_NOTISEND 조회.  
   - 조건: 5키가 모두 비어 있지 않을 때만  
     `findOneForVByMsgAndCidAndCallNo(msgId, srcCID, destCID, srcCallNo, destCallNo)` 호출 (SERVERTYPE='V' 전제).
3. **5키 부족 시**  
   - MOCALLINFO 조회 생략, MO_NOTISEND fallback 조회도 생략 → effectiveMoInfo = null.

- **정리**: MOCALLINFO와 MO_NOTISEND 조회는 **동일 5키**를 사용하며, msgId + SERVERTYPE 만으로 하는 조회는 사용하지 않음.

### 4.3 삭제 시

- MOCALLINFO: 5키로 조회·삭제.
- MO_NOTISEND: 5키(MSGID, SRCCID, DESTCID, SRCCALLNO, DESTCALLNO) + SERVERTYPE='V' 조건으로 삭제 (traceId 미사용 권장).

## 5. 참고 코드 위치

- **processMOBilling**  
  - MOCALLINFO 5키 조회: `SmsResServiceImpl.selectGIPMOCallInfo`  
  - MO_NOTISEND 5키 fallback: `moNotISendRepository.findOneForVByMsgAndCidAndCallNo`
- **5키 조회**  
  - MOCALLINFO: `MOCallInfoRepository.findBySrcAndDestAndMsgId`  
  - MO_NOTISEND: `MONotISendRepository.findOneForVByMsgAndCidAndCallNo`
