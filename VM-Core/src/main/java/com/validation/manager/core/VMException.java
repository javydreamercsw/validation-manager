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
package com.validation.manager.core;

import java.util.List;
import static java.util.Locale.getDefault;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class VMException extends Exception {

    private String vm_message = "";
    protected static ResourceBundle rb
            = getBundle(
                    "com.validation.manager.resources.VMMessages",
                    getDefault());

    public VMException() {
        super();
    }

    public VMException(String message) {
        super(rb.containsKey(message) ? rb.getString(message) : message);
    }

    public VMException(List<String> messages) {
        messages.forEach((s) -> {
            vm_message += s + "\n";
        });
    }

    public VMException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return vm_message.isEmpty() ? super.getLocalizedMessage()
                : vm_message;
    }
}
