-- phpMyAdmin SQL Dump
-- version 5.1.3
-- https://www.phpmyadmin.net/
--
-- Host: 192.168.178.112:3307
-- Generation Time: Apr 14, 2022 at 04:34 PM
-- Server version: 8.0.28
-- PHP Version: 8.0.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `lukos`
--

-- --------------------------------------------------------

--
-- Table structure for table `ActionLogs`
--

CREATE TABLE `ActionLogs` (
  `messageID` int NOT NULL,
  `actionID` int NOT NULL,
  `receiverID` int NOT NULL,
  `status` enum('LOCKED','NOT_SENT','SENT') NOT NULL,
  `messageType` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ActionLogsData`
--

CREATE TABLE `ActionLogsData` (
  `messageID` int NOT NULL,
  `position` int NOT NULL,
  `data` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Actions`
--

CREATE TABLE `Actions` (
  `actionID` int NOT NULL,
  `instanceID` int NOT NULL,
  `userID` int,
  `time` datetime NOT NULL,
  `name` text NOT NULL,
  `status` enum('NOT_EXECUTED','EXECUTED','COMPLETED') NOT NULL,
  `targetType` enum('PLAYER','LOCATION','BOTH') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ActionTargetLocation`
--

CREATE TABLE `ActionTargetLocation` (
  `actionID` int NOT NULL,
  `targetLocationID` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ActionTargetPlayers`
--

CREATE TABLE `ActionTargetPlayers` (
  `actionID` int NOT NULL,
  `targetUserID` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Ballots`
--

CREATE TABLE `Ballots` (
  `userID` int NOT NULL,
  `instanceID` int NOT NULL,
  `voteID` int NOT NULL,
  `targetID` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Bridge`
--

CREATE TABLE `Bridge` (
  `bridgeID` int NOT NULL,
  `instanceID` int NOT NULL,
  `name` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ChatInstance`
--

CREATE TABLE `ChatInstance` (
  `chatID` int NOT NULL,
  `instanceID` int NOT NULL,
  `chatType` enum('GENERAL','DECEASED','WOLVES','CULT','GOSSIP','LOVERS') NOT NULL,
  `isOpen` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ChatMembers`
--

CREATE TABLE `ChatMembers` (
  `userID` int NOT NULL,
  `chatID` int NOT NULL,
  `writeAccess` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ChatMessages`
--

CREATE TABLE `ChatMessages` (
  `messageID` int NOT NULL,
  `chatID` int NOT NULL,
  `timeSent` datetime NOT NULL,
  `userID` int NOT NULL,
  `message` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Instance`
--

CREATE TABLE `Instance` (
  `instanceID` int NOT NULL,
  `name` varchar(25) NOT NULL,
  `day` int DEFAULT NULL,
  `dayPhase` enum('MORNING','DAY','VOTE','EXECUTION','EVENING','NIGHT') DEFAULT NULL,
  `gameMasterID` int NOT NULL,
  `undecidedLynches` int DEFAULT '0',
  `seed` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `InstanceVotes`
--

CREATE TABLE `InstanceVotes` (
  `voteID` int NOT NULL,
  `instanceID` int NOT NULL,
  `voteType` enum('LYNCH','MAYOR','ALPHA_WOLF','MISC') NOT NULL,
  `started` tinyint(1) DEFAULT NULL,
  `ended` tinyint(1) DEFAULT NULL,
  `allowed` json DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Location`
--

CREATE TABLE `Location` (
  `locationID` int NOT NULL,
  `instanceID` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `PlayerItems`
--

CREATE TABLE `PlayerItems` (
  `itemID` int NOT NULL,
  `userID` int NOT NULL,
  `instanceID` int NOT NULL,
  `item` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Players`
--

CREATE TABLE `Players` (
  `instanceID` int NOT NULL,
  `userID` int NOT NULL,
  `houseID` int DEFAULT NULL,
  `houseState` enum('BURNED','REPAIRED','SOAKED','CLEANED') DEFAULT NULL,
  `houseStateDay` int DEFAULT NULL,
  `alive` enum('ALIVE','DECEASED','NOT_STARTED') NOT NULL,
  `currentLocation` int DEFAULT NULL,
  `coupleUID` int DEFAULT NULL,
  `coupleIID` int DEFAULT NULL,
  `toBeExecuted` tinyint(1) DEFAULT NULL,
  `muted` tinyint(1) NOT NULL DEFAULT '0',
  `deathNote` text,
  `deathnoteIsChangeable` tinyint(1) NOT NULL DEFAULT '1',
  `isProtected` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Roles`
--

CREATE TABLE `Roles` (
  `userID` int NOT NULL,
  `instanceID` int NOT NULL,
  `purposeType` enum('mainRole','doubleRole','job') NOT NULL,
  `purpose` varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Successor`
--

CREATE TABLE `Successor` (
  `instanceID` int NOT NULL,
  `successorType` enum('MAYOR','ALPHA_WOLF') NOT NULL,
  `userID` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `TiedPlayers`
--

CREATE TABLE `TiedPlayers` (
  `userID` int NOT NULL,
  `instanceID` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE `Users` (
  `userID` int NOT NULL,
  `sub` text NOT NULL,
  `issuer` text NOT NULL,
  `username` text NOT NULL,
  `wins` int NOT NULL,
  `losses` int NOT NULL,
  `last_login` datetime NOT NULL,
  `last_logout` datetime NOT NULL,
  `playtime` int NOT NULL,
  `toBeDeleted` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `UserStats`
--

CREATE TABLE `UserStats` (
  `userID` int NOT NULL,
  `purpose` varchar(32) NOT NULL,
  `gamesPlayed` int NOT NULL,
  `wins` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ActionLogs`
--
ALTER TABLE `ActionLogs`
  ADD PRIMARY KEY (`messageID`) USING BTREE,
  ADD KEY `actionID` (`actionID`),
  ADD KEY `receiverID` (`receiverID`);

--
-- Indexes for table `ActionLogsData`
--
ALTER TABLE `ActionLogsData`
  ADD PRIMARY KEY (`messageID`,`position`) USING BTREE;

--
-- Indexes for table `Actions`
--
ALTER TABLE `Actions`
  ADD PRIMARY KEY (`actionID`) USING BTREE,
  ADD UNIQUE KEY `actionID` (`actionID`),
  ADD KEY `userID` (`userID`),
  ADD KEY `instanceID` (`instanceID`);

--
-- Indexes for table `ActionTargetLocation`
--
ALTER TABLE `ActionTargetLocation`
  ADD PRIMARY KEY (`actionID`);

--
-- Indexes for table `ActionTargetPlayers`
--
ALTER TABLE `ActionTargetPlayers`
  ADD PRIMARY KEY (`actionID`, `targetUserID`);

--
-- Indexes for table `Ballots`
--
ALTER TABLE `Ballots`
  ADD PRIMARY KEY (`instanceID`,`userID`,`voteID`),
  ADD KEY `voteID` (`voteID`),
  ADD KEY `userID` (`userID`);

--
-- Indexes for table `Bridge`
--
ALTER TABLE `Bridge`
  ADD PRIMARY KEY (`bridgeID`),
  ADD KEY `instanceID` (`instanceID`);

--
-- Indexes for table `ChatInstance`
--
ALTER TABLE `ChatInstance`
  ADD PRIMARY KEY (`chatID`),
  ADD KEY `instanceID` (`instanceID`);

--
-- Indexes for table `ChatMembers`
--
ALTER TABLE `ChatMembers`
  ADD PRIMARY KEY (`userID`,`chatID`) USING BTREE,
  ADD KEY `chatID` (`chatID`);

--
-- Indexes for table `ChatMessages`
--
ALTER TABLE `ChatMessages`
  ADD PRIMARY KEY (`messageID`) USING BTREE,
  ADD KEY `userID` (`userID`,`timeSent`,`chatID`) USING BTREE,
  ADD KEY `chatID` (`chatID`);

--
-- Indexes for table `Instance`
--
ALTER TABLE `Instance`
  ADD PRIMARY KEY (`instanceID`);

--
-- Indexes for table `InstanceVotes`
--
ALTER TABLE `InstanceVotes`
  ADD PRIMARY KEY (`voteID`),
  ADD KEY `instanceID` (`instanceID`);

--
-- Indexes for table `Location`
--
ALTER TABLE `Location`
  ADD PRIMARY KEY (`locationID`),
  ADD KEY `instanceID` (`instanceID`);

--
-- Indexes for table `PlayerItems`
--
ALTER TABLE `PlayerItems`
  ADD PRIMARY KEY (`itemID`) USING BTREE,
  ADD KEY `userID` (`userID`),
  ADD KEY `instanceID` (`instanceID`);

--
-- Indexes for table `Players`
--
ALTER TABLE `Players`
  ADD PRIMARY KEY (`instanceID`,`userID`),
  ADD KEY `userID` (`userID`),
  ADD KEY `houseID` (`houseID`);

--
-- Indexes for table `Roles`
--
ALTER TABLE `Roles`
  ADD PRIMARY KEY (`instanceID`,`userID`,`purpose`),
  ADD KEY `userID` (`userID`);

--
-- Indexes for table `Successor`
--
ALTER TABLE `Successor`
  ADD PRIMARY KEY (`instanceID`,`successorType`);

--
-- Indexes for table `TiedPlayers`
--
ALTER TABLE `TiedPlayers`
  ADD PRIMARY KEY (`userID`,`instanceID`),
  ADD KEY `instanceID` (`instanceID`);

--
-- Indexes for table `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`userID`);

--
-- Indexes for table `UserStats`
--
ALTER TABLE `UserStats`
  ADD PRIMARY KEY (`purpose`,`userID`),
  ADD KEY `userID` (`userID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ActionLogs`
--
ALTER TABLE `ActionLogs`
  MODIFY `messageID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Actions`
--
ALTER TABLE `Actions`
  MODIFY `actionID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ChatInstance`
--
ALTER TABLE `ChatInstance`
  MODIFY `chatID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ChatMessages`
--
ALTER TABLE `ChatMessages`
  MODIFY `messageID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Instance`
--
ALTER TABLE `Instance`
  MODIFY `instanceID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `InstanceVotes`
--
ALTER TABLE `InstanceVotes`
  MODIFY `voteID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Location`
--
ALTER TABLE `Location`
  MODIFY `locationID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `PlayerItems`
--
ALTER TABLE `PlayerItems`
  MODIFY `itemID` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Users`
--
ALTER TABLE `Users`
  MODIFY `userID` int NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `ActionLogs`
--
ALTER TABLE `ActionLogs`
  ADD CONSTRAINT `ActionLogs_ibfk_1` FOREIGN KEY (`actionID`) REFERENCES `Actions` (`actionID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `ActionLogsData`
--
ALTER TABLE `ActionLogsData`
  ADD CONSTRAINT `ActionLogsData_ibfk_1` FOREIGN KEY (`messageID`) REFERENCES `ActionLogs` (`messageID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Actions`
--
ALTER TABLE `Actions`
  ADD CONSTRAINT `Actions_ibfk_1` FOREIGN KEY (`instanceID`) REFERENCES `Players` (`instanceID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Actions_ibfk_2` FOREIGN KEY (`userID`) REFERENCES `Players` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `ActionTargetLocation`
--
ALTER TABLE `ActionTargetLocation`
  ADD CONSTRAINT `ActionTargetLocation_ibfk_1` FOREIGN KEY (`actionID`) REFERENCES `Actions` (`actionID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `ActionTargetPlayers`
--
ALTER TABLE `ActionTargetPlayers`
  ADD CONSTRAINT `ActionTargetPlayers_ibfk_1` FOREIGN KEY (`actionID`) REFERENCES `Actions` (`actionID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Ballots`
--
ALTER TABLE `Ballots`
  ADD CONSTRAINT `Ballots_ibfk_3` FOREIGN KEY (`voteID`) REFERENCES `InstanceVotes` (`voteID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Ballots_ibfk_4` FOREIGN KEY (`instanceID`) REFERENCES `Players` (`instanceID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Ballots_ibfk_5` FOREIGN KEY (`userID`) REFERENCES `Players` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Bridge`
--
ALTER TABLE `Bridge`
  ADD CONSTRAINT `Bridge_ibfk_2` FOREIGN KEY (`bridgeID`) REFERENCES `Location` (`locationID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Bridge_ibfk_3` FOREIGN KEY (`instanceID`) REFERENCES `Location` (`instanceID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `ChatInstance`
--
ALTER TABLE `ChatInstance`
  ADD CONSTRAINT `ChatInstance_ibfk_1` FOREIGN KEY (`instanceID`) REFERENCES `Instance` (`instanceID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `ChatMembers`
--
ALTER TABLE `ChatMembers`
  ADD CONSTRAINT `ChatMembers_ibfk_1` FOREIGN KEY (`chatID`) REFERENCES `ChatInstance` (`chatID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `ChatMessages`
--
ALTER TABLE `ChatMessages`
  ADD CONSTRAINT `ChatMessages_ibfk_5` FOREIGN KEY (`chatID`) REFERENCES `ChatInstance` (`chatID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `InstanceVotes`
--
ALTER TABLE `InstanceVotes`
  ADD CONSTRAINT `InstanceVotes_ibfk_1` FOREIGN KEY (`instanceID`) REFERENCES `Instance` (`instanceID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Location`
--
ALTER TABLE `Location`
  ADD CONSTRAINT `Location_ibfk_1` FOREIGN KEY (`instanceID`) REFERENCES `Instance` (`instanceID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `PlayerItems`
--
ALTER TABLE `PlayerItems`
  ADD CONSTRAINT `PlayerItems_ibfk_1` FOREIGN KEY (`instanceID`) REFERENCES `Players` (`instanceID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `PlayerItems_ibfk_2` FOREIGN KEY (`userID`) REFERENCES `Players` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Players`
--
ALTER TABLE `Players`
  ADD CONSTRAINT `Players_ibfk_1` FOREIGN KEY (`instanceID`) REFERENCES `Instance` (`instanceID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Players_ibfk_2` FOREIGN KEY (`userID`) REFERENCES `Users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Players_ibfk_3` FOREIGN KEY (`houseID`) REFERENCES `Location` (`locationID`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `Roles`
--
ALTER TABLE `Roles`
  ADD CONSTRAINT `Roles_ibfk_1` FOREIGN KEY (`instanceID`) REFERENCES `Players` (`instanceID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Roles_ibfk_2` FOREIGN KEY (`userID`) REFERENCES `Players` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Successor`
--
ALTER TABLE `Successor`
  ADD CONSTRAINT `Successor_ibfk_1` FOREIGN KEY (`instanceID`) REFERENCES `Instance` (`instanceID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `TiedPlayers`
--
ALTER TABLE `TiedPlayers`
  ADD CONSTRAINT `TiedPlayers_ibfk_1` FOREIGN KEY (`instanceID`) REFERENCES `Instance` (`instanceID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `UserStats`
--
ALTER TABLE `UserStats`
  ADD CONSTRAINT `UserStats_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `Users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
