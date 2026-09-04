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

    /**
     * Row ids keep the same synthetic String ids the v7 TreeTable used
     * ("project" + project id, "testproject" + test project id and
     * {@link Tool#buildId(java.lang.Object)} test case ids) mixed with
     * {@link com.validation.manager.core.db.TestPlanPK} plan ids, so the
     * selection processing keeps working unchanged.
     */
    private final Project p;
    private final Wizard w;
    private final TreeData<Object> treeData = new TreeData<>();
    private final Map<Object, TreeTableCheckBox> checkboxes = new HashMap<>();
    private final Map<Object, String> descriptions = new HashMap<>();
    private final TreeNavigatorImpl navigator
            = new TreeNavigatorImpl(treeData, checkboxes);
    private final TreeGrid<Object> testTree = new TreeGrid<>();
    private TreeDataProvider<Object> dataProvider;
    private final List<Integer> projects = new ArrayList<>();
    private static final Logger LOG
            = Logger.getLogger(SelectTestCasesStep.class.getSimpleName());

    public SelectTestCasesStep(Wizard w, Project p) {
        this.p = p;
        this.w = w;
        testTree.setCaption("available.tests");
        testTree.addComponentColumn(this::getCheckBoxFor)
                .setId("general.name")
                .setCaption("general.name")
                .setRenderer(new ComponentRenderer());
        testTree.addColumn(this::getDescription)
                .setId("general.description")
                .setCaption("general.description");
        testTree.setWidth(20, Unit.EM);
        testTree.setHierarchyColumn("general.name");
    }

    private TreeTableCheckBox getCheckBoxFor(Object id) {
        return checkboxes.get(id);
    }

    private String getDescription(Object id) {
        return descriptions.getOrDefault(id, "");
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
            //Existing entries are kept so revisiting the step (back from the
            //detail step) keeps the current selection and expansion, like the
            //v7 TreeTable
            List<Object> expanded = new ArrayList<>();
            addProjectTestPlanning(p, expanded);
            if (dataProvider == null) {
                dataProvider = new TreeDataProvider<>(treeData);
                testTree.setDataProvider(dataProvider);
            } else {
                dataProvider.refreshAll();
            }
            //Projects and test projects start expanded, test plans collapsed
            testTree.expand(expanded);
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

    private void addProjectTestPlanning(Project p, List<Object> expanded) {
        //Add the test projects
        String projectId = "project" + p.getId();
        if (!treeData.contains(projectId)) {
            checkboxes.put(projectId, new TreeTableCheckBox(navigator,
                    p.getName(), projectId));
            descriptions.put(projectId, "");
            treeData.addRootItems(projectId);
            expanded.add(projectId);
        }
        if (p.getParentProjectId() != null) {
            //Add as child of the parent project when that is part of the
            //tree, as root item otherwise (the v7 TreeTable silently
            //ignored the re-parenting in that case)
            String parentId = "project" + p.getParentProjectId().getId();
            if (treeData.contains(parentId)) {
                treeData.setParent(projectId, parentId);
            }
        }
        p.getTestProjectList().forEach((tp) -> {
            String testProjectId = "testproject" + tp.getId();
            if (!treeData.contains(testProjectId)) {
                TreeTableCheckBox cb = new TreeTableCheckBox(navigator,
                        tp.getName(), testProjectId);
                cb.setIcon(ValidationManagerUI.TEST_SUITE_ICON);
                checkboxes.put(testProjectId, cb);
                descriptions.put(testProjectId, "");
                treeData.addItem(projectId, testProjectId);
                expanded.add(testProjectId);
            }
            tp.getTestPlanList().forEach((plan) -> {
                Object planId = plan.getTestPlanPK();
                if (!treeData.contains(planId)) {
                    TreeTableCheckBox pcb = new TreeTableCheckBox(navigator,
                            plan.getName(), planId);
                    pcb.setIcon(ValidationManagerUI.PLAN_ICON);
                    checkboxes.put(planId, pcb);
                    descriptions.put(planId, "");
                    treeData.addItem(testProjectId, planId);
                }
                plan.getTestCaseList().forEach((tc) -> {
                    Object tcId = Tool.buildId(tc);
                    if (!treeData.contains(tcId)) {
                        TreeTableCheckBox tccb = new TreeTableCheckBox(navigator,
                                tc.getName(), tcId);
                        tccb.setIcon(ValidationManagerUI.TEST_ICON);
                        checkboxes.put(tcId, tccb);
                        descriptions.put(tcId, tc.getSummary() != null
                                ? new String(tc.getSummary()) : "");
                        treeData.addItem(planId, tcId);
                    }
                });
            });
        });
        p.getProjectList().forEach((sp) -> {
            addProjectTestPlanning(sp, expanded);
        });
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
                    if (ttcb != null && Boolean.TRUE.equals(ttcb.getValue())) {
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
                            pid = navigator.getParent(pid);
                        }
                    }
                }
            }
            if (navigator.hasChildren(o)) {
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
    private static class TreeNavigatorImpl
            implements TreeTableCheckBox.TreeNavigator {

        private final TreeData<Object> treeData;
        private final Map<Object, TreeTableCheckBox> checkboxes;

        TreeNavigatorImpl(TreeData<Object> treeData,
                Map<Object, TreeTableCheckBox> checkboxes) {
            this.treeData = treeData;
            this.checkboxes = checkboxes;
        }

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
