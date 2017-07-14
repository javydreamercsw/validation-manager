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

import com.vaadin.data.Container;
import com.vaadin.ui.Tree;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMUI;
import static com.validation.manager.core.VMUI.PROJECT_ICON;
import com.validation.manager.core.api.internationalization.InternationalizationProvider;
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.tool.Tool;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class ProjectTreeComponent extends Tree {

    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private final String projTreeRoot = TRANSLATOR
            .translate("project.root.label");
    private boolean showProject = true, showRequirement = true,
            showTestCase = true, showExecution = true;

    public ProjectTreeComponent() {
        update();
    }

    public ProjectTreeComponent(String caption, boolean showProject,
            boolean showRequirement, boolean showTestCase) {
        super(caption);
        this.showProject = showProject;
        this.showRequirement = showRequirement;
        this.showTestCase = showTestCase;
        update();
    }

    public ProjectTreeComponent(String caption, Container dataSource,
            boolean showProject, boolean showRequirement, boolean showTestCase,
            boolean showExecution) {
        super(caption, dataSource);
        this.showProject = showProject;
        this.showRequirement = showRequirement;
        this.showTestCase = showTestCase;
        this.showExecution = showExecution;
        update();
    }

    public void update() {
        removeAllItems();
        addItem(projTreeRoot);
        setImmediate(true);
        expandItem(projTreeRoot);
        setSizeFull();
        new ProjectJpaController(DataBaseManager.getEntityManagerFactory())
                .findProjectEntities().forEach((p) -> {
                    if (p.getParentProjectId() == null) {
                        //Check for permissions to show the project
//                        if (((VMUI) UI.getCurrent()).getUser() != null
//                                && ((VMUI) UI.getCurrent()).checkAnyProjectRole(p, null)
//                                || !((VMUI) UI.getCurrent()).getUser()
//                                        .getRoleList().isEmpty()) {
                        addProject(p);
//                        }
                    }
                });
    }

    public void addProject(Project p) {
        if (showProject) {
            addItem(p);
            setItemCaption(p, p.getName());
            setParent(p, p.getParentProjectId() == null
                    ? projTreeRoot : p.getParentProjectId());
            setItemIcon(p, PROJECT_ICON);
            boolean children = false;
            if (showProject && !p.getProjectList().isEmpty()) {
                p.getProjectList().forEach((sp) -> {
                    addProject(sp);
                });
                children = true;
            }
            if (showRequirement && !p.getRequirementSpecList().isEmpty()) {
                p.getRequirementSpecList().forEach((rs) -> {
                    addRequirementSpec(rs);
                });
                children = true;
            }
            if (showTestCase && !p.getTestProjectList().isEmpty()) {
                p.getTestProjectList().forEach((tp) -> {
                    addTestProject(tp);
                });
                children = true;
            }
            if (showExecution) {
                List<TestCaseExecution> executions = TestCaseExecutionServer
                        .getExecutions(p);
                String id = "executions" + p.getId();
                addItem(id);
                setItemCaption(id, TRANSLATOR.translate("general.execution"));
                setItemIcon(id, VMUI.EXECUTIONS_ICON);
                setParent(id, p);
                executions.forEach((tce) -> {
                    addTestCaseExecutions(id, tce);
                });
                children = true;
            }
            if (!children) {
                // No subprojects
                setChildrenAllowed(p, false);
            }
        }
    }

    private void addRequirementSpec(RequirementSpec rs) {
        if (showRequirement) {
            // Add the item as a regular item.
            addItem(rs);
            setItemCaption(rs, rs.getName());
            setItemIcon(rs, VMUI.SPEC_ICON);
            // Set it to be a child.
            setParent(rs, rs.getProject());
            if (rs.getRequirementSpecNodeList().isEmpty()
                    && rs.getBaselineList().isEmpty()) {
                //No children
                setChildrenAllowed(rs, false);
            } else {
                rs.getRequirementSpecNodeList().forEach((rsn) -> {
                    addRequirementSpecsNode(rsn);
                });
                //Add the baseline to the spec
                rs.getBaselineList().forEach(bl -> {
                    addBaseline(bl);
                });
            }
        }
    }

    private void addTestProject(TestProject tp) {
        if (showTestCase) {
            addItem(tp);
            setItemCaption(tp, tp.getName());
            setItemIcon(tp, VMUI.TEST_SUITE_ICON);
            setParent(tp, tp.getProjectList().get(0));
            boolean children = false;
            if (!tp.getTestPlanList().isEmpty()) {
                tp.getTestPlanList().forEach((plan) -> {
                    addTestPlan(plan);
                });
                children = true;
            }
            setChildrenAllowed(tp, children);
        }
    }

    private void addTestCaseExecutions(String parent, TestCaseExecution tce) {
        if (showExecution) {
            addItem(tce);
            setItemCaption(tce, tce.getName());
            setItemIcon(tce, VMUI.EXECUTION_ICON);
            setParent(tce, parent);
            for (ExecutionStep es : tce.getExecutionStepList()) {
                //Group under the Test Case
                TestCase tc = es.getStep().getTestCase();
                Collection<?> children = getChildren(tce);
                String node = Tool.buildId(tce,
                        Tool.buildId(tc, null, false)).toString();
                boolean add = true;
                if (children != null) {
                    //Check if already added as children
                    for (Object o : children) {
                        if (o.equals(node)) {
                            add = false;
                            break;
                        }
                    }
                }
                if (add) {
                    //Add Test Case if not there
                    addItem(node);
                    setItemCaption(node, tc.getName());
                    setItemIcon(node, VMUI.TEST_ICON);
                    setParent(node, tce);
                }
                addItem(es);
                setItemCaption(es, "Step #" + es.getStep().getStepSequence());
                //Use icon based on result of step
                setItemIcon(es, VMUI.STEP_ICON);
                setParent(es, node);
                setChildrenAllowed(es, false);
            }
        }
    }

    private void addRequirementSpecsNode(RequirementSpecNode rsn) {
        if (showRequirement) {
            // Add the item as a regular item.
            addItem(rsn);
            setItemCaption(rsn, rsn.getName());
            setItemIcon(rsn, VMUI.SPEC_ICON);
            // Set it to be a child.
            setParent(rsn, rsn.getRequirementSpec());
            if (rsn.getRequirementList().isEmpty()) {
                //No children
                setChildrenAllowed(rsn, false);
            } else {
                ArrayList<Requirement> list
                        = new ArrayList<>(rsn.getRequirementList());
                Collections.sort(list,
                        (Requirement o1, Requirement o2)
                        -> o1.getUniqueId().compareTo(o2.getUniqueId()));
                list.forEach((req) -> {
                    addRequirement(req);
                });
            }
        }
    }

    private void addBaseline(Baseline bl) {
        if (showRequirement && !containsId(bl)) {
            addItem(bl);
            setItemCaption(bl, bl.getBaselineName());
            setItemIcon(bl, VMUI.BASELINE_ICON);
            setParent(bl, bl.getRequirementSpec());
            //No children
            setChildrenAllowed(bl, false);
        }
    }

    private void addTestPlan(TestPlan tp) {
        if (showTestCase) {
            addItem(tp);
            setItemCaption(tp, tp.getName());
            setItemIcon(tp, VMUI.TEST_PLAN_ICON);
            setParent(tp, tp.getTestProject());
            if (!tp.getTestCaseList().isEmpty()) {
                tp.getTestCaseList().forEach((tc) -> {
                    addTestCase(tc, tp);
                });
            } else {
                setChildrenAllowed(tp, false);
            }
        }
    }

    private void addRequirement(Requirement req) {
        if (showRequirement) {
            // Add the item as a regular item.
            addItem(req);
            setItemCaption(req, req.getUniqueId());
            setItemIcon(req, VMUI.REQUIREMENT_ICON);
            setParent(req, req.getRequirementSpecNode());
            //No children
            setChildrenAllowed(req, false);
        }
    }

    private void addTestCase(TestCase t, TestPlan plan) {
        if (showTestCase) {
            addItem(t);
            setItemCaption(t, t.getName());
            setItemIcon(t, VMUI.TEST_ICON);
            setParent(t, plan);
            List<Step> stepList = t.getStepList();
            Collections.sort(stepList, (Step o1, Step o2)
                    -> o1.getStepSequence() - o2.getStepSequence());
            stepList.forEach((s) -> {
                addStep(s);
            });
        }
    }

    private void addStep(Step s) {
        if (showTestCase) {
            addStep(s, null);
        }
    }

    private void addStep(Step s, Object parent) {
        if (showTestCase) {
            addItem(s);
            setItemCaption(s, "Step # " + s.getStepSequence());
            setItemIcon(s, VMUI.STEP_ICON);
            Object parentId = s.getTestCase();
            if (parent != null) {
                parentId = parent;
            }
            setParent(s, parentId);
            setChildrenAllowed(s, false);
        }
    }
}
