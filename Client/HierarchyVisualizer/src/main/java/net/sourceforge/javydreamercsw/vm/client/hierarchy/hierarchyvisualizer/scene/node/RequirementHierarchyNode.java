package net.sourceforge.javydreamercsw.vm.client.hierarchy.hierarchyvisualizer.scene.node;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Requirement;
import java.awt.Image;
import java.awt.image.BufferedImage;
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
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementHierarchyNode extends AbstractHierarchyNode {

    private static final Logger LOG =
            Logger.getLogger(RequirementHierarchyNode.class.getSimpleName());

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
        try {
            BufferedImage image;
            //TODO: Handle partial coverage
            int coverage = getCoverage();
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
            return image;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private int getCoverage() {
        int result = 0;
        Requirement req = (Requirement) object;
        //Has test cases and no related requirements
        if (req.getStepList().size() > 0 && req.getRequirementList().isEmpty()) {
            result = 100;
        }//Has nothing
        else if (req.getStepList().isEmpty() && req.getRequirementList().isEmpty()) {
            result = 0;
        } else {
            //TODO: Need to calculate amount of related requirements
            
        }
        LOG.log(Level.INFO, "{0} Coverage: {1}",
                new Object[]{req.getUniqueId(), result});
        return result;
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
        ArrayList<AbstractHierarchyNode> children =
                new ArrayList<AbstractHierarchyNode>();
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
