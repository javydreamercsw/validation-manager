package net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene.node;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.server.core.RequirementServer;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene.AbstractHierarchyNode;
import org.netbeans.api.visual.widget.Scene;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementHierarchyNode extends AbstractHierarchyNode {

    private static final Logger LOG
            = Logger.getLogger(RequirementHierarchyNode.class.getSimpleName());

    public RequirementHierarchyNode(Object object, Scene scene) {
        super(object, scene);
    }

    public RequirementHierarchyNode(Object object, Scene scene, TextOrientation orientation) {
        super(object, scene, orientation);
    }

    @Override
    public Object getID() {
        return ((Requirement) object).getRequirementPK();
    }

    @Override
    public Image getCurrentImage() {
        BufferedImage image = null;
        try {
            int coverage = 
                    new RequirementServer(((Requirement) object)).getTestCoverage();
            if (coverage == 100) {
                image = ImageIO.read(getClass().getResource(
                        "/net/sourceforge/javydreamercsw/vm/client/hierarchy/visualizer/circle_green.png"));
            } else if (coverage < 100 && coverage > 50) {
                image = ImageIO.read(getClass().getResource(
                        "/net/sourceforge/javydreamercsw/vm/client/hierarchy/visualizer/circle_yellow.png"));
            } else if (coverage < 50 && coverage > 0) {
                image = ImageIO.read(getClass().getResource(
                        "/net/sourceforge/javydreamercsw/vm/client/hierarchy/visualizer/circle_orange.png"));
            } else {
                image = ImageIO.read(getClass().getResource(
                        "/net/sourceforge/javydreamercsw/vm/client/hierarchy/visualizer/circle_red.png"));
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return image;
    }

    private List<Requirement> getChildRequirements() {
        Requirement req = (Requirement) object;
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", req.getRequirementPK().getId());
        parameters.put("version", req.getRequirementPK().getVersion());
        List<Requirement> children = Arrays.asList(DataBaseManager.createdQuery(
                "select rhr from RequirementHasRequirement rhr "
                + "where rhr.parent_requirement_id=:id and "
                + "rhr.parent_requirement_version=:version ", parameters).toArray(new Requirement[1]));
        return children;
    }

    @Override
    public String getCurrentLabel() {
        return ((Requirement) object).getUniqueId();
    }

    @Override
    protected List<AbstractHierarchyNode> getNodeChildren() {
        ArrayList<AbstractHierarchyNode> children
                = new ArrayList<AbstractHierarchyNode>();
        //Clear and recreate
        Requirement req = (Requirement) object;
        LOG.log(Level.INFO, "Updating children for {0}", req.getUniqueId());
        //getRequirementList() shows all parents to this requirement
        for (Iterator<Requirement> it = req.getRequirementList().iterator(); it.hasNext();) {
            Requirement r = it.next();
            children.add(new RequirementHierarchyNode(r, getScene()));
        }
        return children;
    }
}
