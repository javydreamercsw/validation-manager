package net.sourceforge.javydreamercsw.client.ui.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateProjectAction;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.RefreshAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RootNode extends AbstractNode implements RefreshableNode {
    private final AbstractChildFactory factory;

    public RootNode(AbstractChildFactory factory) {
        super(Children.create(factory, true), Lookup.EMPTY);
        this.factory = factory;
        setDisplayName("Projects");
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Folder.png");
    }

    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new RefreshAction(this));
        actions.add(new CreateProjectAction());
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void refresh() {
        factory.refresh();
    }
}
