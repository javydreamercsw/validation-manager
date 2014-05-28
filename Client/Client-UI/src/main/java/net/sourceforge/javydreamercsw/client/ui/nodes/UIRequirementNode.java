package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.tool.ImageProvider;
import com.validation.manager.core.tool.Timer;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.EditRequirementAction;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIRequirementNode extends AbstractVMBeanNode {

    private static BufferedImage green, red, orange, yellow;
    private final Requirement requirement;
    private int coverage = -1;
    private BufferedImage image = null;
    private static final Logger LOG
            = Logger.getLogger(UIRequirementNode.class.getSimpleName());

    public UIRequirementNode(Requirement req,
            RequirementTestChildFactory factory) throws IntrospectionException {
        super(req, factory, new InstanceContent());
        requirement = req;
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Document.png");
        setShortDescription(req.getDescription());
    }

    @Override
    public String getName() {
        return new RequirementJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirement(getLookup().lookup(Requirement.class)
                        .getId()).getUniqueId();
    }

    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(Arrays.asList(super.getActions(b)));
        actions.add(new EditRequirementAction());
        /**
         * TODO: Need the following to accommodate Requirement revisions: 1)
         * Blindly copy the test coverage. 2) Review the current test cases
         * covering previous version and deciding if those still cover the
         * requirement changes in a one by one basis. 3) Don't do anything,
         * leaving it uncovered.
         */
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void refreshMyself() {
        coverage = -1;//Reset so it is recalculated
        RequirementServer rs
                = new RequirementServer(getLookup().lookup(Requirement.class));
        rs.update((Requirement) getBean(), rs.getEntity());
        rs.update(getLookup().lookup(Requirement.class), rs.getEntity());
    }

    @Override
    public Image getIcon(int type) {
        if (image == null || coverage < 0) {
            try {
                if (coverage < 0) {
                    Timer timer = new Timer();
                    coverage = new RequirementServer(getLookup().lookup(Requirement.class)).getTestCoverage();
                    timer.stop();
                    LOG.log(Level.FINE, "Time calculating coverage for {0}: {1}",
                            new Object[]{getLookup().lookup(Requirement.class).getUniqueId(),
                                timer.elapsedTime()});
                }
                ImageProvider provider = null;
                Timer timer = new Timer();
                for (ImageProvider p : Lookup.getDefault().lookupAll(ImageProvider.class)) {
                    if (p.supported(getLookup().lookup(Requirement.class))) {
                        provider = p;
                        break;
                    }
                }
                if (provider != null) {
                    image = provider.getIcon(getLookup().lookup(Requirement.class), coverage);
                }
                timer.stop();
                LOG.log(Level.FINE, "Time getting icon for {0}: {1}",
                        new Object[]{getLookup().lookup(Requirement.class).getUniqueId(),
                            timer.elapsedTime()});
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        return image == null ? null
                : image.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
    }

    /**
     * @return the requirement
     */
    public Requirement getRequirement() {
        return requirement;
    }
}
