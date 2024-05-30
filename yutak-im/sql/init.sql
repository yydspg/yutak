CREATE TABLE user_token (
    id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uid VARCHAR(40) NOT NULL DEFAULT '',
    device_level TINYINT NOT NULL DEFAULT 0,
    token VARCHAR(40) NOT NULL DEFAULT ''
);

CREATE UNIQUE INDEX user_uid ON user_token (uid);