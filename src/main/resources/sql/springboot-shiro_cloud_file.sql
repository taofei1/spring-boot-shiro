/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 5.7.17-log : Database - test
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`test` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `test`;

/*Table structure for table `cloud_file` */

DROP TABLE IF EXISTS `cloud_file`;

CREATE TABLE `cloud_file` (
  `file_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `size` bigint(20) DEFAULT NULL,
  `content_type` varchar(50) DEFAULT NULL,
  `model` varchar(50) DEFAULT NULL,
  `suffix` varchar(50) DEFAULT NULL,
  `is_trash` int(1) DEFAULT '0',
  `is_share` int(1) DEFAULT '0',
  `user_id` bigint(20) DEFAULT NULL,
  `file_order` bigint(20) DEFAULT NULL,
  `is_directory` int(1) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

/*Data for the table `cloud_file` */

insert  into `cloud_file`(`file_id`,`file_name`,`file_path`,`size`,`content_type`,`model`,`suffix`,`is_trash`,`is_share`,`user_id`,`file_order`,`is_directory`,`parent_id`,`create_time`,`update_time`) values (1,'首页','',0,'','','',0,0,0,0,1,0,'2019-01-14 14:50:39','2019-01-14 14:50:42'),(3,'新建文件夹',NULL,0,NULL,NULL,NULL,0,0,1,2,1,1,'2019-01-14 14:52:28','2019-01-14 17:07:58'),(4,'1.jpg','/profile/cloudFile/1.jpg',52410,'image/jpg',NULL,'jpg',0,1,1,2,0,1,'2019-01-14 14:56:19','2019-01-18 13:35:22'),(5,'小众.txt','/profile/cloudFile/小众.txt',203,'',NULL,'txt',0,0,1,1,0,3,'2019-01-14 14:57:44','2019-01-18 11:48:32'),(6,'新建文件夹2','',0,'directory',NULL,'',0,0,1,3,1,1,'2019-01-15 13:46:48','2019-01-18 11:51:14'),(11,'新建文件夹8','',0,'directory',NULL,'',0,0,1,8,1,1,'2019-01-15 15:24:37','2019-01-18 11:51:14'),(12,'新建文件夹7','',0,'directory',NULL,'',0,0,1,9,1,1,'2019-01-15 15:56:44','2019-01-18 11:51:01'),(13,'新建文件夹','',0,'directory',NULL,'',2,0,1,1,1,12,'2019-01-15 16:18:08','2019-01-18 11:49:26'),(14,'新建文件夹1','',0,'directory',NULL,'',2,0,1,2,1,12,'2019-01-15 16:18:26','2019-01-18 11:49:26'),(15,'新建文件夹','',0,'directory',NULL,'',2,0,1,1,1,13,'2019-01-15 16:23:25','2019-01-18 11:49:26'),(16,'呵呵','',0,'directory',NULL,'',2,0,1,1,1,10,'2019-01-15 16:24:37','2019-01-17 16:40:51'),(17,'新建文件夹2','',0,'directory',NULL,'',2,0,1,2,1,13,'2019-01-15 17:14:33','2019-01-18 11:49:26'),(18,'新建文件夹1','',0,'directory',NULL,'',0,0,1,10,1,1,'2019-01-15 17:38:15','2019-01-15 17:38:15'),(20,'新建文件夹11','',0,'directory',NULL,'',2,0,1,3,1,12,'2019-01-15 17:39:17','2019-01-18 11:49:26'),(21,'新建文件夹','',0,'directory',NULL,'',2,0,1,1,1,19,'2019-01-15 17:39:43','2019-01-17 17:18:07'),(22,'新建文件夹1','',0,'directory',NULL,'',2,0,1,2,1,19,'2019-01-15 17:39:50','2019-01-17 17:18:07'),(23,'新建文件夹111','',0,'directory',NULL,'',2,0,1,4,1,12,'2019-01-15 17:39:58','2019-01-18 11:49:27'),(24,'新建文件夹1111','',0,'directory',NULL,'',2,0,1,5,1,12,'2019-01-15 17:40:09','2019-01-18 11:49:27'),(25,'新建文件夹11','',0,'directory',NULL,'',2,0,1,3,1,19,'2019-01-15 17:40:14','2019-01-17 17:18:07'),(26,'新建文件夹','',0,'directory',NULL,'',0,0,1,1,1,18,'2019-01-15 17:42:12','2019-01-15 17:42:12'),(27,'新建文件夹1','',0,'directory',NULL,'',0,0,1,2,1,18,'2019-01-15 17:42:14','2019-01-15 17:42:14'),(28,'新建文件夹11','',0,'directory',NULL,'',0,0,1,3,1,18,'2019-01-15 17:42:15','2019-01-15 17:42:15'),(29,'新建文件夹111','',0,'directory',NULL,'',0,0,1,4,1,18,'2019-01-15 17:43:25','2019-01-15 17:43:25'),(31,'新建文件夹2','',0,'directory',NULL,'',2,0,1,4,1,19,'2019-01-15 17:49:09','2019-01-17 17:18:07'),(32,'新建文件夹3','',0,'directory',NULL,'',2,0,1,5,1,19,'2019-01-15 17:49:14','2019-01-17 17:18:07'),(33,'新建文件夹4','',0,'directory',NULL,'',2,0,1,6,1,19,'2019-01-15 17:49:16','2019-01-17 17:18:07'),(34,'新建文件夹5','',0,'directory',NULL,'',2,0,1,7,1,19,'2019-01-15 17:49:18','2019-01-17 17:18:07'),(36,'新建文件夹(1)','',0,'directory',NULL,'',2,0,1,2,1,11,'2019-01-15 17:50:21','2019-01-18 11:51:09'),(37,'新建文件夹(2)','',0,'directory',NULL,'',2,0,1,3,1,11,'2019-01-15 17:50:24','2019-01-18 11:51:09');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
