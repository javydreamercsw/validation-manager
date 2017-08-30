/*Risk Management Roles*/
INSERT INTO `user_right` (`id`, `description`) VALUES (18, 'risk.management.editor');
INSERT INTO `user_right` (`id`, `description`) VALUES (19, 'risk.management.view');

/*Assign to roles*/
/*Admin*/
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 18);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (8, 19);
/*Quality*/
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (10, 18);
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (10, 19);
/*Test Designer*/
INSERT INTO `role_has_right` (`role_id`, `right_id`) VALUES (4, 19);