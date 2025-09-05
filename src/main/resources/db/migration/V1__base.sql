-- ====== USERS ======
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    provider      VARCHAR(20)  NOT NULL DEFAULT 'DEV',
    subject       VARCHAR(64)  NULL,
    nickname      VARCHAR(100) NULL,
    created_at    DATETIME(6)  NULL,
    updated_at    DATETIME(6)  NULL,
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
    tf            VARCHAR(4)   NOT NULL,
    ts            DATETIME(6)  NOT NULL,
    open_price    DECIMAL(18,4) NOT NULL,
    high_price    DECIMAL(18,4) NOT NULL,
    low_price     DECIMAL(18,4) NOT NULL,
    close_price   DECIMAL(18,4) NOT NULL,
    volume        BIGINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (stock_code, tf, ts)
    );

-- ====== ORDERS / TRADES / POSITIONS / LEDGER ======
CREATE TABLE IF NOT EXISTS orders (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    stock_code      VARCHAR(32)  NOT NULL,
    side            ENUM('BUY','SELL')       NOT NULL,
    type            ENUM('MARKET','LIMIT')   NOT NULL,
    price           DECIMAL(18,4) NULL,
    qty             DECIMAL(18,0) NOT NULL,
    filled_qty      DECIMAL(18,0) NOT NULL DEFAULT 0,
    status          ENUM('OPEN','PARTIAL','FILLED','CANCELED') NOT NULL,
    client_order_id VARCHAR(64)  NULL,
    valid_after_ts  DATETIME(6)  NULL,
    time_in_force   ENUM('DAY','GTC') NOT NULL DEFAULT 'DAY',
    created_at      DATETIME(6)  NULL,
    updated_at      DATETIME(6)  NULL,
    filled_at       DATETIME(6)  NULL,
    UNIQUE KEY uq_client_order (user_id, client_order_id),
    KEY ix_user_status_created (user_id, status, created_at),
    KEY ix_stock_code (stock_code)
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
    delta       DECIMAL(18,4) NOT NULL,
    reason      ENUM('DEPOSIT','WITHDRAW','TRADE','FEE','QUIZ_REWARD','MISSION_REWARD') NOT NULL,
    ref_id      VARCHAR(64)   NULL,
    ts          DATETIME(6)   NOT NULL,
    KEY ix_user_ts (user_id, ts)
    );
