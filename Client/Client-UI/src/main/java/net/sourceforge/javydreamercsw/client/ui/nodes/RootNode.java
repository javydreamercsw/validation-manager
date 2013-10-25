package net.sourceforge.javydreamercsw.client.ui.nodes;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.CreateProjectAction;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.RefreshAction;
import net.sourceforge.javydreamercsw.client.ui.nodes.capability.RefreshableCapability;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RootNode extends AbstractNode implements RefreshableCapability {

    private final AbstractChildFactory factory;
    private Lookup lookup;
    private InstanceContent content;

    public RootNode(AbstractChildFactory factory) {
        super(Children.create(factory, true));
        this.factory = factory;
        //Create instance content to hold abilities
        content = new InstanceContent();
        //Create lookup to expose contents
        lookup = new AbstractLookup(content);
        //Add abilities
        content.add(new RefreshableCapability() {
            @Override
            public void refresh() {
                RootNode.this.refresh();
            }

            @Override
            public Lookup createAdditionalLookup(Lookup lkp) {
                return lookup;
            }
        });
        setDisplayName("Projects");
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Folder.png");
    }

    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<>();
        if (!getLookup().lookupAll(RefreshableCapability.class).isEmpty()) {
            actions.add(new RefreshAction(this));
        }
        actions.add(new CreateProjectAction());
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void refresh() {
        factory.refresh();
    }

    @Override
    public Lookup createAdditionalLookup(Lookup baseContext) {
        return Lookup.EMPTY;
    }
}
