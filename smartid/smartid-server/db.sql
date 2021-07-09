

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for smart_id_info
-- ----------------------------
DROP TABLE IF EXISTS `smart_id_info`;
CREATE TABLE `smart_id_info`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `biz_type` varchar(63) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '业务类型，唯一',
  `begin_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '开始id，仅记录初始值，无其他含义。初始化时begin_id和max_id应相同',
  `max_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '当前最大id',
  `step` int(11) NULL DEFAULT 0 COMMENT '步长',
  `delta` int(11) NOT NULL DEFAULT 1 COMMENT '每次id增量',
  `remainder` int(11) NOT NULL DEFAULT 0 COMMENT '余数',
  `create_time` timestamp(0) NOT NULL DEFAULT '2010-01-01 00:00:00' COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT '2010-01-01 00:00:00' COMMENT '更新时间',
  `version` bigint(20) NOT NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_biz_type`(`biz_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'id信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of smart_id_info
-- ----------------------------
INSERT INTO `smart_id_info` VALUES (1, 'test', 1, 32900001, 100000, 1, 0, '2018-07-21 23:52:58', '2020-12-15 16:31:22', 330);
INSERT INTO `smart_id_info` VALUES (2, 'test_odd', 1, 1, 100000, 2, 1, '2018-07-21 23:52:58', '2018-07-23 00:39:24', 3);
INSERT INTO `smart_id_info` VALUES (3, 'orders', 1, 730002, 10000, 1, 0, '2010-01-01 00:00:00', '2020-12-22 14:40:53', 74);
INSERT INTO `smart_id_info` VALUES (5, 'orderProduct', 1, 1, 10000, 1, 0, '2010-01-01 00:00:00', '2020-01-15 16:52:53', 1);
INSERT INTO `smart_id_info` VALUES (6, 'goods', 1, 370001, 10000, 1, 0, '2010-01-01 00:00:00', '2020-12-21 20:15:00', 38);
INSERT INTO `smart_id_info` VALUES (7, 'product', 1, 390001, 10000, 1, 0, '2010-01-01 00:00:00', '2020-12-21 20:15:00', 40);
INSERT INTO `smart_id_info` VALUES (8, 'store', 1, 60001, 10000, 1, 0, '2010-01-01 00:00:00', '2020-12-21 14:08:21', 7);
INSERT INTO `smart_id_info` VALUES (9, 'vendor', 1, 90001, 10000, 1, 0, '2010-01-01 00:00:00', '2020-12-21 14:11:18', 10);
INSERT INTO `smart_id_info` VALUES (10, 'notice', 111, 111, 10000, 1, 0, '2010-01-01 00:00:00', '2010-01-01 00:00:00', 1);
INSERT INTO `smart_id_info` VALUES (11, 'orderPay', 1, 660001, 10000, 1, 0, '2010-01-01 00:00:00', '2020-12-22 14:40:53', 67);
INSERT INTO `smart_id_info` VALUES (12, 'bill', 1, 150001, 10000, 1, 0, '2020-01-01 00:00:00', '2020-12-23 00:30:00', 16);
INSERT INTO `smart_id_info` VALUES (13, 'memberName', 1881991272, 1882311272, 10000, 1, 0, '2010-01-01 00:00:00', '2020-12-21 16:03:29', 32);
INSERT INTO `smart_id_info` VALUES (14, 'orderAfs', 1, 500001, 10000, 1, 0, '2010-01-01 00:00:00', '2020-12-21 18:07:07', 59);
INSERT INTO `smart_id_info` VALUES (15, 'goodsExtend', 1, 45001, 1000, 1, 0, '2010-01-01 00:00:00', '2020-12-21 20:15:00', 36);
INSERT INTO `smart_id_info` VALUES (16, 'productCode', 1, 10001, 1000, 1, 0, '2010-01-01 00:00:00', '2010-01-01 00:00:00', 1);
INSERT INTO `smart_id_info` VALUES (17, 'goodsPool', 1, 10001, 1000, 1, 0, '2010-01-01 00:00:00', '2010-01-01 00:00:00', 1);
INSERT INTO `smart_id_info` VALUES (18, 'productPool', 1, 10001, 1000, 1, 0, '2010-01-01 00:00:00', '2010-01-01 00:00:00', 1);

-- ----------------------------
-- Table structure for smart_id_token
-- ----------------------------
DROP TABLE IF EXISTS `smart_id_token`;
CREATE TABLE `smart_id_token`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `token` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'token',
  `biz_type` varchar(63) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '此token可访问的业务类型标识',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` timestamp(0) NOT NULL DEFAULT '2010-01-01 00:00:00' COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT '2010-01-01 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'token信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of smart_id_token
-- ----------------------------
INSERT INTO `smart_id_token` VALUES (1, '0f673adf80504e2eaa552f5d791b644c', 'test', '1', '2017-12-14 16:36:46', '2017-12-14 16:36:48');
INSERT INTO `smart_id_token` VALUES (2, '0f673adf80504e2eaa552f5d791b644c', 'test_odd', '1', '2017-12-14 16:36:46', '2017-12-14 16:36:48');
INSERT INTO `smart_id_token` VALUES (3, '0f673adf80504e2eaa552f5d791b644c', 'orders', '1', '2020-04-08 16:36:46', '2020-04-08 16:36:46');
INSERT INTO `smart_id_token` VALUES (4, '0f673adf80504e2eaa552f5d791b644c', 'orderProduct', '1', '2020-04-08 16:36:46', '2020-04-08 16:36:46');
INSERT INTO `smart_id_token` VALUES (5, '0f673adf80504e2eaa552f5d791b644c', 'goods', '1', '2020-04-08 16:36:46', '2020-04-08 16:36:46');
INSERT INTO `smart_id_token` VALUES (6, '0f673adf80504e2eaa552f5d791b644c', 'product', '1', '2020-04-08 16:36:46', '2020-04-08 16:36:46');
INSERT INTO `smart_id_token` VALUES (7, '0f673adf80504e2eaa552f5d791b644c', 'store', '1', '2020-04-08 16:36:46', '2020-04-08 16:36:46');
INSERT INTO `smart_id_token` VALUES (8, '0f673adf80504e2eaa552f5d791b644c', 'vendor', '1', '2020-04-08 16:36:46', '2020-04-08 16:36:46');
INSERT INTO `smart_id_token` VALUES (9, '0f673adf80504e2eaa552f5d791b644c', 'goods', '1', '2020-04-08 16:36:46', '2020-04-08 16:36:46');
INSERT INTO `smart_id_token` VALUES (10, '0f673adf80504e2eaa552f5d791b644c', 'notice', '1', '2010-01-01 00:00:00', '2010-01-01 00:00:00');
INSERT INTO `smart_id_token` VALUES (11, '0f673adf80504e2eaa552f5d791b644c', 'orderPay', '1', '2010-01-01 00:00:00', '2010-01-01 00:00:00');
INSERT INTO `smart_id_token` VALUES (12, '0f673adf80504e2eaa552f5d791b644c', 'bill', '1', '2020-01-01 00:00:00', '2020-01-01 00:00:00');
INSERT INTO `smart_id_token` VALUES (13, '0f673adf80504e2eaa552f5d791b644c', 'memberName', '1', '2010-01-01 00:00:00', '2010-01-01 00:00:00');
INSERT INTO `smart_id_token` VALUES (14, '0f673adf80504e2eaa552f5d791b644c', 'orderAfs', '1', '2010-01-01 00:00:00', '2010-01-01 00:00:00');
INSERT INTO `smart_id_token` VALUES (16, '0f673adf80504e2eaa552f5d791b644c', 'goodsExtend', '1', '2010-01-01 00:00:00', '2010-01-01 00:00:00');
INSERT INTO `smart_id_token` VALUES (17, '0f673adf80504e2eaa552f5d791b644c', 'productCode', '1', '2010-01-01 00:00:00', '2010-01-01 00:00:00');
INSERT INTO `smart_id_token` VALUES (18, '0f673adf80504e2eaa552f5d791b644c', 'goodsPool', '1', '2010-01-01 00:00:00', '2010-01-01 00:00:00');
INSERT INTO `smart_id_token` VALUES (19, '0f673adf80504e2eaa552f5d791b644c', 'productPool', '1', '2010-01-01 00:00:00', '2010-01-01 00:00:00');

SET FOREIGN_KEY_CHECKS = 1;
