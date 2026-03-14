CREATE TABLE IF NOT EXISTS url (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   click_count BIGINT DEFAULT 0 NOT NULL,
                                   created_at DATETIME(6) NOT NULL,
                                   updated_at DATETIME(6) NOT NULL,
                                   original_url VARCHAR(2048) NOT NULL,
                                   short_url VARCHAR(255) NOT NULL,
                                   CONSTRAINT UKqj1hl3a9n83kqdax9ugtqacsd UNIQUE (short_url)
)