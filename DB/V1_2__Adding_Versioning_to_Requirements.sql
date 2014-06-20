ALTER TABLE `step_has_requirement` 
DROP FOREIGN KEY `fk_step_has_requirement_requirement1`;

ALTER TABLE `risk_control_has_requirement` 
DROP FOREIGN KEY `fk_risk_control_has_requirement_requirement1`;

ALTER TABLE `requirement_has_exception` 
DROP FOREIGN KEY `fk_requirement_has_vm_exception_requirement1`;

ALTER TABLE `requirement_has_requirement` 
DROP FOREIGN KEY `fk_requirement_has_requirement_requirement1`,
DROP FOREIGN KEY `fk_requirement_has_requirement_requirement2`;

ALTER TABLE `requirement` 
DROP COLUMN `version`,
ADD COLUMN `major_version` INT(11) NOT NULL DEFAULT 0 AFTER `id`,
ADD COLUMN `mid_version` INT(11) NOT NULL DEFAULT 0 AFTER `major_version`,
ADD COLUMN `minor_version` INT(11) NOT NULL DEFAULT 1 AFTER `mid_version`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`id`, `major_version`, `mid_version`, `minor_version`);

ALTER TABLE `step_has_requirement` 
DROP COLUMN `requirement_version`,
ADD COLUMN `requirement_major_version` INT(11) NOT NULL AFTER `requirement_id`,
ADD COLUMN `requirement_mid_version` INT(11) NOT NULL AFTER `requirement_major_version`,
ADD COLUMN `requirement_minor_version` INT(11) NOT NULL AFTER `requirement_mid_version`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`step_id`, `step_test_case_id`, `step_test_case_test_id`, `requirement_id`, `requirement_major_version`, `requirement_mid_version`, `requirement_minor_version`),
DROP INDEX `fk_step_has_requirement_requirement1_idx` ,
ADD INDEX `fk_step_has_requirement_requirement1_idx` (`requirement_id` ASC, `requirement_major_version` ASC, `requirement_mid_version` ASC, `requirement_minor_version` ASC);

ALTER TABLE `risk_control_has_requirement` 
DROP COLUMN `requirement_version`,
ADD COLUMN `requirement_major_version` INT(11) NOT NULL AFTER `requirement_id`,
ADD COLUMN `requirement_mid_version` INT(11) NOT NULL AFTER `requirement_major_version`,
ADD COLUMN `requirement_minor_version` INT(11) NOT NULL AFTER `requirement_mid_version`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`risk_control_id`, `risk_control_risk_control_type_id`, `requirement_id`, `requirement_major_version`, `requirement_mid_version`, `requirement_minor_version`),
DROP INDEX `fk_risk_control_has_requirement_requirement1_idx` ,
ADD INDEX `fk_risk_control_has_requirement_requirement1_idx` (`requirement_id` ASC, `requirement_major_version` ASC, `requirement_mid_version` ASC, `requirement_minor_version` ASC);

ALTER TABLE `requirement_has_exception` 
DROP COLUMN `requirement_version`,
CHANGE COLUMN `requirement_id` `requirement_id` INT(11) NOT NULL AFTER `vm_exception_reporter_id`,
ADD COLUMN `requirement_major_version` INT(11) NOT NULL AFTER `requirement_id`,
ADD COLUMN `requirement_mid_version` INT(11) NOT NULL AFTER `requirement_major_version`,
ADD COLUMN `requirement_minor_version` INT(11) NOT NULL AFTER `requirement_mid_version`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`vm_exception_id`, `vm_exception_reporter_id`, `requirement_id`, `requirement_major_version`, `requirement_mid_version`, `requirement_minor_version`),
ADD INDEX `fk_requirement_has_exception_requirement1_idx` (`requirement_id` ASC, `requirement_major_version` ASC, `requirement_mid_version` ASC, `requirement_minor_version` ASC),
DROP INDEX `fk_requirement_has_vm_exception_requirement1_idx` ;

ALTER TABLE `requirement_has_requirement` 
DROP COLUMN `parent_requirement_version`,
DROP COLUMN `parent_requirement_id`,
DROP COLUMN `requirement_version`,
ADD COLUMN `requirement_major_version` INT(11) NOT NULL AFTER `requirement_id`,
ADD COLUMN `requirement_mid_version` INT(11) NOT NULL AFTER `requirement_major_version`,
ADD COLUMN `requirement_minor_version` INT(11) NOT NULL AFTER `requirement_mid_version`,
ADD COLUMN `requirement_id1` INT(11) NOT NULL AFTER `requirement_minor_version`,
ADD COLUMN `requirement_major_version1` INT(11) NOT NULL AFTER `requirement_id1`,
ADD COLUMN `requirement_mid_version1` INT(11) NOT NULL AFTER `requirement_major_version1`,
ADD COLUMN `requirement_minor_version1` INT(11) NOT NULL AFTER `requirement_mid_version1`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`requirement_id`, `requirement_major_version`, `requirement_mid_version`, `requirement_minor_version`, `requirement_id1`, `requirement_major_version1`, `requirement_mid_version1`, `requirement_minor_version1`),
DROP INDEX `fk_requirement_has_requirement_requirement2_idx` ,
ADD INDEX `fk_requirement_has_requirement_requirement2_idx` (`requirement_id1` ASC, `requirement_major_version1` ASC, `requirement_mid_version1` ASC, `requirement_minor_version1` ASC),
DROP INDEX `fk_requirement_has_requirement_requirement1_idx` ,
ADD INDEX `fk_requirement_has_requirement_requirement1_idx` (`requirement_id` ASC, `requirement_major_version` ASC, `requirement_mid_version` ASC, `requirement_minor_version` ASC);

ALTER TABLE `step_has_requirement` 
ADD CONSTRAINT `fk_step_has_requirement_requirement1`
  FOREIGN KEY (`requirement_id` , `requirement_major_version` , `requirement_mid_version` , `requirement_minor_version`)
  REFERENCES `requirement` (`id` , `major_version` , `mid_version` , `minor_version`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `risk_control_has_requirement` 
ADD CONSTRAINT `fk_risk_control_has_requirement_requirement1`
  FOREIGN KEY (`requirement_id` , `requirement_major_version` , `requirement_mid_version` , `requirement_minor_version`)
  REFERENCES `requirement` (`id` , `major_version` , `mid_version` , `minor_version`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `requirement_has_exception` 
ADD CONSTRAINT `fk_requirement_has_exception_requirement1`
  FOREIGN KEY (`requirement_id` , `requirement_major_version` , `requirement_mid_version` , `requirement_minor_version`)
  REFERENCES `requirement` (`id` , `major_version` , `mid_version` , `minor_version`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `requirement_has_requirement` 
ADD CONSTRAINT `fk_requirement_has_requirement_requirement1`
  FOREIGN KEY (`requirement_id` , `requirement_major_version` , `requirement_mid_version` , `requirement_minor_version`)
  REFERENCES `requirement` (`id` , `major_version` , `mid_version` , `minor_version`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_requirement_has_requirement_requirement2`
  FOREIGN KEY (`requirement_id1` , `requirement_major_version1` , `requirement_mid_version1` , `requirement_minor_version1`)
  REFERENCES `requirement` (`id` , `major_version` , `mid_version` , `minor_version`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
