/*
Navicat MySQL Data Transfer

Source Server         : 本地
Source Server Version : 50727
Source Host           : localhost:3306
Source Database       : caller

Target Server Type    : MYSQL
Target Server Version : 50727
File Encoding         : 65001

Date: 2021-02-18 11:30:41
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for task_detail_info
-- ----------------------------
DROP TABLE IF EXISTS `task_detail_info`;
CREATE TABLE `task_detail_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `group_key` varchar(20) NOT NULL COMMENT '任务组ID',
  `biz_key` varchar(20) NOT NULL COMMENT '业务ID',
  `topic` varchar(20) NOT NULL DEFAULT '-1' COMMENT '任务主题',
  `execution_param` varchar(255) NOT NULL COMMENT '执行参数',
  `execution_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '执行时间',
  `execution_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:未上线 1:已上线 2.暂停 3:执行中 4:重试中 5:执行成功 6:执行失败',
  `timeout` int(1) NOT NULL COMMENT '超时时间 单位毫秒',
  `retry_num` tinyint(4) NOT NULL DEFAULT '0' COMMENT '重试次数 默认 0：不执行重试',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_biz_topic_time_uniq_index` (`group_key`,`biz_key`,`topic`,`execution_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='任务信息项表';

-- ----------------------------
-- Records of task_detail_info
-- ----------------------------

-- ----------------------------
-- Table structure for task_log
-- ----------------------------
DROP TABLE IF EXISTS `task_log`;
CREATE TABLE `task_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `group_key` varchar(20) NOT NULL COMMENT '组key',
  `biz_key` varchar(20) NOT NULL COMMENT '业务key',
  `topic` varchar(20) NOT NULL COMMENT '任务主题',
  `execute_param` varchar(512) NOT NULL COMMENT '执行器任务参数',
  `executor_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '执行时间',
  `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
  `retry_count` tinyint(4) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `executor_status` tinyint(4) NOT NULL COMMENT '0:未上线 1:已上线 2.暂停 3:执行中 4:重试中 5:执行成功 6:执行失败',
  `executor_result_msg` text COMMENT '执行结果描述（json结构）',
  `alarm_status` tinyint(4) DEFAULT '0' COMMENT '告警状态：0默认 1-无需告警、2-告警成功、3-告警失败',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `create_time_index` (`create_time`) USING BTREE,
  KEY `group_biz_topic_uniq_index` (`group_key`,`biz_key`,`topic`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='任务日志记录表';

-- ----------------------------
-- Records of task_log
-- ----------------------------

-- ----------------------------
-- Table structure for task_registry
-- ----------------------------
DROP TABLE IF EXISTS `task_registry`;
CREATE TABLE `task_registry` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_key` varchar(20) NOT NULL COMMENT '组key',
  `biz_key` varchar(20) NOT NULL COMMENT '业务key',
  `registry_address` varchar(255) NOT NULL DEFAULT '' COMMENT '客户端地址',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_biz_addr_index_uniq` (`group_key`,`biz_key`,`registry_address`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户端注册的地址';

-- ----------------------------
-- Records of task_registry
-- ----------------------------

-- ----------------------------
-- Table structure for task_lock
-- ----------------------------
DROP TABLE IF EXISTS `task_lock`;
CREATE TABLE `task_lock` (
  `lock_name` varchar(50) NOT NULL COMMENT '锁名称',
  PRIMARY KEY (`lock_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of task_lock
-- ----------------------------
INSERT INTO `task_lock` VALUES ('schedule');

-- ----------------------------
-- Table structure for task_group
-- ----------------------------
DROP TABLE IF EXISTS `task_group`;
CREATE TABLE `task_group` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `group_key` varchar(20) NOT NULL DEFAULT '' COMMENT '组名称关键字',
  `biz_key` varchar(20) NOT NULL DEFAULT '' COMMENT '业务名称关键字',
  `address_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '上传地址类型 1:自动上传 2:手动上传',
  `log_retention_days` smallint(4) NOT NULL DEFAULT '0' COMMENT '日志保留天数',
  `address_list` varchar(255) NOT NULL DEFAULT '' COMMENT '地址列表',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT ' 0 未加锁 1 加锁',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `group_biz_index_uniq` (`group_key`,`biz_key`) USING BTREE COMMENT '组合唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='任务组';

-- ----------------------------
-- Records of task_group
-- ----------------------------
INSERT INTO `task_group` VALUES ('1', 'taobao', 'order', '0', '10', '', '0', '2021-02-01 14:41:20', '2021-02-01 14:41:20');

-- ----------------------------
-- Table structure for task_base_info
-- ----------------------------
DROP TABLE IF EXISTS `task_base_info`;
CREATE TABLE `task_base_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `group_key` varchar(20) NOT NULL COMMENT '任务组ID',
  `biz_key` varchar(20) NOT NULL COMMENT '业务ID',
  `topic` varchar(20) NOT NULL DEFAULT '-1' COMMENT '任务主题',
  `description` varchar(255) NOT NULL COMMENT '任务描述',
  `alarm_email` varchar(50) NOT NULL COMMENT '报警邮件',
  `executor_router_strategy` tinyint(4) NOT NULL COMMENT '路由策略 1:轮询 2:随机 3:hash 4:第一台 5:最后一台',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `group_biz_topic_uniq_index` (`group_key`,`biz_key`,`topic`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='基础信息表';

-- ----------------------------
-- Records of task_base_info
-- ----------------------------
INSERT INTO `task_base_info` VALUES ('1', 'taobao', 'order', 'clsExpireOrder', '订单', '123@qq.com', '1', '2021-02-01 14:42:43', '2021-02-01 14:42:43');
