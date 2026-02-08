# 일반 항목 도메인 클래스 구현 현황

**기준:** ESMClass_발생가능_규칙.mdc §5 일반 (나머지 통합), §5.1 번호규칙(특번).

---

## 1. 서비스 타입·핸들러 매핑

| 서비스 타입 | 핸들러 클래스 | 구현 여부 | 비고 |
|-------------|---------------|-----------|------|
| NORMAL_MO | NormalMoHandler | ✅ 구현됨 | 일반 나머지(1, 4, 57, 56, 6, 10, 9, 11, 64, 65, 71, 80, 81, 82, 0) 전담 |
| CHARACTER_1584 | Character1584Handler | ✅ 구현됨 | destCID 1584 |
| SMS_MANAGER_638 | SmsManager638Handler | ✅ 구현됨 | destCID 638 |
| SMS_MESSENGER_2580 | SmsMessenger2580Handler | ✅ 구현됨 | destCID 2580 |
| SPECIAL_SHARP | **(전용 핸들러 없음)** | ⚠️ 미구현 | destCID # 포함 시 → Registry에서 NORMAL_MO로 fallback 처리 |

---

## 2. Resolver 분기 (일반 항목 관련)

| 구분 | Resolver 동작 | 비고 |
|------|----------------|------|
| 5.1 번호규칙 | destCID 선행 적용: 1584→CHARACTER_1584, 638→SMS_MANAGER_638, 2580→SMS_MESSENGER_2580, #→SPECIAL_SHARP | ✅ 모든 도메인 공통 적용 |
| 일반 ESMClass 집합 | **명시적 집합 없음**. NOTI_PLUS / NOTI_REGISTERED / CDMA / GSM / FORWARD가 아니면 모두 NORMAL_MO 반환 | 1, 4, 57, 56, 6, 10, 9, 11, 64, 65, 71, 80, 81, 82, 0 → NORMAL_MO |
| 등기문자(90~95) | notiRegisteredEsmClasses → NOTI_REGISTERED (일반 항목 아님) | 규칙 5절 표에는 포함되나 도메인은 등기문자로 분리 |

---

## 3. 일반 항목 ESMClass → 실제 처리

| ESMClass (10진) | 규칙 5절 분류 | Resolver 결과 | 사용 핸들러 |
|-----------------|---------------|---------------|-------------|
| 1 | 일반 MO | destCID 특번 시 해당 타입, 아니면 NORMAL_MO | Character1584 / SmsManager638 / SmsMessenger2580 / NormalMo(또는 fallback) |
| 4, 57, 56, 6, 10, 9, 11 | 일반(나머지) | destCID 특번 시 해당 타입, 아니면 NORMAL_MO | 동일 |
| 64, 65, 71, 80, 81, 82 | 일반(나머지) | 동일 | 동일 |
| 0 | Unknown | NORMAL_MO | NormalMoHandler |
| 90~95 | 규칙 5절 표 포함, 도메인은 등기문자 | NOTI_REGISTERED | NotiRegisteredHandler |

---

## 4. 핸들러별 구현 내용 (일반 항목 관련)

| 핸들러 | serviceType() | handle() | checkQItemVariables | recordMoSuccessInsqStat | recordMoAckBilling | shouldSkipBprintf |
|--------|----------------|----------|---------------------|-------------------------|--------------------|--------------------|
| NormalMoHandler | NORMAL_MO | processSMReqSimple / MOTR 위임 | msgId·traceId 검증 | InsqStat ST_GIPEVENT_MO_OK | InsqStat ST_GIPEVENT_MOACK_BILL_OK | false |
| Character1584Handler | CHARACTER_1584 | processSMReqSimple (1584 entity 재조회 등) | 동일 | 동일 | 동일 | false |
| SmsManager638Handler | SMS_MANAGER_638 | processSMReqSimple | 동일 | 동일 | 동일 | **true** |
| SmsMessenger2580Handler | SMS_MESSENGER_2580 | processSMReqSimple | 동일 | 동일 | 동일 | **true** |
| SPECIAL_SHARP 전용 | — | **없음** | — | — | — | getHandler(SPECIAL_SHARP) → NormalMoHandler 사용 |

---

## 5. 요약

| 항목 | 상태 |
|------|------|
| 일반 나머지 통합 (ESMClass 1, 4, 57, 56, 6, 10, 9, 11, 64, 65, 71, 80, 81, 82, 0) | ✅ NORMAL_MO 한 흐름으로 NormalMoHandler에서 처리 |
| 5.1 번호규칙 (1584 / 638 / 2580 / #) | ✅ Resolver에서 destCID 선행 적용, 1584/638/2580 전용 핸들러 있음 |
| SPECIAL_SHARP (# 포함 등기) | ⚠️ MoServiceType만 있고 전용 핸들러 없음 → NORMAL_MO fallback |
| 일반 ESMClass 집합 명시 | ❌ Resolver에 “일반” set 미정의 (나머지로 NORMAL_MO 처리) |

**권장:**  
- 등기(#) 전용 로직이 필요하면 **SpecialSharpHandler** 추가 후 Registry 등록.  
- “5. 일반 (나머지 통합)” 표와 동일하게 **일반 ESMClass 집합**을 Resolver에 상수로 정의하면 유지보수·검증에 유리함.
