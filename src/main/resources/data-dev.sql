
-- 현금 1,000,000원 입금
INSERT INTO portfolio_ledger (member_id, delta, reason, ref_id, ts)
VALUES (1, 1000000.0000, 'DEPOSIT', 'seed', NOW(6));

-- 심볼과 캔들 예시
INSERT INTO instruments (stock_code, stock_name, market, currency, created_at, updated_at)
VALUES ('AAPL', 'Apple Inc.', 'NASDAQ', 'USD', NOW(6), NOW(6))
    ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);

INSERT INTO candles (stock_code, tf, ts, open_price, high_price, low_price, close_price, volume)
VALUES
    ('AAPL', '1d', '2025-09-03 09:00:00', 170.0000, 176.0000, 169.5000, 175.5000, 1000000)
    ON DUPLICATE KEY UPDATE close_price = VALUES(close_price);
