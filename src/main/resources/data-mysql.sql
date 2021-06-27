CREATE TABLE IF NOT EXISTS `bootdb`.`error` (
  `ErrorId` INT NOT NULL,
  `ErrorDescription` VARCHAR(45) NOT NULL,
  `ErrorHTTPCode` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`ErrorId`),
  UNIQUE INDEX `ErrorId_UNIQUE` (`ErrorId` ASC) VISIBLE);


INSERT INTO `bootdb`.`error` (`ErrorId`,`ErrorDescription`,`ErrorHTTPCode`) 
VALUES (1,'Invalid projectName','451') AS new_foo
ON DUPLICATE KEY UPDATE `ErrorHTTPCode`=new_foo.`ErrorHTTPCode`;

INSERT INTO `bootdb`.`error` (`ErrorId`,`ErrorDescription`,`ErrorHTTPCode`) 
VALUES (2,'Invalid projectDescription','452') AS new_foo
ON DUPLICATE KEY UPDATE `ErrorHTTPCode`=new_foo.`ErrorHTTPCode`;

INSERT INTO `bootdb`.`error` (`ErrorId`,`ErrorDescription`,`ErrorHTTPCode`) 
VALUES (3,'Invalid startDate','453') AS new_foo
ON DUPLICATE KEY UPDATE `ErrorHTTPCode`=new_foo.`ErrorHTTPCode`;

INSERT INTO `bootdb`.`error` (`ErrorId`,`ErrorDescription`,`ErrorHTTPCode`) 
VALUES (4,'Invalid endDate','454') AS new_foo
ON DUPLICATE KEY UPDATE `ErrorHTTPCode`=new_foo.`ErrorHTTPCode`;

INSERT INTO `bootdb`.`error` (`ErrorId`,`ErrorDescription`,`ErrorHTTPCode`) 
VALUES (5,'startDate must be greater or equal to today','455') AS new_foo
ON DUPLICATE KEY UPDATE `ErrorHTTPCode`=new_foo.`ErrorHTTPCode`;

INSERT INTO `bootdb`.`error` (`ErrorId`,`ErrorDescription`,`ErrorHTTPCode`) 
VALUES (6,'endDate must be greater than startDate','456') AS new_foo
ON DUPLICATE KEY UPDATE `ErrorHTTPCode`=new_foo.`ErrorHTTPCode`;

INSERT INTO `bootdb`.`error` (`ErrorId`,`ErrorDescription`,`ErrorHTTPCode`) 
VALUES (7,'Invalid taskName','457') AS new_foo
ON DUPLICATE KEY UPDATE `ErrorHTTPCode`=new_foo.`ErrorHTTPCode`;

INSERT INTO `bootdb`.`error` (`ErrorId`,`ErrorDescription`,`ErrorHTTPCode`) 
VALUES (8,'Invalid taskDescription','458') AS new_foo
ON DUPLICATE KEY UPDATE `ErrorHTTPCode`=new_foo.`ErrorHTTPCode`;

INSERT INTO `bootdb`.`error` (`ErrorId`,`ErrorDescription`,`ErrorHTTPCode`) 
VALUES (9,'Invalid executionDate','459') AS new_foo
ON DUPLICATE KEY UPDATE `ErrorHTTPCode`=new_foo.`ErrorHTTPCode`;

INSERT INTO `bootdb`.`error` (`ErrorId`,`ErrorDescription`,`ErrorHTTPCode`) 
VALUES (10,'Invalid new endDate','460') AS new_foo
ON DUPLICATE KEY UPDATE `ErrorHTTPCode`=new_foo.`ErrorHTTPCode`;

INSERT INTO `bootdb`.`user` (`id`,`password`,`username`,`is_active`) 
VALUES (1, '$2y$12$AgV2YJRPaCXzCJGBCzFJ/uhqDLEysQwtQefZF72VPnY4rONFsEQr.','lozano',true) AS new_foo
ON DUPLICATE KEY UPDATE `is_active`=new_foo.`is_active`;

INSERT INTO `bootdb`.`role` (`id`,`description`,`name`) 
VALUES (1, 'Admin role', 'ADMIN') AS new_foo
ON DUPLICATE KEY UPDATE `description`=new_foo.`description`;

INSERT INTO `bootdb`.`role` (`id`,`description`,`name`) 
VALUES (2, 'Operator role', 'OPERATOR') AS new_foo
ON DUPLICATE KEY UPDATE `description`=new_foo.`description`;

INSERT INTO `bootdb`.`user_roles` (`user_id`,`role_id`) 
VALUES (1, 1) AS new_foo
ON DUPLICATE KEY UPDATE `role_id`=new_foo.`role_id`;