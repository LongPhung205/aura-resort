-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: hotel_booking_db
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attendance_logs`
--

DROP TABLE IF EXISTS `attendance_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `check_time` datetime(6) NOT NULL,
  `check_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `latitude` double DEFAULT NULL,
  `location_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `match_accuracy` double DEFAULT NULL,
  `method` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `photo_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `staff_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdbs7cdoqhs91576bjxj9c57x5` (`staff_id`),
  CONSTRAINT `FKdbs7cdoqhs91576bjxj9c57x5` FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_logs`
--

LOCK TABLES `attendance_logs` WRITE;
/*!40000 ALTER TABLE `attendance_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendance_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_details`
--

DROP TABLE IF EXISTS `booking_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `price_per_night` decimal(12,2) NOT NULL,
  `booking_id` bigint NOT NULL,
  `room_id` bigint DEFAULT NULL,
  `villa_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkbcan6ybv86uappnh0qtdmvas` (`booking_id`),
  KEY `FK7ap4htogm0353mc6hgdlcvvou` (`villa_id`),
  KEY `FK8pcpg1hs4kqwqcvq79i1esg5` (`room_id`),
  CONSTRAINT `FK7ap4htogm0353mc6hgdlcvvou` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`),
  CONSTRAINT `FK8pcpg1hs4kqwqcvq79i1esg5` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FKkbcan6ybv86uappnh0qtdmvas` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_details`
--

LOCK TABLES `booking_details` WRITE;
/*!40000 ALTER TABLE `booking_details` DISABLE KEYS */;
INSERT INTO `booking_details` VALUES (1,'2026-09-30 12:11:43.863829','2026-09-30 12:11:43.863829',0.00,15,NULL,29,NULL),(2,'2026-09-30 12:12:26.180143','2026-09-30 12:12:26.180703',0.00,16,NULL,38,NULL),(3,'2026-09-30 12:13:07.438344','2026-09-30 12:13:07.438344',5000000.00,17,NULL,38,NULL),(4,'2026-09-30 12:13:42.429696','2026-09-30 12:13:42.429696',12000000.00,18,NULL,29,NULL),(5,'2026-09-30 12:22:28.879442','2026-09-30 12:22:28.879442',5000000.00,19,NULL,38,NULL),(6,NULL,'2026-10-03 10:31:36.841753',360437.00,24,NULL,29,'2026-10-03 10:31:36.841753'),(7,NULL,'2026-10-03 10:46:29.118185',360437.00,25,NULL,29,'2026-10-03 10:46:29.118185'),(8,NULL,'2026-10-03 17:57:23.000000',4000000.00,26,NULL,430,'2026-10-03 17:57:23.000000'),(9,NULL,'2026-10-03 17:57:23.000000',5000000.00,27,NULL,431,'2026-10-03 17:57:23.000000'),(10,NULL,'2026-10-03 17:57:23.000000',6000000.00,28,NULL,460,'2026-10-03 17:57:23.000000'),(11,NULL,'2026-10-04 10:22:28.000000',12000000.00,29,NULL,432,'2026-10-04 10:22:28.000000'),(12,NULL,'2026-10-04 10:22:28.000000',12000000.00,30,NULL,433,'2026-10-04 10:22:28.000000'),(13,NULL,'2026-10-04 10:22:28.000000',8500000.00,31,NULL,462,'2026-10-04 10:22:28.000000'),(14,NULL,'2026-10-04 10:22:28.000000',25000000.00,32,NULL,464,'2026-10-04 10:22:28.000000'),(15,NULL,'2026-10-04 10:22:28.000000',5500000.00,33,NULL,491,'2026-10-04 10:22:28.000000'),(16,NULL,'2026-10-04 10:22:28.000000',5500000.00,34,NULL,493,'2026-10-04 10:22:28.000000'),(17,NULL,'2026-10-04 10:59:18.845836',12000000.00,35,NULL,38,'2026-10-04 10:59:18.845836'),(18,NULL,'2026-10-04 11:06:15.299702',12000000.00,36,NULL,38,'2026-10-04 11:06:15.299702'),(19,NULL,'2026-10-04 11:14:10.193678',12000000.00,37,NULL,38,'2026-10-04 11:14:10.193678'),(20,NULL,'2026-10-04 11:24:21.986994',12000000.00,38,NULL,38,'2026-10-04 11:24:21.986994'),(21,NULL,'2026-10-04 11:32:22.004175',12000000.00,39,NULL,431,'2026-10-04 11:32:22.004175'),(22,NULL,'2026-10-05 07:49:09.483223',3288546.00,40,NULL,464,'2026-10-05 07:49:09.483223'),(23,NULL,'2026-10-05 16:30:21.355183',547479.00,41,NULL,432,'2026-10-05 16:30:21.355183');
/*!40000 ALTER TABLE `booking_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `booking_extra_services`
--

DROP TABLE IF EXISTS `booking_extra_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_extra_services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `price_at_booking` decimal(12,2) NOT NULL,
  `quantity` int NOT NULL,
  `booking_id` bigint NOT NULL,
  `extra_service_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK521km2ifkx6xwmb9sfwd94v59` (`booking_id`),
  KEY `FK7l9ch9j6fi3376ruq82f3y2sj` (`extra_service_id`),
  CONSTRAINT `FK521km2ifkx6xwmb9sfwd94v59` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FK7l9ch9j6fi3376ruq82f3y2sj` FOREIGN KEY (`extra_service_id`) REFERENCES `extra_services` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_extra_services`
--

LOCK TABLES `booking_extra_services` WRITE;
/*!40000 ALTER TABLE `booking_extra_services` DISABLE KEYS */;
/*!40000 ALTER TABLE `booking_extra_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `booking_code` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `check_in_date` date NOT NULL,
  `check_out_date` date NOT NULL,
  `expire_at` datetime(6) DEFAULT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  `status` enum('CANCELLED','CHECKED_IN','CHECKED_OUT','CONFIRMED','PENDING') COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `promotion_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `check_in_time` datetime(6) DEFAULT NULL,
  `check_out_time` datetime(6) DEFAULT NULL,
  `deposit_amount` decimal(12,2) DEFAULT NULL,
  `folio_balance` decimal(12,2) DEFAULT NULL,
  `guest_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guest_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guest_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_card_number` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKq97166k18hklq6ls46osbrftx` (`booking_code`),
  KEY `FKk4byobgkjv3y3952wwpxyep7o` (`promotion_id`),
  KEY `FKeyog2oic85xg7hsu2je2lx3s6` (`user_id`),
  CONSTRAINT `FKeyog2oic85xg7hsu2je2lx3s6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKk4byobgkjv3y3952wwpxyep7o` FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (15,'2026-09-30 12:11:43.802651','2026-09-30 12:27:00.018935','BK-20260930-747B38','2026-10-01','2026-10-02','2026-09-30 12:26:43.793849',NULL,'CANCELLED',0.00,NULL,1,NULL,NULL,0.00,0.00,NULL,NULL,NULL,NULL,NULL),(16,'2026-09-30 12:12:26.177627','2026-09-30 12:28:00.013643','BK-20260930-3EFAC0','2026-10-15','2026-10-17','2026-09-30 12:27:26.177627',NULL,'CANCELLED',0.00,NULL,26,NULL,NULL,0.00,0.00,NULL,NULL,NULL,NULL,NULL),(17,'2026-09-30 12:13:07.435134','2026-09-30 12:29:00.024598','BK-20260930-DFE34B','2026-10-20','2026-10-22','2026-09-30 12:28:07.434578',NULL,'CANCELLED',10000000.00,NULL,26,NULL,NULL,0.00,0.00,NULL,NULL,NULL,NULL,NULL),(18,'2026-09-30 12:13:42.425088','2026-09-30 12:29:00.024598','BK-20260930-2B01CA','2026-10-03','2026-10-08','2026-09-30 12:28:42.425088',NULL,'CANCELLED',60000000.00,NULL,1,NULL,NULL,0.00,0.00,NULL,NULL,NULL,NULL,NULL),(19,'2026-09-30 12:22:28.866105','2026-09-30 12:38:00.023046','BK-20260930-7D2223','2026-10-01','2026-10-02','2026-09-30 12:37:28.865419',NULL,'CANCELLED',5000000.00,NULL,1,NULL,NULL,0.00,0.00,NULL,NULL,NULL,NULL,NULL),(24,NULL,'2026-10-03 10:31:36.860892','BK-20261003-9B5D43','2026-10-04','2026-10-05','2026-10-03 10:46:36.725419',NULL,'CANCELLED',360437.00,NULL,44,NULL,NULL,0.00,0.00,NULL,NULL,NULL,NULL,'2026-10-03 10:31:36.732325'),(25,NULL,'2026-10-03 11:03:00.043812','BK-20261003-3DF0FB','2026-10-04','2026-10-05','2026-10-03 11:01:29.039665',NULL,'CANCELLED',360437.00,NULL,44,NULL,NULL,0.00,0.00,NULL,NULL,NULL,NULL,'2026-10-03 10:46:29.050206'),(26,NULL,'2026-10-03 11:24:45.854561','BK-20261002-HUY01','2026-10-02','2026-10-05',NULL,'Khách yêu cầu hoa tươi và rượu vang chúc mừng kỷ niệm','CHECKED_OUT',12000000.00,NULL,31,'2026-10-02 07:00:00.000000','2026-10-03 11:24:45.827785',NULL,NULL,'giahuy.tran@auraholdings.vn','Trần Gia Huy','0918223999',NULL,'2026-10-03 17:57:23.000000'),(27,NULL,'2026-10-03 11:27:11.866924','BK-20261003-MAI02','2026-10-03','2026-10-06',NULL,'Đến resort tầm 15:00 chiều','CHECKED_OUT',15000000.00,NULL,39,'2026-10-03 18:25:22.000000','2026-10-03 11:27:11.848492',NULL,NULL,'mai.le@sanctuary.vn','Lê Thị Mai','0903112233',NULL,'2026-10-03 17:57:23.000000'),(28,NULL,'2026-10-04 11:11:29.816539','BK-20261004-HUY03','2026-10-04','2026-10-07',NULL,'Khách đặt xe đón tại sân bay','CHECKED_OUT',18000000.00,NULL,44,'2026-10-03 11:09:45.815937','2026-10-04 11:11:29.810335',NULL,NULL,'dcg14067@laoia.com','Đặng Quốc Huy','0988776655',NULL,'2026-10-03 17:57:23.000000'),(29,NULL,'2026-10-04 11:11:25.382239','BK-20261003-NAM103','2026-10-03','2026-10-06',NULL,'Khách VIP cần chuẩn bị xe điện đưa đón','CHECKED_OUT',36000000.00,NULL,1,'2026-10-03 07:00:00.000000','2026-10-04 11:11:25.305955',NULL,NULL,'nam.nguyen@auraholdings.vn','Nguyễn Hoàng Nam','0908123456',NULL,'2026-10-04 10:22:28.000000'),(30,NULL,'2026-10-04 10:22:28.000000','BK-20261006-HUY104','2026-10-06','2026-10-09',NULL,'Yêu cầu set-up hoa tươi và rượu vang kỷ niệm','CONFIRMED',36000000.00,NULL,1,NULL,NULL,NULL,NULL,'giahuy.tran@auraholdings.vn','Trần Gia Huy','0918223999',NULL,'2026-10-04 10:22:28.000000'),(31,NULL,'2026-10-05 16:42:15.241460','BK-20261005-ANH203','2026-10-05','2026-10-08',NULL,'Đặt tiệc nướng BBQ hoàng hôn tại ban công villa','CHECKED_OUT',25500000.00,NULL,1,'2026-10-05 16:27:27.124033','2026-10-05 16:42:15.210542',NULL,NULL,'tuananh.pham@auraholdings.vn','Phạm Tuấn Anh','0977889900',NULL,'2026-10-04 10:22:28.000000'),(32,NULL,'2026-10-04 10:22:28.000000','BK-20261005-THAO-VIP','2026-10-05','2026-10-08',NULL,'Gia đình 6 người, yêu cầu butler riêng và trà chiều','CONFIRMED',75000000.00,NULL,1,NULL,NULL,NULL,NULL,'thao.bui@auraholdings.vn','Bùi Phương Thảo','0933221100',NULL,'2026-10-04 10:22:28.000000'),(33,NULL,'2026-10-04 11:11:28.161978','BK-20261003-HUONG302','2026-10-03','2026-10-06',NULL,'Check-in sớm, yêu cầu gối lông vũ','CHECKED_OUT',16500000.00,NULL,1,'2026-10-03 07:00:00.000000','2026-10-04 11:11:28.148109',NULL,NULL,'huong.le@auraholdings.vn','Lê Thị Mai Hương','0988665544',NULL,'2026-10-04 10:22:28.000000'),(34,NULL,'2026-10-04 10:22:28.000000','BK-20261007-TRI304','2026-10-07','2026-10-10',NULL,'Khách công tác, cần xuất hóa đơn VAT điện tử','CONFIRMED',16500000.00,NULL,1,NULL,NULL,NULL,NULL,'tri.vo@auraholdings.vn','Võ Minh Trí','0912345678',NULL,'2026-10-04 10:22:28.000000'),(35,NULL,'2026-10-04 11:15:00.011148','BK-20261004-1C712C','2026-10-05','2026-10-06','2026-10-04 11:14:18.754418','','CANCELLED',12000000.00,NULL,1,NULL,NULL,0.00,0.00,NULL,NULL,NULL,NULL,'2026-10-04 10:59:18.762871'),(36,NULL,'2026-10-04 11:24:00.069952','BK-20261004-CB2F50','2026-10-05','2026-10-06','2026-10-04 11:21:15.293174','','CANCELLED',12000000.00,NULL,1,NULL,NULL,0.00,0.00,NULL,NULL,NULL,NULL,'2026-10-04 11:06:15.295174'),(37,NULL,'2026-10-04 11:30:00.015040','BK-20261004-ACBB2F','2026-10-05','2026-10-06','2026-10-04 11:29:10.189716','','CANCELLED',12000000.00,NULL,1,NULL,NULL,0.00,0.00,NULL,NULL,NULL,NULL,'2026-10-04 11:14:10.189716'),(38,NULL,'2026-10-05 16:33:50.846759','BK-20261004-5557CE','2026-10-05','2026-10-06',NULL,'','CHECKED_OUT',12000000.00,NULL,1,'2026-10-04 11:31:54.360855','2026-10-05 16:33:50.837796',0.00,0.00,'phungvanlong10925@gmail.com','http://localhost:4200/','0378203598',NULL,'2026-10-04 11:24:21.949379'),(39,NULL,'2026-10-04 11:32:22.010806','BK-20261004-6D12E7','2026-10-05','2026-10-06',NULL,'','CONFIRMED',12000000.00,NULL,44,NULL,NULL,0.00,0.00,'huylan205@gmail.com','BK-20261004-5557CE','0378203598',NULL,'2026-10-04 11:32:22.000061'),(40,NULL,'2026-10-05 16:33:48.807612','BK-20261005-B19841','2026-10-09','2026-10-10',NULL,'','CHECKED_OUT',3288546.00,NULL,1,'2026-10-05 13:58:26.740935','2026-10-05 16:33:48.782741',0.00,0.00,'phungvanlong10925@gmail.com','ads','0378203598',NULL,'2026-10-05 07:49:09.404734'),(41,NULL,'2026-10-05 16:30:21.368532','BK-20261005-CB6E5D','2026-10-06','2026-10-07',NULL,'','CONFIRMED',547479.00,NULL,45,NULL,NULL,0.00,0.00,'2311061241@hunre.edu.vn','hao nam','0378203598',NULL,'2026-10-05 16:30:21.349545');
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collections`
--

DROP TABLE IF EXISTS `collections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `collections` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `display_order` int DEFAULT NULL,
  `image_url` longtext COLLATE utf8mb4_unicode_ci,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `slug` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKddet6fvs2nj65hg90ew34sgjy` (`name`),
  UNIQUE KEY `UKf5d7hxjge10f628bpt8444641` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collections`
--

LOCK TABLES `collections` WRITE;
/*!40000 ALTER TABLE `collections` DISABLE KEYS */;
/*!40000 ALTER TABLE `collections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `combo_package_services`
--

DROP TABLE IF EXISTS `combo_package_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `combo_package_services` (
  `combo_package_id` bigint NOT NULL,
  `extra_service_id` bigint NOT NULL,
  KEY `FKcc3ko5kjw3c1e1obd7bp5sr59` (`extra_service_id`),
  KEY `FKq50gw15ry3t2ee6b4tsl4h5` (`combo_package_id`),
  CONSTRAINT `FKcc3ko5kjw3c1e1obd7bp5sr59` FOREIGN KEY (`extra_service_id`) REFERENCES `extra_services` (`id`),
  CONSTRAINT `FKq50gw15ry3t2ee6b4tsl4h5` FOREIGN KEY (`combo_package_id`) REFERENCES `combo_packages` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `combo_package_services`
--

LOCK TABLES `combo_package_services` WRITE;
/*!40000 ALTER TABLE `combo_package_services` DISABLE KEYS */;
INSERT INTO `combo_package_services` VALUES (5,1),(5,2),(5,3),(5,4),(5,6),(5,7),(1,1),(1,2),(1,4),(2,1),(2,2),(2,3),(2,4),(2,5);
/*!40000 ALTER TABLE `combo_package_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `combo_packages`
--

DROP TABLE IF EXISTS `combo_packages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `combo_packages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `include_bbq` bit(1) DEFAULT NULL,
  `include_cleaning` bit(1) DEFAULT NULL,
  `include_transport` bit(1) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(15,2) DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `combo_packages`
--

LOCK TABLES `combo_packages` WRITE;
/*!40000 ALTER TABLE `combo_packages` DISABLE KEYS */;
INSERT INTO `combo_packages` VALUES (1,'2026-10-02 13:54:44.887836','2026-10-03 07:43:18.539325','','/assets/images/uploads/villa_8c44ae35.jpg',_binary '',_binary '',_binary '','Gia đình vui vẻ',200000.00,'PUBLISH'),(2,'2026-10-02 13:55:32.034096','2026-10-03 07:44:04.418362','','/assets/images/uploads/villa_24aca42c.jpg',_binary '',_binary '',_binary '','Kỳ Sinh Nhật',500000.00,'PUBLISH'),(5,'2026-10-03 07:42:48.501587','2026-10-03 07:42:48.501587','','/assets/images/uploads/villa_cdbc3cc1.jpg',NULL,NULL,NULL,'Kỳ nghỉ trăng mật',1000000.00,'PUBLISH');
/*!40000 ALTER TABLE `combo_packages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contacts`
--

DROP TABLE IF EXISTS `contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contacts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ticket_code` varchar(20) NOT NULL COMMENT 'Mã phiếu tiếp nhận (VD: REQ-89412)',
  `fullname` varchar(50) NOT NULL COMMENT 'Họ và tên (2 - 50 ký tự)',
  `email` varchar(100) NOT NULL COMMENT 'Địa chỉ email',
  `phone` varchar(10) DEFAULT NULL COMMENT 'Số điện thoại (Tùy chọn, đúng 10 số)',
  `subject` enum('technical','feedback','general') NOT NULL COMMENT 'Mã chủ đề',
  `subject_name` varchar(50) NOT NULL COMMENT 'Tên hiển thị chủ đề',
  `message` text NOT NULL COMMENT 'Nội dung liên hệ (10 - 1000 ký tự)',
  `policy_agreed` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Đồng ý chính sách (1: Có)',
  `status` enum('pending','processing','resolved','closed') NOT NULL DEFAULT 'pending',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ticket_code` (`ticket_code`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contacts`
--

LOCK TABLES `contacts` WRITE;
/*!40000 ALTER TABLE `contacts` DISABLE KEYS */;
/*!40000 ALTER TABLE `contacts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `day_end_closings`
--

DROP TABLE IF EXISTS `day_end_closings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `day_end_closings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `adr` decimal(12,2) DEFAULT NULL,
  `closed_at` datetime(6) DEFAULT NULL,
  `closing_date` date NOT NULL,
  `net_cash` decimal(14,2) NOT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `occupancy_rate` double DEFAULT NULL,
  `occupied_rooms` int DEFAULT NULL,
  `rev_par` decimal(12,2) DEFAULT NULL,
  `room_revenue` decimal(14,2) NOT NULL,
  `service_revenue` decimal(14,2) NOT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_bookings` int DEFAULT NULL,
  `total_opex` decimal(14,2) NOT NULL,
  `total_revenue` decimal(14,2) NOT NULL,
  `closed_by_user_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK68k036fgg3d5fpyhbdeq8hwm6` (`closing_date`),
  KEY `FKs5vuo37cq5pp71aflcskabdme` (`closed_by_user_id`),
  CONSTRAINT `FKs5vuo37cq5pp71aflcskabdme` FOREIGN KEY (`closed_by_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `day_end_closings`
--

LOCK TABLES `day_end_closings` WRITE;
/*!40000 ALTER TABLE `day_end_closings` DISABLE KEYS */;
/*!40000 ALTER TABLE `day_end_closings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `extra_services`
--

DROP TABLE IF EXISTS `extra_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `extra_services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image_url` text COLLATE utf8mb4_unicode_ci,
  `is_active` bit(1) DEFAULT NULL,
  `type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `extra_services`
--

LOCK TABLES `extra_services` WRITE;
/*!40000 ALTER TABLE `extra_services` DISABLE KEYS */;
INSERT INTO `extra_services` VALUES (1,'2026-09-27 14:01:04.659643','2026-09-27 14:01:04.659643','Bữa sáng buffet cao cấp tại villa hoặc nhà hàng','Ăn sáng',80000.00,'restaurant',NULL,_binary '','DINING','người',NULL),(2,'2026-09-27 14:01:04.670721','2026-09-27 14:01:04.670721','Tiệc BBQ ngoài trời bên bờ biển với hải sản tươi sống','BBQ',500000.00,'outdoor_grill',NULL,_binary '','DINING','gói',NULL),(3,'2026-09-27 14:01:04.673721','2026-09-27 14:01:04.673721','Xe máy tay ga Honda Air Blade / Lead giao tận villa','Thuê xe máy',150000.00,'two_wheeler',NULL,_binary '','TRANSPORT','ngày',NULL),(4,'2026-09-27 14:01:04.677756','2026-09-27 14:01:04.677756','Dọn vệ sinh villa theo yêu cầu ngoài lịch trình','Dọn phòng',100000.00,'cleaning_services',NULL,_binary '','CLEANING','lần',NULL),(5,'2026-09-27 14:01:04.680765','2026-09-27 14:01:04.680765','Gói trang trí sinh nhật với bóng bay, bánh kem và hoa tươi','Trang trí sinh nhật',800000.00,'celebration',NULL,_binary '','ENTERTAINMENT','gói',NULL),(6,'2026-09-27 14:01:04.683767','2026-09-27 14:01:04.683767','Xe riêng đón/tiễn sân bay Phú Quốc - Villa','Đưa đón sân bay',300000.00,'airport_shuttle',NULL,_binary '','TRANSPORT','chuyến',NULL),(7,'2026-09-27 14:01:04.686287','2026-09-27 14:01:04.686287','Thuê không gian bếp villa để tự nấu ăn','Thuê bếp',200000.00,'cooking',NULL,_binary '','DINING','ngày',NULL);
/*!40000 ALTER TABLE `extra_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `home_banners`
--

DROP TABLE IF EXISTS `home_banners`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `home_banners` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `badges_json` text COLLATE utf8mb4_unicode_ci,
  `cta_link` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cta_text` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `display_order` int NOT NULL,
  `image_url` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` bit(1) NOT NULL,
  `mobile_image_url` longtext COLLATE utf8mb4_unicode_ci,
  `subtitle` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `placement` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `home_banners`
--

LOCK TABLES `home_banners` WRITE;
/*!40000 ALTER TABLE `home_banners` DISABLE KEYS */;
INSERT INTO `home_banners` VALUES (1,'2026-09-28 09:57:47.428063','2026-09-30 09:52:19.726806',NULL,'/villas','Khám Phá Ngay','Thoải mái bên gia đình',1,'/assets/images/uploads/villa_afbd9cfe.jpg',_binary '','/assets/images/uploads/villa_afbd9cfe.jpg','AURA VILLAS','Nâng tầm kì nghỉ của bạn',NULL,NULL);
/*!40000 ALTER TABLE `home_banners` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `housekeeping_tasks`
--

DROP TABLE IF EXISTS `housekeeping_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `housekeeping_tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `checklist_json` text COLLATE utf8mb4_unicode_ci,
  `completed_at` datetime(6) DEFAULT NULL,
  `evidence_photo_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ozone_ended_at` datetime(6) DEFAULT NULL,
  `ozone_started_at` datetime(6) DEFAULT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supervisor_note` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `task_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `housekeeper_id` bigint DEFAULT NULL,
  `room_id` bigint DEFAULT NULL,
  `supervisor_id` bigint DEFAULT NULL,
  `cleaning_note` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `started_at` datetime(6) DEFAULT NULL,
  `villa_id` bigint DEFAULT NULL,
  `ozone_enabled` bit(1) DEFAULT NULL,
  `priority` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `re_clean_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `booking_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlof7vlyakq85n4ue2ff52ns9c` (`housekeeper_id`),
  KEY `FKgry8goieoewq4cc34lmtm8von` (`supervisor_id`),
  KEY `FKvp30hxy62fh1la1hmjbq89no` (`villa_id`),
  KEY `FK7iwarfg423ucxofsu0qg507pf` (`booking_id`),
  KEY `FKbuj2qtxq2odlqhj9qivxxvawn` (`room_id`),
  CONSTRAINT `FK7iwarfg423ucxofsu0qg507pf` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKbuj2qtxq2odlqhj9qivxxvawn` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FKgry8goieoewq4cc34lmtm8von` FOREIGN KEY (`supervisor_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKlof7vlyakq85n4ue2ff52ns9c` FOREIGN KEY (`housekeeper_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKvp30hxy62fh1la1hmjbq89no` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `housekeeping_tasks`
--

LOCK TABLES `housekeeping_tasks` WRITE;
/*!40000 ALTER TABLE `housekeeping_tasks` DISABLE KEYS */;
INSERT INTO `housekeeping_tasks` VALUES (1,NULL,'2026-10-05 13:45:22.727546','[{\"id\":1,\"title\":\"Kiểm tra toàn phòng: Đồ khách bỏ quên (Lost & Found) & Sự cố hư hỏng\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":2,\"title\":\"Thu gom rác, rửa gạt tàn & phân loại rác tái chế\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":3,\"title\":\"Tháo bỏ toàn bộ ga giường, vỏ gối, vỏ chăn, khăn tắm vào túi đồ dơ\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":4,\"title\":\"Cọ rửa, tẩy trùng bồn cầu, bồn tắm, vách kính phòng tắm\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":5,\"title\":\"Lau khô và đánh bóng vòi sen, gương kính, mặt bàn lavabo\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":6,\"title\":\"Trải ga giường mới, lồng ruột gối ruột chăn sạch, make bed góc vuông chuẩn sao\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":7,\"title\":\"Setup bộ đồ vải mới: 4 Khăn tắm, 4 Khăn mặt, 2 Áo choàng tắm sạch thơm\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":8,\"title\":\"Bổ sung Amenities mới 100%: Bàn chải, xà phòng, dầu gội, sữa tắm, chụp tóc\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":9,\"title\":\"Setup quầy Mini Bar: Nước suối miễn phí, gói trà, cà phê, kiểm kê tủ mát\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":10,\"title\":\"Lau bụi bàn làm việc, kệ tivi, tủ quần áo, kiểm tra két sắt mở sẵn\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":11,\"title\":\"Hút bụi thảm, quét và lau sạch bóng sàn gạch/gỗ\",\"category\":\"FLOOR\",\"completed\":true,\"required\":true},{\"id\":12,\"title\":\"Xịt phòng khử mùi nhẹ nhàng (bật máy Ozone nếu phòng ám khói/mùi thức ăn)\",\"category\":\"DISINFECTION\",\"completed\":true,\"required\":true}]','2026-10-05 13:45:22.640329',NULL,NULL,NULL,'COMPLETED',NULL,'CHECKOUT_DEEP',29,NULL,1,'','2026-10-04 11:56:24.638791',430,_binary '\0','NORMAL',NULL,26,'2026-10-03 18:17:47.000000'),(2,NULL,'2026-10-05 13:45:25.610401',NULL,'2026-10-05 13:45:25.593483',NULL,NULL,NULL,'COMPLETED',NULL,'CHECKOUT_DEEP',29,NULL,1,NULL,'2026-10-04 12:05:38.070621',430,_binary '\0','NORMAL',NULL,26,'2026-10-03 11:24:45.834130'),(3,NULL,'2026-10-05 16:59:40.435735','[{\"id\":1,\"title\":\"Kiểm tra toàn phòng: Đồ khách bỏ quên (Lost & Found) & Sự cố hư hỏng\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":2,\"title\":\"Thu gom rác, rửa gạt tàn & phân loại rác tái chế trong phòng & ban công\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":3,\"title\":\"Tháo bỏ toàn bộ ga giường, vỏ gối, vỏ chăn, khăn tắm vào túi đồ dơ\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":4,\"title\":\"Cọ rửa, tẩy trùng bồn cầu, bồn tắm nằm, vách kính phòng tắm\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":5,\"title\":\"Lau khô và đánh bóng vòi sen, gương soi kính, mặt bàn lavabo đá hoa cương\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":6,\"title\":\"Trải ga giường mới, lồng ruột gối ruột chăn sạch, make bed góc vuông chuẩn sao\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":7,\"title\":\"Setup bộ đồ vải mới: 4 Khăn tắm, 4 Khăn mặt, 2 Áo choàng tắm sạch thơm\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":8,\"title\":\"Bổ sung Amenities mới 100%: Bàn chải, xà phòng, dầu gội, sữa tắm, chụp tóc\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":9,\"title\":\"Setup quầy Mini Bar: Nước suối miễn phí, gói trà, cà phê, kiểm kê tủ mát\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":10,\"title\":\"Lau bụi bàn làm việc, kệ tivi, tủ quần áo, kiểm tra két sắt mở sẵn\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":11,\"title\":\"Hút bụi thảm, quét và lau sạch bóng sàn gạch/gỗ phòng ngủ & phòng khách\",\"category\":\"FLOOR\",\"completed\":true,\"required\":true},{\"id\":12,\"title\":\"Lau kính cửa sổ, cửa trượt ban công, tay nắm cửa và công tắc đèn\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":13,\"title\":\"Kiểm tra hoạt động máy lạnh điều hòa (set 25°C), quạt trần và rèm cửa\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":14,\"title\":\"Kiểm tra khu vực ban công, bàn ghế ngoài trời và khu vực bể bơi riêng\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":15,\"title\":\"Bật máy khử trùng và khử mùi Ozone 20 phút đảm bảo không khí tinh khiết\",\"category\":\"DISINFECTION\",\"completed\":true,\"required\":true},{\"id\":16,\"title\":\"Xịt tinh dầu thơm nhẹ tự nhiên, kiểm tra lần cuối trước khi bàn giao QC\",\"category\":\"DISINFECTION\",\"completed\":true,\"required\":true}]','2026-10-05 16:59:40.391695',NULL,'2026-10-05 17:32:44.814142','2026-10-05 16:47:44.814142','COMPLETED',NULL,'CHECKOUT_DEEP',29,1451,1,'','2026-10-05 16:47:44.814142',431,_binary '\0','NORMAL',NULL,27,'2026-10-03 11:27:11.853172'),(4,NULL,'2026-10-05 16:49:28.232726','[{\"id\":1,\"title\":\"Thu gom rác và gạt tàn trong phòng & ban công\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":2,\"title\":\"Làm lại giường ngủ (make bed) phẳng phiu, thay vỏ gối nếu dơ\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":3,\"title\":\"Thu gom khăn ướt trong nhà tắm và treo khăn sạch thay thế\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":4,\"title\":\"Vệ sinh bồn rửa mặt, lau khô mặt đá lavabo\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":5,\"title\":\"Bổ sung nước suối chai complimentary và trà/cà phê\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":6,\"title\":\"Kiểm tra minibar và ghi nhận đồ uống khách đã dùng\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":7,\"title\":\"Hút bụi sàn và lau sàn phòng ngủ, nhà tắm\",\"category\":\"FLOOR\",\"completed\":true,\"required\":true},{\"id\":8,\"title\":\"Xịt thơm phòng và kiểm tra điều hòa để ở 25°C\",\"category\":\"DISINFECTION\",\"completed\":true,\"required\":false}]','2026-10-05 16:49:28.219588',NULL,'2026-10-05 17:32:04.395832','2026-10-05 16:47:04.395832','COMPLETED',NULL,'DAILY',29,NULL,1,'','2026-10-05 16:47:04.395832',460,_binary '\0','NORMAL',NULL,NULL,'2026-10-03 23:00:00.232467'),(6,NULL,'2026-10-05 16:59:43.625009','[{\"id\":1,\"title\":\"Kiểm tra toàn phòng: Đồ khách bỏ quên (Lost & Found) & Sự cố hư hỏng\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":2,\"title\":\"Thu gom rác, rửa gạt tàn & phân loại rác tái chế trong phòng & ban công\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":3,\"title\":\"Tháo bỏ toàn bộ ga giường, vỏ gối, vỏ chăn, khăn tắm vào túi đồ dơ\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":4,\"title\":\"Cọ rửa, tẩy trùng bồn cầu, bồn tắm nằm, vách kính phòng tắm\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":5,\"title\":\"Lau khô và đánh bóng vòi sen, gương soi kính, mặt bàn lavabo đá hoa cương\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":6,\"title\":\"Trải ga giường mới, lồng ruột gối ruột chăn sạch, make bed góc vuông chuẩn sao\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":7,\"title\":\"Setup bộ đồ vải mới: 4 Khăn tắm, 4 Khăn mặt, 2 Áo choàng tắm sạch thơm\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":8,\"title\":\"Bổ sung Amenities mới 100%: Bàn chải, xà phòng, dầu gội, sữa tắm, chụp tóc\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":9,\"title\":\"Setup quầy Mini Bar: Nước suối miễn phí, gói trà, cà phê, kiểm kê tủ mát\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":10,\"title\":\"Lau bụi bàn làm việc, kệ tivi, tủ quần áo, kiểm tra két sắt mở sẵn\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":11,\"title\":\"Hút bụi thảm, quét và lau sạch bóng sàn gạch/gỗ phòng ngủ & phòng khách\",\"category\":\"FLOOR\",\"completed\":true,\"required\":true},{\"id\":12,\"title\":\"Lau kính cửa sổ, cửa trượt ban công, tay nắm cửa và công tắc đèn\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":13,\"title\":\"Kiểm tra hoạt động máy lạnh điều hòa (set 25°C), quạt trần và rèm cửa\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":14,\"title\":\"Kiểm tra khu vực ban công, bàn ghế ngoài trời và khu vực bể bơi riêng\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":15,\"title\":\"Bật máy khử trùng và khử mùi Ozone 20 phút đảm bảo không khí tinh khiết\",\"category\":\"DISINFECTION\",\"completed\":true,\"required\":true},{\"id\":16,\"title\":\"Xịt tinh dầu thơm nhẹ tự nhiên, kiểm tra lần cuối trước khi bàn giao QC\",\"category\":\"DISINFECTION\",\"completed\":true,\"required\":true}]','2026-10-05 16:59:43.608351',NULL,'2026-10-05 17:37:58.499581','2026-10-05 16:52:58.499581','COMPLETED',NULL,'CHECKOUT_DEEP',29,1802,1,'','2026-10-05 16:52:58.499581',432,_binary '\0','NORMAL',NULL,29,'2026-10-04 11:11:25.319249'),(7,NULL,'2026-10-05 16:59:42.563149','[{\"id\":1,\"title\":\"Kiểm tra toàn phòng: Đồ khách bỏ quên (Lost & Found) & Sự cố hư hỏng\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":2,\"title\":\"Thu gom rác, rửa gạt tàn & phân loại rác tái chế trong phòng & ban công\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":3,\"title\":\"Tháo bỏ toàn bộ ga giường, vỏ gối, vỏ chăn, khăn tắm vào túi đồ dơ\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":4,\"title\":\"Cọ rửa, tẩy trùng bồn cầu, bồn tắm nằm, vách kính phòng tắm\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":5,\"title\":\"Lau khô và đánh bóng vòi sen, gương soi kính, mặt bàn lavabo đá hoa cương\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":6,\"title\":\"Trải ga giường mới, lồng ruột gối ruột chăn sạch, make bed góc vuông chuẩn sao\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":7,\"title\":\"Setup bộ đồ vải mới: 4 Khăn tắm, 4 Khăn mặt, 2 Áo choàng tắm sạch thơm\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":8,\"title\":\"Bổ sung Amenities mới 100%: Bàn chải, xà phòng, dầu gội, sữa tắm, chụp tóc\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":9,\"title\":\"Setup quầy Mini Bar: Nước suối miễn phí, gói trà, cà phê, kiểm kê tủ mát\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":10,\"title\":\"Lau bụi bàn làm việc, kệ tivi, tủ quần áo, kiểm tra két sắt mở sẵn\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":11,\"title\":\"Hút bụi thảm, quét và lau sạch bóng sàn gạch/gỗ phòng ngủ & phòng khách\",\"category\":\"FLOOR\",\"completed\":true,\"required\":true},{\"id\":12,\"title\":\"Lau kính cửa sổ, cửa trượt ban công, tay nắm cửa và công tắc đèn\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":13,\"title\":\"Kiểm tra hoạt động máy lạnh điều hòa (set 25°C), quạt trần và rèm cửa\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":14,\"title\":\"Kiểm tra khu vực ban công, bàn ghế ngoài trời và khu vực bể bơi riêng\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":15,\"title\":\"Bật máy khử trùng và khử mùi Ozone 20 phút đảm bảo không khí tinh khiết\",\"category\":\"DISINFECTION\",\"completed\":true,\"required\":true},{\"id\":16,\"title\":\"Xịt tinh dầu thơm nhẹ tự nhiên, kiểm tra lần cuối trước khi bàn giao QC\",\"category\":\"DISINFECTION\",\"completed\":true,\"required\":true}]','2026-10-05 16:59:42.545636',NULL,'2026-10-05 17:37:16.957484','2026-10-05 16:52:16.957484','COMPLETED',NULL,'CHECKOUT_DEEP',29,1813,1,'','2026-10-05 16:52:16.957484',491,_binary '\0','NORMAL',NULL,33,'2026-10-04 11:11:28.151464'),(8,NULL,'2026-10-05 16:49:30.963905','[{\"id\":1,\"title\":\"Kiểm tra toàn phòng: Đồ khách bỏ quên (Lost & Found) & Sự cố hư hỏng\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":2,\"title\":\"Thu gom rác, rửa gạt tàn & phân loại rác tái chế trong phòng & ban công\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":3,\"title\":\"Tháo bỏ toàn bộ ga giường, vỏ gối, vỏ chăn, khăn tắm vào túi đồ dơ\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":4,\"title\":\"Cọ rửa, tẩy trùng bồn cầu, bồn tắm nằm, vách kính phòng tắm\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":5,\"title\":\"Lau khô và đánh bóng vòi sen, gương soi kính, mặt bàn lavabo đá hoa cương\",\"category\":\"BATHROOM\",\"completed\":true,\"required\":true},{\"id\":6,\"title\":\"Trải ga giường mới, lồng ruột gối ruột chăn sạch, make bed góc vuông chuẩn sao\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":7,\"title\":\"Setup bộ đồ vải mới: 4 Khăn tắm, 4 Khăn mặt, 2 Áo choàng tắm sạch thơm\",\"category\":\"BEDDING\",\"completed\":true,\"required\":true},{\"id\":8,\"title\":\"Bổ sung Amenities mới 100%: Bàn chải, xà phòng, dầu gội, sữa tắm, chụp tóc\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":9,\"title\":\"Setup quầy Mini Bar: Nước suối miễn phí, gói trà, cà phê, kiểm kê tủ mát\",\"category\":\"AMENITIES\",\"completed\":true,\"required\":true},{\"id\":10,\"title\":\"Lau bụi bàn làm việc, kệ tivi, tủ quần áo, kiểm tra két sắt mở sẵn\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":11,\"title\":\"Hút bụi thảm, quét và lau sạch bóng sàn gạch/gỗ phòng ngủ & phòng khách\",\"category\":\"FLOOR\",\"completed\":true,\"required\":true},{\"id\":12,\"title\":\"Lau kính cửa sổ, cửa trượt ban công, tay nắm cửa và công tắc đèn\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":13,\"title\":\"Kiểm tra hoạt động máy lạnh điều hòa (set 25°C), quạt trần và rèm cửa\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":14,\"title\":\"Kiểm tra khu vực ban công, bàn ghế ngoài trời và khu vực bể bơi riêng\",\"category\":\"CLEANING\",\"completed\":true,\"required\":true},{\"id\":15,\"title\":\"Bật máy khử trùng và khử mùi Ozone 20 phút đảm bảo không khí tinh khiết\",\"category\":\"DISINFECTION\",\"completed\":true,\"required\":true},{\"id\":16,\"title\":\"Xịt tinh dầu thơm nhẹ tự nhiên, kiểm tra lần cuối trước khi bàn giao QC\",\"category\":\"DISINFECTION\",\"completed\":true,\"required\":true}]','2026-10-05 16:49:30.919734',NULL,'2026-10-05 17:31:34.072644','2026-10-05 16:46:34.072644','COMPLETED',NULL,'CHECKOUT_DEEP',29,1824,1,'','2026-10-05 16:46:34.072644',460,_binary '\0','NORMAL',NULL,28,'2026-10-04 11:11:29.811292'),(9,NULL,'2026-10-05 16:33:48.789783',NULL,NULL,NULL,NULL,NULL,'PENDING',NULL,'CHECKOUT_DEEP',NULL,1548,NULL,NULL,NULL,464,_binary '\0','NORMAL',NULL,40,'2026-10-05 16:33:48.789783'),(10,NULL,'2026-10-05 16:33:50.839857',NULL,NULL,NULL,NULL,NULL,'PENDING',NULL,'CHECKOUT_DEEP',NULL,110,NULL,NULL,NULL,38,_binary '\0','NORMAL',NULL,38,'2026-10-05 16:33:50.839857'),(11,NULL,'2026-10-05 16:42:15.216061',NULL,NULL,NULL,NULL,NULL,'PENDING',NULL,'CHECKOUT_DEEP',NULL,1810,NULL,NULL,NULL,462,_binary '\0','NORMAL',NULL,31,'2026-10-05 16:42:15.216061');
/*!40000 ALTER TABLE `housekeeping_tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_items`
--

DROP TABLE IF EXISTS `inventory_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `category` enum('AMENITY','CLEANING','EQUIPMENT','LINEN','MINIBAR','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `in_stock` int NOT NULL,
  `location` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `min_threshold` int NOT NULL,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit_price` decimal(12,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2qgk5sc56ih2ab04mvjoj1e01` (`code`),
  KEY `idx_inventory_code` (`code`),
  KEY `idx_inventory_category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_items`
--

LOCK TABLES `inventory_items` WRITE;
/*!40000 ALTER TABLE `inventory_items` DISABLE KEYS */;
INSERT INTO `inventory_items` VALUES (1,'2026-09-27 16:47:46.244968','2026-10-05 12:16:30.784366','AMENITY','AMN-TOOTH-01','Bộ đồ dùng cá nhân thân thiện môi trường tiêu chuẩn 5 sao',477,'Kho Tổng A1',100,'Bàn chải & Kem đánh răng sinh học','EcoSupply Vietnam','Bộ',12000.00,NULL),(2,'2026-09-27 16:47:46.259553','2026-09-27 17:08:44.016756','AMENITY','AMN-SHAMP-02','Sữa tắm hữu cơ tinh dầu tràm & oải hương',319,'Kho Tổng A1',150,'Dầu gội & Sữa tắm hữu cơ Aura (50ml)','Natural Care Ltd','Chai',35000.00,NULL),(3,'2026-09-27 16:47:46.271802','2026-10-04 03:37:55.001899','LINEN','LIN-TOWL-03','Khăn bông tắm cao cấp thấm hút tốt dập nổi logo Aura',84,'Kho Vải Linen B2',50,'Khăn tắm cao cấp 100% Cotton Ai Cập (70x140cm)','Dệt May Phong Phú','Chiếc',185000.00,NULL),(4,'2026-09-27 16:47:46.279028','2026-09-27 16:47:46.279028','MINIBAR','MINI-WINE-04','Vang đỏ cao cấp dành riêng cho tủ minibar villa VIP',24,'Kho Bar & F&B C1',15,'Vang Đỏ Chateau Dalat Reserve 750ml','Dalat Wine Corp','Chai',420000.00,NULL),(5,'2026-09-27 16:47:46.288706','2026-09-27 16:47:46.288706','CLEANING','CLN-OZONE-05','Dung dịch diệt khuẩn chuyên dùng cho chu trình làm sạch Ozon',12,'Kho Hóa Chất Buồng Phòng',8,'Dung dịch khử khuẩn chuyên dụng máy Ozon','Hóa Chất Khử Khuẩn 3M','Can 5L',650000.00,NULL);
/*!40000 ALTER TABLE `inventory_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_transactions`
--

DROP TABLE IF EXISTS `inventory_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `performer` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantity` int NOT NULL,
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_amount` decimal(14,2) DEFAULT NULL,
  `type` enum('ADJUSTMENT','EXPORT','IMPORT') COLLATE utf8mb4_unicode_ci NOT NULL,
  `unit_price` decimal(12,2) DEFAULT NULL,
  `destination_villa_id` bigint DEFAULT NULL,
  `item_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_inv_trans_type` (`type`),
  KEY `idx_inv_trans_item` (`item_id`),
  KEY `FK5j4t5eff0bclysk6gdsc9kj1o` (`destination_villa_id`),
  CONSTRAINT `FK5j4t5eff0bclysk6gdsc9kj1o` FOREIGN KEY (`destination_villa_id`) REFERENCES `villas` (`id`),
  CONSTRAINT `FKl6jyry359ycfs63gsme5lwh9q` FOREIGN KEY (`item_id`) REFERENCES `inventory_items` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_transactions`
--

LOCK TABLES `inventory_transactions` WRITE;
/*!40000 ALTER TABLE `inventory_transactions` DISABLE KEYS */;
INSERT INTO `inventory_transactions` VALUES (1,'2026-09-27 16:47:46.300233','2026-09-27 16:47:46.300233','Trần Văn Kho',300,'Nhập định kỳ đầu tuần từ nhà cung cấp',3600000.00,'IMPORT',12000.00,NULL,1,NULL),(2,'2026-09-27 16:47:46.310234','2026-09-27 16:47:46.310234','Nguyễn Thị Hoa',20,'Cấp phát cho Housekeeping thay mới buồng phòng',3700000.00,'EXPORT',185000.00,NULL,3,NULL),(7,'2026-09-27 17:08:43.977578','2026-09-27 17:08:43.978150','Trần Văn Kho (Thủ kho)',2,'Cấp phát Refill theo nhiệm vụ RF-20260928-0001 cho 01',24000.00,'EXPORT',12000.00,29,1,NULL),(8,'2026-09-27 17:08:44.013813','2026-09-27 17:08:44.013813','Trần Văn Kho (Thủ kho)',1,'Cấp phát Refill theo nhiệm vụ RF-20260928-0001 cho 01',35000.00,'EXPORT',35000.00,29,2,NULL),(9,'2026-09-27 17:08:44.034130','2026-09-27 17:08:44.034130','Trần Văn Kho (Thủ kho)',1,'Cấp phát Refill theo nhiệm vụ RF-20260928-0001 cho 01',185000.00,'EXPORT',185000.00,29,3,NULL),(20,NULL,'2026-10-05 12:16:30.748211','huylan205@gmail.com',2,'Cấp phát Refill theo nhiệm vụ RF-20261004-8899 cho NT-101',24000.00,'EXPORT',12000.00,430,1,'2026-10-05 12:16:30.748211');
/*!40000 ALTER TABLE `inventory_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lost_and_found_items`
--

DROP TABLE IF EXISTS `lost_and_found_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lost_and_found_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `finder_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `found_location` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `guest_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guest_phone` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `item_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `item_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  `photo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `returned_at` datetime(6) DEFAULT NULL,
  `status` enum('DISPOSED','GUEST_NOTIFIED','RETURNED','STORED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `storage_location` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `booking_id` bigint DEFAULT NULL,
  `room_id` bigint DEFAULT NULL,
  `villa_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKf2a2spqh0ejk3aiojcnct44y7` (`item_code`),
  KEY `idx_lf_code` (`item_code`),
  KEY `idx_lf_status` (`status`),
  KEY `idx_lf_villa` (`villa_id`),
  KEY `FK9yx6gn4kht7ew098dtb44eliq` (`booking_id`),
  KEY `FK2ibcggptrsa188gxrko6dmsj1` (`room_id`),
  CONSTRAINT `FK2ibcggptrsa188gxrko6dmsj1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK9yx6gn4kht7ew098dtb44eliq` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKd0n9rlrqtxmo7oh76rf000gge` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lost_and_found_items`
--

LOCK TABLES `lost_and_found_items` WRITE;
/*!40000 ALTER TABLE `lost_and_found_items` DISABLE KEYS */;
INSERT INTO `lost_and_found_items` VALUES (1,'2026-09-30 02:41:56.980166','2026-09-30 02:41:56.980166','Ví tiền / Giấy tờ','Nguyễn Thị Hoa','Dưới gầm giường ngủ Master','Ông Trần Gia Huy','(+84) 918 223 999','LF-2026-001','Ví da Montblanc màu đen','Bên trong có CCCD và thẻ ngân hàng.',NULL,NULL,'STORED','Két sắt an ninh Lễ tân Tầng 1',NULL,111,430,NULL);
/*!40000 ALTER TABLE `lost_and_found_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenance_tickets`
--

DROP TABLE IF EXISTS `maintenance_tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `maintenance_tickets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `photo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `priority` enum('EMERGENCY','HIGH','LOW','MEDIUM') COLLATE utf8mb4_unicode_ci NOT NULL,
  `reported_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `resolved_at` datetime(6) DEFAULT NULL,
  `status` enum('CANCELLED','IN_PROGRESS','REPORTED','RESOLVED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `technician_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `technician_note` text COLLATE utf8mb4_unicode_ci,
  `ticket_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `room_id` bigint DEFAULT NULL,
  `villa_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4mnok13q824nsh6uy9dgt8035` (`ticket_code`),
  KEY `idx_mt_code` (`ticket_code`),
  KEY `idx_mt_status` (`status`),
  KEY `idx_mt_villa` (`villa_id`),
  KEY `FKsnppu7s42iodul8yy8cns0wrv` (`room_id`),
  CONSTRAINT `FKk27adonn7w7149iqsj53s0yjh` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`),
  CONSTRAINT `FKsnppu7s42iodul8yy8cns0wrv` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenance_tickets`
--

LOCK TABLES `maintenance_tickets` WRITE;
/*!40000 ALTER TABLE `maintenance_tickets` DISABLE KEYS */;
INSERT INTO `maintenance_tickets` VALUES (1,'2026-09-30 02:41:56.993174','2026-09-30 02:41:56.993174','Điều hòa & Điện lạnh','Điều hòa phòng khách Daikin Inverter kêu rè và không phả hơi lạnh.',NULL,'HIGH','Nguyễn Thị Hoa',NULL,'REPORTED',NULL,NULL,'MT-2026-001',110,38,NULL);
/*!40000 ALTER TABLE `maintenance_tickets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `amount` decimal(12,2) NOT NULL,
  `payment_method` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payment_time` datetime(6) DEFAULT NULL,
  `status` enum('FAILED','PENDING','REFUNDED','SUCCESS') COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `booking_id` bigint DEFAULT NULL,
  `guest_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ledger_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reference_no` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `reconciled_by` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reconciliation_note` text COLLATE utf8mb4_unicode_ci,
  `reconciliation_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_payments_booking_id` (`booking_id`),
  CONSTRAINT `FKc52o2b1jkxttngufqp3t7jr3h` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (8,NULL,'2026-10-03 17:57:23.000000',12000000.00,'VNPAY','2026-10-03 17:57:23.000000','SUCCESS','TXN-VNP-2026100201',26,'Trần Gia Huy','ROOM_CHARGE',NULL,'2026-10-03 17:57:23.000000','Thảo (Kế toán)','Đã đối soát khớp sao kê Vietcombank Techcombank số 992837','2026-10-02 19:13:15.000000'),(9,NULL,'2026-10-03 17:57:23.000000',15000000.00,'MOMO','2026-10-03 17:57:23.000000','SUCCESS','TXN-MOMO-2026100302',27,'Lê Thị Mai','ROOM_CHARGE',NULL,'2026-10-03 17:57:23.000000','Thảo (Kế toán)','Đã đối soát khớp sao kê Vietcombank Techcombank số 992837','2026-10-02 19:13:15.000000'),(10,NULL,'2026-10-03 17:57:23.000000',18000000.00,'BANK_TRANSFER','2026-10-03 17:57:23.000000','SUCCESS','TXN-VCB-2026100403',28,'Đặng Quốc Huy','ROOM_CHARGE',NULL,'2026-10-03 17:57:23.000000','Thảo (Kế toán)','Đã đối soát khớp sao kê Vietcombank Techcombank số 992837','2026-10-02 19:13:15.000000'),(11,NULL,'2026-10-05 16:56:29.621868',36000000.00,'VNPAY','2026-10-04 10:22:28.000000','SUCCESS','TXN-VNP-148280',29,'Nguyễn Hoàng Nam','ROOM_CHARGE','BK-20261003-NAM103','2026-10-04 10:22:28.000000','Long Phùng','','2026-10-05 16:56:29.608592'),(12,NULL,'2026-10-05 16:56:28.196853',36000000.00,'MOMO','2026-10-04 10:22:28.000000','SUCCESS','TXN-MOM-148290',30,'Trần Gia Huy','ROOM_CHARGE','BK-20261006-HUY104','2026-10-04 10:22:28.000000','Long Phùng','','2026-10-05 16:56:28.184322'),(13,NULL,'2026-10-05 16:56:26.470119',25500000.00,'BANK_TRANSFER','2026-10-04 10:22:28.000000','SUCCESS','TXN-BAN-148294',31,'Phạm Tuấn Anh','ROOM_CHARGE','BK-20261005-ANH203','2026-10-04 10:22:28.000000','Long Phùng','','2026-10-05 16:56:26.461360'),(14,NULL,'2026-10-05 16:54:04.405186',75000000.00,'BANK_TRANSFER','2026-10-04 10:22:28.000000','SUCCESS','TXN-BAN-148297',32,'Bùi Phương Thảo','ROOM_CHARGE','BK-20261005-THAO-VIP','2026-10-04 10:22:28.000000','Long Phùng','','2026-10-05 16:54:04.400216'),(15,NULL,'2026-10-05 16:54:07.635142',16500000.00,'CASH','2026-10-04 10:22:28.000000','SUCCESS','TXN-CAS-148302',33,'Lê Thị Mai Hương','ROOM_CHARGE','BK-20261003-HUONG302','2026-10-04 10:22:28.000000','Long Phùng','','2026-10-05 16:54:07.623114'),(16,NULL,'2026-10-05 16:54:13.604295',16500000.00,'PAYOS','2026-10-04 10:22:28.000000','SUCCESS','TXN-PAY-148304',34,'Võ Minh Trí','ROOM_CHARGE','BK-20261007-TRI304','2026-10-04 10:22:28.000000','Long Phùng','','2026-10-05 16:54:13.594597'),(17,NULL,NULL,18500000.00,'BANK_TRANSFER','2026-09-28 14:15:00.000000','SUCCESS','TXN-2026092801',NULL,'Đỗ Hoàng Quân','ROOM_CHARGE','REF-20260928-01',NULL,NULL,NULL,NULL),(18,NULL,NULL,22000000.00,'VNPAY','2026-09-29 11:20:00.000000','SUCCESS','TXN-2026092901',NULL,'Ngô Bảo Châu','ROOM_CHARGE','REF-20260929-01',NULL,NULL,NULL,NULL),(19,NULL,NULL,25500000.00,'MOMO','2026-09-30 16:40:00.000000','SUCCESS','TXN-2026093001',NULL,'Trịnh Thăng Bình','ROOM_CHARGE','REF-20260930-01',NULL,NULL,NULL,NULL),(20,NULL,NULL,31000000.00,'PAYOS','2026-10-01 09:30:00.000000','SUCCESS','TXN-2026100101',NULL,'Vương Đình Huệ','ROOM_CHARGE','REF-20261001-01',NULL,NULL,NULL,NULL),(21,NULL,NULL,38000000.00,'BANK_TRANSFER','2026-10-02 15:45:00.000000','SUCCESS','TXN-2026100201',NULL,'Trần Gia Huy','ROOM_CHARGE','REF-20261002-01',NULL,NULL,NULL,NULL),(22,NULL,'2026-10-05 16:54:18.654356',12000000.00,NULL,'2026-10-05 16:54:18.630812','SUCCESS',NULL,35,NULL,'ROOM_CHARGE',NULL,'2026-10-04 10:59:18.851255','Long Phùng','','2026-10-05 16:54:18.634869'),(23,NULL,'2026-10-04 11:06:15.304068',12000000.00,NULL,NULL,'PENDING',NULL,36,NULL,'ROOM_CHARGE',NULL,'2026-10-04 11:06:15.304068',NULL,NULL,NULL),(24,NULL,'2026-10-04 11:14:10.196660',12000000.00,NULL,NULL,'PENDING',NULL,37,NULL,'ROOM_CHARGE',NULL,'2026-10-04 11:14:10.196660',NULL,NULL,NULL),(25,NULL,'2026-10-04 11:24:21.992412',12000000.00,NULL,NULL,'PENDING',NULL,38,NULL,'ROOM_CHARGE',NULL,'2026-10-04 11:24:21.992412',NULL,NULL,NULL),(26,NULL,'2026-10-04 11:32:22.006677',12000000.00,NULL,NULL,'PENDING',NULL,39,NULL,'ROOM_CHARGE',NULL,'2026-10-04 11:32:22.006677',NULL,NULL,NULL),(27,NULL,'2026-10-05 16:54:40.234640',3288546.00,NULL,'2026-10-05 16:54:40.211871','SUCCESS',NULL,40,NULL,'ROOM_CHARGE',NULL,'2026-10-05 07:49:09.488904','Long Phùng','Đã khớp sao kê tài khoản ngân hàng','2026-10-05 16:54:40.214874'),(28,NULL,'2026-10-05 16:51:05.903869',547479.00,NULL,'2026-10-05 16:51:05.895740','PENDING',NULL,41,NULL,'ROOM_CHARGE',NULL,'2026-10-05 16:30:21.359018',NULL,NULL,NULL);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotions`
--

DROP TABLE IF EXISTS `promotions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promotions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `discount_type` enum('FIXED_AMOUNT','PERCENTAGE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `discount_value` decimal(12,2) NOT NULL,
  `end_date` date NOT NULL,
  `quantity` int DEFAULT NULL,
  `start_date` date NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKjdho73ymbyu46p2hh562dk4kk` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotions`
--

LOCK TABLES `promotions` WRITE;
/*!40000 ALTER TABLE `promotions` DISABLE KEYS */;
INSERT INTO `promotions` VALUES (1,NULL,'2026-10-02 14:37:22.261214','AURA10','PERCENTAGE',10.00,'2027-12-31',100,'2026-01-01','SUMMER','Ưu Đãi Đặt Sớm 10%','2026-10-02 14:37:22.261214'),(2,NULL,'2026-10-02 14:37:22.262192','WELCOME200','FIXED_AMOUNT',200000.00,'2027-12-31',100,'2026-01-01','HONEYMOON','Giảm 200.000₫ Cho Khách Mới','2026-10-02 14:37:22.262192'),(3,NULL,'2026-10-02 14:37:22.264192','VIP20','PERCENTAGE',20.00,'2027-12-31',50,'2026-01-01','ELITE','Đặc Quyền Thành Viên VIP 20%','2026-10-02 14:37:22.264192'),(5,NULL,'2026-10-04 11:40:50.000000','FLC15','PERCENTAGE',15.00,'2027-12-31',100,'2026-01-01','SUMMER','Ưu Đãi Nghỉ Dưỡng FLC 15%','2026-10-04 11:40:50.000000'),(6,NULL,'2026-10-04 11:40:50.000000','LUXE10','PERCENTAGE',10.00,'2027-12-31',100,'2026-01-01','ELITE','Ưu Đãi Sang Trọng Luxe 10%','2026-10-04 11:40:50.000000');
/*!40000 ALTER TABLE `promotions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refill_task_items`
--

DROP TABLE IF EXISTS `refill_task_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refill_task_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `actual_quantity` int NOT NULL,
  `consumed_quantity` int DEFAULT NULL,
  `damaged_quantity` int DEFAULT NULL,
  `is_fulfilled` bit(1) DEFAULT NULL,
  `missing_quantity` int DEFAULT NULL,
  `refill_quantity` int NOT NULL,
  `standard_quantity` int NOT NULL,
  `item_id` bigint NOT NULL,
  `refill_task_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_refill_item_task` (`refill_task_id`),
  KEY `idx_refill_item_item` (`item_id`),
  CONSTRAINT `FKbcid9fin0gj8djiyhb88tpsf1` FOREIGN KEY (`item_id`) REFERENCES `inventory_items` (`id`),
  CONSTRAINT `FKesa9jdm1ladkvfppjc6grwols` FOREIGN KEY (`refill_task_id`) REFERENCES `refill_tasks` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refill_task_items`
--

LOCK TABLES `refill_task_items` WRITE;
/*!40000 ALTER TABLE `refill_task_items` DISABLE KEYS */;
INSERT INTO `refill_task_items` VALUES (1,'2026-09-27 17:02:31.027900','2026-09-27 17:08:44.016756',2,2,0,_binary '',0,2,4,1,1,NULL),(2,'2026-09-27 17:02:31.032424','2026-09-27 17:08:44.036971',1,1,0,_binary '',0,1,2,2,1,NULL),(3,'2026-09-27 17:02:31.036424','2026-09-27 17:08:44.045343',3,1,0,_binary '',0,1,4,3,1,NULL),(24,NULL,'2026-10-05 12:16:30.783810',1,3,0,_binary '',0,2,3,1,14,'2026-10-04 11:57:28.933349');
/*!40000 ALTER TABLE `refill_task_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refill_tasks`
--

DROP TABLE IF EXISTS `refill_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refill_tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `assigned_staff` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `creator` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  `status` enum('CANCELLED','COMPLETED','IN_PROGRESS','PENDING') COLLATE utf8mb4_unicode_ci NOT NULL,
  `task_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `housekeeping_task_id` bigint DEFAULT NULL,
  `villa_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKo9s5wer5ja72b2xhlptqgqoqx` (`task_code`),
  KEY `idx_refill_task_code` (`task_code`),
  KEY `idx_refill_villa` (`villa_id`),
  KEY `idx_refill_status` (`status`),
  KEY `FK5ogxdy5l1bo7dfj7kfwmcetyp` (`housekeeping_task_id`),
  CONSTRAINT `FK5ogxdy5l1bo7dfj7kfwmcetyp` FOREIGN KEY (`housekeeping_task_id`) REFERENCES `housekeeping_tasks` (`id`),
  CONSTRAINT `FKoknb7hwu0kb82nknmlry5115y` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refill_tasks`
--

LOCK TABLES `refill_tasks` WRITE;
/*!40000 ALTER TABLE `refill_tasks` DISABLE KEYS */;
INSERT INTO `refill_tasks` VALUES (1,'2026-09-27 17:02:31.023508','2026-09-27 17:08:44.045343','Trần Văn Kho (Thủ kho)','2026-09-27 17:08:44.042516','Nguyễn Thị Hoa (Housekeeping)','Khách đoàn VIP checkout lúc 11:30. Cần bổ sung vật tư đón khách mới 14:00','COMPLETED','RF-20260928-0001',NULL,29,NULL),(14,NULL,'2026-10-05 12:16:30.783810',NULL,'2026-10-05 12:16:30.779939','hoa.housekeeping@auraholdings.vn','Yêu cầu bù đồ tự động từ kết quả kiểm kê dọn phòng: NT-101','COMPLETED','RF-20261004-8899',1,430,'2026-10-04 11:57:28.921056'),(15,NULL,'2026-10-05 13:38:18.248174',NULL,'2026-10-05 13:38:18.234740','Hệ thống','Tự động bù đồ sau khi hoàn tất dọn phòng Checkout','COMPLETED','RF-20261005-1329',1,430,'2026-10-05 13:12:31.333139'),(16,NULL,'2026-10-05 13:45:53.206959',NULL,'2026-10-05 13:45:53.205195','Hệ thống','Tự động bù đồ sau khi hoàn tất dọn phòng Checkout','COMPLETED','RF-20261005-2646',1,430,'2026-10-05 13:45:22.650471'),(17,NULL,'2026-10-05 13:45:51.693000',NULL,'2026-10-05 13:45:51.690056','Hệ thống','Tự động bù đồ sau khi hoàn tất dọn phòng Checkout','COMPLETED','RF-20261005-5594',2,430,'2026-10-05 13:45:25.594484'),(18,NULL,'2026-10-05 16:49:30.933033',NULL,NULL,'Hệ thống','Tự động bù đồ sau khi hoàn tất dọn phòng Checkout','PENDING','RF-20261005-0931',8,460,'2026-10-05 16:49:30.933033'),(19,NULL,'2026-10-05 16:59:40.400415',NULL,NULL,'Hệ thống','Tự động bù đồ sau khi hoàn tất dọn phòng Checkout','PENDING','RF-20261005-0399',3,431,'2026-10-05 16:59:40.400415'),(20,NULL,'2026-10-05 16:59:42.548637',NULL,NULL,'Hệ thống','Tự động bù đồ sau khi hoàn tất dọn phòng Checkout','PENDING','RF-20261005-2547',7,491,'2026-10-05 16:59:42.548637'),(21,NULL,'2026-10-05 16:59:43.610801',NULL,NULL,'Hệ thống','Tự động bù đồ sau khi hoàn tất dọn phòng Checkout','PENDING','RF-20261005-3610',6,432,'2026-10-05 16:59:43.610801');
/*!40000 ALTER TABLE `refill_tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `rating` int NOT NULL,
  `booking_id` bigint NOT NULL,
  `room_type_id` bigint NOT NULL,
  `guest_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `management_reply` text COLLATE utf8mb4_unicode_ci,
  `replied_at` datetime(6) DEFAULT NULL,
  `sentiment` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `villa_type_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3p9j9vyr1qofbcxju65es206r` (`booking_id`),
  KEY `FKrsky1fnfdmm2chhfxvivf67hx` (`room_type_id`),
  KEY `FKpxm63utxbx5h3xklgro6hlur` (`villa_type_id`),
  CONSTRAINT `FK28an517hrxtt2bsg93uefugrm` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKpxm63utxbx5h3xklgro6hlur` FOREIGN KEY (`villa_type_id`) REFERENCES `villa_types` (`id`),
  CONSTRAINT `FKrsky1fnfdmm2chhfxvivf67hx` FOREIGN KEY (`room_type_id`) REFERENCES `room_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,NULL,'2026-10-04 12:09:40.000000','Kỳ nghỉ tuyệt vời cùng gia đình tại phân khu Ngọc Trai! Quản gia Butler Hoàng phục vụ vô cùng tận tâm và chu đáo, xe điện đón tận nơi đúng giờ.',5,15,3,'Long Phùng','Aura Resort chân thành cảm ơn gia đình quý khách đã tin tưởng lựa chọn chúng tôi! Rất mong được tiếp đón gia đình trong những kỳ nghỉ kế tiếp.','2026-10-04 05:09:40.752000','POSITIVE',1,'2026-10-04 12:09:40.000000'),(2,NULL,'2026-10-04 05:13:09.874560','Biệt thự view biển rất đẹp, hồ bơi riêng sạch sẽ và nước ấm dễ chịu. Điểm trừ nhỏ là đồ ăn sáng nên bổ sung thêm một số món địa phương, nhưng nhìn chung rất hài lòng!',4,16,3,'Phạm Văn Minh (Tổng Quản Lý)','Aura Luxury Resort xin gửi lời cảm ơn chân thành đến Quý khách vì đã dành thời gian chia sẻ cảm nhận quý báu. Những đóng góp của Quý khách là động lực to lớn để đội ngũ tiếp tục hoàn thiện, hướng tới tiêu chuẩn dịch vụ hoàn mỹ nhất. Kính chúc Quý khách luôn nhiều sức khỏe và niềm vui!','2026-10-04 05:13:09.862283','POSITIVE',1,'2026-10-02 12:09:40.000000'),(3,NULL,'2026-10-04 12:09:40.000000','Không gian yên tĩnh, đẳng cấp đúng chuẩn 5 sao quốc tế. Dịch vụ Lotus Spa và bữa tối BBQ ngoài trời set up rất chuyên nghiệp. Chắc chắn sẽ quay lại!',5,17,3,'Phạm Văn Minh (Tổng Quản Lý)','Kính gửi Quý khách, Aura Resort rất vinh hạnh khi mang đến trải nghiệm tuyệt vời cho kỳ nghỉ của Quý khách. Chúc Quý khách vạn sự như ý!','2026-10-02 05:09:40.785000','POSITIVE',1,'2026-09-30 12:09:40.000000'),(4,NULL,'2026-10-04 12:09:40.000000','Resort đẹp, phòng ốc tiện nghi hiện đại. Tuy nhiên hôm check-in phải chờ khoảng 10 phút vì lượng khách đoàn khá đông.',3,18,3,'Long Phùng','Chân thành xin lỗi Quý khách vì sự chậm trễ trong khâu làm thủ tục nhận phòng. Ban quản lý đã cải tiến quy trình đón tiếp nhanh để phục vụ chu đáo hơn.','2026-10-01 05:09:40.790000','NEUTRAL',1,'2026-09-28 12:09:40.000000'),(5,NULL,'2026-10-04 12:09:40.000000','Dịch vụ trên cả mong đợi! Từ khâu đón tại sân bay bằng xe Maybach đến bữa tiệc rượu vang lúc hoàng hôn đều được chăm chút tỉ mỉ.',5,19,3,'Long Phùng',NULL,NULL,'POSITIVE',1,'2026-09-26 12:09:40.000000');
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_consumption_records`
--

DROP TABLE IF EXISTS `room_consumption_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_consumption_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `approved_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `evidence_photo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `item_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `item_type` enum('ASSET_DAMAGED','ASSET_LOST','MINIBAR_CONSUMED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  `quantity` int NOT NULL,
  `recorded_by` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('APPROVED_CHARGED','PENDING_RECEPTION_APPROVAL','WAIVED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_price` decimal(12,2) NOT NULL,
  `unit_price` decimal(12,2) NOT NULL,
  `booking_id` bigint DEFAULT NULL,
  `housekeeping_task_id` bigint NOT NULL,
  `room_id` bigint DEFAULT NULL,
  `villa_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_rc_booking` (`booking_id`),
  KEY `idx_rc_task` (`housekeeping_task_id`),
  KEY `idx_rc_status` (`status`),
  KEY `FKsl202igl9g545g84aqesxdh2w` (`villa_id`),
  KEY `FKplabvnh7jvq1150y7l809o3q8` (`room_id`),
  CONSTRAINT `FK7wvrvo4qn9m6qoaqwf6i1xq7d` FOREIGN KEY (`housekeeping_task_id`) REFERENCES `housekeeping_tasks` (`id`),
  CONSTRAINT `FKplabvnh7jvq1150y7l809o3q8` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FKqhbwqf9tx8pf1xoclvt1c68ah` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKsl202igl9g545g84aqesxdh2w` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_consumption_records`
--

LOCK TABLES `room_consumption_records` WRITE;
/*!40000 ALTER TABLE `room_consumption_records` DISABLE KEYS */;
INSERT INTO `room_consumption_records` VALUES (4,'2026-09-30 02:41:56.955014','2026-09-30 02:41:56.955014',NULL,NULL,'Bia Heineken lon 330ml','MINIBAR_CONSUMED',NULL,2,'Nguyễn Thị Hoa','PENDING_RECEPTION_APPROVAL',70000.00,35000.00,NULL,4,111,430,NULL),(5,'2026-09-30 02:41:56.961959','2026-09-30 02:41:56.961959',NULL,NULL,'Nước khoáng có gas Perrier 330ml','MINIBAR_CONSUMED',NULL,1,'Nguyễn Thị Hoa','PENDING_RECEPTION_APPROVAL',45000.00,45000.00,NULL,4,111,430,NULL),(6,'2026-09-30 02:41:56.966821','2026-09-30 02:41:56.966821',NULL,NULL,'Tách trà gốm sứ cao cấp Bát Tràng (Sứt mẻ)','ASSET_DAMAGED','Khách làm rơi sứt quai tách trà trên bàn ăn ngoài ban công.',1,'Nguyễn Thị Hoa','PENDING_RECEPTION_APPROVAL',120000.00,120000.00,NULL,4,111,430,NULL),(16,NULL,'2026-10-04 11:57:28.831836',NULL,NULL,'Bia Heineken lon 330ml','MINIBAR_CONSUMED',NULL,3,'hoa.housekeeping@auraholdings.vn','PENDING_RECEPTION_APPROVAL',105000.00,35000.00,26,1,NULL,430,'2026-10-04 11:57:28.831836');
/*!40000 ALTER TABLE `room_consumption_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_images`
--

DROP TABLE IF EXISTS `room_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `room_type_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6o0wwek8pty6k3cqlqjscdywy` (`room_type_id`),
  CONSTRAINT `FK6o0wwek8pty6k3cqlqjscdywy` FOREIGN KEY (`room_type_id`) REFERENCES `room_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_images`
--

LOCK TABLES `room_images` WRITE;
/*!40000 ALTER TABLE `room_images` DISABLE KEYS */;
/*!40000 ALTER TABLE `room_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_types`
--

DROP TABLE IF EXISTS `room_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `base_price` decimal(12,2) NOT NULL,
  `capacity` int NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `image_url` longtext COLLATE utf8mb4_unicode_ci,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dynamic_price` decimal(12,2) DEFAULT NULL,
  `is_dynamic_pricing_enabled` bit(1) DEFAULT NULL,
  `bed_type` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `adults` int DEFAULT '2',
  `children` int DEFAULT '0',
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKb70k1tp1aa52elkkxht660u36` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_types`
--

LOCK TABLES `room_types` WRITE;
/*!40000 ALTER TABLE `room_types` DISABLE KEYS */;
INSERT INTO `room_types` VALUES (1,'2026-09-23 00:56:08.314013','2026-09-23 01:02:35.253093',0.00,1,'Biệt thự nghỉ dưỡng cao cấp.','/assets/images/uploads/villa_108db697.png','Single Bed',0.00,_binary '','Rộng 1m × Dài 1.9m',1,0,NULL),(2,'2026-09-23 01:05:26.902815','2026-09-23 01:32:40.804286',0.00,2,'Tiêu chuẩn giường ngủ và nghỉ dưỡng cao cấp.','/assets/images/uploads/villa_dbf180bc.jpg','Queen Size Bed',0.00,_binary '','Rộng 1.8m × Dài 2.0m',2,0,NULL),(3,'2026-09-23 01:12:42.419299','2026-09-23 01:30:59.466232',0.00,3,'Tiêu chuẩn giường ngủ và nghỉ dưỡng cao cấp.','/assets/images/uploads/villa_9f7e91b6.jpg','King Size Bed',0.00,_binary '','Rộng 1.8m × Dài 2.0m',2,1,NULL),(4,'2026-09-23 01:15:48.982077','2026-09-23 01:25:47.101613',0.00,4,'Tiêu chuẩn giường ngủ và nghỉ dưỡng cao cấp.','/assets/images/uploads/villa_44454c00.jpg','Super King',0.00,_binary '','Rộng 1.8m × Dài 2.0m',4,0,NULL);
/*!40000 ALTER TABLE `room_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `floor` int DEFAULT NULL,
  `room_number` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('AVAILABLE','CLEANING','MAINTENANCE','OCCUPIED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `room_type_id` bigint DEFAULT NULL,
  `current_guest_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_cleaned_at` datetime(6) DEFAULT NULL,
  `ozone_status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `villa_id` bigint DEFAULT NULL,
  `zone_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh9m2n1paq5hmd3u0klfl7wsfv` (`room_type_id`),
  KEY `FKq0r98dfkfj2pqyk2e7697q3ht` (`villa_id`),
  KEY `FK6jgp4sxs0mpyk4pr431yk4j6v` (`zone_id`),
  CONSTRAINT `FK6jgp4sxs0mpyk4pr431yk4j6v` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`id`),
  CONSTRAINT `FKh9m2n1paq5hmd3u0klfl7wsfv` FOREIGN KEY (`room_type_id`) REFERENCES `room_types` (`id`),
  CONSTRAINT `FKq0r98dfkfj2pqyk2e7697q3ht` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1825 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (110,'2026-09-27 14:44:39.481298','2026-10-02 16:44:35.842671',1,'01-P1','AVAILABLE',1,NULL,'2026-10-02 16:44:35.828002','CLEANED',NULL,NULL,'Phòng Ngủ Master',38,5,NULL),(111,'2026-09-27 14:50:01.094667','2026-09-30 02:41:56.999172',1,'01-P1','CLEANING',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ Master',NULL,6,NULL),(112,'2026-09-27 14:50:01.097172','2026-09-27 14:50:01.097172',2,'01-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ Phụ 1 (Queen Size Bed)',NULL,6,NULL),(113,'2026-09-27 14:50:01.101181','2026-09-27 14:50:01.101181',3,'01-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ Phụ 2 (King Size Bed)',NULL,6,NULL),(164,'2026-09-30 09:26:05.780065','2026-09-30 09:26:05.780065',1,'Ngọc Trai 01-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ Master',29,4,NULL),(165,'2026-09-30 09:26:05.787066','2026-09-30 09:26:05.787066',2,'Ngọc Trai 01-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ Phụ 1 (King Size Bed)',29,4,NULL),(166,'2026-09-30 10:12:11.088197','2026-09-30 10:12:11.088197',1,'OCEAN-99-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(167,'2026-09-30 10:12:11.091344','2026-09-30 10:12:11.091344',2,'OCEAN-99-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(168,'2026-09-30 10:12:11.093062','2026-09-30 10:12:11.093062',2,'OCEAN-99-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,4,NULL),(169,'2026-09-30 10:12:11.094116','2026-09-30 10:12:11.094116',2,'OCEAN-99-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,4,NULL),(1449,'2026-09-30 11:25:26.703855','2026-09-30 11:25:26.703855',1,'NT-002-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',431,4,NULL),(1450,'2026-09-30 11:25:26.703855','2026-09-30 11:25:26.703855',1,'NT-002-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',431,4,NULL),(1451,'2026-09-30 11:25:26.704842','2026-10-05 16:59:40.435735',1,'NT-002-P3','AVAILABLE',2,NULL,'2026-10-05 16:59:40.391695','CLEANED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',431,4,NULL),(1458,'2026-09-30 11:25:26.746242','2026-09-30 11:25:26.746242',1,'NT-005-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1459,'2026-09-30 11:25:26.746768','2026-09-30 11:25:26.746768',2,'NT-005-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1460,'2026-09-30 11:25:26.747286','2026-09-30 11:25:26.747286',2,'NT-005-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,4,NULL),(1461,'2026-09-30 11:25:26.747799','2026-09-30 11:25:26.747799',2,'NT-005-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,4,NULL),(1462,'2026-09-30 11:25:26.747799','2026-09-30 11:25:26.747799',2,'NT-005-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,4,NULL),(1463,'2026-09-30 11:25:26.748322','2026-09-30 11:25:26.748322',2,'NT-005-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,4,NULL),(1464,'2026-09-30 11:25:26.761102','2026-09-30 11:25:26.761102',1,'NT-006-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1465,'2026-09-30 11:25:26.761753','2026-09-30 11:25:26.761753',1,'NT-006-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,4,NULL),(1466,'2026-09-30 11:25:26.774045','2026-09-30 11:25:26.774045',1,'NT-007-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1467,'2026-09-30 11:25:26.774045','2026-09-30 11:25:26.774045',2,'NT-007-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1468,'2026-09-30 11:25:26.775052','2026-09-30 11:25:26.775052',2,'NT-007-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,4,NULL),(1469,'2026-09-30 11:25:26.775557','2026-09-30 11:25:26.775557',2,'NT-007-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,4,NULL),(1470,'2026-09-30 11:25:26.776105','2026-09-30 11:25:26.776105',2,'NT-007-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,4,NULL),(1471,'2026-09-30 11:25:26.776615','2026-09-30 11:25:26.776615',2,'NT-007-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,4,NULL),(1472,'2026-09-30 11:25:26.789451','2026-09-30 11:25:26.789451',1,'NT-008-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1473,'2026-09-30 11:25:26.789974','2026-09-30 11:25:26.789974',1,'NT-008-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1474,'2026-09-30 11:25:26.789974','2026-09-30 11:25:26.789974',1,'NT-008-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,4,NULL),(1475,'2026-09-30 11:25:26.799726','2026-09-30 11:25:26.799726',1,'NT-009-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1476,'2026-09-30 11:25:26.808627','2026-09-30 11:25:26.808627',1,'NT-010-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1477,'2026-09-30 11:25:26.808627','2026-09-30 11:25:26.808627',2,'NT-010-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1478,'2026-09-30 11:25:26.808627','2026-09-30 11:25:26.808627',2,'NT-010-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,4,NULL),(1479,'2026-09-30 11:25:26.810259','2026-09-30 11:25:26.810259',2,'NT-010-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,4,NULL),(1480,'2026-09-30 11:25:26.810259','2026-09-30 11:25:26.810259',2,'NT-010-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,4,NULL),(1481,'2026-09-30 11:25:26.820427','2026-09-30 11:25:26.820427',1,'NT-011-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1482,'2026-09-30 11:25:26.820427','2026-09-30 11:25:26.820427',1,'NT-011-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1483,'2026-09-30 11:25:26.820427','2026-09-30 11:25:26.820427',1,'NT-011-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,4,NULL),(1484,'2026-09-30 11:25:26.833579','2026-09-30 11:25:26.833579',1,'NT-012-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1485,'2026-09-30 11:25:26.834957','2026-09-30 11:25:26.834957',2,'NT-012-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,4,NULL),(1486,'2026-09-30 11:25:26.845934','2026-09-30 11:25:26.845934',1,'NT-013-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1487,'2026-09-30 11:25:26.845934','2026-09-30 11:25:26.845934',1,'NT-013-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1488,'2026-09-30 11:25:26.845934','2026-09-30 11:25:26.845934',1,'NT-013-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,4,NULL),(1489,'2026-09-30 11:25:26.846943','2026-09-30 11:25:26.846943',1,'NT-013-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,4,NULL),(1490,'2026-09-30 11:25:26.847448','2026-09-30 11:25:26.847448',1,'NT-013-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,4,NULL),(1491,'2026-09-30 11:25:26.848108','2026-09-30 11:25:26.848108',1,'NT-013-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,4,NULL),(1492,'2026-09-30 11:25:26.857398','2026-09-30 11:25:26.857398',1,'NT-014-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1493,'2026-09-30 11:25:26.858214','2026-09-30 11:25:26.858214',1,'NT-014-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1494,'2026-09-30 11:25:26.858214','2026-09-30 11:25:26.858214',1,'NT-014-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,4,NULL),(1495,'2026-09-30 11:25:26.867930','2026-09-30 11:25:26.867930',1,'NT-015-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1496,'2026-09-30 11:25:26.868509','2026-09-30 11:25:26.868509',2,'NT-015-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1497,'2026-09-30 11:25:26.868509','2026-09-30 11:25:26.868509',2,'NT-015-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,4,NULL),(1498,'2026-09-30 11:25:26.869041','2026-09-30 11:25:26.869041',2,'NT-015-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,4,NULL),(1499,'2026-09-30 11:25:26.869706','2026-09-30 11:25:26.869706',2,'NT-015-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,4,NULL),(1500,'2026-09-30 11:25:26.870231','2026-09-30 11:25:26.870231',2,'NT-015-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,4,NULL),(1501,'2026-09-30 11:25:26.878873','2026-09-30 11:25:26.878873',1,'NT-016-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1502,'2026-09-30 11:25:26.878873','2026-09-30 11:25:26.878873',2,'NT-016-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,4,NULL),(1503,'2026-09-30 11:25:26.888127','2026-09-30 11:25:26.888127',1,'NT-017-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1504,'2026-09-30 11:25:26.888127','2026-09-30 11:25:26.888127',2,'NT-017-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,4,NULL),(1505,'2026-09-30 11:25:26.897843','2026-09-30 11:25:26.897843',1,'NT-018-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1506,'2026-09-30 11:25:26.898349','2026-09-30 11:25:26.898349',1,'NT-018-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,4,NULL),(1507,'2026-09-30 11:25:26.910434','2026-09-30 11:25:26.910434',1,'NT-019-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1508,'2026-09-30 11:25:26.912096','2026-09-30 11:25:26.912096',1,'NT-019-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1509,'2026-09-30 11:25:26.912096','2026-09-30 11:25:26.912096',1,'NT-019-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,4,NULL),(1510,'2026-09-30 11:25:26.922385','2026-09-30 11:25:26.922385',1,'NT-020-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1511,'2026-09-30 11:25:26.943555','2026-09-30 11:25:26.943555',1,'NT-021-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1512,'2026-09-30 11:25:26.944561','2026-09-30 11:25:26.944561',1,'NT-021-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,4,NULL),(1513,'2026-09-30 11:25:26.971401','2026-09-30 11:25:26.971401',1,'NT-022-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1514,'2026-09-30 11:25:26.972407','2026-09-30 11:25:26.972407',2,'NT-022-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1515,'2026-09-30 11:25:26.972911','2026-09-30 11:25:26.972911',2,'NT-022-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,4,NULL),(1516,'2026-09-30 11:25:26.985471','2026-09-30 11:25:26.985471',1,'NT-023-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1517,'2026-09-30 11:25:26.986475','2026-09-30 11:25:26.986475',2,'NT-023-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1518,'2026-09-30 11:25:26.986982','2026-09-30 11:25:26.986982',2,'NT-023-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,4,NULL),(1519,'2026-09-30 11:25:26.995357','2026-09-30 11:25:26.995357',1,'NT-024-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1520,'2026-09-30 11:25:27.006432','2026-09-30 11:25:27.006432',1,'NT-025-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1521,'2026-09-30 11:25:27.017625','2026-09-30 11:25:27.017625',1,'NT-026-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1522,'2026-09-30 11:25:27.018141','2026-09-30 11:25:27.018141',2,'NT-026-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1523,'2026-09-30 11:25:27.018141','2026-09-30 11:25:27.018141',2,'NT-026-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,4,NULL),(1524,'2026-09-30 11:25:27.018797','2026-09-30 11:25:27.018797',2,'NT-026-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,4,NULL),(1525,'2026-09-30 11:25:27.019329','2026-09-30 11:25:27.019329',2,'NT-026-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,4,NULL),(1526,'2026-09-30 11:25:27.019329','2026-09-30 11:25:27.019329',2,'NT-026-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,4,NULL),(1527,'2026-09-30 11:25:27.029026','2026-09-30 11:25:27.029026',1,'NT-027-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1528,'2026-09-30 11:25:27.038813','2026-09-30 11:25:27.038813',1,'NT-028-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1529,'2026-09-30 11:25:27.047813','2026-09-30 11:25:27.047813',1,'NT-029-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1530,'2026-09-30 11:25:27.056438','2026-09-30 11:25:27.056438',1,'NT-030-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,NULL),(1531,'2026-09-30 11:25:27.056961','2026-09-30 11:25:27.056961',1,'NT-030-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,NULL),(1532,'2026-09-30 11:25:27.056961','2026-09-30 11:25:27.056961',1,'NT-030-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,4,NULL),(1548,'2026-09-30 11:25:27.109940','2026-09-30 11:25:27.109940',1,'SB-005-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',464,5,NULL),(1549,'2026-09-30 11:25:27.118904','2026-09-30 11:25:27.118904',1,'SB-006-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1550,'2026-09-30 11:25:27.119411','2026-09-30 11:25:27.119411',2,'SB-006-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1551,'2026-09-30 11:25:27.119411','2026-09-30 11:25:27.119411',2,'SB-006-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,5,NULL),(1552,'2026-09-30 11:25:27.128220','2026-09-30 11:25:27.128220',1,'SB-007-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1553,'2026-09-30 11:25:27.128724','2026-09-30 11:25:27.128724',2,'SB-007-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1554,'2026-09-30 11:25:27.129244','2026-09-30 11:25:27.129244',2,'SB-007-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,5,NULL),(1555,'2026-09-30 11:25:27.129752','2026-09-30 11:25:27.129752',2,'SB-007-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1556,'2026-09-30 11:25:27.130270','2026-09-30 11:25:27.130270',2,'SB-007-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,5,NULL),(1557,'2026-09-30 11:25:27.130270','2026-09-30 11:25:27.130270',2,'SB-007-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,5,NULL),(1558,'2026-09-30 11:25:27.143373','2026-09-30 11:25:27.143373',1,'SB-008-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1559,'2026-09-30 11:25:27.143967','2026-09-30 11:25:27.143967',1,'SB-008-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1560,'2026-09-30 11:25:27.144481','2026-09-30 11:25:27.144481',1,'SB-008-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,5,NULL),(1561,'2026-09-30 11:25:27.145049','2026-09-30 11:25:27.145049',1,'SB-008-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1562,'2026-09-30 11:25:27.145049','2026-09-30 11:25:27.145049',1,'SB-008-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,5,NULL),(1563,'2026-09-30 11:25:27.146925','2026-09-30 11:25:27.146925',1,'SB-008-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,5,NULL),(1564,'2026-09-30 11:25:27.156299','2026-09-30 11:25:27.156299',1,'SB-009-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1565,'2026-09-30 11:25:27.156810','2026-09-30 11:25:27.156810',1,'SB-009-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1566,'2026-09-30 11:25:27.157339','2026-09-30 11:25:27.157339',1,'SB-009-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,5,NULL),(1567,'2026-09-30 11:25:27.157339','2026-09-30 11:25:27.157339',1,'SB-009-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1568,'2026-09-30 11:25:27.157339','2026-09-30 11:25:27.157339',1,'SB-009-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,5,NULL),(1569,'2026-09-30 11:25:27.158343','2026-09-30 11:25:27.158343',1,'SB-009-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,5,NULL),(1570,'2026-09-30 11:25:27.166899','2026-09-30 11:25:27.166899',1,'SB-010-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1571,'2026-09-30 11:25:27.167430','2026-09-30 11:25:27.167430',2,'SB-010-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1572,'2026-09-30 11:25:27.167430','2026-09-30 11:25:27.167430',2,'SB-010-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,5,NULL),(1573,'2026-09-30 11:25:27.176573','2026-09-30 11:25:27.176573',1,'SB-011-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1574,'2026-09-30 11:25:27.177579','2026-09-30 11:25:27.177579',2,'SB-011-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1575,'2026-09-30 11:25:27.178600','2026-09-30 11:25:27.178600',2,'SB-011-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,5,NULL),(1576,'2026-09-30 11:25:27.179110','2026-09-30 11:25:27.179110',2,'SB-011-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1577,'2026-09-30 11:25:27.179648','2026-09-30 11:25:27.179648',2,'SB-011-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,5,NULL),(1578,'2026-09-30 11:25:27.179648','2026-09-30 11:25:27.179648',2,'SB-011-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,5,NULL),(1579,'2026-09-30 11:25:27.189711','2026-09-30 11:25:27.189711',1,'SB-012-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1580,'2026-09-30 11:25:27.189711','2026-09-30 11:25:27.189711',1,'SB-012-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1581,'2026-09-30 11:25:27.190277','2026-09-30 11:25:27.190277',1,'SB-012-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,5,NULL),(1582,'2026-09-30 11:25:27.190803','2026-09-30 11:25:27.190803',1,'SB-012-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1583,'2026-09-30 11:25:27.190803','2026-09-30 11:25:27.190803',1,'SB-012-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,5,NULL),(1584,'2026-09-30 11:25:27.202320','2026-09-30 11:25:27.202320',1,'SB-013-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1585,'2026-09-30 11:25:27.202826','2026-09-30 11:25:27.202826',1,'SB-013-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1586,'2026-09-30 11:25:27.202826','2026-09-30 11:25:27.202826',1,'SB-013-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,5,NULL),(1587,'2026-09-30 11:25:27.203834','2026-09-30 11:25:27.203834',1,'SB-013-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1588,'2026-09-30 11:25:27.204340','2026-09-30 11:25:27.204340',1,'SB-013-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,5,NULL),(1589,'2026-09-30 11:25:27.214717','2026-09-30 11:25:27.214717',1,'SB-014-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1590,'2026-09-30 11:25:27.215223','2026-09-30 11:25:27.215223',1,'SB-014-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1591,'2026-09-30 11:25:27.215773','2026-09-30 11:25:27.215773',1,'SB-014-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,5,NULL),(1592,'2026-09-30 11:25:27.225061','2026-09-30 11:25:27.225061',1,'SB-015-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1593,'2026-09-30 11:25:27.232784','2026-09-30 11:25:27.232784',1,'SB-016-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1594,'2026-09-30 11:25:27.241852','2026-09-30 11:25:27.241852',1,'SB-017-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1595,'2026-09-30 11:25:27.242429','2026-09-30 11:25:27.242429',2,'SB-017-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,5,NULL),(1596,'2026-09-30 11:25:27.250095','2026-09-30 11:25:27.250095',1,'SB-018-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1597,'2026-09-30 11:25:27.251102','2026-09-30 11:25:27.251102',2,'SB-018-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1598,'2026-09-30 11:25:27.251608','2026-09-30 11:25:27.251608',2,'SB-018-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,5,NULL),(1599,'2026-09-30 11:25:27.251608','2026-09-30 11:25:27.251608',2,'SB-018-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1600,'2026-09-30 11:25:27.251608','2026-09-30 11:25:27.251608',2,'SB-018-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,5,NULL),(1601,'2026-09-30 11:25:27.252613','2026-09-30 11:25:27.252613',2,'SB-018-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,5,NULL),(1602,'2026-09-30 11:25:27.265865','2026-09-30 11:25:27.265865',1,'SB-019-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1603,'2026-09-30 11:25:27.267176','2026-09-30 11:25:27.267176',1,'SB-019-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1604,'2026-09-30 11:25:27.267762','2026-09-30 11:25:27.267762',1,'SB-019-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,5,NULL),(1605,'2026-09-30 11:25:27.285353','2026-09-30 11:25:27.285353',1,'SB-020-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1606,'2026-09-30 11:25:27.286424','2026-09-30 11:25:27.286424',2,'SB-020-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1607,'2026-09-30 11:25:27.286961','2026-09-30 11:25:27.286961',2,'SB-020-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,5,NULL),(1608,'2026-09-30 11:25:27.287530','2026-09-30 11:25:27.287530',2,'SB-020-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1609,'2026-09-30 11:25:27.288321','2026-09-30 11:25:27.288321',2,'SB-020-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,5,NULL),(1610,'2026-09-30 11:25:27.303993','2026-09-30 11:25:27.303993',1,'SB-021-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1611,'2026-09-30 11:25:27.304499','2026-09-30 11:25:27.304499',1,'SB-021-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,5,NULL),(1612,'2026-09-30 11:25:27.320051','2026-09-30 11:25:27.320051',1,'SB-022-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1613,'2026-09-30 11:25:27.321072','2026-09-30 11:25:27.321072',1,'SB-022-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1614,'2026-09-30 11:25:27.321589','2026-09-30 11:25:27.321589',1,'SB-022-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,5,NULL),(1615,'2026-09-30 11:25:27.322130','2026-09-30 11:25:27.322130',1,'SB-022-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1616,'2026-09-30 11:25:27.323139','2026-09-30 11:25:27.323139',1,'SB-022-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,5,NULL),(1617,'2026-09-30 11:25:27.323647','2026-09-30 11:25:27.323647',1,'SB-022-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,5,NULL),(1618,'2026-09-30 11:25:27.338235','2026-09-30 11:25:27.338235',1,'SB-023-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1619,'2026-09-30 11:25:27.338752','2026-09-30 11:25:27.338752',2,'SB-023-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1620,'2026-09-30 11:25:27.339335','2026-09-30 11:25:27.339335',2,'SB-023-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,5,NULL),(1621,'2026-09-30 11:25:27.340847','2026-09-30 11:25:27.340847',2,'SB-023-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1622,'2026-09-30 11:25:27.341911','2026-09-30 11:25:27.341911',2,'SB-023-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,5,NULL),(1623,'2026-09-30 11:25:27.359590','2026-09-30 11:25:27.359590',1,'SB-024-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1624,'2026-09-30 11:25:27.361167','2026-09-30 11:25:27.361167',1,'SB-024-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,5,NULL),(1625,'2026-09-30 11:25:27.380244','2026-09-30 11:25:27.380244',1,'SB-025-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1626,'2026-09-30 11:25:27.381627','2026-09-30 11:25:27.381627',1,'SB-025-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1627,'2026-09-30 11:25:27.382632','2026-09-30 11:25:27.382632',1,'SB-025-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,5,NULL),(1628,'2026-09-30 11:25:27.383667','2026-09-30 11:25:27.383667',1,'SB-025-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1629,'2026-09-30 11:25:27.384706','2026-09-30 11:25:27.384706',1,'SB-025-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,5,NULL),(1630,'2026-09-30 11:25:27.401297','2026-09-30 11:25:27.401297',1,'SB-026-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1631,'2026-09-30 11:25:27.415340','2026-09-30 11:25:27.415340',1,'SB-027-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1632,'2026-09-30 11:25:27.415846','2026-09-30 11:25:27.415846',2,'SB-027-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1633,'2026-09-30 11:25:27.416891','2026-09-30 11:25:27.416891',2,'SB-027-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,5,NULL),(1634,'2026-09-30 11:25:27.417419','2026-09-30 11:25:27.417419',2,'SB-027-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1635,'2026-09-30 11:25:27.417943','2026-09-30 11:25:27.417943',2,'SB-027-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,5,NULL),(1636,'2026-09-30 11:25:27.418452','2026-09-30 11:25:27.418452',2,'SB-027-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,5,NULL),(1637,'2026-09-30 11:25:27.432535','2026-09-30 11:25:27.432535',1,'SB-028-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1638,'2026-09-30 11:25:27.434047','2026-09-30 11:25:27.434047',2,'SB-028-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,5,NULL),(1639,'2026-09-30 11:25:27.434581','2026-09-30 11:25:27.434581',2,'SB-028-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,5,NULL),(1640,'2026-09-30 11:25:27.435089','2026-09-30 11:25:27.435089',2,'SB-028-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,5,NULL),(1641,'2026-09-30 11:25:27.435640','2026-09-30 11:25:27.435640',2,'SB-028-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,5,NULL),(1642,'2026-09-30 11:25:27.435640','2026-09-30 11:25:27.435640',2,'SB-028-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,5,NULL),(1643,'2026-09-30 11:25:27.451057','2026-09-30 11:25:27.451057',1,'SB-029-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1644,'2026-09-30 11:25:27.452088','2026-09-30 11:25:27.452088',1,'SB-029-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,5,NULL),(1645,'2026-09-30 11:25:27.465879','2026-09-30 11:25:27.465879',1,'SB-030-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,5,NULL),(1646,'2026-09-30 11:25:27.467046','2026-09-30 11:25:27.467046',1,'SB-030-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,5,NULL),(1651,'2026-09-30 11:25:27.495014','2026-09-30 11:25:27.495014',1,'SH-003-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',492,6,NULL),(1652,'2026-09-30 11:25:27.495548','2026-09-30 11:25:27.495548',2,'SH-003-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',492,6,NULL),(1657,'2026-09-30 11:25:27.525710','2026-09-30 11:25:27.525710',1,'SH-006-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1658,'2026-09-30 11:25:27.526214','2026-09-30 11:25:27.526214',2,'SH-006-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1659,'2026-09-30 11:25:27.527282','2026-09-30 11:25:27.527282',3,'SH-006-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,6,NULL),(1660,'2026-09-30 11:25:27.527831','2026-09-30 11:25:27.527831',3,'SH-006-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,6,NULL),(1661,'2026-09-30 11:25:27.529960','2026-09-30 11:25:27.529960',3,'SH-006-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,6,NULL),(1662,'2026-09-30 11:25:27.530464','2026-09-30 11:25:27.530464',3,'SH-006-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,6,NULL),(1663,'2026-09-30 11:25:27.541888','2026-09-30 11:25:27.541888',1,'SH-007-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1664,'2026-09-30 11:25:27.542417','2026-09-30 11:25:27.542417',2,'SH-007-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,6,NULL),(1665,'2026-09-30 11:25:27.553633','2026-09-30 11:25:27.553633',1,'SH-008-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1666,'2026-09-30 11:25:27.554142','2026-09-30 11:25:27.554142',2,'SH-008-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1667,'2026-09-30 11:25:27.554669','2026-09-30 11:25:27.554669',3,'SH-008-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,6,NULL),(1668,'2026-09-30 11:25:27.555375','2026-09-30 11:25:27.555375',3,'SH-008-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,6,NULL),(1669,'2026-09-30 11:25:27.555930','2026-09-30 11:25:27.555930',3,'SH-008-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,6,NULL),(1670,'2026-09-30 11:25:27.556593','2026-09-30 11:25:27.556593',3,'SH-008-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,6,NULL),(1671,'2026-09-30 11:25:27.566138','2026-09-30 11:25:27.566138',1,'SH-009-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1672,'2026-09-30 11:25:27.566138','2026-09-30 11:25:27.566138',2,'SH-009-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1673,'2026-09-30 11:25:27.566138','2026-09-30 11:25:27.566138',2,'SH-009-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1674,'2026-09-30 11:25:27.567142','2026-09-30 11:25:27.567142',2,'SH-009-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,6,NULL),(1675,'2026-09-30 11:25:27.567142','2026-09-30 11:25:27.567142',2,'SH-009-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,6,NULL),(1676,'2026-09-30 11:25:27.578062','2026-09-30 11:25:27.578062',1,'SH-010-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1677,'2026-09-30 11:25:27.578813','2026-09-30 11:25:27.578813',2,'SH-010-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1678,'2026-09-30 11:25:27.579821','2026-09-30 11:25:27.579821',3,'SH-010-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1679,'2026-09-30 11:25:27.639746','2026-09-30 11:25:27.639746',1,'SH-011-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1680,'2026-09-30 11:25:27.640275','2026-09-30 11:25:27.640275',2,'SH-011-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1681,'2026-09-30 11:25:27.640834','2026-09-30 11:25:27.640834',2,'SH-011-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1682,'2026-09-30 11:25:27.651248','2026-09-30 11:25:27.651248',1,'SH-012-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1683,'2026-09-30 11:25:27.652254','2026-09-30 11:25:27.652254',2,'SH-012-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,6,NULL),(1684,'2026-09-30 11:25:27.664379','2026-09-30 11:25:27.664379',1,'SH-013-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1685,'2026-09-30 11:25:27.664910','2026-09-30 11:25:27.664910',2,'SH-013-P2','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (Queen Size Bed)',NULL,6,NULL),(1686,'2026-09-30 11:25:27.678861','2026-09-30 11:25:27.678861',1,'SH-014-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1687,'2026-09-30 11:25:27.679381','2026-09-30 11:25:27.679381',2,'SH-014-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1688,'2026-09-30 11:25:27.680526','2026-09-30 11:25:27.680526',2,'SH-014-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1689,'2026-09-30 11:25:27.681090','2026-09-30 11:25:27.681090',2,'SH-014-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,6,NULL),(1690,'2026-09-30 11:25:27.682099','2026-09-30 11:25:27.682099',2,'SH-014-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,6,NULL),(1691,'2026-09-30 11:25:27.697595','2026-09-30 11:25:27.697595',1,'SH-015-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1692,'2026-09-30 11:25:27.698652','2026-09-30 11:25:27.698652',2,'SH-015-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1693,'2026-09-30 11:25:27.699160','2026-09-30 11:25:27.699160',3,'SH-015-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1694,'2026-09-30 11:25:27.709689','2026-09-30 11:25:27.709689',1,'SH-016-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1695,'2026-09-30 11:25:27.709689','2026-09-30 11:25:27.709689',2,'SH-016-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1696,'2026-09-30 11:25:27.710245','2026-09-30 11:25:27.710245',3,'SH-016-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1697,'2026-09-30 11:25:27.710753','2026-09-30 11:25:27.710753',3,'SH-016-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,6,NULL),(1698,'2026-09-30 11:25:27.710753','2026-09-30 11:25:27.710753',3,'SH-016-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,6,NULL),(1699,'2026-09-30 11:25:27.720692','2026-09-30 11:25:27.720692',1,'SH-017-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1700,'2026-09-30 11:25:27.721227','2026-09-30 11:25:27.721227',2,'SH-017-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1701,'2026-09-30 11:25:27.721227','2026-09-30 11:25:27.721227',2,'SH-017-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1702,'2026-09-30 11:25:27.730356','2026-09-30 11:25:27.730356',1,'SH-018-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1703,'2026-09-30 11:25:27.730877','2026-09-30 11:25:27.730877',2,'SH-018-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1704,'2026-09-30 11:25:27.731388','2026-09-30 11:25:27.731388',3,'SH-018-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1705,'2026-09-30 11:25:27.731388','2026-09-30 11:25:27.731388',3,'SH-018-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,6,NULL),(1706,'2026-09-30 11:25:27.731904','2026-09-30 11:25:27.731904',3,'SH-018-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,6,NULL),(1707,'2026-09-30 11:25:27.741267','2026-09-30 11:25:27.741267',1,'SH-019-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1708,'2026-09-30 11:25:27.741847','2026-09-30 11:25:27.741847',2,'SH-019-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1709,'2026-09-30 11:25:27.741847','2026-09-30 11:25:27.741847',2,'SH-019-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1710,'2026-09-30 11:25:27.742359','2026-09-30 11:25:27.742359',2,'SH-019-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,6,NULL),(1711,'2026-09-30 11:25:27.742893','2026-09-30 11:25:27.742893',2,'SH-019-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,6,NULL),(1712,'2026-09-30 11:25:27.753335','2026-09-30 11:25:27.753335',1,'SH-020-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1713,'2026-09-30 11:25:27.764470','2026-09-30 11:25:27.764470',1,'SH-021-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1714,'2026-09-30 11:25:27.765034','2026-09-30 11:25:27.765034',2,'SH-021-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1715,'2026-09-30 11:25:27.765584','2026-09-30 11:25:27.765584',2,'SH-021-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1716,'2026-09-30 11:25:27.766109','2026-09-30 11:25:27.766109',2,'SH-021-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,6,NULL),(1717,'2026-09-30 11:25:27.766109','2026-09-30 11:25:27.766109',2,'SH-021-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',NULL,6,NULL),(1718,'2026-09-30 11:25:27.776115','2026-09-30 11:25:27.776115',1,'SH-022-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1719,'2026-09-30 11:25:27.776115','2026-09-30 11:25:27.776115',2,'SH-022-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1720,'2026-09-30 11:25:27.776688','2026-09-30 11:25:27.776688',2,'SH-022-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,6,NULL),(1721,'2026-09-30 11:25:27.776688','2026-09-30 11:25:27.776688',2,'SH-022-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,6,NULL),(1722,'2026-09-30 11:25:27.777247','2026-09-30 11:25:27.777247',2,'SH-022-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,6,NULL),(1723,'2026-09-30 11:25:27.777247','2026-09-30 11:25:27.777247',2,'SH-022-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,6,NULL),(1724,'2026-09-30 11:25:27.787519','2026-09-30 11:25:27.787519',1,'SH-023-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1725,'2026-09-30 11:25:27.788101','2026-09-30 11:25:27.788101',2,'SH-023-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1726,'2026-09-30 11:25:27.788101','2026-09-30 11:25:27.788101',3,'SH-023-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,6,NULL),(1727,'2026-09-30 11:25:27.789108','2026-09-30 11:25:27.789108',3,'SH-023-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,6,NULL),(1728,'2026-09-30 11:25:27.789108','2026-09-30 11:25:27.789108',3,'SH-023-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,6,NULL),(1729,'2026-09-30 11:25:27.789614','2026-09-30 11:25:27.789614',3,'SH-023-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,6,NULL),(1730,'2026-09-30 11:25:27.798624','2026-09-30 11:25:27.798624',1,'SH-024-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1731,'2026-09-30 11:25:27.799194','2026-09-30 11:25:27.799194',2,'SH-024-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1732,'2026-09-30 11:25:27.799705','2026-09-30 11:25:27.799705',2,'SH-024-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1733,'2026-09-30 11:25:27.808040','2026-09-30 11:25:27.808040',1,'SH-025-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1734,'2026-09-30 11:25:27.819141','2026-09-30 11:25:27.819141',1,'SH-026-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1735,'2026-09-30 11:25:27.820654','2026-09-30 11:25:27.820654',2,'SH-026-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1736,'2026-09-30 11:25:27.821182','2026-09-30 11:25:27.821182',3,'SH-026-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,6,NULL),(1737,'2026-09-30 11:25:27.821695','2026-09-30 11:25:27.821695',3,'SH-026-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,6,NULL),(1738,'2026-09-30 11:25:27.822754','2026-09-30 11:25:27.822754',3,'SH-026-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,6,NULL),(1739,'2026-09-30 11:25:27.823265','2026-09-30 11:25:27.823265',3,'SH-026-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',NULL,6,NULL),(1740,'2026-09-30 11:25:27.834162','2026-09-30 11:25:27.834162',1,'SH-027-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1741,'2026-09-30 11:25:27.835169','2026-09-30 11:25:27.835169',2,'SH-027-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1742,'2026-09-30 11:25:27.835169','2026-09-30 11:25:27.835169',2,'SH-027-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1743,'2026-09-30 11:25:27.843765','2026-09-30 11:25:27.843765',1,'SH-028-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1744,'2026-09-30 11:25:27.843765','2026-09-30 11:25:27.843765',2,'SH-028-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1745,'2026-09-30 11:25:27.843765','2026-09-30 11:25:27.843765',2,'SH-028-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1746,'2026-09-30 11:25:27.853468','2026-09-30 11:25:27.853468',1,'SH-029-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1747,'2026-09-30 11:25:27.853468','2026-09-30 11:25:27.853468',2,'SH-029-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,6,NULL),(1748,'2026-09-30 11:25:27.854477','2026-09-30 11:25:27.854477',3,'SH-029-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',NULL,6,NULL),(1749,'2026-09-30 11:25:27.863001','2026-09-30 11:25:27.863001',1,'SH-030-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,6,NULL),(1781,NULL,'2026-10-02 09:44:42.305947',1,'LAGOON-05-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,'2026-10-02 09:44:42.305947'),(1782,NULL,'2026-10-02 09:44:42.310940',1,'LAGOON-05-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,'2026-10-02 09:44:42.310940'),(1783,NULL,'2026-10-02 09:44:57.612820',3,'CLIFF-12-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',NULL,4,'2026-10-02 09:44:57.612820'),(1784,NULL,'2026-10-02 09:44:57.623832',3,'CLIFF-12-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',NULL,4,'2026-10-02 09:44:57.623832'),(1785,NULL,'2026-10-02 09:44:57.633839',1,'CLIFF-12-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',NULL,4,'2026-10-02 09:44:57.632876'),(1786,NULL,'2026-10-02 09:44:57.641825',2,'CLIFF-12-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',NULL,4,'2026-10-02 09:44:57.641825'),(1787,NULL,'2026-10-02 09:44:57.649841',3,'CLIFF-12-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',NULL,4,'2026-10-02 09:44:57.649841'),(1789,NULL,'2026-10-04 04:06:21.125551',1,'NT-004-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',433,4,'2026-10-04 04:06:21.125551'),(1790,NULL,'2026-10-04 04:06:21.136899',2,'NT-004-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',433,4,'2026-10-04 04:06:21.136899'),(1791,NULL,'2026-10-04 04:06:21.147426',2,'NT-004-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',433,4,'2026-10-04 04:06:21.147426'),(1792,NULL,'2026-10-04 04:06:29.245535',1,'SH-001-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',490,6,'2026-10-04 04:06:29.245535'),(1796,NULL,'2026-10-04 04:06:43.755461',1,'SH-004-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',493,6,'2026-10-04 04:06:43.755461'),(1800,NULL,'2026-10-04 04:07:16.328384',1,'NT-001-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',430,4,'2026-10-04 04:07:16.328384'),(1801,NULL,'2026-10-04 04:07:25.547507',2,'NT-003-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',432,4,'2026-10-04 04:07:25.547507'),(1802,NULL,'2026-10-05 16:59:43.625009',1,'NT-003-P1','AVAILABLE',3,NULL,'2026-10-05 16:59:43.608351','CLEANED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',432,4,'2026-10-04 04:07:25.551771'),(1803,NULL,'2026-10-04 04:07:25.556133',2,'NT-003-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',432,4,'2026-10-04 04:07:25.556133'),(1804,NULL,'2026-10-04 04:07:44.543728',2,'SB-002-P5','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (King Size Bed)',461,5,'2026-10-04 04:07:44.543728'),(1805,NULL,'2026-10-04 04:07:44.549731',2,'SB-002-P4','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',461,5,'2026-10-04 04:07:44.549731'),(1806,NULL,'2026-10-04 04:07:44.553285',2,'SB-002-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',461,5,'2026-10-04 04:07:44.552280'),(1807,NULL,'2026-10-04 04:07:44.556871',2,'SB-002-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',461,5,'2026-10-04 04:07:44.556871'),(1808,NULL,'2026-10-04 04:07:44.560854',1,'SB-002-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',461,5,'2026-10-04 04:07:44.560854'),(1809,NULL,'2026-10-04 04:07:49.328565',1,'SB-003-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',462,5,'2026-10-04 04:07:49.328565'),(1810,NULL,'2026-10-04 04:07:49.333067',2,'SB-003-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',462,5,'2026-10-04 04:07:49.333067'),(1811,NULL,'2026-10-04 04:07:49.335582',2,'SB-003-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',462,5,'2026-10-04 04:07:49.335582'),(1812,NULL,'2026-10-04 04:07:52.455635',1,'SB-004-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',463,5,'2026-10-04 04:07:52.454620'),(1813,NULL,'2026-10-05 16:59:42.563149',2,'SH-002-P2','AVAILABLE',3,NULL,'2026-10-05 16:59:42.545636','CLEANED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',491,6,'2026-10-04 04:08:05.544827'),(1814,NULL,'2026-10-04 04:08:05.550896',1,'SH-002-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',491,6,'2026-10-04 04:08:05.550896'),(1815,NULL,'2026-10-04 04:08:05.557216',3,'SH-002-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',491,6,'2026-10-04 04:08:05.557216'),(1816,NULL,'2026-10-04 04:08:27.579068',1,'SH-005-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',494,6,'2026-10-04 04:08:27.579068'),(1817,NULL,'2026-10-04 04:08:27.582080',3,'SH-005-P3','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (Queen Size Bed)',494,6,'2026-10-04 04:08:27.582080'),(1818,NULL,'2026-10-04 04:08:27.584078',2,'SH-005-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',494,6,'2026-10-04 04:08:27.584078'),(1819,NULL,'2026-10-04 04:33:55.643403',1,'SB-001-P3','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 3 (King Size Bed)',460,5,'2026-10-04 04:33:55.643403'),(1820,NULL,'2026-10-04 04:33:55.665468',1,'SB-001-P6','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 6 (King Size Bed)',460,5,'2026-10-04 04:33:55.665468'),(1821,NULL,'2026-10-04 04:33:55.675014',1,'SB-001-P5','AVAILABLE',2,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 5 (Queen Size Bed)',460,5,'2026-10-04 04:33:55.675014'),(1822,NULL,'2026-10-04 04:33:55.707215',1,'SB-001-P2','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 2 (King Size Bed)',460,5,'2026-10-04 04:33:55.707215'),(1823,NULL,'2026-10-04 04:33:55.718735',1,'SB-001-P1','AVAILABLE',3,NULL,NULL,'EXPIRED',NULL,NULL,'Phòng Ngủ 1 (King Size Bed)',460,5,'2026-10-04 04:33:55.718735'),(1824,NULL,'2026-10-05 16:49:30.963905',1,'SB-001-P4','AVAILABLE',2,NULL,'2026-10-05 16:49:30.919734','CLEANED',NULL,NULL,'Phòng Ngủ 4 (Queen Size Bed)',460,5,'2026-10-04 04:33:55.728683');
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_dispatches`
--

DROP TABLE IF EXISTS `service_dispatches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_dispatches` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `asset_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cost` decimal(12,2) DEFAULT NULL,
  `destination` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `flight_number` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guest_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `pickup_location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `room_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `scheduled_time` datetime(6) NOT NULL,
  `service_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `assigned_staff_id` bigint DEFAULT NULL,
  `booking_id` bigint DEFAULT NULL,
  `booking_extra_service_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr50ojjv337yxyddv8psok28rk` (`assigned_staff_id`),
  KEY `FKn2pvenw47enbtd5o0a2y57j37` (`booking_id`),
  KEY `FK92bwavvpq5gs0ylrennbjwwaa` (`booking_extra_service_id`),
  CONSTRAINT `FK92bwavvpq5gs0ylrennbjwwaa` FOREIGN KEY (`booking_extra_service_id`) REFERENCES `booking_extra_services` (`id`),
  CONSTRAINT `FKn2pvenw47enbtd5o0a2y57j37` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`),
  CONSTRAINT `FKr50ojjv337yxyddv8psok28rk` FOREIGN KEY (`assigned_staff_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_dispatches`
--

LOCK TABLES `service_dispatches` WRITE;
/*!40000 ALTER TABLE `service_dispatches` DISABLE KEYS */;
INSERT INTO `service_dispatches` VALUES (1,NULL,'2026-10-04 10:38:13.000000','Maybach S680 (#01)',2500000.00,'Sân bay Phú Quốc (PQC)',NULL,'Nguyễn Hoàng Nam','Đón tiễn VIP Sanctuary kèm hoa tươi và sâm-panh','Villa NT-103','NT-103','2026-10-04 04:30:00.000000','MAYBACH_AIRPORT','IN_TRANSIT',26,29,NULL,'2026-10-04 10:38:13.000000'),(2,NULL,'2026-10-04 10:38:13.000000','Du thuyền Aura Pearl (#02)',15000000.00,'Bến du thuyền An Thới',NULL,'Đặng Quốc Huy','Hải trình hoàng hôn phục vụ canapé & Dom Pérignon','Villa SB-201','SB-201','2026-10-04 09:30:00.000000','YACHT_SUNSET','DISPATCHED',26,28,NULL,'2026-10-04 10:38:13.000000'),(3,NULL,'2026-10-04 10:38:13.000000','Buggy VIP Luxury (#05)',500000.00,'Nhà hàng Biển Corallo',NULL,'Lê Thị Mai Hương','Đưa đón ăn sáng buffet 5 sao bãi biển','Villa SH-302','SH-302','2026-10-04 02:00:00.000000','BUGGY_VIP','COMPLETED',1,33,NULL,'2026-10-04 10:38:13.000000'),(4,NULL,'2026-10-04 10:38:13.000000','Trực Thăng Bell 505 (#VIP)',35000000.00,'Bãi đáp Helipad Phú Quốc',NULL,'Bùi Phương Thảo','Chuyến bay ngắm toàn cảnh quần đảo An Thới','Villa SB-VIP','SB-VIP','2026-10-05 07:00:00.000000','HELICOPTER_TRANSFER','SCHEDULED',26,32,NULL,'2026-10-04 10:38:13.000000');
/*!40000 ALTER TABLE `service_dispatches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_recovery_tickets`
--

DROP TABLE IF EXISTS `service_recovery_tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_recovery_tickets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `actual_resolution_minutes` int DEFAULT NULL,
  `guest_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `incident_category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `issue_summary` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `resolution_action` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resolved_at` datetime(6) DEFAULT NULL,
  `room_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sla_minutes` int DEFAULT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `assigned_manager_id` bigint DEFAULT NULL,
  `review_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6aehb1guaoe73bibot117q92j` (`assigned_manager_id`),
  KEY `FK40sre1vxdd6fc0x72sboc3oa4` (`review_id`),
  CONSTRAINT `FK40sre1vxdd6fc0x72sboc3oa4` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`),
  CONSTRAINT `FK6aehb1guaoe73bibot117q92j` FOREIGN KEY (`assigned_manager_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_recovery_tickets`
--

LOCK TABLES `service_recovery_tickets` WRITE;
/*!40000 ALTER TABLE `service_recovery_tickets` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_recovery_tickets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shift_swap_requests`
--

DROP TABLE IF EXISTS `shift_swap_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shift_swap_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `action_at` datetime(6) DEFAULT NULL,
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `request_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_date` date NOT NULL,
  `approver_id` bigint DEFAULT NULL,
  `current_shift_id` bigint DEFAULT NULL,
  `desired_shift_id` bigint DEFAULT NULL,
  `requester_id` bigint NOT NULL,
  `target_staff_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq8oue5y8p1sm9bi6n67gj8j2l` (`approver_id`),
  KEY `FK4qmpwk1v8moy47haisemp8gv9` (`current_shift_id`),
  KEY `FKnx1t5rxjmvlub846nkljonpkh` (`desired_shift_id`),
  KEY `FKkht5cfay0h1chhec6m8k6t5ct` (`requester_id`),
  KEY `FKha8c2a3i1gjbin4aoi4as8r9y` (`target_staff_id`),
  CONSTRAINT `FK4qmpwk1v8moy47haisemp8gv9` FOREIGN KEY (`current_shift_id`) REFERENCES `shifts` (`id`),
  CONSTRAINT `FKha8c2a3i1gjbin4aoi4as8r9y` FOREIGN KEY (`target_staff_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKkht5cfay0h1chhec6m8k6t5ct` FOREIGN KEY (`requester_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKnx1t5rxjmvlub846nkljonpkh` FOREIGN KEY (`desired_shift_id`) REFERENCES `shifts` (`id`),
  CONSTRAINT `FKq8oue5y8p1sm9bi6n67gj8j2l` FOREIGN KEY (`approver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shift_swap_requests`
--

LOCK TABLES `shift_swap_requests` WRITE;
/*!40000 ALTER TABLE `shift_swap_requests` DISABLE KEYS */;
INSERT INTO `shift_swap_requests` VALUES (1,NULL,'2026-10-04 05:19:55.079211','2026-10-04 05:19:55.073568','Tôi xin đổi ca chiều sang ca sáng để xử lý việc gia đình đột xuất, đã thỏa thuận trước với bạn Nam.','SWAP','APPROVED','2026-10-05',26,NULL,NULL,28,27,'2026-10-04 12:17:12.000000'),(2,NULL,'2026-10-04 05:25:41.473642','2026-10-04 05:25:41.471914','Đăng ký làm thêm giờ (OT +4h) ca tối phục vụ đoàn khách VIP nhận 3 căn Beachfront Villa.','OVERTIME','APPROVED','2026-10-04',1,NULL,NULL,29,NULL,'2026-10-04 12:17:12.000000'),(3,NULL,'2026-10-04 12:17:12.000000','2026-10-04 12:17:12.000000','Đổi ca trực tiếp đón đoàn khách VIP check-in sáng sớm.','SWAP','APPROVED','2026-10-03',26,NULL,NULL,32,27,'2026-10-04 12:17:12.000000');
/*!40000 ALTER TABLE `shift_swap_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shifts`
--

DROP TABLE IF EXISTS `shifts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shifts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `color_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `department` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `end_time` time NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_time` time NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shifts`
--

LOCK TABLES `shifts` WRITE;
/*!40000 ALTER TABLE `shifts` DISABLE KEYS */;
INSERT INTO `shifts` VALUES (1,NULL,'2026-10-04 12:17:12.000000','#10b981','ALL','14:30:00','Ca Sáng','06:00:00','2026-10-04 12:17:12.000000'),(2,NULL,'2026-10-04 12:17:12.000000','#0284c7','ALL','22:30:00','Ca Chiều','14:00:00','2026-10-04 12:17:12.000000'),(3,NULL,'2026-10-04 12:17:12.000000','#6366f1','ALL','06:30:00','Ca Đêm','22:00:00','2026-10-04 12:17:12.000000'),(4,NULL,'2026-10-04 12:17:12.000000','#f59e0b','BUTLER','23:59:59','On-Call VIP','00:00:00','2026-10-04 12:17:12.000000');
/*!40000 ALTER TABLE `shifts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff_schedules`
--

DROP TABLE IF EXISTS `staff_schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff_schedules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_off` bit(1) NOT NULL,
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `work_date` date NOT NULL,
  `shift_id` bigint NOT NULL,
  `staff_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKacvp4a4kpk04lnbljhsfl80fq` (`shift_id`),
  KEY `FKk30u6rwff272bq7rh8d2yc3ud` (`staff_id`),
  CONSTRAINT `FKacvp4a4kpk04lnbljhsfl80fq` FOREIGN KEY (`shift_id`) REFERENCES `shifts` (`id`),
  CONSTRAINT `FKk30u6rwff272bq7rh8d2yc3ud` FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff_schedules`
--

LOCK TABLES `staff_schedules` WRITE;
/*!40000 ALTER TABLE `staff_schedules` DISABLE KEYS */;
INSERT INTO `staff_schedules` VALUES (1,NULL,'2026-10-06 00:50:21.490681',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-05',1,1,'2026-10-06 00:50:21.490681'),(2,NULL,'2026-10-06 00:50:21.571431',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-06',2,1,'2026-10-06 00:50:21.571431'),(3,NULL,'2026-10-06 00:50:21.575341',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-07',3,1,'2026-10-06 00:50:21.575341'),(4,NULL,'2026-10-06 00:50:21.578845',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-08',1,1,'2026-10-06 00:50:21.578845'),(5,NULL,'2026-10-06 00:50:21.581923',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-09',1,1,'2026-10-06 00:50:21.581923'),(6,NULL,'2026-10-06 00:50:21.584558',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-10',2,1,'2026-10-06 00:50:21.584558'),(7,NULL,'2026-10-06 00:50:21.587849',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-11',3,1,'2026-10-06 00:50:21.587304'),(8,NULL,'2026-10-06 00:50:21.590114',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-05',2,26,'2026-10-06 00:50:21.590114'),(9,NULL,'2026-10-06 00:50:21.592414',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-06',3,26,'2026-10-06 00:50:21.592414'),(10,NULL,'2026-10-06 00:50:21.595428',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-07',1,26,'2026-10-06 00:50:21.595428'),(11,NULL,'2026-10-06 00:50:21.597428',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-08',1,26,'2026-10-06 00:50:21.597428'),(12,NULL,'2026-10-06 00:50:21.601249',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-09',2,26,'2026-10-06 00:50:21.601249'),(13,NULL,'2026-10-06 00:50:21.605425',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-10',3,26,'2026-10-06 00:50:21.605425'),(14,NULL,'2026-10-06 00:50:21.611146',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-11',1,26,'2026-10-06 00:50:21.611146'),(15,NULL,'2026-10-06 00:50:21.615071',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-05',3,27,'2026-10-06 00:50:21.615071'),(16,NULL,'2026-10-06 00:50:21.618942',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-06',1,27,'2026-10-06 00:50:21.618942'),(17,NULL,'2026-10-06 00:50:21.623770',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-07',1,27,'2026-10-06 00:50:21.623770'),(18,NULL,'2026-10-06 00:50:21.625770',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-08',2,27,'2026-10-06 00:50:21.625770'),(19,NULL,'2026-10-06 00:50:21.628282',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-09',3,27,'2026-10-06 00:50:21.628282'),(20,NULL,'2026-10-06 00:50:21.631317',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-10',1,27,'2026-10-06 00:50:21.631317'),(21,NULL,'2026-10-06 00:50:21.633805',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-11',1,27,'2026-10-06 00:50:21.633805'),(22,NULL,'2026-10-06 00:50:21.635993',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-05',1,28,'2026-10-06 00:50:21.635993'),(23,NULL,'2026-10-06 00:50:21.638959',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-06',1,28,'2026-10-06 00:50:21.638959'),(24,NULL,'2026-10-06 00:50:21.640978',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-07',2,28,'2026-10-06 00:50:21.640978'),(25,NULL,'2026-10-06 00:50:21.644148',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-08',3,28,'2026-10-06 00:50:21.644148'),(26,NULL,'2026-10-06 00:50:21.646152',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-09',1,28,'2026-10-06 00:50:21.646152'),(27,NULL,'2026-10-06 00:50:21.648145',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-10',1,28,'2026-10-06 00:50:21.648145'),(28,NULL,'2026-10-06 00:50:21.651286',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-11',2,28,'2026-10-06 00:50:21.651286'),(29,NULL,'2026-10-06 00:50:21.654480',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-05',1,29,'2026-10-06 00:50:21.654480'),(30,NULL,'2026-10-06 00:50:21.657886',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-06',2,29,'2026-10-06 00:50:21.657886'),(31,NULL,'2026-10-06 00:50:21.660516',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-07',3,29,'2026-10-06 00:50:21.660516'),(32,NULL,'2026-10-06 00:50:21.662052',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-08',1,29,'2026-10-06 00:50:21.662052'),(33,NULL,'2026-10-06 00:50:21.667416',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-09',1,29,'2026-10-06 00:50:21.667416'),(34,NULL,'2026-10-06 00:50:21.669787',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-10',2,29,'2026-10-06 00:50:21.669787'),(35,NULL,'2026-10-06 00:50:21.671809',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-11',3,29,'2026-10-06 00:50:21.671809'),(36,NULL,'2026-10-06 00:50:21.673915',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-05',2,30,'2026-10-06 00:50:21.673915'),(37,NULL,'2026-10-06 00:50:21.678424',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-06',3,30,'2026-10-06 00:50:21.678424'),(38,NULL,'2026-10-06 00:50:21.680444',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-07',1,30,'2026-10-06 00:50:21.680444'),(39,NULL,'2026-10-06 00:50:21.682971',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-08',1,30,'2026-10-06 00:50:21.682971'),(40,NULL,'2026-10-06 00:50:21.686274',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-09',2,30,'2026-10-06 00:50:21.686274'),(41,NULL,'2026-10-06 00:50:21.687274',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-10',3,30,'2026-10-06 00:50:21.687274'),(42,NULL,'2026-10-06 00:50:21.689271',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-11',1,30,'2026-10-06 00:50:21.689271'),(43,NULL,'2026-10-06 00:50:21.691274',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-05',3,32,'2026-10-06 00:50:21.691274'),(44,NULL,'2026-10-06 00:50:21.693855',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-06',1,32,'2026-10-06 00:50:21.693855'),(45,NULL,'2026-10-06 00:50:21.695879',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-07',1,32,'2026-10-06 00:50:21.695879'),(46,NULL,'2026-10-06 00:50:21.696872',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-08',2,32,'2026-10-06 00:50:21.696872'),(47,NULL,'2026-10-06 00:50:21.700237',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-09',3,32,'2026-10-06 00:50:21.700237'),(48,NULL,'2026-10-06 00:50:21.701346',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-10',1,32,'2026-10-06 00:50:21.701346'),(49,NULL,'2026-10-06 00:50:21.703891',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-11',1,32,'2026-10-06 00:50:21.703891'),(50,NULL,'2026-10-06 00:50:21.706279',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-12',3,1,'2026-10-06 00:50:21.706279'),(51,NULL,'2026-10-06 00:50:21.708278',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-13',1,1,'2026-10-06 00:50:21.708278'),(52,NULL,'2026-10-06 00:50:21.709795',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-14',1,1,'2026-10-06 00:50:21.709795'),(53,NULL,'2026-10-06 00:50:21.710795',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-15',2,1,'2026-10-06 00:50:21.710795'),(54,NULL,'2026-10-06 00:50:21.712806',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-16',3,1,'2026-10-06 00:50:21.712806'),(55,NULL,'2026-10-06 00:50:21.715097',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-17',1,1,'2026-10-06 00:50:21.715097'),(56,NULL,'2026-10-06 00:50:21.715968',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-18',1,1,'2026-10-06 00:50:21.715968'),(57,NULL,'2026-10-06 00:50:21.719319',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-12',1,26,'2026-10-06 00:50:21.719319'),(58,NULL,'2026-10-06 00:50:21.720568',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-13',1,26,'2026-10-06 00:50:21.720568'),(59,NULL,'2026-10-06 00:50:21.722578',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-14',2,26,'2026-10-06 00:50:21.722578'),(60,NULL,'2026-10-06 00:50:21.724087',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-15',3,26,'2026-10-06 00:50:21.724087'),(61,NULL,'2026-10-06 00:50:21.725161',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-16',1,26,'2026-10-06 00:50:21.725161'),(62,NULL,'2026-10-06 00:50:21.727169',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-17',1,26,'2026-10-06 00:50:21.727169'),(63,NULL,'2026-10-06 00:50:21.729168',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-18',2,26,'2026-10-06 00:50:21.729168'),(64,NULL,'2026-10-06 00:50:21.730163',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-12',1,27,'2026-10-06 00:50:21.730163'),(65,NULL,'2026-10-06 00:50:21.732107',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-13',2,27,'2026-10-06 00:50:21.732107'),(66,NULL,'2026-10-06 00:50:21.734518',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-14',3,27,'2026-10-06 00:50:21.734518'),(67,NULL,'2026-10-06 00:50:21.736642',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-15',1,27,'2026-10-06 00:50:21.736642'),(68,NULL,'2026-10-06 00:50:21.737793',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-16',1,27,'2026-10-06 00:50:21.737793'),(69,NULL,'2026-10-06 00:50:21.740034',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-17',2,27,'2026-10-06 00:50:21.740034'),(70,NULL,'2026-10-06 00:50:21.742048',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-18',3,27,'2026-10-06 00:50:21.742048'),(71,NULL,'2026-10-06 00:50:21.743055',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-12',2,28,'2026-10-06 00:50:21.743055'),(72,NULL,'2026-10-06 00:50:21.745772',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-13',3,28,'2026-10-06 00:50:21.745772'),(73,NULL,'2026-10-06 00:50:21.747439',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-14',1,28,'2026-10-06 00:50:21.747439'),(74,NULL,'2026-10-06 00:50:21.748450',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-15',1,28,'2026-10-06 00:50:21.748450'),(75,NULL,'2026-10-06 00:50:21.750458',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-16',2,28,'2026-10-06 00:50:21.750458'),(76,NULL,'2026-10-06 00:50:21.752451',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-17',3,28,'2026-10-06 00:50:21.752451'),(77,NULL,'2026-10-06 00:50:21.753452',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-18',1,28,'2026-10-06 00:50:21.753452'),(78,NULL,'2026-10-06 00:50:21.754972',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-12',3,29,'2026-10-06 00:50:21.754972'),(79,NULL,'2026-10-06 00:50:21.756992',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-13',1,29,'2026-10-06 00:50:21.756992'),(80,NULL,'2026-10-06 00:50:21.758996',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-14',1,29,'2026-10-06 00:50:21.758996'),(81,NULL,'2026-10-06 00:50:21.761385',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-15',2,29,'2026-10-06 00:50:21.761385'),(82,NULL,'2026-10-06 00:50:21.763309',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-16',3,29,'2026-10-06 00:50:21.763309'),(83,NULL,'2026-10-06 00:50:21.765358',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-17',1,29,'2026-10-06 00:50:21.765358'),(84,NULL,'2026-10-06 00:50:21.767373',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-18',1,29,'2026-10-06 00:50:21.767373'),(85,NULL,'2026-10-06 00:50:21.768373',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-12',1,30,'2026-10-06 00:50:21.768373'),(86,NULL,'2026-10-06 00:50:21.770760',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-13',1,30,'2026-10-06 00:50:21.770760'),(87,NULL,'2026-10-06 00:50:21.772759',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-14',2,30,'2026-10-06 00:50:21.772759'),(88,NULL,'2026-10-06 00:50:21.775766',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-15',3,30,'2026-10-06 00:50:21.775766'),(89,NULL,'2026-10-06 00:50:21.778601',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-16',1,30,'2026-10-06 00:50:21.778601'),(90,NULL,'2026-10-06 00:50:21.779614',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-17',1,30,'2026-10-06 00:50:21.779614'),(91,NULL,'2026-10-06 00:50:21.781694',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-18',2,30,'2026-10-06 00:50:21.781694'),(92,NULL,'2026-10-06 00:50:21.783913',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-12',1,32,'2026-10-06 00:50:21.783913'),(93,NULL,'2026-10-06 00:50:21.784895',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-13',2,32,'2026-10-06 00:50:21.784895'),(94,NULL,'2026-10-06 00:50:21.786443',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-14',3,32,'2026-10-06 00:50:21.786443'),(95,NULL,'2026-10-06 00:50:21.787501',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-15',1,32,'2026-10-06 00:50:21.787501'),(96,NULL,'2026-10-06 00:50:21.790464',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-16',1,32,'2026-10-06 00:50:21.790464'),(97,NULL,'2026-10-06 00:50:21.792503',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-17',2,32,'2026-10-06 00:50:21.792503'),(98,NULL,'2026-10-06 00:50:21.794318',_binary '\0','Lịch phân bổ tiêu chuẩn resort','SCHEDULED','2026-10-18',3,32,'2026-10-06 00:50:21.794318');
/*!40000 ALTER TABLE `staff_schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('ROLE_ACCOUNTANT','ROLE_ADMIN','ROLE_BUTLER','ROLE_CUSTOMER','ROLE_HOUSEKEEPING','ROLE_RECEPTIONIST','ROLE_STAFF') COLLATE utf8mb4_unicode_ci NOT NULL,
  `provider` enum('GOOGLE','LOCAL') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `avatar` longtext COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-09-19 03:33:23.336317','2026-09-19 03:57:02.123720','huylan205@gmail.com','Long Phùng',_binary '','$2a$10$yShdphOloP/GGNFxB49FVeMDbSgtUSj3rmkET7It3sTzk/MPKJpqa',NULL,'ROLE_ADMIN','LOCAL',NULL,NULL),(26,'2026-09-20 16:33:58.378068','2026-09-20 16:33:58.378068','admin@auraholdings.vn','Phạm Văn Minh (Tổng Quản Lý)',_binary '','$2a$10$dwvHUOZb/9qXymHJ6vFlhecMdqbJL/ibUOH/ivZnmLPxJiKhFa6ve','+84 908 112 334','ROLE_ADMIN','LOCAL',NULL,NULL),(27,'2026-09-20 16:33:58.435410','2026-09-20 16:33:58.435410','nam.reception@auraholdings.vn','Nguyễn Hoàng Nam',_binary '','$2a$10$6RmPWl6fe/Lt0E9mkjIqKu5Dg79BuRASmOO3vJRaqceA8R8EF0Bma','+84 912 345 678','ROLE_RECEPTIONIST','LOCAL',NULL,NULL),(28,'2026-09-20 16:33:58.439603','2026-09-20 16:33:58.439603','hoang.butler@auraholdings.vn','Trần Văn Hoàng',_binary '','$2a$10$CJmJFdKk66006OlZHgwgtOxQ0gWI0h.RulR8pPJsO9U1P0onjyZXi','+84 909 888 123','ROLE_BUTLER','LOCAL',NULL,NULL),(29,'2026-09-20 16:33:58.445700','2026-09-30 02:43:23.388508','hoa.housekeeping@auraholdings.vn','Nguyễn Thị Hoa',_binary '','$2a$10$P/oqUO.mdwdjou/gUAQKQeoxV0I7ITLybYJUvZvyfDwyF2S9Zs27G','+84 933 456 789','ROLE_HOUSEKEEPING','LOCAL',NULL,NULL),(30,'2026-09-20 16:33:58.449419','2026-09-20 16:33:58.449419','thao.accountant@auraholdings.vn','Trần Thị Thu Thảo',_binary '','$2a$10$1AieM2nXVUmmfTd7axmsgOlIWdTyIStc3SC0PxuP7W9syPogr30tS','+84 988 777 666','ROLE_ACCOUNTANT','LOCAL',NULL,NULL),(31,'2026-09-20 16:33:58.453150','2026-09-20 16:33:58.453150','giahuy.tran@auraholdings.vn','Trần Gia Huy (Diamond VIP)',_binary '','$2a$10$IsqvppTJcv2aQH1ORVzimeXZXiDX4qBOyGgOims5MhEXy7kNTKE.q','+84 918 223 999','ROLE_CUSTOMER','LOCAL',NULL,NULL),(32,'2026-09-21 01:03:55.531920','2026-09-21 11:51:10.858402','test.receptionist@sanctuary.vn','Lê Thị Mai Lễ Tân',_binary '','$2a$10$15da/BJfvsOX1h36cuqYCOTAkSHkk8yFgXlkcvWwI4SoojvNLWb8a','0919888999','ROLE_RECEPTIONIST','LOCAL',NULL,NULL),(39,'2026-09-30 19:09:28.000000','2026-09-30 19:09:28.000000','test.customer@aura.vn','Test Customer',_binary '','\\.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',NULL,'ROLE_CUSTOMER','GOOGLE',NULL,NULL),(44,NULL,'2026-10-03 07:22:54.651341','dcg14067@laoia.com','hello',_binary '','$2a$10$RUUVo7Yeb3.HrBx5j06i6uCmUAvMOVDujCuAwB7pUDb17I9Z.ZUCy',NULL,'ROLE_CUSTOMER','LOCAL','2026-10-03 07:22:54.651341',NULL),(45,NULL,'2026-10-05 16:29:46.030349','2311061241@hunre.edu.vn','LONG PHUNG VAN',_binary '','$2a$10$9NZsqSATdHQpJqVGlgZjTuAvnBACavIeg/U6dooucPRSd3dMUDoA.',NULL,'ROLE_CUSTOMER','GOOGLE','2026-10-05 16:24:56.785820',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `villa_assets`
--

DROP TABLE IF EXISTS `villa_assets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `villa_assets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `asset_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `install_date` date DEFAULT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  `serial_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('BROKEN','GOOD','MAINTENANCE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `villa_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `warranty_expiry` date DEFAULT NULL,
  `villa_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_asset_villa` (`villa_id`),
  KEY `idx_asset_status` (`status`),
  CONSTRAINT `FKh3mj5imjfxqlxo96h9sybdehe` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `villa_assets`
--

LOCK TABLES `villa_assets` WRITE;
/*!40000 ALTER TABLE `villa_assets` DISABLE KEYS */;
INSERT INTO `villa_assets` VALUES (1,'2026-09-27 16:47:46.322233','2026-09-27 16:47:46.322233','Smart TV Sony Bravia 65 inch 4K','Điện tử','2026-03-27','Lắp tại phòng khách chính','SN-SONY-65-8891','GOOD','Villa #101','2028-03-27',NULL,NULL),(2,'2026-09-27 16:47:46.333842','2026-09-27 16:47:46.333842','Máy phát tạo ion Ozon khử khuẩn phòng','Vệ sinh','2026-06-27','Máy khử trùng Ozon tiêu chuẩn phòng sạch','SN-OZONE-PRO-2026','GOOD','Villa #101','2027-06-27',NULL,NULL),(3,'2026-09-27 16:47:46.348742','2026-09-27 16:47:46.348742','Tủ lạnh minibar inverter Bosch 90L','Gia dụng','2026-01-27','Bảo hành chính hãng Bosch','SN-BOSCH-90L-4412','GOOD','Villa #101','2028-01-27',NULL,NULL);
/*!40000 ALTER TABLE `villa_assets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `villa_images`
--

DROP TABLE IF EXISTS `villa_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `villa_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `image_url` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_primary` bit(1) DEFAULT NULL,
  `villa_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmqpqko9onbpp9x2ubuymp3dyu` (`villa_id`),
  CONSTRAINT `FKmqpqko9onbpp9x2ubuymp3dyu` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1723 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `villa_images`
--

LOCK TABLES `villa_images` WRITE;
/*!40000 ALTER TABLE `villa_images` DISABLE KEYS */;
INSERT INTO `villa_images` VALUES (13,'2026-09-27 14:44:39.474774','2026-09-27 14:44:39.474774','/assets/images/uploads/villa_61f6d707.jpg',_binary '',38,NULL),(16,'2026-09-30 09:26:05.720969','2026-09-30 09:26:05.720969','/assets/images/uploads/villa_c29f5e3c.jpg',_binary '',29,NULL),(1417,'2026-09-30 11:25:26.701741','2026-09-30 11:25:26.701741','/assets/images/uploads/real_resort_hero_2.jpg',_binary '',431,NULL),(1418,'2026-09-30 11:25:26.702288','2026-09-30 11:25:26.702288','/assets/images/uploads/real_resort_gallery_3.jpg',_binary '\0',431,NULL),(1419,'2026-09-30 11:25:26.703333','2026-09-30 11:25:26.703333','/assets/images/uploads/real_resort_gallery_3.jpg',_binary '\0',431,NULL),(1516,'2026-09-30 11:25:27.108895','2026-09-30 11:25:27.108895','/assets/images/uploads/real_resort_hero_10.jpg',_binary '',464,NULL),(1517,'2026-09-30 11:25:27.109426','2026-09-30 11:25:27.109426','/assets/images/uploads/real_resort_gallery_3.jpg',_binary '\0',464,NULL),(1518,'2026-09-30 11:25:27.109426','2026-09-30 11:25:27.109426','/assets/images/uploads/real_resort_gallery_4.jpg',_binary '\0',464,NULL),(1600,'2026-09-30 11:25:27.493922','2026-09-30 11:25:27.493922','/assets/images/uploads/real_resort_hero_15.jpg',_binary '',492,NULL),(1601,'2026-09-30 11:25:27.494441','2026-09-30 11:25:27.494441','/assets/images/uploads/real_resort_gallery_3.jpg',_binary '\0',492,NULL),(1602,'2026-09-30 11:25:27.495014','2026-09-30 11:25:27.495014','/assets/images/uploads/real_resort_gallery_1.jpg',_binary '\0',492,NULL),(1696,NULL,'2026-10-04 04:06:21.020071','/assets/images/uploads/real_resort_hero_4.jpg',_binary '',433,'2026-10-04 04:06:21.020071'),(1697,NULL,'2026-10-04 04:06:21.116507','/assets/images/uploads/real_resort_gallery_4.jpg',_binary '\0',433,'2026-10-04 04:06:21.116507'),(1698,NULL,'2026-10-04 04:06:29.232787','/assets/images/uploads/real_resort_gallery_3.jpg',_binary '',490,'2026-10-04 04:06:29.232787'),(1699,NULL,'2026-10-04 04:06:29.240009','/assets/images/uploads/real_resort_hero_8.jpg',_binary '\0',490,'2026-10-04 04:06:29.240009'),(1702,NULL,'2026-10-04 04:06:43.742957','/assets/images/uploads/real_resort_gallery_3.jpg',_binary '',493,'2026-10-04 04:06:43.742957'),(1703,NULL,'2026-10-04 04:06:43.750052','/assets/images/uploads/real_resort_hero_14.jpg',_binary '\0',493,'2026-10-04 04:06:43.750052'),(1706,NULL,'2026-10-04 04:07:16.324278','/assets/images/uploads/real_resort_gallery_3.jpg',_binary '\0',430,'2026-10-04 04:07:16.324278'),(1707,NULL,'2026-10-04 04:07:16.327264','/assets/images/uploads/real_resort_hero_1.jpg',_binary '',430,'2026-10-04 04:07:16.327264'),(1708,NULL,'2026-10-04 04:07:25.544496','/assets/images/uploads/real_resort_hero_3.jpg',_binary '',432,'2026-10-04 04:07:25.544496'),(1709,NULL,'2026-10-04 04:07:44.535182','/assets/images/uploads/real_resort_gallery_4.jpg',_binary '',461,'2026-10-04 04:07:44.535182'),(1710,NULL,'2026-10-04 04:07:44.538170','/assets/images/uploads/real_resort_hero_5.jpg',_binary '\0',461,'2026-10-04 04:07:44.538170'),(1711,NULL,'2026-10-04 04:07:49.315033','/assets/images/uploads/real_resort_gallery_4.jpg',_binary '\0',462,'2026-10-04 04:07:49.315033'),(1712,NULL,'2026-10-04 04:07:49.322045','/assets/images/uploads/real_resort_hero_9.jpg',_binary '',462,'2026-10-04 04:07:49.322045'),(1713,NULL,'2026-10-04 04:07:52.438682','/assets/images/uploads/real_resort_gallery_4.jpg',_binary '\0',463,'2026-10-04 04:07:52.438682'),(1714,NULL,'2026-10-04 04:07:52.444821','/assets/images/uploads/real_resort_hero_5.jpg',_binary '\0',463,'2026-10-04 04:07:52.443825'),(1715,NULL,'2026-10-04 04:07:52.450366','/assets/images/uploads/real_resort_gallery_3.jpg',_binary '',463,'2026-10-04 04:07:52.450366'),(1716,NULL,'2026-10-04 04:08:05.530333','/assets/images/uploads/real_resort_gallery_1.jpg',_binary '',491,'2026-10-04 04:08:05.530333'),(1717,NULL,'2026-10-04 04:08:05.537936','/assets/images/uploads/real_resort_hero_15.jpg',_binary '\0',491,'2026-10-04 04:08:05.537936'),(1718,NULL,'2026-10-04 04:08:27.566007','/assets/images/uploads/villa_729ee8ca.jpg',_binary '',494,'2026-10-04 04:08:27.564998'),(1719,NULL,'2026-10-04 04:08:27.572551','/assets/images/uploads/real_resort_hero_2.jpg',_binary '\0',494,'2026-10-04 04:08:27.572551'),(1720,NULL,'2026-10-04 04:33:55.598230','/assets/images/uploads/real_resort_gallery_4.jpg',_binary '\0',460,'2026-10-04 04:33:55.598230'),(1721,NULL,'2026-10-04 04:33:55.626840','/assets/images/uploads/real_resort_hero_4.jpg',_binary '\0',460,'2026-10-04 04:33:55.626840'),(1722,NULL,'2026-10-04 04:33:55.635404','/assets/images/uploads/villa_96b2d8e5.jpg',_binary '',460,'2026-10-04 04:33:55.635404');
/*!40000 ALTER TABLE `villa_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `villa_inventories`
--

DROP TABLE IF EXISTS `villa_inventories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `villa_inventories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `current_quantity` int NOT NULL,
  `last_checked_at` datetime(6) DEFAULT NULL,
  `item_id` bigint NOT NULL,
  `villa_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKiod63meqjcqmp68ir9q7eb8u5` (`villa_id`,`item_id`),
  KEY `idx_villa_inv_villa` (`villa_id`),
  KEY `idx_villa_inv_item` (`item_id`),
  CONSTRAINT `FKetx90cy95ykh2pioj1a95b8qn` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`),
  CONSTRAINT `FKqga22ak7arkcd6sp6hng8k2tc` FOREIGN KEY (`item_id`) REFERENCES `inventory_items` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=509 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `villa_inventories`
--

LOCK TABLES `villa_inventories` WRITE;
/*!40000 ALTER TABLE `villa_inventories` DISABLE KEYS */;
INSERT INTO `villa_inventories` VALUES (1,'2026-09-27 17:02:30.998051','2026-09-27 17:08:44.016756',6,'2026-09-27 17:08:44.013269',1,29,NULL),(2,'2026-09-27 17:02:31.003524','2026-09-27 17:08:44.036971',3,'2026-09-27 17:08:44.032477',2,29,NULL),(3,'2026-09-27 17:02:31.007230','2026-09-27 17:08:44.045343',5,'2026-09-27 17:08:44.041976',3,29,NULL),(4,'2026-09-27 17:02:31.009244','2026-09-27 17:02:31.009244',2,'2026-09-27 17:02:30.926304',4,29,NULL),(5,'2026-09-27 17:02:31.011771','2026-09-27 17:02:31.011771',1,'2026-09-27 17:02:30.926304',5,29,NULL),(13,'2026-09-27 17:08:58.514931','2026-09-27 17:08:58.514931',4,'2026-09-27 17:08:58.514423',1,38,NULL),(14,'2026-09-27 17:08:58.527064','2026-09-27 17:08:58.527064',4,'2026-09-27 17:08:58.526508',2,38,NULL),(15,'2026-09-27 17:08:58.537438','2026-09-27 17:08:58.537438',4,'2026-09-27 17:08:58.536889',3,38,NULL),(16,'2026-09-27 17:08:58.547700','2026-09-27 17:08:58.547700',2,'2026-09-27 17:08:58.547071',4,38,NULL),(17,'2026-09-27 17:08:58.558464','2026-09-27 17:08:58.558464',1,'2026-09-27 17:08:58.558464',5,38,NULL),(59,NULL,'2026-10-05 12:16:30.784366',3,'2026-10-05 12:16:30.779178',1,430,'2026-10-03 08:22:01.328403'),(60,NULL,'2026-10-03 08:22:01.340547',1,'2026-10-03 08:22:01.340547',2,430,'2026-10-03 08:22:01.340547'),(61,NULL,'2026-10-03 08:22:01.350197',1,'2026-10-03 08:22:01.350197',3,430,'2026-10-03 08:22:01.350197'),(62,NULL,'2026-10-03 08:22:01.361864',1,'2026-10-03 08:22:01.361864',4,430,'2026-10-03 08:22:01.361864'),(63,NULL,'2026-10-03 08:22:01.372250',1,'2026-10-03 08:22:01.372250',5,430,'2026-10-03 08:22:01.372250'),(64,NULL,'2026-10-03 08:22:01.389775',1,'2026-10-03 08:22:01.389775',1,431,'2026-10-03 08:22:01.389775'),(65,NULL,'2026-10-03 08:22:01.399142',1,'2026-10-03 08:22:01.399142',2,431,'2026-10-03 08:22:01.399142'),(66,NULL,'2026-10-03 08:22:01.410665',1,'2026-10-03 08:22:01.409669',3,431,'2026-10-03 08:22:01.410665'),(67,NULL,'2026-10-03 08:22:01.420813',1,'2026-10-03 08:22:01.420813',4,431,'2026-10-03 08:22:01.420813'),(68,NULL,'2026-10-03 08:22:01.432033',1,'2026-10-03 08:22:01.432033',5,431,'2026-10-03 08:22:01.432033'),(69,NULL,'2026-10-03 08:22:01.447934',1,'2026-10-03 08:22:01.447934',1,432,'2026-10-03 08:22:01.447934'),(70,NULL,'2026-10-03 08:22:01.458464',1,'2026-10-03 08:22:01.458464',2,432,'2026-10-03 08:22:01.458464'),(71,NULL,'2026-10-03 08:22:01.466465',1,'2026-10-03 08:22:01.466465',3,432,'2026-10-03 08:22:01.466465'),(72,NULL,'2026-10-03 08:22:01.475568',1,'2026-10-03 08:22:01.475568',4,432,'2026-10-03 08:22:01.475568'),(73,NULL,'2026-10-03 08:22:01.484663',1,'2026-10-03 08:22:01.484663',5,432,'2026-10-03 08:22:01.484663'),(74,NULL,'2026-10-03 08:22:01.497649',1,'2026-10-03 08:22:01.496120',1,433,'2026-10-03 08:22:01.497649'),(75,NULL,'2026-10-03 08:22:01.506791',1,'2026-10-03 08:22:01.506791',2,433,'2026-10-03 08:22:01.506791'),(76,NULL,'2026-10-03 08:22:01.514921',1,'2026-10-03 08:22:01.514921',3,433,'2026-10-03 08:22:01.514921'),(77,NULL,'2026-10-03 08:22:01.522191',1,'2026-10-03 08:22:01.522191',4,433,'2026-10-03 08:22:01.522191'),(78,NULL,'2026-10-03 08:22:01.531922',1,'2026-10-03 08:22:01.531922',5,433,'2026-10-03 08:22:01.531922'),(209,NULL,'2026-10-03 08:22:02.891184',1,'2026-10-03 08:22:02.891184',1,460,'2026-10-03 08:22:02.891184'),(210,NULL,'2026-10-03 08:22:02.899325',1,'2026-10-03 08:22:02.899325',2,460,'2026-10-03 08:22:02.899325'),(211,NULL,'2026-10-03 08:22:02.907524',1,'2026-10-03 08:22:02.907524',3,460,'2026-10-03 08:22:02.907524'),(212,NULL,'2026-10-03 08:22:02.915567',1,'2026-10-03 08:22:02.915567',4,460,'2026-10-03 08:22:02.915567'),(213,NULL,'2026-10-03 08:22:02.926392',1,'2026-10-03 08:22:02.926392',5,460,'2026-10-03 08:22:02.926392'),(214,NULL,'2026-10-03 08:22:02.937773',1,'2026-10-03 08:22:02.937773',1,461,'2026-10-03 08:22:02.937773'),(215,NULL,'2026-10-03 08:22:02.946118',1,'2026-10-03 08:22:02.946118',2,461,'2026-10-03 08:22:02.946118'),(216,NULL,'2026-10-03 08:22:02.955778',1,'2026-10-03 08:22:02.954118',3,461,'2026-10-03 08:22:02.955778'),(217,NULL,'2026-10-03 08:22:02.966297',1,'2026-10-03 08:22:02.966297',4,461,'2026-10-03 08:22:02.966297'),(218,NULL,'2026-10-03 08:22:02.976199',1,'2026-10-03 08:22:02.976199',5,461,'2026-10-03 08:22:02.976199'),(219,NULL,'2026-10-03 08:22:02.992448',1,'2026-10-03 08:22:02.992448',1,462,'2026-10-03 08:22:02.992448'),(220,NULL,'2026-10-03 08:22:03.003997',1,'2026-10-03 08:22:03.003997',2,462,'2026-10-03 08:22:03.003997'),(221,NULL,'2026-10-03 08:22:03.012358',1,'2026-10-03 08:22:03.012358',3,462,'2026-10-03 08:22:03.012358'),(222,NULL,'2026-10-03 08:22:03.020580',1,'2026-10-03 08:22:03.020580',4,462,'2026-10-03 08:22:03.020580'),(223,NULL,'2026-10-03 08:22:03.028839',1,'2026-10-03 08:22:03.028839',5,462,'2026-10-03 08:22:03.028839'),(224,NULL,'2026-10-03 08:22:03.041943',1,'2026-10-03 08:22:03.041943',1,463,'2026-10-03 08:22:03.041943'),(225,NULL,'2026-10-03 08:22:03.050086',1,'2026-10-03 08:22:03.050086',2,463,'2026-10-03 08:22:03.050086'),(226,NULL,'2026-10-03 08:22:03.058741',1,'2026-10-03 08:22:03.058741',3,463,'2026-10-03 08:22:03.058741'),(227,NULL,'2026-10-03 08:22:03.067229',1,'2026-10-03 08:22:03.067229',4,463,'2026-10-03 08:22:03.067229'),(228,NULL,'2026-10-03 08:22:03.077575',1,'2026-10-03 08:22:03.075491',5,463,'2026-10-03 08:22:03.077575'),(229,NULL,'2026-10-03 08:22:03.092823',1,'2026-10-03 08:22:03.092823',1,464,'2026-10-03 08:22:03.092823'),(230,NULL,'2026-10-03 08:22:03.104668',1,'2026-10-03 08:22:03.103001',2,464,'2026-10-03 08:22:03.104668'),(231,NULL,'2026-10-03 08:22:03.154207',1,'2026-10-03 08:22:03.154207',3,464,'2026-10-03 08:22:03.154207'),(232,NULL,'2026-10-03 08:22:03.167348',1,'2026-10-03 08:22:03.167348',4,464,'2026-10-03 08:22:03.167348'),(233,NULL,'2026-10-03 08:22:03.175623',1,'2026-10-03 08:22:03.175623',5,464,'2026-10-03 08:22:03.175623'),(359,NULL,'2026-10-03 08:22:04.431478',1,'2026-10-03 08:22:04.431478',1,490,'2026-10-03 08:22:04.431478'),(360,NULL,'2026-10-03 08:22:04.441910',1,'2026-10-03 08:22:04.441910',2,490,'2026-10-03 08:22:04.441910'),(361,NULL,'2026-10-03 08:22:04.449273',1,'2026-10-03 08:22:04.449273',3,490,'2026-10-03 08:22:04.449273'),(362,NULL,'2026-10-03 08:22:04.460027',1,'2026-10-03 08:22:04.460027',4,490,'2026-10-03 08:22:04.460027'),(363,NULL,'2026-10-03 08:22:04.474149',1,'2026-10-03 08:22:04.474149',5,490,'2026-10-03 08:22:04.474149'),(364,NULL,'2026-10-03 08:22:04.491127',1,'2026-10-03 08:22:04.489490',1,491,'2026-10-03 08:22:04.491127'),(365,NULL,'2026-10-03 08:22:04.504125',1,'2026-10-03 08:22:04.504125',2,491,'2026-10-03 08:22:04.504125'),(366,NULL,'2026-10-03 08:22:04.519311',1,'2026-10-03 08:22:04.519311',3,491,'2026-10-03 08:22:04.519311'),(367,NULL,'2026-10-03 08:22:04.533571',1,'2026-10-03 08:22:04.533571',4,491,'2026-10-03 08:22:04.533571'),(368,NULL,'2026-10-03 08:22:04.550259',1,'2026-10-03 08:22:04.550259',5,491,'2026-10-03 08:22:04.550259'),(369,NULL,'2026-10-03 08:22:04.567612',1,'2026-10-03 08:22:04.567612',1,492,'2026-10-03 08:22:04.567612'),(370,NULL,'2026-10-03 08:22:04.576352',1,'2026-10-03 08:22:04.576352',2,492,'2026-10-03 08:22:04.576352'),(371,NULL,'2026-10-03 08:22:04.587285',1,'2026-10-03 08:22:04.587285',3,492,'2026-10-03 08:22:04.587285'),(372,NULL,'2026-10-03 08:22:04.594580',1,'2026-10-03 08:22:04.594580',4,492,'2026-10-03 08:22:04.594580'),(373,NULL,'2026-10-03 08:22:04.605744',1,'2026-10-03 08:22:04.605744',5,492,'2026-10-03 08:22:04.605744'),(374,NULL,'2026-10-03 08:22:04.618443',1,'2026-10-03 08:22:04.618443',1,493,'2026-10-03 08:22:04.618443'),(375,NULL,'2026-10-03 08:22:04.626338',1,'2026-10-03 08:22:04.626338',2,493,'2026-10-03 08:22:04.626338'),(376,NULL,'2026-10-03 08:22:04.632862',1,'2026-10-03 08:22:04.632862',3,493,'2026-10-03 08:22:04.632862'),(377,NULL,'2026-10-03 08:22:04.643337',1,'2026-10-03 08:22:04.643337',4,493,'2026-10-03 08:22:04.643337'),(378,NULL,'2026-10-03 08:22:04.655153',1,'2026-10-03 08:22:04.655153',5,493,'2026-10-03 08:22:04.655153'),(379,NULL,'2026-10-03 08:22:04.673922',1,'2026-10-03 08:22:04.672178',1,494,'2026-10-03 08:22:04.673922'),(380,NULL,'2026-10-03 08:22:04.685186',1,'2026-10-03 08:22:04.683463',2,494,'2026-10-03 08:22:04.685186'),(381,NULL,'2026-10-03 08:22:04.698844',1,'2026-10-03 08:22:04.698844',3,494,'2026-10-03 08:22:04.698844'),(382,NULL,'2026-10-03 08:22:04.710846',1,'2026-10-03 08:22:04.710846',4,494,'2026-10-03 08:22:04.710846'),(383,NULL,'2026-10-03 08:22:04.721132',1,'2026-10-03 08:22:04.721132',5,494,'2026-10-03 08:22:04.721132');
/*!40000 ALTER TABLE `villa_inventories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `villa_services`
--

DROP TABLE IF EXISTS `villa_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `villa_services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `is_available` bit(1) DEFAULT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  `price_override` decimal(12,2) DEFAULT NULL,
  `service_id` bigint NOT NULL,
  `villa_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKy6twt930w8jtts23t7cglphq` (`villa_id`,`service_id`),
  KEY `FKn6ihggcdx114uvye81k03oqec` (`service_id`),
  CONSTRAINT `FKn6ihggcdx114uvye81k03oqec` FOREIGN KEY (`service_id`) REFERENCES `extra_services` (`id`),
  CONSTRAINT `FKs3fs49fghvqclqg06m5qy1tba` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `villa_services`
--

LOCK TABLES `villa_services` WRITE;
/*!40000 ALTER TABLE `villa_services` DISABLE KEYS */;
INSERT INTO `villa_services` VALUES (1,'2026-09-27 14:19:27.953689','2026-09-27 14:19:27.953689',_binary '',NULL,NULL,2,29,NULL),(2,'2026-09-27 14:19:27.953689','2026-09-27 14:19:27.953689',_binary '',NULL,NULL,7,29,NULL),(3,'2026-09-27 14:19:27.953689','2026-09-27 14:19:27.953689',_binary '',NULL,NULL,1,29,NULL);
/*!40000 ALTER TABLE `villa_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `villa_supply_standards`
--

DROP TABLE IF EXISTS `villa_supply_standards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `villa_supply_standards` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `standard_quantity` int NOT NULL,
  `item_id` bigint NOT NULL,
  `villa_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKe67iaw7fmbn5k58e1plqyrygk` (`villa_id`,`item_id`),
  KEY `idx_supply_std_villa` (`villa_id`),
  KEY `idx_supply_std_item` (`item_id`),
  CONSTRAINT `FKbn0ybl7c2bgjv37qft6hk61ti` FOREIGN KEY (`item_id`) REFERENCES `inventory_items` (`id`),
  CONSTRAINT `FKrdjjhslxela13lq8fsgtniya5` FOREIGN KEY (`villa_id`) REFERENCES `villas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=509 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `villa_supply_standards`
--

LOCK TABLES `villa_supply_standards` WRITE;
/*!40000 ALTER TABLE `villa_supply_standards` DISABLE KEYS */;
INSERT INTO `villa_supply_standards` VALUES (1,'2026-09-27 17:02:30.933326','2026-10-03 08:33:26.382880','Định mức tiêu chuẩn buồng phòng Resort',6,1,29,NULL),(2,'2026-09-27 17:02:30.981954','2026-10-03 08:33:26.700240','Định mức tiêu chuẩn buồng phòng Resort',6,2,29,NULL),(3,'2026-09-27 17:02:30.986727','2026-10-03 08:33:26.963649','Định mức tiêu chuẩn buồng phòng Resort',6,3,29,NULL),(4,'2026-09-27 17:02:30.990851','2026-10-03 08:33:27.268364','Định mức tiêu chuẩn buồng phòng Resort',1,4,29,NULL),(5,'2026-09-27 17:02:30.993598','2026-09-27 17:02:30.993598','Định mức tiêu chuẩn buồng phòng Resort',1,5,29,NULL),(13,'2026-09-27 17:08:58.507724','2026-10-03 08:33:26.381881','Định mức tự động tiêu chuẩn Resort',1,1,38,NULL),(14,'2026-09-27 17:08:58.520160','2026-10-03 08:33:26.701240','Định mức tự động tiêu chuẩn Resort',1,2,38,NULL),(15,'2026-09-27 17:08:58.531888','2026-10-03 08:33:26.971691','Định mức tự động tiêu chuẩn Resort',1,3,38,NULL),(16,'2026-09-27 17:08:58.542609','2026-10-03 08:33:27.271360','Định mức tự động tiêu chuẩn Resort',1,4,38,NULL),(17,'2026-09-27 17:08:58.552799','2026-09-27 17:08:58.552799','Định mức tự động tiêu chuẩn Resort',1,5,38,NULL),(59,NULL,'2026-10-03 08:33:26.472613','Định mức tự động tiêu chuẩn Resort',3,1,430,'2026-10-03 08:22:01.323505'),(60,NULL,'2026-10-03 08:33:26.714944','Định mức tự động tiêu chuẩn Resort',3,2,430,'2026-10-03 08:22:01.333627'),(61,NULL,'2026-10-03 08:33:26.979042','Định mức tự động tiêu chuẩn Resort',3,3,430,'2026-10-03 08:22:01.345645'),(62,NULL,'2026-10-03 08:35:55.780948','Định mức tự động tiêu chuẩn Resort',1,4,430,'2026-10-03 08:22:01.355206'),(63,NULL,'2026-10-03 08:22:01.366849','Định mức tự động tiêu chuẩn Resort',1,5,430,'2026-10-03 08:22:01.366849'),(64,NULL,'2026-10-03 08:33:26.475951','Định mức tự động tiêu chuẩn Resort',8,1,431,'2026-10-03 08:22:01.383894'),(65,NULL,'2026-10-03 08:33:26.716471','Định mức tự động tiêu chuẩn Resort',8,2,431,'2026-10-03 08:22:01.394321'),(66,NULL,'2026-10-03 08:33:26.986553','Định mức tự động tiêu chuẩn Resort',8,3,431,'2026-10-03 08:22:01.404673'),(67,NULL,'2026-10-03 08:35:55.780948','Định mức tự động tiêu chuẩn Resort',1,4,431,'2026-10-03 08:22:01.414861'),(68,NULL,'2026-10-03 08:22:01.424980','Định mức tự động tiêu chuẩn Resort',1,5,431,'2026-10-03 08:22:01.424980'),(69,NULL,'2026-10-03 08:33:26.590610','Định mức tự động tiêu chuẩn Resort',8,1,432,'2026-10-03 08:22:01.440704'),(70,NULL,'2026-10-03 08:33:26.716471','Định mức tự động tiêu chuẩn Resort',8,2,432,'2026-10-03 08:22:01.452916'),(71,NULL,'2026-10-03 08:33:26.986553','Định mức tự động tiêu chuẩn Resort',8,3,432,'2026-10-03 08:22:01.462466'),(72,NULL,'2026-10-03 08:35:55.783018','Định mức tự động tiêu chuẩn Resort',1,4,432,'2026-10-03 08:22:01.471570'),(73,NULL,'2026-10-03 08:22:01.480656','Định mức tự động tiêu chuẩn Resort',1,5,432,'2026-10-03 08:22:01.480656'),(74,NULL,'2026-10-03 08:33:26.603685','Định mức tự động tiêu chuẩn Resort',8,1,433,'2026-10-03 08:22:01.493122'),(75,NULL,'2026-10-03 08:33:26.722134','Định mức tự động tiêu chuẩn Resort',8,2,433,'2026-10-03 08:22:01.502682'),(76,NULL,'2026-10-03 08:33:26.988995','Định mức tự động tiêu chuẩn Resort',8,3,433,'2026-10-03 08:22:01.510921'),(77,NULL,'2026-10-03 08:35:55.793685','Định mức tự động tiêu chuẩn Resort',1,4,433,'2026-10-03 08:22:01.518443'),(78,NULL,'2026-10-03 08:22:01.527844','Định mức tự động tiêu chuẩn Resort',1,5,433,'2026-10-03 08:22:01.527844'),(209,NULL,'2026-10-03 08:33:26.507593','Định mức tự động tiêu chuẩn Resort',16,1,460,'2026-10-03 08:22:02.887520'),(210,NULL,'2026-10-03 08:33:26.815729','Định mức tự động tiêu chuẩn Resort',16,2,460,'2026-10-03 08:22:02.896190'),(211,NULL,'2026-10-03 08:33:27.066954','Định mức tự động tiêu chuẩn Resort',16,3,460,'2026-10-03 08:22:02.904175'),(212,NULL,'2026-10-03 08:35:55.675469','Định mức tự động tiêu chuẩn Resort',1,4,460,'2026-10-03 08:22:02.912356'),(213,NULL,'2026-10-03 08:22:02.920137','Định mức tự động tiêu chuẩn Resort',1,5,460,'2026-10-03 08:22:02.920137'),(214,NULL,'2026-10-03 08:33:26.511408','Định mức tự động tiêu chuẩn Resort',13,1,461,'2026-10-03 08:22:02.934610'),(215,NULL,'2026-10-03 08:33:26.819498','Định mức tự động tiêu chuẩn Resort',13,2,461,'2026-10-03 08:22:02.941087'),(216,NULL,'2026-10-03 08:33:27.068395','Định mức tự động tiêu chuẩn Resort',13,3,461,'2026-10-03 08:22:02.949973'),(217,NULL,'2026-10-03 08:35:55.678109','Định mức tự động tiêu chuẩn Resort',1,4,461,'2026-10-03 08:22:02.959061'),(218,NULL,'2026-10-03 08:22:02.971349','Định mức tự động tiêu chuẩn Resort',1,5,461,'2026-10-03 08:22:02.971349'),(219,NULL,'2026-10-03 08:33:26.511408','Định mức tự động tiêu chuẩn Resort',8,1,462,'2026-10-03 08:22:02.987505'),(220,NULL,'2026-10-03 08:33:26.824569','Định mức tự động tiêu chuẩn Resort',8,2,462,'2026-10-03 08:22:02.997354'),(221,NULL,'2026-10-03 08:33:27.071935','Định mức tự động tiêu chuẩn Resort',8,3,462,'2026-10-03 08:22:03.007282'),(222,NULL,'2026-10-03 08:35:55.690023','Định mức tự động tiêu chuẩn Resort',1,4,462,'2026-10-03 08:22:03.017210'),(223,NULL,'2026-10-03 08:22:03.024378','Định mức tự động tiêu chuẩn Resort',1,5,462,'2026-10-03 08:22:03.024378'),(224,NULL,'2026-10-03 08:33:26.515527','Định mức tự động tiêu chuẩn Resort',3,1,463,'2026-10-03 08:22:03.036982'),(225,NULL,'2026-10-03 08:33:26.824569','Định mức tự động tiêu chuẩn Resort',3,2,463,'2026-10-03 08:22:03.045097'),(226,NULL,'2026-10-03 08:33:27.072929','Định mức tự động tiêu chuẩn Resort',3,3,463,'2026-10-03 08:22:03.053554'),(227,NULL,'2026-10-03 08:35:55.690023','Định mức tự động tiêu chuẩn Resort',1,4,463,'2026-10-03 08:22:03.063778'),(228,NULL,'2026-10-03 08:22:03.072265','Định mức tự động tiêu chuẩn Resort',1,5,463,'2026-10-03 08:22:03.072265'),(229,NULL,'2026-10-03 08:33:26.515527','Định mức tự động tiêu chuẩn Resort',3,1,464,'2026-10-03 08:22:03.086362'),(230,NULL,'2026-10-03 08:33:26.833848','Định mức tự động tiêu chuẩn Resort',3,2,464,'2026-10-03 08:22:03.098461'),(231,NULL,'2026-10-03 08:33:27.072929','Định mức tự động tiêu chuẩn Resort',3,3,464,'2026-10-03 08:22:03.145757'),(232,NULL,'2026-10-03 08:35:55.690023','Định mức tự động tiêu chuẩn Resort',1,4,464,'2026-10-03 08:22:03.163306'),(233,NULL,'2026-10-03 08:22:03.171614','Định mức tự động tiêu chuẩn Resort',1,5,464,'2026-10-03 08:22:03.171614'),(359,NULL,'2026-10-03 08:33:26.621757','Định mức tự động tiêu chuẩn Resort',3,1,490,'2026-10-03 08:22:04.426984'),(360,NULL,'2026-10-03 08:33:26.896046','Định mức tự động tiêu chuẩn Resort',3,2,490,'2026-10-03 08:22:04.435160'),(361,NULL,'2026-10-03 08:33:27.149579','Định mức tự động tiêu chuẩn Resort',3,3,490,'2026-10-03 08:22:04.446204'),(362,NULL,'2026-10-03 08:35:55.755215','Định mức tự động tiêu chuẩn Resort',1,4,490,'2026-10-03 08:22:04.454983'),(363,NULL,'2026-10-03 08:22:04.466933','Định mức tự động tiêu chuẩn Resort',1,5,490,'2026-10-03 08:22:04.466933'),(364,NULL,'2026-10-03 08:33:26.621757','Định mức tự động tiêu chuẩn Resort',8,1,491,'2026-10-03 08:22:04.484894'),(365,NULL,'2026-10-03 08:33:26.898230','Định mức tự động tiêu chuẩn Resort',8,2,491,'2026-10-03 08:22:04.497385'),(366,NULL,'2026-10-03 08:33:27.149579','Định mức tự động tiêu chuẩn Resort',8,3,491,'2026-10-03 08:22:04.511915'),(367,NULL,'2026-10-03 08:35:55.755756','Định mức tự động tiêu chuẩn Resort',1,4,491,'2026-10-03 08:22:04.526947'),(368,NULL,'2026-10-03 08:22:04.542398','Định mức tự động tiêu chuẩn Resort',1,5,491,'2026-10-03 08:22:04.542398'),(369,NULL,'2026-10-03 08:33:26.619752','Định mức tự động tiêu chuẩn Resort',5,1,492,'2026-10-03 08:22:04.561691'),(370,NULL,'2026-10-03 08:33:26.896046','Định mức tự động tiêu chuẩn Resort',5,2,492,'2026-10-03 08:22:04.571135'),(371,NULL,'2026-10-03 08:33:27.156443','Định mức tự động tiêu chuẩn Resort',5,3,492,'2026-10-03 08:22:04.580784'),(372,NULL,'2026-10-03 08:35:55.757250','Định mức tự động tiêu chuẩn Resort',1,4,492,'2026-10-03 08:22:04.591348'),(373,NULL,'2026-10-03 08:22:04.599430','Định mức tự động tiêu chuẩn Resort',1,5,492,'2026-10-03 08:22:04.599430'),(374,NULL,'2026-10-03 08:33:26.629653','Định mức tự động tiêu chuẩn Resort',3,1,493,'2026-10-03 08:22:04.614700'),(375,NULL,'2026-10-03 08:33:26.905206','Định mức tự động tiêu chuẩn Resort',3,2,493,'2026-10-03 08:22:04.621560'),(376,NULL,'2026-10-03 08:33:27.170717','Định mức tự động tiêu chuẩn Resort',3,3,493,'2026-10-03 08:22:04.627937'),(377,NULL,'2026-10-03 08:35:55.756736','Định mức tự động tiêu chuẩn Resort',1,4,493,'2026-10-03 08:22:04.638560'),(378,NULL,'2026-10-03 08:22:04.649385','Định mức tự động tiêu chuẩn Resort',1,5,493,'2026-10-03 08:22:04.649385'),(379,NULL,'2026-10-03 08:33:26.629653','Định mức tự động tiêu chuẩn Resort',8,1,494,'2026-10-03 08:22:04.667189'),(380,NULL,'2026-10-03 08:33:26.905206','Định mức tự động tiêu chuẩn Resort',8,2,494,'2026-10-03 08:22:04.678192'),(381,NULL,'2026-10-03 08:33:27.165797','Định mức tự động tiêu chuẩn Resort',8,3,494,'2026-10-03 08:22:04.693008'),(382,NULL,'2026-10-03 08:35:55.759423','Định mức tự động tiêu chuẩn Resort',1,4,494,'2026-10-03 08:22:04.703920'),(383,NULL,'2026-10-03 08:22:04.716017','Định mức tự động tiêu chuẩn Resort',1,5,494,'2026-10-03 08:22:04.716017');
/*!40000 ALTER TABLE `villa_supply_standards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `villa_types`
--

DROP TABLE IF EXISTS `villa_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `villa_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `base_price` decimal(12,2) NOT NULL,
  `capacity` int NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `dynamic_price` decimal(12,2) DEFAULT NULL,
  `image_url` longtext COLLATE utf8mb4_unicode_ci,
  `is_dynamic_pricing_enabled` bit(1) DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `bed_type` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `adults` int DEFAULT '2',
  `children` int DEFAULT '0',
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8b4ubk0k54xg4p6p3eiclsfa2` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `villa_types`
--

LOCK TABLES `villa_types` WRITE;
/*!40000 ALTER TABLE `villa_types` DISABLE KEYS */;
INSERT INTO `villa_types` VALUES (1,'2026-09-23 00:56:08.314013','2026-09-23 01:02:35.253093',12000000.00,1,'Biệt thự nghỉ dưỡng cao cấp.',0.00,'/assets/images/uploads/villa_108db697.png',_binary '','Beachfront Pool Villa','Rộng 1m × Dài 1.9m',1,0,NULL),(2,'2026-09-23 01:05:26.902815','2026-09-23 01:32:40.804286',8500000.00,2,'Tiêu chuẩn giường ngủ và nghỉ dưỡng cao cấp.',0.00,'/assets/images/uploads/villa_dbf180bc.jpg',_binary '','Ocean Sunset Villa','Rộng 1.8m × Dài 2.0m',2,0,NULL),(3,'2026-09-23 01:12:42.419299','2026-09-23 01:30:59.466232',5500000.00,3,'Tiêu chuẩn giường ngủ và nghỉ dưỡng cao cấp.',0.00,'/assets/images/uploads/villa_9f7e91b6.jpg',_binary '','Lagoon Garden Villa','Rộng 1.8m × Dài 2.0m',2,1,NULL),(4,'2026-09-23 01:15:48.982077','2026-09-23 01:25:47.101613',25000000.00,4,'Tiêu chuẩn giường ngủ và nghỉ dưỡng cao cấp.',0.00,'/assets/images/uploads/villa_44454c00.jpg',_binary '','Presidential Sanctuary Villa','Rộng 1.8m × Dài 2.0m',4,0,NULL);
/*!40000 ALTER TABLE `villa_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `villas`
--

DROP TABLE IF EXISTS `villas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `villas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `current_guest_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `floor` int DEFAULT NULL,
  `last_cleaned_at` datetime(6) DEFAULT NULL,
  `ozone_status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('AVAILABLE','CLEANING','MAINTENANCE','OCCUPIED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `villa_number` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `zone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `villa_type_id` bigint NOT NULL,
  `amenities` text COLLATE utf8mb4_unicode_ci,
  `base_price` decimal(12,2) DEFAULT NULL,
  `bedroom_count` int DEFAULT NULL,
  `structure_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `zone_id` bigint DEFAULT NULL,
  `area` double DEFAULT NULL,
  `overview_description` text COLLATE utf8mb4_unicode_ci,
  `pool_size` double DEFAULT NULL,
  `view_direction` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKouo3yad5418liqd5os34aq8a1` (`villa_type_id`),
  KEY `FKr660wx9gy24da102dwtek43xi` (`zone_id`),
  CONSTRAINT `FKouo3yad5418liqd5os34aq8a1` FOREIGN KEY (`villa_type_id`) REFERENCES `villa_types` (`id`),
  CONSTRAINT `FKr660wx9gy24da102dwtek43xi` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=538 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `villas`
--

LOCK TABLES `villas` WRITE;
/*!40000 ALTER TABLE `villas` DISABLE KEYS */;
INSERT INTO `villas` VALUES (29,'2026-09-27 12:57:21.486128','2026-09-30 09:26:05.613054',NULL,2,'2026-09-27 12:57:21.455105','STERILIZED','AVAILABLE','NT-VIP',NULL,4,'Ăn sáng, BBQ, Thuê bếp',256236.00,2,'2 Tầng (2 Floors)',4,NULL,NULL,NULL,NULL,NULL),(38,'2026-09-27 14:28:58.573998','2026-10-05 16:33:50.846759',NULL,1,'2026-10-02 16:44:35.828002','CLEANED','CLEANING','NT-105',NULL,1,'',439705.00,1,'1 Tầng (Ground Floor)',4,NULL,NULL,NULL,NULL,NULL),(430,'2026-09-30 11:25:26.682112','2026-10-05 13:45:25.610401',NULL,1,'2026-10-05 13:45:25.593483','CLEANED','AVAILABLE','NT-101',NULL,1,'Lối đi riêng ra biển, Ghế tắm nắng, BBQ ngoài trời',2678458.00,1,'1 Tầng (Beachfront)',4,385.5,'Sự kết hợp hoàn hảo giữa tiện nghi xa xỉ và vẻ đẹp nguyên sơ của bãi biển, đem đến trải nghiệm nghỉ dưỡng đích thực. Tại căn NT-001, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 385.5m2 cùng hồ bơi vô cực 52m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',52,'Trực diện Biển',NULL),(431,'2026-09-30 11:25:26.700601','2026-10-05 16:59:40.435735',NULL,1,'2026-10-05 16:59:40.391695','CLEANED','AVAILABLE','NT-102',NULL,1,'Ghế tắm nắng, Lối đi riêng ra biển, BBQ ngoài trời, Hồ bơi vô cực',1756570.00,3,'1 Tầng (Beachfront)',4,425.5,'Nép mình bên bờ cát trắng mịn, mang đến không gian mở hòa quyện cùng tiếng sóng biển du dương. Tại căn NT-002, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 425.5m2 cùng hồ bơi vô cực 34m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',34,'Biển một phần',NULL),(432,'2026-09-30 11:25:26.714464','2026-10-05 16:59:43.625009',NULL,2,'2026-10-05 16:59:43.608351','CLEANED','AVAILABLE','NT-103',NULL,1,'Hồ bơi vô cực, BBQ ngoài trời, Lối đi riêng ra biển, Ghế tắm nắng',547479.00,3,'2 Tầng (Duplex Beachfront)',4,415.5,'Nép mình bên bờ cát trắng mịn, mang đến không gian mở hòa quyện cùng tiếng sóng biển du dương. Tại căn NT-003, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 415.5m2 cùng hồ bơi vô cực 20m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',20,'Hồ bơi trung tâm',NULL),(433,'2026-09-30 11:25:26.729524','2026-09-30 11:25:26.729524',NULL,2,'2026-09-30 11:25:26.728451','STERILIZED','AVAILABLE','NT-104',NULL,1,'Ghế tắm nắng, Lối đi riêng ra biển, BBQ ngoài trời, Hồ bơi vô cực',2067686.00,3,'2 Tầng (Duplex Beachfront)',4,450.5,'Sự kết hợp hoàn hảo giữa tiện nghi xa xỉ và vẻ đẹp nguyên sơ của bãi biển, đem đến trải nghiệm nghỉ dưỡng đích thực. Tại căn NT-004, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 450.5m2 cùng hồ bơi vô cực 36m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',36,'Hồ bơi trung tâm',NULL),(460,'2026-09-30 11:25:27.063954','2026-10-05 16:49:30.963905',NULL,1,'2026-10-05 16:49:30.919734','CLEANED','AVAILABLE','SB-201',NULL,2,'Bồn tắm spa ngoài trời, Võng trên nước, Thang xuống lagoon, Bữa sáng nổi',1337064.00,6,'1 Tầng (Lagoon Water Villa)',5,342.5,'Tận hưởng bữa sáng nổi trên hồ bơi và ngắm nhìn ánh hoàng hôn rực rỡ buông xuống từ ban công riêng tư. Tại căn SB-001, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 342.5m2 cùng hồ bơi vô cực 34m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',34,'Khu lặn biển',NULL),(461,'2026-09-30 11:25:27.076037','2026-10-04 04:07:44.484206',NULL,2,'2026-09-30 11:25:27.074524','STERILIZED','AVAILABLE','SB-202',NULL,2,'Bồn tắm spa ngoài trời, Bữa sáng nổi, Võng trên nước',1275103.00,5,'2 Tầng (Lagoon Family)',5,546.5,'Lơ lửng trên mặt nước Lagoon trong vắt, biệt thự mang lại trải nghiệm độc bản lấy cảm hứng từ Maldives thu nhỏ. Tại căn SB-002, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 546.5m2 cùng hồ bơi vô cực 24m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',24,'Mặt hồ tĩnh lặng',NULL),(462,'2026-09-30 11:25:27.090630','2026-10-05 16:42:15.241460',NULL,2,'2026-09-30 11:25:27.088458','STERILIZED','CLEANING','SB-203',NULL,2,'Thang xuống lagoon, Bồn tắm spa ngoài trời, Bữa sáng nổi, Võng trên nước',2164322.00,3,'2 Tầng (Lagoon Family)',5,226.5,'Tận hưởng bữa sáng nổi trên hồ bơi và ngắm nhìn ánh hoàng hôn rực rỡ buông xuống từ ban công riêng tư. Tại căn SB-003, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 226.5m2 cùng hồ bơi vô cực 18m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',18,'Lagoon & Hoàng Hôn',NULL),(463,'2026-09-30 11:25:27.099816','2026-09-30 11:25:27.099816',NULL,2,'2026-09-30 11:25:27.098099','STERILIZED','AVAILABLE','SB-204',NULL,2,'Bồn tắm spa ngoài trời, Võng trên nước, Thang xuống lagoon, Bữa sáng nổi',1996302.00,1,'2 Tầng (Lagoon Family)',5,520.5,'Tận hưởng bữa sáng nổi trên hồ bơi và ngắm nhìn ánh hoàng hôn rực rỡ buông xuống từ ban công riêng tư. Tại căn SB-004, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 520.5m2 cùng hồ bơi vô cực 34m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',34,'Lagoon & Hoàng Hôn',NULL),(464,'2026-09-30 11:25:27.107681','2026-10-05 16:33:48.807612',NULL,1,'2026-09-30 11:25:27.107681','STERILIZED','CLEANING','SB-VIP',NULL,4,'Võng trên nước, Bữa sáng nổi, Thang xuống lagoon',3288546.00,1,'1 Tầng (Lagoon Water Villa)',5,298.5,'Tận hưởng bữa sáng nổi trên hồ bơi và ngắm nhìn ánh hoàng hôn rực rỡ buông xuống từ ban công riêng tư. Tại căn SB-005, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 298.5m2 cùng hồ bơi vô cực 35m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',35,'Mặt hồ tĩnh lặng',NULL),(490,'2026-09-30 11:25:27.474200','2026-09-30 11:25:27.474200',NULL,3,'2026-09-30 11:25:27.473690','STERILIZED','MAINTENANCE','SH-301',NULL,3,'Phòng thiền/Yoga, Khu vườn riêng, Bếp mở, Hồ bơi sinh thái',3468663.00,1,'3 Tầng (Triplex View Đồi)',6,365.5,'Kiến trúc sinh thái giao hòa cùng thiên nhiên, với những ô cửa kính lớn thu trọn mảng xanh vào tầm mắt. Tại căn SH-001, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 365.5m2 cùng hồ bơi vô cực 29m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',29,'Đồi sinh thái',NULL),(491,'2026-09-30 11:25:27.483045','2026-10-05 16:59:42.563149',NULL,3,'2026-10-05 16:59:42.545636','CLEANED','AVAILABLE','SH-302',NULL,3,'Bếp mở, Hồ bơi sinh thái',2377135.00,3,'3 Tầng (Triplex View Đồi)',6,303.5,'Ẩn mình trong khu vườn nhiệt đới xanh mát, biệt thự là điểm đến lý tưởng cho những ai tìm kiếm sự tĩnh lặng và an yên. Tại căn SH-002, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 303.5m2 cùng hồ bơi vô cực 23m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',23,'Hồ nước ngọt',NULL),(492,'2026-09-30 11:25:27.493410','2026-09-30 11:25:27.493410',NULL,3,'2026-09-30 11:25:27.492317','STERILIZED','AVAILABLE','SH-303',NULL,3,'Phòng thiền/Yoga, Bếp mở, Khu vườn riêng, Hồ bơi sinh thái',1279686.00,2,'3 Tầng (Triplex View Đồi)',6,256.5,'Kiến trúc sinh thái giao hòa cùng thiên nhiên, với những ô cửa kính lớn thu trọn mảng xanh vào tầm mắt. Tại căn SH-003, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 256.5m2 cùng hồ bơi vô cực 30m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',30,'Đồi sinh thái',NULL),(493,'2026-09-30 11:25:27.502569','2026-09-30 11:25:27.502569',NULL,3,'2026-09-30 11:25:27.502054','STERILIZED','AVAILABLE','SH-304',NULL,3,'Hồ bơi sinh thái, Khu vườn riêng, Bếp mở',3867026.00,1,'3 Tầng (Triplex View Đồi)',6,210.5,'Kiến trúc sinh thái giao hòa cùng thiên nhiên, với những ô cửa kính lớn thu trọn mảng xanh vào tầm mắt. Tại căn SH-004, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 210.5m2 cùng hồ bơi vô cực 33m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',33,'Hồ nước ngọt',NULL),(494,'2026-09-30 11:25:27.512153','2026-09-30 11:25:27.512153',NULL,3,'2026-09-30 11:25:27.511639','STERILIZED','AVAILABLE','SH-305',NULL,3,'Khu vườn riêng, Phòng thiền/Yoga, Hồ bơi sinh thái',896071.00,3,'3 Tầng (Triplex View Đồi)',6,536.5,'Kiến trúc sinh thái giao hòa cùng thiên nhiên, với những ô cửa kính lớn thu trọn mảng xanh vào tầm mắt. Tại căn SH-005, du khách sẽ được chìm đắm trong không không gian nghỉ dưỡng có diện tích 536.5m2 cùng hồ bơi vô cực 31m2 cực kỳ thư thái. Nội thất tinh tuyển kết hợp các tiện nghi công nghệ thông minh sẵn sàng đáp ứng mọi nhu cầu.',31,'Đồi sinh thái',NULL);
/*!40000 ALTER TABLE `villas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weekly_shift_registration_details`
--

DROP TABLE IF EXISTS `weekly_shift_registration_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `weekly_shift_registration_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `day_of_week` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shift_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `work_date` date NOT NULL,
  `registration_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKntc30te8podv3jai8sa1y2lsn` (`registration_id`),
  CONSTRAINT `FKntc30te8podv3jai8sa1y2lsn` FOREIGN KEY (`registration_id`) REFERENCES `weekly_shift_registrations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weekly_shift_registration_details`
--

LOCK TABLES `weekly_shift_registration_details` WRITE;
/*!40000 ALTER TABLE `weekly_shift_registration_details` DISABLE KEYS */;
INSERT INTO `weekly_shift_registration_details` VALUES (1,'2026-10-06 01:16:50.962829','2026-10-06 01:16:50.962829','T2','','MORNING','2026-10-11',3),(2,'2026-10-06 01:16:50.964420','2026-10-06 01:16:50.964420','T3','','MORNING','2026-10-12',3),(3,'2026-10-06 01:16:50.966461','2026-10-06 01:16:50.966461','T4','','MORNING','2026-10-13',3),(4,'2026-10-06 01:16:50.967441','2026-10-06 01:16:50.967441','T5','','NIGHT','2026-10-14',3),(5,'2026-10-06 01:16:50.969003','2026-10-06 01:16:50.969003','T6','','MORNING','2026-10-15',3),(6,'2026-10-06 01:16:50.970020','2026-10-06 01:16:50.970020','T7','','OFF','2026-10-16',3),(7,'2026-10-06 01:16:50.974543','2026-10-06 01:16:50.974543','CN','','MORNING','2026-10-17',3);
/*!40000 ALTER TABLE `weekly_shift_registration_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weekly_shift_registrations`
--

DROP TABLE IF EXISTS `weekly_shift_registrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `weekly_shift_registrations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `approved_at` datetime(6) DEFAULT NULL,
  `notes` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `preferred_zone` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rejection_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `week_end_date` date NOT NULL,
  `week_start_date` date NOT NULL,
  `approver_id` bigint DEFAULT NULL,
  `staff_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnbi85konbula0mgctegqagox8` (`approver_id`),
  KEY `FK1p0vfb69oryf9q6o5y5holle0` (`staff_id`),
  CONSTRAINT `FK1p0vfb69oryf9q6o5y5holle0` FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKnbi85konbula0mgctegqagox8` FOREIGN KEY (`approver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weekly_shift_registrations`
--

LOCK TABLES `weekly_shift_registrations` WRITE;
/*!40000 ALTER TABLE `weekly_shift_registrations` DISABLE KEYS */;
INSERT INTO `weekly_shift_registrations` VALUES (3,'2026-10-06 01:16:50.956681','2026-10-06 01:16:50.956681',NULL,'','ALL',NULL,'PENDING','2026-10-17','2026-10-11',NULL,29);
/*!40000 ALTER TABLE `weekly_shift_registrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `yield_rules`
--

DROP TABLE IF EXISTS `yield_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `yield_rules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `condition_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `price_multiplier` double NOT NULL,
  `rule_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_room_types` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `threshold_value` double DEFAULT NULL,
  `target_villa_types` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `yield_rules`
--

LOCK TABLES `yield_rules` WRITE;
/*!40000 ALTER TABLE `yield_rules` DISABLE KEYS */;
/*!40000 ALTER TABLE `yield_rules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zones`
--

DROP TABLE IF EXISTS `zones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `zones` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `badge_class` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `icon` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `match_key` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tag` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `banner_url` longtext COLLATE utf8mb4_unicode_ci,
  `display_order` int DEFAULT NULL,
  `highlights` text COLLATE utf8mb4_unicode_ci,
  `slug` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9vf2c47kjchldfq92cptovfts` (`name`),
  UNIQUE KEY `UK2itgih8wxuecowlir3qw18aju` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zones`
--

LOCK TABLES `zones` WRITE;
/*!40000 ALTER TABLE `zones` DISABLE KEYS */;
INSERT INTO `zones` VALUES (4,'2026-09-27 12:53:39.316241','2026-10-02 12:11:01.547085','bg-sky-50 text-sky-700 border-sky-200','Tổ hợp biệt thự và tiện ích nghỉ dưỡng cao cấp.','/assets/images/uploads/villa_83359151.jpg',_binary '','ngọc trai','Ngọc Trai','NGỌC TRAI','/assets/images/rooms/villa-beachfront.jpg',1,'Gần bãi biển riêng Sầm Sơn,Hồ bơi vô cực nước ngọt,Quản gia và đầu bếp riêng 24/7,Sân vườn tổ chức tiệc BBQ ngoài trời','villa-ngoc-trai',NULL),(5,'2026-09-27 12:54:11.680369','2026-10-02 12:18:53.680321','bg-sky-50 text-sky-700 border-sky-200','Tổ hợp biệt thự và tiện ích nghỉ dưỡng cao cấp.','/assets/images/uploads/villa_7bf1e543.jpg',_binary '','sao biển','Sao Biển','SAO BIỂN','/assets/images/rooms/grand-oceanfront.jpg',2,'Tầm nhìn panorama hướng biển tuyệt mỹ,Nội thất gỗ óc chó cao cấp phong cách Modern Luxury,Bể sục Jacuzzi thư giãn trên ban công,Liền kề trung tâm ẩm thực & sân golf','villa-sao-bien',NULL),(6,'2026-09-27 12:54:20.161128','2026-10-02 12:17:10.641596','bg-sky-50 text-sky-700 border-sky-200','Tổ hợp biệt thự và tiện ích nghỉ dưỡng cao cấp.','/assets/images/uploads/villa_f2fa3f67.jpg',_binary '','san hô','San Hô','SAN HÔ','/assets/images/rooms/royal-penthouse.jpg',3,'Không gian biệt lập an tĩnh bên rặng dừa xanh,Khu vui chơi trẻ em riêng trong khuôn viên,Phòng chiếu phim & karaoke gia đình công nghệ cao,Sức chứa lớn lý tưởng cho đại gia đình và đoàn teambuilding','villa-san-ho',NULL);
/*!40000 ALTER TABLE `zones` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-07 18:23:54
