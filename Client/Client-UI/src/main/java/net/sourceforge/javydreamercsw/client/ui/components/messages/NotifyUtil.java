package net.sourceforge.javydreamercsw.client.ui.components.messages;

import com.validation.manager.core.tool.message.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class NotifyUtil {

    private NotifyUtil() {
    }

    /**
     * Show message with the specified type and action listener
     */
    public static void show(String title, String message, MessageType type,
            ActionListener actionListener) {
        NotificationDisplayer.getDefault().notify(title, type.getIcon(), message,
                actionListener);
    }

    /**
     * Show message with the specified type and a default action which displays
     * the message using {@link MessageUtil} with the same message type
     */
    public static void show(String title, final String message,
            final MessageType type) {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lookup.getDefault().lookup(MessageUtil.class).show(message, type);
            }
        };

        show(title, message, type, actionListener);
    }

    /**
     * Show an information notification
     *
     * @param message
     */
    public static void info(String title, String message) {
        show(title, message, MessageType.INFO);
    }

    /**
     * Show an error notification
     *
     * @param message
     */
    public static void error(String title, String message) {
        show(title, message, MessageType.ERROR);
    }

    /**
     * Show an warning notification
     *
     * @param message
     */
    public static void warn(String title, String message) {
        show(title, message, MessageType.WARNING);
    }

    /**
     * Show an plain notification
     *
     * @param message
     */
    public static void plain(String title, String message) {
        show(title, message, MessageType.PLAIN);
    }
}
