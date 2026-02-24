# CallTypeRelay enum 적용 후 C·Java 비교 및 MO 시점 bprintf 검토

## 1. Java enum 적용 요약

- **파일**: `dto/jna/CallTypeRelay.kt`
- **상수**:
  - `VSMSS_RELAY_MT` ('5')
  - `HSMSS_RELAY_MT` ('6')
  - `HSMSS_RELAY_MO` ('7')
- **활용**:
  - `CallTypeRelay.isRelaySrcType(srcType)` → C LINE 423 조건 (5/6/7이면 Relay 구간)
  - `CallTypeRelay.shouldCallRelayTrafficBprintf(srcType)` → C LINE 530 조건 (5가 아니면 RelayTraffic bprintf 호출)
- **RelayFlag 로직**: `SmsResServiceImpl.processMOBilling` 내 하드코딩 "5","6","7" 제거, 위 enum 사용으로 통일.

---

## 2. C 코드와 비교

### 2.1 호출 유형(ucAgingCnt / SRC_TYPE) 판단

| 항목 | C (VBILLMO_c.c) | Java (enum 적용 후) |
|------|------------------|----------------------|
| **Relay 구간** | `ucAgingCnt == '5' \|\| '6' \|\| '7'` (LINE 423, 637) | `CallTypeRelay.isRelaySrcType(srcType)` → 동일 의미 |
| **RelayTraffic bprintf** | `RelayFlag=='Y' && ucAgingCnt != CALL_TYPE_VSMSS_RELAY_MT` (LINE 530) | `isRelayFlag && CallTypeRelay.shouldCallRelayTrafficBprintf(srcType)` → 동일 (5 제외, 6·7만) |
| **상수 정의** | 헤더 `CALL_TYPE_VSMSS_RELAY_MT` 등 | `CallTypeRelay.VSMSS_RELAY_MT` 등 |

**결론**: 분기 조건과 의미는 C와 동일. Java만 enum으로 상수 관리.

### 2.2 RelayFlag 결정 방식 (차이 유지)

| 항목 | C | Java |
|------|---|------|
| **RelayFlag='Y'** | ucAgingCnt가 5/6/7이면 무조건 'Y' (조회 실패와 무관) | `isRelaySrcType && relayCidList != null && (RELAY_FLAG_KT==1 \|\| RELAY_FLAG_LGU==1)` |
| **조회 실패 시** | RelayFlag='Y' 유지, Relay bprintf 진입 가능(비유효 데이터 리스크) | RelayFlag='N'으로 일반 bprintf (보수적) |

→ enum 변경과 무관하게, 기존에 문서화한 “RelayFlag 규칙 리스크·방안” 차이는 그대로 유지.

### 2.3 MO_NOTISEND 경로

- **C**: LINE 637–639에서 RelayFlag 재설정, LINE 722에서 `RelayFlag == 'Y'`이면 RelayTraffic NOTISEND CDF bprintf. (ucAgingCnt 추가 조건 없음)
- **Java**: 현재 `processMOBilling`은 MOCALLINFO/MO_NOTISEND 공통으로 같은 RelayFlag·CallTypeRelay 로직 사용. MO_NOTISEND 전용 분기에서도 동일 enum 사용 가능.

---

## 3. MO 시점에서의 bprintf 진행 여부 검토

### 3.1 C (VBILLMO_c.c)

- **bprintf 호출 위치**: `ProcessTR()` 내부 (LINE 364, 371, 528–537, 540–547 등).
- **ProcessTR 호출 경로**: `VBILLCMainLoop` → `ProcessSMRepQ()` → `GetAMsgFromSmsQ(gQNo, &stQItem)` → `ProcessTR(&stQItem)`.
- **의미**: VBILL_MO 큐에 적재된 **MO-TR 결과** 메시지를 디큐한 뒤 처리. 즉, **MO-TR(전달 결과 수신) 시점**에만 ProcessTR이 돌고, 그 안에서만 bprintf 호출.
- **정리**: C에서는 **MO-TR 시점**에서만 bprintf 수행. MO-ACK 단계에서의 bprintf는 없음 (GIPEVENT 등 다른 모듈에서 처리하는 구조라면 별도).

### 3.2 Java (processMOBilling)

- **호출처**:
  - **isMoAckContext = true** (MO-ACK 시점): 각 핸들러의 `recordMoAckBilling` 등에서 호출 (예: NormalMoHandler, NotiPlusHandler, NotiRegisteredHandler 등).
  - **isMoAckContext = false** (MO-TR 시점): `processSMReqTransResult` 흐름 내 TR 처리 후 호출 (예: processTR_NormalMo, processTR_NotiPlus 등).
- **의미**: Java는 **MO-ACK**과 **MO-TR** 두 시점 모두에서 `processMOBilling` → bprintf가 호출될 수 있음. (실제로는 MOTRBILL·BILLTYPE·도메인별 분기로 “언제 호출할지”가 결정됨.)

### 3.3 검토 결론: “MO 시점에서 bprintf 진행” 맞는지 여부

- **C 기준 “MO 시점”**: VBILLMO_c.c 기준으로는 **MO-TR(전달 결과 처리) 시점**만 해당. 여기서 bprintf 진행 **맞음**.
- **Java**:
  - **MO-TR 시점**에서 `processMOBilling(..., isMoAckContext = false)` 호출 시: C와 동일하게 **MO-TR 시점에서 bprintf 진행** → **맞음**.
  - **MO-ACK 시점**에서 `processMOBilling(..., isMoAckContext = true)` 호출 시: C에는 없는 경로. Java는 MOTRBILL=N 등으로 “MO 단계에서 즉시 과금”할 때 이 경로로 bprintf 수행. 즉, **MO 시점(MO-ACK)에서도 bprintf 진행**하는 설계가 Java에 있음 → **의도된 동작**이며, C와는 “시점”이 하나 더 있는 차이.

**요약**  
- **C**: MO-TR 시점에서만 bprintf → “MO 시점(MO-TR)에서 bprintf 진행” **맞음**.  
- **Java**: MO-TR 시점에서 bprintf 진행 **맞고**, 추가로 MO-ACK 시점에서도 (도메인·설정에 따라) bprintf 진행 가능 → 둘 다 “MO 관련 시점”에서의 bprintf이므로, “JAVA 의 경우 MO 시점에서 bprintf 를 위해 진행된다”고 보는 것이 **맞음**. 다만 C와 완전히 같은 시점만 쓰려면 MO-TR 호출 경로만 사용하면 됨.

---

## 4. 차이 정리

| 구분 | C | Java (enum 적용 후) |
|------|---|---------------------|
| **호출 유형 상수** | 매크로/헤더 상수 | CallTypeRelay enum ('5','6','7') |
| **Relay 구간·RelayTraffic 조건** | 동일 논리 | enum 메서드로 동일 구현 |
| **RelayFlag='Y' 조건** | ucAgingCnt만 | relayCidList + RELAY_FLAG_KT/LGU 추가 (기존 정책 유지) |
| **bprintf 호출 시점** | MO-TR만 | MO-ACK 또는 MO-TR (호출처·설정에 따름) |

enum 도입으로 **상수·분기 표현**만 정리되었고, C와의 **기존 정책 차이**(RelayFlag 엄격화, MO-ACK 시 bprintf 가능)는 그대로 유지됩니다.
