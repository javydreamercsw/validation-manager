package net.sourceforge.javydreamercsw.client.ui.components.requirement.edit;

import com.validation.manager.core.db.Step;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIStepNode extends BeanNode {

    public UIStepNode(Step key) throws IntrospectionException {
        super(key, Children.LEAF,
                Lookups.singleton(key));
        setDisplayName(key.getTestCase().getName()
                + ": Step #:" + key.getStepSequence());
    }
}
