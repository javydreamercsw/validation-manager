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

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TreeTable;
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
import java.util.Date;
import java.util.List;
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
    private final TreeTable testTree
            = new TreeTable("available.tests");
    private final OptionGroup userGroup
            = new OptionGroup("available.tester");
    private TestCaseExecutionServer tce = null;
    private TestCaseServer tc = null;
    private static final Logger LOG
            = Logger.getLogger(AssignUserStep.class.getSimpleName());
    private final ValidationManagerUI ui;

    public AssignUserStep(ValidationManagerUI ui, Object item) {
        this.key = item;
        this.ui = ui;
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
        testTree.addContainerProperty("general.name",
                TreeTableCheckBox.class, "");
        testTree.addContainerProperty("general.description",
                String.class, "");
        testTree.setWidth("20em");
        testCases.forEach((t) -> {
            testTree.addItem(new Object[]{new TreeTableCheckBox(testTree,
                t.getName(), t.getTestCasePK()),
                t.getSummary() == null ? "" : new String(t.getSummary(),
                StandardCharsets.UTF_8)}, t.getTestCasePK());
            testTree.setChildrenAllowed(t.getTestCasePK(), false);
        });
        testTree.setPageLength(testCases.size() + 1);
        testTree.setSizeFull();
        l.addComponent(testTree);
        //Add list of testers
        users.addAll(RoleServer.getRole("tester").getVmUserList());
        BeanItemContainer<VmUser> userContainer
                = new BeanItemContainer<>(VmUser.class);
        userContainer.addAll(users);
        userGroup.setContainerDataSource(userContainer);
        userGroup.setItemCaptionMode(ItemCaptionMode.EXPLICIT);
        userGroup.getItemIds().forEach(id -> {
            VmUser u = (VmUser) id;
            userGroup.setItemCaption(id, u.getFirstName() + " "
                    + u.getLastName());
            userGroup.setItemIcon(id, VaadinIcons.USER);
        });
        l.addComponent(userGroup);
        return l;
    }

    @Override
    public boolean onAdvance() {
        boolean selectedTestCase = false;
        List<TestCasePK> testCaseIds = new ArrayList<>();
        for (Object id : testTree.getItemIds()) {
            Item item = testTree.getItem(id);
            Object val = item.getItemProperty("general.name").getValue();
            if (val instanceof TreeTableCheckBox) {
                TreeTableCheckBox ttcb = (TreeTableCheckBox) val;
                if (ttcb.getValue()) {
                    selectedTestCase = true;
                    testCaseIds.add((TestCasePK) id);
                }
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
            VMUserServer user = new VMUserServer((VmUser) userGroup.getValue());
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
}
