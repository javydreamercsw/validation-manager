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

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.VerticalLayout;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCasePK;
import com.validation.manager.core.tool.Tool;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final TreeData<Object> treeData = new TreeData<>();
    private final Map<Object, TreeTableCheckBox> checkboxes = new HashMap<>();
    private final TreeGrid<Object> testTree = new TreeGrid<>("available.tests");
    private final List<Integer> projects = new ArrayList<>();
    private static final Logger LOG
            = Logger.getLogger(SelectTestCasesStep.class.getSimpleName());

    public SelectTestCasesStep(Wizard w, Project p) {
        this.p = p;
        this.w = w;
        testTree.addColumn(o -> checkboxes.get(o)).setId("general.name")
                .setCaption("general.name")
                .setRenderer(new ComponentRenderer());
        testTree.addColumn(SelectTestCasesStep::getDescription)
                .setId("general.description")
                .setCaption("general.description");
        testTree.setWidth(20, Unit.EM);
        testTree.setHierarchyColumn("general.name");
    }

    private static String getDescription(Object id) {
        if (id instanceof TestCase) {
            TestCase tc = (TestCase) id;
            return tc.getSummary() != null
                    ? new String(tc.getSummary()) : "";
        }
        return "";
    }

    @Override
    public String getCaption() {
        return "select.test.case";
    }

    @Override
    public Component getContent() {
        VerticalLayout l = new VerticalLayout();
        if (p != null) {
            //Show the Test Plans for the selected project (including sub projects
            addProjectTestPlanning(treeData, p);
            testTree.setDataProvider(new TreeDataProvider<>(treeData));
        }
        testTree.setSizeFull();
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

    private void addProjectTestPlanning(TreeData<Object> treeData, Project p) {
        //Add the test projects
        String projectId = "project" + p.getId();
        if (!treeData.contains(projectId)) {
            treeData.addRootItems(projectId);
            checkboxes.put(projectId, new TreeTableCheckBox(new TreeNavigatorImpl(),
                    p.getName(), projectId));
        }
        if (p.getParentProjectId() != null) {
            //Add as child
            String parentId = "project"
                    + p.getParentProjectId().getId();
            if (!treeData.contains(parentId)) {
                treeData.addRootItems(parentId);
            }
            treeData.setParent(projectId, parentId);
        }
        p.getTestProjectList().forEach((tp) -> {
            String testProjectId = "testproject" + tp.getId();
            if (!treeData.contains(testProjectId)) {
                TreeTableCheckBox cb = new TreeTableCheckBox(new TreeNavigatorImpl(),
                        tp.getName(), testProjectId);
                cb.setIcon(ValidationManagerUI.TEST_SUITE_ICON);
                checkboxes.put(testProjectId, cb);
                treeData.addItem(projectId, testProjectId);
            }
            tp.getTestPlanList().forEach((plan) -> {
                Object planId = plan.getTestPlanPK();
                if (!treeData.contains(planId)) {
                    TreeTableCheckBox pcb = new TreeTableCheckBox(new TreeNavigatorImpl(),
                            plan.getName(), planId);
                    pcb.setIcon(ValidationManagerUI.PLAN_ICON);
                    checkboxes.put(planId, pcb);
                    treeData.addItem(testProjectId, planId);
                }
                plan.getTestCaseList().forEach((tc) -> {
                    Object tcId = tcId(tc);
                    if (!treeData.contains(tcId)) {
                        TreeTableCheckBox tccb = new TreeTableCheckBox(new TreeNavigatorImpl(),
                                tc.getName(), tcId);
                        tccb.setIcon(ValidationManagerUI.TEST_ICON);
                        checkboxes.put(tcId, tccb);
                        treeData.addItem(planId, tcId);
                    }
                });
            });
        });
        p.getProjectList().forEach((sp) -> {
            addProjectTestPlanning(treeData, sp);
        });
    }

    /**
     * The tree item id for a test case. Kept in sync with the id encoded in
     * {@link Tool#buildId(com.validation.manager.core.db.TestCase)} so
     * {@link #processChildren(java.lang.Object)} can decode it.
     */
    private static Object tcId(TestCase tc) {
        return Tool.buildId(tc);
    }

    private List<TestCasePK> processChildren(Object parent) {
        List<TestCasePK> testCases = new ArrayList<>();
        //Get a list of selected test cases
        if (!treeData.contains(parent)) {
            return testCases;
        }
        for (Object o : treeData.getChildren(parent)) {
            if (o instanceof String) {
                String id = (String) o;
                if (id.startsWith("tc")) {
                    //Is a Test Case
                    TreeTableCheckBox ttcb = checkboxes.get(id);
                    if (ttcb != null && ttcb.getValue() != null
                            && ttcb.getValue()) {
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
                            pid = treeData.getParent(pid);
                        }
                    }
                }
            }
            if (treeData.contains(o) && !treeData.getChildren(o).isEmpty()) {
                testCases.addAll(processChildren(o));
            }
        }
        return testCases;
    }

    /**
     * @return the projects
     */
    public List<Integer> getProjects() {
        return projects;
    }

    /**
     * Tree access backing the checkboxes' parent/child cascade.
     */
    private class TreeNavigatorImpl implements TreeTableCheckBox.TreeNavigator {

        @Override
        public boolean hasChildren(Object objectId) {
            return treeData.contains(objectId)
                    && !treeData.getChildren(objectId).isEmpty();
        }

        @Override
        public Collection<Object> getChildren(Object objectId) {
            if (!treeData.contains(objectId)) {
                return new ArrayList<>();
            }
            return treeData.getChildren(objectId);
        }

        @Override
        public Object getParent(Object objectId) {
            return treeData.contains(objectId)
                    ? treeData.getParent(objectId) : null;
        }

        @Override
        public TreeTableCheckBox getCheckBox(Object objectId) {
            return checkboxes.get(objectId);
        }
    }
}
