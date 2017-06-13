/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.javydreamercsw.validation.manager.web.notification;

import com.validation.manager.core.api.email.IEmailManager;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.api.notification.INotificationManager;
import com.validation.manager.core.api.notification.NotificationTypes;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.server.core.NotificationServer;
import com.validation.manager.core.server.core.NotificationTypeServer;
import com.validation.manager.core.server.core.VMSettingServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.Date;
import java.util.Locale;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = INotificationManager.class)
public class NotificationManager implements INotificationManager {

    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);

    @Override
    public void addNotification(String message, NotificationTypes type,
            VmUser target, VmUser author) throws Exception {
        NotificationServer ns = new NotificationServer();
        ns.setAuthor(author);
        ns.setTargetUser(target);
        ns.setContent(message);
        ns.setCreationDate(new Date());
        String selected;
        switch (type) {
            case TEST:
                selected = "notification.test.pending";
                break;
            case REVIEW:
                selected = "notification.review.pending";
                break;
            default:
                selected = "general.notification";
        }
        ns.setNotificationType(NotificationTypeServer.getType(selected));
        ns.write2DB();
        //If email is configured, send the user an email as well.
        if (VMSettingServer.getSetting("mail.enable").getBoolVal()
                && !target.getEmail().isEmpty()) {
            Lookup.getDefault().lookup(IEmailManager.class)
                    .sendEmail(target.getEmail(), null,
                            author.getEmail().isEmpty()
                            ? new VMUserServer(1).getEmail()
                            : author.getEmail(),
                            TRANSLATOR.translate(selected,
                                    new Locale(target.getLocale())),
                            TRANSLATOR.translate("notification.new",
                                    new Locale(target.getLocale())));
        }
    }
}
