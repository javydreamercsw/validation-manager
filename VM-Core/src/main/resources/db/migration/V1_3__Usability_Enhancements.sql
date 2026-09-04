/*Add settings*/
INSERT INTO "VM_SETTING" ("ID", "SETTING", "BOOL_VAL", "INT_VAL", "LONG_VAL", "STRING_VAL") VALUES (12, 'date.format', 0, 0, '0', 'MM-dd-yyyy hh:hh:ss');
/*Notification Types*/
INSERT INTO "NOTIFICATION_TYPE" ("ID", "TYPE_NAME") VALUES (1, 'general.notification');
INSERT INTO "NOTIFICATION_TYPE" ("ID", "TYPE_NAME") VALUES (2, 'notification.test.pending');
INSERT INTO "NOTIFICATION_TYPE" ("ID", "TYPE_NAME") VALUES (3, 'notification.review.pending');