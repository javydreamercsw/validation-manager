SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `validation_manager` ;
CREATE SCHEMA IF NOT EXISTS `validation_manager` DEFAULT CHARACTER SET latin1 ;
USE `validation_manager` ;

-- -----------------------------------------------------
-- Table `validation_manager`.`test_project`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`test_project` (
  `id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `active` TINYINT(1) NOT NULL DEFAULT 1 ,
  `notes` TEXT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `unique` (`name` ASC) )
ENGINE = InnoDB
COMMENT = 'In some scenarios this is considered a validation project.';


-- -----------------------------------------------------
-- Table `validation_manager`.`test_plan`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`test_plan` (
  `id` INT UNSIGNED NOT NULL ,
  `test_project_id` INT NOT NULL ,
  `notes` TEXT NULL ,
  `active` TINYINT(1) NOT NULL DEFAULT 1 ,
  `is_open` TINYINT(1) NOT NULL DEFAULT 1 ,
  `regression_test_plan_id` INT UNSIGNED NULL ,
  `regression_test_plan_test_project_id` INT NULL ,
  PRIMARY KEY (`id`, `test_project_id`) ,
  INDEX `fk_test_plan_test_project_idx` (`test_project_id` ASC) ,
  INDEX `fk_test_plan_test_plan1_idx` (`regression_test_plan_id` ASC, `regression_test_plan_test_project_id` ASC) ,
  CONSTRAINT `fk_test_plan_test_project`
    FOREIGN KEY (`test_project_id` )
    REFERENCES `validation_manager`.`test_project` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_plan_test_plan1`
    FOREIGN KEY (`regression_test_plan_id` , `regression_test_plan_test_project_id` )
    REFERENCES `validation_manager`.`test_plan` (`id` , `test_project_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'In some scenarios this is considered a validation plan.';


-- -----------------------------------------------------
-- Table `validation_manager`.`user_status`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`user_status` (
  `id` INT NOT NULL ,
  `status` VARCHAR(45) NOT NULL ,
  `description` VARCHAR(45) NULL ,
  UNIQUE INDEX `unique` (`status` ASC) ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`vm_user`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`vm_user` (
  `id` INT NOT NULL ,
  `username` VARCHAR(45) NOT NULL ,
  `password` VARCHAR(45) NOT NULL ,
  `email` VARCHAR(100) NOT NULL ,
  `first` VARCHAR(45) NOT NULL ,
  `last` VARCHAR(45) NOT NULL ,
  `locale` VARCHAR(10) NOT NULL DEFAULT 'en' ,
  `last_modified` DATETIME NOT NULL ,
  `attempts` INT NOT NULL DEFAULT 0 ,
  `user_status_id` INT NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_vm_user_user_status1_idx` (`user_status_id` ASC) ,
  CONSTRAINT `fk_vm_user_user_status1`
    FOREIGN KEY (`user_status_id` )
    REFERENCES `validation_manager`.`user_status` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`test`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`test` (
  `id` INT NOT NULL ,
  `notes` TEXT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `purpose` TEXT NOT NULL ,
  `scope` TEXT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
COMMENT = 'In some scenarios this is considered a validation protocol.';


-- -----------------------------------------------------
-- Table `validation_manager`.`test_case`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`test_case` (
  `id` INT UNSIGNED NOT NULL ,
  `test_id` INT NOT NULL ,
  `version` SMALLINT(5) UNSIGNED NOT NULL DEFAULT 1 ,
  `summary` TEXT NULL ,
  `expected_results` TEXT NULL ,
  `creation_date` DATETIME NOT NULL ,
  `active` TINYINT(1) NULL DEFAULT 1 ,
  `is_open` TINYINT(1) NULL DEFAULT 1 ,
  `author_id` INT NOT NULL ,
  PRIMARY KEY (`id`, `test_id`) ,
  INDEX `fk_test_case_user1_idx` (`author_id` ASC) ,
  INDEX `fk_test_case_Test1_idx` (`test_id` ASC) ,
  CONSTRAINT `fk_test_case_user1`
    FOREIGN KEY (`author_id` )
    REFERENCES `validation_manager`.`vm_user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_case_Test1`
    FOREIGN KEY (`test_id` )
    REFERENCES `validation_manager`.`test` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`test_plan_has_test`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`test_plan_has_test` (
  `test_plan_id` INT UNSIGNED NOT NULL ,
  `test_plan_test_project_id` INT NOT NULL ,
  `test_id` INT NOT NULL ,
  `start_date` DATETIME NOT NULL ,
  `end_date` DATETIME NULL ,
  `node_order` INT UNSIGNED NOT NULL DEFAULT 1 ,
  PRIMARY KEY (`test_plan_id`, `test_plan_test_project_id`, `test_id`) ,
  INDEX `fk_test_plan_has_Test_test_plan1_idx` (`test_plan_id` ASC, `test_plan_test_project_id` ASC) ,
  INDEX `fk_test_plan_has_Test_Test1_idx` (`test_id` ASC) ,
  CONSTRAINT `fk_test_plan_has_Test_test_plan1`
    FOREIGN KEY (`test_plan_id` , `test_plan_test_project_id` )
    REFERENCES `validation_manager`.`test_plan` (`id` , `test_project_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_plan_has_Test_Test1`
    FOREIGN KEY (`test_id` )
    REFERENCES `validation_manager`.`test` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`role`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`role` (
  `id` INT NOT NULL ,
  `description` VARCHAR(45) NOT NULL ,
  `notes` TEXT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`user_right`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`user_right` (
  `id` INT NOT NULL ,
  `description` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`role_has_right`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`role_has_right` (
  `role_id` INT NOT NULL ,
  `right_id` INT NOT NULL ,
  PRIMARY KEY (`role_id`, `right_id`) ,
  INDEX `fk_role_has_right_role1_idx` (`role_id` ASC) ,
  INDEX `fk_role_has_right_right1_idx` (`right_id` ASC) ,
  CONSTRAINT `fk_role_has_right_role1`
    FOREIGN KEY (`role_id` )
    REFERENCES `validation_manager`.`role` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_role_has_right_right1`
    FOREIGN KEY (`right_id` )
    REFERENCES `validation_manager`.`user_right` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`user_has_role`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`user_has_role` (
  `user_id` INT NOT NULL ,
  `role_id` INT NOT NULL ,
  PRIMARY KEY (`user_id`, `role_id`) ,
  INDEX `fk_user_has_role_user1_idx` (`user_id` ASC) ,
  INDEX `fk_user_has_role_role1_idx` (`role_id` ASC) ,
  CONSTRAINT `fk_user_has_role_user1`
    FOREIGN KEY (`user_id` )
    REFERENCES `validation_manager`.`vm_user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_role_role1`
    FOREIGN KEY (`role_id` )
    REFERENCES `validation_manager`.`role` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`user_test_plan_role`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`user_test_plan_role` (
  `test_plan_id` INT UNSIGNED NOT NULL ,
  `test_plan_test_project_id` INT NOT NULL ,
  `user_id` INT NOT NULL ,
  `role_id` INT NOT NULL ,
  PRIMARY KEY (`test_plan_id`, `test_plan_test_project_id`, `user_id`, `role_id`) ,
  INDEX `fk_test_plan_has_user_test_plan1_idx` (`test_plan_id` ASC, `test_plan_test_project_id` ASC) ,
  INDEX `fk_test_plan_has_user_user1_idx` (`user_id` ASC) ,
  INDEX `fk_user_test_plan_role_role1_idx` (`role_id` ASC) ,
  CONSTRAINT `fk_test_plan_has_user_test_plan1`
    FOREIGN KEY (`test_plan_id` , `test_plan_test_project_id` )
    REFERENCES `validation_manager`.`test_plan` (`id` , `test_project_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_plan_has_user_user1`
    FOREIGN KEY (`user_id` )
    REFERENCES `validation_manager`.`vm_user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_test_plan_role_role1`
    FOREIGN KEY (`role_id` )
    REFERENCES `validation_manager`.`role` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`user_test_project_role`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`user_test_project_role` (
  `test_project_id` INT NOT NULL ,
  `user_id` INT NOT NULL ,
  `role_id` INT NOT NULL ,
  PRIMARY KEY (`test_project_id`, `user_id`, `role_id`) ,
  INDEX `fk_test_project_has_user_test_project1_idx` (`test_project_id` ASC) ,
  INDEX `fk_test_project_has_user_user1_idx` (`user_id` ASC) ,
  INDEX `fk_user_test_project_role_role1_idx` (`role_id` ASC) ,
  CONSTRAINT `fk_test_project_has_user_test_project1`
    FOREIGN KEY (`test_project_id` )
    REFERENCES `validation_manager`.`test_project` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_test_project_has_user_user1`
    FOREIGN KEY (`user_id` )
    REFERENCES `validation_manager`.`vm_user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_test_project_role_role1`
    FOREIGN KEY (`role_id` )
    REFERENCES `validation_manager`.`role` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`assigment_type`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`assigment_type` (
  `id` INT NOT NULL ,
  `fk_table` VARCHAR(45) NOT NULL ,
  `description` TEXT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`assignment_status`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`assignment_status` (
  `id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `description` TEXT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`user_assigment`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`user_assigment` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `assigner_id` INT NOT NULL ,
  `assigment_type_id` INT NOT NULL ,
  `assignment_status_id` INT NOT NULL ,
  `assignee_id` INT NOT NULL ,
  `deadline` DATETIME NULL ,
  `creation_time` DATETIME NOT NULL ,
  PRIMARY KEY (`id`, `assigner_id`, `assigment_type_id`, `assignment_status_id`) ,
  INDEX `fk_user_assigment_user1_idx` (`assigner_id` ASC) ,
  INDEX `fk_user_assigment_user2_idx` (`assignee_id` ASC) ,
  INDEX `fk_user_assigment_assigment_type1_idx` (`assigment_type_id` ASC) ,
  INDEX `fk_user_assigment_assignment_status1_idx` (`assignment_status_id` ASC) ,
  CONSTRAINT `fk_user_assigment_user1`
    FOREIGN KEY (`assigner_id` )
    REFERENCES `validation_manager`.`vm_user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_assigment_user2`
    FOREIGN KEY (`assignee_id` )
    REFERENCES `validation_manager`.`vm_user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_assigment_assigment_type1`
    FOREIGN KEY (`assigment_type_id` )
    REFERENCES `validation_manager`.`assigment_type` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_assigment_assignment_status1`
    FOREIGN KEY (`assignment_status_id` )
    REFERENCES `validation_manager`.`assignment_status` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`step`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`step` (
  `id` INT NOT NULL ,
  `test_case_id` INT UNSIGNED NOT NULL ,
  `test_case_test_id` INT NOT NULL ,
  `step_sequence` INT NOT NULL DEFAULT 1 ,
  `text` VARCHAR(100) NOT NULL ,
  `notes` TEXT NULL ,
  PRIMARY KEY (`id`, `test_case_id`, `test_case_test_id`) ,
  INDEX `fk_step_test_case1_idx` (`test_case_id` ASC, `test_case_test_id` ASC) ,
  CONSTRAINT `fk_step_test_case1`
    FOREIGN KEY (`test_case_id` , `test_case_test_id` )
    REFERENCES `validation_manager`.`test_case` (`id` , `test_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`requirement_type`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`requirement_type` (
  `id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `description` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`requirement_status`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`requirement_status` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `status` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`project`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`project` (
  `id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `notes` TEXT NULL ,
  `parent_project_id` INT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_project_project1_idx` (`parent_project_id` ASC) ,
  CONSTRAINT `fk_project_project1`
    FOREIGN KEY (`parent_project_id` )
    REFERENCES `validation_manager`.`project` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`spec_level`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`spec_level` (
  `id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `description` TEXT NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`requirement_spec`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`requirement_spec` (
  `id` INT NOT NULL ,
  `project_id` INT NOT NULL ,
  `spec_level_id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `description` TEXT NULL ,
  `version` INT NOT NULL DEFAULT 1 ,
  `modificationDate` DATETIME NOT NULL ,
  PRIMARY KEY (`id`, `project_id`, `spec_level_id`) ,
  INDEX `fk_requirement_spec_product1_idx` (`project_id` ASC) ,
  UNIQUE INDEX `product-spec` (`project_id` ASC, `name` ASC) ,
  INDEX `fk_requirement_spec_spec_level1_idx` (`spec_level_id` ASC) ,
  CONSTRAINT `fk_requirement_spec_product1`
    FOREIGN KEY (`project_id` )
    REFERENCES `validation_manager`.`project` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_requirement_spec_spec_level1`
    FOREIGN KEY (`spec_level_id` )
    REFERENCES `validation_manager`.`spec_level` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`requirement_spec_node`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`requirement_spec_node` (
  `id` INT NOT NULL ,
  `requirement_spec_id` INT NOT NULL ,
  `requirement_spec_project_id` INT NOT NULL ,
  `requirement_spec_spec_level_id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `description` TEXT NULL ,
  `scope` TEXT NULL ,
  `parent_requirement_spec_node_requirement_spec_id` INT NULL ,
  `requirement_spec_node_id` INT NULL ,
  `requirement_spec_node_requirement_spec_id` INT NULL ,
  `requirement_spec_node_requirement_spec_project_id` INT NULL ,
  `requirement_spec_node_requirement_spec_spec_level_id` INT NULL ,
  PRIMARY KEY (`id`, `requirement_spec_id`, `requirement_spec_project_id`, `requirement_spec_spec_level_id`) ,
  INDEX `fk_requirement_spec_node_requirement_spec1_idx` (`requirement_spec_id` ASC, `requirement_spec_project_id` ASC, `requirement_spec_spec_level_id` ASC) ,
  INDEX `fk_requirement_spec_node_requirement_spec_node1_idx` (`requirement_spec_node_id` ASC, `requirement_spec_node_requirement_spec_id` ASC, `requirement_spec_node_requirement_spec_project_id` ASC, `requirement_spec_node_requirement_spec_spec_level_id` ASC) ,
  CONSTRAINT `fk_requirement_spec_node_requirement_spec1`
    FOREIGN KEY (`requirement_spec_id` , `requirement_spec_project_id` , `requirement_spec_spec_level_id` )
    REFERENCES `validation_manager`.`requirement_spec` (`id` , `project_id` , `spec_level_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_requirement_spec_node_requirement_spec_node1`
    FOREIGN KEY (`requirement_spec_node_id` , `requirement_spec_node_requirement_spec_id` , `requirement_spec_node_requirement_spec_project_id` , `requirement_spec_node_requirement_spec_spec_level_id` )
    REFERENCES `validation_manager`.`requirement_spec_node` (`id` , `requirement_spec_id` , `requirement_spec_project_id` , `requirement_spec_spec_level_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`requirement`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`requirement` (
  `id` INT NOT NULL ,
  `version` INT NOT NULL DEFAULT 1 ,
  `requirement_type_id` INT NOT NULL ,
  `unique_id` VARCHAR(45) NOT NULL ,
  `description` TEXT NOT NULL ,
  `notes` TEXT NULL ,
  `requirement_status_id` INT NOT NULL ,
  `requirement_spec_node_id` INT NOT NULL ,
  `requirement_spec_node_requirement_spec_id` INT NOT NULL ,
  `requirement_spec_node_requirement_spec_project_id` INT NOT NULL ,
  `requirement_spec_node_requirement_spec_spec_level_id` INT NOT NULL ,
  UNIQUE INDEX `unique_id` (`unique_id` ASC) ,
  INDEX `fk_requirement_requirement_type1_idx` (`requirement_type_id` ASC) ,
  PRIMARY KEY (`id`, `version`) ,
  INDEX `fk_requirement_requirement_status1_idx` (`requirement_status_id` ASC) ,
  INDEX `fk_requirement_requirement_spec_node1_idx` (`requirement_spec_node_id` ASC, `requirement_spec_node_requirement_spec_id` ASC, `requirement_spec_node_requirement_spec_project_id` ASC, `requirement_spec_node_requirement_spec_spec_level_id` ASC) ,
  CONSTRAINT `fk_requirement_requirement_type1`
    FOREIGN KEY (`requirement_type_id` )
    REFERENCES `validation_manager`.`requirement_type` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_requirement_requirement_status1`
    FOREIGN KEY (`requirement_status_id` )
    REFERENCES `validation_manager`.`requirement_status` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_requirement_requirement_spec_node1`
    FOREIGN KEY (`requirement_spec_node_id` , `requirement_spec_node_requirement_spec_id` , `requirement_spec_node_requirement_spec_project_id` , `requirement_spec_node_requirement_spec_spec_level_id` )
    REFERENCES `validation_manager`.`requirement_spec_node` (`id` , `requirement_spec_id` , `requirement_spec_project_id` , `requirement_spec_spec_level_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`project_has_test_project`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`project_has_test_project` (
  `project_id` INT NOT NULL ,
  `test_project_id` INT NOT NULL ,
  PRIMARY KEY (`project_id`, `test_project_id`) ,
  INDEX `fk_product_has_test_project_product1_idx` (`project_id` ASC) ,
  INDEX `fk_product_has_test_project_test_project1_idx` (`test_project_id` ASC) ,
  CONSTRAINT `fk_product_has_test_project_product1`
    FOREIGN KEY (`project_id` )
    REFERENCES `validation_manager`.`project` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_has_test_project_test_project1`
    FOREIGN KEY (`test_project_id` )
    REFERENCES `validation_manager`.`test_project` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`step_has_requirement`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`step_has_requirement` (
  `step_id` INT NOT NULL ,
  `step_test_case_id` INT UNSIGNED NOT NULL ,
  `step_test_case_test_id` INT NOT NULL ,
  `requirement_id` INT NOT NULL ,
  `requirement_version` INT NOT NULL ,
  PRIMARY KEY (`step_id`, `step_test_case_id`, `step_test_case_test_id`, `requirement_id`, `requirement_version`) ,
  INDEX `fk_step_has_requirement_step1_idx` (`step_id` ASC, `step_test_case_id` ASC, `step_test_case_test_id` ASC) ,
  INDEX `fk_step_has_requirement_requirement1_idx` (`requirement_id` ASC, `requirement_version` ASC) ,
  CONSTRAINT `fk_step_has_requirement_step1`
    FOREIGN KEY (`step_id` , `step_test_case_id` , `step_test_case_test_id` )
    REFERENCES `validation_manager`.`step` (`id` , `test_case_id` , `test_case_test_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_step_has_requirement_requirement1`
    FOREIGN KEY (`requirement_id` , `requirement_version` )
    REFERENCES `validation_manager`.`requirement` (`id` , `version` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`test_case_t`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`test_case_t` (
  `record_id` INT NOT NULL ,
  `id` INT UNSIGNED NOT NULL ,
  `test_id` INT NOT NULL ,
  `version` SMALLINT(5) UNSIGNED NOT NULL DEFAULT 1 ,
  `summary` TEXT NULL ,
  `expected_results` TEXT NULL ,
  `creation_date` DATETIME NOT NULL ,
  `modification_date` DATETIME NOT NULL ,
  `active` TINYINT(1) NULL DEFAULT 1 ,
  `is_open` TINYINT(1) NULL DEFAULT 1 ,
  `author_id` INT NOT NULL ,
  `updater_id` INT NULL ,
  PRIMARY KEY (`record_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`user_modified_record`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`user_modified_record` (
  `user_id` INT NOT NULL ,
  `record_id` INT NOT NULL ,
  `modified_date` DATETIME NOT NULL ,
  `reason` VARCHAR(45) NOT NULL ,
  PRIMARY KEY (`record_id`, `user_id`) ,
  INDEX `fk_user_modified_record_user1_idx` (`user_id` ASC) ,
  CONSTRAINT `fk_user_modified_record_user1`
    FOREIGN KEY (`user_id` )
    REFERENCES `validation_manager`.`vm_user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`step_t`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`step_t` (
  `record_id` INT NOT NULL ,
  `id` INT NOT NULL ,
  `test_case_id` INT UNSIGNED NOT NULL ,
  `test_case_test_id` INT NOT NULL ,
  `step_sequence` INT NOT NULL DEFAULT 1 ,
  `text` VARCHAR(100) NOT NULL ,
  `notes` TEXT NULL ,
  PRIMARY KEY (`record_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`test_case_execution`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`test_case_execution` (
  `id` INT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`vm_exception`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`vm_exception` (
  `id` INT NOT NULL ,
  `reporter_id` INT NOT NULL ,
  `report_date` DATETIME NOT NULL ,
  `close_date` DATETIME NULL ,
  `description` TEXT NOT NULL ,
  PRIMARY KEY (`id`, `reporter_id`) ,
  INDEX `fk_exception_user1_idx` (`reporter_id` ASC) ,
  CONSTRAINT `fk_exception_user1`
    FOREIGN KEY (`reporter_id` )
    REFERENCES `validation_manager`.`vm_user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`investigation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`investigation` (
  `id` INT NOT NULL ,
  `description` TEXT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`user_has_investigation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`user_has_investigation` (
  `user_id` INT NOT NULL ,
  `investigation_id` INT NOT NULL ,
  `start_date` DATETIME NULL ,
  `close_date` DATETIME NULL ,
  PRIMARY KEY (`user_id`, `investigation_id`) ,
  INDEX `fk_user_has_investigation_user1_idx` (`user_id` ASC) ,
  INDEX `fk_user_has_investigation_investigation1_idx` (`investigation_id` ASC) ,
  CONSTRAINT `fk_user_has_investigation_user1`
    FOREIGN KEY (`user_id` )
    REFERENCES `validation_manager`.`vm_user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_investigation_investigation1`
    FOREIGN KEY (`investigation_id` )
    REFERENCES `validation_manager`.`investigation` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`root_cause_type`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`root_cause_type` (
  `id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `description` VARCHAR(100) NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`root_cause`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`root_cause` (
  `id` INT NOT NULL ,
  `root_cause_type_id` INT NOT NULL ,
  `details` TEXT NOT NULL ,
  PRIMARY KEY (`id`, `root_cause_type_id`) ,
  INDEX `fk_root_cause_root_cause_type1_idx` (`root_cause_type_id` ASC) ,
  CONSTRAINT `fk_root_cause_root_cause_type1`
    FOREIGN KEY (`root_cause_type_id` )
    REFERENCES `validation_manager`.`root_cause_type` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`user_has_root_cause`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`user_has_root_cause` (
  `user_id` INT NOT NULL ,
  `root_cause_id` INT NOT NULL ,
  `root_cause_root_cause_type_id` INT NOT NULL ,
  `start_date` DATETIME NOT NULL ,
  `end_date` DATETIME NULL ,
  PRIMARY KEY (`user_id`, `root_cause_id`, `root_cause_root_cause_type_id`) ,
  INDEX `fk_user_has_root_cause_user1_idx` (`user_id` ASC) ,
  INDEX `fk_user_has_root_cause_root_cause1_idx` (`root_cause_id` ASC, `root_cause_root_cause_type_id` ASC) ,
  CONSTRAINT `fk_user_has_root_cause_user1`
    FOREIGN KEY (`user_id` )
    REFERENCES `validation_manager`.`vm_user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_root_cause_root_cause1`
    FOREIGN KEY (`root_cause_id` , `root_cause_root_cause_type_id` )
    REFERENCES `validation_manager`.`root_cause` (`id` , `root_cause_type_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`corrective_action`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`corrective_action` (
  `id` INT NOT NULL ,
  `details` TEXT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`user_has_corrective_action`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`user_has_corrective_action` (
  `user_id` INT NOT NULL ,
  `corrective_action_id` INT NOT NULL ,
  PRIMARY KEY (`user_id`, `corrective_action_id`) ,
  INDEX `fk_user_has_corrective_action_user1_idx` (`user_id` ASC) ,
  INDEX `fk_user_has_corrective_action_corrective_action1_idx` (`corrective_action_id` ASC) ,
  CONSTRAINT `fk_user_has_corrective_action_user1`
    FOREIGN KEY (`user_id` )
    REFERENCES `validation_manager`.`vm_user` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_corrective_action_corrective_action1`
    FOREIGN KEY (`corrective_action_id` )
    REFERENCES `validation_manager`.`corrective_action` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`step_has_exception`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`step_has_exception` (
  `step_id` INT NOT NULL ,
  `step_test_case_id` INT UNSIGNED NOT NULL ,
  `step_test_case_test_id` INT NOT NULL ,
  `exception_id` INT NOT NULL ,
  `exception_reporter_id` INT NOT NULL ,
  PRIMARY KEY (`step_id`, `step_test_case_id`, `step_test_case_test_id`, `exception_id`, `exception_reporter_id`) ,
  INDEX `fk_step_has_exception_step1_idx` (`step_id` ASC, `step_test_case_id` ASC, `step_test_case_test_id` ASC) ,
  INDEX `fk_step_has_exception_exception1_idx` (`exception_id` ASC, `exception_reporter_id` ASC) ,
  CONSTRAINT `fk_step_has_exception_step1`
    FOREIGN KEY (`step_id` , `step_test_case_id` , `step_test_case_test_id` )
    REFERENCES `validation_manager`.`step` (`id` , `test_case_id` , `test_case_test_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_step_has_exception_exception1`
    FOREIGN KEY (`exception_id` , `exception_reporter_id` )
    REFERENCES `validation_manager`.`vm_exception` (`id` , `reporter_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`exception_has_root_cause`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`exception_has_root_cause` (
  `exception_id` INT NOT NULL ,
  `exception_reporter_id` INT NOT NULL ,
  `root_cause_id` INT NOT NULL ,
  `root_cause_root_cause_type_id` INT NOT NULL ,
  PRIMARY KEY (`exception_id`, `exception_reporter_id`, `root_cause_id`, `root_cause_root_cause_type_id`) ,
  INDEX `fk_exception_has_root_cause_exception1_idx` (`exception_id` ASC, `exception_reporter_id` ASC) ,
  INDEX `fk_exception_has_root_cause_root_cause1_idx` (`root_cause_id` ASC, `root_cause_root_cause_type_id` ASC) ,
  CONSTRAINT `fk_exception_has_root_cause_exception1`
    FOREIGN KEY (`exception_id` , `exception_reporter_id` )
    REFERENCES `validation_manager`.`vm_exception` (`id` , `reporter_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_exception_has_root_cause_root_cause1`
    FOREIGN KEY (`root_cause_id` , `root_cause_root_cause_type_id` )
    REFERENCES `validation_manager`.`root_cause` (`id` , `root_cause_type_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`exception_has_investigation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`exception_has_investigation` (
  `exception_id` INT NOT NULL ,
  `exception_reporter_id` INT NOT NULL ,
  `investigation_id` INT NOT NULL ,
  PRIMARY KEY (`exception_id`, `exception_reporter_id`, `investigation_id`) ,
  INDEX `fk_exception_has_investigation_exception1_idx` (`exception_id` ASC, `exception_reporter_id` ASC) ,
  INDEX `fk_exception_has_investigation_investigation1_idx` (`investigation_id` ASC) ,
  CONSTRAINT `fk_exception_has_investigation_exception1`
    FOREIGN KEY (`exception_id` , `exception_reporter_id` )
    REFERENCES `validation_manager`.`vm_exception` (`id` , `reporter_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_exception_has_investigation_investigation1`
    FOREIGN KEY (`investigation_id` )
    REFERENCES `validation_manager`.`investigation` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`exception_has_corrective_action`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`exception_has_corrective_action` (
  `exception_id` INT NOT NULL ,
  `exception_reporter_id` INT NOT NULL ,
  `corrective_action_id` INT NOT NULL ,
  PRIMARY KEY (`exception_id`, `exception_reporter_id`, `corrective_action_id`) ,
  INDEX `fk_exception_has_corrective_action_exception1_idx` (`exception_id` ASC, `exception_reporter_id` ASC) ,
  INDEX `fk_exception_has_corrective_action_corrective_action1_idx` (`corrective_action_id` ASC) ,
  CONSTRAINT `fk_exception_has_corrective_action_exception1`
    FOREIGN KEY (`exception_id` , `exception_reporter_id` )
    REFERENCES `validation_manager`.`vm_exception` (`id` , `reporter_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_exception_has_corrective_action_corrective_action1`
    FOREIGN KEY (`corrective_action_id` )
    REFERENCES `validation_manager`.`corrective_action` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`requirement_has_exception`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`requirement_has_exception` (
  `requirement_id` INT NOT NULL ,
  `exception_id` INT NOT NULL ,
  `exception_reporter_id` INT NOT NULL ,
  `requirement_has_exceptioncol` VARCHAR(45) NULL ,
  PRIMARY KEY (`requirement_id`, `exception_id`, `exception_reporter_id`) ,
  INDEX `fk_requirement_has_exception_requirement1_idx` (`requirement_id` ASC) ,
  INDEX `fk_requirement_has_exception_exception1_idx` (`exception_id` ASC, `exception_reporter_id` ASC) ,
  CONSTRAINT `fk_requirement_has_exception_requirement1`
    FOREIGN KEY (`requirement_id` )
    REFERENCES `validation_manager`.`requirement` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_requirement_has_exception_exception1`
    FOREIGN KEY (`exception_id` , `exception_reporter_id` )
    REFERENCES `validation_manager`.`vm_exception` (`id` , `reporter_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`vm_user_t`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`vm_user_t` (
  `record_id` INT NOT NULL ,
  `id` INT NOT NULL ,
  `username` VARCHAR(45) NOT NULL ,
  `password` VARCHAR(45) NOT NULL ,
  `email` VARCHAR(100) NULL ,
  `first` VARCHAR(45) NULL ,
  `last` VARCHAR(45) NULL ,
  `locale` VARCHAR(10) NULL DEFAULT 'en' ,
  `last_modifed` DATETIME NULL ,
  `attempts` INT NULL ,
  `user_status_id` INT NULL ,
  PRIMARY KEY (`record_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`vm_id`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`vm_id` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `table_name` VARCHAR(100) NOT NULL ,
  `last_id` INT NOT NULL DEFAULT 1000 ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `Unique` (`table_name` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`vm_setting`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`vm_setting` (
  `id` INT NOT NULL ,
  `setting` VARCHAR(45) NOT NULL ,
  `bool_val` TINYINT(1) NULL ,
  `int_val` INT NULL ,
  `long_val` MEDIUMTEXT NULL ,
  `string_val` TEXT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `unique` (`setting` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`test_project_t`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`test_project_t` (
  `record_id` INT NOT NULL ,
  `id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `active` TINYINT(1) NOT NULL DEFAULT 1 ,
  `notes` TEXT NULL ,
  PRIMARY KEY (`record_id`) ,
  UNIQUE INDEX `unique` (`name` ASC) )
ENGINE = InnoDB
COMMENT = 'In some scenarios this is considered a validation project.';


-- -----------------------------------------------------
-- Table `validation_manager`.`test_plan_t`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`test_plan_t` (
  `record_id` INT NOT NULL ,
  `id` INT UNSIGNED NOT NULL ,
  `test_project_id` INT NOT NULL ,
  `notes` TEXT NULL ,
  `active` TINYINT(1) NOT NULL DEFAULT 1 ,
  `is_open` TINYINT(1) NOT NULL DEFAULT 1 ,
  `regression_test_plan_id` INT UNSIGNED NULL ,
  `regression_test_plan_test_project_id` INT NULL ,
  PRIMARY KEY (`record_id`) )
ENGINE = InnoDB
COMMENT = 'In some scenarios this is considered a validation plan.';


-- -----------------------------------------------------
-- Table `validation_manager`.`FMEA`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`FMEA` (
  `id` INT NOT NULL ,
  `parent` INT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `description` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) ,
  INDEX `fk_FMEA_FMEA1_idx` (`parent` ASC) ,
  CONSTRAINT `fk_FMEA_FMEA1`
    FOREIGN KEY (`parent` )
    REFERENCES `validation_manager`.`FMEA` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_item`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_item` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `FMEA_id` INT NOT NULL ,
  `sequence` INT NOT NULL ,
  `version` INT NOT NULL DEFAULT 1 ,
  PRIMARY KEY (`id`, `FMEA_id`) ,
  INDEX `fk_risk_item_FMEA1_idx` (`FMEA_id` ASC) ,
  CONSTRAINT `fk_risk_item_FMEA1`
    FOREIGN KEY (`FMEA_id` )
    REFERENCES `validation_manager`.`FMEA` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_category`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_category` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `minimum` INT NOT NULL ,
  `maximum` INT NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`hazard`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`hazard` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `description` TEXT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`failure_mode`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`failure_mode` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `description` TEXT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`cause`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`cause` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `description` TEXT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_item_has_hazard`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_item_has_hazard` (
  `risk_item_id` INT NOT NULL ,
  `risk_item_FMEA_id` INT NOT NULL ,
  `hazard_id` INT NOT NULL ,
  PRIMARY KEY (`risk_item_id`, `risk_item_FMEA_id`, `hazard_id`) ,
  INDEX `fk_risk_item_has_hazard_hazard1_idx` (`hazard_id` ASC) ,
  INDEX `fk_risk_item_has_hazard_risk_item1_idx` (`risk_item_id` ASC, `risk_item_FMEA_id` ASC) ,
  CONSTRAINT `fk_risk_item_has_hazard_risk_item1`
    FOREIGN KEY (`risk_item_id` , `risk_item_FMEA_id` )
    REFERENCES `validation_manager`.`risk_item` (`id` , `FMEA_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_risk_item_has_hazard_hazard1`
    FOREIGN KEY (`hazard_id` )
    REFERENCES `validation_manager`.`hazard` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_item_has_failure_mode`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_item_has_failure_mode` (
  `risk_item_id` INT NOT NULL ,
  `risk_item_FMEA_id` INT NOT NULL ,
  `failure_mode_id` INT NOT NULL ,
  PRIMARY KEY (`risk_item_id`, `risk_item_FMEA_id`, `failure_mode_id`) ,
  INDEX `fk_risk_item_has_failure_mode_failure_mode1_idx` (`failure_mode_id` ASC) ,
  INDEX `fk_risk_item_has_failure_mode_risk_item1_idx` (`risk_item_id` ASC, `risk_item_FMEA_id` ASC) ,
  CONSTRAINT `fk_risk_item_has_failure_mode_risk_item1`
    FOREIGN KEY (`risk_item_id` , `risk_item_FMEA_id` )
    REFERENCES `validation_manager`.`risk_item` (`id` , `FMEA_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_risk_item_has_failure_mode_failure_mode1`
    FOREIGN KEY (`failure_mode_id` )
    REFERENCES `validation_manager`.`failure_mode` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_item_has_cause`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_item_has_cause` (
  `risk_item_id` INT NOT NULL ,
  `risk_item_FMEA_id` INT NOT NULL ,
  `cause_id` INT NOT NULL ,
  PRIMARY KEY (`risk_item_id`, `risk_item_FMEA_id`, `cause_id`) ,
  INDEX `fk_risk_item_has_cause_cause1_idx` (`cause_id` ASC) ,
  INDEX `fk_risk_item_has_cause_risk_item1_idx` (`risk_item_id` ASC, `risk_item_FMEA_id` ASC) ,
  CONSTRAINT `fk_risk_item_has_cause_risk_item1`
    FOREIGN KEY (`risk_item_id` , `risk_item_FMEA_id` )
    REFERENCES `validation_manager`.`risk_item` (`id` , `FMEA_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_risk_item_has_cause_cause1`
    FOREIGN KEY (`cause_id` )
    REFERENCES `validation_manager`.`cause` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_control_type`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_control_type` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `description` TEXT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_control`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_control` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `risk_control_type_id` INT NOT NULL ,
  PRIMARY KEY (`id`, `risk_control_type_id`) ,
  INDEX `fk_risk_control_risk_control_type1_idx` (`risk_control_type_id` ASC) ,
  CONSTRAINT `fk_risk_control_risk_control_type1`
    FOREIGN KEY (`risk_control_type_id` )
    REFERENCES `validation_manager`.`risk_control_type` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_item_has_risk_control`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_item_has_risk_control` (
  `risk_item_id` INT NOT NULL ,
  `risk_item_FMEA_id` INT NOT NULL ,
  `risk_control_id` INT NOT NULL ,
  PRIMARY KEY (`risk_item_id`, `risk_item_FMEA_id`, `risk_control_id`) ,
  INDEX `fk_risk_item_has_risk_control_risk_control1_idx` (`risk_control_id` ASC) ,
  INDEX `fk_risk_item_has_risk_control_risk_item1_idx` (`risk_item_id` ASC, `risk_item_FMEA_id` ASC) ,
  CONSTRAINT `fk_risk_item_has_risk_control_risk_item1`
    FOREIGN KEY (`risk_item_id` , `risk_item_FMEA_id` )
    REFERENCES `validation_manager`.`risk_item` (`id` , `FMEA_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_risk_item_has_risk_control_risk_control1`
    FOREIGN KEY (`risk_control_id` )
    REFERENCES `validation_manager`.`risk_control` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_control_has_test_case`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_control_has_test_case` (
  `risk_control_id` INT NOT NULL ,
  `risk_control_risk_control_type_id` INT NOT NULL ,
  `test_case_id` INT UNSIGNED NOT NULL ,
  `test_case_test_id` INT NOT NULL ,
  PRIMARY KEY (`risk_control_id`, `risk_control_risk_control_type_id`, `test_case_id`, `test_case_test_id`) ,
  INDEX `fk_risk_control_has_test_case_test_case1_idx` (`test_case_id` ASC, `test_case_test_id` ASC) ,
  INDEX `fk_risk_control_has_test_case_risk_control1_idx` (`risk_control_id` ASC, `risk_control_risk_control_type_id` ASC) ,
  CONSTRAINT `fk_risk_control_has_test_case_risk_control1`
    FOREIGN KEY (`risk_control_id` , `risk_control_risk_control_type_id` )
    REFERENCES `validation_manager`.`risk_control` (`id` , `risk_control_type_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_risk_control_has_test_case_test_case1`
    FOREIGN KEY (`test_case_id` , `test_case_test_id` )
    REFERENCES `validation_manager`.`test_case` (`id` , `test_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_control_has_residual_risk_item`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_control_has_residual_risk_item` (
  `risk_control_id` INT NOT NULL ,
  `risk_control_risk_control_type_id` INT NOT NULL ,
  `risk_item_id` INT NOT NULL ,
  `risk_item_FMEA_id` INT NOT NULL ,
  PRIMARY KEY (`risk_control_id`, `risk_control_risk_control_type_id`, `risk_item_id`, `risk_item_FMEA_id`) ,
  INDEX `fk_risk_control_has_risk_item_risk_item1_idx` (`risk_item_id` ASC, `risk_item_FMEA_id` ASC) ,
  INDEX `fk_risk_control_has_risk_item_risk_control1_idx` (`risk_control_id` ASC, `risk_control_risk_control_type_id` ASC) ,
  CONSTRAINT `fk_risk_control_has_risk_item_risk_control1`
    FOREIGN KEY (`risk_control_id` , `risk_control_risk_control_type_id` )
    REFERENCES `validation_manager`.`risk_control` (`id` , `risk_control_type_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_risk_control_has_risk_item_risk_item1`
    FOREIGN KEY (`risk_item_id` , `risk_item_FMEA_id` )
    REFERENCES `validation_manager`.`risk_item` (`id` , `FMEA_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`FMEA_has_risk_category`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`FMEA_has_risk_category` (
  `FMEA_id` INT NOT NULL ,
  `risk_category_id` INT NOT NULL ,
  PRIMARY KEY (`FMEA_id`, `risk_category_id`) ,
  INDEX `fk_FMEA_has_risk_category_risk_category1_idx` (`risk_category_id` ASC) ,
  INDEX `fk_FMEA_has_risk_category_FMEA1_idx` (`FMEA_id` ASC) ,
  CONSTRAINT `fk_FMEA_has_risk_category_FMEA1`
    FOREIGN KEY (`FMEA_id` )
    REFERENCES `validation_manager`.`FMEA` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FMEA_has_risk_category_risk_category1`
    FOREIGN KEY (`risk_category_id` )
    REFERENCES `validation_manager`.`risk_category` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_item_has_risk_category`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_item_has_risk_category` (
  `risk_item_id` INT NOT NULL ,
  `risk_item_FMEA_id` INT NOT NULL ,
  `risk_category_id` INT NOT NULL ,
  `value` INT NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`risk_item_id`, `risk_item_FMEA_id`, `risk_category_id`) ,
  INDEX `fk_risk_item_has_risk_category_risk_category1_idx` (`risk_category_id` ASC) ,
  INDEX `fk_risk_item_has_risk_category_risk_item1_idx` (`risk_item_id` ASC, `risk_item_FMEA_id` ASC) ,
  CONSTRAINT `fk_risk_item_has_risk_category_risk_item1`
    FOREIGN KEY (`risk_item_id` , `risk_item_FMEA_id` )
    REFERENCES `validation_manager`.`risk_item` (`id` , `FMEA_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_risk_item_has_risk_category_risk_category1`
    FOREIGN KEY (`risk_category_id` )
    REFERENCES `validation_manager`.`risk_category` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`requirement_has_requirement`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`requirement_has_requirement` (
  `requirement_id` INT NOT NULL ,
  `requirement_version` INT NOT NULL ,
  `parent_requirement_id` INT NOT NULL ,
  `parent_requirement_version` INT NOT NULL ,
  PRIMARY KEY (`requirement_id`, `requirement_version`, `parent_requirement_id`, `parent_requirement_version`) ,
  INDEX `fk_requirement_has_requirement_requirement2_idx` (`parent_requirement_id` ASC, `parent_requirement_version` ASC) ,
  INDEX `fk_requirement_has_requirement_requirement1_idx` (`requirement_id` ASC, `requirement_version` ASC) ,
  CONSTRAINT `fk_requirement_has_requirement_requirement1`
    FOREIGN KEY (`requirement_id` , `requirement_version` )
    REFERENCES `validation_manager`.`requirement` (`id` , `version` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_requirement_has_requirement_requirement2`
    FOREIGN KEY (`parent_requirement_id` , `parent_requirement_version` )
    REFERENCES `validation_manager`.`requirement` (`id` , `version` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `validation_manager`.`attachment_type`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`attachment_type` (
  `id` INT(11) NOT NULL ,
  `fk_table` VARCHAR(255) NOT NULL ,
  `description` TEXT NULL ,
  `TYPE` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `validation_manager`.`attachment`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`attachment` (
  `id` INT(11) NOT NULL ,
  `attachment_type_id` INT(11) NOT NULL ,
  `file` LONGBLOB NOT NULL ,
  `string_value` VARCHAR(255) NOT NULL ,
  `TEXT_VALUE` LONGTEXT NULL DEFAULT NULL ,
  `attachmentcol` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`, `attachment_type_id`) ,
  INDEX `fk_attachment_attachment_type1_idx` (`attachment_type_id` ASC) ,
  CONSTRAINT `fk_attachment_attachment_type1`
    FOREIGN KEY (`attachment_type_id` )
    REFERENCES `validation_manager`.`attachment_type` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_attachment_ATTACHMENT_TYPE_ID`
    FOREIGN KEY (`attachment_type_id` )
    REFERENCES `validation_manager`.`attachment_type` (`id` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `validation_manager`.`risk_control_has_requirement`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `validation_manager`.`risk_control_has_requirement` (
  `risk_control_id` INT NOT NULL ,
  `risk_control_risk_control_type_id` INT NOT NULL ,
  `requirement_id` INT NOT NULL ,
  `requirement_version` INT NOT NULL ,
  PRIMARY KEY (`risk_control_id`, `risk_control_risk_control_type_id`, `requirement_id`, `requirement_version`) ,
  INDEX `fk_risk_control_has_requirement_requirement1_idx` (`requirement_id` ASC, `requirement_version` ASC) ,
  INDEX `fk_risk_control_has_requirement_risk_control1_idx` (`risk_control_id` ASC, `risk_control_risk_control_type_id` ASC) ,
  CONSTRAINT `fk_risk_control_has_requirement_risk_control1`
    FOREIGN KEY (`risk_control_id` , `risk_control_risk_control_type_id` )
    REFERENCES `validation_manager`.`risk_control` (`id` , `risk_control_type_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_risk_control_has_requirement_requirement1`
    FOREIGN KEY (`requirement_id` , `requirement_version` )
    REFERENCES `validation_manager`.`requirement` (`id` , `version` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`user_status`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`user_status` (`id`, `status`, `description`) VALUES (1, 'user.status.active', 'user.status.active.desc');
INSERT INTO `validation_manager`.`user_status` (`id`, `status`, `description`) VALUES (2, 'user.status.inactive', 'user.status.inactive.desc');
INSERT INTO `validation_manager`.`user_status` (`id`, `status`, `description`) VALUES (3, 'user.status.locked', 'user.status.locked.desc');
INSERT INTO `validation_manager`.`user_status` (`id`, `status`, `description`) VALUES (4, 'user.status.aged', 'user.status.aged.desc');

COMMIT;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`vm_user`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`vm_user` (`id`, `username`, `password`, `email`, `first`, `last`, `locale`, `last_modified`, `attempts`, `user_status_id`) VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', '', 'System', 'Administrator', 'en', now(), 0, 1);

COMMIT;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`role`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`role` (`id`, `description`, `notes`) VALUES (1, 'reserved1', 'reserver.desc');
INSERT INTO `validation_manager`.`role` (`id`, `description`, `notes`) VALUES (2, 'reserved2', 'reserved2.desc');
INSERT INTO `validation_manager`.`role` (`id`, `description`, `notes`) VALUES (3, 'none', 'none.desc');
INSERT INTO `validation_manager`.`role` (`id`, `description`, `notes`) VALUES (4, 'test.designer', 'test.designer.desc');
INSERT INTO `validation_manager`.`role` (`id`, `description`, `notes`) VALUES (5, 'guest', 'guest.desc');
INSERT INTO `validation_manager`.`role` (`id`, `description`, `notes`) VALUES (6, 'senior.tester', 'senior.tester.desc');
INSERT INTO `validation_manager`.`role` (`id`, `description`, `notes`) VALUES (7, 'tester', 'tester.desc');
INSERT INTO `validation_manager`.`role` (`id`, `description`, `notes`) VALUES (8, 'admin', 'admin.desc');
INSERT INTO `validation_manager`.`role` (`id`, `description`, `notes`) VALUES (9, 'leader', 'leader.desc');

COMMIT;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`user_right`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (1, 'testplan.execute');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (2, 'testplan.create.build');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (3, 'testplan.metrics');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (4, 'testplan.planning');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (5, 'testplan.user_role.assigment');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (6, 'testcase.view');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (7, 'testcase.modify');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (8, 'requirement.view');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (9, 'requirement.modify');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (10, 'product.modify');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (11, 'manage.users');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (12, 'testplan.create');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (13, 'events.view');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (14, 'events.management');
INSERT INTO `validation_manager`.`user_right` (`id`, `description`) VALUES (15, 'system.configuration');

COMMIT;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`role_has_right`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 1);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 2);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 3);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 4);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 5);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 6);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 7);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 8);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 9);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 10);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 11);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 12);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 13);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 14);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (8, 15);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (5, 3);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (5, 6);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (4, 3);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (4, 6);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (4, 7);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (4, 8);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (4, 9);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (7, 1);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (7, 3);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (7, 6);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (6, 1);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (6, 2);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (6, 3);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (6, 6);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (6, 7);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (6, 9);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (9, 1);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (9, 2);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (9, 3);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (9, 4);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (9, 5);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (9, 6);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (9, 7);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (9, 8);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (9, 9);
INSERT INTO `validation_manager`.`role_has_right` (`role_id`, `right_id`) VALUES (9, 12);

COMMIT;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`user_has_role`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`user_has_role` (`user_id`, `role_id`) VALUES (1, 8);

COMMIT;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`assigment_type`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`assigment_type` (`id`, `fk_table`, `description`) VALUES (1, 'test_plan_has_test', 'testcase.execution');
INSERT INTO `validation_manager`.`assigment_type` (`id`, `fk_table`, `description`) VALUES (2, 'test_case', 'testcase.review');

COMMIT;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`assignment_status`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`assignment_status` (`id`, `name`, `description`) VALUES (1, 'assignment.status.open', 'assignment.status.open.desc');
INSERT INTO `validation_manager`.`assignment_status` (`id`, `name`, `description`) VALUES (2, 'assignment.status.closed', 'assignment.status.closed.desc');
INSERT INTO `validation_manager`.`assignment_status` (`id`, `name`, `description`) VALUES (3, 'assignment.status.completed', 'assignment.status.completed.desc');
INSERT INTO `validation_manager`.`assignment_status` (`id`, `name`, `description`) VALUES (4, 'assignment.status..todo.urgent', 'assignment.status.todo.urgent.desc');
INSERT INTO `validation_manager`.`assignment_status` (`id`, `name`, `description`) VALUES (5, 'assignment.status.todo', 'assignment.status.todo.desc');

COMMIT;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`requirement_type`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`requirement_type` (`id`, `name`, `description`) VALUES (1, 'HW', 'Hardware');
INSERT INTO `validation_manager`.`requirement_type` (`id`, `name`, `description`) VALUES (2, 'SW', 'Software');
INSERT INTO `validation_manager`.`requirement_type` (`id`, `name`, `description`) VALUES (3, 'Labeling', 'Labeling requirements');
INSERT INTO `validation_manager`.`requirement_type` (`id`, `name`, `description`) VALUES (4, 'Standards', 'Testing related to applicable standards');

COMMIT;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`requirement_status`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`requirement_status` (`id`, `status`) VALUES (1, 'general.open');
INSERT INTO `validation_manager`.`requirement_status` (`id`, `status`) VALUES (2, 'general.approved');
INSERT INTO `validation_manager`.`requirement_status` (`id`, `status`) VALUES (3, 'general.obsolete');
INSERT INTO `validation_manager`.`requirement_status` (`id`, `status`) VALUES (4, 'general.rejected');

COMMIT;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`spec_level`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`spec_level` (`id`, `name`, `description`) VALUES (1, 'User Need', 'Higher level of requirements.');
INSERT INTO `validation_manager`.`spec_level` (`id`, `name`, `description`) VALUES (2, 'Requirements', 'Detailed version of the User Needs. Usually the lowest level of requirement for non-software projects.');
INSERT INTO `validation_manager`.`spec_level` (`id`, `name`, `description`) VALUES (3, 'System Design', 'Systems design is the phase where system engineers analyze and understand the business of the proposed system by studying the user requirements document.');
INSERT INTO `validation_manager`.`spec_level` (`id`, `name`, `description`) VALUES (4, 'Architecture Design', 'The baseline in selecting the architecture is that it should realize all which typically consists of the list of modules, brief functionality of each module, their interface relationships, dependencies, database tables, architecture diagrams, technology details etc.');
INSERT INTO `validation_manager`.`spec_level` (`id`, `name`, `description`) VALUES (5, 'Module Design', 'The designed system is broken up into smaller units or modules and each of them is explained so that the programmer can start coding directly. The low level design document or program specifications will contain a detailed functional logic of the module, in pseudocode.');

COMMIT;

-- -----------------------------------------------------
-- Data for table `validation_manager`.`vm_setting`
-- -----------------------------------------------------
START TRANSACTION;
USE `validation_manager`;
INSERT INTO `validation_manager`.`vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (4, 'password.aging', 0, 90, '0', NULL);
INSERT INTO `validation_manager`.`vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (5, 'password.attempts', 0, 3, '0', NULL);
INSERT INTO `validation_manager`.`vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (6, 'password.unusable_period', 0, 365, '0', NULL);
INSERT INTO `validation_manager`.`vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (1, 'version.high', 0, 0, '0', NULL);
INSERT INTO `validation_manager`.`vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (2, 'version.mid', 0, 0, '0', NULL);
INSERT INTO `validation_manager`.`vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (3, 'version.low', 0, 1, '0', NULL);
INSERT INTO `validation_manager`.`vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (7, 'version.postfix', 0, 0, '0', 'Alpha');

COMMIT;
