CREATE TABLE IF NOT EXISTS users (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   role VARCHAR(50) NOT NULL,
                                   username VARCHAR(50) NOT NULL,
                                   CONSTRAINT UKqj1hl3a9n83kqdax9ugtqacsd UNIQUE (username),
                                   password VARCHAR(50) NOT NULL
)
