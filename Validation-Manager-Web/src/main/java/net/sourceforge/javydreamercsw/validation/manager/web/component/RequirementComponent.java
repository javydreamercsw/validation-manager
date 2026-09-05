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
package net.sourceforge.javydreamercsw.validation.manager.web.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import com.validation.manager.core.server.core.RequirementServer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementComponent extends VerticalLayout {

    private final Requirement req;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(RequirementSpecComponent.class.getSimpleName());

    public RequirementComponent(Requirement r, boolean edit) {
        this.req = r;
        this.edit = edit;
        init();
    }

    public RequirementComponent(Requirement r, boolean edit, String caption) {
        this.req = r;
        this.edit = edit;
        add(new com.vaadin.flow.component.html.Span(caption));
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        add(layout);
        Binder<Requirement> binder = new Binder<>(Requirement.class);
        binder.setBean(req);
        TextField id = new TextField(TRANSLATOR.translate("requirement.id"));
        binder.bind(id, "uniqueId");
        layout.add(id);
        TextArea desc = new TextArea(TRANSLATOR.translate("general.description"));
        binder.bind(desc, "description");
        desc.setSizeFull();
        layout.add(desc);
        TextArea notes = new TextArea(TRANSLATOR.translate("general.notes"));
        binder.bind(notes, "notes");
        notes.setSizeFull();
        layout.add(notes);
        if (req.getParentRequirementId() != null) {
            TextField tf = new TextField(TRANSLATOR.translate("general.parent"));
            tf.setValue(req.getParentRequirementId().getUniqueId());
            tf.setReadOnly(true);
            layout.add(tf);
        }
        if (req.getRequirementList() == null) {
            req.setRequirementList(new ArrayList<>());
        }
        if (!req.getRequirementList().isEmpty() && !edit) {
            layout.add(((ValidationManagerUI) UI.getCurrent())
                    .getDisplayRequirementList(
                            TRANSLATOR.translate("related.requirements"),
                            req.getRequirementList()));
        } else if (edit) {
            //Allow user to add children
            com.vaadin.flow.data.selection.MultiSelect<?, Requirement> as
                    = ((ValidationManagerUI) UI.getCurrent())
                            .getRequirementSelectionComponent();
            as.setValue(new HashSet<>(req.getRequirementList()));
            as.addValueChangeListener(event -> {
                Set<Requirement> selected = event.getValue();
                req.getRequirementList().clear();
                selected.forEach(r -> {
                    req.getRequirementList().add(r);
                });
            });
            layout.add((com.vaadin.flow.component.Component) as);
        }
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((event) -> {
            if (req.getId() == null) {
                ((ValidationManagerUI) UI.getCurrent())
                        .displayObject(req.getRequirementSpecNode());
            } else {
                ((ValidationManagerUI) UI.getCurrent())
                        .displayObject(req, false);
            }
        });
        if (edit) {
            if (req.getId() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((event) -> {
                    req.setUniqueId(id.getValue().toString());
                    req.setNotes(notes.getValue().toString());
                    req.setDescription(desc.getValue().toString());
                    req.setRequirementSpecNode((RequirementSpecNode) ((ValidationManagerUI) UI
                            .getCurrent()).getSelectdValue());
                    new RequirementJpaController(DataBaseManager
                            .getEntityManagerFactory()).create(req);
                    setVisible(false);
                    //Recreate the tree to show the addition
                    ((ValidationManagerUI) UI.getCurrent()).buildProjectTree(req);
                    ((ValidationManagerUI) UI.getCurrent()).displayObject(req, true);
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(save, cancel);
                layout.add(hl);
            } else {
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((event) -> {
                    try {
                        RequirementServer rs = new RequirementServer(req);
                        rs.setDescription(desc.getValue());
                        rs.setNotes(notes.getValue());
                        rs.setUniqueId(id.getValue());
                        ((ValidationManagerUI) UI.getCurrent()).handleVersioning(rs, () -> {
                            try {
                                rs.write2DB();
                                //Recreate the tree to show the addition
                                ((ValidationManagerUI) UI.getCurrent())
                                        .buildProjectTree(rs.getEntity());
                                ((ValidationManagerUI) UI.getCurrent())
                                        .displayObject(rs.getEntity(), false);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update"));
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update"));
                            }
                        });
                        setVisible(false);
                    } catch (VMException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.update"));
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(update, cancel);
                layout.add(hl);
            }
        }
        try {
            //Add a history section
            if (req.getId() != null) {
                List<History> versions
                        = new RequirementServer(req).getHistoryList();
                if (!versions.isEmpty()) {
                    layout.add(((ValidationManagerUI) UI.getCurrent())
                            .createRequirementHistoryTable(
                                    TRANSLATOR.translate("general.history"),
                                    versions, true));
                }
            }
        } catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        binder.setReadOnly(!edit);
        setSizeFull();
    }

}
