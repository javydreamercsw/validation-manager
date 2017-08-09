This classes are created by NetBeans persistence wizards. 

If the database structure changes this need to be deleted and recreated. 

All of them. Selecting a few might lead to relationships left out of the controller class.

Keep the following things in mind when doing this:

* Run Inspect and Transform on the project looking for Can use Diamond. 
Many of the changes marked by version control will go away.
* If entities have more than one relationship to certain entity, 
the wizard will have some issues getting them right. As the time of writing 
this, NotificationJpaController is one such example. Revert changes changing 
getNotificationList1() to getNotificationList() and viceversa. This is hard 
to track, but it's caught by unit tests.