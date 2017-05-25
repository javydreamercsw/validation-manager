package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.data.util.converter.Converter;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.Locale;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserToStringConverter implements Converter<String, VmUser> {

    private VmUser user;

    @Override
    public Class<VmUser> getModelType() {
        return VmUser.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

    @Override
    public VmUser convertToModel(String value,
            Class<? extends VmUser> targetType, Locale locale) throws ConversionException {
        return user;
    }

    @Override
    public String convertToPresentation(VmUser value,
            Class<? extends String> targetType, Locale locale) throws ConversionException {
        user = value;
        return new VMUserServer(value).toString();
    }
}
