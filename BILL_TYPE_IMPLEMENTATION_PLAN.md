# BILL_TYPE='2', '4', '5' 구현 계획

## 1. 현재 구현 상태 분석

### BILL_TYPE='2' (SRC - 발신자 과금)
- ✅ **한도체크**: `checkLimitMO()` 함수 구현 완료
- ✅ **호출 위치**: `MOThreadPool.kt:686` - MO 메시지 전송 전
- ✅ **MDN 형식**: `${srcCid}${srcMinNo}`
- ✅ **캐시 활용**: `LimitCheckFlagsMap` 사용
- ⚠️ **검증 필요**: C 코드와 동일한 동작 확인

### BILL_TYPE='4' (GIVE - 선물 과금)
- ✅ **한도체크 함수**: `checkLimitGIVE()` 함수 구현 완료
- ❌ **호출 위치**: 현재 호출되지 않음
- ✅ **MDN 형식**: `0${srcCallNo}` (C 코드와 동일)
- ✅ **캐시 활용**: `LimitCheckFlagsMap` 사용
- ⚠️ **결정 필요**: MO 프로젝트에서 GIVE 한도체크가 필요한지 확인
  - C 코드: MT 처리에서만 사용 (ProcessSmReqSimple, ProcessSmReqPorted)
  - MO 처리: 과금은 수행하지만 한도체크는 없음

### BILL_TYPE='5' (CNT - 비과금 정산용)
- ✅ **한도체크**: 불필요 (모든 플래그 FALSE)
- ✅ **과금 처리**: `bprintf` 호출 (정산용 데이터 출력)
- ✅ **기본 처리**: `gBILLTYPE != '1'` 조건에 포함되어 정상 처리
- ⚠️ **검증 필요**: 정산용 데이터 출력이 정상 동작하는지 확인

---

## 2. C 코드 분석 결과

### BILL_TYPE='2' (SRC)
```c
// GIPEVENT_c.c:940
case SM_REQ_SIMPLE :
    nRet = CheckLimitMO(ptrMsgHdr);  // MO 한도체크
    if(nRet == TRUE) {
        // 한도 초과 처리
    }
```
- **MDN 형식**: `sprintf(szMdn, "%s%u", ptrGIMsgHdr->szSrcCId, ptrGIMsgHdr->uSrcCallNo)`
- **호출 시점**: MO 메시지 전송 전 (SM_REQ_SIMPLE)

### BILL_TYPE='4' (GIVE)
```c
// GIPEVENT_c.c:2824, 3644
ret = CheckLimitGiveBill(ptrGIMsgHdr);  // MT 처리에서만 호출
if(ret == TRUE) {
    // GIVE 한도 초과 처리
}
```
- **MDN 형식**: `sprintf(szMdn, "0%u", ptrGIMsgHdr->uSrcCallNo)`
- **호출 시점**: MT 메시지 처리 시 (ProcessSmReqSimple, ProcessSmReqPorted)
- **MO 처리**: 한도체크 없음, 과금만 수행

### BILL_TYPE='5' (CNT)
```c
// GIDBLib.c:5161-5166
else {  // BILLTYPE_CNT 포함
    *_iLimitMO = FALSE;
    *_iLimitMT = FALSE;
    *_iLimitGIVE = FALSE;
}
```
- **한도체크**: 없음
- **과금 처리**: `bprintf` 호출 (정산용 데이터)

---

## 3. 구현 계획

### 3.1 BILL_TYPE='2' (SRC) - 검증 및 개선

#### 현재 상태
- ✅ `checkLimitMO()` 함수 구현 완료
- ✅ `LimitCheckFlagsMap` 캐시 활용
- ✅ MO 메시지 전송 전 호출됨

#### 검증 항목
1. ✅ MDN 형식 확인: `${srcCid}${srcMinNo}` (C 코드와 일치)
2. ✅ 한도체크 로직 확인: `limitFlags.limitMO` 사용
3. ✅ 통계 기록 확인: `ERRORID_CP_MO_LIMIT`, `ST_GIP_MO_LIMIT`
4. ⚠️ 에러 처리 확인: DB 에러 시 false 반환

#### 개선 사항
- 현재 구현이 C 코드와 일치하므로 추가 개선 불필요

---

### 3.2 BILL_TYPE='4' (GIVE) - 구현 검토 및 결정

#### 현재 상태
- ✅ `checkLimitGIVE()` 함수 구현 완료
- ❌ 호출되지 않음
- ✅ MDN 형식: `0${srcCallNo}` (C 코드와 일치)

#### C 코드 분석 결과
- **MT 처리**: `CheckLimitGiveBill()` 호출됨 (LINE 2824, 3644)
- **MO 처리**: 한도체크 없음, 과금만 수행

#### 결정 사항
**옵션 1: MO 프로젝트 특성상 불필요 (권장)**
- C 코드에서 GIVE 한도체크는 MT 전용
- MO 프로젝트에서는 불필요
- `checkLimitGIVE()` 함수는 유지하되 호출하지 않음

**옵션 2: MO에서도 GIVE 한도체크 수행**
- MO 메시지 전송 전 `checkLimitGIVE()` 호출 추가
- C 코드와 다르지만, MO 프로젝트 요구사항일 수 있음

#### 권장 구현
- **결정**: 옵션 1 (MO 프로젝트 특성상 불필요)
- **이유**: C 코드에서 GIVE 한도체크는 MT 전용이며, 주석에도 "선물 MT 한도체크 추가"로 명시됨
- **조치**: `checkLimitGIVE()` 함수는 유지하되, 호출 로직은 추가하지 않음

---

### 3.3 BILL_TYPE='5' (CNT) - 구현 검증

#### 현재 상태
- ✅ 한도체크: 모든 플래그 FALSE (C 코드와 일치)
- ✅ 과금 처리: `bprintf` 호출 (정산용 데이터)
- ✅ 기본 처리: `gBILLTYPE != '1'` 조건에 포함

#### 검증 항목
1. ✅ 한도체크: `BillTypeUtil.getLimitCheck()`에서 모든 플래그 FALSE
2. ✅ 과금 처리: `processMOBilling()`에서 `bprintf` 호출
3. ✅ DB 저장: `InsertGIPMOCallInfo` 호출
4. ⚠️ 로깅: BILL_TYPE='5'일 때 적절한 로그 출력 확인

#### 개선 사항
- 현재 구현이 C 코드와 일치하므로 추가 개선 불필요
- 다만, BILL_TYPE='5'일 때 명시적인 로그 추가 고려

---

## 4. 구현 작업 계획

### 작업 1: BILL_TYPE='2' (SRC) 검증
- [x] 현재 구현 확인
- [ ] C 코드와 동작 일치 여부 검증
- [ ] MDN 형식 검증
- [ ] 에러 처리 검증

### 작업 2: BILL_TYPE='4' (GIVE) 결정 및 구현
- [x] C 코드 분석 완료
- [ ] MO 프로젝트 요구사항 확인
- [ ] 결정: MO에서 GIVE 한도체크 필요 여부
- [ ] 필요 시 호출 로직 추가

### 작업 3: BILL_TYPE='5' (CNT) 검증
- [x] 현재 구현 확인
- [ ] 과금 처리(bprintf) 검증
- [ ] 로깅 개선 (선택사항)

### 작업 4: 통합 테스트
- [ ] BILL_TYPE='2' 테스트
- [ ] BILL_TYPE='4' 테스트 (필요 시)
- [ ] BILL_TYPE='5' 테스트

---

## 5. 예상 변경 사항

### 변경 불필요
- BILL_TYPE='2': 현재 구현 완료
- BILL_TYPE='5': 현재 구현 완료

### 검토 필요
- BILL_TYPE='4': MO 프로젝트 요구사항 확인 후 결정

---

## 6. 최종 권장사항

1. **BILL_TYPE='2'**: ✅ 현재 구현 유지 (추가 작업 불필요)
2. **BILL_TYPE='4'**: ⚠️ MO 프로젝트 특성상 불필요 (함수는 유지, 호출하지 않음)
3. **BILL_TYPE='5'**: ✅ 현재 구현 유지 (추가 작업 불필요)

---

## 7. 다음 단계

1. 사용자 확인: BILL_TYPE='4'의 MO 프로젝트 요구사항 확인
2. 필요 시: BILL_TYPE='4' 호출 로직 추가
3. 검증: 전체 BILL_TYPE 테스트 수행
