# MO-NOTISEND 레코드 삭제 문제 분석 및 해결

## 문제 상황
mo-report API로 요청 시 해당하는 MO-NOTISEND 레코드가 있음에도 삭제되지 않는 문제가 발생했습니다.

## 요청 데이터
```json
{
  "msgVerId": 510,
  "encFlag": 0,
  "data": {
    "accessCid": "1571700212",
    "msgId": "001AB3F1",
    "srcCid": "010",
    "srcCallNo": "41907189",
    "destCid": "1584",
    "destCallNo": "1041901346",
    "status": 2
  }
}
```

## 원인 분석

### 1. 처리 흐름
1. `SmsController.receiveSmsResult()`에서 mo-report 요청 수신
2. MOCALLINFO 조회 시도 → 없으면 MO_NOTISEND 조회
3. MO_NOTISEND 조회 성공 시 `buildMoCallInfoFromMoNotiForReport()` 호출
4. `processMoReport()` 호출하여 처리

### 2. 문제 발생 지점

#### `processMoReport()` 메서드 (SmsResServiceImpl.kt:4400-4431)
```kotlin
val isNotiOrNotiPlusForBranch = if (isFromMONotISend) {
    // MOCALLINFO 없고 MO_NOTISEND 있음 → 안심/등기로 처리
    true
} else {
    // MOCALLINFO 있음 → ESMClass 기준으로 판단
    val effectiveEsmClassForBranch = moCallInfo.esmClass ?: ...
    (effectiveEsmClassForBranch == NOTI_PLUS_NORMAL_MO || ...)
}

if (isNotiOrNotiPlusForBranch) {
    processESMClassBranch(...)
}
```

**문제점:**
- `moNotiToDelete != null` (MO_NOTISEND에서 온 경우)이면 `isNotiOrNotiPlusForBranch = true`로 설정됨
- 따라서 `processESMClassBranch()`가 호출됨
- 하지만 `processESMClassBranch()` 내부에서 ESMClass가 안심/등기(20, 21, 90, 91)가 아니면 else 분기로 이동
- **else 분기에서는 `dbDelMOCallInfo()`만 호출하고 MO_NOTISEND 삭제를 수행하지 않음**

#### `processESMClassBranch()` 메서드의 else 분기 (SmsResServiceImpl.kt:3622-3637)
```kotlin
else -> {
    // DB 삭제: MOCALLINFO에서 레코드 삭제
    dbDelMOCallInfo(request, loggerName)
    
    // 메시지 라우팅: PCS로 라우팅
    routeTRMsg2PCS(qItem, request, gipHttpMoAccess, smsQLib, loggerName)
    // ❌ MO_NOTISEND 삭제 로직이 없음!
}
```

### 3. 삭제가 수행되는 경우
MO_NOTISEND 삭제는 다음 경우에만 수행되었습니다:
1. **BILLTYPE='1' (비과금)** 경우: `processMoReport()`의 4367-4391 라인
2. **안심/등기 (ESMClass 20, 21, 90, 91)** 경우: `processESMClassBranch()` 내부

### 4. 삭제가 수행되지 않는 경우
- **일반 MO (ESMClass가 안심/등기가 아님)** + **MO_NOTISEND에서 조회된 경우**
  - `processESMClassBranch()`의 else 분기로 이동
  - MO_NOTISEND 삭제 로직이 없어서 레코드가 남음

## 해결 방법

### 수정 내용

#### 1. `processMoReport()`의 일반 MO 경로에 MO_NOTISEND 삭제 로직 추가
`isNotiOrNotiPlusForBranch`가 false인 경우 (일반 MO)에도 MO_NOTISEND 삭제를 수행하도록 수정:

```kotlin
} else {
    // 일반 MO 경로: processESMClassBranch의 else 분기에서 MO_NOTISEND 삭제를 수행하지 않으므로 여기서 처리
    if (moNotiToDelete != null && !skipMoNotiDelete) {
        try {
            moNotISendRepository.delete(moNotiToDelete)
            // 로그 기록
        } catch (e: Exception) {
            // 에러 로그 기록
        }
    }
}
```

#### 2. `processESMClassBranch()`의 else 분기에 MO_NOTISEND 삭제 로직 추가
일반 MO 케이스에서도 MO_NOTISEND 삭제를 수행하도록 수정:

```kotlin
else -> {
    // DB 삭제: MOCALLINFO에서 레코드 삭제
    dbDelMOCallInfo(request, loggerName)

    // MO_NOTISEND 삭제: moNotiToDelete가 있으면 삭제 수행
    if (moNotiToDelete != null && !skipMoNotiDelete) {
        try {
            moNotISendRepository.delete(moNotiToDelete)
            // 로그 기록
        } catch (e: Exception) {
            // 에러 로그 기록
        }
    }

    // 메시지 라우팅: PCS로 라우팅
    routeTRMsg2PCS(qItem, request, gipHttpMoAccess, smsQLib, loggerName)
}
```

## 수정 후 동작 흐름

### 케이스 1: 일반 MO + MO_NOTISEND에서 조회
1. `moNotiToDelete != null` → `isNotiOrNotiPlusForBranch = true`
2. `processESMClassBranch()` 호출
3. ESMClass가 안심/등기가 아님 → else 분기로 이동
4. **MO_NOTISEND 삭제 수행** ✅
5. `dbDelMOCallInfo()` 호출
6. `routeTRMsg2PCS()` 호출

### 케이스 2: 일반 MO + MOCALLINFO에서 조회
1. `moNotiToDelete == null` → `isNotiOrNotiPlusForBranch = false`
2. `processMoReport()`의 else 블록 실행
3. **MO_NOTISEND 삭제는 불필요** (MOCALLINFO 경로이므로)
4. 일반 MO 처리 로직 수행

### 케이스 3: 안심/등기 + MO_NOTISEND에서 조회
1. `moNotiToDelete != null` → `isNotiOrNotiPlusForBranch = true`
2. `processESMClassBranch()` 호출
3. ESMClass가 안심/등기 → 해당 분기로 이동
4. **MO_NOTISEND 삭제 수행** ✅
5. 안심/등기 처리 로직 수행

## 검증 방법

1. mo-report API로 일반 MO 케이스 요청 전송
2. MO_NOTISEND 레코드가 조회되는지 확인
3. 요청 처리 후 MO_NOTISEND 레코드가 삭제되었는지 확인
4. 로그에서 다음 메시지 확인:
   - `[processESMClassBranch] 일반 분기: MO_NOTISEND 삭제 완료` 또는
   - `[processMoReport] 일반 MO 경로: MO_NOTISEND 삭제 완료`

## 참고 사항

- `skipMoNotiDelete` 플래그가 true인 경우 삭제를 스킵합니다 (동일 MSGID로 생략한 경우)
- MO_NOTISEND 삭제는 `moNotISendRepository.delete(moNotiToDelete)`로 수행됩니다
- 삭제 실패 시 WARN 레벨 로그가 기록됩니다
