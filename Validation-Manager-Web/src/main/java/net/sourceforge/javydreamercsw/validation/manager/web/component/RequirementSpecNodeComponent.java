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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
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
public final class RequirementSpecNodeComponent extends VerticalLayout {

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
        this.rsn = rsn;
        this.edit = edit;
        add(new Span(caption));
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        add(layout);
        Binder<RequirementSpecNode> binder = new Binder<>(RequirementSpecNode.class);
        binder.setBean(rsn);
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        binder.bind(name, "name");
        layout.add(name);
        TextArea desc = new TextArea(TRANSLATOR.translate("general.description"));
        binder.bind(desc, "description");
        desc.setSizeFull();
        layout.add(desc);
        TextField scope = new TextField(TRANSLATOR.translate("general.scope"));
        binder.bind(scope, "scope");
        layout.add(scope);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((event) -> {
            if (rsn.getRequirementSpecNodePK() == null) {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(rsn.getRequirementSpec());
            } else {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(rsn, false);
            }
        });
        if (edit) {
            if (rsn.getRequirementSpecNodePK() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((event) -> {
                    try {
                        rsn.setName(name.getValue().toString());
                        rsn.setDescription(desc.getValue().toString());
                        rsn.setScope(scope.getValue().toString());
                        rsn.setRequirementSpec((RequirementSpec) ((ValidationManagerUI) UI.getCurrent())
                                .getSelectdValue());
                        new RequirementSpecNodeJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(rsn);
                        setVisible(false);
                        //Recreate the tree to show the addition
                        ((ValidationManagerUI) UI.getCurrent()).updateProjectList();
                        ((ValidationManagerUI) UI.getCurrent()).buildProjectTree(rsn);
                        ((ValidationManagerUI) UI.getCurrent()).displayObject(rsn, true);
                        ((ValidationManagerUI) UI.getCurrent()).updateScreen();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation"));
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(save);
                hl.add(cancel);
                layout.add(hl);
            } else {
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((event) -> {
                    rsn.setName(name.getValue().toString());
                    rsn.setDescription(desc.getValue().toString());
                    rsn.setScope(scope.getValue().toString());
                    ((ValidationManagerUI) UI.getCurrent()).handleVersioning(rsn, () -> {
                        try {
                            new RequirementSpecNodeJpaController(DataBaseManager
                                    .getEntityManagerFactory()).edit(rsn);
                            ((ValidationManagerUI) UI.getCurrent()).displayObject(rsn, true);
                        } catch (NonexistentEntityException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                            Notification.show(TRANSLATOR.translate("general.error.record.update")
                                    + ": " + ex.getLocalizedMessage());
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                            Notification.show(TRANSLATOR.translate("general.error.record.update")
                                    + ": " + ex.getLocalizedMessage());
                        }
                    });
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(update);
                hl.add(cancel);
                layout.add(hl);
            }
        }
        binder.setReadOnly(!edit);
        layout.setSizeFull();
        setSizeFull();
    }
}
