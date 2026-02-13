-- SMS.HTTP_MOSEND_ACCESS 테이블에 로우 1건 삽입 (샘플)
INSERT INTO SMS.HTTP_MOSEND_ACCESS (
    LOG_NO,
    CID,
    IP_ADDR,
    PORT_NO,
    QUEUE_NO,
    FX,
    FY,
    FLAG,
    RC,
    TC,
    DESCRIPTION,
    UPDATE_FLAG,
    CP_NAME,
    CP_PHONE,
    SKT_NAME,
    SKT_PHONE,
    CP_TEAM,
    CP_PERSONNEL,
    CP_SKT_TEAM,
    BILL_TYPE,
    LOG_FLAG,
    LIMIT_CHECK_FLAG,
    GIPVERID,
    DESIRE_NODE,
    CURRENT_NODE,
    RM_FLAG,
    AES_KEY_BASE64,
    IV_BASE64,
    CP_URL,
    MO_TR_BILL
) VALUES (
    '0001',                    -- LOG_NO (PK)
    'TESTCP0001',              -- CID
    '192.168.0.1',             -- IP_ADDR
    8080,                      -- PORT_NO
    1,                         -- QUEUE_NO
    60,                        -- FX (제한시간 초)
    10,                        -- FY (Flow Time 내 최대 메시지 수)
    '0',                       -- FLAG (0:동기, 1:비동기)
    3,                         -- RC (Retry 횟수)
    5,                         -- TC (Timeout 초)
    '테스트 CP 연동',           -- DESCRIPTION
    'A',                       -- UPDATE_FLAG (A:Add, U:Update, D:Delete)
    NULL,                      -- CP_NAME
    NULL,                      -- CP_PHONE
    NULL,                      -- SKT_NAME
    NULL,                      -- SKT_PHONE
    NULL,                      -- CP_TEAM
    NULL,                      -- CP_PERSONNEL
    NULL,                      -- CP_SKT_TEAM
    '0',                       -- BILL_TYPE (default)
    1,                         -- LOG_FLAG (default)
    'N',                       -- LIMIT_CHECK_FLAG (default)
    462,                       -- GIPVERID (default)
    NULL,                      -- DESIRE_NODE
    NULL,                      -- CURRENT_NODE
    0,                         -- RM_FLAG (default)
    NULL,                      -- AES_KEY_BASE64
    NULL,                      -- IV_BASE64
    'http://localhost:8080/mo/callback',  -- CP_URL (필수)
    0                          -- MO_TR_BILL (default)
);
