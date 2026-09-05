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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
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
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.controller.BaselineJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.BaselineServer;
import com.validation.manager.core.tool.Tool;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class BaselineComponent extends VerticalLayout {

    private static final Logger LOG
            = Logger.getLogger(BaselineComponent.class.getSimpleName());

    public BaselineComponent(Baseline baseline,
            boolean edit, RequirementSpec rs) {
        Span caption = new Span(TRANSLATOR.translate("baseline.detail"));
        add(caption);
        FormLayout layout = new FormLayout();
        add(layout);
        Binder<Baseline> binder = new Binder<>(Baseline.class);
        binder.setBean(baseline);
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        binder.bind(name, "baselineName");
        layout.add(name);
        TextArea desc = new TextArea(TRANSLATOR.translate("general.description"));
        binder.bind(desc, "description");
        desc.setSizeFull();
        layout.add(desc);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        if (rs != null) {
            List<History> potential = new ArrayList<>();
            Tool.extractRequirements(rs).forEach((r) -> {
                potential.add(r.getHistoryList().get(r.getHistoryList().size() - 1));
            });
            layout.add(((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                    .createRequirementHistoryTable(TRANSLATOR.translate("included.requirements"),
                            potential, true));
        } else {
            layout.add(((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                    .createRequirementHistoryTable(TRANSLATOR.translate("included.requirements"),
                            baseline.getHistoryList(), true));
        }
        cancel.addClickListener((event) -> {
            ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                    .displayObject(((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                            .getTree().asSingleSelect().getValue());
        });
        if (edit) {
            if (baseline.getId() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((event) -> {
                    if (rs != null) {
                        ConfirmDialog prompt = new ConfirmDialog();
                        prompt.setHeader(TRANSLATOR.translate("save.baseline.title"));
                        prompt.setText(TRANSLATOR.translate("save.baseine.message")
                                + "requirements will be released to a new major version");
                        prompt.setConfirmButton(TRANSLATOR.translate("general.yes"),
                                (e) -> {
                                    Baseline entity = BaselineServer
                                            .createBaseline(
                                                    baseline.getBaselineName(),
                                                    baseline.getDescription(),
                                                    rs)
                                            .getEntity();
                                    ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                                            .updateProjectList();
                                    ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                                            .buildProjectTree(entity);
                                    ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                                            .displayObject(entity, false);
                                    ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                                            .updateScreen();
                                });
                        prompt.setRejectable(true);
                        prompt.setRejectButton(TRANSLATOR.translate("general.no"),
                                (e) -> {
                                    ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                                            .displayObject(((ValidationManagerUI) com.vaadin
                                                    .flow.component.UI.getCurrent())
                                                    .getTree().asSingleSelect().getValue());
                                });
                        prompt.setCancelable(false);
                        prompt.open();
                    } else {
                        //Recreate the tree to show the addition
                        ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                                .displayObject(baseline, true);
                    }
                    ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                            .updateProjectList();
                    ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                            .updateScreen();
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(save, cancel);
                layout.add(hl);
            } else {
                //Editing existing one
                Button update = new Button(TRANSLATOR.translate("general.update"));
                update.addClickListener((event) -> {
                    try {
                        ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                                .handleVersioning(baseline, () -> {
                                    try {
                                        new BaselineJpaController(DataBaseManager
                                                .getEntityManagerFactory()).edit(baseline);
                                        //Recreate the tree to show the addition
                                        ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                                                .buildProjectTree(baseline);
                                        ((ValidationManagerUI) com.vaadin.flow.component.UI.getCurrent())
                                                .displayBaseline(baseline, false);
                                    } catch (NonexistentEntityException ex) {
                                        LOG.log(Level.SEVERE, null, ex);
                                        Notification.show(TRANSLATOR
                                                .translate("general.error.record.update"));
                                    } catch (Exception ex) {
                                        LOG.log(Level.SEVERE, null, ex);
                                        Notification.show(TRANSLATOR
                                                .translate("general.error.record.update"));
                                    }
                                });
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.update"));
                    }
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(update, cancel);
                layout.add(hl);
            }
        }
        binder.setReadOnly(!edit);
        setSizeFull();
    }

}
