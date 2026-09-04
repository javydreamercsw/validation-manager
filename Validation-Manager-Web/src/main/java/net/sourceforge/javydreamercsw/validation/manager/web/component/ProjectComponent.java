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
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.ProjectType;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.ProjectTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class ProjectComponent extends VerticalLayout {

    private final Project p;
    private final boolean edit;
    private final static Logger LOG
            = Logger.getLogger(ProjectComponent.class.getSimpleName());
    private TextField name;
    private TextArea notes;
    private ComboBox<ProjectType> type;
    private final Button save = new Button(TRANSLATOR.translate("general.save"));
    private final Button update = new Button(TRANSLATOR.translate("general.update"));

    public ProjectComponent(Project p, boolean edit) {
        this.p = p;
        this.edit = edit;
        add(new com.vaadin.flow.component.html.Span(
                TRANSLATOR.translate("project.detail")));
        init();
    }

    public ProjectComponent(Project p, String caption, boolean edit) {
        this.p = p;
        this.edit = edit;
        add(new com.vaadin.flow.component.html.Span(caption));
        init();
    }

    private void init() {
        type = new ComboBox<>(TRANSLATOR.translate("general.type"));
        FormLayout layout = new FormLayout();
        add(layout);
        Binder<Project> binder = new Binder<>(Project.class);
        binder.setBean(getProject());
        name = new TextField(TRANSLATOR.translate("general.name"));
        binder.forField(name).withNullRepresentation("").bind("name");
        notes = new TextArea(TRANSLATOR.translate("general.notes"));
        binder.forField(notes).withNullRepresentation("").bind("notes");
        getNotes().setSizeFull();
        getName().setRequiredIndicatorVisible(true);
        layout.add(getName());
        layout.add(getNotes());
        type.setAllowCustomValue(false);
        type.setRequiredIndicatorVisible(true);
        type.setItems(new ProjectTypeJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findProjectTypeEntities());
        type.setItemLabelGenerator(temp
                -> TRANSLATOR.translate(temp.getTypeName()));
        layout.add(type);
        binder.bind(type, "projectTypeId");
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((event) -> {
            if (getProject().getId() == null) {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(((ValidationManagerUI) UI.getCurrent())
                        .getSelectdValue());
            } else {
                ((ValidationManagerUI) UI.getCurrent()).displayObject(getProject(), false);
            }
        });
        if (edit) {
            if (getProject().getId() == null) {
                //Creating a new one
                getSave().addClickListener((event) -> {
                    if (getName().getValue() == null
                            || getName().getValue().trim().isEmpty()) {
                        Notification.show(TRANSLATOR.translate("missing.name.message"));
                        return;
                    }
                    getProject().setName(getName().getValue());
                    if (getNotes().getValue() != null) {
                        getProject().setNotes(getNotes().getValue());
                    }
                    if (type.getValue() == null) {
                        Notification.show(TRANSLATOR
                                .translate("message.required.field.missing")
                                .replaceAll("%f", TRANSLATOR.translate("general.type")));
                        return;
                    }
                    getProject().setProjectTypeId((ProjectType) type.getValue());
                    new ProjectJpaController(DataBaseManager
                            .getEntityManagerFactory()).create(getProject());
                    //Recreate the tree to show the addition
                    ((ValidationManagerUI) UI.getCurrent()).updateProjectList();
                    ((ValidationManagerUI) UI.getCurrent()).buildProjectTree(getProject());
                    ((ValidationManagerUI) UI.getCurrent()).displayObject(getProject(), false);
                    ((ValidationManagerUI) UI.getCurrent()).updateScreen();
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(getSave());
                hl.add(cancel);
                layout.add(hl);
            } else {
                //Editing existing one
                getUpdate().addClickListener((event) -> {
                    ((ValidationManagerUI) UI.getCurrent()).handleVersioning(getProject(), null);
                    try {
                        getProject().setName(getName().getValue());
                        if (getNotes().getValue() != null) {
                            getProject().setNotes(getNotes().getValue());
                        }
                        if (type.getValue() == null) {
                            Notification.show(TRANSLATOR
                                    .translate("message.required.field.missing")
                                    .replaceAll("%f", TRANSLATOR.translate("general.type")));
                            return;
                        }
                        getProject().setProjectTypeId((ProjectType) type.getValue());
                        new ProjectJpaController(DataBaseManager
                                .getEntityManagerFactory()).edit(getProject());
                    } catch (NonexistentEntityException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.update"));
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                        Notification.show(TRANSLATOR.translate("general.error.record.update"));
                    }
                    //Recreate the tree to show the addition
                    ((ValidationManagerUI) UI.getCurrent()).updateProjectList();
                    ((ValidationManagerUI) UI.getCurrent()).buildProjectTree(getProject());
                    ((ValidationManagerUI) UI.getCurrent()).displayObject(getProject(), false);
                    ((ValidationManagerUI) UI.getCurrent()).updateScreen();
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.add(getUpdate());
                hl.add(cancel);
                layout.add(hl);
            }
        }
        binder.setReadOnly(!edit);
        setSizeFull();
    }

    /**
     * @return the p
     */
    public Project getProject() {
        return p;
    }

    /**
     * @return the name
     */
    public TextField getName() {
        return name;
    }

    /**
     * @return the notes
     */
    public TextArea getNotes() {
        return notes;
    }

    /**
     * @return the save
     */
    public Button getSave() {
        return save;
    }

    /**
     * @return the update
     */
    public Button getUpdate() {
        return update;
    }
}
