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

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.ui.TreeGrid;
import com.validation.manager.core.DataBaseManager;
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
 * Project hierarchy rendered as a {@link TreeGrid}. The tree mixes several
 * entity types (plus a few synthetic String nodes), so items are identified by
 * their natural object identity.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class ProjectTreeComponent extends TreeGrid<Object> {

    private static final InternationalizationProvider TRANSLATOR
            = Lookup.getDefault().lookup(InternationalizationProvider.class);
    private final String projTreeRoot = TRANSLATOR
            .translate("project.root.label");
    private boolean showProject = true, showRequirement = true,
            showTestCase = true, showExecution = true;
    private TreeDataProvider<Object> dataProvider;

    public ProjectTreeComponent() {
        update();
    }

    public ProjectTreeComponent(String caption, boolean showProject,
            boolean showRequirement, boolean showTestCase) {
        super();
        setCaption(caption);
        this.showProject = showProject;
        this.showRequirement = showRequirement;
        this.showTestCase = showTestCase;
        update();
    }

    public ProjectTreeComponent(String caption, Object dataSource,
            boolean showProject, boolean showRequirement, boolean showTestCase,
            boolean showExecution) {
        //The v7 Container based constructor is no longer applicable.
        this(caption, showProject, showRequirement, showTestCase);
        this.showExecution = showExecution;
    }

    public TreeDataProvider<Object> getProjectDataProvider() {
        if (dataProvider == null) {
            update();
        }
        return dataProvider;
    }

    /**
     * Direct access to the underlying tree data for structural edits.
     */
    public TreeData<Object> getTreeData() {
        return getProjectDataProvider().getTreeData();
    }

    @Override
    public void setDataProvider(
            com.vaadin.data.provider.DataProvider<Object, ?> dataProvider) {
        super.setDataProvider(dataProvider);
        if (dataProvider instanceof TreeDataProvider) {
            this.dataProvider = (TreeDataProvider<Object>) dataProvider;
        }
    }

    public void update() {
        TreeData<Object> treeData = new TreeData<>();
        treeData.addRootItems(projTreeRoot);
        setSizeFull();
        if (getColumn("caption") == null) {
            addColumn(this::getCaptionFor)
                    .setId("caption")
                    .setCaption(projTreeRoot);
            setHierarchyColumn("caption");
        }
        new ProjectJpaController(DataBaseManager.getEntityManagerFactory())
                .findProjectEntities().forEach((p) -> {
                    if (p.getParentProjectId() == null) {
                        //Check for permissions to show the project
//                        if (((VMUI) UI.getCurrent()).getUser() != null
//                                && ((VMUI) UI.getCurrent()).checkAnyProjectRole(p, null)
//                                || !((VMUI) UI.getCurrent()).getUser()
//                                        .getRoleList().isEmpty()) {
                        addProject(p, treeData);
//                        }
                    }
                });
        setDataProvider(new TreeDataProvider<>(treeData));
        expand(projTreeRoot);
    }

    private void addProject(Project p, TreeData<Object> treeData) {
        if (showProject) {
            treeData.addItem(p.getParentProjectId() == null
                    ? projTreeRoot : p.getParentProjectId(), p);
            if (showProject && !p.getProjectList().isEmpty()) {
                p.getProjectList().forEach((sp) -> {
                    addProject(sp, treeData);
                });
            }
            if (showRequirement && !p.getRequirementSpecList().isEmpty()) {
                p.getRequirementSpecList().forEach((rs) -> {
                    addRequirementSpec(rs, treeData);
                });
            }
            if (showTestCase && !p.getTestProjectList().isEmpty()) {
                p.getTestProjectList().forEach((tp) -> {
                    addTestProject(tp, treeData);
                });
            }
            if (showExecution) {
                List<TestCaseExecution> executions = TestCaseExecutionServer
                        .getExecutions(p);
                String id = "executions" + p.getId();
                treeData.addItem(p, id);
                executions.forEach((tce) -> {
                    addTestCaseExecutions(id, tce, treeData);
                });
            }
        }
    }

    private void addRequirementSpec(RequirementSpec rs,
            TreeData<Object> treeData) {
        if (showRequirement) {
            // Add the item as a regular item.
            treeData.addItem(rs.getProject(), rs);
            rs.getRequirementSpecNodeList().forEach((rsn) -> {
                addRequirementSpecsNode(rsn, treeData);
            });
            //Add the baseline to the spec
            rs.getBaselineList().forEach(bl -> {
                addBaseline(bl, treeData);
            });
        }
    }

    private void addTestProject(TestProject tp, TreeData<Object> treeData) {
        if (showTestCase) {
            treeData.addItem(tp.getProjectList().get(0), tp);
            tp.getTestPlanList().forEach((plan) -> {
                addTestPlan(plan, treeData);
            });
        }
    }

    private void addTestCaseExecutions(String parent, TestCaseExecution tce,
            TreeData<Object> treeData) {
        if (showExecution) {
            //Attach the execution under the synthetic "executions" node
            treeData.addItem(parent, tce);
            for (ExecutionStep es : tce.getExecutionStepList()) {
                //Group under the Test Case
                TestCase tc = es.getStep().getTestCase();
                Collection<Object> children = treeData.getChildren(tce);
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
                    treeData.addItem(tce, node);
                }
                treeData.addItem(node, es);
                treeData.setParent(es, node);
            }
        }
    }

    private void addRequirementSpecsNode(RequirementSpecNode rsn,
            TreeData<Object> treeData) {
        if (showRequirement) {
            // Add the item as a regular item.
            treeData.addItem(rsn.getRequirementSpec(), rsn);
            ArrayList<Requirement> list
                    = new ArrayList<>(rsn.getRequirementList());
            Collections.sort(list,
                    (Requirement o1, Requirement o2)
                    -> o1.getUniqueId().compareTo(o2.getUniqueId()));
            list.forEach((req) -> {
                addRequirement(req, treeData);
            });
        }
    }

    private void addBaseline(Baseline bl, TreeData<Object> treeData) {
        if (showRequirement && !treeData.contains(bl)) {
            treeData.addItem(bl.getRequirementSpec(), bl);
        }
    }

    private void addTestPlan(TestPlan tp, TreeData<Object> treeData) {
        if (showTestCase) {
            treeData.addItem(tp.getTestProject(), tp);
            tp.getTestCaseList().forEach((tc) -> {
                addTestCase(tc, tp, treeData);
            });
        }
    }

    private void addRequirement(Requirement req, TreeData<Object> treeData) {
        if (showRequirement) {
            // Add the item as a regular item.
            treeData.addItem(req.getRequirementSpecNode(), req);
        }
    }

    private void addTestCase(TestCase t, TestPlan plan,
            TreeData<Object> treeData) {
        if (showTestCase) {
            treeData.addItem(plan, t);
            List<Step> stepList = t.getStepList();
            Collections.sort(stepList, (Step o1, Step o2)
                    -> o1.getStepSequence() - o2.getStepSequence());
            stepList.forEach((s) -> {
                addStep(s, t, treeData);
            });
        }
    }

    private void addStep(Step s, Object parent, TreeData<Object> treeData) {
        if (showTestCase) {
            Object parentId = s.getTestCase();
            if (parent != null) {
                parentId = parent;
            }
            treeData.addItem(parentId, s);
        }
    }

    private String getCaptionFor(Object item) {
        if (item instanceof Project) {
            return ((Project) item).getName();
        } else if (item instanceof RequirementSpec) {
            return ((RequirementSpec) item).getName();
        } else if (item instanceof RequirementSpecNode) {
            return ((RequirementSpecNode) item).getName();
        } else if (item instanceof Requirement) {
            return ((Requirement) item).getUniqueId();
        } else if (item instanceof TestProject) {
            return ((TestProject) item).getName();
        } else if (item instanceof TestPlan) {
            return ((TestPlan) item).getName();
        } else if (item instanceof TestCase) {
            return ((TestCase) item).getName();
        } else if (item instanceof TestCaseExecution) {
            return ((TestCaseExecution) item).getName();
        } else if (item instanceof Step) {
            return "Step # " + ((Step) item).getStepSequence();
        } else if (item instanceof Baseline) {
            return ((Baseline) item).getBaselineName();
        } else if (item instanceof String) {
            String val = (String) item;
            if (val.equals(projTreeRoot)) {
                return projTreeRoot;
            } else if (val.startsWith("executions")) {
                return TRANSLATOR.translate("general.execution");
            } else if (val.startsWith("tce")) {
                return TRANSLATOR.translate("general.execution");
            }
        }
        return String.valueOf(item);
    }
}
