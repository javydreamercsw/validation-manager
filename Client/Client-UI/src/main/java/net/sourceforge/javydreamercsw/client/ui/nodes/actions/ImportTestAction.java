package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.db.TestPlan;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import net.sourceforge.javydreamercsw.client.ui.components.test.importer.TestImportTopComponent;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ImportTestAction extends AbstractAction {

    private static final long serialVersionUID = 2518681189161437035L;

    public ImportTestAction() {
        super("Import From Document",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Create and display the dialog */
        TestImportTopComponent component = new TestImportTopComponent();
        TestPlan tp = Utilities.actionsGlobalContext().lookup(TestPlan.class);
        assert tp != null;
        component.setTestPlan(tp);
        component.open();
        component.requestActive();
    }
}
