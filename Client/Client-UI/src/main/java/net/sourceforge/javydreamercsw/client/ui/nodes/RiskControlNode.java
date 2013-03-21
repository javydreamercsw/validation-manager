package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.db.fmea.RiskControl;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class RiskControlNode extends BeanNode {

    public RiskControlNode(RiskControl rs) throws IntrospectionException {
        super(rs,
                null,//TODO Children.create(new SubProjectChildFactory(project), true),
                Lookups.singleton(rs));
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Folder.png");
    }

    @Override
    public String getName() {
        return ""+getLookup().lookup(RiskControl.class).getRiskControlPK().getId();
    }
}
