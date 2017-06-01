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
package com.validation.manager.core.spi.email;

import com.validation.manager.core.api.email.IEmailManager;
import com.validation.manager.core.server.core.VMSettingServer;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IEmailManager.class)
public class EmailManager implements IEmailManager {

    @Override
    public void sendEmail(String to, String cc, String from,
            String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host",
                VMSettingServer.getSetting("mail.smtp.host").getStringVal());
        props.put("mail.smtp.auth", VMSettingServer.getSetting("mail.smtp.auth")
                .getBoolVal().toString());
        props.put("mail.debug", "true");
        props.put("mail.smtp.starttls.enable",
                VMSettingServer.getSetting("mail.smtp.starttls.enable")
                        .getBoolVal().toString());
        props.put("mail.smtp.port", ""
                + VMSettingServer.getSetting("mail.smtp.port").getIntVal());
        props.put("mail.smtp.socketFactory.port", ""
                + VMSettingServer.getSetting("mail.smtp.socketFactory.port").getIntVal());
        props.put("mail.smtp.socketFactory.class",
                VMSettingServer.getSetting("mail.smtp.socketFactory.class")
                        .getStringVal());
        props.put("mail.smtp.socketFactory.fallback",
                VMSettingServer.getSetting("mail.smtp.socketFactory.fallback")
                        .getBoolVal().toString());
        Session mailSession;
        String username = VMSettingServer.getSetting("mail.auth.username")
                .getStringVal();
        String password = VMSettingServer.getSetting("mail.auth.password")
                .getStringVal();
        if (username != null) {
            mailSession = Session.getInstance(props, new javax.mail.Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            mailSession = Session.getInstance(props);
        }
        mailSession.setDebug(true); // Enable the debug mode

        Message email = new MimeMessage(mailSession);
        //--[ Set the FROM, TO, DATE and SUBJECT fields
        email.setFrom(new InternetAddress(from));
        email.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
        email.setSentDate(new Date());
        email.setSubject(subject);
        if (cc != null && !cc.isEmpty()) {
            email.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(cc));
        }
        //--[ Create the body of the mail
        email.setText(bodyText);
        //--[ Ask the Transport class to send our mail message
        Transport.send(email);
    }
}
