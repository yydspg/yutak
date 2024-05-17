-- +migrate Up


create table `app`
(
    app_id VARCHAR(40) NOT NULL DEFAULT ''  COMMENT 'app id',
    app_key VARCHAR(40) NOT NULL DEFAULT ''  COMMENT 'app key',
    status  integer   NOT NULL DEFAULT 0  COMMENT '状态 0.禁用 1.可用',
    created_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP,
    updated_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX app_id on `app` (app_id);

insert into `app`(app_id,app_key,status) VALUES('wukongchat',substring(MD5(RAND()),1,20),1);
-- +migrate Up

ALTER TABLE `app` ADD COLUMN app_name VARCHAR(40) NOT NULL DEFAULT '' COMMENT 'app名字';

ALTER TABLE `app` ADD COLUMN app_logo VARCHAR(400) NOT NULL DEFAULT '' COMMENT 'app logo';
-- +migrate Up

-- 事件表
create table `event`
(
    id         integer       not null primary key AUTO_INCREMENT,
    event      VARCHAR(40)   not null default '',                             -- 事件标示
    `type`       smallint      not null default 0,                             -- 事件类型
    data       VARCHAR(3000) not null default '',                             -- 事件数据
    status     smallint      NOT NULL DEFAULT 0,                              -- 事件状态 0.待发布 1.已发布 2.发布失败,
    reason     VARCHAR(1000) not null default '',                             -- 失败原因
    version_lock integer    NOT NULL DEFAULT 0   comment '乐观锁',
    created_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);
CREATE INDEX event_key on `event` (event);
CREATE INDEX event_type on `event` (type);

-- -- +migrate StatementBegin
-- CREATE TRIGGER event_updated_at
--   BEFORE UPDATE
--   ON `event` for each row
--   BEGIN
--      set NEW.updated_at = NOW(),NEW.version_lock = NEW.version_lock + 1;
--   END;
-- -- +migrate StatementEnd