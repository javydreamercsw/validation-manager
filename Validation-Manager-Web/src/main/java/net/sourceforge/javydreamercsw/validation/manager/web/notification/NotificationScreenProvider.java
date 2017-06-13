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
package net.sourceforge.javydreamercsw.validation.manager.web.notification;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.icons.VaadinIcons;
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
import com.validation.manager.core.VMException;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.Notification;
import com.validation.manager.core.db.NotificationType;
import com.validation.manager.core.db.controller.NotificationTypeJpaController;
import com.validation.manager.core.server.core.NotificationServer;
import com.validation.manager.core.server.core.VMSettingServer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.UserToStringConverter;
import net.sourceforge.javydreamercsw.validation.manager.web.provider.AbstractProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.vaadin.gridutil.cell.GridCellFilter;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = IMainContentProvider.class)
public class NotificationScreenProvider extends AbstractProvider {

    private static final Logger LOG
            = Logger.getLogger(NotificationScreenProvider.class.getSimpleName());

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
        VerticalLayout vs = new VerticalLayout();
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
        Grid grid = new Grid(TRANSLATOR.translate("general.notifications"),
                container);
        grid.setColumns("notificationType", "author", "creationDate", "archieved");
        if (container.size() > 0) {
            grid.setHeightMode(HeightMode.ROW);
            grid.setHeightByRows(container.size() > 5 ? 5 : container.size());
        }
        GridCellFilter filter = new GridCellFilter(grid);
        filter.setBooleanFilter("archieved",
                new GridCellFilter.BooleanRepresentation(VaadinIcons.CHECK,
                        TRANSLATOR.translate("general.yes")),
                new GridCellFilter.BooleanRepresentation(VaadinIcons.CLOSE,
                        TRANSLATOR.translate("general.no")));
        filter.setDateFilter("creationDate",
                new SimpleDateFormat(VMSettingServer.getSetting("date.format")
                        .getStringVal()), true);
        grid.sort("creationDate");
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
        Column archive = grid.getColumn("archieved");
        archive.setHeaderCaption(TRANSLATOR.translate("general.archived"));
        archive.setConverter(new Converter<String, Boolean>() {
            @Override
            public Boolean convertToModel(String value,
                    Class<? extends Boolean> targetType,
                    Locale locale) throws Converter.ConversionException {
                return value.equals(TRANSLATOR.translate("general.yes"));
            }

            @Override
            public String convertToPresentation(Boolean value,
                    Class<? extends String> targetType, Locale locale)
                    throws Converter.ConversionException {
                return value ? TRANSLATOR.translate("general.yes")
                        : TRANSLATOR.translate("general.no");
            }

            @Override
            public Class<Boolean> getModelType() {
                return Boolean.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setSizeFull();
        ContextMenu menu = new ContextMenu(grid, true);
        menu.addItem(TRANSLATOR.translate("notification.mark.unread"),
                (MenuItem selectedItem) -> {
                    Object selected = ((SingleSelectionModel) grid.getSelectionModel())
                            .getSelectedRow();
                    if (selected != null) {
                        NotificationServer ns = new NotificationServer((Notification) selected);
                        ns.setAcknowledgeDate(null);
                        try {
                            ns.write2DB();
                            ((VMUI) UI.getCurrent()).updateScreen();
                            ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                        } catch (VMException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                });
        menu.addItem(TRANSLATOR.translate("notification.archive"),
                (MenuItem selectedItem) -> {
                    Object selected = ((SingleSelectionModel) grid.getSelectionModel())
                            .getSelectedRow();
                    if (selected != null) {
                        NotificationServer ns = new NotificationServer((Notification) selected);
                        ns.setArchieved(true);
                        try {
                            ns.write2DB();
                            ((VMUI) UI.getCurrent()).updateScreen();
                            ((VMUI) UI.getCurrent()).showTab(getComponentCaption());
                        } catch (VMException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                });
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
                    } catch (VMException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vs.addComponent(grid);
        vs.addComponent(text);
        vs.setSizeFull();
        vs.setId(getComponentCaption());
        return vs;
    }
}
