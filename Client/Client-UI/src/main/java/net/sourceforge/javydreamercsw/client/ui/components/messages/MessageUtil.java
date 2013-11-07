package net.sourceforge.javydreamercsw.client.ui.components.messages;

import com.validation.manager.core.tool.message.MessageHandler;
import com.validation.manager.core.tool.message.MessageType;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MessageHandler.class)
public class MessageUtil implements MessageHandler {

    public MessageUtil() {
    }

    /**
     * @return The dialog displayer used to show message boxes
     */
    public static DialogDisplayer getDialogDisplayer() {
        return DialogDisplayer.getDefault();
    }

    /**
     * Show a message of the specified type
     *
     * @param message
     * @param messageType As in {@link NotifyDescription} message type
     * constants.
     */
    @Override
    public void show(String message, MessageType messageType) {
        getDialogDisplayer().notify(new NotifyDescriptor.Message(message,
                messageType.getNotifyDescriptorType()));
    }

    /**
     * Show an information dialog
     *
     * @param message
     */
    @Override
    public void info(String message) {
        show(message, MessageType.INFO);
    }

    /**
     * Show an error dialog
     *
     * @param message
     */
    @Override
    public void error(String message) {
        show(message, MessageType.ERROR);
    }

    /**
     * Show an question dialog
     *
     * @param message
     */
    @Override
    public void question(String message) {
        show(message, MessageType.QUESTION);
    }

    /**
     * Show an warning dialog
     *
     * @param message
     */
    @Override
    public void warn(String message) {
        show(message, MessageType.WARNING);
    }

    /**
     * Show an plain dialog
     *
     * @param message
     */
    @Override
    public void plain(String message) {
        show(message, MessageType.PLAIN);
    }
}
