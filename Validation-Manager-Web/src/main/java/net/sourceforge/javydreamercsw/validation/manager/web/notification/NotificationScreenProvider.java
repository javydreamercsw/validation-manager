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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.TextRenderer;
import net.sourceforge.javydreamercsw.validation.manager.web.core.IMainContentProvider;
import com.validation.manager.core.VMException;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.Notification;
import com.validation.manager.core.server.core.NotificationServer;
import com.validation.manager.core.server.core.VMSettingServer;
import com.vaadin.flow.component.UI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.core.VMUI;
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
        List<Notification> notifications
                = ValidationManagerUI.getInstance().getUser().getNotificationList();
        TextArea text = new TextArea(TRANSLATOR.translate("general.text"));
        text.setReadOnly(true);
        text.setSizeFull();
        Grid<Notification> grid = new Grid<>();
        grid.setItems(notifications);
        grid.addColumn(new TextRenderer<>(value -> Lookup.getDefault()
                .lookup(InternationalizationProvider.class)
                .translate(((com.validation.manager.core.db.Notification) value)
                        .getNotificationType().getTypeName())))
                .setKey("notificationType")
                .setHeader(TRANSLATOR.translate("notification.type"));
        grid.addColumn(n -> n.getAuthor())
                .setKey("author")
                .setHeader(TRANSLATOR.translate("notification.author"));
        java.text.DateFormat format = new SimpleDateFormat(
                VMSettingServer.getSetting("date.format").getStringVal());
        grid.addColumn(n -> n.getCreationDate() == null ? ""
                : format.format(n.getCreationDate()))
                .setKey("creationDate")
                .setHeader(TRANSLATOR.translate("creation.time"));
        grid.addColumn(n -> n.getArchieved()
                ? TRANSLATOR.translate("general.yes")
                : TRANSLATOR.translate("general.no"))
                .setKey("archieved")
                .setHeader(TRANSLATOR.translate("general.archived"));
        if (!notifications.isEmpty()) {
            //v8 capped the grid at 5 rows; Flow shows all rows instead.
            grid.setAllRowsVisible(true);
        }
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setSizeFull();
        //The gridutil date filter row has no Flow equivalent; the date is
        //still sortable via the column header.
        ContextMenu menu = new ContextMenu(grid);
        menu.addItem(TRANSLATOR.translate("notification.mark.unread"),
                e -> {
                    Notification selected = grid.getSelectedItems().stream()
                            .findFirst().orElse(null);
                    if (selected != null) {
                        NotificationServer ns
                                = new NotificationServer(selected);
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
                e -> {
                    Notification selected = grid.getSelectedItems().stream()
                            .findFirst().orElse(null);
                    if (selected != null) {
                        NotificationServer ns
                                = new NotificationServer(selected);
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
            Notification selected = grid.getSelectedItems().stream()
                    .findFirst().orElse(null);
            if (selected != null) {
                text.setReadOnly(false);
                text.setValue(selected.getContent());
                text.setReadOnly(true);
                if (selected.getAcknowledgeDate() != null) {
                    try {
                        //Mark as read
                        NotificationServer ns
                                = new NotificationServer(selected);
                        ns.setAcknowledgeDate(new Date());
                        ns.write2DB();
                    } catch (VMException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        vs.add(grid);
        vs.add(text);
        vs.setSizeFull();
        vs.setId(getComponentCaption());
        return vs;
    }
}
