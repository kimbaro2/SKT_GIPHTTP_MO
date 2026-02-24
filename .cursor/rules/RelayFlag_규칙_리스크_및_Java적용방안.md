# RelayFlag 규칙 적용 리스크 탐색 및 Java 적용 방안

NPPrefix(RelayNpPrefix)와 유사한 관점에서 RelayFlag 규칙 적용 시 리스크를 정리하고, Java 적용 방안을 제시합니다.

---

## 1. NPPrefix 규칙 정리 (참조 기준)

### 1.1 C 코드 동작

| 항목 | 내용 |
|------|------|
| **설정** | `RelayNpPrefix = GetRelayCidInfoByQueueNo(...)` — Relay 타입(5/6/7)일 때만 호출, 실패 시 0 유지 |
| **초기값** | `int RelayNpPrefix = 0` (LINE 408) |
| **사용 규칙** | bprintf 시 `RelayNpPrefix == 0 ? 11 : RelayNpPrefix` (LINE 536) |
| **의미** | 0이면 **기본값 11** 사용 — CDR에 유효한 NP_PREFIX가 들어가도록 하는 안전 규칙 |

### 1.2 리스크와 완화

- **리스크**: GetRelayCidInfoByQueueNo 실패 또는 미호출 시 RelayNpPrefix가 0인데, 그대로 쓰면 CDR/정산 오류 가능.
- **완화**: C·Java 모두 **0 → 11** 규칙 적용으로 동일. Java는 `relayCidList?.npPrefix ?: 0` 후 `if (relayNpPrefix == 0) 11 else relayNpPrefix` 사용.

---

## 2. RelayFlag 규칙 적용 시 리스크 탐색

### 2.1 C 코드의 RelayFlag 규칙

- **설정**: `ucAgingCnt == 5 || 6 || 7` 이면 **무조건** `RelayFlag = 'Y'`.
- **GetRelayCidInfoByQueueNo**: 같은 블록에서 호출하지만, **반환값/성공 여부와 RelayFlag는 무관**. 실패해도 RelayFlag는 'Y' 유지.
- **사용**: `RelayFlag == 'Y' && ucAgingCnt != 5` 일 때 RelayTraffic bprintf 호출. 이때 **RelayCidCode, RelayNpPrefix** 사용.

### 2.2 C 측 리스크

| 시나리오 | RelayFlag | Relay 데이터 | 결과·리스크 |
|----------|-----------|--------------|-------------|
| ucAgingCnt 6/7, GetRelayCidInfoByQueueNo **성공** | 'Y' | RelayCidCode·RelayNpPrefix 유효 | 정상 RelayTraffic bprintf |
| ucAgingCnt 6/7, GetRelayCidInfoByQueueNo **실패** (해당 queue 없음) | 'Y' | RelayNpPrefix=0(초기값), RelayCidCode **미설정/쓰레기** | **RelayTraffic bprintf 진입** → 잘못된/빈 RelayCidCode가 CDR에 기록될 수 있음 |
| ucAgingCnt 5 | 'Y' | 조회함 | RelayTraffic bprintf는 **진입 안 함** (ucAgingCnt != 5 조건), 일반 bprintf만 호출 |

즉, C는 **RelayFlag만 보고** Relay 경로를 타기 때문에, **조회 실패 시에도 Relay 블록에 들어가고**, 이때 RelayCidCode/RelayNpPrefix가 비유효할 수 있는 리스크가 있습니다. NPPrefix는 0→11로 완화했지만, **RelayCidCode에 대한 동일한 “기본값” 규칙은 C에 없음**.

### 2.3 Java 현재 정책 (보수적 적용)

| 항목 | C | Java (현재) |
|------|---|-------------|
| RelayFlag='Y' 조건 | ucAgingCnt 5/6/7 | ucAgingCnt(5/6/7) **그리고** relayCidList != null **그리고** (RELAY_FLAG_KT==1 \|\| RELAY_FLAG_LGU==1) |
| 조회 실패 시 | RelayFlag='Y' 유지, Relay bprintf 가능(비유효 데이터 리스크) | RelayFlag='N'으로 간주 → **일반 bprintf만** 호출 |

- **장점**: RelayTraffic bprintf는 **유효한 CFG_RELAY_CID_LIST 레코드가 있을 때만** 호출되므로, NPE·빈 RelayCidCode로 인한 CDR 오류를 원천적으로 줄임.
- **차이**: C와 **동작 불일치**. C에서는 “조회 실패해도 RelayFlag='Y'이고, 6/7이면 Relay 포맷 bprintf”가 나갈 수 있음(비록 데이터는 비유효할 수 있음). Java는 그 경우 **일반 bprintf**로만 감.

### 2.4 RelayFlag를 “C와 동일”하게만 적용할 때 리스크

Java에서 **RelayFlag = isRelaySrcType 만** 사용(relayCidList·RELAY_FLAG_KT/LGU 무시)하면:

| 리스크 | 내용 | 심각도 |
|--------|------|--------|
| **NPE** | relayCidList가 null인데 relayCidCode/relayNpPrefix 접근 | 코드상 `relayCidList?.moBillCid ?: ""` 등으로 회피 가능하나, relayCidCode="" 로 bprintf 호출 |
| **빈/잘못된 CDR** | RelayTraffic bprintf에 relayCidCode="", relayNpPrefix=0 전달 → 정산·추적 오류 | 중 |
| **정책 불명확** | “C와 완전 동일” vs “실제 사용 가능할 때만 Relay” 중 어떤 것이 운영 정책인지 혼동 | 낮음 |

NPPrefix처럼 **기본값 규칙**만 적용한다고 해도, RelayCidCode에 대한 합의된 “기본값”이 없으면 동일 리스크가 남습니다.

---

## 3. 리스크 요약 (NPPrefix vs RelayFlag)

| 구분 | NPPrefix (RelayNpPrefix) | RelayFlag |
|------|---------------------------|-----------|
| **규칙** | 0 → 11 사용 | C: ucAgingCnt 5/6/7 → 'Y'. Java: 여기에 relayCidList·KT/LGU 추가 조건 |
| **조회 실패 시** | 0으로 남음 → 11로 치환하여 사용 | C: RelayFlag='Y' 유지 → 비유효 데이터로 Relay bprintf 가능. Java: RelayFlag='N'으로 일반 bprintf |
| **리스크** | 0을 그대로 쓰지 않고 11로 통일해 완화됨 | C: 잘못된 RelayCidCode CDR. Java: C와 동작 차이(과금 경로 분기 다름) |
| **Java 적용 상태** | 0→11 동일 적용, 리스크 낮음 | 보수적 적용으로 CDR 리스크는 낮으나, C 동작과 정책 선택 필요 |

---

## 4. Java 적용 방안

### 4.1 목표

- NPPrefix처럼 **명확한 규칙**으로 RelayFlag를 다루고,
- **조회 실패/비유효 데이터** 시 리스크를 제한하면서,
- **C와의 정책 일치 여부**를 선택 가능하게 한다.

### 4.2 방안 A: 현행 유지 (권장 기본)

- **규칙**: RelayFlag = `isRelaySrcType && relayCidList != null && (relayFlagKt == 1 || relayFlagLgu == 1)`.
- **NPPrefix**: 계속 `relayNpPrefix == 0 → 11` 유지.
- **효과**: RelayTraffic bprintf는 “실제 사용 가능한 Relay 설정”이 있을 때만 호출 → CDR·NPE 리스크 최소.
- **문서화**: “C는 ucAgingCnt만 보나, Java는 CFG_RELAY_CID_LIST 존재·RELAY_FLAG_KT/LGU까지 검사해, 조회 실패 시 일반 bprintf로 fallback”이라고 규칙/주석에 명시.

**적용 내용**  
- 코드 변경 없이, 아래 4.5의 주석·설정 문서만 반영.

---

### 4.3 방안 B: C와 동일 규칙 + 안전 기본값 (선택)

- **규칙**: RelayFlag = `isRelaySrcType` (C와 동일).  
  단, **RelayTraffic bprintf 진입 조건**을 다음으로 제한:
  - `RelayFlag == 'Y' && srcType != "5"` **그리고**
  - **Relay 사용 가능**: `relayCidList != null` 또는 (`relayCidCode`/`relayNpPrefix`에 대한 안전 기본값 사용 시에만 진입).

- **안전 기본값 (NPPrefix 스타일)**  
  - `relayNpPrefix`: 이미 `0 → 11` 적용. 유지.  
  - `relayCidCode`: 빈 문자열이면 **RelayTraffic bprintf 진입을 스킵**하고 일반 bprintf로 fallback (C는 진입하지만 데이터가 비유효할 수 있으므로, Java는 “데이터 없으면 Relay 포맷 호출 안 함”으로 완화).

- **구현 예시**  
  - `isRelayFlagByC = isRelaySrcType`  
  - RelayTraffic bprintf 블록: `if (isRelayFlagByC && srcType != "5" && relayCidList != null) { ... }`  
  - 또는: `relayCidList == null || (relayCidCode.isBlank())` 이면 일반 bprintf 분기로 보냄.

- **효과**: C와 동일한 “RelayFlag='Y' 범위”를 가지면서, **실제로 유효한 Relay 데이터가 있을 때만** RelayTraffic bprintf 호출 → 리스크 제한.

---

### 4.4 방안 C: 설정으로 정책 선택

- **설정 예**: `relay.flag.strict=true`(기본) / `false`.
  - `true`: 현재처럼 relayCidList·RELAY_FLAG_KT/LGU 필수 (방안 A).
  - `false`: C 동일, `isRelaySrcType`만으로 RelayFlag 판단 + 방안 B의 “Relay 데이터 없으면 일반 bprintf” 규칙 적용.
- **효과**: 환경/운영 정책에 따라 C 완전 동일 vs 보수적 동작을 선택 가능.

---

### 4.5 공통으로 적용할 사항 (즉시)

1. **주석·규칙 문서**
   - `SmsResServiceImpl.kt` processMOBilling 내 RelayFlag·RelayTraffic bprintf 블록에:
     - “RelayFlag: C는 ucAgingCnt만 사용. Java는 CFG_RELAY_CID_LIST 존재 및 RELAY_FLAG_KT/LGU로 추가 검사하여, 조회 실패 시 일반 bprintf로 fallback.”
     - “NPPrefix: 0 → 11 규칙 적용 (C LINE 536과 동일).”
   - `.cursor/rules/VBILLMO_RelayFlag_src_type_분석.md` 또는 본 문서에 “RelayFlag 규칙 적용 리스크 및 Java 정책” 요약 추가.

2. **로깅**
   - `isRelaySrcType`이지만 `relayCidList == null` 또는 `relayFlagKt/LGU != 1` 인 경우:
     - “RelayFlag=N (no relay config or RELAY_FLAG_KT/LGU): SRC_TYPE=%s, QueueNo=%s” 수준 로그 한 줄 추가하면, C와의 차이·조회 실패 추적에 유리.

3. **단위/통합 테스트**
   - SRC_TYPE 5/6/7, relayCidList 유무, RELAY_FLAG_KT/LGU 0/1 조합으로:
     - RelayTraffic bprintf 호출 여부,
     - 일반 bprintf 호출 여부,
     - relayNpPrefix 0 → 11 치환 여부
     를 검증.

4. **MO_NOTISEND 경로**
   - C 722라인: MO_NOTISEND에서도 RelayFlag=='Y'이면 RelayTraffic NOTISEND CDF bprintf.  
   - Java에서 MO_NOTISEND 기반 과금 경로를 둘 경우, **동일한 RelayFlag 규칙**(방안 A/B/C 중 선택한 정책)과 **NPPrefix 0→11** 규칙을 그대로 적용할 것.

---

## 5. 권장 순서

| 단계 | 작업 |
|------|------|
| 1 | **방안 A 유지** + 4.5의 주석·로깅·문서 반영 (리스크 없음). |
| 2 | 테스트로 현재 동작(RelayFlag=N 조건, NPPrefix 11) 검증. |
| 3 | 운영/요구사항으로 “C와 완전 동일 필요” 여부 결정. 필요 시 **방안 B** 또는 **방안 C** 도입. |

이 순서로 적용하면 NPPrefix와 같이 RelayFlag 규칙을 명확히 하면서, 적용 리스크를 제어할 수 있습니다.
