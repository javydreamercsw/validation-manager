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
package com.validation.manager.core.tool.message;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public enum MessageType {

    PLAIN(NotifyDescriptor.PLAIN_MESSAGE, null),
    INFO(NotifyDescriptor.INFORMATION_MESSAGE, null),
    QUESTION(NotifyDescriptor.QUESTION_MESSAGE, null),
    ERROR(NotifyDescriptor.ERROR_MESSAGE, null),
    WARNING(NotifyDescriptor.WARNING_MESSAGE, null);

    private final int notifyDescriptorType;
    private final Icon icon;

    private static final Logger LOG
            = Logger.getLogger(MessageType.class.getSimpleName());

    private MessageType(int notifyDescriptorType, String resourceName) {
        this.notifyDescriptorType = notifyDescriptorType;
        if (resourceName == null) {
            icon = new ImageIcon();
        } else {
            icon = loadIcon(resourceName);
        }
    }

    private static Icon loadIcon(String resourceName) {
        //TODO: Use icons
        URL resource = MessageType.class.getResource("icons/" + resourceName);

        if (resource == null) {
            LOG.log(Level.WARNING,
                    "Failed to load NotifyUtil icon resource: {0}. "
                    + "Using blank image.", resourceName);
        }
        return resource == null ? new ImageIcon()
                : ImageUtilities.loadImageIcon(resourceName, false);
    }

    public int getNotifyDescriptorType() {
        return notifyDescriptorType;
    }

    public Icon getIcon() {
        return icon;
    }
}
