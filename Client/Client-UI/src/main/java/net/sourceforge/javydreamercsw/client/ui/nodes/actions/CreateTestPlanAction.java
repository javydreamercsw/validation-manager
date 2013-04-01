package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.dreamer.outputhandler.OutputHandler;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.TestPlanServer;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import net.sourceforge.javydreamercsw.client.ui.ProjectExplorerComponent;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CreateTestPlanAction extends AbstractAction{

    public CreateTestPlanAction() {
        super("Create Test Plan",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            TestPlanServer tps= new TestPlanServer(
                    Utilities.actionsGlobalContext().lookup(TestProject.class), true, true);
            tps.write2DB();
            OutputHandler.setStatus("Test Plan created!");
            ProjectExplorerComponent.refresh();
        } catch (IllegalOrphanException ex) {
            Exceptions.printStackTrace(ex);
        } catch (NonexistentEntityException ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
