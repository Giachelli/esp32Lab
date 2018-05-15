-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: May 15, 2018 at 09:19 AM
-- Server version: 5.6.34-log
-- PHP Version: 7.1.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `progetto_esp`
--

-- --------------------------------------------------------

--
-- Table structure for table `dati_applicazione`
--

CREATE TABLE `dati_applicazione` (
  `MAC_ADDRESS` text CHARACTER SET armscii8 COLLATE armscii8_bin,
  `SSID` int(11) NOT NULL,
  `DATE` int(11) NOT NULL,
  `HASH` int(11) NOT NULL,
  `SIGNAL` int(11) NOT NULL,
  `ESP_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dati_applicazione`
--

INSERT INTO `dati_applicazione` (`MAC_ADDRESS`, `SSID`, `DATE`, `HASH`, `SIGNAL`, `ESP_ID`) VALUES
('12:34:54:65:45:42', 3774, 20180509, 3763764, 60, 1),
('12:34:54:65:45:42', 3774, 20180509, 3763764, 60, 1),
('12:67:90:09:09:89', 3554, 20170519, 3767444, 75, 2),
('56:76:87:67:65:56', 347467, 20180517, 34368, 49, 3),
('56:76:87:67:65:56', 347467, 20180517, 34368, 49, 3);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
