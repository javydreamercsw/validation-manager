package net.sourceforge.javydreamercsw.client.ui.nodes;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateProjectAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RootNode extends AbstractNode {

    private static ProjectChildFactory factory = new ProjectChildFactory();

    public RootNode() {
        super(Children.create(factory, true));
        setDisplayName("Projects");
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Folder.png");
    }
    
    public static void refresh(){
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                factory.refresh();
            }
        });
    }
    
    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new CreateProjectAction());
        actions.add(new RefreshAction());
        return actions.toArray(new Action[actions.size()]);
    }
    
    private class RefreshAction extends AbstractAction{

        public RefreshAction() {
            super("Refresh",
                new ImageIcon("com/validation/manager/resources/icons/Signage/Add Square.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}
