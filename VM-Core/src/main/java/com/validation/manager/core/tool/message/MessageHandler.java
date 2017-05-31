package com.validation.manager.core.tool.message;

/**
 * This handles various messages for display to the user.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface MessageHandler {

    /**
     * Display a message.
     *
     * @param message Message
     * @param messageType Message type
     */
    public void show(String message, MessageType messageType);

    /**
     * Display a message.
     *
     * @param message Message
     */
    public void error(String message);

    /**
     * Show an information dialog
     *
     * @param message Message
     */
    public void info(String message);

    /**
     * Show an question dialog
     *
     * @param message Message
     */
    public void question(String message);

    /**
     * Show an warning dialog
     *
     * @param message Message
     */
    public void warn(String message);

    /**
     * Show an plain dialog
     *
     * @param message Message
     */
    public void plain(String message);
}
