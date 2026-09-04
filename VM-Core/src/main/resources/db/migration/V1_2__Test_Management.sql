/*New Roles*/
UPDATE "ROLE" SET "ROLE_NAME" = 'requirement.manager', "DESCRIPTION" = 'requirement.manager.desc' WHERE id = 1;
/*Add default test results*/
INSERT INTO "EXECUTION_RESULT" ("ID", "RESULT_NAME") VALUES (1, 'result.pass');
INSERT INTO "EXECUTION_RESULT" ("ID", "RESULT_NAME") VALUES (2, 'result.fail');
INSERT INTO "EXECUTION_RESULT" ("ID", "RESULT_NAME") VALUES (3, 'result.blocked');
INSERT INTO "EXECUTION_RESULT" ("ID", "RESULT_NAME") VALUES (4, 'result.pending');
INSERT INTO "EXECUTION_RESULT" ("ID", "RESULT_NAME") VALUES (5, 'result.progress');
/*Add default review results*/
INSERT INTO "REVIEW_RESULT" ("ID", "REVIEW_NAME") VALUES (1, 'result.pass');
INSERT INTO "REVIEW_RESULT" ("ID", "REVIEW_NAME") VALUES (2, 'result.fail');
INSERT INTO "REVIEW_RESULT" ("ID", "REVIEW_NAME") VALUES (3, 'result.pending');
/*Add some more demo users and their roles*/
INSERT INTO "VM_USER" ("ID", "USERNAME", "PASSWORD", "EMAIL", "FIRST_NAME", "LAST_NAME", "LOCALE", "USER_STATUS_ID") VALUES (2, 'tester', 'f5d1278e8109edd94e1e4197e04873b9', '', 'Mr.', 'Tester', 'en', 1);
INSERT INTO "USER_HAS_ROLE" ("USER_ID", "ROLE_ID") VALUES (2, 7);
INSERT INTO "VM_USER" ("ID", "USERNAME", "PASSWORD", "EMAIL", "FIRST_NAME", "LAST_NAME", "LOCALE", "USER_STATUS_ID") VALUES (3, 'tester2', '2e9fcf8e3df4d415c96bcf288d5ca4ba', '', 'Mrs.', 'tester', 'en', 1);
INSERT INTO "USER_HAS_ROLE" ("USER_ID", "ROLE_ID") VALUES (3, 7);
INSERT INTO "VM_USER" ("ID", "USERNAME", "PASSWORD", "EMAIL", "FIRST_NAME", "LAST_NAME", "LOCALE", "USER_STATUS_ID") VALUES (4, 'designer', '230ace927da4bb74817fa22adc663e0a', '', 'Test', 'Designer', 'en', 1);
INSERT INTO "USER_HAS_ROLE" ("USER_ID", "ROLE_ID") VALUES (4, 4);
INSERT INTO "VM_USER" ("ID", "USERNAME", "PASSWORD", "EMAIL", "FIRST_NAME", "LAST_NAME", "LOCALE", "USER_STATUS_ID") VALUES (5, 'senior', 'c1a1738648ecda410dc3a0dbbb3be683', '', 'Senior', 'Tester', 'en', 1);
INSERT INTO "USER_HAS_ROLE" ("USER_ID", "ROLE_ID") VALUES (5, 6);
INSERT INTO "VM_USER" ("ID", "USERNAME", "PASSWORD", "EMAIL", "FIRST_NAME", "LAST_NAME", "LOCALE", "USER_STATUS_ID") VALUES (6, 'leader', 'c444858e0aaeb727da73d2eae62321ad', '', 'Lead', 'Tester', 'en', 1);
INSERT INTO "USER_HAS_ROLE" ("USER_ID", "ROLE_ID") VALUES (6, 9);
/*Add Quality user*/
INSERT INTO "VM_USER" ("ID", "USERNAME", "PASSWORD", "EMAIL", "FIRST_NAME", "LAST_NAME", "LOCALE", "USER_STATUS_ID") VALUES (7, 'quality', 'd66636b253cb346dbb6240e30def3618', '', 'Quality', 'Assurance', 'en', 1);
INSERT INTO "USER_HAS_ROLE" ("USER_ID", "ROLE_ID") VALUES (7, 10);
/*Update version*/
UPDATE "VM_SETTING" SET "INT_VAL" = 2 WHERE id = 3;
/*Add permissions*/
INSERT INTO "ROLE_HAS_RIGHT" ("ROLE_ID", "RIGHT_ID") VALUES (7, 8);
/*Attachment Types*/
INSERT INTO "ATTACHMENT_TYPE" ("ID", "DESCRIPTION", "TYPE") VALUES (1, 'plain text', 'txt');
INSERT INTO "ATTACHMENT_TYPE" ("ID", "DESCRIPTION", "TYPE") VALUES (2, 'PDF', 'pdf');
INSERT INTO "ATTACHMENT_TYPE" ("ID", "DESCRIPTION", "TYPE") VALUES (3, 'Legacy Word Document', 'doc');
INSERT INTO "ATTACHMENT_TYPE" ("ID", "DESCRIPTION", "TYPE") VALUES (4, 'Word Document', 'docx');
INSERT INTO "ATTACHMENT_TYPE" ("ID", "DESCRIPTION", "TYPE") VALUES (5, 'Legacy Excel Document', 'xls');
INSERT INTO "ATTACHMENT_TYPE" ("ID", "DESCRIPTION", "TYPE") VALUES (6, 'Excel Document', 'xlsx');
INSERT INTO "ATTACHMENT_TYPE" ("ID", "DESCRIPTION", "TYPE") VALUES (7, 'Legacy Power Point Document', 'ppt');
INSERT INTO "ATTACHMENT_TYPE" ("ID", "DESCRIPTION", "TYPE") VALUES (8, 'Power Point Document', 'pptx');
INSERT INTO "ATTACHMENT_TYPE" ("ID", "DESCRIPTION", "TYPE") VALUES (9, 'Comment', 'comment');
INSERT INTO "ATTACHMENT_TYPE" ("ID", "DESCRIPTION", "TYPE") VALUES (10, 'undefined', '');
/*Settings*/
INSERT INTO "VM_SETTING" ("ID", "SETTING", "BOOL_VAL", "INT_VAL", "LONG_VAL", "STRING_VAL") VALUES (8, 'show.expected.result', 1, 0, '0', '');
INSERT INTO "VM_SETTING" ("ID", "SETTING", "BOOL_VAL", "INT_VAL", "LONG_VAL", "STRING_VAL") VALUES (9, 'openoffice.port', 0, 1000, '0', '');
INSERT INTO "VM_SETTING" ("ID", "SETTING", "BOOL_VAL", "INT_VAL", "LONG_VAL", "STRING_VAL") VALUES (10, 'openoffice.home', 0, 0, '0', '');
INSERT INTO "VM_SETTING" ("ID", "SETTING", "BOOL_VAL", "INT_VAL", "LONG_VAL", "STRING_VAL") VALUES (11, 'quality.review', 1, 0, '0', '');
/*Issue Types*/
INSERT INTO "ISSUE_TYPE" ("ID", "TYPE_NAME", "DESCRIPTION") VALUES (1, 'bug.name', 'bug.desc');
INSERT INTO "ISSUE_TYPE" ("ID", "TYPE_NAME", "DESCRIPTION") VALUES (2, 'observation.name', 'observation.desc');
INSERT INTO "ISSUE_TYPE" ("ID", "TYPE_NAME", "DESCRIPTION") VALUES (3, 'step.issue.name', 'step.issue.desc');
/*Issue Resolutions*/
INSERT INTO "ISSUE_RESOLUTION" ("ID", "NAME") VALUES (1, 'issue.invalid');
INSERT INTO "ISSUE_RESOLUTION" ("ID", "NAME") VALUES (2, 'issue.not.reproduceable');
INSERT INTO "ISSUE_RESOLUTION" ("ID", "NAME") VALUES (3, 'issue.fixed');
