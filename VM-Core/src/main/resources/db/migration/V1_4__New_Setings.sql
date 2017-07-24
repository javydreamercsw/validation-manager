/*Add settings*/
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (13, 'mail.smtp.host', 0, 0, '0', 'smtp.gmail.com');
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (14, 'mail.smtp.socketFactory.class', 0, 0, '0', 'javax.net.ssl.SSLSocketFactory');
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (15, 'mail.smtp.socketFactory.fallback', 0, 0, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (16, 'mail.smtp.port', 0, 465, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (17, 'mail.smtp.socketFactory.port', 0, 465, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (18, 'mail.smtp.auth', 1, 0, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (19, 'mail.smtp.quitwait', 0, 0, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (20, 'mail.smtp.starttls.enable', 1, 0, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (21, 'mail.auth.username', 1, 0, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (22, 'mail.auth.password', 1, 0, '0', NULL);
INSERT INTO `vm_setting` (`id`, `setting`, `bool_val`, `int_val`, `long_val`, `string_val`) VALUES (23, 'mail.enable', 0, 0, '0', NULL);
/*Fix typos*/
UPDATE `role` set description = 'reserved.desc' where id in (1,2);