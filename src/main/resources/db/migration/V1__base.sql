-- charset/콜레이션은 DB 생성 시점에 지정했다고 가정 (utf8mb4)
-- -- ====== USERS (카카오 로그인 도입 전 최소 스키마) ======
-- CREATE TABLE IF NOT EXISTS users (
--                                      id            BIGINT PRIMARY KEY AUTO_INCREMENT,
--                                      provider      VARCHAR(20)  NOT NULL DEFAULT 'DEV',
--     subject       VARCHAR(64)  NULL,            -- KAKAO 고유 id (미사용시 NULL)
--     nickname      VARCHAR(100) NULL,
--     created_at    DATETIME(6)  NULL,
--     updated_at    DATETIME(6)  NULL,
--     UNIQUE KEY uq_provider_subject (provider, subject)
--     );
-- USERS: 카카오 전용 로그인 스키마
CREATE TABLE users (
   member_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
   provider          VARCHAR(20)  NOT NULL DEFAULT 'KAKAO',
   subject           VARCHAR(64)  NOT NULL,
   nickname          VARCHAR(100) NULL,
   profile_image_url VARCHAR(512) NULL,
   created_at        DATETIME(6)  NULL,
   updated_at        DATETIME(6)  NULL,
   UNIQUE KEY uq_provider_subject (provider, subject)
);

-- ====== INSTRUMENTS / CANDLES ======
CREATE TABLE IF NOT EXISTS instruments (
    stock_code    VARCHAR(32)  NOT NULL,
    stock_name    VARCHAR(255) NOT NULL,
    market        VARCHAR(16)  NOT NULL DEFAULT 'KRX',
    currency      VARCHAR(3)   NOT NULL DEFAULT 'KRW',
    created_at    DATETIME(6)  NULL,
    updated_at    DATETIME(6)  NULL,
    PRIMARY KEY (stock_code)
    );

CREATE TABLE IF NOT EXISTS candles (
    stock_code    VARCHAR(32)  NOT NULL,
    tf            VARCHAR(4)   NOT NULL,        -- '1m','1d' 등
    ts            DATETIME(6)  NOT NULL,        -- 봉 시작 시각 (KST 기준 저장 권장)
    open_price    DECIMAL(18,4) NOT NULL,
    high_price    DECIMAL(18,4) NOT NULL,
    low_price     DECIMAL(18,4) NOT NULL,
    close_price   DECIMAL(18,4) NOT NULL,
    volume        BIGINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (stock_code, tf, ts)
    );

-- ====== ORDERS / TRADES / POSITIONS / LEDGER ======
-- enum은 MySQL ENUM을 사용 (운영 선호가 VARCHAR라면 V2에서 변경)
CREATE TABLE IF NOT EXISTS orders (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    stock_code      VARCHAR(32)  NOT NULL,
    side            ENUM('BUY','SELL')       NOT NULL,
    type            ENUM('MARKET','LIMIT')   NOT NULL,
    price           DECIMAL(18,4) NULL,              -- LIMIT만 사용
    qty             DECIMAL(18,0) NOT NULL,
    filled_qty      DECIMAL(18,0) NOT NULL DEFAULT 0,
    status          ENUM('OPEN','PARTIAL','FILLED','CANCELED') NOT NULL,
    client_order_id VARCHAR(64)  NULL,
    valid_after_ts  DATETIME(6)  NULL,               -- 예약 주문 유효 시작 (개장 전)
    time_in_force   ENUM('DAY','GTC') NOT NULL DEFAULT 'DAY',
    created_at      DATETIME(6)  NULL,
    updated_at      DATETIME(6)  NULL,
    filled_at       DATETIME(6)  NULL,

    UNIQUE KEY uq_client_order (user_id, client_order_id),
    KEY ix_user_status_created (user_id, status, created_at),
    KEY ix_stock_code (stock_code)
    -- FK는 초기에 생략(개발 민첩성). 운영 전 안정화 때 FK 추가 고려.
    );

CREATE TABLE IF NOT EXISTS trades (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id    BIGINT       NOT NULL,
    stock_code  VARCHAR(32)  NOT NULL,
    price       DECIMAL(18,4) NOT NULL,
    qty         DECIMAL(18,0) NOT NULL,
    ts          DATETIME(6)  NOT NULL,
    KEY ix_order (order_id),
    KEY ix_symbol_ts (stock_code, ts)
    );

CREATE TABLE IF NOT EXISTS positions (
    user_id     BIGINT       NOT NULL,
    stock_code  VARCHAR(32)  NOT NULL,
    qty         DECIMAL(18,0) NOT NULL DEFAULT 0,
    avg_price   DECIMAL(18,4) NOT NULL DEFAULT 0,
    updated_at  DATETIME(6)  NULL,
    PRIMARY KEY (user_id, stock_code)
    );

CREATE TABLE IF NOT EXISTS portfolio_ledger (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT        NOT NULL,
    delta       DECIMAL(18,4) NOT NULL,           -- +입금 / -출금 / 거래± / 수수료-
    reason      ENUM('DEPOSIT','WITHDRAW','TRADE','FEE','QUIZ_REWARD','MISSION_REWARD') NOT NULL,
    ref_id      VARCHAR(64)   NULL,               -- 관련 주문/퀴즈 id 등
    ts          DATETIME(6)   NOT NULL,
    KEY ix_user_ts (user_id, ts)
    );

/* =========================
 * LEARNING / QUIZ TABLES
 * ========================= */

/* 학습 단계 */
CREATE TABLE IF NOT EXISTS learning_steps (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    step_title   VARCHAR(255) NOT NULL,
    content      TEXT         NOT NULL,
    rule_json    JSON         NULL,
    next_step_id BIGINT       NULL,        -- 다음 단계 없으면 NULL
    created_at   DATETIME(6)  NULL,
    updated_at   DATETIME(6)  NULL,
    KEY ix_learning_steps_next (next_step_id)
    );

/* 사용자별 학습 진행상태 */
CREATE TABLE IF NOT EXISTS learning_progress (
    id         BIGINT       PRIMARY KEY AUTO_INCREMENT,
    member_id  BIGINT       NOT NULL,
    step_id    BIGINT       NOT NULL,
    status     ENUM('LOCKED','ONGOING','DONE') NOT NULL,
    done_at    DATETIME(6)  NULL,
    created_at DATETIME(6)  NULL,
    updated_at DATETIME(6)  NULL,
    UNIQUE KEY uq_member_step (member_id, step_id),
    KEY ix_member_status (member_id, status)
    );

/* 퀴즈(객관식) */
CREATE TABLE IF NOT EXISTS quizzes (
    quiz_id        BIGINT        PRIMARY KEY AUTO_INCREMENT,
    step_id        BIGINT        NOT NULL,       -- 어느 학습 단계 소속인지
    question       TEXT          NOT NULL,
    reward_capital DECIMAL(18,4) NOT NULL,
    created_at     DATETIME(6)   NULL,
    updated_at     DATETIME(6)   NULL,
    KEY ix_quizzes_step (step_id)
    );

/* 퀴즈 보기 */
CREATE TABLE IF NOT EXISTS quiz_options (
    option_id   BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    quiz_id     BIGINT NOT NULL,
    option_no   INT NOT NULL,                 -- 각 퀴즈 내부 번호 (1~4)
    option_text VARCHAR(255) NOT NULL,
    is_correct  BOOLEAN NOT NULL,

    CONSTRAINT uq_quiz_option UNIQUE (quiz_id, option_no), -- 퀴즈 내 중복 방지
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id)
);




/* 퀴즈 응시 기록 */
CREATE TABLE IF NOT EXISTS quiz_attempts (
    attempt_id       BIGINT       PRIMARY KEY AUTO_INCREMENT,
    member_id        BIGINT       NOT NULL,
    quiz_id          BIGINT       NOT NULL,
    selected_option_id BIGINT     NULL,
    is_correct       TINYINT(1)   NOT NULL,
    rewarded         TINYINT(1)   NOT NULL,
    attempted_at     DATETIME(6)  NOT NULL,
    UNIQUE KEY uq_member_quiz (member_id, quiz_id),   -- 같은 퀴즈 중복 응시 방지
    KEY ix_attempt_member (member_id)
    );


-- ====== 시드(옵션): dev 환경에서만 data-dev.sql 따로 로드 권장 ======
