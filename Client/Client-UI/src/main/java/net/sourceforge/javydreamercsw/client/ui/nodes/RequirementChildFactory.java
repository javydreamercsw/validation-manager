package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.server.core.RequirementServer;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.client.ui.components.RequirementStatusFilterChangeListener;
import net.sourceforge.javydreamercsw.client.ui.components.RequirementStatusFilterChangeProvider;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementChildFactory extends AbstractChildFactory
        implements RequirementStatusFilterChangeListener {

    private RequirementSpecNode node;
    private Integer[] ids = new Integer[0];
    private static final Logger LOG
            = Logger.getLogger(RequirementChildFactory.class.getSimpleName());

    public RequirementChildFactory(RequirementSpecNode node) {
        this.node = node;
        for (RequirementStatusFilterChangeProvider provider
                : Utilities.actionsGlobalContext().lookupAll(RequirementStatusFilterChangeProvider.class)) {
            provider.register((RequirementChildFactory) this);
            System.out.println("Found provider: " + provider.getClass().getSimpleName());
        }
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        List<Requirement> toAdd = new ArrayList<>();
        for (Requirement req : node.getRequirementList()) {
            //TODO: Filter out status ids
//            if (!new ArrayList<>(Arrays.asList(ids)).contains(req.getRequirementStatusId().getId())) {
            if (req.getRequirementStatusId() != null
                    && req.getRequirementStatusId().getId() == 2) {
                boolean found = false;
                for (Requirement in : toAdd) {
                    if (in.getUniqueId().trim().equals(req.getUniqueId().trim())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    //Only add the latest version
                    RequirementServer rs = new RequirementServer(req);
                    Requirement max = Collections.max(rs.getVersions(), null);
                    toAdd.add(max);
                }
            } else if (req.getRequirementStatusId() == null) {
                LOG.log(Level.WARNING,
                        "Invalid Requirement without status: {0}", req);
            }
        }
        toPopulate.addAll(toAdd);
        for (RequirementSpecNode req : node.getRequirementSpecNodeList()) {
            toPopulate.add(req);
        }
        Collections.sort(toPopulate, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                //Sort them by unique id
                return ((Requirement) o1).getUniqueId()
                        .compareToIgnoreCase(((Requirement) o2).getUniqueId());
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
            if (key instanceof Requirement) {
                Requirement req = (Requirement) key;
                return new UIRequirementNode(req,
                        new RequirementTestChildFactory(req));
            } else if (key instanceof RequirementSpecNode) {
                RequirementSpecNode rs = (RequirementSpecNode) key;
                return new UIRequirementSpecNodeNode(rs);
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
        RequirementSpecNodeJpaController controller
                = new RequirementSpecNodeJpaController(
                        DataBaseManager.getEntityManagerFactory());
        node = controller.findRequirementSpecNode(node.getRequirementSpecNodePK());
    }

    @Override
    public void filterChange(Integer[] ids) {
        this.ids = ids;
        refresh();
    }
}
