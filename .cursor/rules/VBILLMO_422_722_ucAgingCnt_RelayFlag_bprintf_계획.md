# VBILLMO_c.c 422/722 라인 로직 분석 및 Java 반영 계획

## 1. 목적

- **C 소스**: `VBILLMO_c.c` (src 워크스페이스 C 프로젝트)
  - **422 라인**: `ucAgingCnt` 값을 활용한 **RelayFlag** 정의
  - **722 라인**: **bprintf** 분기 결정
- 위 로직을 분석하고, 현재 Java 코드 반영 현황을 정리한 뒤 검증·보완 계획을 수립한다.

---

## 2. C 코드 로직 분석 (참조 기준)

> ※ `VBILLMO_c.c` 파일은 본 워크스페이스(SKT_GIPHTTP_MO)에 없으며, 기존 Java 주석·문서·이식 체크 자료를 기반으로 정리함.

### 2.1 422 라인 부근: ucAgingCnt → RelayFlag

| 항목 | 내용 |
|------|------|
| **위치** | `ProcessTR` 내부 (ProcessTR: LINE 373–932) |
| **역할** | `ptrQitem->ucAgingCnt` 값에 따라 **RelayFlag** 설정 및 **GetRelayCidInfoByQueueNo** 호출 여부 결정 |
| **조건** | `ucAgingCnt == '5' \|\| '6' \|\| '7'` → Relay 관련 처리 |
| **의미** | - `'5'`: CALL_TYPE_VSMSS_RELAY_MT<br>- `'6'`: CALL_TYPE_HSMSS_RELAY_MT<br>- `'7'`: CALL_TYPE_HSMSS_RELAY_MO |
| **동작** | - `ucAgingCnt`가 5/6/7이면 **RelayFlag = 'Y'**, **GetRelayCidInfoByQueueNo**(queue_no, RelayCidCode 등) 호출<br>- 그 외는 **RelayFlag = 'N'**, Relay CID 조회 안 함 |

### 2.2 722 라인 부근: bprintf 분기 결정

| 항목 | 내용 |
|------|------|
| **위치** | `ProcessTR` 내부, 422에서 설정한 RelayFlag 이후 흐름 |
| **역할** | **RelayFlag** 및 **ucAgingCnt**에 따라 **어떤 bprintf 포맷을 호출할지** 분기 |
| **분기 요약** | - **RelayFlag == 'Y' && ucAgingCnt != CALL_TYPE_VSMSS_RELAY_MT('5')** → **RelayTraffic 전용 bprintf** (LINE 528–541 부근)<br>- 그 외 → **일반 bprintf** (LINE 542–550, 이후 2169/2179/2190 등 포맷별 호출) |
| **결과** | 722 라인에서 “RelayTraffic bprintf vs 일반 bprintf” 중 하나가 선택되고, 실제 출력 포맷은 2163–2195 구간과 연계됨 |

### 2.3 C 라인 번호 대응 요약

| C 라인 (참조) | 역할 |
|----------------|------|
| 422–428 | ucAgingCnt → RelayFlag, GetRelayCidInfoByQueueNo 호출 여부 |
| 426 | GetRelayCidInfoByQueueNo 로그 (NpPrefix, QueueNo, RelayCidCode) |
| 528–541 | RelayFlag=='Y' && ucAgingCnt!='5' → RelayTraffic CDF/bprintf |
| 530 | ucAgingCnt != CALL_TYPE_VSMSS_RELAY_MT('5') 체크 |
| 542–550 | RelayFlag!='Y' 또는 SRC_TYPE=='5' → 일반 bprintf 경로 |
| 722 | bprintf 분기 결정 (Relay vs 일반) |
| 2163–2195 | bprintf 과금 데이터 출력 (포맷 구성) |
| 2169 / 2179 / 2190 | 일반 bprintf 포맷별 호출 (가입자 번호 0%d, 일반 %d, DestCID 등) |

---

## 3. 현재 Java 반영 현황

### 3.1 반영 위치

- **파일**: `SmsResServiceImpl.kt`
- **메서드**: `processMOBilling(...)`
- **대략 라인**: 1211–1247 (RelayFlag 계산), 1266–1460 (bprintf 분기 및 호출)

### 3.2 ucAgingCnt → RelayFlag (422 라인 대응)

| C 동작 | Java 구현 | 비고 |
|--------|-----------|------|
| ucAgingCnt 사용 | `effectiveMoInfo.srcType` (DB SRC_TYPE) + `qItem.ucAgingCnt` | SRC_TYPE은 SelectTRMOCallInfo/SelectTRMO_NOTISEND에서 온 값과 동일 의미로 사용 |
| 5/6/7 여부 | `isRelaySrcType = (srcType == "5" \|\| srcType == "6" \|\| srcType == "7")` | ✅ 동일 조건 |
| GetRelayCidInfoByQueueNo | `cfgRelayCidListRepository.findByRelayQueue(queueNo)` | queue_no = `qItem.usSource`, Relay CID 리스트 조회 |
| RelayFlag 결정 | `isRelayFlag = isRelaySrcType && relayCidList != null && (relayFlagKt==1 \|\| relayFlagLgu==1)`<br>`relayFlag = if (isRelayFlag) "Y" else "N"` | ⚠️ C는 ucAgingCnt만 보고 RelayFlag='Y'; Java는 **RELAY_FLAG_KT/LGU**까지 검사 (의도적 보강) |

### 3.3 bprintf 분기 (722 라인 대응)

| C 분기 | Java 구현 | 비고 |
|--------|-----------|------|
| RelayFlag=='Y' && ucAgingCnt!='5' | `if (isRelayFlag && srcType != "5")` → RelayTraffic bprintf | ✅ LINE 1266–1322 |
| 그 외 | `else` → 일반 bprintf (Color/Avata, 가입자/일반 번호, DestCID) | ✅ LINE 1331–1460 |
| RelayTraffic bprintf 포맷 | `smsQLib.bprintf(";%d;;;%d;%s;%s%s;...")` | ✅ LINE 1291–1318 |
| 일반 bprintf 포맷 | LINE 2169/2179/2190 대응 3가지 분기 (isColorOrAvata, isPortableNo 등) | ✅ LINE 1350 / 1391 / 1433 |

### 3.4 정리

- **422 라인 로직**: ucAgingCnt(5/6/7) 기반 RelayFlag 및 Relay CID 조회는 Java에 반영되어 있음. 다만 RelayFlag='Y' 조건을 Java에서 CFG_RELAY_CID_LIST의 RELAY_FLAG_KT/LGU까지 넣어 더 엄격히 적용함.
- **722 라인 bprintf 분기**: RelayTraffic vs 일반 bprintf 선택이 `isRelayFlag && srcType != "5"` / 그 외 else로 구현되어 있어, C의 722 분기와 동일한 결정 구조로 보임.

---

## 4. C–Java 차이점 및 검토 사항

| 항목 | C | Java | 조치 |
|------|---|------|------|
| RelayFlag='Y' 조건 | ucAgingCnt가 5/6/7이면 'Y' (GetRelayCid 실패 시에도 플래그는 'Y') | 5/6/7 + **relayCidList 존재** + **RELAY_FLAG_KT 또는 RELAY_FLAG_LGU == 1** | C와 완전 동일하게 맞출지, 현재처럼 “실제 사용 가능할 때만 Y”로 둘지 결정 필요 |
| SRC_TYPE 소스 | C는 ptrQitem->ucAgingCnt 직접 사용 가능 | Java는 MOCALLINFO/MO_NOTISEND의 SRC_TYPE 컬럼 사용 (dequeue 시점 ucAgingCnt와 동기화된 값으로 가정) | MO 저장 시 SRC_TYPE이 ucAgingCnt와 일치하도록 넣는지 확인 |
| 722 라인 직접 대응 | ProcessTR 내 한 지점에서 bprintf 분기 | processMOBilling 내에서 동일 분기 구조로 구현됨 (주석에 530/542만 명시, 722는 미명시) | 주석에 “C LINE 722 bprintf 분기 대응” 추가 권장 |

---

## 5. Java 반영을 위한 계획 (체크리스트)

### 5.1 분석·검증

- [ ] **C 소스 확보**: `VBILLMO_c.c` 422, 722 라인 실제 코드로 422 IF 조건·변수명·722 분기 조건 재확인
- [ ] **722 정확한 역할**: 722가 “Relay vs 일반 선택”만 하는지, 추가 조건(한도/에러 등)이 있는지 확인
- [ ] **ucAgingCnt vs SRC_TYPE**: C에서 RelayFlag 계산 시 사용하는 값이 **ptrQitem->ucAgingCnt** 단일 소스인지, DB SRC_TYPE을 별도 읽는지 확인

### 5.2 코드 반영·보완

- [ ] **주석 보강**: `SmsResServiceImpl.kt` processMOBilling 내 RelayFlag·bprintf 블록에  
  `// C 코드 LINE 422: ucAgingCnt → RelayFlag`,  
  `// C 코드 LINE 722: bprintf 분기 (RelayTraffic vs 일반)` 명시
- [ ] **RelayFlag 정책 결정**: C와 동일(ucAgingCnt만) vs 현재 Java(RELAY_FLAG_KT/LGU 포함) 중 하나로 확정 후, 필요 시 C 동작 맞춤 수정 또는 문서화
- [ ] **단위/통합 테스트**: ucAgingCnt(SRC_TYPE) 5/6/7 및 Relay CID 유무에 따라 RelayTraffic bprintf vs 일반 bprintf가 기대대로 호출되는지 테스트

### 5.3 문서화

- [ ] 이 계획서를 “422/722 로직 공식 대응 문서”로 두고, C 소스 422/722 수정 시 이 문서와 Java 쪽 주석을 함께 갱신
- [ ] `C_JAVA_기능_이식_체크.md` 등에 “VBILLMO 422/722 → processMOBilling RelayFlag·bprintf” 참조 추가

---

## 6. 요약

| 구분 | 내용 |
|------|------|
| **422** | `ucAgingCnt` 5/6/7 → RelayFlag 정의 및 GetRelayCidInfoByQueueNo 호출. Java는 `processMOBilling`에서 `srcType`(5/6/7) + CFG_RELAY_CID_LIST 조회로 반영됨. |
| **722** | RelayFlag·ucAgingCnt에 따른 **bprintf 분기** 결정. Java는 `isRelayFlag && srcType != "5"` → RelayTraffic bprintf, else → 일반 bprintf로 동일 구조 반영됨. |
| **다음 단계** | C 422/722 실제 소스로 검증 후, 주석 보강·RelayFlag 정책 확정·테스트 추가. |
