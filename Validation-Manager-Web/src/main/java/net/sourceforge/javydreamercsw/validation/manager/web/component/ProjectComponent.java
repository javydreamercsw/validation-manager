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
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
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
import com.validation.manager.core.VMUI;
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
public final class ProjectComponent extends Panel {

    private final Project p;
    private final boolean edit;
    private final static Logger LOG
            = Logger.getLogger(ProjectComponent.class.getSimpleName());
    private TextField name;
    private TextArea notes;
    private ComboBox type;
    private final Button save = new Button(TRANSLATOR.translate("general.save"));
    private final Button update = new Button(TRANSLATOR.translate("general.update"));

    public ProjectComponent(Project p, boolean edit) {
        this.p = p;
        this.edit = edit;
        setCaption(TRANSLATOR.translate("project.detail"));
        init();
    }

    public ProjectComponent(Project p, String caption, boolean edit) {
        super(caption);
        this.p = p;
        this.edit = edit;
        init();
    }

    private void init() {
        addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        type = new ComboBox(TRANSLATOR.translate("general.type"));
        FormLayout layout = new FormLayout();
        setContent(layout);
        BeanFieldGroup binder = new BeanFieldGroup(getProject().getClass());
        binder.setItemDataSource(getProject());
        name = (TextField) binder.buildAndBind(TRANSLATOR.translate("general.name"),
                "name", TextField.class);
        name.setNullRepresentation("");
        notes = (TextArea) binder.buildAndBind(TRANSLATOR.translate("general.notes"),
                "notes", TextArea.class);
        getNotes().setNullRepresentation("");
        getNotes().setSizeFull();
        getName().setRequired(true);
        getName().setRequiredError(TRANSLATOR.translate("missing.name.message"));
        layout.addComponent(getName());
        layout.addComponent(getNotes());
        type.setNewItemsAllowed(false);
        type.setTextInputAllowed(false);
        type.addValidator(new NullValidator(TRANSLATOR
                .translate("message.required.field.missing")
                .replaceAll("%f",
                        TRANSLATOR.translate("general.type")),
                false));
        BeanItemContainer<ProjectType> container
                = new BeanItemContainer<>(ProjectType.class,
                        new ProjectTypeJpaController(DataBaseManager
                                .getEntityManagerFactory())
                                .findProjectTypeEntities());
        type.setContainerDataSource(container);
        type.getItemIds().forEach(id -> {
            ProjectType temp = ((ProjectType) id);
            type.setItemCaption(id,
                    TRANSLATOR.translate(temp.getTypeName()));
        });
        layout.addComponent(type);
        binder.bind(type, "projectTypeId");
        Button cancel = new Button(TRANSLATOR.translate("general.cancel"));
        cancel.addClickListener((Button.ClickEvent event) -> {
            binder.discard();
            if (getProject().getId() == null) {
                ((VMUI) UI.getCurrent()).displayObject(((VMUI) UI.getCurrent())
                        .getSelectdValue());
            } else {
                ((VMUI) UI.getCurrent()).displayObject(getProject(), false);
            }
        });
        if (edit) {
            if (getProject().getId() == null) {
                //Creating a new one
                getSave().addClickListener((Button.ClickEvent event) -> {
                    if (getName().getValue() == null) {
                        Notification.show(getName().getRequiredError(),
                                Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                    getProject().setName(getName().getValue());
                    if (getNotes().getValue() != null) {
                        getProject().setNotes(getNotes().getValue());
                    }
                    if (type.getValue() == null) {
                        Notification.show(type.getRequiredError(),
                                Notification.Type.ERROR_MESSAGE);
                        return;
                    }
                    getProject().setProjectTypeId((ProjectType) type.getValue());
                    new ProjectJpaController(DataBaseManager
                            .getEntityManagerFactory()).create(getProject());
                    //Recreate the tree to show the addition
                    ((VMUI) UI.getCurrent()).updateProjectList();
                    ((VMUI) UI.getCurrent()).buildProjectTree(getProject());
                    ((VMUI) UI.getCurrent()).displayObject(getProject(), false);
                    ((VMUI) UI.getCurrent()).updateScreen();
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(getSave());
                hl.addComponent(cancel);
                layout.addComponent(hl);
            } else {
                //Editing existing one
                getUpdate().addClickListener((Button.ClickEvent event) -> {
                    ((VMUI) UI.getCurrent()).handleVersioning(getProject(), null);
                    try {
                        getProject().setName(getName().getValue());
                        if (getNotes().getValue() != null) {
                            getProject().setNotes(getNotes().getValue());
                        }
                        if (type.getValue() == null) {
                            Notification.show(type.getRequiredError(),
                                    Notification.Type.ERROR_MESSAGE);
                            return;
                        }
                        getProject().setProjectTypeId((ProjectType) type.getValue());
                        new ProjectJpaController(DataBaseManager
                                .getEntityManagerFactory()).edit(getProject());
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
                    //Recreate the tree to show the addition
                    ((VMUI) UI.getCurrent()).updateProjectList();
                    ((VMUI) UI.getCurrent()).buildProjectTree(getProject());
                    ((VMUI) UI.getCurrent()).displayObject(getProject(), false);
                    ((VMUI) UI.getCurrent()).updateScreen();
                });
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(getUpdate());
                hl.addComponent(cancel);
                layout.addComponent(hl);
            }
        }
        binder.setBuffered(true);
        binder.setReadOnly(!edit);
        binder.bindMemberFields(this);
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
