package net.sourceforge.javydreamercsw.client.ui.nodes;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.server.core.RequirementServer;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Action;
import net.sourceforge.javydreamercsw.client.ui.nodes.actions.EditRequirementAction;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIRequirementNode extends AbstractVMBeanNode {

    private static BufferedImage green, red, orange, yellow;

    public UIRequirementNode(Requirement req,
            RequirementTestChildFactory factory) throws IntrospectionException {
        super(req, factory, new InstanceContent());
        setIconBaseWithExtension("com/validation/manager/resources/icons/Papermart/Document.png");
        setShortDescription(req.getDescription());
    }

    @Override
    public String getName() {
        return new RequirementJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirement(getLookup().lookup(Requirement.class)
                        .getRequirementPK()).getUniqueId();
    }

    @Override
    public Action[] getActions(boolean b) {
        List<Action> actions = new ArrayList<>();
        actions.addAll(Arrays.asList(super.getActions(b)));
        actions.add(new EditRequirementAction());
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void refreshMyself() {
        RequirementServer rs
                = new RequirementServer(getLookup().lookup(Requirement.class));
        rs.update((Requirement) getBean(), rs.getEntity());
        rs.update(getLookup().lookup(Requirement.class), rs.getEntity());
    }

    @Override
    public Image getIcon(int type) {
        BufferedImage image = null;
        int coverage
                = new RequirementServer(getLookup().lookup(Requirement.class)).getTestCoverage();
        try {
            System.out.println(InstalledFileLocator.getDefault());
            if (coverage == 100) {
                if (green == null) {
                    green = ImageIO.read(getClass().getResource(
                            "/net/sourceforge/javydreamercsw/client/ui/circle_green.png"));
                }
                image = green;
            } else if (coverage < 100 && coverage > 50) {
                if (yellow == null) {
                    yellow = ImageIO.read(getClass().getResource(
                            "/net/sourceforge/javydreamercsw/client/ui/circle_yellow.png"));
                }
                image = yellow;
            } else if (coverage < 50 && coverage > 0) {
                if (orange == null) {
                    orange = ImageIO.read(getClass().getResource(
                            "/net/sourceforge/javydreamercsw/client/ui/circle_orange.png"));
                }
                image = orange;
            } else {
                if (red == null) {
                    red = ImageIO.read(getClass().getResource(
                            "/net/sourceforge/javydreamercsw/client/ui/circle_red.png"));
                }
                image = red;
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return image == null ? null
                : image.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
    }
}
