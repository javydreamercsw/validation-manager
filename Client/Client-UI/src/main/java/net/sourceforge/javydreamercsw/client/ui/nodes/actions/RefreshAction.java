package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import net.sourceforge.javydreamercsw.client.ui.nodes.RefreshableCapability;
import org.openide.nodes.Node;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RefreshAction extends AbstractAction {

    private final Node outer;

    public RefreshAction(final Node outer) {
        super("Refresh",
                new ImageIcon("com/validation/manager/resources/icons/refresh.png"));
        this.outer = outer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Iterator<? extends RefreshableCapability> it = 
                outer.getLookup().lookupAll(RefreshableCapability.class).iterator(); it.hasNext();) {
            RefreshableCapability rc = it.next();
            rc.refresh();
        }
    }
}
