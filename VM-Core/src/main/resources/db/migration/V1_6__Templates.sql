/*Templates*/
INSERT INTO `template` (`id`, `template_name`) VALUES (1, 'GAMP 5');

/*Template node types*/
INSERT INTO `template_node_type` (`id`, `type_name`) VALUES (1, 'general.requirement');
INSERT INTO `template_node_type` (`id`, `type_name`) VALUES (2, 'general.test.plan');
INSERT INTO `template_node_type` (`id`, `type_name`) VALUES (3, 'general.folder');
INSERT INTO `template_node_type` (`id`, `type_name`) VALUES (4, 'general.risk.management');

/*Template Nodes*/
/*GAMP 5*/
INSERT INTO `template_node` (`id`, `template_id`, `template_node_type_id`, `parent_template_node_id`, `template_node_template_id`, `parent_template_node_template_node_type_id`, `node_name`) VALUES (1, 1, 1, NULL, NULL, NULL, 'Good Manufacturing Practice (GMP)');
INSERT INTO `template_node` (`id`, `template_id`, `template_node_type_id`, `parent_template_node_id`, `template_node_template_id`, `parent_template_node_template_node_type_id`, `node_name`) VALUES (2, 1, 1, NULL, NULL, NULL, 'Good Clinical Practice (GCP)');
INSERT INTO `template_node` (`id`, `template_id`, `template_node_type_id`, `parent_template_node_id`, `template_node_template_id`, `parent_template_node_template_node_type_id`, `node_name`) VALUES (3, 1, 1, NULL, NULL, NULL, 'Good Laboratory Practice (GLP)');
INSERT INTO `template_node` (`id`, `template_id`, `template_node_type_id`, `parent_template_node_id`, `template_node_template_id`, `parent_template_node_template_node_type_id`, `node_name`) VALUES (4, 1, 1, NULL, NULL, NULL, 'Good Distribution Practice (GDP)');
INSERT INTO `template_node` (`id`, `template_id`, `template_node_type_id`, `parent_template_node_id`, `template_node_template_id`, `parent_template_node_template_node_type_id`, `node_name`) VALUES (5, 1, 1, NULL, NULL, NULL, 'Good Quality Practice (GQP)');
INSERT INTO `template_node` (`id`, `template_id`, `template_node_type_id`, `parent_template_node_id`, `template_node_template_id`, `parent_template_node_template_node_type_id`, `node_name`) VALUES (6, 1, 1, NULL, NULL, NULL, 'Good Pharmacovigilance Practice');
INSERT INTO `template_node` (`id`, `template_id`, `template_node_type_id`, `parent_template_node_id`, `template_node_template_id`, `parent_template_node_template_node_type_id`, `node_name`) VALUES (7, 1, 1, NULL, NULL, NULL, 'Medical Device Regulations');
INSERT INTO `template_node` (`id`, `template_id`, `template_node_type_id`, `parent_template_node_id`, `template_node_template_id`, `parent_template_node_template_node_type_id`, `node_name`) VALUES (8, 1, 1, NULL, NULL, NULL, 'Prescription Drug Marketing Act (PDMA)');

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