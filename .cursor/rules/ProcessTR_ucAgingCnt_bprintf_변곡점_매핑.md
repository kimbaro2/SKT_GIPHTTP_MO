# ProcessTR(382) ucAgingCnt 활용 로직 및 bprintf 변곡점 매핑

VBILLMO_c.c `void ProcessTR(QITEMPTR ptrQitem)` (LINE 382)에서 ucAgingCnt가 쓰이는 흐름과 bprintf까지의 변곡점, Java 대응을 정리한 문서.

---

## 1. ucAgingCnt 활용 위치 (C)

| 순서 | C 라인 | 역할 | ucAgingCnt 사용 |
|------|--------|------|------------------|
| 1 | 419 | SelectTRMOCallInfo | 사용 안 함 |
| 2 | **423–428** | RelayFlag·Relay CID 조회 | 5\|6\|7 → RelayFlag='Y', GetRelayCidInfoByQueueNo(usSource) |
| 3 | 444 | 진입 분기 | ret>0 → MOCALLINFO, ret==ORA_NODATA → MO_NOTISEND |
| 4 | 459 | SEND_OK 여부 | ucMsgStatus만 사용 |
| 5 | **495–549** | MOCALLINFO bprintf | 변곡점 ①~④ (아래) |
| 6 | **599–606** | bprintf 이후 후처리 | 5\|6 → SendRelayMTTR, 7 → DBDelOCSCallInfoFromVN |
| 7 | **637–639** | MO_NOTISEND RelayFlag | 5\|6\|7 → RelayFlag='Y' |
| 8 | **718–737** | MO_NOTISEND bprintf | 변곡점 ⑤ (RelayFlag만 사용) |

---

## 2. bprintf 변곡점 (C → Java 대응)

### MOCALLINFO 경로 (ret > 0)

| 변곡점 | C 조건 | 결과 | Java (processMOBilling) |
|--------|--------|------|--------------------------|
| ① | szSrcCId == COLORSMS/AVATASMS (349) | Color/Avata 전용 bprintf | isColorOrAvata → Color/Avata 분기 |
| ② | strlen(szSrcMinNo)<6 (418) | 일반 bprintf 1종 | (발신번호 길이 분기) |
| ③ | RelayFlag=='Y' (528) | Relay 분기 진입 | isRelayFlag → Relay 분기 |
| ④ | ucAgingCnt != VSMSS_RELAY_MT (530) | RelayTraffic CDF vs 일반 | CallTypeRelay.shouldCallRelayTrafficBprintf(srcType) |

### MO_NOTISEND 경로 (ret==ORA_NODATA 후 SelectTRMO_NOTISEND 성공)

| 변곡점 | C 조건 | 결과 | Java |
|--------|--------|------|------|
| ⑤ | RelayFlag=='Y' (722) | RelayTraffic NOTISEND CDF bprintf | 동일 블록에서 RelayFlag 검토, 포맷은 MOCALLINFO Relay 1종 공용 (C의 NOTISEND 전용 포맷은 미구현) |

---

## 3. Java 반영 기점

- **진입점**: `SmsResServiceImpl.processMOBilling(qItem, request, gipHttpMoAccess, smsQLib, loggerName, isMoAckContext)`.
- **SRC_TYPE**  
  - MO-TR: MOCALLINFO/MO_NOTISEND 조회 `effectiveMoInfo.srcType`.  
  - MOTRBILL=N MO 단계: `buildEffectiveMoInfoFromRequestAndQItem`에서 **qItem.ucAgingCnt** 기반 srcType 설정 (insertGIPMOCallInfo와 동일 규칙).
- **RelayFlag**: isRelaySrcType(srcType) + relayCidList + RELAY_FLAG_KT/LGU → isRelayFlag, relayFlag "Y"/"N".
- **RelayTraffic bprintf**: isRelayFlag && shouldCallRelayTrafficBprintf(srcType) (MOCALLINFO). MO_NOTISEND는 동일 블록에서 RelayFlag만 검토.

---

## 4. 적용 완료·비고

- **MOTRBILL=N MO 단계**: `buildEffectiveMoInfoFromRequestAndQItem`에서 srcType = qItem.ucAgingCnt 기반 설정 반영 완료.
- **MO_NOTISEND Relay**: RelayFlag 검토는 MOCALLINFO와 동일 블록에서 수행. C LINE 722–730의 NOTISEND 전용 bprintf 포맷은 현재 Java에 없고, 필요 시 `isFromMONotISend` 분기로 별도 포맷 추가 가능.
