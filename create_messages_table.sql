-- 메시지 테이블 생성
CREATE TABLE IF NOT EXISTS messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    content TEXT NOT NULL,
    send_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id)
);

-- 테이블 구조 확인
DESCRIBE messages;

-- 샘플 데이터 삽입 (테스트용)
-- INSERT INTO messages (sender_id, content) VALUES (1, '환자 상태 확인 부탁드립니다.');
-- INSERT INTO messages (sender_id, content) VALUES (2, '약 처방 완료했습니다.'); 