package net.sourceforge.javydreamercsw.validation.manager.web.notification;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalSplitPanel;
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
        ValidationManagerUI.getInstance().getUser().getNotificationList()
                .forEach(n -> {
                    container.addBean(n);
                });
        VerticalSplitPanel vs = new VerticalSplitPanel();
        vs.setSplitPosition(25, Sizeable.Unit.PERCENTAGE);
        TextArea text = new TextArea(ValidationManagerUI.getInstance()
                .translate("general.text"));
        text.setWordwrap(true);
        text.setSizeFull();
        Grid grid = new Grid(ValidationManagerUI.getInstance()
                .translate("general.notifications"), container);
        grid.setColumns("notificationType", "author", "creationDate");
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
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setSizeFull();
        grid.addSelectionListener(selectionEvent -> { // Java 8
            // Get selection from the selection model
            Object selected = ((SingleSelectionModel) grid.getSelectionModel())
                    .getSelectedRow();
            if (selected != null) {
                text.setReadOnly(false);
                Notification n = (Notification) selected;
                text.setValue(n.getContent());
                text.setReadOnly(true);
            }
        });
        vs.setFirstComponent(grid);
        vs.setSecondComponent(text);
        vs.setSizeFull();
        vs.setId(getComponentCaption());
        return vs;
    }
}
