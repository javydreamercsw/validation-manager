package net.sourceforge.javydreamercsw.validation.manager.web.notification;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.db.Notification;
import com.validation.manager.core.db.NotificationType;
import com.validation.manager.core.db.controller.NotificationTypeJpaController;
import java.util.Locale;
import net.sourceforge.javydreamercsw.validation.manager.web.UserToStringConverter;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.AbstractProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IMainContentProvider.class)
public class NotificationScreenProvider extends AbstractProvider {

    @Override
    public boolean shouldDisplay() {
        //Show whenever an user is logged in.
        return ValidationManagerUI.getInstance().getUser() != null;
    }

    @Override
    public String getComponentCaption() {
        return "general.notifications";
    }

    @Override
    public Component getContent() {
        //On top put a list of notifications
        BeanItemContainer<Notification> container
                = new BeanItemContainer<>(Notification.class);
        ValidationManagerUI.getInstance().getUser().getNotificationList().forEach(n -> {
            container.addBean(n);
        });
        Grid grid = new Grid(ValidationManagerUI.getInstance()
                .translate("general.notifications"), container);
        grid.setColumns("notificationType", "author", "creationDate", "content");
        Column nt = grid.getColumn("notificationType");
        nt.setHeaderCaption(ValidationManagerUI.getInstance()
                .translate("notification.type"));
        nt.setConverter(new Converter<String, NotificationType>() {
            @Override
            public NotificationType convertToModel(String value,
                    Class<? extends NotificationType> targetType,
                    Locale locale) throws Converter.ConversionException {
                for (NotificationType n : new NotificationTypeJpaController(DataBaseManager
                        .getEntityManagerFactory())
                        .findNotificationTypeEntities()) {
                    if (ValidationManagerUI.getInstance()
                            .translate(n.getTypeName()).equals(value)) {
                        return n;
                    }
                }
                return null;
            }

            @Override
            public String convertToPresentation(NotificationType value,
                    Class<? extends String> targetType, Locale locale)
                    throws Converter.ConversionException {
                return ValidationManagerUI.getInstance()
                        .translate(value.getTypeName());
            }

            @Override
            public Class<NotificationType> getModelType() {
                return NotificationType.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });
        Column author = grid.getColumn("author");
        author.setConverter(new UserToStringConverter());
        author.setHeaderCaption(ValidationManagerUI.getInstance()
                .translate("notification.author"));
        Column creation = grid.getColumn("creationDate");
        creation.setHeaderCaption(ValidationManagerUI.getInstance()
                .translate("creation.time"));
        Column content = grid.getColumn("content");
        content.setHeaderCaption(ValidationManagerUI.getInstance()
                .translate("general.text"));
        grid.setSizeFull();
        return grid;
    }
}
