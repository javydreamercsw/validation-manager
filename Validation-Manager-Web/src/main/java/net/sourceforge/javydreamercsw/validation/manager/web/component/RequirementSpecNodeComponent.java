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
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementSpecNodeComponent extends Panel {

    private final RequirementSpecNode rsn;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(RequirementSpecComponent.class.getSimpleName());

    public RequirementSpecNodeComponent(RequirementSpecNode rsn, boolean edit) {
        this.rsn = rsn;
        this.edit = edit;
        init();
    }

    public RequirementSpecNodeComponent(RequirementSpecNode rsn, boolean edit,
            String caption) {
        super(caption);
        this.rsn = rsn;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        setContent(layout);
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(rsn.getClass());
        binder.setItemDataSource(rsn);
        Field<?> name = binder.buildAndBind(TRANSLATOR.translate("general.name"),
                "name");
        layout.addComponent(name);
        Field desc = binder.buildAndBind(TRANSLATOR.translate("general.description"),
                "description",
                TextArea.class);
        desc.setSizeFull();
        layout.addComponent(desc);
        Field<?> scope = binder.buildAndBind(TRANSLATOR.translate("general.scope"),
                "scope");
        layout.addComponent(scope);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (rsn.getRequirementSpecNodePK() == null) {
                ((VMUI) UI.getCurrent()).displayObject(rsn.getRequirementSpec());
            } else {
                ((VMUI) UI.getCurrent()).displayObject(rsn, false);
            }
        });
        if (edit) {
            if (rsn.getRequirementSpecNodePK() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((Button.ClickEvent event) -> {
                    try {
                        rsn.setName(name.getValue().toString());
                        rsn.setDescription(desc.getValue().toString());
                        rsn.setScope(scope.getValue().toString());
                        rsn.setRequirementSpec((RequirementSpec) ((VMUI) UI.getCurrent())
                                .getSelectdValue());
                        new RequirementSpecNodeJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(rsn);
                        setVisible(false);
                        //Recreate the tree to show the addition
                        ((VMUI) UI.getCurrent()).updateProjectList();
                        ((VMUI) UI.getCurrent()).buildProjectTree(rsn);
                        ((VMUI) UI.getCurrent()).displayObject(rsn, true);
                        ((VMUI) UI.getCurrent()).updateScreen();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"),
                                ex.getLocalizedMessage(),
                                Notification.Type.ERROR_MESSAGE);
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(save);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((Button.ClickEvent event) -> {
                    rsn.setName(name.getValue().toString());
                    rsn.setDescription(desc.getValue().toString());
                    rsn.setScope(scope.getValue().toString());
                    ((VMUI) UI.getCurrent()).handleVersioning(rsn, () -> {
                        try {
                            new RequirementSpecNodeJpaController(DataBaseManager
                                    .getEntityManagerFactory()).edit(rsn);
                            ((VMUI) UI.getCurrent()).displayObject(rsn, true);
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
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(update);
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setReadOnly(!edit);
        binder.bindMemberFields(this);
        layout.setSizeFull();
        setSizeFull();
    }
}
