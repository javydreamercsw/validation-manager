package net.sourceforge.javydreamercsw.client.ui.nodes;

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
        factory.refresh();
    }
}
