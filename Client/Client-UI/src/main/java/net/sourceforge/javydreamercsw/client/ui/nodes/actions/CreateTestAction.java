package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import com.validation.manager.core.db.TestPlan;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CreateTestAction extends AbstractAction {

    public CreateTestAction() {
        super("Create Test",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final CreateTestDialog dialog =
                        new CreateTestDialog(new javax.swing.JFrame(), true);
                dialog.setTestPlan(Utilities.actionsGlobalContext().lookup(TestPlan.class));
                dialog.setLocationRelativeTo(null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        dialog.dispose();
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
}
