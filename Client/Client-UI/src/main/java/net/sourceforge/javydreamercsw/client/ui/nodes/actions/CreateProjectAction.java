package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CreateProjectAction extends AbstractAction {

    public CreateProjectAction() {
        super("Create Project",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final CreateProjectDialog dialog
                        = new CreateProjectDialog(new javax.swing.JFrame(), true);
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
