-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: project_4105
-- ------------------------------------------------------
-- Server version	8.4.7

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `course_schedules`
--

DROP TABLE IF EXISTS `course_schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_schedules` (
  `schedule_id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL,
  `section` varchar(10) DEFAULT NULL,
  `day_of_week` varchar(50) NOT NULL,
  `time_start` time DEFAULT NULL,
  `time_end` time DEFAULT NULL,
  `room` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`schedule_id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `course_schedules_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_schedules`
--

LOCK TABLES `course_schedules` WRITE;
/*!40000 ALTER TABLE `course_schedules` DISABLE KEYS */;
INSERT INTO `course_schedules` VALUES (9,6,'Lec.','Tuesday','09:30:00','11:20:00','SCL211/2'),(10,6,'Lab.','Tuesday','11:30:00','13:20:00','SCL211/2'),(11,7,NULL,'Monday','09:30:00','11:20:00','SCL201'),(12,8,NULL,'Tuesday','13:30:00','15:20:00','SCL204'),(13,9,'Lec.','Monday','09:30:00','11:20:00','SCL206'),(14,9,'Lab.','Monday','09:30:00','11:20:00','SCL206'),(16,11,'Lec.','Thrusday','11:30:00','13:20:00','SCL204'),(17,11,'Lab.','Thrusday','13:30:00','15:20:00','SCL211/2'),(18,12,'Lec.','Wednesday','09:30:00','11:20:00','SCL211/2'),(19,12,'Lab.','Wednesday','11:30:00','13:20:00','SCL211/2'),(20,13,'Lec.','Wednesday','11:30:00','13:20:00','SCL201'),(21,13,'Lab.','Wednesday','13:30:00','15:20:00','SCL201'),(22,14,'Lec.','Tuesday','13:30:00','15:20:00','SCL206'),(23,14,'Lab.','Tuesday','15:30:00','17:20:00','SCL206'),(24,15,NULL,'Monday','09:30:00','11:20:00','SCL201'),(25,16,NULL,'Friday','07:30:00','09:20:00','SCL204'),(26,17,NULL,'Monday','09:30:00','11:20:00','SCL211'),(27,18,'Lec.','Wednesday','13:30:00','15:20:00','SCL206'),(28,18,'Lab.','Wednesday','15:30:00','17:20:00','SCL206'),(29,19,'Lec.','Thrusday','13:30:00','15:20:00','SCL204'),(30,19,'Lab.','Thrusday','15:30:00','17:20:00','SCL211/2'),(31,20,'Lec.','Friday','09:30:00','11:20:00','SCL211/2'),(32,20,'Lab.','Friday','11:30:00','13:20:00','SCL211/2'),(33,21,'Lec.','Tuesday','13:30:00','15:20:00','SCL211'),(34,21,'Lab.','Tuesday','15:30:00','17:20:00','SCL211'),(35,22,NULL,'Wednesday','11:30:00','13:20:00','SCL211'),(36,23,NULL,'Wednesday','11:30:00','13:20:00','SCL206'),(37,24,'Lec.','Wednesday','13:30:00','15:20:00','SCL211'),(38,24,'Lab.','Wednesday','15:30:00','17:20:00','SCL211'),(39,25,NULL,'Contact Instructor',NULL,NULL,NULL),(40,26,'Lec.','Monday','13:30:00','15:20:00','SCL211/2'),(41,26,'Lab.','Monday','15:30:00','17:20:00','SCL211/2'),(42,27,'Lec.','Monday','09:30:00','11:20:00','SCL211/2'),(43,27,'Lab.','Monday','11:30:00','13:20:00','SCL211/2'),(44,28,'Lec.','Thrusday','09:30:00','11:20:00','SCL206'),(45,28,'Lab.','Thrusday','11:30:00','13:20:00','SCL206'),(46,29,'Lec.','Friday','09:30:00','11:20:00','SCL211'),(47,29,'Lab.','Friday','11:30:00','13:20:00','SCL211'),(48,30,NULL,'Wednesday','11:30:00','13:20:00','SCL205'),(49,31,NULL,'Wednesday','09:30:00','11:20:00','SCL205'),(50,33,NULL,'Wednesday','09:30:00','11:20:00','SCL206'),(51,34,NULL,'Tuesday','09:30:00','11:20:00','SCL205'),(52,35,NULL,'Thrusday','13:30:00','15:20:00','SCL211'),(53,36,'Lec.','Thrusday','09:30:00','11:20:00','SCL211/2'),(54,36,'Lec.','Thrusday','11:30:00','13:20:00','SCL211/2'),(55,37,NULL,'Thrusday','13:30:00','15:20:00','SCL205'),(56,32,'Sec. 1','Monday','11:30:00','00:00:13','SCL209'),(57,32,'Sec. 2','Monday','09:30:00','11:20:00','SCL209'),(58,32,'Sec. 3','Tuesday','11:30:00','13:20:00','SCL220'),(59,32,'Sec. 4','Wednesday','09:30:00','11:20:00','SCL226'),(60,32,'Sec. 5','Thrusday','11:30:00','13:20:00','SCL220'),(61,32,'Sec. 6','Monday','13:30:00','15:20:00','SCL220'),(62,32,'Sec. 7','Monday','11:30:00','13:20:00','SCL207'),(63,32,'Sec. 8','Wednesday','11:30:00','13:20:00','SCL226'),(64,32,'Sec. 9','Tuesday','11:30:00','13:20:00','SCL227'),(65,32,'Sec. 10','Wednesday','11:30:00','13:20:00','SCL226'),(66,32,'Sec. 11','Thrusday','09:30:00','11:20:00','SCL208'),(67,32,'Sec. 12','Wednesday','11:30:00','13:20:00','SCL220'),(68,32,'Sec. 13','Wednesday','09:30:00','11:20:00','SCL220'),(69,32,'Sec. 14','Tuesday','11:30:00','13:20:00','SCL220'),(71,10,NULL,'Tuesday','11:30:00','13:20:00','SCL211'),(80,2,NULL,'วันพุธ','09:30:00','11:20:00','SCL204'),(81,1,'Lab.','วันอังคาร','13:30:00','15:20:00','SCL204'),(82,1,'Lec.','วันอังคาร','09:30:00','11:20:00','SCL211/2'),(83,3,NULL,'วันอังคาร','11:30:00','13:20:00','SCL204'),(84,4,'Lec.','วันพฤหัสบดี','15:30:00','17:20:00','SCL204'),(85,4,'Lab.','วันศุกร์','09:30:00','11:20:00','SCL211/2'),(86,5,'Lab.','วันพุธ','15:30:00','17:20:00','SCL211/2'),(87,5,'Lec.','วันพุธ','13:30:00','15:20:00','SCL204');
/*!40000 ALTER TABLE `course_schedules` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-30 13:53:41
