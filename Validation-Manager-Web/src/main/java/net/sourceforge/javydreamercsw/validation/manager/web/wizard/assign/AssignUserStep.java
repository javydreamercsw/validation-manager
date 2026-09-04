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
package net.sourceforge.javydreamercsw.validation.manager.web.wizard.assign;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import static com.validation.manager.core.ContentProvider.TRANSLATOR;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCasePK;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.server.core.ActivityServer;
import com.validation.manager.core.server.core.RoleServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.VMUserServer;
import com.validation.manager.core.tool.TCEExtraction;
import com.validation.manager.core.tool.Tool;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import net.sourceforge.javydreamercsw.validation.manager.web.component.TreeTableCheckBox;
import org.vaadin.teemu.wizards.WizardStep;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class AssignUserStep implements WizardStep {

    private final Object key;
    private final TreeData<Object> treeData = new TreeData<>();
    private final Map<Object, TreeTableCheckBox> checkboxes = new HashMap<>();
    private final TreeGrid<Object> testTree = new TreeGrid<>("available.tests");
    private final RadioButtonGroup<VmUser> userGroup
            = new RadioButtonGroup<>("available.tester");
    private TestCaseExecutionServer tce = null;
    private TestCaseServer tc = null;
    private static final Logger LOG
            = Logger.getLogger(AssignUserStep.class.getSimpleName());
    private final ValidationManagerUI ui;

    public AssignUserStep(ValidationManagerUI ui, Object item) {
        this.key = item;
        this.ui = ui;
        testTree.addColumn(o -> getCheckBoxFor(o)).setId("general.name")
                .setCaption("general.name")
                .setRenderer(new ComponentRenderer());
        testTree.addColumn(AssignUserStep::getDescription)
                .setId("general.description")
                .setCaption("general.description");
        testTree.setWidth(20, Unit.EM);
        testTree.setHierarchyColumn("general.name");
        testTree.setSizeFull();
    }

    private TreeTableCheckBox getCheckBoxFor(Object id) {
        return checkboxes.get(id);
    }

    private static String getDescription(Object id) {
        TestCase tc = findTestCase(id);
        return tc == null ? ""
                : (tc.getSummary() == null ? ""
                        : new String(tc.getSummary(), StandardCharsets.UTF_8));
    }

    private static TestCase findTestCase(Object id) {
        if (id instanceof TestCasePK) {
            TestCasePK pk = (TestCasePK) id;
            return new TestCaseJpaController(DataBaseManager
                    .getEntityManagerFactory()).findTestCase(pk);
        }
        return null;
    }

    @Override
    public String getCaption() {
        return "assign.test.case";
    }

    @Override
    public Component getContent() {
        VerticalLayout l = new VerticalLayout();
        List<TestCase> testCases = new ArrayList<>();
        List<VmUser> users = new ArrayList<>();
        TCEExtraction extracted = Tool.extractTCE(key);
        tc = extracted.getTestCase();
        tce = extracted.getTestCaseExecution();
        if (tc != null) {
            testCases.add(tc.getEntity());
        } else if (tce != null) {
            tce.getExecutionStepList().stream().filter((es)
                    -> (!testCases.contains(es.getStep().getTestCase())))
                    .forEachOrdered((es) -> {
                        testCases.add(es.getStep().getTestCase());
                    });
        }
        testCases.forEach((t) -> {
            TestCasePK id = t.getTestCasePK();
            treeData.addRootItems(id);
            TreeTableCheckBox check = new TreeTableCheckBox(new TreeNavigatorImpl(),
                    t.getName(), id);
            checkboxes.put(id, check);
        });
        testTree.setHeightByRows(testCases.size() + 1);
        testTree.setDataProvider(new TreeDataProvider<>(treeData));
        l.addComponent(testTree);
        //Add list of testers
        users.addAll(RoleServer.getRole("tester").getVmUserList());
        userGroup.setItems(users);
        userGroup.setItemCaptionGenerator((VmUser u) -> u.getFirstName() + " "
                + u.getLastName());
        userGroup.setItemIconGenerator(u -> VaadinIcons.USER);
        l.addComponent(userGroup);
        return l;
    }

    @Override
    public boolean onAdvance() {
        boolean selectedTestCase = false;
        List<TestCasePK> testCaseIds = new ArrayList<>();
        for (TestCasePK id : treeData.getRootItems()) {
            TreeTableCheckBox ttcb = checkboxes.get(id);
            if (ttcb != null && ttcb.getValue() != null && ttcb.getValue()) {
                selectedTestCase = true;
                testCaseIds.add(id);
            }
        }
        if (!selectedTestCase) {
            Notification.show("unable.to.proceed",
                    "select.test.case.message",
                    Notification.Type.WARNING_MESSAGE);
            return false;
        }
        try {
            //Now process the data
            VMUserServer user = new VMUserServer(userGroup.getValue());
            TestCaseJpaController c
                    = new TestCaseJpaController(DataBaseManager
                            .getEntityManagerFactory());
            testCaseIds.forEach((id) -> {
                user.assignTestCase(tce, c.findTestCase(id), ui.getUser());
            });
            new ActivityServer(5, new Date(),
                    TRANSLATOR.translate("test.case.assign.desc")
                            .replaceAll("%u",
                                    ((VMUI) UI.getCurrent()).getUser().toString())
                            .replaceAll("%i", TRANSLATOR.translate("general.test.case"))
                            .replaceAll("%t", user.toString()),
                    ((VMUI) UI.getCurrent()).getUser().getEntity())
                    .write2DB();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public boolean onBack() {
        return false;
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
