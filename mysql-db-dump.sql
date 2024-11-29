CREATE TABLE IF NOT EXISTS user (
                      id CHAR(36) NOT NULL PRIMARY KEY,
                      level INT NOT NULL,
                      coins INT NOT NULL,
                      country VARCHAR(255) NOT NULL
);