package com.validation.manager.core.api.internationalization;

import java.util.ResourceBundle;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public interface InternationalizationProvider extends LocaleListener {

    /**
     * Translate a string.
     *
     * @param mess String to translate
     * @return Translated string or the same string if no translation found.
     */
    public String translate(String mess);

    /**
     * Get the ResourceBundle.
     *
     * @return The resource bundle.
     */
    public ResourceBundle getResourceBundle();
}
