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

import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.TestCasePK;
import com.validation.manager.core.tool.Tool;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.TreeTableCheckBox;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class SelectTestCasesStep implements WizardStep {

    private final Project p;
    private final Wizard w;
    private final TreeTable testTree = new TreeTable("available.tests");
    private final List<Integer> projects = new ArrayList<>();
    private static final Logger LOG
            = Logger.getLogger(SelectTestCasesStep.class.getSimpleName());

    public SelectTestCasesStep(Wizard w, Project p) {
        this.p = p;
        this.w = w;
    }

    @Override
    public String getCaption() {
        return "select.test.case";
    }

    @Override
    public Component getContent() {
        VerticalLayout l = new VerticalLayout();
        //Add menu
        HorizontalLayout menu = new HorizontalLayout();
        if (p != null) {
            //Show the Test Plans for the selected project (including sub projects
            testTree.addContainerProperty("general.name",
                    TreeTableCheckBox.class, "");
            testTree.addContainerProperty("general.description",
                    String.class, "");
            testTree.setWidth("20em");
            addProjectTestPlanning(testTree, p);
        }
        testTree.setSizeFull();
        l.addComponent(menu);
        l.addComponent(testTree);
        return l;
    }

    @Override
    public boolean onAdvance() {
        //Get a list of selected test cases
        List<TestCasePK> testCases
                = processChildren("project" + p.getId());
        testCases.forEach((i) -> {
            LOG.log(Level.FINE, "Test Case: {0}", i);
        });
        if (testCases.isEmpty()) {
            Notification.show("unable.to.proceed",
                    "select.test.case.message",
                    Notification.Type.WARNING_MESSAGE);
            return false;
        }
        //update next step
        DetailStep next = ((DetailStep) w.getSteps().get(1));
        next.setTestCases(testCases);
        return true;
    }

    @Override
    public boolean onBack() {
        return false;
    }

    private void addProjectTestPlanning(TreeTable testTree, Project p) {
        //Add the test projects
        testTree.addItem(new Object[]{new TreeTableCheckBox(testTree,
            p.getName(), "project" + p.getId()), ""},
                "project" + p.getId());
        if (p.getParentProjectId() != null) {
            //Add as child
            testTree.setParent("project" + p.getId(),
                    "project" + p.getParentProjectId().getId());
        }
        p.getTestProjectList().stream().map((tp) -> {
            TreeTableCheckBox cb = new TreeTableCheckBox(testTree,
                    tp.getName(), "testproject" + tp.getId());
            cb.setIcon(ValidationManagerUI.TEST_SUITE_ICON);
            testTree.addItem(new Object[]{cb, ""},
                    "testproject" + tp.getId());
            return tp;
        }).map((tp) -> {
            testTree.setParent("testproject" + tp.getId(),
                    "project" + p.getId());
            return tp;
        }).map((tp) -> {
            tp.getTestPlanList().stream().map((plan) -> {
                TreeTableCheckBox pcb = new TreeTableCheckBox(testTree,
                        plan.getName(), plan.getTestPlanPK());
                pcb.setIcon(ValidationManagerUI.PLAN_ICON);
                testTree.addItem(new Object[]{pcb, ""},
                        plan.getTestPlanPK());
                return plan;
            }).map((plan) -> {
                testTree.setParent(plan.getTestPlanPK(),
                        "testproject" + tp.getId());
                return plan;
            }).forEachOrdered((plan) -> {
                plan.getTestCaseList().stream().map((tc) -> {
                    TreeTableCheckBox tccb = new TreeTableCheckBox(testTree,
                            tc.getName(), Tool.buildId(tc));
                    tccb.setIcon(ValidationManagerUI.TEST_ICON);
                    testTree.addItem(new Object[]{tccb,
                        tc.getSummary() != null
                        ? new String(tc.getSummary()) : ""},
                            Tool.buildId(tc));
                    return tc;
                }).map((tc) -> {
                    testTree.setParent(Tool.buildId(tc),
                            plan.getTestPlanPK());
                    return tc;
                }).forEachOrdered((tc) -> {
                    testTree.setChildrenAllowed(Tool.buildId(tc), false);
                });
            });
            return tp;
        }).forEachOrdered((tp) -> {
            testTree.setCollapsed("testproject" + tp.getId(), false);
        });
        p.getProjectList().forEach((sp) -> {
            addProjectTestPlanning(testTree, sp);
        });
        testTree.setCollapsed("project" + p.getId(), false);
    }

    private List<TestCasePK> processChildren(Object parent) {
        List<TestCasePK> testCases = new ArrayList<>();
        //Get a list of selected test cases
        testTree.getChildren(parent).stream().map((o) -> {
            if (o instanceof String) {
                String id = (String) o;
                if (id.startsWith("tc")) {
                    //Is a Test Case
                    Item item = testTree.getItem(id);
                    Object val = item.getItemProperty("general.name").getValue();
                    if (val instanceof TreeTableCheckBox) {
                        TreeTableCheckBox ttcb = (TreeTableCheckBox) val;
                        if (ttcb.getValue()) {
                            //Selected
                            LOG.log(Level.FINE, "Included TC: {0}",
                                    ttcb.getObjectId());
                            StringTokenizer st = new StringTokenizer(id, "-");
                            st.nextToken();//Ignore tc
                            testCases.add(new TestCasePK(Integer
                                    .parseInt(st.nextToken()),
                                    Integer.parseInt(st.nextToken())));
                            Object pid = id;
                            //Add the related project to the list.
                            while (pid != null) {
                                if (pid instanceof String) {
                                    String s = (String) pid;
                                    if (s.startsWith("project")) {
                                        LOG.log(Level.FINE, "Processing: {0}", s);
                                        getProjects().add(Integer.parseInt(s.substring(7)));
                                        break;
                                    }
                                }
                                pid = testTree.getParent(pid);
                            }
                        }
                    }
                }
            }
            return o;
        }).filter((o) -> (testTree.hasChildren(o))).forEachOrdered((o) -> {
            testCases.addAll(processChildren(o));
        });
        return testCases;
    }

    /**
     * @return the projects
     */
    public List<Integer> getProjects() {
        return projects;
    }
}
