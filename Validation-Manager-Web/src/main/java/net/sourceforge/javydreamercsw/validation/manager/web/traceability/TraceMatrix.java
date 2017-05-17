package net.sourceforge.javydreamercsw.validation.manager.web.traceability;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeTable;
import com.validation.manager.core.VMUI;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.tool.Tool;
import net.sourceforge.javydreamercsw.validation.manager.web.ValidationManagerUI;

/**
 * Trace Matrix component. Traces relationship from requirements to test case
 * steps including results and issues.
 *
 * @author Javier Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class TraceMatrix extends TreeTable {

    private final Project p;

    public TraceMatrix(Project p) {
        this.p = p;
        init();
    }

    public TraceMatrix(Project p, String caption) {
        super(caption);
        this.p = p;
        init();
    }

    private void init() {
        addContainerProperty(ValidationManagerUI.getInstance()
                .translate("general.requirement"),
                String.class, "");
        addContainerProperty(ValidationManagerUI.getInstance()
                .translate("general.test.case"),
                Label.class, "");
        addContainerProperty(ValidationManagerUI.getInstance()
                .translate("general.result"),
                Label.class, "");
        Tool.extractRequirements(p).forEach((r) -> {
            if (r.getParentRequirementId() == null) {
                addRequirement(r);
            }
        });
        setAnimationsEnabled(true);
        for (Object item : getItemIds().toArray()) {
            expandItem(item);
        }
        setSizeFull();
    }

    private void expandItem(Object item) {
        setCollapsed(item, false);
        if (hasChildren(item)) {
            getChildren(item).forEach(child -> {
                expandItem(child);
            });
        }
    }

    private void addRequirement(Requirement r) {
        addItem(new Object[]{r.getUniqueId(),
            new Label(), new Label()}, r.getUniqueId());
        addTestCases(r);
        //Add children
        r.getRequirementList().forEach(child -> {
            addRequirement(child);
            setParent(child.getUniqueId(), r.getUniqueId());
        });
        setItemIcon(r.getUniqueId(), VMUI.REQUIREMENT_ICON);
    }

    private void addTestCases(Requirement r) {
        r.getHistoryList().forEach(h -> {
            h.getExecutionStepList().forEach(es -> {
                TestCase tc = es.getStep().getTestCase();
                Label label = new Label(tc.getName());
                label.setIcon(VMUI.TEST_ICON);
                if (!containsId("tc-" + tc.getId())) {
                    addItem(new Object[]{"",
                        label, new Label()}, "tc-" + tc.getId());
                    setParent("tc-" + tc.getId(), r.getUniqueId());
                }
                if (es.getResultId() != null
                        && !containsId(es.getExecutionStepPK())) {
                    String result = es.getResultId().getResultName();
                    Label resultLabel
                            = new Label(ValidationManagerUI.getInstance()
                                    .translate(result));
                    addItem(new Object[]{"",
                        new Label("Step #" + es.getStep().getStepSequence()),
                        resultLabel}, es.getExecutionStepPK());
                    setChildrenAllowed(es.getExecutionStepPK(), false);
                    setParent(es.getExecutionStepPK(), "tc-" + tc.getId());
                    //Completed. Now check result
                    Resource icon;
                    switch (result) {
                        case "result.pass":
                            icon = VaadinIcons.CHECK;
                            break;
                        case "result.fail":
                            icon = VaadinIcons.CLOSE;
                            break;
                        case "result.blocked":
                            icon = VaadinIcons.PAUSE;
                            break;
                        case "result.progress":
                            icon = VaadinIcons.AUTOMATION;
                            break;
                        default:
                            icon = VaadinIcons.CLOCK;
                            break;
                    }
                    resultLabel.setIcon(icon);
                }
            });
        });
    }
}
