package net.sourceforge.javydreamercsw.validation.manager.web;

import com.vaadin.data.util.converter.Converter;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.VmUserJpaController;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserToStringConverter implements Converter<String, VmUser> {

    private final VmUserJpaController controller
            = new VmUserJpaController(DataBaseManager.getEntityManagerFactory());

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
        StringTokenizer st = new StringTokenizer(value, " ");
        String name = st.nextToken();
        String last = st.nextToken();
        for (VmUser user : controller.findVmUserEntities()) {
            if (user.getFirstName().equals(name)
                    && user.getLastName().equals(last)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public String convertToPresentation(VmUser value,
            Class<? extends String> targetType, Locale locale) throws ConversionException {
        return value.getFirstName() + " " + value.getLastName();
    }
}
