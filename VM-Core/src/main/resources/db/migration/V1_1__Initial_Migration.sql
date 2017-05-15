/*Add user status*/
INSERT INTO `user_status` (`id`, `status`, `description`) VALUES (1, 'user.status.active', 'user.status.active.desc');
INSERT INTO `user_status` (`id`, `status`, `description`) VALUES (2, 'user.status.inactive', 'user.status.inactive.desc');
INSERT INTO `user_status` (`id`, `status`, `description`) VALUES (3, 'user.status.locked', 'user.status.locked.desc');
INSERT INTO `user_status` (`id`, `status`, `description`) VALUES (4, 'user.status.aged', 'user.status.aged.desc');
/*Add roles*/
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES (1, 'reserved1', 'reserver.desc');
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES (2, 'reserved2', 'reserved2.desc');
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES (3, 'none', 'none.desc');
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES (4, 'test.designer', 'test.designer.desc');
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES (5, 'guest', 'guest.desc');
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES (6, 'senior.tester', 'senior.tester.desc');
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES (7, 'tester', 'tester.desc');
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES (8, 'admin', 'admin.desc');
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES (9, 'leader', 'leader.desc');
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES (10, 'quality', 'quality.desc');
/*Add user rights*/
INSERT INTO `user_right` (`id`, `description`) VALUES (1, 'testplan.execute');
INSERT INTO `user_right` (`id`, `description`) VALUES (2, 'testplan.create.build');
INSERT INTO `user_right` (`id`, `description`) VALUES (3, 'testplan.metrics');
INSERT INTO `user_right` (`id`, `description`) VALUES (4, 'testplan.planning');
INSERT INTO `user_right` (`id`, `description`) VALUES (5, 'testplan.user_role.assigment');
INSERT INTO `user_right` (`id`, `description`) VALUES (6, 'testcase.view');
INSERT INTO `user_right` (`id`, `description`) VALUES (7, 'testcase.modify');
INSERT INTO `user_right` (`id`, `description`) VALUES (8, 'requirement.view');
INSERT INTO `user_right` (`id`, `description`) VALUES (9, 'requirement.modify');
INSERT INTO `user_right` (`id`, `description`) VALUES (10, 'product.modify');
INSERT INTO `user_right` (`id`, `description`) VALUES (11, 'manage.users');
INSERT INTO `user_right` (`id`, `description`) VALUES (12, 'testplan.create');
INSERT INTO `user_right` (`id`, `description`) VALUES (13, 'events.view');
INSERT INTO `user_right` (`id`, `description`) VALUES (14, 'events.management');
INSERT INTO `user_right` (`id`, `description`) VALUES (15, 'system.configuration');
INSERT INTO `user_right` (`id`, `description`) VALUES (16, 'project.viewer');
INSERT INTO `user_right` (`id`, `description`) VALUES (17, 'quality.assurance');
/*Set rights per role*/
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 1);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 2);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 3);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 4);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 5);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 6);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 7);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 8);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 9);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 10);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 11);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 12);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 13);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 14);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 15);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 16);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 17);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (5, 3);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (5, 6);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (4, 3);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (4, 6);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (4, 7);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (4, 8);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (4, 9);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (7, 1);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (7, 3);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (7, 6);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (6, 1);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (6, 2);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (6, 3);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (6, 6);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (6, 7);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (6, 9);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (9, 1);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (9, 2);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (9, 3);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (9, 4);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (9, 5);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (9, 6);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (9, 7);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (9, 8);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (9, 9);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (9, 12);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (10, 2);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (10, 5);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (10, 6);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (10, 8);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (10, 9);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (10, 12);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (10, 16);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (10, 17);
/*Add assignment types*/
INSERT INTO `assigment_type` (`id`, `fk_table`, `description`) VALUES (1, 'test_plan_has_test', 'testcase.execution');
INSERT INTO `assigment_type` (`id`, `fk_table`, `description`) VALUES (2, 'test_case', 'testcase.review');
/*Add assignment statuses*/
INSERT INTO `assignment_status` (`id`, `name`, `description`) VALUES (1, 'assignment.status.open', 'assignment.status.open.desc');
INSERT INTO `assignment_status` (`id`, `name`, `description`) VALUES (2, 'assignment.status.closed', 'assignment.status.closed.desc');
INSERT INTO `assignment_status` (`id`, `name`, `description`) VALUES (3, 'assignment.status.completed', 'assignment.status.completed.desc');
INSERT INTO `assignment_status` (`id`, `name`, `description`) VALUES (4, 'assignment.status..todo.urgent', 'assignment.status.todo.urgent.desc');
INSERT INTO `assignment_status` (`id`, `name`, `description`) VALUES (5, 'assignment.status.todo', 'assignment.status.todo.desc');
/*Add requirement types*/
INSERT INTO `requirement_type` (`id`, `name`, `description`, `level`) VALUES (1, 'UN', 'User Need', 0);
INSERT INTO `requirement_type` (`id`, `name`, `description`, `level`) VALUES (2, 'PS', 'Product Specification', 1);
INSERT INTO `requirement_type` (`id`, `name`, `description`, `level`) VALUES (3, 'SA', 'System Architecture', 2);
INSERT INTO `requirement_type` (`id`, `name`, `description`, `level`) VALUES (4, 'HW', 'Hardware', 3);
INSERT INTO `requirement_type` (`id`, `name`, `description`, `level`) VALUES (5, 'SW', 'Software', 3);
INSERT INTO `requirement_type` (`id`, `name`, `description`, `level`) VALUES (6, 'Labeling', 'Labeling requirements', 1);
INSERT INTO `requirement_type` (`id`, `name`, `description`, `level`) VALUES (7, 'Standards', 'Related to applicable standards', 1);

/*Add requirement statuses*/
INSERT INTO `requirement_status` (`id`, `status`) VALUES (1, 'general.open');
INSERT INTO `requirement_status` (`id`, `status`) VALUES (2, 'general.approved');
INSERT INTO `requirement_status` (`id`, `status`) VALUES (3, 'general.obsolete');
INSERT INTO `requirement_status` (`id`, `status`) VALUES (4, 'general.rejected');
/*Add Spec levels*/
INSERT INTO `spec_level` (`id`, `name`, `description`) VALUES (1, 'spec.level.user.need', 'spec.level.user.need.desc');
INSERT INTO `spec_level` (`id`, `name`, `description`) VALUES (2, 'spec.level.requirement', 'spec.level.requirement.desc');
INSERT INTO `spec_level` (`id`, `name`, `description`) VALUES (3, 'spec.level.system', 'spec.level.system.desc');
INSERT INTO `spec_level` (`id`, `name`, `description`) VALUES (4, 'spec.level.arch', 'spec.level.arch.desc');
INSERT INTO `spec_level` (`id`, `name`, `description`) VALUES (5, 'spec.level.module', 'spec.level.module.desc');
/*Add settings*/
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (1, 'version.high', 0, 0, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (2, 'version.mid', 0, 0, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (3, 'version.low', 0, 1, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (4, 'password.aging', 0, 90, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (5, 'password.attempts', 0, 3, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (6, 'password.unusable_period', 0, 365, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (7, 'version.postfix', 0, 0, '0', 'Alpha');
/*Add Admin user*/
INSERT INTO `vm_user` (`id`, `username`, `password`, `email`, `first_name`, `last_name`, `locale`, `user_status_id`) VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', '', 'System', 'Administrator', 'en', 1);
INSERT INTO `user_has_role` (`user_id`, `role_id`) VALUES (1, 8);