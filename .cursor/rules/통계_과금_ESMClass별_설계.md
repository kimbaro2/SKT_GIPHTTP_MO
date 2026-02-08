# 통계·과금 ESMClass별 설계 (유지보수성 우선)

## DDD 관점 (참조)
- **Application Service(핸들러)**: 통계·과금 호출(InsqStat, bprintf, bill 등)의 **호출 주체**. 각 핸들러 내부에서 서비스 타입별로 분기·호출.
- **도메인 규칙**: “어떤 서비스 타입에 어떤 통계/과금을 적용할지”의 판단 기준. 핸들러 내부·MoServiceTypeResolver 등에 반영.
- **인프라**: InsqStat, bprintf, DB 등 실제 수행 구현. 공용 유틸·서비스에 두며, **언제·어디서 호출할지**는 핸들러가 결정.

## 총원칙
- **MO는 subcode를 구분하지 않는다.** (SM_REQ_SIMPLE / SM_REQ_TRANS_RESULT 등 subcode별 분기 없이 MO 처리.)
- **MO 전송, InsqStat, bprintf, bill 등 모든 기능**은 **ESMClass 호출 클래스 내부에서만 동작**하도록 설계한다.
- **중복 코드라도 유지보수성을 우선**하여, ESMClass 호출 클래스별로 **각각 전부 구현**한다. (호출 클래스 간 로직 공유·추상화를 최소화.)
- **공용동작로직영역에는 비즈니스로직을 배치하지 않는다.**  
  공용 영역(공통 유틸, 공통 서비스 레이어 등)에는 인프라·유틸만 둔다 (예: QITEM 복사, nInforNo 반환, JNA 호출 래퍼).  
  "언제 InsqStat 호출", "언제 bprintf 스킵", "어떤 서비스타입에 어떤 통계/과금 적용" 같은 **판단·분기·비즈니스로직**은 **호출 클래스(MOThreadPool, SmsResServiceImpl, 핸들러 등) 내부**에만 둔다.

## 처리 순서 (최우선)
1. **ESMClass 체크 최우선**: 처리 진입 시 **먼저** ESMClass(또는 MoServiceType)를 확정한다.
2. **이후 QITEM 변수체크**: ESMClass 확정 후, QITEM 필드(msgId, traceId, segment, szCId 등) 검증을 수행한다.
3. **QITEM 변수체크는 ESMClass 호출 클래스 내부에서 구현**: QITEM 검증 로직은 공용 유틸이 아니라, **해당 ESMClass를 사용하는 호출 클래스(MOThreadPool, SmsResServiceImpl, 핸들러 등) 내부**에 private 메서드 등으로 두고, 서비스타입별로 다르게 검사할 수 있게 한다.

## 원칙
- **통계(InsqStat)·과금(ST_GIPEVENT_MOACK_BILL_OK, bprintf 등)은 ESMClass별로 모두 다르다.**
- **호출 클래스별 내부에 배치**: 각 호출 클래스(MOThreadPool, SmsResServiceImpl, 핸들러 등) **내부**에 ESMClass(또는 MoServiceType)별 분기·호출 로직을 두고, MO 전송·InsqStat·bprintf·bill 등 **전부 해당 클래스 안에서** 동작시킨다.
- **효율성보다 유지보수성**: 새 ESMClass/서비스 타입 추가 시, 해당 호출 클래스 내부만 수정하면 되도록 하고, 공용 영역은 건드리지 않는다.

## 호출 클래스별 책임

### 1. MOThreadPool (`config.threadPool.MOThreadPool`)
- **위치**: MO dequeue → MO 처리 흐름 내 통계·과금 호출 (subcode 구분 없음)
- **역할**: ESMClass 확정 후 **핸들러 위임**만 수행. 비즈니스 분기는 하지 않음.
  - **QITEM 변수체크**: `handler.checkQItemVariables(gstQItem, contextForBody)` 호출
  - **MO 전송 성공 통계 (ST_GIPEVENT_MO_OK)**: `handler.recordMoSuccessInsqStat(gstQItem, context)` 호출
  - **MO-ACK 과금 통계 (ST_GIPEVENT_MOACK_BILL_OK)**: `handler.recordMoAckBilling(gstQItem, context, gBILLTYPE)` 호출
- **분기 키**: `MoServiceType` (ESMClass + destCID로 `MoServiceTypeResolver.resolve()` 사용).  
  핸들러는 `MoServiceHandlerRegistry.getHandler(serviceType)`으로 조회.

### 2. SmsResServiceImpl (`service.impl.SmsResServiceImpl`)
- **위치**: processSMReqSimple(MO-ACK), processMOBilling(bprintf·InsqStat) 등
- **역할**: bprintf 스킵 여부는 **핸들러 위임**으로 결정.
  - **bprintf 스킵 여부**: `shouldSkipBprintfByServiceType(esmClass, destCid)` 내부에서 `handler.shouldSkipBprintf()` 호출
- **분기 키**: `MoServiceType`. `MoServiceTypeResolver.resolve(esmClass, destCid)` 후 `MoServiceHandlerRegistry.getHandler(serviceType)`으로 핸들러 조회.

### 3. 기타 호출처 (SmsMoServiceImpl, BillingDecisionHandler 등)
- 통계·과금 호출이 있는 경우, **해당 클래스 내부**에 ESMClass(또는 MoServiceType)별 `when` 분기를 두고, 경우에 따라 다르게 호출한다.
- 공통화는 “같은 호출 클래스 내”에서만 하고, 호출 클래스 간에는 로직을 복제해도 되며, 유지보수 시 “어디를 고쳐야 하는지”가 분명한 것을 우선한다.

## QITEM 변수체크 (호출 클래스 내부)
- **순서**: ESMClass(서비스타입) 확정 **이후**에 QITEM 필드(msgId, traceId, segment 등) 검증 수행.
- **구현 위치**: QITEM 변수체크는 **ESMClass를 사용하는 호출 클래스 내부**에 private 메서드로 구현. (공용 유틸에 두지 않음.)
- **MOThreadPool**: ESMClass 확정 후 `handler.checkQItemVariables(gstQItem, contextForBody)` 호출로 위임. 검증 로직은 각 핸들러 내부에 구현.

## 분기 정의 위치 (유지보수 시 수정할 곳)
| 구분 | 정의 위치 | 비고 |
|------|-----------|------|
| ESMClass → 서비스 타입 | `MoServiceTypeResolver.resolve(esmClass, destCid)` | ESMClass·번호규칙 추가 시 여기만 수정 |
| QITEM 변수체크 | 각 핸들러 내부 `checkQItemVariables` | 서비스타입별 검사 추가 시 해당 핸들러만 수정 |
| MO 전송 성공 InsqStat | 각 핸들러 내부 `recordMoSuccessInsqStat` | 서비스 타입별 호출/스킵 추가 시 해당 핸들러만 수정 |
| MO-ACK 과금 InsqStat | 각 핸들러 내부 `recordMoAckBilling` | 서비스 타입별 호출/스킵 추가 시 해당 핸들러만 수정 |
| bprintf / processMOBilling | 각 핸들러 내부 `shouldSkipBprintf` | 서비스 타입별 스킵 추가 시 해당 핸들러만 수정 |

## 정리
- 통계·과금은 **호출 클래스별 내부**에 ESMClass(실제 구현은 MoServiceType)별로 **다르게 호출**되도록 분기한다.
- 효율성보다 **유지보수성**을 우선하여, “어느 클래스의 어느 메서드에서 어떤 서비스 타입을 어떻게 처리하는지”가 코드 한 곳에서 읽히도록 한다.

## 새 서비스 타입 추가 시 (체크리스트)
- **MoServiceType** 추가 → **핸들러** 추가 → **MoServiceTypeResolver** 분기 추가 → **MoServiceHandlerRegistry** 매핑 등록.  
- 통계·과금 추가 시: 해당 **핸들러** 내부에 `recordMoSuccessInsqStat`, `recordMoAckBilling`, `shouldSkipBprintf` 등 서비스 타입별 분기 추가. (상세: `ESMClass_서비스_리팩토링_규칙.mdc` §5 체크리스트 참조.)
