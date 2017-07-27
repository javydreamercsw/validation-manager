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
import com.validation.manager.core.server.core.ProjectTypeServer;
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
        ProjectServer ps = new ProjectServer("", "",
                new ProjectTypeServer(1).getEntity());
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
        assertEquals(2, w.getSteps().size());
        ProjectTypeStep step1 = (ProjectTypeStep) w.getSteps().get(0);
        assertFalse(step1.onAdvance());
        //Select HW
        step1.getType().setValue("general.hardware");
        assertTrue(step1.onAdvance());
        w.next();
        ProjectTemplateStep step2 = (ProjectTemplateStep) w.getSteps().get(1);
        //Try to advance without selecting a template
        assertFalse(step2.onAdvance());
        //Select GAMP template
        step2.getTemplates().setValue(new TemplateJpaController(DataBaseManager
                .getEntityManagerFactory()).findTemplate(1));
        assertEquals(4, w.getSteps().size());//Added GAMP and detail steps
        assertNull(instance.getTemplate());
        assertTrue(step2.onAdvance());
        assertNotNull(instance.getTemplate());
        w.next();
        //Step 3
        GAMPStep step3 = (GAMPStep) w.getSteps().get(2);
        assertFalse(step3.onAdvance());
        assertEquals(2, step3.getCategory().getItemIds().size());
        //Select category 1
        step3.getCategory().setValue("template.gamp5.hw.cat1");
        assertEquals(true, step3.isIq());
        assertEquals(true, step3.isVersion());
        assertEquals(false, step3.isCs());
        assertEquals(false, step3.isDs());
        assertEquals(false, step3.isMs());
        assertEquals(false, step3.isFs());
        assertEquals(false, step3.isSupplierAssessment());
        assertEquals(false, step3.isUrs());
        assertNull(instance.getProcess());
        //Select category 2
        step3.getCategory().setValue("template.gamp5.hw.cat2");
        assertEquals(true, step3.isIq());
        assertEquals(true, step3.isVersion());
        assertEquals(false, step3.isCs());
        assertEquals(true, step3.isDs());
        assertEquals(false, step3.isMs());
        assertEquals(false, step3.isFs());
        assertEquals(true, step3.isSupplierAssessment());
        assertEquals(false, step3.isUrs());
        assertNull(instance.getProcess());
        //Select SW
        step1.getType().setValue("general.software");
        step1.onAdvance();//Set the new type
        step3.getContent();//Refresh the categories
        assertEquals(4, step3.getCategory().getItemIds().size());
        assertNull(instance.getProcess());
        //Select category 1
        step3.getCategory().setValue("template.gamp5.sw.cat1");
        assertEquals(true, step3.isIq());
        assertEquals(true, step3.isVersion());
        assertEquals(false, step3.isCs());
        assertEquals(false, step3.isDs());
        assertEquals(false, step3.isMs());
        assertEquals(false, step3.isFs());
        assertEquals(false, step3.isSupplierAssessment());
        assertEquals(false, step3.isUrs());
        assertNull(instance.getProcess());
        //Select category 3
        step3.getCategory().setValue("template.gamp5.sw.cat3");
        assertEquals(true, step3.isIq());
        assertEquals(true, step3.isVersion());
        assertEquals(false, step3.isCs());
        assertEquals(false, step3.isDs());
        assertEquals(false, step3.isMs());
        assertEquals(false, step3.isFs());
        assertEquals(true, step3.isSupplierAssessment());
        assertEquals(true, step3.isUrs());
        assertNull(instance.getProcess());
        //Select category 4
        step3.getCategory().setValue("template.gamp5.sw.cat4");
        assertEquals(true, step3.isIq());
        assertEquals(true, step3.isVersion());
        assertEquals(true, step3.isCs());
        assertEquals(false, step3.isDs());
        assertEquals(false, step3.isMs());
        assertEquals(true, step3.isFs());
        assertEquals(true, step3.isSupplierAssessment());
        assertEquals(true, step3.isUrs());
        assertNull(instance.getProcess());
        //Select category 5
        step3.getCategory().setValue("template.gamp5.sw.cat5");
        assertEquals(true, step3.isIq());
        assertEquals(true, step3.isVersion());
        assertEquals(false, step3.isCs());
        assertEquals(true, step3.isDs());
        assertEquals(true, step3.isMs());
        assertEquals(true, step3.isFs());
        assertEquals(true, step3.isSupplierAssessment());
        assertEquals(true, step3.isUrs());
        assertNull(instance.getProcess());
        assertTrue(step3.onAdvance());
        assertNotNull(instance.getProcess());
        w.next();
        //Step 3
        ProjectDetailsStep step4 = (ProjectDetailsStep) w.getSteps().get(3);
        assertFalse(step4.onAdvance());
        //Set the project name
        step4.getName().setValue("Test");
        step4.getNotes().setValue("Test Notes");
        //Finish the wizard
        assertTrue(step4.onAdvance());
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
