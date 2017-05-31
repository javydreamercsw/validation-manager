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
