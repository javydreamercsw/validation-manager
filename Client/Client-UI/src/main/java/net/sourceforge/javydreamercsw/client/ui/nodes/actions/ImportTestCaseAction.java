package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.db.TestPlan;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import net.sourceforge.javydreamercsw.client.ui.components.testcase.importer.TestCaseImporterTopComponent;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ImportTestCaseAction extends AbstractAction {

    public ImportTestCaseAction() {
        super("Import Test Case",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Create and display the dialog */
        TestCaseImporterTopComponent component
                = new TestCaseImporterTopComponent();
        TestPlan test = Utilities.actionsGlobalContext().lookup(TestPlan.class);
        assert test != null;
        component.setTestPlan(test);
        component.open();
        component.requestActive();
    }
}
