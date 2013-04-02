package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.awt.event.ActionEvent;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import net.sourceforge.javydreamercsw.client.ui.ProjectExplorerComponent;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CreateTestCaseAction extends AbstractAction {

    public CreateTestCaseAction() {
        super("Create Test Case",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Test t = Utilities.actionsGlobalContext().lookup(Test.class);
            TestCaseServer tcs = new TestCaseServer(t.getTestCaseList().size() + 1,
                    new Short("1"), new Date());
            //TODO: Use logged user instead
            tcs.setAuthorId(new VMUserServer(1).getEntity());
            tcs.setActive(true);
            tcs.setIsOpen(true);
            tcs.setTest(t);
            tcs.write2DB();
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
