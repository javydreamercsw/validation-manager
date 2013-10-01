package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import com.validation.manager.core.api.report.ReportChooserInterface;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ReportAction extends AbstractAction {

    public ReportAction() {
        super("Reports",
                new ImageIcon("com/validation/manager/resources/icons/refresh.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ReportChooserInterface dialog
                        = Lookup.getDefault().lookup(ReportChooserInterface.class);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });
    }
}
