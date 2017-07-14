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
package net.sourceforge.javydreamercsw.validation.manager.web.wizard.plan;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestCasePK;
import com.validation.manager.core.db.controller.TestCaseExecutionJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.ByteToStringConverter;
import org.vaadin.teemu.wizards.WizardStep;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DetailStep implements WizardStep {

    private List<TestCasePK> testCases;
    private final TestCaseExecution tce;
    private static final Logger LOG
            = Logger.getLogger(DetailStep.class.getSimpleName());

    public DetailStep() {
        this.tce = new TestCaseExecution();
    }

    @Override
    public String getCaption() {
        return "add.details";
    }

    @Override
    public Component getContent() {
        Panel form = new Panel("execution.detail");
        FormLayout layout = new FormLayout();
        form.setContent(layout);
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        BeanFieldGroup binder = new BeanFieldGroup(TestCaseExecution.class);
        binder.setItemDataSource(tce);
        TextArea name = new TextArea("general.name");
        name.setConverter(new ByteToStringConverter());
        binder.bind(name, "name");
        layout.addComponent(name);
        TextArea scope = new TextArea("general.scope");
        scope.setConverter(new ByteToStringConverter());
        binder.bind(scope, "scope");
        layout.addComponent(scope);
        if (tce.getId() != null) {
            TextArea conclusion = new TextArea("general.conclusion");
            conclusion.setConverter(new ByteToStringConverter());
            binder.bind(conclusion, "conclusion");
            layout.addComponent(conclusion);
            conclusion.setSizeFull();
            layout.addComponent(conclusion);
        }
        binder.setBuffered(false);
        binder.bindMemberFields(form);
        form.setSizeFull();
        return form;
    }

    @Override
    public boolean onAdvance() {
        try {
            //Create the record
            new TestCaseExecutionJpaController(DataBaseManager
                    .getEntityManagerFactory()).create(tce);
            TestCaseExecutionServer tces = new TestCaseExecutionServer(tce);
            //Now create the execution records
            TestCaseServer tc;
            for (TestCasePK id : testCases) {
                //Retrieve the TestCase to get the steps
                tc = new TestCaseServer(id);
                tces.addTestCase(tc.getEntity());
            }
            try {
                tces.write2DB();
                VMUI ui = ValidationManagerUI.getInstance();
                ui.buildProjectTree(ui.getSelectdValue());
                ui.updateProjectList();
                ui.updateScreen();
                ui.displayObject(tces.getEntity(), false);
                return true;
            } catch (NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean onBack() {
        return true;
    }

    /**
     * @return the testCases
     */
    public List<TestCasePK> getTestCases() {
        return testCases;
    }

    /**
     * @param testCases the testCases to set
     */
    public void setTestCases(List<TestCasePK> testCases) {
        this.testCases = testCases;
    }
}
