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
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import static net.sourceforge.javydreamercsw.validation.manager.web.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TestProjectComponent extends VerticalLayout {

    private final TestProject tp;
    private final boolean edit;
    private static final Logger LOG
            = Logger.getLogger(TestPlanComponent.class.getSimpleName());

    public TestProjectComponent(TestProject tp, boolean edit) {
        this.tp = tp;
        this.edit = edit;
        add(new com.vaadin.flow.component.html.Span(TRANSLATOR.translate("test.project.detail")));
        init();
    }

    public TestProjectComponent(TestProject tp, boolean edit, String caption) {
        add(new com.vaadin.flow.component.html.Span(caption));
        this.tp = tp;
        this.edit = edit;
        init();
    }

    private void init() {
        FormLayout layout = new FormLayout();
        add(layout);
        Binder<TestProject> binder = new Binder<>(TestProject.class);
        binder.setBean(tp);
        TextField name = new TextField(TRANSLATOR.translate("general.name"));
        binder.bind(name, "name");
        layout.add(name);
        TextArea notes = new TextArea(TRANSLATOR.translate("general.notes"));
        binder.bind(notes, "notes");
        notes.setSizeFull();
        layout.add(notes);
        Checkbox active = new Checkbox(TRANSLATOR.translate("general.active"));
        binder.bind(active, "active");
        layout.add(active);
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((event) -> {
            if (tp.getId() == null) {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(((ValidationManagerUI) UI.getCurrent())
                        .getSelectdValue());
            } else {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(tp, false);
            }
        });
        if (edit) {
            if (tp.getId() == null) {
                //Creating a new one
                Button save = new Button(TRANSLATOR.translate("general.save"));
                save.addClickListener((event) -> {
                    try {
                        tp.setName(name.getValue().toString());
                        tp.setNotes(notes.getValue().toString());
                        tp.setActive(active.getValue());
                        new TestProjectJpaController(DataBaseManager
                                .getEntityManagerFactory()).create(tp);
                        setVisible(false);
                        //Recreate the tree to show the addition
                        ((ValidationManagerUI) UI.getCurrent()).updateProjectList();
                        ((ValidationManagerUI) UI.getCurrent()).buildProjectTree(tp);
                        ((ValidationManagerUI) UI.getCurrent()).displayObject(tp, false);
                        ((ValidationManagerUI) UI.getCurrent()).updateScreen();
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation") + ": " + ex.getLocalizedMessage());
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
                    try {
                        tp.setName(name.getValue().toString());
                        tp.setNotes(notes.getValue().toString());
                        tp.setActive(active.getValue());
                        ((ValidationManagerUI) UI.getCurrent()).handleVersioning(tp, () -> {
                            try {
                                new TestProjectJpaController(DataBaseManager
                                        .getEntityManagerFactory()).edit(tp);
                                ((ValidationManagerUI) UI.getCurrent()).displayObject(tp, true);
                            } catch (NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update") + ": " + ex.getLocalizedMessage());
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, null, ex);
                                Notification.show(TRANSLATOR.translate("general.error.record.update") + ": " + ex.getLocalizedMessage());
                            }
                        });
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.creation") + ": " + ex.getLocalizedMessage());
                    }
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
