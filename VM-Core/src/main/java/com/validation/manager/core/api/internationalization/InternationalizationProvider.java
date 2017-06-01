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
package com.validation.manager.core.api.internationalization;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface InternationalizationProvider {

    /**
     * Translate a string.
     *
     * @param mess String to translate
     * @return Translated string or the same string if no translation found.
     */
    public String translate(String mess);

    /**
     * Translate a string.
     *
     * @param mess String to translate
     * @param l Locale to translate into.
     * @return Translated string or the same string if no translation found.
     */
    public String translate(String mess, Locale l);

    /**
     * Get the ResourceBundle.
     *
     * @return The resource bundle.
     */
    public ResourceBundle getResourceBundle();
}
