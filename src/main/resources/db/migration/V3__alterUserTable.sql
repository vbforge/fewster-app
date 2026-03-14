ALTER TABLE users
    CHANGE COLUMN `role` `role` VARCHAR(255) NOT NULL ,
    CHANGE COLUMN `username` `username` VARCHAR(255) NOT NULL ,
    CHANGE COLUMN `password` `password` VARCHAR(255) NOT NULL ;
