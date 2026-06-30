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
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `course_id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(7) NOT NULL,
  `name` varchar(100) NOT NULL,
  `credit` int NOT NULL,
  `pre_requiste` varchar(20) DEFAULT 'None',
  `description` text,
  `exam_note` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`course_id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (1,'COS1101','Introduction to Computer Science',3,'None','-','วันอาทิตย์ 25/10/2026, 14:00 - 16:30'),(2,'COS1102','Discrete Structures',3,'None','-','วันอังคาร 20/10/2026, 09:30 - 12:00'),(3,'COS1103','Algorithms and Programming Concepts',3,'COS1101','-','วันพุธ 28/10/2026, 09:30 - 12:00'),(4,'COS2101','Procedural Programming',3,'COS1103','-','วันเสาร์ 24/10/2026,14:00 - 16:30'),(5,'COS2102','Object-Oriented Programming',3,'COS2101','-','Exam Commit'),(6,'COS2103','Data Structures and Algorithms',3,'COS2101','-','Wednesday 21/10/2026, 09:30 - 12:00'),(7,'COS2104','Computer Organization and Assembly Language',3,'COS1103','-','Sunday 25/10/2026,09:30-12:00'),(8,'COS2105','Theory of Computation',3,'COS1102','-','Wednesday 14/10/2026,09:30-12:00'),(9,'COS2107','Human Computer Interaction',3,'COS1102','-','Exam Commit'),(10,'COS2108','Computer Organization and Architectures',3,'COS1103','-','Exam Commit'),(11,'COS2204','Web Programming',3,'COS1103','-','Exam Commit'),(12,'COS2208','Java Programming',3,'COS1103','-','Exam Commit'),(13,'COS2210','Python Programming',3,'COS1103','-','Exam Commit'),(14,'COS2212','Swift Programming',3,'COS1103','-','Exam Commit'),(15,'COS2213','Assembly Langauge Programming',3,'COS1103','-','Exam Commit'),(16,'COS3101','Numerical Method',3,'COS1103','-','Exam Commit'),(17,'COS3102','Computer Architectures',3,'COS2104','-','Saturday 17/10/2026, 09:30 - 12:00'),(18,'COS3103','Database Systems',3,'COS2103','-','Exam Commit'),(19,'COS3104','Programming Languages',3,'COS2105','-','Saturday 17/10/2026, 14:00 - 16:30'),(20,'COS3105','Operating Systems',3,'COS2108','-','Monday 19/10/202, 09:30 - 12:00'),(21,'COS3106','Computer network',3,'COS2108','-','Saturday 24/10/2026, 09:30 - 12:00'),(22,'COS3107','Information Management',3,'COS3103','-','Exam Commit'),(23,'COS3108','System Analysis and Design',3,'COS3103','-','Exam Commit'),(24,'COS3109','Artificial Intelligence',3,'COS2103','-','Exam Commit'),(25,'COS3110','Job Training',0,'None','-','Exam Commit'),(26,'COS3302','Applied Data Science',3,'None','-','Exam Commit'),(27,'COS3401','Digital Image Processing',3,'COS3101','-','Exam Commit'),(28,'COS4101','Seftware Engineering',3,'COS3104 & COS3108','-','Exam Commit'),(29,'COS4102','Computer Graphics',3,'COS1102','-','Exam Commit'),(30,'COS4103','Computational Science',3,'COS3109 & COS3101','-','Exam Commit'),(31,'COS4104','Seminar',1,'None','-','Exam Commit'),(32,'COS4105','Special Projects',3,'None','-','Exam Commit'),(33,'COS4106','Social and Professional Ethics',3,'None','-','Exam Commit'),(34,'COS4310','Data Mining',3,'None','-','Exam Commit'),(35,'COS4311','Pattern and Software Design',3,'None','-','Exam Commit'),(36,'COS4312','Deep Learning',3,'None','-','Exam Commit'),(37,'COS4502','Algorithm Design and Analysis',3,'COS2103','-','Exam Commit');
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-30 13:53:40
