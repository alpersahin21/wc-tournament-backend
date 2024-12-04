CREATE TABLE IF NOT EXISTS user (
                                    id CHAR(36) NOT NULL PRIMARY KEY,
                                    username VARCHAR(255) NOT NULL,
                                    level INT NOT NULL,
                                    coins INT NOT NULL,
                                    country VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tournament (
                                          id CHAR(36) NOT NULL PRIMARY KEY,
                                          start_time DATETIME NOT NULL,
                                          end_time DATETIME NOT NULL,
                                          is_active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS tournament_group (
                                                id CHAR(36) NOT NULL PRIMARY KEY,
                                                tournament_id CHAR(36) NOT NULL,
                                                CONSTRAINT fk_tournament_group_tournament FOREIGN KEY (tournament_id) REFERENCES tournament(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tournament_participation (
        id CHAR(36) NOT NULL PRIMARY KEY,
        score INT NOT NULL,
        is_reward_claimed BOOLEAN NOT NULL,
        tournament_group_id CHAR(36) NOT NULL,
        user_id CHAR(36) NOT NULL,
        CONSTRAINT fk_tournament_participation_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
        CONSTRAINT fk_tournament_participation_tournament_group FOREIGN KEY (tournament_group_id) REFERENCES tournament_group(id) ON DELETE CASCADE
);





