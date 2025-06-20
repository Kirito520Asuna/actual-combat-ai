/*
 Navicat Premium Data Transfer

 Source Server         : 124.70.23.222
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 124.70.23.222:3306
 Source Schema         : im

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 19/02/2023 15:33:42
*/
DROP DATABASE IF EXISTS `master`;
CREATE DATABASE `master` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

DROP DATABASE IF EXISTS `im`;
CREATE DATABASE `im` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `im`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for apply
-- ----------------------------
DROP TABLE IF EXISTS `apply`;
CREATE TABLE `apply`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` bigint(20) NULL DEFAULT NULL,
  `tid` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of apply
-- ----------------------------
INSERT INTO `apply` VALUES (4, 1, 2);
INSERT INTO `apply` VALUES (5, 1, 3);

/*
 Navicat Premium Data Transfer

 Source Server         : 虚拟机
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 192.168.200.128:3306
 Source Schema         : im

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 27/11/2023 10:00:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend`  (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                           `uid` bigint(20) NULL DEFAULT NULL,
                           `fid` bigint(20) NULL DEFAULT NULL,
                           `has_remark_name` tinyint(1) NOT NULL DEFAULT 0,
                           `remark_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Records of friend
-- ----------------------------
INSERT INTO `friend` VALUES (18, 2, 1,false,null);
INSERT INTO `friend` VALUES (19, 1, 2,false,null);
INSERT INTO `friend` VALUES (20, 3, 1,false,null);
INSERT INTO `friend` VALUES (21, 1, 3,false,null);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `from` bigint(20) NULL DEFAULT NULL COMMENT '来源id',
                            `to` bigint(20) NULL DEFAULT NULL COMMENT '给谁',
                            `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型  ',
                            `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
                            `time` datetime NULL DEFAULT NULL,
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for user 弃用
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user 弃用
-- ----------------------------
INSERT INTO `user` VALUES (1, 'user1','user1', '$10$pl9yg8GotBQysL75OvXkI.an6u52mQ4kseoPQjUO3g0fjs2Ix.Fpi', 'https://cdn.uviewui.com/uview/album/4.jpg');
INSERT INTO `user` VALUES (2, 'user2','user2', '$10$pl9yg8GotBQysL75OvXkI.an6u52mQ4kseoPQjUO3g0fjs2Ix.Fpi', 'https://img2.baidu.com/it/u=260211041,3935441240&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1676048400&t=1f746e561a2d0cdd7abdd8567e3f620d');
INSERT INTO `user` VALUES (3, 'user3','user3', '$10$pl9yg8GotBQysL75OvXkI.an6u52mQ4kseoPQjUO3g0fjs2Ix.Fpi', 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg');
INSERT INTO `user` VALUES (4, 'user4','user4', '$10$pl9yg8GotBQysL75OvXkI.an6u52mQ4kseoPQjUO3g0fjs2Ix.Fpi', 'https://img2.baidu.com/it/u=1035356506,3713698341&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1675962000&t=6ef8fe24b49bb84eca0feeae9ec678d5');

SET FOREIGN_KEY_CHECKS = 1;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_user
-- ----------------------------
DROP TABLE IF EXISTS `chat_user`;
CREATE TABLE `chat_user`  (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `chat_id` bigint(20) NOT NULL COMMENT '聊天窗口id',
                              `chat_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型 群聊|私聊',
                              `user_id` bigint(20) NOT NULL COMMENT '用户id',
                              `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '聊天窗口关联用户' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_window
-- ----------------------------
DROP TABLE IF EXISTS `chat_window`;
CREATE TABLE `chat_window`  (
                                `chat_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '聊天窗口id',
                                `chat_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型 群聊|私聊',
                                `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
                                `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                                PRIMARY KEY (`chat_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '聊天窗口' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
                                 `id` int(11) NOT NULL AUTO_INCREMENT,
                                 `chat_id` bigint(20) NOT NULL COMMENT '聊天窗口id',
                                 `message_id` bigint(20) NOT NULL COMMENT '消息id',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '聊天窗口关联消息' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
