package net.sourceforge.javydreamercsw.validation.manager.web.tester;

import com.vaadin.event.Action;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.validation.manager.core.AbstractProvider;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ExecutionStepPK;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
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
import net.sourceforge.javydreamercsw.validation.manager.web.execution.ExecutionWindow;
import net.sourceforge.javydreamercsw.validation.manager.web.quality.QualityScreenProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class ExecutionScreen extends AbstractProvider {

    private ExecutionWindow executionWindow = null;
    private final TreeTable testCaseTree = new TreeTable("Available Tests");

    public ExecutionScreen() {
        testCaseTree.setAnimationsEnabled(true);
        testCaseTree.addContainerProperty(ValidationManagerUI.getInstance()
                .translate("general.name"),
                String.class, "");
        testCaseTree.addGeneratedColumn(ValidationManagerUI.getInstance()
                .translate("general.status"),
                (Table source, Object itemId, Object columnId) -> {
                    if (ValidationManagerUI.getInstance()
                            .translate("general.status").equals(columnId)
                    && itemId instanceof String) {
                        String id = (String) itemId;
                        String message;
                        HorizontalLayout icons = new HorizontalLayout();
                        Button label = new Button();
                        Button label2 = new Button();
                        icons.addComponent(label2);
                        icons.addComponent(label);
                        label.addStyleName(ValoTheme.BUTTON_BORDERLESS
                                + " labelButton");
                        label2.addStyleName(ValoTheme.BUTTON_BORDERLESS
                                + " labelButton");
                        Map<String, Integer> summary = new HashMap<>();
                        boolean locked = false;
                        if (id.startsWith("tce")) {
                            TestCaseExecutionServer tce
                            = new TestCaseExecutionServer(
                                    Integer.parseInt(id.substring(3)));
                            summary = getSummary(tce, -1);
                            locked = isLocked(tce);
                        } else if (id.startsWith("es")) {
                            ExecutionStepServer es
                            = new ExecutionStepServer(extractExecutionStepPK(id));
                            summary = getSummary(
                                    es.getTestCaseExecution(),
                                    Integer.parseInt(id
                                            .substring(id.lastIndexOf("-") + 1)));
                            locked = es.getLocked();
                        }
                        if (locked) {
                            label2.setIcon(VaadinIcons.LOCK);
                            label2.setDescription(ValidationManagerUI
                                    .getInstance().translate("message.locked"));
                        }
                        if (!summary.isEmpty()) {
                            if (summary.containsKey("result.fail")) {
                                //At least one failure means the test case is failing
                                message = "result.fail";
                            } else if (summary.containsKey("result.blocked")) {
                                //It is blocked
                                message = "result.blocked";
                            } else if (summary.containsKey("result.pending")
                            && !summary.containsKey("result.pass")) {
                                //Still not done
                                message = "result.pending";
                            } else if (summary.containsKey("result.pending")
                            && summary.containsKey("result.pass")) {
                                //In progress
                                message = "result.progress";
                            } else {
                                //All is pass
                                message = "result.pass";
                            }
                            label.setCaption(ValidationManagerUI
                                    .getInstance().translate(message));
                            label.setDescription(ValidationManagerUI
                                    .getInstance().translate(message));
                            //Completed. Now check result
                            switch (message) {
                                case "result.pass":
                                    label.setIcon(VaadinIcons.CHECK);
                                    break;
                                case "result.fail":
                                    label.setIcon(VaadinIcons.CLOSE);
                                    break;
                                case "result.blocked":
                                    label.setIcon(VaadinIcons.PAUSE);
                                    break;
                                case "result.pending":
                                    label.setIcon(VaadinIcons.CLOCK);
                                    break;
                                case "result.progress":
                                    label.setIcon(VaadinIcons.AUTOMATION);
                                    break;
                                default:
                                    label.setIcon(VaadinIcons.CLOCK);
                                    break;
                            }
                            return icons;
                        }
                    }
                    return new Label();
                });
        testCaseTree.addContainerProperty(ValidationManagerUI.getInstance()
                .translate("general.summary"),
                String.class, "");
        testCaseTree.addContainerProperty(ValidationManagerUI.getInstance()
                .translate("general.assignment.date"),
                String.class, "");
        testCaseTree.setVisibleColumns(new Object[]{ValidationManagerUI
            .getInstance().translate("general.name"),
            ValidationManagerUI.getInstance().translate("general.status"),
            ValidationManagerUI.getInstance().translate("general.summary"),
            ValidationManagerUI.getInstance().translate("general.assignment.date")});
        testCaseTree.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(Object target, Object sender) {
                List<Action> actions = new ArrayList<>();
                if (target instanceof String) {
                    String t = (String) target;
                    int tcID = -1;
                    TestCaseExecutionServer tce = null;
                    if (t.startsWith("es")) {
                        tce = new TestCaseExecutionServer(new ExecutionStepServer(
                                extractExecutionStepPK(t))
                                .getTestCaseExecution().getId());
                        tcID = Integer.parseInt(t
                                .substring(t.lastIndexOf("-") + 1));
                    } else if (t.startsWith("tce")) {
                        tce = new TestCaseExecutionServer(
                                Integer.parseInt(t.substring(3)));
                    }
                    if (!isLocked(tce, tcID)
                            && ExecutionScreen.this instanceof TesterScreenProvider) {
                        actions.add(new Action(ValidationManagerUI.getInstance()
                                .translate("general.execute")));
                    } else if (isLocked(tce, tcID)
                            && ExecutionScreen.this instanceof QualityScreenProvider) {
                        actions.add(new Action(ValidationManagerUI.getInstance()
                                .translate("general.review")));
                    }
                }
                return actions.toArray(new Action[actions.size()]);
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                //Parse the information to get the exact Execution Step
                List<TestCaseExecutionServer> executions = new ArrayList<>();
                int tcID = -1;
                if (((String) target).startsWith("tce")) {
                    executions.add(new TestCaseExecutionServer(
                            Integer.parseInt(((String) target).substring(3))));
                } else if (((String) target).startsWith("es")) {
                    executions.add(new TestCaseExecutionServer(new ExecutionStepServer(
                            extractExecutionStepPK((String) target))
                            .getTestCaseExecution().getId()));
                    tcID = Integer.parseInt(((String) target)
                            .substring(((String) target).lastIndexOf("-") + 1));
                }
                showExecutionScreen(executions, tcID);
            }
        });
    }

    protected Map<String, Integer> getSummary(TestCaseExecution tce, int tcId) {
        Map<String, Integer> summary = new HashMap<>();
        tce.getExecutionStepList().forEach((ExecutionStep es) -> {
            if (tcId == -1 || es.getStep().getTestCase().getId() == tcId) {
                if (es.getExecutionStart() != null && es.getExecutionEnd() == null) {
                    //In progress
                    if (!summary.containsKey("progress")) {
                        summary.put("progress", 0);
                    }
                    summary.put("progress", summary.get("progress") + 1);
                } else if (es.getResultId() == null
                        || (es.getExecutionStart() == null
                        && es.getExecutionEnd() == null)) {
                    //Not started
                    if (!summary.containsKey("result.pending")) {
                        summary.put("result.pending", 0);
                    }
                    summary.put("result.pending", summary.get("result.pending") + 1);
                } else if (es.getExecutionStart() != null && es.getExecutionEnd() != null) {
                    if (!summary.containsKey(es.getResultId().getResultName())) {
                        summary.put(es.getResultId().getResultName(), 0);
                    }
                    summary.put(es.getResultId().getResultName(),
                            summary.get(es.getResultId().getResultName()) + 1);
                }
            }
        });
        return summary;
    }

    protected ExecutionStepPK extractExecutionStepPK(String itemId) {
        String id = itemId.substring(2); //Remove es
        int esId;
        int sId;
        int tcId;
        StringTokenizer st = new StringTokenizer(id, "-");
        esId = Integer.parseInt(st.nextToken());
        sId = Integer.parseInt(st.nextToken());
        tcId = Integer.parseInt(st.nextToken());
        return new ExecutionStepPK(esId, sId, tcId);
    }

    protected void showExecutionScreen(List<TestCaseExecutionServer> executions,
            int tcID) {
        if (executionWindow == null) {
            executionWindow = new ExecutionWindow(executions, tcID,
                    this instanceof QualityScreenProvider);
            executionWindow.setCaption("Test Execution");
            executionWindow.setVisible(true);
            executionWindow.setClosable(false);
            executionWindow.setResizable(false);
            executionWindow.center();
            executionWindow.setModal(true);
            executionWindow.setSizeFull();
        }
        if (!ValidationManagerUI.getInstance().getWindows().contains(executionWindow)) {
            ValidationManagerUI.getInstance().addWindow(executionWindow);
        }
    }

    @Override
    public Component getContent() {
        VerticalLayout vl = new VerticalLayout();
        update();
        vl.addComponent(testCaseTree);
        return vl;
    }

    @Override
    public void update() {
        testCaseTree.removeAllItems();
        if (ValidationManagerUI.getInstance().getUser() != null) {
            ProjectServer.getProjects().forEach(p -> {
                if (p.getParentProjectId() == null) {
                    testCaseTree.addItem(new Object[]{p.getName(),
                        "", "",}, "p" + p.getId());
                    testCaseTree.setItemIcon("p" + p.getId(),
                            ValidationManagerUI.PROJECT_ICON);
                    p.getProjectList().forEach(sp -> {
                        //Add subprojects
                        testCaseTree.addItem(new Object[]{sp.getName(),
                            "", "",}, "p" + sp.getId());
                        testCaseTree.setParent("p" + sp.getId(), "p" + p.getId());
                        testCaseTree.setItemIcon("p" + sp.getId(),
                                ValidationManagerUI.PROJECT_ICON);
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
                                            testCaseTree.setParent("tce" + tce.getId(),
                                                    "p" + sp.getId());
                                            testCaseTree.setItemIcon("tce" + tce.getId(),
                                                    ValidationManagerUI.EXECUTION_ICON);
                                            if (this instanceof QualityScreenProvider
                                                    && es.getLocked()
                                                    || (es.getAssignee() != null
                                                    && es.getAssignee().getId()
                                                            .equals(ValidationManagerUI.getInstance()
                                                                    .getUser().getId()))) {
                                                TestCase tc = es.getStep().getTestCase();
                                                if (!tcids.contains(tc.getId())) {
                                                    tcids.add(tc.getId());
                                                    DateTimeFormatter format
                                                            = DateTimeFormatter.ofPattern("MMM d yyyy  hh:mm a");
                                                    LocalDateTime time
                                                            = LocalDateTime.ofInstant(es.getAssignedTime()
                                                                    .toInstant(), ZoneId.systemDefault());
                                                    String key = "es" + es.getExecutionStepPK().getTestCaseExecutionId()
                                                            + "-" + es.getStep().getStepPK().getId()
                                                            + "-" + tc.getId();
                                                    testCaseTree.addItem(new Object[]{tc.getName(),
                                                        tc.getSummary(), format.format(time),},
                                                            key);
                                                    testCaseTree.setParent(key, "tce"
                                                            + tce.getId());
                                                    testCaseTree.setItemIcon(key,
                                                            ValidationManagerUI.TEST_ICON);
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
        }
    }
}
