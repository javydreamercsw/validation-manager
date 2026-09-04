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
package net.sourceforge.javydreamercsw.validation.manager.web.wizard.project;

import com.vaadin.data.Binder;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.server.core.ProjectServer;
import org.vaadin.teemu.wizards.WizardStep;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ProjectDetailsStep implements WizardStep {

    private final ProjectServer ps;
    private final ProjectCreationWizard w;
    private final TextField name;
    private final TextArea notes;

    public ProjectDetailsStep(ProjectCreationWizard wizard) {
        this.w = wizard;
        ps = new ProjectServer(new Project());
        Binder<ProjectServer> binder = new Binder<>(ProjectServer.class);
        binder.setBean(ps);
        name = new TextField(TRANSLATOR.translate("general.name"));
        binder.forField(name).withNullRepresentation("").bind("name");
        name.setRequiredIndicatorVisible(true);
        notes = new TextArea(TRANSLATOR.translate("general.notes"));
        binder.forField(notes).withNullRepresentation("").bind("notes");
        notes.setSizeFull();
    }

    @Override
    public String getCaption() {
        return TRANSLATOR.translate("project.detail");
    }

    @Override
    public Component getContent() {
        FormLayout layout = new FormLayout();
        layout.addComponent(getName());
        layout.addComponent(getNotes());
        return layout;
    }

    @Override
    public boolean onAdvance() {
        ps.setName(getName().getValue());
        if (getNotes().getValue() != null) {
            ps.setNotes(getNotes().getValue());
        }
        w.setProject(ps);
        return getName().getValue() != null
                && !"".equals(name.getValue());
    }

    @Override
    public boolean onBack() {
        return false;
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
}
