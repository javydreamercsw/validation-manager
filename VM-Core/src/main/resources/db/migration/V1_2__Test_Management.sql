/*New Roles*/
UPDATE `role` SET `role_name` = 'requirement.manager', `description` = 'requirement.manager.desc' WHERE id = 1;
/*Add default test results*/
INSERT INTO `execution_result` (`id`, `result_name`) VALUES (1, 'result.pass');
INSERT INTO `execution_result` (`id`, `result_name`) VALUES (2, 'result.fail');
INSERT INTO `execution_result` (`id`, `result_name`) VALUES (3, 'result.blocked');
INSERT INTO `execution_result` (`id`, `result_name`) VALUES (4, 'result.pending');
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
/*Update version*/
UPDATE `vm_setting` SET `int_val` = 2 WHERE id = 3;
/*Add permissions*/
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (7, 8);