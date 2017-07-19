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

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.TemplateJpaController;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.test.AbstractVMTestCase;
import org.junit.Test;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ProjectCreationWizardTest extends AbstractVMTestCase {

    /**
     * Test of setProject method, of class ProjectCreationWizard.
     *
     * @throws com.validation.manager.core.VMException
     */
    @Test
    public void testGAMPWizard() throws VMException {
        System.out.println("GAMP Wizard");
        assertEquals(0, ProjectServer.getProjects().size());
        ProjectServer ps = new ProjectServer("", "");
        ProjectCreationWizard instance = new ProjectCreationWizard(ps);
        Wizard w = instance.getWizard();
        w.addListener(new WizardProgressListener() {
            @Override
            public void activeStepChanged(WizardStepActivationEvent event) {
                System.out.println("Activated step: "
                        + event.getActivatedStep().getCaption());
            }

            @Override
            public void stepSetChanged(WizardStepSetChangedEvent event) {
                System.out.println("Step set changed!");
            }

            @Override
            public void wizardCompleted(WizardCompletedEvent event) {
                System.out.println("Wizard completed");
            }

            @Override
            public void wizardCancelled(WizardCancelledEvent event) {
                System.out.println("Wizard cancelled");
            }
        });
        assertEquals(1, w.getSteps().size());
        ProjectTemplateStep step1 = (ProjectTemplateStep) w.getSteps().get(0);
        //Try to advance without selecting a template
        assertFalse(step1.onAdvance());
        //Select GAMP template
        step1.getTemplates().setValue(new TemplateJpaController(DataBaseManager
                .getEntityManagerFactory()).findTemplate(1));
        assertEquals(3, w.getSteps().size());//Added GAMP and detail steps
        assertNull(instance.getTemplate());
        assertTrue(step1.onAdvance());
        assertNotNull(instance.getTemplate());
        w.next();
        //Step 2
        GAMPStep step2 = (GAMPStep) w.getSteps().get(1);
        assertFalse(step2.onAdvance());
        assertEquals(0, step2.getCategory().getItemIds().size());
        //Select HW
        step2.getType().setValue("general.hardware");
        assertEquals(2, step2.getCategory().getItemIds().size());
        //Select category 1
        step2.getCategory().setValue("template.gamp5.hw.cat1");
        assertEquals(true, step2.isIq());
        assertEquals(true, step2.isVersion());
        assertEquals(false, step2.isCs());
        assertEquals(false, step2.isDs());
        assertEquals(false, step2.isMs());
        assertEquals(false, step2.isFs());
        assertEquals(false, step2.isSupplierAssessment());
        assertEquals(false, step2.isUrs());
        assertNull(instance.getProcess());
        //Select category 2
        step2.getCategory().setValue("template.gamp5.hw.cat2");
        assertEquals(true, step2.isIq());
        assertEquals(true, step2.isVersion());
        assertEquals(false, step2.isCs());
        assertEquals(true, step2.isDs());
        assertEquals(false, step2.isMs());
        assertEquals(false, step2.isFs());
        assertEquals(true, step2.isSupplierAssessment());
        assertEquals(false, step2.isUrs());
        assertNull(instance.getProcess());
        //Select SW
        step2.getType().setValue("general.software");
        assertEquals(4, step2.getCategory().getItemIds().size());
        assertNull(instance.getProcess());
        //Select category 1
        step2.getCategory().setValue("template.gamp5.sw.cat1");
        assertEquals(true, step2.isIq());
        assertEquals(true, step2.isVersion());
        assertEquals(false, step2.isCs());
        assertEquals(false, step2.isDs());
        assertEquals(false, step2.isMs());
        assertEquals(false, step2.isFs());
        assertEquals(false, step2.isSupplierAssessment());
        assertEquals(false, step2.isUrs());
        assertNull(instance.getProcess());
        //Select category 3
        step2.getCategory().setValue("template.gamp5.sw.cat3");
        assertEquals(true, step2.isIq());
        assertEquals(true, step2.isVersion());
        assertEquals(false, step2.isCs());
        assertEquals(false, step2.isDs());
        assertEquals(false, step2.isMs());
        assertEquals(false, step2.isFs());
        assertEquals(true, step2.isSupplierAssessment());
        assertEquals(true, step2.isUrs());
        assertNull(instance.getProcess());
        //Select category 4
        step2.getCategory().setValue("template.gamp5.sw.cat4");
        assertEquals(true, step2.isIq());
        assertEquals(true, step2.isVersion());
        assertEquals(true, step2.isCs());
        assertEquals(false, step2.isDs());
        assertEquals(false, step2.isMs());
        assertEquals(true, step2.isFs());
        assertEquals(true, step2.isSupplierAssessment());
        assertEquals(true, step2.isUrs());
        assertNull(instance.getProcess());
        //Select category 5
        step2.getCategory().setValue("template.gamp5.sw.cat5");
        assertEquals(true, step2.isIq());
        assertEquals(true, step2.isVersion());
        assertEquals(false, step2.isCs());
        assertEquals(true, step2.isDs());
        assertEquals(true, step2.isMs());
        assertEquals(true, step2.isFs());
        assertEquals(true, step2.isSupplierAssessment());
        assertEquals(true, step2.isUrs());
        assertNull(instance.getProcess());
        assertTrue(step2.onAdvance());
        assertNotNull(instance.getProcess());
        w.next();
        //Step 3
        ProjectDetailsStep step3 = (ProjectDetailsStep) w.getSteps().get(2);
        assertFalse(step3.onAdvance());
        //Set the project name
        step3.getName().setValue("Test");
        step3.getNotes().setValue("Test Notes");
        //Finish the wizard
        assertTrue(step3.onAdvance());
        assertEquals("Test", instance.getProject().getName());
        assertEquals("Test Notes", instance.getProject().getNotes());
        w.next();
        //Now check the result
        assertEquals(1, ProjectServer.getProjects().size());
        Project p = ProjectServer.getProjects().get(0);
        assertEquals(2, p.getTestProjectList().size());//Verification and Validation
        //Check verification contents
        TestProject verification = p.getTestProjectList().get(0);
        assertEquals(1, verification.getTestPlanList().size());
        TestPlan plan = verification.getTestPlanList().get(0);
        assertNotNull(plan.getName());
        assertEquals(6 /*Supplier Assessment not included*/,
                plan.getTestCaseList().size());
    }
}
