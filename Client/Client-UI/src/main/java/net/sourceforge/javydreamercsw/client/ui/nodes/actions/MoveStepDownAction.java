/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class MoveStepDownAction extends AbstractAction {

    public MoveStepDownAction() {
        super("Move Down",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Step current = Utilities.actionsGlobalContext().lookup(Step.class);
        Step next = null;
        TestCase tc = current.getTestCase();
        //Get next
        for (Step s : tc.getStepList()) {
            if (s.getStepSequence() == current.getStepSequence() + 1) {
                next = s;
                break;
            }
        }
        if (next != null) {
            try {
                StepServer c = new StepServer(current);
                StepServer n = new StepServer(next);
                c.setStepSequence(c.getStepSequence() + 1);
                n.setStepSequence(n.getStepSequence() - 1);
                c.write2DB();
                n.write2DB();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
