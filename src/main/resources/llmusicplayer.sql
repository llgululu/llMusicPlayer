/*
 Navicat Premium Data Transfer

 Source Server         : 本机
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : llmusicplayer

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 21/08/2023 14:32:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for love_music
-- ----------------------------
DROP TABLE IF EXISTS `love_music`;
CREATE TABLE `love_music`  (
  `lo_id` int(0) NOT NULL AUTO_INCREMENT,
  `lo_music_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '歌曲id',
  `lo_playlist_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '歌单id',
  `lo_time` datetime(0) NULL DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (`lo_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of love_music
-- ----------------------------

-- ----------------------------
-- Table structure for music_api
-- ----------------------------
DROP TABLE IF EXISTS `music_api`;
CREATE TABLE `music_api`  (
  `ap_id` int(0) NOT NULL AUTO_INCREMENT,
  `ap_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接口url',
  `ap_number` int(0) NULL DEFAULT NULL COMMENT '接口获得数据的长度(>=2)',
  `ap_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接口名称',
  PRIMARY KEY (`ap_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of music_api
-- ----------------------------
INSERT INTO `music_api` VALUES (1, '/recommend/resource', 8, '首页每日推荐歌单（第一个歌单没要）');
INSERT INTO `music_api` VALUES (2, '/recommend/songs', 9, '首页每日推荐歌曲   ');
INSERT INTO `music_api` VALUES (3, '/song/url', NULL, '获取音乐Url  ');
INSERT INTO `music_api` VALUES (4, '/recommend/songs', 9999, '每日推荐页歌曲   ');
INSERT INTO `music_api` VALUES (5, '/playlist/catlist', NULL, '歌单广场页歌单分类  ');
INSERT INTO `music_api` VALUES (6, '/top/playlist', 21, '歌单广场根据分类查找歌单 ');
INSERT INTO `music_api` VALUES (7, '/playlist/detail', NULL, '歌单页面获取歌单详情');
INSERT INTO `music_api` VALUES (8, '/playlist/track/all', NULL, '歌单页面获取歌单所有歌曲');
INSERT INTO `music_api` VALUES (9, '/toplist/detail ', NULL, '排行榜详情');
INSERT INTO `music_api` VALUES (10, '/playlist/create?name=我的收藏', NULL, '新建歌单（收藏）');
INSERT INTO `music_api` VALUES (11, '/playlist/tracks', NULL, '对歌单添加或删除歌曲');
INSERT INTO `music_api` VALUES (12, '/cloudsearch', 8, '搜索页的搜索只获得8个数据（type：1单曲、1000歌单）');
INSERT INTO `music_api` VALUES (13, '/cloudsearch?type=1000', 30, '搜索结果页面搜索歌单 获得30个数据');
INSERT INTO `music_api` VALUES (14, '/cloudsearch', 30, '搜索结果页面搜索单曲 获得30个数据');

-- ----------------------------
-- Table structure for setting
-- ----------------------------
DROP TABLE IF EXISTS `setting`;
CREATE TABLE `setting`  (
  `se_id` int(0) NOT NULL AUTO_INCREMENT,
  `se_app_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信小程序appid',
  `se_open_id_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '获取openid的url',
  `se_app_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信小程序appSecret',
  `se_music_cookie` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '网易云音乐登录后的cookie',
  PRIMARY KEY (`se_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of setting
-- ----------------------------
INSERT INTO `setting` VALUES (1, '', 'https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code', '', '');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `u_id` int(0) NOT NULL AUTO_INCREMENT,
  `u_open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'openid微信',
  `u_playlist_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户收藏歌单id',
  `u_update_time` datetime(0) NULL DEFAULT NULL COMMENT '用户最新使用时间',
  `u_sign_time` datetime(0) NULL DEFAULT NULL COMMENT '用户注册时间',
  PRIMARY KEY (`u_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
