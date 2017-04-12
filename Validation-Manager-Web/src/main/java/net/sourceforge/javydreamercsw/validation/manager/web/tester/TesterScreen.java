package net.sourceforge.javydreamercsw.validation.manager.web.tester;

import com.vaadin.event.Action;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepPK;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.server.core.ExecutionResultServer;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;
import static net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI.EXECUTION_ICON;
import static net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI.PROJECT_ICON;
import static net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI.TEST_ICON;
import net.sourceforge.javydreamercsw.validation.manager.web.execution.ExecutionWindow;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TesterScreen extends Panel {

    private final ValidationManagerUI ui;
    private ExecutionWindow executionWindow = null;

    public TesterScreen(ValidationManagerUI ui) {
        this.ui = ui;
        init();
    }

    public TesterScreen(ValidationManagerUI ui, String caption) {
        super(caption);
        this.ui = ui;
        init();
    }

    private void init() {
        VerticalLayout vl = new VerticalLayout();
        TreeTable testCaseTree = new TreeTable("Available Tests");
        testCaseTree.setAnimationsEnabled(true);
        testCaseTree.addContainerProperty("Name", String.class, "");
        testCaseTree.addGeneratedColumn("Status",
                (Table source, Object itemId, Object columnId) -> {
                    if (columnId.equals("Status")
                    && itemId instanceof String
                    && ((String) itemId).startsWith("es")) {
                        Button label = new Button();
                        label.addStyleName(ValoTheme.BUTTON_BORDERLESS + " labelButton");
                        ExecutionStepServer ess = new ExecutionStepServer(extractExecutionStepPK((String) itemId));
                        String message;
                        if (ess.getResultId() == null) {
                            ExecutionResult result = ExecutionResultServer.getResult("result.pending");
                            message = result.getResultName();
                        } else {
                            message = ess.getResultId().getResultName();
                        }
                        label.setCaption(ui.translate(message));
                        if (ess.getExecutionStart() != null
                        && ess.getExecutionEnd() == null) {
                            //In progress
                            label.setIcon(VaadinIcons.AUTOMATION);
                        } else if (ess.getExecutionStart() == null
                        && ess.getExecutionEnd() == null) {
                            //Not started
                            label.setIcon(VaadinIcons.CLOCK);
                        } else if (ess.getExecutionStart() != null
                        && ess.getExecutionEnd() != null) {
                            //Completed. Now check result
                            switch (ess.getResultId().getId()) {
                                case 1:
                                    label.setIcon(VaadinIcons.CHECK);
                                    break;
                                case 2:
                                    label.setIcon(VaadinIcons.CLOSE);
                                    break;
                                case 3:
                                    label.setIcon(VaadinIcons.PAUSE);
                                    break;
                                default:
                                    label.setIcon(VaadinIcons.CLOCK);
                                    break;
                            }
                        }
                        return label;
                    }
                    return new Label();
                });
        testCaseTree.addContainerProperty("Summary", String.class, "");
        testCaseTree.addContainerProperty("Assignment Date",
                String.class, "");
        testCaseTree.setVisibleColumns(new Object[]{"Name",
            "Status", "Summary", "Assignment Date"});
        testCaseTree.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(Object target, Object sender) {
                List<Action> actions = new ArrayList<>();
                if (target instanceof String
                        && ((String) target).startsWith("es")) {
                    actions.add(new Action("Execute"));
                }
                return actions.toArray(new Action[actions.size()]);
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                //Parse the information to get the exact Execution Step
                List<TestCaseExecutionServer> executions = new ArrayList<>();
                executions.add(new TestCaseExecutionServer(new ExecutionStepServer(extractExecutionStepPK((String) target)).getTestCaseExecution().getId()));
                showExecutionScreen(executions);
            }
        });
        ProjectServer.getProjects().forEach(p -> {
            if (p.getParentProjectId() == null) {
                testCaseTree.addItem(new Object[]{p.getName(),
                    "", "",}, "p" + p.getId());
                testCaseTree.setItemIcon("p" + p.getId(), PROJECT_ICON);
                p.getProjectList().forEach(sp -> {
                    //Add subprojects
                    testCaseTree.addItem(new Object[]{sp.getName(),
                        "", "",}, "p" + sp.getId());
                    testCaseTree.setParent("p" + sp.getId(), "p" + p.getId());
                    testCaseTree.setItemIcon("p" + sp.getId(), PROJECT_ICON);
                    //Add applicable Executions
                    Map<Integer, ExecutionStep> tests = new HashMap<>();
                    sp.getTestProjectList().forEach(test -> {
                        test.getTestPlanList().forEach(tp -> {
                            tp.getTestCaseList().forEach(testCase -> {
                                List<Integer> tcids = new ArrayList<>();
                                testCase.getStepList().forEach(s -> {
                                    s.getExecutionStepList().forEach(es -> {
                                        TestCaseExecution tce = es.getTestCaseExecution();
                                        testCaseTree.addItem(new Object[]{tce.getName(),
                                            "", "",}, "tce" + tce.getId());
                                        testCaseTree.setParent("tce" + tce.getId(), "p" + sp.getId());
                                        testCaseTree.setItemIcon("tce" + tce.getId(), EXECUTION_ICON);
                                        if (es.getAssignee().getId().equals(ui.getUser().getId())) {
                                            TestCase tc = es.getStep().getTestCase();
                                            if (!tcids.contains(tc.getId())) {
                                                tcids.add(tc.getId());
                                                DateTimeFormatter format
                                                        = DateTimeFormatter.ofPattern("MMM d yyyy  hh:mm a");
                                                LocalDateTime time
                                                        = LocalDateTime.ofInstant(es.getAssignedTime()
                                                                .toInstant(), ZoneId.systemDefault());
                                                String key = "es" + es.getExecutionStepPK().getTestCaseExecutionId()
                                                        + "-" + es.getStep().getStepPK().getId() + "-" + tc.getId();
                                                testCaseTree.addItem(new Object[]{tc.getName(),
                                                    tc.getSummary(), format.format(time),},
                                                        key);
                                                testCaseTree.setParent(key, "tce" + tce.getId());
                                                testCaseTree.setItemIcon(key, TEST_ICON);
                                                testCaseTree.setChildrenAllowed(key, false);
                                            }
                                        }
                                    });
                                });
                                tcids.clear();
                            });
                        });
                    });
                    //Make columns autofit
                    int count = 0;
                    for (Object id : testCaseTree.getVisibleColumns()) {
                        if (count < 2) {
                            testCaseTree.setColumnExpandRatio(id, 1.0f);
                        }
                        count++;
                    }
                    testCaseTree.setSizeFull();
                });
            }
        });
        vl.addComponent(testCaseTree);
        setContent(vl);
    }

    private ExecutionStepPK extractExecutionStepPK(String itemId) {
        String id = itemId.substring(2);//Remove es
        int esId, sId, tcId;
        StringTokenizer st = new StringTokenizer(id, "-");
        esId = Integer.parseInt(st.nextToken());
        sId = Integer.parseInt(st.nextToken());
        tcId = Integer.parseInt(st.nextToken());
        return new ExecutionStepPK(esId, sId, tcId);
    }

    private void showExecutionScreen(List<TestCaseExecutionServer> executions) {
        if (executionWindow == null) {
            executionWindow = new ExecutionWindow(ui, executions);
            executionWindow.setCaption("Test Execution");
            executionWindow.setVisible(true);
            executionWindow.setClosable(false);
            executionWindow.setResizable(false);
            executionWindow.center();
            executionWindow.setModal(true);
            executionWindow.setSizeFull();
        }
        if (!ui.getWindows().contains(executionWindow)) {
            ui.addWindow(executionWindow);
        }
    }
}
