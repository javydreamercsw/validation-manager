package net.sourceforge.javydreamercsw.client.ui.nodes;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RootNode extends AbstractNode {

    public RootNode() {
        super(Children.create(new ProjectChildFactory(), true));
        setDisplayName("Projects");
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Folder.png");
    }
}
