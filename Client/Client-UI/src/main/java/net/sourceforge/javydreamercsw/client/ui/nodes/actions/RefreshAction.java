package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import net.sourceforge.javydreamercsw.client.ui.nodes.capability.RefreshableCapability;
import org.openide.nodes.Node;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RefreshAction extends AbstractAction {

    private final Node node;

    public RefreshAction(final Node node) {
        super("Refresh",
                new ImageIcon("com/validation/manager/resources/icons/refresh.png"));
        this.node = node;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Iterator<? extends RefreshableCapability> it =
                node.getLookup().lookupAll(RefreshableCapability.class).iterator(); it.hasNext();) {
            RefreshableCapability rc = it.next();
            rc.refresh();
        }
    }
}
