// user token table
CREATE TABLE user_token (
    id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    uid VARCHAR(40) NOT NULL DEFAULT '',
    device_level TINYINT NOT NULL DEFAULT 0,
    token VARCHAR(40) NOT NULL DEFAULT ''
);

CREATE UNIQUE INDEX user_uid ON user_token (uid);

// commonChannel info

CREATE TABLE channel_info
(
    id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    channel_id VARCHAR(40) NOT NULL DEFAULT '',
    ban BOOL NOT NULL DEFAULT false,
    disband BOOL NOT NULL DEFAULT false,
    large BOOL NOT NULL DEFAULT false,
);
CREATE UNIQUE INDEX channel_id on channel_info(channel_id);

// ip block list
CREATE TABLE ip_block (
    id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    ip VARCHAR(15) NOT NULL DEFAULT ''
);
CREATE UNIQUE INDEX ip on ip_block(ip);