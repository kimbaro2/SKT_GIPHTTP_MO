# SKT GIP HTTP 아키텍처 — 현재 구조와 DDD 전환 방향

**문서 목적**: 회의 보고용 — 이전 프로젝트(현재 구조)와 DDD 패턴 전환 방향 정리  
**참조**: `.cursor/rules` (패키지 구조, C/JNA 연동, 코드 컨벤션)

---

## 1. 현재 아키텍처 (레이어드)

HTTP 수신 → 서비스에서 비즈니스·C 연동·DB 처리 혼재.

```mermaid
flowchart TB
    subgraph Client["클라이언트"]
        HTTP["HTTP Request"]
    end

    subgraph App["애플리케이션 (Spring Boot)"]
        direction TB
        subgraph Presentation["Presentation Layer"]
            Ctrl["Controller\n(SmsController 등)"]
        end
        subgraph Business["Business Layer"]
            Svc["Service\n(비즈니스 + C연동 + DB)"]
        end
        subgraph Data["Data Layer"]
            Repo["Repository"]
        end
        subgraph Shared["공통"]
            DTO["DTO / VO"]
            Entity["Entity"]
            Config["Config"]
            Util["Utils"]
            Ex["Exception"]
        end
    end

    subgraph External["외부 / 레거시"]
        GIP["C 모듈 (GIPALL)\nQITEM, InsertIntoSmsQ,\nInsqStat 등"]
        DB[(Altibase)]
    end

    HTTP --> Ctrl
    Ctrl --> Svc
    Svc --> Repo
    Svc --> GIP
    Repo --> DB
    Ctrl -.-> DTO
    Svc -.-> DTO
    Svc -.-> Entity
```

### 1.1 현재 패키지 구조 (rules 기준)

```
com.infra.recv.skt_giphttp_recv/
├── config/           # 설정
├── controller/       # HTTP 진입점
├── service/          # 비즈니스 + C연동 + 조합
├── repository/       # DB 접근
├── entity/           # DB 엔티티
├── dto/              # 요청/응답, JNA(QITEM 등)
├── utils/            # 공통 유틸
└── exception/        # 예외
```

### 1.2 현재 구조의 특징

| 항목 | 설명 |
|------|------|
| **데이터 흐름** | Controller → Service → Repository / C 모듈 |
| **도메인 계층** | 없음. 비즈니스 규칙이 Service에 혼재 |
| **C 연동** | Service에서 QITEM 생성·JNA 호출 직접 수행 |
| **용어** | QITEM, usMsgCode, nRsv4Protocol 등 C/레거시 중심 |

---

## 2. DDD 전환 목표 아키텍처

도메인 계층을 두고, C/JNA·DB·HTTP는 각각 인프라/어댑터로 격리.

```mermaid
flowchart TB
    subgraph Client["클라이언트"]
        HTTP["HTTP Request"]
    end

    subgraph App["애플리케이션"]
        direction TB

        subgraph Presentation["Presentation (Adapter)"]
            Ctrl["Controller"]
            ReqDTO["Request DTO"]
            ResDTO["Response DTO"]
        end

        subgraph Application["Application Layer"]
            AppSvc["Application Service\n(오케스트레이션)"]
        end

        subgraph Domain["Domain Layer"]
            Agg["Aggregate / Entity"]
            DomainSvc["Domain Service"]
            ValueObj["Value Object"]
            DomainEvent["Domain Event"]
        end

        subgraph Infrastructure["Infrastructure (Adapters)"]
            CAdapter["C/JNA Adapter\n(QITEM 변환, GIPALL 호출)"]
            RepoImpl["Repository Impl"]
            Persistence["Persistence"]
        end
    end

    subgraph External["외부"]
        GIP["C 모듈 (GIPALL)"]
        DB[(DB)]
    end

    HTTP --> Ctrl
    Ctrl --> AppSvc
    AppSvc --> DomainSvc
    AppSvc --> Agg
    DomainSvc --> Agg
    AppSvc --> CAdapter
    AppSvc --> RepoImpl
    CAdapter --> GIP
    RepoImpl --> Persistence
    Persistence --> DB
```

### 2.1 DDD 패키지 구조 (제안)

```
com.infra.recv.skt_giphttp_recv/
├── application/          # Application Service
│   └── sms/
├── domain/               # Domain Layer
│   └── sms/
│       ├── model/        # Aggregate, Entity, Value Object
│       ├── service/      # Domain Service
│       └── repository/   # Repository Interface (port)
├── infrastructure/       # Adapters
│   ├── adapter/
│   │   ├── jna/          # QITEM 매핑, GIPALL 호출
│   │   └── persistence/ # Repository 구현
│   └── config/
├── presentation/         # HTTP
│   └── controller/
└── common/               # 공통 DTO, 예외, 유틸
```

### 2.2 계층별 역할

| 계층 | 역할 |
|------|------|
| **Presentation** | HTTP 수신/응답, DTO 변환만 담당 |
| **Application** | 유스케이스 오케스트레이션, 트랜잭션 경계 |
| **Domain** | 비즈니스 규칙, 유비쿼터스 언어(메시지/전달결과/통계 등) |
| **Infrastructure** | C/JNA 연동(QITEM), DB, 설정 등 외부 연동 구현 |

---

## 3. 전환 전·후 비교 다이어그램

```mermaid
flowchart LR
    subgraph Before["현재 (레이어드)"]
        B1[Controller] --> B2[Service\n도메인+C+DB 혼재]
        B2 --> B3[Repo / C]
    end

    subgraph After["DDD 전환 후"]
        A1[Controller] --> A2[App Service]
        A2 --> A3[Domain]
        A2 --> A4[C Adapter]
        A2 --> A5[Repo Impl]
        A3 -.-> A4
        A3 -.-> A5
    end

    Before --> After
```

---

## 4. C 연동 위치 비교

```mermaid
flowchart TB
    subgraph Current["현재"]
        Svc1["Service"]
        Svc1 --> QITEM1["QITEM 생성"]
        Svc1 --> JNA1["JNA 호출"]
        QITEM1 --> GIP1["GIPALL"]
        JNA1 --> GIP1
    end

    subgraph DDD["DDD 전환 후"]
        AppSvc2["Application Service"]
        Domain2["Domain\n(도메인 모델)"]
        Adapter2["C/JNA Adapter"]
        AppSvc2 --> Domain2
        AppSvc2 --> Adapter2
        Adapter2 --> QITEM2["QITEM 변환"]
        Adapter2 --> JNA2["JNA 호출"]
        QITEM2 --> GIP2["GIPALL"]
        JNA2 --> GIP2
    end
```

- **현재**: Service가 도메인 로직과 QITEM/JNA를 함께 처리.
- **DDD**: 도메인은 순수 모델만 다루고, QITEM·GIPALL 호출은 C/JNA Adapter에만 존재.

---

## 5. 요약

| 구분 | 현재 (레이어드) | DDD 전환 후 |
|------|------------------|-------------|
| **구조** | controller / service / repository | application / domain / infrastructure |
| **도메인** | Service에 혼재 | Domain 모델·도메인 서비스로 응집 |
| **C/JNA** | Service에서 직접 사용 | Adapter에서만 QITEM·GIPALL 사용 |
| **용어** | C/레거시(QITEM 등) 중심 | 유비쿼터스 언어(도메인 용어) 중심 |

rules의 **패키지 구조·C 연동 규칙·코드 컨벤션**은 유지하면서, **도메인 계층 분리**와 **Adapter 격리**로 DDD 방향 전환을 목표로 한다는 내용으로 회의 보고에 사용할 수 있습니다.
