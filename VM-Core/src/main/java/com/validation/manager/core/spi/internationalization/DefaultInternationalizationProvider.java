package com.validation.manager.core.spi.internationalization;

import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.api.internationalization.LocaleListener;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders({
    @ServiceProvider(service = InternationalizationProvider.class, position = 1)
    ,@ServiceProvider(service = LocaleListener.class)})
public class DefaultInternationalizationProvider
        implements InternationalizationProvider {

    private ResourceBundle RB = ResourceBundle.getBundle(
            "com.validation.manager.resources.VMMessages", Locale.getDefault());
    private static final Logger LOG
            = Logger.getLogger(DefaultInternationalizationProvider.class
                    .getSimpleName());

    @Override
    public String translate(String mess) {
        try {
            if (RB.containsKey(mess)) {
                return RB.containsKey(mess) ? new String(RB.getString(mess)
                        .getBytes("ISO-8859-1"), "UTF-8") : mess;
            } else {
                //Try to search on any other provider
                for (InternationalizationProvider i
                        : Lookup.getDefault()
                                .lookupAll(InternationalizationProvider.class)) {
                    if (!(i instanceof DefaultInternationalizationProvider)
                            && i.getResourceBundle().containsKey(mess)) {
                        return i.translate(mess);
                    }
                }
            }
        }
        catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return mess;
    }

    @Override
    public void setlocale(Locale locale) {
        RB = ResourceBundle.getBundle(
                "com.validation.manager.resources.VMMessages", locale);
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return RB;
    }
}
