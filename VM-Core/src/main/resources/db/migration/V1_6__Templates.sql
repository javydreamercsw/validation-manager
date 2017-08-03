/*Project Types*/
INSERT INTO `project_type` (`id`, `type_name`) VALUES (1, 'general.software');
INSERT INTO `project_type` (`id`, `type_name`) VALUES (2, 'general.hardware');
INSERT INTO `project_type` (`id`, `type_name`) VALUES (3, 'general.mixed');

/*Templates*/
INSERT INTO `template` (`id`, `template_name`, `project_type_id`) VALUES (1, 'GAMP 5', 3);

/*Template node types*/
INSERT INTO `template_node_type` (`id`, `type_name`) VALUES (1, 'general.requirement');
INSERT INTO `template_node_type` (`id`, `type_name`) VALUES (2, 'general.test.plan');
INSERT INTO `template_node_type` (`id`, `type_name`) VALUES (3, 'general.folder');
INSERT INTO `template_node_type` (`id`, `type_name`) VALUES (4, 'general.risk.management');

INSERT INTO `test_case_type` (`id`, `type_name`, `type_description`) VALUES (1, 'module.test', 'module.test.desc');
INSERT INTO `test_case_type` (`id`, `type_name`, `type_description`) VALUES (2, 'integration.test', 'integration.test.desc');
INSERT INTO `test_case_type` (`id`, `type_name`, `type_description`) VALUES (3, 'configuration.test', 'configuration.test.desc');
INSERT INTO `test_case_type` (`id`, `type_name`, `type_description`) VALUES (4, 'functional.test', 'functional.test.desc');
INSERT INTO `test_case_type` (`id`, `type_name`, `type_description`) VALUES (5, 'requirement.test', 'requirement.test.desc');
INSERT INTO `test_case_type` (`id`, `type_name`, `type_description`) VALUES (6, 'installation.test', 'installation.test.desc');

/*Update Descriptions*/
update `requirement_type` set description = 'user.specification.name' where id =1;
update `requirement_type` set description = 'functional.specification.name', name = 'FS' where id =2;
update `requirement_type` set description = 'design.specification.name', name = 'DS' where id =3;
update `requirement_type` set description = 'hardware.specification.name' where id =4;
update `requirement_type` set description = 'module.specification.name', name = 'MS' where id =5;
update `requirement_type` set description = 'labeling.specification.name' where id =6;
update `requirement_type` set description = 'standards.specification.name' where id =7;

/*Activity Stream*/
INSERT INTO `activity_type` (`id`, `type_name`) VALUES (1, 'general.create');
INSERT INTO `activity_type` (`id`, `type_name`) VALUES (2, 'general.edit');
INSERT INTO `activity_type` (`id`, `type_name`) VALUES (3, 'test.execution');
INSERT INTO `activity_type` (`id`, `type_name`) VALUES (4, 'test.review');
INSERT INTO `activity_type` (`id`, `type_name`) VALUES (5, 'assign.test.case');
