# VBILLMO_c.c RelayFlag 및 src_type(ucAgingCnt) 동작·활용 분석

VBILLMO_c.c의 `RelayFlag`와 `ucAgingCnt`(Java의 **src_type**에 대응)가 어떻게 설정·사용되는지 정리한 문서입니다.

---

## 1. 용어·위치 정리

| C (VBILLMO_c.c) | Java/DB | 의미 |
|-----------------|---------|------|
| `ptrQitem->ucAgingCnt` | SRC_TYPE (MOCALLINFO/MO_NOTISEND 컬럼), `qItem.ucAgingCnt` | 호 유형: 일반(1,2), Relay MT/MO(5,6,7) |
| `RelayFlag` (지역 변수) | `relayFlag` ("Y"/"N") | Relay 과금 경로 여부 |
| CALL_TYPE_VSMSS_RELAY_MT | '5' | VSMSS Relay MT |
| CALL_TYPE_HSMSS_RELAY_MT | '6' | HSMSS Relay MT |
| CALL_TYPE_HSMSS_RELAY_MO | '7' | HSMSS Relay MO |

- **ucAgingCnt**는 QITEM에 들어 있는 값으로, MO 저장 시점에 설정되고 ProcessTR에서 그대로 사용됩니다.  
- DB의 **SRC_TYPE**은 SelectTRMOCallInfo/SelectTRMO_NOTISEND로 조회한 MOCALLINFO/MO_NOTISEND의 해당 컬럼이며, C에서는 **ucAgingCnt**만 사용하고 별도 SRC_TYPE 변수는 없습니다.

---

## 2. RelayFlag 설정 (한 번만 소스 사용)

### 2.1 MOCALLINFO 경로 진입 전 공통 (LINE 423–428)

**항상** ProcessTR에서 `SelectTRMOCallInfo` 호출 직후 한 번 실행됩니다.

```c
if (ptrQitem->ucAgingCnt == CALL_TYPE_VSMSS_RELAY_MT ||
    ptrQitem->ucAgingCnt == CALL_TYPE_HSMSS_RELAY_MT ||
    ptrQitem->ucAgingCnt == CALL_TYPE_HSMSS_RELAY_MO) {
    RelayFlag = 'Y';
    RelayNpPrefix = GetRelayCidInfoByQueueNo(&stRelayCID, ptrQitem->usSource, RelayCidCode);
    Lvdprintf(LOG_NORMAL, "[NORMAL] GetRelayCidInfoByQueueNo(...)\n", ...);
} else
    RelayFlag = 'N';
```

- **RelayFlag**: ucAgingCnt가 **5 or 6 or 7**이면 `'Y'`, 아니면 `'N'`.
- **RelayNpPrefix, RelayCidCode**: Relay일 때만 `GetRelayCidInfoByQueueNo(..., ptrQitem->usSource, ...)`로 설정.  
  → MOCALLINFO이든 MO_NOTISEND이든, 이후 bprintf에서 사용하는 Relay 정보는 모두 이 한 번의 호출 결과입니다.

### 2.2 MO_NOTISEND 경로 내 재설정 (LINE 637–639)

`ret == ORA_NODATA` 후 `SelectTRMO_NOTISEND` 성공 시에만 들어오는 블록 안에서:

```c
if (ptrQitem->ucAgingCnt == CALL_TYPE_VSMSS_RELAY_MT ||
    ptrQitem->ucAgingCnt == CALL_TYPE_HSMSS_RELAY_MT ||
    ptrQitem->ucAgingCnt == CALL_TYPE_HSMSS_RELAY_MO)
    RelayFlag = 'Y';
else
    RelayFlag = 'N';
```

- **역할**: MO_NOTISEND 경로에서도 동일 기준으로 RelayFlag만 다시 맞춤.  
- **RelayNpPrefix / RelayCidCode**: 여기서는 설정하지 않음. 위 423–428에서 이미 설정된 값을 그대로 사용합니다.

---

## 3. RelayFlag·ucAgingCnt 활용 (bprintf 분기)

과금 로그(bprintf)는 **Color/Avata 여부** → **Relay 여부** → **일반** 순으로 분기합니다.

### 3.1 MOCALLINFO 경로 (ret > 0, LINE 349–549)

- **Color/Avata** (LINE 349): `szSrcCId`가 COLORSMS("200") 또는 AVATASMS("1584")  
  → 해당 전용 bprintf만 수행.
- **그 외** (LINE 363–549):
  - **RelayFlag == 'Y'** (LINE 528):
    - **ucAgingCnt != CALL_TYPE_VSMSS_RELAY_MT** (LINE 530)  
      → **RelayTraffic CDF** bprintf (W2PMsgId, RelayCidCode, RelayNpPrefix, VirtualNum 등 사용).
    - ucAgingCnt == '5'(VSMSS_RELAY_MT)이면  
      → RelayTraffic bprintf **호출 안 함** → 아래 else(LINE 540)로 빠져 **일반 bprintf**.
  - **RelayFlag != 'Y'** (LINE 540 else):  
    → **일반 bprintf** (0%s, ucMsgId 등).

정리:

- **RelayTraffic bprintf**가 나가는 경우:  
  `RelayFlag == 'Y'` 이고 **ucAgingCnt가 6 또는 7** (HSMSS_RELAY_MT, HSMSS_RELAY_MO).
- **ucAgingCnt == 5**(VSMSS_RELAY_MT)이면 RelayFlag가 'Y'여도 **일반 bprintf**만 수행.

### 3.2 MO_NOTISEND 경로 (LINE 718–737)

- **strlen(ptrQitem->szSrcMinNo) < 6** (LINE 715):  
  → 일반 bprintf 한 종류만.
- **그 외**:
  - **RelayFlag == 'Y'** (LINE 722):  
    → **RelayTraffic NOTISEND CDF** bprintf (EsmClass, VirtualNum 등 사용).  
    → 여기서는 **ucAgingCnt 추가 조건 없음** (MOCALLINFO과 다름).
  - **RelayFlag != 'Y'** (LINE 731 else):  
    → 일반 bprintf.

즉, MO_NOTISEND에서는 RelayFlag만 보고, MOCALLINFO처럼 “ucAgingCnt != 5” 같은 이중 조건은 없습니다.

---

## 4. ucAgingCnt만 사용하는 후처리 (LINE 599–606)

bprintf·InsqStat 이후, **Relay MT/MO**에 대한 후처리:

```c
if (ptrQitem->ucAgingCnt == CALL_TYPE_HSMSS_RELAY_MT ||
    ptrQitem->ucAgingCnt == CALL_TYPE_VSMSS_RELAY_MT) {
    memcpy((char*)ptrQitem->ucMsgId, W2PMsgId, QITEM_SIZE_MSGID);
    SendRelayMTTR(ptrQitem);
} else if (ptrQitem->ucAgingCnt == CALL_TYPE_HSMSS_RELAY_MO) {
    if (!strncmp(ptrQitem->szVirtualNum, AI_SURVEY_NUMBER, ...))
        DBDelOCSCallInfoFromVN(W2PMsgId, ptrQitem->szVirtualNum);
}
```

- **5 or 6**: W2PMsgId 복사 후 **SendRelayMTTR** (VBILL/PCSRECV 등 큐 적재).
- **7**: HSMSS_RELAY_MO 전용 (AI Survey 가상번호 시 OCS 정리).

RelayFlag는 이 블록에서 사용하지 않고, **ucAgingCnt만**으로 분기합니다.

---

## 5. 동작 요약표

| 구분 | RelayFlag | ucAgingCnt (src_type) | 동작 |
|------|-----------|------------------------|------|
| **설정** | 423–428 (및 NOTISEND 경로 637–639) | QITEM 원본 값 사용 | 5/6/7 → RelayFlag='Y', GetRelayCidInfoByQueueNo 호출(최초 1회) |
| **MOCALLINFO bprintf** | 'Y' 필요 | **6 or 7**일 때만 RelayTraffic bprintf; **5**면 일반 bprintf | RelayFlag=='Y' && ucAgingCnt!='5' → Relay CDF |
| **MO_NOTISEND bprintf** | 'Y'이면 Relay CDF | 추가 조건 없음 | RelayFlag=='Y' → RelayTraffic NOTISEND CDF |
| **후처리** | 미사용 | 5 or 6 → SendRelayMTTR; 7 → AI Survey OCS 삭제 등 | ucAgingCnt만 사용 |

---

## 6. Java 반영 시 유의사항

1. **RelayFlag**  
   - C는 **ucAgingCnt만** 보고 RelayFlag='Y'로 두며, GetRelayCidInfoByQueueNo 실패 여부와 무관하게 플래그는 'Y'로 유지합니다.  
   - Java는 CFG_RELAY_CID_LIST 조회 성공 + RELAY_FLAG_KT/LGU 중 1이라도 1일 때만 RelayFlag='Y'로 두는 등 더 엄격할 수 있음 → 정책 확정 후 C와 동작 일치 여부 검증 필요.

2. **src_type(ucAgingCnt)**  
   - MOCALLINFO 경로: **RelayTraffic bprintf**는 `RelayFlag == 'Y' && src_type != '5'`일 때만 호출 (5는 일반 bprintf).  
   - MO_NOTISEND 경로: RelayFlag만 보면 되고, src_type 추가 제한은 C에 없음.

3. **MO_NOTISEND Relay CDF**  
   - C 722라인: RelayFlag=='Y'이면 NOTISEND 전용 Relay CDF bprintf.  
   - Java에서 MO_NOTISEND 기반 과금 시 동일한 “Relay NOTISEND” 포맷을 쓸지, MOCALLINFO와 같은 Relay 포맷만 쓸지 명확히 할 필요 있음.

4. **후처리(SendRelayMTTR, OCS 삭제)**  
   - C는 ucAgingCnt(5/6/7)로만 분기. Java에서 TR 후처리(큐 적재, OCS 삭제 등)를 구현할 때도 **src_type(또는 qItem.ucAgingCnt)** 기준으로 5/6/7 분기하면 C와 동일하게 활용할 수 있습니다.

이 문서는 VBILLMO_c.c의 RelayFlag와 src_type(ucAgingCnt) 동작을 기준으로 하며, Java는 이 규칙에 맞춰 RelayFlag·src_type을 활용하면 됩니다.
