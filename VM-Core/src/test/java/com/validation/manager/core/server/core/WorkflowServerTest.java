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
package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.WorkflowStep;
import com.validation.manager.core.db.controller.StepTransitionsToStepJpaController;
import com.validation.manager.core.db.controller.WorkflowJpaController;
import com.validation.manager.core.db.controller.WorkflowStepJpaController;
import com.validation.manager.test.AbstractVMTestCase;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class WorkflowServerTest extends AbstractVMTestCase {

    /**
     * Test of write2DB method, of class WorkflowServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWorkflow() throws Exception {
        System.out.println("Worflow");
        WorkflowJpaController c
                = new WorkflowJpaController(DataBaseManager
                        .getEntityManagerFactory());
        WorkflowStepJpaController sc
                = new WorkflowStepJpaController(DataBaseManager
                        .getEntityManagerFactory());
        StepTransitionsToStepJpaController tc
                = new StepTransitionsToStepJpaController(DataBaseManager
                        .getEntityManagerFactory());
        int workflows = c.getWorkflowCount();
        int steps = sc.getWorkflowStepCount();
        WorkflowServer instance = new WorkflowServer("Test");
        instance.write2DB();
        assertEquals(workflows + 1, c.getWorkflowCount());
        assertEquals(steps + 1, sc.getWorkflowStepCount());
        instance.addStep("Step #1");
        assertEquals(steps + 2, sc.getWorkflowStepCount());
        assertEquals(2, instance.getEntity().getWorkflowStepList().size());
        instance.addStep("Step #2");
        assertEquals(steps + 3, sc.getWorkflowStepCount());
        assertEquals(3, instance.getEntity().getWorkflowStepList().size());
        int transitions = tc.getStepTransitionsToStepCount();
        instance.addTransition(instance.getEntity().getWorkflowStepList().get(0),
                instance.getEntity().getWorkflowStepList().get(1), "Transition 1");
        assertEquals(transitions + 1, tc.getStepTransitionsToStepCount());
    }

    @Test
    public void testWorkflowInstance() throws Exception {
        System.out.println("Worflow Instance");
        createTestUsers();
        WorkflowServer ws = new WorkflowServer("Test");
        ws.write2DB();
        for (int i = 1; i <= 5; i++) {
            ws.addStep("Step #" + i);
            //Add a transition from previous step
            WorkflowStep source = ws.getEntity()
                    .getWorkflowStepList().get(i - 1);
            WorkflowStep target = ws.getEntity().getWorkflowStepList().get(i);
            ws.addTransition(source, target, "Transition " + i);
            System.out.println(source.getStepName()
                    + "----->" + target.getStepName());
        }
        //Create an instance
        WorkflowInstanceServer wis
                = new WorkflowInstanceServer(ws.createInstance()
                        .getWorkflowInstancePK());
        assertNotNull(wis);
        assertTrue(wis.getWorkflowInstancePK().getId() > 0);
        //Attempt bad transition
        try {
            wis.transition(ws.getWorkflowStepList().get(2), designer, "Test");
            fail("Invalid transition!");
        }
        catch (VMException ex) {
            //Expected failure
        }
        for (int i = 0; i < 5; i++) {
            //Attempt good transition
            try {
                assertEquals(i, wis.getWorkflowInstanceHasTransitionList().size());
                wis.transition(ws.getWorkflowStepList().get(i + 1), designer, "Test");
                assertEquals(i + 1, wis.getWorkflowInstanceHasTransitionList().size());
                assertNotNull(wis.getWorkflowStep());
            }
            catch (VMException ex) {
                //Expected failure
                Exceptions.printStackTrace(ex);
                fail();
            }
        }
        wis.getWorkflowInstanceHasTransitionList().forEach(t -> {
            System.out.println("Transitioned from: "
                    + t.getStepTransitionsToStep().getWorkflowStepSource()
                            .getStepName()
                    + " to "
                    + t.getStepTransitionsToStep().getWorkflowStepTarget()
                            .getStepName()
                    + " on " + t.getTransitionDate()
                    + " by: " + t.getTransitioner().getFirstName() + " "
                    + t.getTransitioner().getLastName()
                    + " Reason: " + t.getTransitionSource());
        });
    }
}
