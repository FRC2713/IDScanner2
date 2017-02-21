# IDScanner
Built way too late at night

To use this, clone the respositoy to any directory, set up a local Mysql database, open up Main.java, and change the final variables to your values. Next step is to set up the database. Execute the following sql commands:

``CREATE SCHEMA `Members` DEFAULT CHARACTER SET utf8;``

``CREATE TABLE `Members`.`memberAttendance` (
  `memberId` varchar(45) NOT NULL,
  `date` varchar(15) NOT NULL,
  `time` double NOT NULL,
  PRIMARY KEY (`time`),
  UNIQUE KEY `time_UNIQUE` (`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8``

``CREATE TABLE `Members`.`members` (
  `memberId` varchar(45) NOT NULL,
  `memberName` varchar(45) NOT NULL,
  PRIMARY KEY (`memberId`),
  UNIQUE KEY `memberId_UNIQUE` (`memberId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8``



(In the following commands use : for Unix and ; for windows)

Then, in your terminal or command prompt, navigate to the directory you cloned the files and run the commands:

`javac -classpath .:lib/mysql-connector-java-5.1.40-bin.jar Main.java`  and

`java -classpath .:lib/mysql-connector-java-5.1.40-bin.jar Main`
