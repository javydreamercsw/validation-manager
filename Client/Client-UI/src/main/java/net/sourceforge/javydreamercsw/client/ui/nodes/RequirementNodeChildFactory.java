package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import java.beans.IntrospectionException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
class RequirementNodeChildFactory extends AbstractChildFactory {

    private final Project parent;

    public RequirementNodeChildFactory(Project parent) {
        this.parent = parent;
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("requirementSpecProjectId", parent.getId());
        List<Object> projects = DataBaseManager.namedQuery(
                "RequirementSpecNode.findByRequirementSpecProjectId", parameters);
        for (Iterator<Object> it = projects.iterator(); it.hasNext();) {
            toPopulate.add((com.validation.manager.core.db.RequirementSpecNode) it.next());
        }
        Collections.sort(toPopulate, new Comparator<Object>() {

                    @Override
                    public int compare(Object o1, Object o2) {
                        //Sort them by unique id
                        return ((Requirement)o1).getUniqueId().compareToIgnoreCase(((Requirement)o2).getUniqueId());
                    }
                });
        return true;
    }

    @Override
    protected Node[] createNodesForKey(Object key) {
        return new Node[]{createNodeForKey(key)};
    }

    @Override
    protected Node createNodeForKey(Object key) {
        try {
            if (key instanceof RequirementSpecNode) {
                RequirementSpecNode req = (RequirementSpecNode) key;
                return new UIRequirementSpecNodeNode(req);
            } else {
                return null;
            }
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    @Override
    protected void updateBean() {
        //Nothing to do, createKeys already does.
    }
}
