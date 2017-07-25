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

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.RequirementServer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementComponent extends Panel {

    private final Requirement req;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(RequirementSpecComponent.class.getSimpleName());

    public RequirementComponent(Requirement r, boolean edit) {
        this.req = r;
        this.edit = edit;
        setCaption(TRANSLATOR.translate("requirement.detail"));
        init();
    }

    public RequirementComponent(Requirement r, boolean edit, String caption) {
        super(caption);
        this.req = r;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(req.getClass());
        binder.setItemDataSource(req);
        TextField id = (TextField) binder.buildAndBind(TRANSLATOR.translate("requirement.id"),
                "uniqueId", TextField.class);
        id.setNullRepresentation("");
        layout.addComponent(id);
        TextArea desc = (TextArea) binder.buildAndBind(TRANSLATOR.translate("general.description"),
                "description",
                TextArea.class);
        desc.setNullRepresentation("");
        desc.setSizeFull();
        layout.addComponent(desc);
        TextArea notes = (TextArea) binder.buildAndBind(TRANSLATOR.translate("general.notes"),
                "notes",
                TextArea.class);
        notes.setNullRepresentation("");
        notes.setSizeFull();
        layout.addComponent(notes);
        if (req.getParentRequirementId() != null) {
            TextField tf = new TextField(TRANSLATOR.translate("general.parent"));
            tf.setValue(req.getParentRequirementId().getUniqueId());
            tf.setReadOnly(true);
            layout.addComponent(tf);
        }
        if (req.getRequirementList() == null) {
            req.setRequirementList(new ArrayList<>());
        }
        if (!req.getRequirementList().isEmpty() && !edit) {
            layout.addComponent(((VMUI) UI.getCurrent()).getDisplayRequirementList(
                    TRANSLATOR.translate("related.requirements"),
                    req.getRequirementList()));
        } else if (edit) {
            //Allow user to add children
            AbstractSelect as = ((VMUI) UI.getCurrent()).getRequirementSelectionComponent();
            req.getRequirementList().forEach(sub -> {
                as.select(sub);
            });
            as.addValueChangeListener(event -> {
                Set<Requirement> selected
                        = (Set<Requirement>) event.getProperty().getValue();
                req.getRequirementList().clear();
                selected.forEach(r -> {
                    req.getRequirementList().add(r);
                });
            });
            layout.addComponent(as);
        }
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (req.getId() == null) {
                ((VMUI) UI.getCurrent()).displayObject(req.getRequirementSpecNode());
            } else {
                ((VMUI) UI.getCurrent()).displayObject(req, false);
            }
        });
        if (edit) {
            if (req.getId() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((Button.ClickEvent event) -> {
                    req.setUniqueId(id.getValue().toString());
                    req.setNotes(notes.getValue().toString());
                    req.setDescription(desc.getValue().toString());
                    req.setRequirementSpecNode((RequirementSpecNode) ((VMUI) UI
                            .getCurrent()).getSelectdValue());
                    new RequirementJpaController(DataBaseManager
                            .getEntityManagerFactory()).create(req);
                    setVisible(false);
                    //Recreate the tree to show the addition
                    ((VMUI) UI.getCurrent()).buildProjectTree(req);
                    ((VMUI) UI.getCurrent()).displayObject(req, true);
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((Button.ClickEvent event) -> {
                    try {
                        RequirementServer rs = new RequirementServer(req);
                        rs.setDescription(((TextArea) desc).getValue());
                        rs.setNotes(((TextArea) notes).getValue());
                        rs.setUniqueId(((TextField) id).getValue());
                        ((VMUI) UI.getCurrent()).handleVersioning(rs, () -> {
                            try {
                                rs.write2DB();
                                //Recreate the tree to show the addition
                                ((VMUI) UI.getCurrent()).buildProjectTree(rs.getEntity());
                                ((VMUI) UI.getCurrent()).displayObject(rs.getEntity(), false);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update"),
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update"),
                                        ex.getLocalizedMessage(),
                                        Notification.Type.ERROR_MESSAGE);
                            }
                        });
                        setVisible(false);
                    } catch (VMException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.update"),
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        try {
            //Add a history section
            if (req.getId() != null) {
                List<History> versions
                        = new RequirementServer(req).getHistoryList();
                if (!versions.isEmpty()) {
                    layout.addComponent(((VMUI) UI.getCurrent())
                            .createRequirementHistoryTable(
                                    TRANSLATOR.translate("general.history"),
                                    versions, true));
                }
            }
        } catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        binder.setBuffered(true);
        binder.setReadOnly(!edit);
        binder.bindMemberFields(this);
        setSizeFull();
    }
}
