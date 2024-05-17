-- +migrate Up


create table `channel_setting`(
                                  id                      integer                not null primary key AUTO_INCREMENT,
                                  channel_id              VARCHAR(40)            not null default '',
                                  channel_type            smallint               not null default 0,
                                  parent_channel_id       VARCHAR(40)            not null default '',
                                  parent_channel_type     smallint               not null default 0,
                                  created_at              timeStamp              not null DEFAULT CURRENT_TIMESTAMP,      -- 创建时间
                                  updated_at              timeStamp              not null DEFAULT CURRENT_TIMESTAMP       -- 更新时间
);

CREATE UNIQUE INDEX channel_setting_uidx on `channel_setting` (channel_id,channel_type);
-- +migrate Up

-- 修改channel_setting表
ALTER TABLE `channel_setting` ADD COLUMN msg_auto_delete integer not null DEFAULT 0 COMMENT '消息定时删除时间';

-- 修改channel_id字段的长度
ALTER TABLE `channel_setting` MODIFY COLUMN channel_id VARCHAR(80);

