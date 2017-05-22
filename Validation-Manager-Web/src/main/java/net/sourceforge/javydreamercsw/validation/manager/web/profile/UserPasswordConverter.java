package net.sourceforge.javydreamercsw.validation.manager.web.profile;

import com.vaadin.data.util.converter.Converter;
import com.validation.manager.core.tool.MD5;
import java.util.Locale;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserPasswordConverter implements Converter<String, String> {

    private String pw;

    @Override
    public String convertToModel(String value,
            Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        pw = value;
        return pw;
    }

    @Override
    public String convertToPresentation(String value,
            Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        try {
            return MD5.encrypt(value);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }

    @Override
    public Class<String> getModelType() {
        return String.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
