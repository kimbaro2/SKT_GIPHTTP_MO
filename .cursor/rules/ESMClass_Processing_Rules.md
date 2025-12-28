# ESMClass 처리 규칙 (20, 21, 90, 91)

## 1. ESMClass 정의

```kotlin
// SmsDef.kt
const val NOTI_PLUS_NORMAL_MO = 20    // 안심문자 일반
const val NOTI_PLUS_PORTED_MO = 21    // 안심문자 번호이동
const val NOTI_NORMAL_MO = 90         // 등기문자 일반
const val NOTI_PORTED_MO = 91         // 등기문자 번호이동
```

## 2. MO 전송 시점 처리 규칙

### 규칙: ESMClass 20, 21, 90, 91은 모두 MO_NOTISEND 테이블에 저장해야 함

**현재 잘못된 구현:**
```kotlin
// MOThreadPool.kt:464-527
if (gBILLTYPE != '1' && isNotiPlusType) {  // ESMClass 20, 21만
    insertMO_NOTISEND(...)  // MO_NOTISEND에 저장
} else {
    insertGIPMOCallInfo(...)  // ESMClass 90, 91도 여기로 처리됨 (잘못됨)
}
```

**올바른 구현:**
```kotlin
val isNotiPlusType = gESMCLASS == NOTI_PLUS_NORMAL_MO || gESMCLASS == NOTI_PLUS_PORTED_MO  // 20, 21
val isNotiType = gESMCLASS == NOTI_NORMAL_MO || gESMCLASS == NOTI_PORTED_MO  // 90, 91

if (gBILLTYPE != '1' && (isNotiPlusType || isNotiType)) {
    // ESMClass 20, 21, 90, 91 모두 MO_NOTISEND에 저장
    insertMO_NOTISEND(...)  // SERVERTYPE='V'로 INSERT
} else if (isRelayMo) {
    insertRelayMOCallInfo(...)
} else {
    // 그 외 ESMClass (예: 1, 57 등)
    insertGIPMOCallInfo(...)  // MOCALLINFO에 저장
}
```

**핵심 규칙:**
- ESMClass 20, 21, 90, 91: `MO_NOTISEND` 테이블에 `SERVERTYPE='V'`로 INSERT
- 그 외 ESMClass: `MOCALLINFO` 테이블에 INSERT

## 3. TR 결과 수신 시점 처리 규칙

### 규칙: ESMClass 20, 21, 90, 91은 모두 updateMO_NOTISEND() 호출해야 함

**현재 잘못된 구현:**
```kotlin
// SmsResServiceImpl.kt:936-972
val isNotiPlusType = rsv4Protocol11 == NOTI_PLUS_NORMAL_MO || 
                     rsv4Protocol11 == NOTI_PLUS_PORTED_MO  // 20, 21만 체크
if (isNotiPlusType) {
    updateMO_NOTISEND(request)  // ESMClass 20, 21만
} else {
    updateGIPMOCallInfo(request)  // ESMClass 90, 91도 여기로 처리됨 (잘못됨)
}
```

**올바른 구현:**
```kotlin
val rsv4Protocol11 = request.data.rsv4Protocol?.getOrNull(11)?.data ?: 0
val isNotiPlusType = rsv4Protocol11 == NOTI_PLUS_NORMAL_MO || 
                     rsv4Protocol11 == NOTI_PLUS_PORTED_MO  // 20, 21
val isNotiType = rsv4Protocol11 == NOTI_NORMAL_MO || 
                 rsv4Protocol11 == NOTI_PORTED_MO  // 90, 91

if (isNotiPlusType || isNotiType) {
    // ESMClass 20, 21, 90, 91 모두 updateMO_NOTISEND 호출
    updateMO_NOTISEND(request)
} else {
    // 그 외 ESMClass
    updateGIPMOCallInfo(request)
}
```

**핵심 규칙:**
- ESMClass 20, 21, 90, 91: `updateMO_NOTISEND()` 호출
- 그 외 ESMClass: `updateGIPMOCallInfo()` 호출

## 4. SERVERTYPE 처리 규칙

### 규칙: MO 전송 시 'V', TR 수신 시 'H'로 변경

**현재 잘못된 구현:**
```sql
-- MONotISendRepository.java:48-52
INSERT INTO SMS.MO_NOTISEND (..., SERVERTYPE, ...)
SELECT ..., SERVERTYPE, ...  -- SERVERTYPE='V' 그대로 유지 (잘못됨!)
FROM SMS.MO_NOTISEND
WHERE ... AND SERVERTYPE = 'V'
```

**올바른 구현:**
```sql
INSERT INTO SMS.MO_NOTISEND (..., SERVERTYPE, ...)
SELECT ..., 'H' AS SERVERTYPE, ...  -- SERVERTYPE='H'로 변경
FROM SMS.MO_NOTISEND
WHERE ... AND SERVERTYPE = 'V'
```

**핵심 규칙:**
- MO 전송 시점: `SERVERTYPE='V'`로 INSERT
- TR 수신 시점: `updateMO_NOTISEND()`에서 `SERVERTYPE='V'`에서 SELECT하여 `SERVERTYPE='H'`로 새 레코드 생성
- processMOBilling 조회 시: `SERVERTYPE='H'`로 조회

## 5. updateMO_NOTISEND() DELETE 규칙

### 규칙: INSERT 후 원본 레코드(SERVERTYPE='V') 삭제 필수

**현재 잘못된 구현:**
```kotlin
// SmsResServiceImpl.kt:1427-1433
val result = moNotISendRepository.insertFromExisting(...)  // INSERT만 수행
// DELETE 로직 없음!
```

**올바른 구현:**
```kotlin
// 1. SERVERTYPE='V'에서 SELECT하여 SERVERTYPE='H'로 새 레코드 생성
val result = moNotISendRepository.insertFromExisting(ackMsgId, srcCallNo, destCId, msgId)

if (result > 0) {
    // 2. 원본 레코드(SERVERTYPE='V') 삭제
    moNotISendRepository.deleteBySrcCallNoAndDestCIdAndMsgIdAndServerType(
        srcCallNo, destCId, msgId, "V"
    )
}
```

**핵심 규칙:**
- `updateMO_NOTISEND()`는 `updateGIPMOCallInfo()`와 동일하게 INSERT 후 DELETE 수행
- 원본 레코드(`SERVERTYPE='V'`)는 반드시 삭제해야 함

## 6. 과금 처리 후 ESM 분기 처리 규칙

### 규칙: processMOBilling() 완료 후 ESMClass별로 DB 삭제 + 메시지 전송/라우팅 수행

**현재 미구현:**
```kotlin
// SmsResServiceImpl.kt:992
processMOBilling(qItem, request, gipHttpMoAccess, smsQLib)
// 이후 ESM 분기 처리 없음!
```

**올바른 구현:**
```kotlin
// 1. 과금 처리
processMOBilling(qItem, request, gipHttpMoAccess, smsQLib)

// 2. ESMClass별 분기 처리
val rsv4Protocol11 = request.data.rsv4Protocol?.getOrNull(11)?.data ?: 0

when {
    // ESMClass 90, 91: 등기문자
    rsv4Protocol11 == NOTI_NORMAL_MO || rsv4Protocol11 == NOTI_PORTED_MO -> {
        // DB 삭제: MO_NOTISEND에서 SERVERTYPE='H' 레코드 삭제
        dbDelMO_NOTISEND(request)
        // 메시지 전송: 등기문자 전송
        mt_NOTISending(qItem, request)
    }
    // ESMClass 20, 21: 안심문자
    rsv4Protocol11 == NOTI_PLUS_NORMAL_MO || rsv4Protocol11 == NOTI_PLUS_PORTED_MO -> {
        // DB 삭제: MO_NOTISEND에서 SERVERTYPE='H' 레코드 삭제
        dbDelMO_NOTISEND(request)
        // 메시지 전송: 안심문자 전송
        mt_NOTI_PLUS_Sending(qItem, request)
    }
    // 그 외 ESMClass
    else -> {
        // DB 삭제: MOCALLINFO에서 레코드 삭제
        dbDelMOCallInfo(request)
        // 메시지 라우팅: PCS로 라우팅
        routeTRMsg2PCS(qItem, request)
    }
}
```

**핵심 규칙:**
- ESMClass 90/91: `DBDELMO_NOTISEND` + `MT_NOTISending`
- ESMClass 20/21: `DBDELMO_NOTISEND` + `MT_NOTI_PLUS_Sending`
- 그 외: `DBDELMOCallInfo` + `RouteTRMsg2PCS`

## 7. 미과금 대상 MOCALLINFO 처리 규칙

### 규칙: MOCALLINFO 업데이트는 과금 처리와 독립적으로 먼저 수행됨

**TR 결과 수신 시점 처리 순서:**
```kotlin
// SmsResServiceImpl.kt:927-1010
// 1. HBILL 체크 (가장 먼저)
if (gBILLTYPE == '1') {
    return  // MOCALLINFO 업데이트 안 함, 즉시 종료
}

// 2. MOCALLINFO/MO_NOTISEND 업데이트 (과금 처리 전에 먼저 수행)
if (isNotiPlusType || isNotiType) {
    updateMO_NOTISEND(request)  // ESMClass 20, 21, 90, 91
} else {
    updateGIPMOCallInfo(request)  // 그 외 ESMClass
}

// 3. 과금 처리 분기
if (!gMOTRBILL) {
    processMOBilling(...)  // 즉시 과금 처리
} else {
    enqueueVbillMoTR(...)  // VBILL_MO 큐로 전송
}
```

**핵심 규칙:**

1. **HBILL (billType='1')**:
   - MOCALLINFO 업데이트 안 함 (return으로 즉시 종료)
   - 서드파티에서 처리하므로 여기서는 스킵

2. **VBILL_MO (gMOTRBILL='Y')**:
   - MOCALLINFO/MO_NOTISEND는 업데이트됨 (과금 처리 전에 먼저 수행)
   - 과금은 VBILL_MO 큐로 전송 (`enqueueVbillMoTR()`)
   - **MOCALLINFO는 업데이트되지만, 과금은 별도 큐에서 처리**

3. **일반 과금 (!gMOTRBILL)**:
   - MOCALLINFO/MO_NOTISEND는 업데이트됨
   - 과금은 즉시 처리 (`processMOBilling()`)
   - **MOCALLINFO 업데이트 후 즉시 과금 처리**

**중요:**
- MOCALLINFO 업데이트는 과금 처리와 **독립적**으로 수행됨
- gMOTRBILL='Y'인 경우에도 MOCALLINFO는 반드시 업데이트됨
- HBILL인 경우만 MOCALLINFO 업데이트를 스킵함

## 8. 전체 처리 흐름 요약

### MO 전송 시점
1. ESMClass 확인
2. ESMClass 20, 21, 90, 91 → `MO_NOTISEND`에 `SERVERTYPE='V'`로 INSERT
3. 그 외 → `MOCALLINFO`에 INSERT

### TR 결과 수신 시점
1. **HBILL 체크**: billType='1'이면 return (MOCALLINFO 업데이트 안 함)
2. **MOCALLINFO/MO_NOTISEND 업데이트** (과금 처리 전에 먼저 수행)
   - ESMClass 20, 21, 90, 91 → `updateMO_NOTISEND()` 호출
     - `SERVERTYPE='V'`에서 SELECT하여 `SERVERTYPE='H'`로 새 레코드 생성
     - 원본 레코드(`SERVERTYPE='V'`) 삭제
   - 그 외 → `updateGIPMOCallInfo()` 호출
     - INSERT 후 DELETE
3. **과금 처리 분기**:
   - !gMOTRBILL → `processMOBilling()` (즉시 과금 처리)
   - gMOTRBILL='Y' → `enqueueVbillMoTR()` (VBILL_MO 큐로 전송)

### 과금 처리 후
1. `processMOBilling()` 수행 (또는 VBILL_MO 큐에서 처리)
2. ESMClass별 분기:
   - 90/91: `DBDELMO_NOTISEND` + `MT_NOTISending`
   - 20/21: `DBDELMO_NOTISEND` + `MT_NOTI_PLUS_Sending`
   - 그 외: `DBDELMOCallInfo` + `RouteTRMsg2PCS`

## 9. 체크리스트

코드 수정 시 다음 사항을 반드시 확인:

- [ ] ESMClass 90, 91이 MO 전송 시 `MO_NOTISEND`에 저장되는가?
- [ ] ESMClass 90, 91이 TR 수신 시 `updateMO_NOTISEND()`를 호출하는가?
- [ ] `updateMO_NOTISEND()`에서 `SERVERTYPE='H'`로 변경하는가?
- [ ] `updateMO_NOTISEND()`에서 원본 레코드(`SERVERTYPE='V'`)를 삭제하는가?
- [ ] `processMOBilling()` 완료 후 ESMClass별 분기 처리가 구현되어 있는가?
- [ ] ESMClass 90/91 분기에서 `DBDELMO_NOTISEND` + `MT_NOTISending`이 호출되는가?
- [ ] ESMClass 20/21 분기에서 `DBDELMO_NOTISEND` + `MT_NOTI_PLUS_Sending`이 호출되는가?
- [ ] 그 외 ESMClass 분기에서 `DBDELMOCallInfo` + `RouteTRMsg2PCS`가 호출되는가?
- [ ] HBILL (billType='1')인 경우 MOCALLINFO 업데이트를 스킵하는가?
- [ ] VBILL_MO (gMOTRBILL='Y')인 경우에도 MOCALLINFO가 업데이트되는가?
- [ ] MOCALLINFO 업데이트가 과금 처리 전에 먼저 수행되는가?
