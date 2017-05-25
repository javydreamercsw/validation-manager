/*Add settings*/
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (12, 'date.format', 0, 0, '0', 'MM-dd-yyyy hh:hh:ss');
/*Notification Types*/
INSERT INTO `notification_type` (`id`, `type_name`) VALUES (1, 'general.notification');
INSERT INTO `notification_type` (`id`, `type_name`) VALUES (2, 'notification.test.pending');
INSERT INTO `notification_type` (`id`, `type_name`) VALUES (3, 'notification.review.pending');