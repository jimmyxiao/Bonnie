-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: bonnie_draw_db
-- ------------------------------------------------------
-- Server version	5.7.10-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_info`
--

DROP TABLE IF EXISTS `admin_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin_info` (
  `ADMIN_ID` int(11) NOT NULL AUTO_INCREMENT,
  `USER_TYPE` int(11) NOT NULL,
  `USER_CODE` varchar(64) NOT NULL,
  `USER_PW` varchar(32) NOT NULL,
  `USER_NAME` varchar(128) NOT NULL,
  `EMAIL` varchar(128) DEFAULT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `CREATED_BY` int(11) NOT NULL,
  `UPDATE_DATE` datetime NOT NULL,
  `UPDATED_BY` int(11) NOT NULL,
  PRIMARY KEY (`ADMIN_ID`),
  UNIQUE KEY `ADMIN_INFO_UNIQUE` (`ADMIN_ID`,`USER_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_info`
--

LOCK TABLES `admin_info` WRITE;
/*!40000 ALTER TABLE `admin_info` DISABLE KEYS */;
INSERT INTO `admin_info` VALUES (1,1,'root','123456','管理員',NULL,'2017-09-18 11:05:06',0,'2017-09-18 11:05:06',0),(2,1,'Test01','123456','Test01_update','Test01@gmail.com','2017-09-18 16:54:17',1,'2017-09-18 16:54:54',1);
/*!40000 ALTER TABLE `admin_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category_info`
--

DROP TABLE IF EXISTS `category_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category_info` (
  `CATEGORY_ID` int(11) NOT NULL AUTO_INCREMENT,
  `CATEGORY_NAME` varchar(512) NOT NULL,
  `ENABLE` tinyint(1) NOT NULL,
  `CATEGORY_LEVEL` int(11) NOT NULL,
  `CATEGORY_PARENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`CATEGORY_ID`),
  UNIQUE KEY `CATEGORY_INFO_UNIQUE` (`CATEGORY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category_info`
--

LOCK TABLES `category_info` WRITE;
/*!40000 ALTER TABLE `category_info` DISABLE KEYS */;
INSERT INTO `category_info` VALUES (1,'主目錄',1,0,0),(2,'追蹤',1,1,1),(3,'熱門',1,1,1),(4,'精選',1,1,1),(5,'預設',1,1,1),(6,'預設',1,1,1),(7,'預設',1,1,1),(8,'預設',1,1,1),(9,'預設',1,1,1),(10,'預設',1,1,1),(11,'預設',1,1,1),(12,'預設',1,1,1),(13,'預設',1,1,1),(14,'預設',1,1,1),(15,'預設',1,1,1),(16,'預設',1,1,1),(17,'預設',1,1,1),(18,'預設',1,1,1),(19,'預設',1,1,1),(20,'預設',1,1,1),(21,'預設',1,1,1),(22,'Test',1,2,2);
/*!40000 ALTER TABLE `category_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dictionary`
--

DROP TABLE IF EXISTS `dictionary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dictionary` (
  `DICTIONARY_ID` int(11) NOT NULL AUTO_INCREMENT,
  `DICTIONARY_TYPE` int(11) NOT NULL,
  `DICTIONARY_CODE` int(11) NOT NULL,
  `DICTIONARY_NAME` varchar(512) NOT NULL,
  `DICTIONARY_ORDER` int(11) DEFAULT NULL,
  PRIMARY KEY (`DICTIONARY_ID`),
  UNIQUE KEY `DICTIONARY_UNIQUE` (`DICTIONARY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dictionary`
--

LOCK TABLES `dictionary` WRITE;
/*!40000 ALTER TABLE `dictionary` DISABLE KEYS */;
/*!40000 ALTER TABLE `dictionary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `following`
--

DROP TABLE IF EXISTS `following`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `following` (
  `FOLLOWING_ID` int(11) NOT NULL AUTO_INCREMENT,
  `USER_ID` int(11) NOT NULL,
  `FOLLOWING_USER_ID` int(11) NOT NULL,
  PRIMARY KEY (`FOLLOWING_ID`),
  UNIQUE KEY `FOLLOWING_UNIQUE` (`FOLLOWING_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `following`
--

LOCK TABLES `following` WRITE;
/*!40000 ALTER TABLE `following` DISABLE KEYS */;
/*!40000 ALTER TABLE `following` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `login` (
  `LOGIN_ID` int(11) NOT NULL AUTO_INCREMENT,
  `USER_ID` int(11) NOT NULL,
  `LOGIN_TOKEN` varchar(128) NOT NULL,
  `DEVICE_IP` varchar(32) NOT NULL,
  `DEVICE_INFO` varchar(1024) NOT NULL,
  `SERVICE_KEY` varchar(128) NOT NULL,
  `IS_CURRENT` int(11) NOT NULL,
  `LOGIN_RESULT` int(11) NOT NULL,
  `SESSION_ID` int(11) NOT NULL,
  PRIMARY KEY (`LOGIN_ID`),
  UNIQUE KEY `LOGIN_UNIQUE` (`LOGIN_ID`,`LOGIN_TOKEN`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_setup`
--

DROP TABLE IF EXISTS `system_setup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_setup` (
  `SYSTEM_SETUP_ID` int(11) NOT NULL AUTO_INCREMENT,
  `MAIL_HOST` varchar(512) DEFAULT NULL,
  `MAIL_PORT` int(11) DEFAULT NULL,
  `MAIL_USERNAME` varchar(512) DEFAULT NULL,
  `MAIL_PASSWORD` varchar(512) DEFAULT NULL,
  `MAIL_PROTOCOL` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`SYSTEM_SETUP_ID`),
  UNIQUE KEY `SYSTEM_SETUP_UNIQUE` (`SYSTEM_SETUP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_setup`
--

LOCK TABLES `system_setup` WRITE;
/*!40000 ALTER TABLE `system_setup` DISABLE KEYS */;
INSERT INTO `system_setup` VALUES (1,'smtp.gmail.com',25,'sourcecode.tw@gmail.com','0921614909','smtp');
/*!40000 ALTER TABLE `system_setup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turn_in`
--

DROP TABLE IF EXISTS `turn_in`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `turn_in` (
  `TURN_IN_ID` int(11) NOT NULL AUTO_INCREMENT,
  `USER_ID` int(11) NOT NULL,
  `WORKS_ID` int(11) NOT NULL,
  `TURN_IN_TYPE` int(11) NOT NULL,
  `DESCRIPTION` varchar(1024) NOT NULL,
  `STATUS` int(11) NOT NULL,
  `CREATION_DATE` datetime NOT NULL,
  PRIMARY KEY (`TURN_IN_ID`),
  UNIQUE KEY `TURN_IN_UNIQUE` (`TURN_IN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turn_in`
--

LOCK TABLES `turn_in` WRITE;
/*!40000 ALTER TABLE `turn_in` DISABLE KEYS */;
/*!40000 ALTER TABLE `turn_in` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_folder`
--

DROP TABLE IF EXISTS `user_folder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_folder` (
  `USER_FOLDER_ID` int(11) NOT NULL AUTO_INCREMENT,
  `USER_ID` int(11) NOT NULL,
  `FOLDER_NAME` varchar(512) NOT NULL,
  `FOLDER_ORDER` int(11) DEFAULT NULL,
  PRIMARY KEY (`USER_FOLDER_ID`),
  UNIQUE KEY `USER_FOLDER_UNIQUE` (`USER_FOLDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_folder`
--

LOCK TABLES `user_folder` WRITE;
/*!40000 ALTER TABLE `user_folder` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_folder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_info` (
  `USER_ID` int(11) NOT NULL AUTO_INCREMENT,
  `USER_TYPE` int(11) NOT NULL,
  `USER_CODE` varchar(64) NOT NULL,
  `USER_PW` varchar(32) NOT NULL,
  `USER_NAME` varchar(256) NOT NULL,
  `NICK_NAME` varchar(256) DEFAULT NULL,
  `EMAIL` varchar(256) DEFAULT NULL,
  `SEC_EMAIL` varchar(256) DEFAULT NULL,
  `DESCRIPTION` varchar(1024) DEFAULT NULL,
  `WEB_LINK` varchar(1024) DEFAULT NULL,
  `PHONE_COUNTRY_CODE` varchar(32) DEFAULT NULL,
  `PHONE_NO` varchar(32) DEFAULT NULL,
  `GENDER` int(11) DEFAULT NULL,
  `REG_THIRD_ID` varchar(128) DEFAULT NULL,
  `PROFILE_PICTURE` varchar(1024) DEFAULT NULL,
  `BIRTHDAY` datetime DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `LANGUAGE_ID` int(11) DEFAULT NULL,
  `REG_DATA` varchar(1024) DEFAULT NULL,
  `REG_VALID_DATE` datetime DEFAULT NULL,
  `CREATION_DATE` datetime NOT NULL,
  `CREATED_BY` int(11) NOT NULL,
  `UPDATE_DATE` datetime NOT NULL,
  `UPDATED_BY` int(11) NOT NULL,
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `USER_INFO_UNIQUE` (`USER_ID`,`USER_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `works`
--

DROP TABLE IF EXISTS `works`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `works` (
  `WORKS_ID` int(11) NOT NULL AUTO_INCREMENT,
  `USER_ID` int(11) NOT NULL,
  `PRIVACY_TYPE` int(11) NOT NULL DEFAULT '1',
  `DEVICE_TYPE` int(11) NOT NULL,
  `TITLE` varchar(128) NOT NULL,
  `DESCRIPTION` varchar(1024) NOT NULL,
  `IMAGE_PATH` varchar(1024) DEFAULT NULL,
  `BDW_PATH` varchar(1024) DEFAULT NULL,
  `LANGUAGE_ID` int(11) DEFAULT NULL,
  `COUNTRY_ID` int(11) DEFAULT NULL,
  `STATUS` int(11) NOT NULL DEFAULT '1',
  `CREATION_DATE` datetime NOT NULL,
  `CREATED_BY` int(11) NOT NULL,
  `UPDATE_DATE` datetime NOT NULL,
  `UPDATED_BY` int(11) NOT NULL,
  PRIMARY KEY (`WORKS_ID`),
  UNIQUE KEY `WORKS_UNIQUE` (`WORKS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `works`
--

LOCK TABLES `works` WRITE;
/*!40000 ALTER TABLE `works` DISABLE KEYS */;
/*!40000 ALTER TABLE `works` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `works_category`
--

DROP TABLE IF EXISTS `works_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `works_category` (
  `WORKS_CATEGORY_ID` int(11) NOT NULL AUTO_INCREMENT,
  `CATEGORY_ID` int(11) NOT NULL,
  `WORKS_ID` int(11) NOT NULL,
  PRIMARY KEY (`WORKS_CATEGORY_ID`),
  UNIQUE KEY `WORKS_CATEGORY_UNIQUE` (`WORKS_CATEGORY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `works_category`
--

LOCK TABLES `works_category` WRITE;
/*!40000 ALTER TABLE `works_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `works_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `works_folder`
--

DROP TABLE IF EXISTS `works_folder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `works_folder` (
  `WORKS_FOLDER_ID` int(11) NOT NULL AUTO_INCREMENT,
  `USER_FOLDER_ID` int(11) NOT NULL,
  `WORKS_ID` int(11) NOT NULL,
  PRIMARY KEY (`WORKS_FOLDER_ID`),
  UNIQUE KEY `WORKS_FOLDER_UNIQUE` (`WORKS_FOLDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `works_folder`
--

LOCK TABLES `works_folder` WRITE;
/*!40000 ALTER TABLE `works_folder` DISABLE KEYS */;
/*!40000 ALTER TABLE `works_folder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `works_like`
--

DROP TABLE IF EXISTS `works_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `works_like` (
  `WORKS_LIKE_ID` int(11) NOT NULL AUTO_INCREMENT,
  `WORKS_ID` int(11) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  `LIKE_TYPE` int(11) NOT NULL,
  PRIMARY KEY (`WORKS_LIKE_ID`),
  UNIQUE KEY `WORKS_LIKE_UNIQUE` (`WORKS_LIKE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `works_like`
--

LOCK TABLES `works_like` WRITE;
/*!40000 ALTER TABLE `works_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `works_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `works_msg`
--

DROP TABLE IF EXISTS `works_msg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `works_msg` (
  `WORKS_MSG_ID` int(11) NOT NULL AUTO_INCREMENT,
  `WORKS_ID` int(11) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  `MESSAGE` varchar(1024) DEFAULT NULL,
  `MSG_ORDER` int(11) DEFAULT NULL,
  `CREATION_DATE` datetime NOT NULL,
  PRIMARY KEY (`WORKS_MSG_ID`),
  UNIQUE KEY `WORKS_MSG_UNIQUE` (`WORKS_MSG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `works_msg`
--

LOCK TABLES `works_msg` WRITE;
/*!40000 ALTER TABLE `works_msg` DISABLE KEYS */;
/*!40000 ALTER TABLE `works_msg` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `works_tag`
--

DROP TABLE IF EXISTS `works_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `works_tag` (
  `WORKS_TAG_ID` int(11) NOT NULL AUTO_INCREMENT,
  `WORKS_ID` int(11) NOT NULL,
  `TAG_NAME` varchar(512) NOT NULL,
  `TAG_ORDER` int(11) DEFAULT NULL,
  PRIMARY KEY (`WORKS_TAG_ID`),
  UNIQUE KEY `WORKS_TAG_UNIQUE` (`WORKS_TAG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `works_tag`
--

LOCK TABLES `works_tag` WRITE;
/*!40000 ALTER TABLE `works_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `works_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'bonnie_draw_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-09-20 16:16:09
