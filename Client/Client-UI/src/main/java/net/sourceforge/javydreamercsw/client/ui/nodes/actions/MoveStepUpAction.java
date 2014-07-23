package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.server.core.StepServer;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class MoveStepUpAction extends AbstractAction {

    public MoveStepUpAction() {
        super("Move Up",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Step current = Utilities.actionsGlobalContext().lookup(Step.class);
        Step previous = null;
        TestCase tc = current.getTestCase();
        //Get previous
        for (Step s : tc.getStepList()) {
            if (s.getStepSequence() == current.getStepSequence() - 1) {
                previous = s;
                break;
            }
        }
        if (previous != null) {
            try {
                StepServer c = new StepServer(current);
                StepServer p = new StepServer(previous);
                c.setStepSequence(c.getStepSequence() - 1);
                p.setStepSequence(p.getStepSequence() + 1);
                c.write2DB();
                p.write2DB();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
