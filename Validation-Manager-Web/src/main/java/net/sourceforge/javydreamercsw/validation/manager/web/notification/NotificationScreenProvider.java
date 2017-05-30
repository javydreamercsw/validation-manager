package net.sourceforge.javydreamercsw.validation.manager.web.notification;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.IMainContentProvider;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.Notification;
import com.validation.manager.core.db.NotificationType;
import com.validation.manager.core.db.controller.NotificationTypeJpaController;
import com.validation.manager.core.server.core.NotificationServer;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.UserToStringConverter;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.AbstractProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IMainContentProvider.class)
public class NotificationScreenProvider extends AbstractProvider {

    private static final Logger LOG
            = Logger.getLogger(NotificationScreenProvider.class.getSimpleName());
    private VerticalLayout vs = null;
    private final InternationalizationProvider TRANSLATOR = Lookup.getDefault()
            .lookup(InternationalizationProvider.class);

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
        if (vs == null) {
            update();
        }
        return vs;
    }

    @Override
    public void update() {
        vs = new VerticalLayout();
        super.update();
        //On top put a list of notifications
        BeanItemContainer<Notification> container
                = new BeanItemContainer<>(Notification.class);
        ValidationManagerUI.getInstance().getUser().getNotificationList()
                .forEach(n -> {
                    container.addBean(n);
                });
//        Unable to use VerticalSplitPanel as I hoped.
//        See: https://github.com/vaadin/framework/issues/9460
//        VerticalSplitPanel vs = new VerticalSplitPanel();
//        vs.setSplitPosition(25, Sizeable.Unit.PERCENTAGE);
        TextArea text = new TextArea(TRANSLATOR.translate("general.text"));
        text.setWordwrap(true);
        text.setReadOnly(true);
        text.setSizeFull();
        Grid grid = new Grid(TRANSLATOR.translate("general.notifications"), container);
        grid.setColumns("notificationType", "author", "creationDate");
        if (container.size() > 0) {
            grid.setHeightMode(HeightMode.ROW);
            grid.setHeightByRows(container.size() > 5 ? 5 : container.size());
        }
        Column nt = grid.getColumn("notificationType");
        nt.setHeaderCaption(TRANSLATOR.translate("notification.type"));
        nt.setConverter(new Converter<String, NotificationType>() {
            @Override
            public NotificationType convertToModel(String value,
                    Class<? extends NotificationType> targetType,
                    Locale locale) throws Converter.ConversionException {
                for (NotificationType n : new NotificationTypeJpaController(DataBaseManager
                        .getEntityManagerFactory())
                        .findNotificationTypeEntities()) {
                    if (Lookup.getDefault().lookup(InternationalizationProvider.class)
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
                return Lookup.getDefault().lookup(InternationalizationProvider.class)
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
        author.setHeaderCaption(TRANSLATOR.translate("notification.author"));
        Column creation = grid.getColumn("creationDate");
        creation.setHeaderCaption(TRANSLATOR.translate("creation.time"));
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setSizeFull();
        ContextMenu menu = new ContextMenu(grid, true);
        grid.addSelectionListener(selectionEvent -> {
            // Get selection from the selection model
            Object selected = ((SingleSelectionModel) grid.getSelectionModel())
                    .getSelectedRow();
            if (selected != null) {
                text.setReadOnly(false);
                Notification n = (Notification) selected;
                text.setValue(n.getContent());
                text.setReadOnly(true);
                if (n.getAcknowledgeDate() != null) {
                    try {
                        //Mark as read
                        NotificationServer ns
                                = new NotificationServer((Notification) n);
                        ns.setAcknowledgeDate(new Date());
                        ns.write2DB();
                        ((VMUI) UI.getCurrent()).updateScreen();
                        ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vs.addComponent(grid);
        vs.addComponent(text);
        vs.setSizeFull();
        vs.setId(getComponentCaption());
    }
}
