package net.sourceforge.javydreamercsw.client.ui.nodes.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import net.sourceforge.javydreamercsw.client.ui.nodes.RefreshableNode;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RefreshAction extends AbstractAction {

    private final RefreshableNode outer;

    public RefreshAction(final RefreshableNode outer) {
        super("Refresh", 
                new ImageIcon("com/validation/manager/resources/icons/refresh.png"));
        this.outer = outer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        outer.refresh();
    }
}
