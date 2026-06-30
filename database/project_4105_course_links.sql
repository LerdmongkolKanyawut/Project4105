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
-- Table structure for table `course_links`
--

DROP TABLE IF EXISTS `course_links`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_links` (
  `link_id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL,
  `label` varchar(100) NOT NULL,
  `url` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`link_id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `course_links_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_links`
--

LOCK TABLES `course_links` WRITE;
/*!40000 ALTER TABLE `course_links` DISABLE KEYS */;
INSERT INTO `course_links` VALUES (7,6,'Google Classroom',NULL),(8,6,'Line Group',NULL),(9,7,'Google Classroom',NULL),(10,7,'Line Group',NULL),(11,8,'Google Classroom',NULL),(12,8,'Line Group',NULL),(13,9,'Google Classroom',NULL),(14,9,'Line Group',NULL),(16,11,'Google Classroom',NULL),(17,11,'Line Group',NULL),(18,12,'Google Classroom',NULL),(19,12,'Line Group',NULL),(20,13,'Google Classroom',NULL),(21,13,'Line Group',NULL),(22,14,'Line Group',NULL),(23,15,'Google Classroom',NULL),(24,15,'Line Group',NULL),(25,16,'Google Classroom',NULL),(26,16,'Line Group',NULL),(27,17,'Google Classroom',NULL),(28,17,'Line Group',NULL),(29,18,'Line Group',NULL),(30,19,'Line Group',NULL),(31,20,'Google Classroom',NULL),(32,20,'Line Group',NULL),(33,21,'Google Classroom',NULL),(34,22,'Google Classroom',NULL),(35,23,'Google Classroom',NULL),(36,24,'Google Classroom',NULL),(37,24,'Line Group',NULL),(38,26,'Google Classroom',NULL),(39,26,'Line Group',NULL),(40,27,'Google Classroom',NULL),(41,27,'Line Group',NULL),(42,28,'Google Classroom',NULL),(43,28,'Line Group',NULL),(44,29,'Google Classroom',NULL),(45,29,'Line Group',NULL),(46,30,'Google Classroom',NULL),(47,30,'Line Group',NULL),(48,31,'Google Classroom',NULL),(49,31,'Line Group',NULL),(50,33,'Google Classroom',NULL),(51,33,'Line Group',NULL),(52,34,'Google Classroom',NULL),(53,34,'Line Group',NULL),(54,35,'Line Group',NULL),(55,36,'Google Classroom',NULL),(56,36,'Line Group',NULL),(57,37,'Google Classroom',NULL),(58,37,'Line Group',NULL);
/*!40000 ALTER TABLE `course_links` ENABLE KEYS */;
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
