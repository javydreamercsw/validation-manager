/*Project Types*/
INSERT INTO "PROJECT_TYPE" ("ID", "TYPE_NAME") VALUES (1, 'general.software');
INSERT INTO "PROJECT_TYPE" ("ID", "TYPE_NAME") VALUES (2, 'general.hardware');
INSERT INTO "PROJECT_TYPE" ("ID", "TYPE_NAME") VALUES (3, 'general.mixed');

/*Templates*/
INSERT INTO "TEMPLATE" ("ID", "TEMPLATE_NAME", "PROJECT_TYPE_ID") VALUES (1, 'GAMP 5', 3);

/*Template node types*/
INSERT INTO "TEMPLATE_NODE_TYPE" ("ID", "TYPE_NAME") VALUES (1, 'general.requirement');
INSERT INTO "TEMPLATE_NODE_TYPE" ("ID", "TYPE_NAME") VALUES (2, 'general.test.plan');
INSERT INTO "TEMPLATE_NODE_TYPE" ("ID", "TYPE_NAME") VALUES (3, 'general.folder');
INSERT INTO "TEMPLATE_NODE_TYPE" ("ID", "TYPE_NAME") VALUES (4, 'general.risk.management');

INSERT INTO "TEST_CASE_TYPE" ("ID", "TYPE_NAME", "TYPE_DESCRIPTION") VALUES (1, 'module.test', 'module.test.desc');
INSERT INTO "TEST_CASE_TYPE" ("ID", "TYPE_NAME", "TYPE_DESCRIPTION") VALUES (2, 'integration.test', 'integration.test.desc');
INSERT INTO "TEST_CASE_TYPE" ("ID", "TYPE_NAME", "TYPE_DESCRIPTION") VALUES (3, 'configuration.test', 'configuration.test.desc');
INSERT INTO "TEST_CASE_TYPE" ("ID", "TYPE_NAME", "TYPE_DESCRIPTION") VALUES (4, 'functional.test', 'functional.test.desc');
INSERT INTO "TEST_CASE_TYPE" ("ID", "TYPE_NAME", "TYPE_DESCRIPTION") VALUES (5, 'requirement.test', 'requirement.test.desc');
INSERT INTO "TEST_CASE_TYPE" ("ID", "TYPE_NAME", "TYPE_DESCRIPTION") VALUES (6, 'installation.test', 'installation.test.desc');

/*Update Descriptions*/
update "REQUIREMENT_TYPE" set description = 'user.specification.name' where id =1;
update "REQUIREMENT_TYPE" set description = 'functional.specification.name', name = 'FS' where id =2;
update "REQUIREMENT_TYPE" set description = 'design.specification.name', name = 'DS' where id =3;
update "REQUIREMENT_TYPE" set description = 'hardware.specification.name' where id =4;
update "REQUIREMENT_TYPE" set description = 'module.specification.name', name = 'MS' where id =5;
update "REQUIREMENT_TYPE" set description = 'labeling.specification.name' where id =6;
update "REQUIREMENT_TYPE" set description = 'standards.specification.name' where id =7;

/*Activity Stream*/
INSERT INTO "ACTIVITY_TYPE" ("ID", "TYPE_NAME") VALUES (1, 'general.create');
INSERT INTO "ACTIVITY_TYPE" ("ID", "TYPE_NAME") VALUES (2, 'general.edit');
INSERT INTO "ACTIVITY_TYPE" ("ID", "TYPE_NAME") VALUES (3, 'test.execution');
INSERT INTO "ACTIVITY_TYPE" ("ID", "TYPE_NAME") VALUES (4, 'test.review');
INSERT INTO "ACTIVITY_TYPE" ("ID", "TYPE_NAME") VALUES (5, 'assign.test.case');
