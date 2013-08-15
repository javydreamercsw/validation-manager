package net.sourceforge.javydreamercsw.client.ui.components;

import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import java.beans.IntrospectionException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class IStepChildFactory extends ChildFactory<Step> {

    private final TestCase tc;
    private static final Logger LOG
            = Logger.getLogger(IStepChildFactory.class.getSimpleName());

    public IStepChildFactory(TestCase tc) {
        this.tc = tc;
    }

    @Override
    protected boolean createKeys(List<Step> toPopulate) {
        for (Step s : tc.getStepList()) {
            toPopulate.add(s);
        }
        return true;
    }
    
    @Override
    protected Node createNodeForKey(Step key) {
        AbstractNode node = new AbstractNode(Children.LEAF);
        node.setDisplayName("Step: "+key.getStepSequence());
        return node;
    }
}
