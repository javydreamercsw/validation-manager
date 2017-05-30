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
