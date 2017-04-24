/*New Roles*/
UPDATE `role` SET `role_name` = 'requirement.manager', `description` = 'requirement.manager.desc' WHERE id = 1;
/*Add default test results*/
INSERT INTO `execution_result` (`id`, `result_name`) VALUES (1, 'result.pass');
INSERT INTO `execution_result` (`id`, `result_name`) VALUES (2, 'result.fail');
INSERT INTO `execution_result` (`id`, `result_name`) VALUES (3, 'result.blocked');
INSERT INTO `execution_result` (`id`, `result_name`) VALUES (4, 'result.pending');
INSERT INTO `execution_result` (`id`, `result_name`) VALUES (5, 'result.progress');
/*Add default review results*/
INSERT INTO `review_result` (`id`, `review_name`) VALUES (1, 'result.pass');
INSERT INTO `review_result` (`id`, `review_name`) VALUES (2, 'result.fail');
INSERT INTO `review_result` (`id`, `review_name`) VALUES (3, 'result.pending');
/*Add some more demo users and their roles*/
INSERT INTO `vm_user` (`id`, `username`, `password`, `email`, `first_name`, `last_name`, `locale`, `user_status_id`) VALUES (2, 'tester', 'f5d1278e8109edd94e1e4197e04873b9', '', 'Mr.', 'Tester', 'en', 1);
INSERT INTO `user_has_role` (`user_id`, `role_id`) VALUES (2, 7);
INSERT INTO `vm_user` (`id`, `username`, `password`, `email`, `first_name`, `last_name`, `locale`, `user_status_id`) VALUES (3, 'tester2', '2e9fcf8e3df4d415c96bcf288d5ca4ba', '', 'Mrs.', 'tester', 'en', 1);
INSERT INTO `user_has_role` (`user_id`, `role_id`) VALUES (3, 7);
INSERT INTO `vm_user` (`id`, `username`, `password`, `email`, `first_name`, `last_name`, `locale`, `user_status_id`) VALUES (4, 'designer', '230ace927da4bb74817fa22adc663e0a', '', 'Test', 'Designer', 'en', 1);
INSERT INTO `user_has_role` (`user_id`, `role_id`) VALUES (4, 4);
INSERT INTO `vm_user` (`id`, `username`, `password`, `email`, `first_name`, `last_name`, `locale`, `user_status_id`) VALUES (5, 'senior', 'c1a1738648ecda410dc3a0dbbb3be683', '', 'Senior', 'Tester', 'en', 1);
INSERT INTO `user_has_role` (`user_id`, `role_id`) VALUES (5, 6);
INSERT INTO `vm_user` (`id`, `username`, `password`, `email`, `first_name`, `last_name`, `locale`, `user_status_id`) VALUES (6, 'leader', 'c444858e0aaeb727da73d2eae62321ad', '', 'Lead', 'Tester', 'en', 1);
INSERT INTO `user_has_role` (`user_id`, `role_id`) VALUES (6, 9);
/*Add Quality user*/
INSERT INTO `vm_user` (`id`, `username`, `password`, `email`, `first_name`, `last_name`, `locale`, `user_status_id`) VALUES (7, 'quality', 'd66636b253cb346dbb6240e30def3618', '', 'Quality', 'Assurance', 'en', 1);
INSERT INTO `user_has_role` (`user_id`, `role_id`) VALUES (7, 10);
/*Update version*/
UPDATE `vm_setting` SET `int_val` = 2 WHERE id = 3;
/*Add permissions*/
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (7, 8);
/*Attachment Types*/
INSERT INTO `attachment_type` (`id`, `description`, `TYPE`) VALUES (1, 'plain text', 'txt');
INSERT INTO `attachment_type` (`id`, `description`, `TYPE`) VALUES (2, 'PDF', 'pdf');
INSERT INTO `attachment_type` (`id`, `description`, `TYPE`) VALUES (3, 'Legacy Word Document', 'doc');
INSERT INTO `attachment_type` (`id`, `description`, `TYPE`) VALUES (4, 'Word Document', 'docx');
INSERT INTO `attachment_type` (`id`, `description`, `TYPE`) VALUES (5, 'Legacy Excel Document', 'xls');
INSERT INTO `attachment_type` (`id`, `description`, `TYPE`) VALUES (6, 'Excel Document', 'xlsx');
INSERT INTO `attachment_type` (`id`, `description`, `TYPE`) VALUES (7, 'Legacy Power Point Document', 'ppt');
INSERT INTO `attachment_type` (`id`, `description`, `TYPE`) VALUES (8, 'Power Point Document', 'pptx');
INSERT INTO `attachment_type` (`id`, `description`, `TYPE`) VALUES (9, 'Comment', 'comment');
INSERT INTO `attachment_type` (`id`, `description`, `TYPE`) VALUES (10, 'undefined', '');
/*Settings*/
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (8, 'show.expected.result', 1, 0, '0', '');
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (9, 'openoffice.port', 0, 1000, '0', '');
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (10, 'openoffice.home', 0, 0, '0', '');
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (11, 'quality.review', 1, 0, '0', '');
/*Issue Types*/
INSERT INTO `issue_type` (`id`, `type_name`, `description`) VALUES (1, 'bug.name', 'bug.desc');
INSERT INTO `issue_type` (`id`, `type_name`, `description`) VALUES (2, 'observation.name', 'observation.desc');
INSERT INTO `issue_type` (`id`, `type_name`, `description`) VALUES (3, 'step.issue.name', 'step.issue.desc');
/*Issue Resolutions*/
INSERT INTO `issue_resolution` (`id`, `name`) VALUES (1, 'issue.invalid');
INSERT INTO `issue_resolution` (`id`, `name`) VALUES (2, 'issue.not.reproduceable');
INSERT INTO `issue_resolution` (`id`, `name`) VALUES (3, 'issue.fixed');
