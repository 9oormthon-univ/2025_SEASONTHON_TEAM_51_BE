INSERT INTO users (provider, subject, nickname, created_at, updated_at)
VALUES ('DEV', 'u1', 'dev-user-1', NOW(6), NOW(6))
    ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);

-- 현금 1,000,000원 입금
INSERT INTO portfolio_ledger (user_id, delta, reason, ref_id, ts)
VALUES (1, 1000000.0000, 'DEPOSIT', 'seed', NOW(6));

-- 심볼과 캔들 예시
INSERT INTO instruments (stock_code, stock_name, market, currency, created_at, updated_at)
VALUES ('AAPL', 'Apple Inc.', 'NASDAQ', 'USD', NOW(6), NOW(6))
    ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);

INSERT INTO candles (stock_code, tf, ts, open_price, high_price, low_price, close_price, volume)
VALUES
    ('AAPL', '1d', '2025-09-03 09:00:00', 170.0000, 176.0000, 169.5000, 175.5000, 1000000)
    ON DUPLICATE KEY UPDATE close_price = VALUES(close_price);

INSERT INTO learning_steps (id, step_title, content, rule_json, next_step_id, created_at, updated_at)
SELECT 1, '주식 기초 이해',
       '주식의 기본 개념과 매매 방식에 대해 학습합니다.',
       JSON_OBJECT('difficulty','EASY'), 2, NOW(6), NOW(6)
    WHERE NOT EXISTS (SELECT 1 FROM learning_steps WHERE id = 1);

-- Step 2
INSERT INTO learning_steps (id, step_title, content, rule_json, next_step_id, created_at, updated_at)
SELECT 2, '캔들 차트 읽기',
       '봉 차트를 통해 시가, 고가, 저가, 종가를 이해합니다.',
       JSON_OBJECT('difficulty','NORMAL'), 3, NOW(6), NOW(6)
    WHERE NOT EXISTS (SELECT 1 FROM learning_steps WHERE id = 2);

-- Step 3
INSERT INTO learning_steps (id, step_title, content, rule_json, next_step_id, created_at, updated_at)
SELECT 3, '매수/매도 전략',
       '투자 전략과 주문 방식에 대해 배웁니다.',
       JSON_OBJECT('difficulty','HARD'), NULL, NOW(6), NOW(6)
    WHERE NOT EXISTS (SELECT 1 FROM learning_steps WHERE id = 3);

-- ========== LEARNING PROGRESS (member_id = 1) ==========
INSERT INTO learning_progress (member_id, step_id, status, done_at, created_at, updated_at)
SELECT 1, 1, 'ONGOING', NULL, NOW(6), NOW(6)
    WHERE NOT EXISTS (SELECT 1 FROM learning_progress WHERE member_id = 1 AND step_id = 1);

INSERT INTO learning_progress (member_id, step_id, status, done_at, created_at, updated_at)
SELECT 1, 2, 'LOCKED', NULL, NOW(6), NOW(6)
    WHERE NOT EXISTS (SELECT 1 FROM learning_progress WHERE member_id = 1 AND step_id = 2);

INSERT INTO learning_progress (member_id, step_id, status, done_at, created_at, updated_at)
SELECT 1, 3, 'LOCKED', NULL, NOW(6), NOW(6)
    WHERE NOT EXISTS (SELECT 1 FROM learning_progress WHERE member_id = 1 AND step_id = 3);

-- ========== QUIZZES (각 스텝별 1문제) ==========
INSERT INTO quizzes (quiz_id, step_id, question, reward_capital, created_at, updated_at)
SELECT 1, 1, '주식의 기본 단위는 무엇일까요?', 100.00, NOW(6), NOW(6)
    WHERE NOT EXISTS (SELECT 1 FROM quizzes WHERE quiz_id = 1);

INSERT INTO quizzes (quiz_id, step_id, question, reward_capital, created_at, updated_at)
SELECT 2, 2, '하루 동안의 시가, 고가, 저가, 종가를 보여주는 차트는?', 150.00, NOW(6), NOW(6)
    WHERE NOT EXISTS (SELECT 1 FROM quizzes WHERE quiz_id = 2);

INSERT INTO quizzes (quiz_id, step_id, question, reward_capital, created_at, updated_at)
SELECT 3, 3, '시장가 주문과 지정가 주문의 차이는 무엇인가요?', 200.00, NOW(6), NOW(6)
    WHERE NOT EXISTS (SELECT 1 FROM quizzes WHERE quiz_id = 3);

-- ========== QUIZ OPTIONS (option_no: 1~4, 퀴즈별 고정) ==========
-- Quiz 1
INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 1, '1주', TRUE, 1
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 1 AND option_no = 1);

INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 1, '10주', FALSE, 2
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 1 AND option_no = 2);

INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 1, '100주', FALSE, 3
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 1 AND option_no = 3);

INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 1, '1000주', FALSE, 4
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 1 AND option_no = 4);

-- Quiz 2
INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 2, '선 차트', FALSE, 1
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 2 AND option_no = 1);

INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 2, '막대 차트', FALSE, 2
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 2 AND option_no = 2);

INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 2, '캔들 차트', TRUE, 3
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 2 AND option_no = 3);

INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 2, '원형 차트', FALSE, 4
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 2 AND option_no = 4);

-- Quiz 3
INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 3, '시장가는 즉시 체결, 지정가는 원하는 가격에만 체결', TRUE, 1
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 3 AND option_no = 1);

INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 3, '시장가는 예약 주문, 지정가는 당일 주문', FALSE, 2
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 3 AND option_no = 2);

INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 3, '시장가는 무료, 지정가는 수수료 발생', FALSE, 3
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 3 AND option_no = 3);

INSERT INTO quiz_options (quiz_id, option_text, is_correct, option_no)
SELECT 3, '차이가 없다', FALSE, 4
    WHERE NOT EXISTS (SELECT 1 FROM quiz_options WHERE quiz_id = 3 AND option_no = 4);
